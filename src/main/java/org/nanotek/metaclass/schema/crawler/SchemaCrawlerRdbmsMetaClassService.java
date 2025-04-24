package org.nanotek.metaclass.schema.crawler;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nanotek.meta.constants.SystemStaticMessageSource;
import org.nanotek.meta.model.TableClassName;
import org.nanotek.meta.model.rdbms.RdbmsIndex;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.meta.model.rdbms.RdbmsMetaClassAttribute;
import org.nanotek.meta.model.rdbms.RdbmsMetaClassForeignKey;
import org.nanotek.meta.model.rdbms.table.RdbmsSchemaTable;

import reactor.core.publisher.Mono;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

//TODO: implement a "symbolic package name" and the classname strategy for camel case.
//TODO: implement a metaclass method to provide information about a single metaclass present on "metaclass list".
//TODO: implement persistence on relational db for model_relation classes.
public class SchemaCrawlerRdbmsMetaClassService 
{

	ColumnNameTranslationStrategy columnNameTranslationStrategy; 
	
	SchemaCrawlerService schemaCrawlerService;
	
	SystemStaticMessageSource messageSource;
	
	SchemaCrawlerRdbmsMetaClassAttributeService schemaCrawlerRdbmsMetaClassAttributeService;
	
	SchemaCrawlerRbmsIndexService schemaCrawlerRbmsIndexService;
	
	SchemaCrawlerForeignKeyService schemaCrawlerForeignKeyService;
	
	public SchemaCrawlerRdbmsMetaClassService() {
		postConstruct();
	}
	
	
	
	public SchemaCrawlerRdbmsMetaClassService(SchemaCrawlerService schemaCrawlerService) {
		super();
		this.schemaCrawlerService = schemaCrawlerService;
		postConstruct();
	}



	private void postConstruct() {
		this.columnNameTranslationStrategy = new ColumnNameTranslationStrategy();
		this.messageSource = new SystemStaticMessageSource();
		this.schemaCrawlerRdbmsMetaClassAttributeService = new SchemaCrawlerRdbmsMetaClassAttributeService();
		this.schemaCrawlerRbmsIndexService=new SchemaCrawlerRbmsIndexService();
		this.schemaCrawlerForeignKeyService=new SchemaCrawlerForeignKeyService();
		
	}

	//TODO:Verify pagination for large set of TableClasses if necessary.
	public List<TableClassName> getTableClassNameList(){
			return getMetaClassList()
						.stream()
						.map(mtc -> new TableClassName(mtc.getTableName(),mtc.getClassName()))
						.collect(Collectors.toList());
	}
	
	
	

	public List<RdbmsMetaClass> getMetaClassList(){
		 List<RdbmsMetaClass>  rdbmsMetaClassList = getCatalogTables()
												 	.stream()
													.map(t -> createMetaClass(t))
													.collect(Collectors.toList());
		 
		 rdbmsMetaClassList
		 					.forEach(r ->{
			 						populateMetaClassForeignKeys(r,rdbmsMetaClassList);
			 						populateMetaClassIndexes(r);
		 					});
		 
		 return rdbmsMetaClassList;
	}
	
	//TODO: Prepare service to populate foreign key - relation attributes.
	//TODO: Prepare a properties to manage the case when to use snake_case converter
	//TODO: Create a class to manage removal of special characteres as being done on previous version.
	private RdbmsMetaClass createMetaClass(RdbmsSchemaTable schemaTable) {
		Table table = schemaTable.getSchemaTable();
		String tableName = Optional.ofNullable(table.getName()).orElse(table.getFullName());
		String className = tableName
								.toLowerCase()
								.substring(0, 1)
								.toUpperCase()
								.concat(SnakeCaseFluentConverter.from(tableName).substring(1));
		RdbmsMetaClass metaClass = new RdbmsMetaClass(tableName , className , table);
		populateMetaClassAttributes(metaClass);
		return metaClass;
	}
	
	private void populateMetaClassForeignKeys(RdbmsMetaClass metaClass,List<RdbmsMetaClass>metaClasses) {
		List<RdbmsMetaClassForeignKey> fks =   schemaCrawlerForeignKeyService
											.getMetaClassForeignKeys(metaClass, metaClasses); 
		metaClass.setRdbmsForeignKeys(fks);
	}
	
	private void populateMetaClassIndexes(RdbmsMetaClass metaClass) {
		List<RdbmsIndex>indexes=  schemaCrawlerRbmsIndexService.getRdbmsIndexList(metaClass);
		metaClass.setRdbmsIndexes(indexes);
	}

	private void populateMetaClassAttributes(RdbmsMetaClass metaClass) {
		List<RdbmsMetaClassAttribute> attributes =  schemaCrawlerRdbmsMetaClassAttributeService.generateMetaAttributes(metaClass);
		metaClass.setMetaAttributes(attributes);
		//schemaCrawlerRdbmsMetaClassAttributeService.saveMetaAttributes(metaClass);
		//TODO:Refactor moving attribute creation to its own service.
		/*
		 * lc.forEach(c -> { RdbmsMetaClassAttribute md = new RdbmsMetaClassAttribute();
		 * md.setClazz(c.getColumnDataType().getTypeMappedClass().getName());
		 * md.setColumnName(c.getName());
		 * md.setFieldName(SnakeToCamelCaseTranslator.from(c.getName())); var attributes
		 * = c.getAttributes(); verifyAttributes(c,attributes);
		 * md.setPartOfId(c.isPartOfPrimaryKey()); md.setPartOfIndex(c.isPartOfIndex());
		 * md.setPartOfForeignKey(c.isPartOfForeignKey());
		 * metaClass.addMetaAttribute(md); });
		 */
		processMetaAttributesIds(metaClass);
	}
	
	//TODO: implement method and generate a "rationale for the attribute id`s" otherwise cancel method call
	private void processMetaAttributesIds(RdbmsMetaClass metaClass) {
	}


	//TODO:Inspect attributes to check additional information on column.
	private void verifyAttributes(Column c, Map<String, Object> attributes) {
		System.out.println(attributes);
	}

	private Stream<Table> toStream(Collection<Table> ct) {
		return ct.stream();
	}

	private Collection<RdbmsSchemaTable> getCatalogTables(){
		return schemaCrawlerService.getRdbmsMetaclassTable();
	}

	public Mono<RdbmsMetaClass> getRdbmsMetaClass(Mono<TableClassName> tableClassNameMono) {
		return tableClassNameMono
					.flatMap(tcn -> getRdbmsMetaClass(tcn)
							.onErrorMap(o -> new RuntimeException("" , o)
					));
	}

	private Mono<RdbmsMetaClass> getRdbmsMetaClass(TableClassName tcn) {
		return Mono
				.just(getMetaClassList().stream()
				.filter(c -> c.getClassName().equals(tcn.className()) && c.getTableName().equals(tcn.tableName()))
				.findAny()).flatMap(Mono::justOrEmpty);
	}


}
