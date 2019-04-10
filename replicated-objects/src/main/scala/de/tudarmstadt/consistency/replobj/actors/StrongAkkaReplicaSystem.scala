package de.tudarmstadt.consistency.replobj.actors

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.{ReentrantLock, ReentrantReadWriteLock}

import akka.actor.ActorRef
import de.tudarmstadt.consistency.replobj.ConsistencyLevel
import de.tudarmstadt.consistency.replobj.ConsistencyLevel.Strong
import de.tudarmstadt.consistency.replobj.actors.Requests._
import de.tudarmstadt.consistency.replobj.actors.StrongAkkaReplicaSystem.StrongReplicatedObject.{StrongFollowerReplicatedObject, StrongMasterReplicatedObject}

import scala.language.postfixOps
import scala.reflect.runtime.universe._


/**
	* Created on 27.02.19.
	*
	* @author Mirko Köhler
	*/

trait StrongAkkaReplicaSystem[Addr] extends AkkaReplicaSystem[Addr] {

	override protected def createMasterReplica[T <: AnyRef : TypeTag](l : ConsistencyLevel, addr : Addr, obj : T) : AkkaReplicatedObject[Addr, T] = l match {
		case Strong => new StrongMasterReplicatedObject[Addr, T](obj, addr, this)
		case _ =>	super.createMasterReplica[T](l, addr, obj)
	}

	override protected def createFollowerReplica[T <: AnyRef : TypeTag](l : ConsistencyLevel, addr : Addr, obj : T, masterRef : ActorRef) : AkkaReplicatedObject[Addr, T] = l match {
		case Strong => new StrongFollowerReplicatedObject[Addr, T](obj, addr, masterRef, this)
		case _ =>	super.createFollowerReplica[T](l, addr, obj, masterRef)
	}
}

object StrongAkkaReplicaSystem {

	trait StrongReplicatedObject[Addr, T <: AnyRef]
		extends AkkaReplicatedObject[Addr, T]
		//Strong objects cache their results for MVCC
		with AkkaMultiversionReplicatedObject[Addr, T] {

		override final def consistencyLevel : ConsistencyLevel = Strong
	}


	object StrongReplicatedObject {

		class StrongMasterReplicatedObject[Addr, T <: AnyRef](
			init : T, val addr : Addr, val replicaSystem : AkkaReplicaSystem[Addr]
		)(
			protected implicit val ttt : TypeTag[T]
		) extends StrongReplicatedObject[Addr, T] {
			setObject(init)


//			val lock = new ReentrantReadWriteLock()
			val txMutex = new TxMutex

			override def internalInvoke[R](opid: ContextPath, methodName: String, args: Seq[Any]) : R = {
				txMutex.lockFor(opid.txid)
				val res = super.internalInvoke[R](opid, methodName, args)
				txMutex.unlockFor(opid.txid)
				res
			}

			override def internalGetField[R](opid : ContextPath, fldName : String) : R = {
				/*get is not synchronized*/
				txMutex.lockFor(opid.txid)
				val result = super.internalGetField[R](opid, fldName)
				txMutex.unlockFor(opid.txid)
				result
			}

			override def internalSetField(opid : ContextPath, fldName : String, newVal : Any) : Unit = {
				txMutex.lockFor(opid.txid)
				super.internalSetField(opid, fldName, newVal)
				txMutex.unlockFor(opid.txid)
			}

			override def internalSync() : Unit = {

			}

			override def handleRequest(request : Request) : Any = request match {
				case LockReq(txid) =>
					txMutex.lockFor(txid)
					LockRes(getObject)

				case MergeAndUnlock(txid, newObj : T, op, result) =>
					setObject(newObj)
					cache(op, result)
					txMutex.unlockFor(txid)


				case ReadStrongField(GetFieldOp(path, fldName)) =>
					ReadResult(internalGetField(path, fldName))

				case _ => super.handleRequest(request)
			}

			override def toString : String = s"StrongMaster($addr, $getObject)"
		}


		class StrongFollowerReplicatedObject[Addr, T <: AnyRef](
			init : T, val addr : Addr, val masterReplica : ActorRef, val replicaSystem : AkkaReplicaSystem[Addr]
		)(
			protected implicit val ttt : TypeTag[T]
		) extends StrongReplicatedObject[Addr, T] {
			setObject(init)


			override def internalInvoke[R](opid: ContextPath, methodName: String, args: Seq[Any]) : R = {

				val handler = replicaSystem.acquireHandlerFrom(masterReplica)

				val LockRes(masterObj : T) = handler.request(addr, LockReq(opid.txid))
				setObject(masterObj)
				val res = super.internalInvoke[R](opid, methodName, args)
				handler.request(addr, MergeAndUnlock(opid.txid, getObject, InvokeOp(opid, methodName, args), res))
				handler.close()
				res
			}

			override def internalGetField[R](opid : ContextPath, fldName : String) : R = {
				//TODO: This is pretty hacky and undurchdacht. Are there some better ways to do this?
				val handler = replicaSystem.acquireHandlerFrom(masterReplica)
				val ReadResult(res : R) = handler.request(addr, ReadStrongField(GetFieldOp(opid, fldName)))
				handler.close()

				res match {
					case anyRef : AnyRef => replicaSystem.initializeRefFieldsFor(anyRef)
					case _ =>
				}

				res
			}

			override def internalSetField(opid : ContextPath, fldName : String, newVal : Any) : Unit = {

				val handler = replicaSystem.acquireHandlerFrom(masterReplica)

				val LockRes(masterObj : T) = handler.request(addr, LockReq(opid.txid))
				setObject(masterObj)
				super.internalSetField(opid, fldName, newVal)

				val mergeReq = MergeAndUnlock(opid.txid, getObject, SetFieldOp(opid, fldName, newVal), ())
				handler.request(addr, mergeReq)
				handler.close()
			}

			override def internalSync() : Unit = {

			}

			override def toString : String = s"StrongFollower($addr, $getObject)"
		}
	}





	sealed trait StrongReq extends Request
	case class LockReq(txid : Long) extends StrongReq with ReturnRequest
	case class LockRes(obj : AnyRef) extends StrongReq with ReturnRequest
	case class MergeAndUnlock(txid : Long, obj : AnyRef, op : Operation[Any], result : Any) extends StrongReq with ReturnRequest
	case class ReadStrongField(op : GetFieldOp[Any]) extends StrongReq with ReturnRequest



//	private case object SynchronizeWithStrongMaster extends StrongReq with ReturnRequest
//
//	private case class StrongSynchronized[T <: AnyRef](obj : T)
	case class ReadResult(res : Any)



	case object MergeAck

}
