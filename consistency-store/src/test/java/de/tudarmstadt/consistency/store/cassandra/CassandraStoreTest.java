package de.tudarmstadt.consistency.store.cassandra;

import de.tudarmstadt.consistency.checker.qual.Local;
import de.tudarmstadt.consistency.checker.qual.Strong;
import de.tudarmstadt.consistency.store.data.A;
import de.tudarmstadt.consistency.store.data.B;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Created on 18.06.18.
 *
 * @author Mirko Köhler
 */
public class CassandraStoreTest {


	private static CassandraDatabase database = null;

	@BeforeClass
	public static void setup() {
		 database = CassandraDatabase.local();
	}

	@AfterClass
	public static void finish() {
		database.close();
	}



	UUID keyA1() {
		return new UUID(573489594L, 8675789563L);
	}

	UUID keyB1() {
		return new UUID(573489456L, 1675789879L);
	}


	@Test
	public void testPassValueInDatabase() throws Exception {
		database.commit(service -> {
			CassandraRef<@Strong A> strongA = service.obtain(keyA1(), A.class, Strong.class);
			CassandraRef<@Strong B> strongB = service.obtain(keyB1(), B.class, Strong.class);

			A a = new @Local A(312, strongB, "hallo");


			strongA.write(a);
			A received = strongA.read();

			assertEquals(a, received);
		}, null);
	}


	@Test
	public void testUseLocalReference() throws Exception {
		database.commit(service -> {
			CassandraRef<@Strong A> strongA = service.obtain(keyA1(), A.class, Strong.class);
			CassandraRef<@Strong B> strongB = service.obtain(keyB1(), B.class, Strong.class);

			A a = new @Local A(4382, strongB, "hallo2");
			B b = new @Local B("test1");

			strongA.write(a);
			strongB.write(b);

			B received1 = strongA.read().b.read();
			B received2 = strongB.read();

			assertEquals(b, received1);
			assertEquals(b, received2);
		}, null);
	}


}