package org.nanotek.metaclass.schema.crawler;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;

import org.junit.jupiter.api.Test;

public class SchemaCrawlerServiceTest {

	public SchemaCrawlerServiceTest() {
	}

	@Test
	void testSchemaCrawlerService() {
		URL url = getClass().getResource("/datasource.json");
		var theDataService = SchemaCrawlerDataSourceService.loadFromFile(url.getPath());
		assertNotNull(theDataService);
		var theService = new SchemaCrawlerService(theDataService);
		var tables = theService.getCatalogTables();
		assertNotNull(tables);
	}
}
