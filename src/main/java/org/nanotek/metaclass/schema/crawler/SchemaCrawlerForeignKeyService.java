package org.nanotek.metaclass.schema.crawler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.nanotek.meta.model.rdbms.RdbmsForeignKey;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.meta.model.rdbms.RdbmsMetaClassAttribute;
import org.nanotek.meta.model.rdbms.RdbmsMetaClassForeignKey;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Table;

public class SchemaCrawlerForeignKeyService {

	public SchemaCrawlerForeignKeyService() {
	}

	/*
	 * For now it only support simple foreign keys.
	 * 
	 */
	//TODO: implement multi-column support
	public List<RdbmsMetaClassForeignKey> getMetaClassForeignKeys
			(RdbmsMetaClass metaClass, List<RdbmsMetaClass> metaClasses){
		
		System.err.println("MetaClass to analyze " + metaClass.getTableName());
		
		List<RdbmsMetaClassForeignKey> rdbmsMetaClassFks = new ArrayList<>();
		
		Collection <ForeignKey> fks = metaClass
										.getRdbmsClass()
										.getSchemaTable()
										.getImportedForeignKeys();
		
		fks.forEach(fk ->{
			Table table = fk.getPrimaryKeyTable();
			String tableName = table.getName();
			RdbmsMetaClass tableMetaClass = findMetaClass(metaClasses,tableName);
			ColumnReference columnReference = fk.getColumnReferences().get(0);
			List<RdbmsMetaClassAttribute> attributes =  tableMetaClass.getMetaAttributes();
			RdbmsMetaClassAttribute attribute = findMetaAttribute(attributes , columnReference.getPrimaryKeyColumn());
			
			Table fkTable = fk.getForeignKeyTable();
			String fkTableName = fkTable.getName();
			RdbmsMetaClass fkMetaClass = findMetaClass(metaClasses,fkTableName);
			List<RdbmsMetaClassAttribute> fkattributes =  fkMetaClass.getMetaAttributes();
			Column fkColumn = columnReference.getForeignKeyColumn();
			RdbmsMetaClassAttribute fkAttribute = findMetaAttribute(fkattributes , fkColumn);
			RdbmsForeignKey rdbmsfk = new RdbmsForeignKey(tableMetaClass,attribute,  fkMetaClass, fkAttribute);
			RdbmsMetaClassForeignKey rf = new RdbmsMetaClassForeignKey(rdbmsfk);
			rdbmsMetaClassFks.add(rf);
			});
		
		return rdbmsMetaClassFks;
	}

	private RdbmsMetaClassAttribute findMetaAttribute(List<RdbmsMetaClassAttribute> attributes,
			Column column) {
		return attributes.stream()
						.filter(att -> 
									att.getColumnName().equalsIgnoreCase(column.getName()))
									.findFirst().get();
	}

	private RdbmsMetaClass findMetaClass(List<RdbmsMetaClass> metaClasses, String tableName) {
		return metaClasses.stream().filter(mc -> mc.getTableName().equals(tableName)).findFirst().get();
	}
	
	
}
