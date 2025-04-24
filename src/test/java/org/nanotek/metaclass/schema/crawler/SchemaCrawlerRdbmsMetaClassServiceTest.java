package org.nanotek.metaclass.schema.crawler;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;

import org.junit.jupiter.api.Test;

public class SchemaCrawlerRdbmsMetaClassServiceTest {

	public SchemaCrawlerRdbmsMetaClassServiceTest() {
	}

	@Test
	void testSchemaCrawlerRdbmsMetaClassService() {
		URL url = getClass().getResource("/datasource.json");
		var theDataService = SchemaCrawlerDataSourceService.loadFromFile(url.getPath());
		var theService = new SchemaCrawlerService(theDataService);
		var theCrawler = new SchemaCrawlerRdbmsMetaClassService(theService);
		assertNotNull(theCrawler);
		var theList = theCrawler.getMetaClassList();
		assertTrue (!theList.isEmpty());
		
	}
}
