package org.nanotek.metaclass.schema.crawler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.nanotek.meta.model.rdbms.RdbmsMetaClass;
import org.nanotek.meta.model.rdbms.RdbmsMetaClassAttribute;

import schemacrawler.schema.Column;

public class SchemaCrawlerRdbmsMetaClassAttributeService
{
	
	public SchemaCrawlerRdbmsMetaClassAttributeService()
	{ 
	}
	
	public List<RdbmsMetaClassAttribute> generateMetaAttributes(RdbmsMetaClass metaClass) {
		
		var rc = metaClass.getRdbmsClass();
		var lc = rc.getSchemaTable().getColumns();
		return lc.stream().map(c -> createMetaAttribute(c)
		).collect(Collectors.toList());
	}

	private RdbmsMetaClassAttribute createMetaAttribute(Column c) {
		RdbmsMetaClassAttribute md = new RdbmsMetaClassAttribute();
		md.setClazz(c.getColumnDataType().getTypeMappedClass().getName());
		md.setColumnName(c.getName());
		md.setFieldName(SnakeToCamelCaseTranslator.from(c.getName()));
		md.setSqlType(c.getColumnDataType().getDatabaseSpecificTypeName());
		var attributes = c.getAttributes();
		verifyAttributes(c,attributes);
		md.setPartOfId(c.isPartOfPrimaryKey());
		md.setPartOfIndex(c.isPartOfIndex());
		md.setPartOfForeignKey(c.isPartOfForeignKey());
		md.setLength(String.valueOf(c.getSize()));
		md.setScale(c.getDecimalDigits());
		md.setRequired(!c.isNullable());
		return md;
	}

	private void verifyAttributes(Column c, Map<String, Object> attributes) {
	}

}
