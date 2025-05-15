package org.nanotek.metaclass.schema.crawler.classification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.metaclass.schema.crawler.SchemaCrawlerDataSourceService;
import org.nanotek.metaclass.schema.crawler.SchemaCrawlerRdbmsMetaClassService;
import org.nanotek.metaclass.schema.crawler.SchemaCrawlerService;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SchemaCrawlerIdentityServiceTest {

	public SchemaCrawlerIdentityServiceTest() {
	}
	
	/**
	 * @throws Exception
	 */
	@BeforeEach
	public void setUp() throws Exception {
	    String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	    String connectionURL = "jdbc:derby:memory:testDB;create=true";
	    Class.forName(driver);
	    Connection conn = DriverManager.getConnection(connectionURL);
	    conn.setAutoCommit(false);
	    Statement statement = conn.createStatement();
	    try(InputStream stream = getClass().getResourceAsStream("/derby_sql_tables/compositeid_simple_table.sql")){
	    	String str = new String (stream.readAllBytes());
	    	statement.addBatch(str);
	    };
	    try(InputStream stream = getClass().getResourceAsStream("/derby_sql_tables/compositeid_simple_table2.sql")){
	    	String str = new String (stream.readAllBytes());
	    	statement.addBatch(str);
	    	statement.executeBatch(); // Execute all DDL statements in batch
            conn.commit(); // Commit changes
	    }
	}
	@AfterEach
	public void tearDown() {
		String connectionURL = "jdbc:derby:memory:testDB;drop=true";
		try{
			DriverManager.getConnection(connectionURL);
		}catch (Exception ex) {
			//compositeid_simple_table.sql
			//avoid the stack trace of derby
		}
	}
	
	@Test
	void testSchemaCrawlerRdbmsMetaClassService() {
		URL url = getClass().getResource("/datasource_derby.json");
		var theDataService = SchemaCrawlerDataSourceService.loadFromFile(url.getPath());
		var theService = new SchemaCrawlerService(theDataService);
		var theCrawler = new SchemaCrawlerRdbmsMetaClassService(theService);
		assertNotNull(theCrawler);
		var theList = theCrawler.getMetaClassList();
		assertTrue (!theList.isEmpty());
		assertTrue(theList.size()==1);
		RdbmsMetaClass metaClass = theList.get(0);
		assertNotNull(metaClass.getIdentityClassification());
		assertTrue(metaClass.getIdentityClassification().equals("COMPOSITE"));
	}
}
