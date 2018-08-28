package de.tudarmstadt.consistency.store.scala.transactions

import java.util.concurrent.Executors

import de.tudarmstadt.consistency.utils.Log

import scala.concurrent.{ExecutionContext, Future}

/**
	* Created on 20.08.18.
	*
	* @author Mirko Köhler
	*/
object TransactionDemo {


	/*
CREATE FUNCTION sumLength(sum int, s text)
CALLED ON NULL INPUT
RETURNS int
LANGUAGE java
AS '
if (s == null && sum == null)
return 0;
else if (sum == null)
return s.length();
else if (s == null)
return sum;
else
return sum + s.length();'


CREATE AGGREGATE aggregateLength(text)
SFUNC sumLength
STYPE int
INITCOND 0;




CREATE OR REPLACE FUNCTION biggerRow(max tuple<int, text>, id int, data text)
CALLED ON NULL INPUT
RETURNS tuple<int, text>
LANGUAGE java
AS '
if (max == null) {
	max.setInt(0, id);
	max.setString(1, data);
	return max;
}


if (max.getInt(0) >= id) {
	return max;
} else {
	max.setInt(0, id);
	max.setString(1, data);
	return max;
}'


CREATE OR REPLACE AGGREGATE maxRow(int, text)
SFUNC biggerRow
STYPE tuple<int, text>
INITCOND (-1, '_');


CREATE FUNCTION function_name(arg0 type0, arg1 type1)
    RETURNS NULL ON NULL INPUT or CALLED ON NULL INPUT
    RETURNS type0
    LANGUAGE java
    AS '
    return (type0) arg0 + arg1';

CREATE AGGREGATE aggregate_name(type1)
    SFUNC function_name
    STYPE type0
    FINALFUNC function_name2
    INITCOND null;


	CREATE FUNCTION maxRow(row <tuple<text, int>>,


	 */

	def timed[T](f : => T) : T = {
		val before = System.nanoTime()
		val t = f
		val after = System.nanoTime()
		Log.info(null, s"Time taken: ${(after - before) / 1000 / 1000}ms")
		t
	}

	def simpleExample(): Unit = {

		val store = new SimpleCassandraTransactionStore(LocalClusterParams)
		import store._

		initializeKeyspace()

		val session = newSession

		val transactionA : Transaction[Unit] = context => {
			context.write("x", "Hallo")
			context.write("y", "Welt")
		}

		val transactionB : Transaction[Unit] = context => {
			context.write("x", "Hello")
			context.write("z", "World")
		}

		val transactionC : Transaction[String] = context => {
			val x = context.read("x")
			println(s"x = $x")
			val y = context.read("y")
			println(s"y = $y")
			val z = context.read("z")
			println(s"z = $z")

			val s = List(x, y, z).flatten.mkString(" ")
			context.write("s", s)

			s
		}


		timed {
			Log.info(null, commit(session, transactionA, isolationLevelOps.snapshotIsolation))
		}
		timed {
			Log.info(null, commit(session, transactionB, isolationLevelOps.snapshotIsolation))
		}
		timed {
			Log.info(null, commit(session, transactionC, isolationLevelOps.snapshotIsolation))
		}

		session.close()
		close()
		System.exit(0)
	}

//	def concurrentExample(): Unit = {
//
//		import de.tudarmstadt.consistency.store.scala.transactions.SimpleCassandraTransactionStore._
//		initialize()
//
//		val executor = Executors.newFixedThreadPool(4)
//		implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executor)
//
//		Thread.sleep(1000)
//
//		Future {
//			val session = newSession
//			try {
//				Log.info(null, "### A ===> " + commitTransaction(session, List(Write("x", "Hallo", Set.empty), Write("y", "Welt", Set.empty))))
//			} catch {
//				case e => TransactionDemo.synchronized {
//					System.err.println("NODE A")
//					e.printStackTrace()
//				}
//			}
//			Log.info(null, "A done.")
//		}
//
//		Future {
//			val session = newSession
//			try {
//				Log.info(null, "### B ===> " + commitTransaction(session, List(Write("x", "Hello", Set.empty), Write("y", "World", Set.empty))))
//			} catch {
//				case e => TransactionDemo.synchronized {
//					System.err.println("NODE B")
//					e.printStackTrace()
//				}
//			}
//			Log.info(null, "B done.")
//		}
//
//		Future {
//			val session = newSession
//			try {
//				Log.info(null, "### C ===> " + commitTransaction(session, List(Write("x", "Hallösche", Set.empty), Write("z", "Zusamme", Set.empty))))
//			} catch {
//				case e => TransactionDemo.synchronized {
//					System.err.println("NODE C")
//					e.printStackTrace()
//				}
//			}
//			Log.info(null, "C done.")
//		}
//
//		Thread.sleep(3000)
//
//		val session = newSession
//
//		printTables(session)
//
//		Thread.sleep(1000)
//
//		timed {
//			Log.info(null, "x = " + read(session, "x"))
//		}
//		timed {
//			Log.info(null, "y = " + read(session, "y"))
//		}
//		timed {
//			Log.info(null, "z = " + read(session, "z"))
//		}
//		timed {
//			Log.info(null, "x = " + read(session, "x"))
//		}
//
//		Thread.sleep(1000)
//
//		printTables(session)
//
//		System.exit(0)
//
//	}

	def main(args : Array[String]): Unit = {
		simpleExample()
	}
}
