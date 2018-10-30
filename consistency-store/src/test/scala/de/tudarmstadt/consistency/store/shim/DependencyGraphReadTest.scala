package de.tudarmstadt.consistency.store.shim

import de.tudarmstadt.consistency.store.shim.Event.Update
import de.tudarmstadt.consistency.store.shim.EventRef.{TxRef, UpdateRef}
import org.junit.Assert._
import org.junit.Test

/**
	* Created on 07.09.18.
	*
	* @author Mirko Köhler
	*/
class DependencyGraphReadTest extends DependencyGraphTest {

	@Test
	def readNotFound(): Unit = {
		assertNotFound(graph.resolve('x))
		assertNotFound(graph.resolve('z))

		graph.addUpdate(Update(0, 'x, "Hello", None))

		assertResolved(0, 'x, "Hello", None)(graph.resolve('x))
		assertNotFound(graph.resolve('z))
	}

	@Test
	def readUpdatesResolved(): Unit = {
		graph.addUpdate(Update(0, 'x, "Hello", None))
		graph.addUpdate(Update(1, 'y, "World", None, (0, 'x)))

		assertResolved(0, 'x, "Hello", None)(graph.resolve('x))
		assertResolved(1, 'y, "World", None, (0, 'x))(graph.resolve('y))

		graph.addUpdate(Update(2, 'x, "Hi", None, (1, 'y)))

		assertResolved(2, 'x, "Hi", None, (1, 'y))(graph.resolve('x))
		assertResolved(1, 'y, "World", None, (0, 'x))(graph.resolve('y))
	}

	@Test
	def readUpdatesUnresolved(): Unit = {
		graph.addUpdate(Update(0, 'x, "Hello", None))
		graph.addUpdate(Update(1, 'y, "World", None, (0, 'x), (3, 'z)))
		graph.addUpdate(Update(2, 'x, "Hi", None, (1, 'y)))

		assertUnresolved(1, 'y, "World", None, (0, 'x), (3, 'z))(UpdateRef(3, 'z))(graph.resolve('y))
		assertUnresolvedLatest(0, 'x, "Hello", None)(2, 'x, "Hi", None, (1, 'y))(UpdateRef(3, 'z))(graph.resolve('x))

		graph.addUpdate(Update(3, 'z, "!!!", None))

		assertResolved(1, 'y, "World", None, (0, 'x), (3, 'z))(graph.resolve('y))
		assertResolved(2, 'x, "Hi", None, (1, 'y))(graph.resolve('x))
		assertResolved(3, 'z, "!!!", None)(graph.resolve('z))
	}


	@Test
	def readRemoved(): Unit = {
		graph.addUpdate(Update(0, 'x, "Hello", None))
		graph.addUpdate(Update(1, 'y, "World", None, (0, 'x), (3, 'z)))
		graph.addUpdate(Update(2, 'x, "Hi", None, (1, 'y)))

		assertUnresolved(1, 'y, "World", None, (0, 'x), (3, 'z))(UpdateRef(3, 'z))(graph.resolve('y))
		assertUnresolvedLatest(0, 'x, "Hello", None)(2, 'x, "Hi", None, (1, 'y))(UpdateRef(3, 'z))(graph.resolve('x))

		assertEquals(Some(Update(1, 'y, "World", None, Set(UpdateRef(0, 'x), UpdateRef(3, 'z)))),
			graph.remove(1))

		assertNotFound(graph.resolve('y))
		assertUnresolvedLatest(0, 'x, "Hello", None)(2, 'x, "Hi", None, (1, 'y))(UpdateRef(1, 'y))(graph.resolve('x))

		graph.addUpdate(Update(1, 'y, "Welt", None, (3, 'z)))

		assertEquals(Some(Update(0, 'x, "Hello", None)),
			graph.remove(0))

		assertUnresolved(1, 'y, "Welt", None, (3, 'z)) (UpdateRef(3, 'z)) (graph.resolve('y))
		assertUnresolved(2, 'x, "Hi", None, (1, 'y)) (UpdateRef(3, 'z)) (graph.resolve('x))

		graph.addUpdate(Update(3, 'z, "!!!", None))

		assertResolved(1, 'y, "Welt", None, (3, 'z)) (graph.resolve('y))
		assertResolved(2, 'x, "Hi", None, (1, 'y)) (graph.resolve('x))
		assertResolved(3, 'z, "!!!", None) (graph.resolve('z))
	}

	@Test
	def removeNonExisting(): Unit = {
		assertEquals(None,
			graph.remove(0))

		graph.addUpdate(Update(0, 'x, "Hi", None))

		assertEquals(None,
			graph.remove(1))

		assertResolved(0, 'x, "Hi", None)(graph.resolve('x))
	}

	@Test
	def readCyclicDependency(): Unit = {
		graph.addUpdate(Update(0, 'x, "X", None, (1, 'y)))
		graph.addUpdate(Update(1, 'y, "Y", None, (0, 'x)))

		assertResolved(0, 'x, "X", None, (1, 'y))(graph.resolve('x))
		assertResolved(1, 'y, "Y", None, (0, 'x))(graph.resolve('y))
	}

	@Test
	def removeCyclicDependency(): Unit = {
		graph.addUpdate(Update(0, 'x, "X", None, (1, 'y)))
		graph.addUpdate(Update(1, 'y, "Y", None, (2, 'z)))
		graph.addUpdate(Update(2, 'z, "Z", None, (0, 'x)))

		assertResolved(0, 'x, "X", None, (1, 'y))(graph.resolve('x))
		assertResolved(1, 'y, "Y", None, (2, 'z))(graph.resolve('y))
		assertResolved(2, 'z, "Z", None, (0, 'x))(graph.resolve('z))

		graph.remove(2)

		assertUnresolved(0, 'x, "X", None, (1, 'y))(UpdateRef(2, 'z))(graph.resolve('x))
		assertUnresolved(1, 'y, "Y", None, (2, 'z))(UpdateRef(2, 'z))(graph.resolve('y))
	}

	@Test(expected = classOf[java.lang.AssertionError])
	def inconsistentUpdate(): Unit = {
		graph.addUpdate(Update(0, 'x, "Hello", None))
		graph.addUpdate(Update(0, 'y, "Welt", None))
	}

	@Test(expected = classOf[java.lang.AssertionError])
	def inconsistentGraph(): Unit = {
		graph.addUpdate(Update(0, 'x, "Hello", Some(1)))
		graph.addUpdate(Update(1, 'y, "Welt", None)) //TODO: Already find inconsistency here?

		graph.resolve('x)
	}

	@Test
	def readTxResolved(): Unit = {
		graph.addUpdate(Update(0, 'x, "Hello", Some(3)))
		graph.addUpdate(Update(1, 'y, "World", Some(3), (0, 'x)))

		assertUnresolved(0, 'x, "Hello", Some(3))(TxRef(3))(graph.resolve('x))
		assertUnresolved(1, 'y, "World", Some(3), (0, 'x))(TxRef(3))(graph.resolve('y))

		assertResolved(0, 'x, "Hello", Some(3))(graph.resolve('x, Some(TxRef(3))))
		assertResolved(1, 'y, "World", Some(3), (0, 'x))(graph.resolve('y, Some(TxRef(3))))

		assertResolved(0, 'x, "Hello", Some(3))(graph.resolve('x))
		assertResolved(1, 'y, "World", Some(3), (0, 'x))(graph.resolve('y))

		graph.remove(0)

		assertNotFound(graph.resolve('x))
		assertUnresolved(1, 'y, "World", Some(3), (0, 'x))(UpdateRef(0, 'x))(graph.resolve('y))
	}
	//TODO: Add more tests with transactions...



}
