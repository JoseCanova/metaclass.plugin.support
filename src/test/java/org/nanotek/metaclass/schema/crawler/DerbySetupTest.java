package org.nanotek.metaclass.schema.crawler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DerbySetupTest {

	public DerbySetupTest() {
	}
	
	@BeforeEach
	public void setUp() throws Exception {
	    String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	    String connectionURL = "jdbc:derby:memory:testDB;create=true";
	    Class.forName(driver);
	    Connection conn = DriverManager.getConnection(connectionURL);
	}

	@AfterEach
	public void tearDown() {
		String connectionURL = "jdbc:derby:memory:testDB;drop=true";
		try{
			DriverManager.getConnection(connectionURL);
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	void test() {
		assertTrue(1==1);
	}
	
}
