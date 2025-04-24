package org.nanotek.metaclass.schema.crawler;


import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.nanotek.meta.model.rdbms.table.RdbmsSchemaTable;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;

public class SchemaCrawlerService {


	SchemaCrawlerOptions schemaCrawlerOptions;
	
	SchemaCrawlerDataSourceService schemaCrawlerDataSourceService;
	
	public SchemaCrawlerService() {}
	
	
	
	public SchemaCrawlerService(SchemaCrawlerDataSourceService schemaCrawlerDataSourceService) {
		super();
		this.schemaCrawlerDataSourceService = schemaCrawlerDataSourceService;
		postConstruct();
	}



	private void postConstruct() {
		schemaCrawlerOptions = prepareSchemaCrawlerOptions();
	}



	public Collection<RdbmsSchemaTable> getRdbmsMetaclassTable(){
		return getCatalogTables()
				.stream()
				.map(t-> new RdbmsSchemaTable(t))
				.collect(Collectors.toList());
	}
	
	public Collection<Table> getCatalogTables(){
		Catalog  catalog;
		try {
			catalog = SchemaCrawlerUtility.getCatalog(
					schemaCrawlerDataSourceService.getDatabaseConnectionSource(), 
					schemaCrawlerOptions);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return catalog.getTables();
	}
	
	
	public Optional<List<Column>> getTableColumns(Optional<Table> oTable){
		return oTable.map(t -> t.getColumns());
	}
	
	public Optional<PrimaryKey> getTablePrimaryKey(Optional<Table> oTable){
		return oTable.map(t -> t.getPrimaryKey());
	}
	
	public SchemaCrawlerOptions prepareSchemaCrawlerOptions() {
		var schemaInfoLevelBuilder = getSchemaBuilder(); 
		var loadOptionsBuilder = getLoadOptionsBuilder(schemaInfoLevelBuilder);
		return getSchemaCrawlerOptions(loadOptionsBuilder);
	}
	
	public SchemaInfoLevelBuilder getSchemaBuilder() {
		return SchemaInfoLevelBuilder.builder()
				.setRetrieveAdditionalColumnAttributes(true)
				.setRetrieveAdditionalColumnMetadata(false)
				.setRetrieveColumnDataTypes(true)
				.setRetrieveForeignKeys(true)
				.setRetrieveIndexes(true)
				.setRetrieveIndexInformation(true)
				.setRetrieveTriggerInformation(false)
				.setRetrievePrimaryKeys(true)
				.setRetrieveTableColumns(true)
				.setRetrieveTables(true);
	}
	
	public LoadOptionsBuilder getLoadOptionsBuilder(SchemaInfoLevelBuilder builder) {
		return  LoadOptionsBuilder.builder()
				.withSchemaInfoLevel(builder.toOptions());
	}
	
	public SchemaCrawlerOptions getSchemaCrawlerOptions(LoadOptionsBuilder loadOptionsBuilder) {
		return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
				.withLoadOptions(loadOptionsBuilder.toOptions());
	}

}
