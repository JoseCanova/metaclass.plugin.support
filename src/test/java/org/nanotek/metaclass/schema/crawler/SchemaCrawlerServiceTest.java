package org.nanotek.metaclass.schema.crawler;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaCrawlerServiceTest {

	public SchemaCrawlerServiceTest() {
	}
	@BeforeEach
	public void setUp() throws Exception {
	    String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	    String connectionURL = "jdbc:derby:memory:testDB;create=true";
	    Class.forName(driver);
	    Connection conn = DriverManager.getConnection(connectionURL);
	    Statement statement = conn.createStatement();
	    try(InputStream stream = getClass().getResourceAsStream("/derby_sql_tables/simple_table.sql")){
	    	String str = new String (stream.readAllBytes());
	    	statement.execute(str);
	    }
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
	void testSchemaCrawlerService() {
		URL url = getClass().getResource("/datasource_derby.json");
		var theDataService = SchemaCrawlerDataSourceService.loadFromFile(url.getPath());
		assertNotNull(theDataService);
		var theService = new SchemaCrawlerService(theDataService);
		var tables = theService.getCatalogTables();
		assertNotNull(tables);
	}
}
