package org.nanotek.metaclass.schema.crawler;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;

import org.junit.jupiter.api.Test;

public class SchemaCrawlerDataSourceServiceTest {

	public SchemaCrawlerDataSourceServiceTest() {
	}

	@Test
	void testSchemaCrawlerDataSourceService() {
		URL url = getClass().getResource("/datasource.json");
		SchemaCrawlerDataSourceService theService = SchemaCrawlerDataSourceService.loadFromFile(url.getPath());
		assertNotNull(theService);
	}
	
}
