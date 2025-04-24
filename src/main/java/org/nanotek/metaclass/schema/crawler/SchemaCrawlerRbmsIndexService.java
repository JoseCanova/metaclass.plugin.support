package org.nanotek.metaclass.schema.crawler;

import java.util.ArrayList;
import java.util.List;

import org.nanotek.meta.model.rdbms.RdbmsIndex;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;

import schemacrawler.schema.Table;

public class SchemaCrawlerRbmsIndexService {

	public SchemaCrawlerRbmsIndexService() {
	}

	public List<RdbmsIndex> getRdbmsIndexList(RdbmsMetaClass metaClass){
		List<RdbmsIndex> indexes = new ArrayList<>();
		
		Table table = metaClass.getRdbmsClass().getSchemaTable();
		
		table
			.getIndexes()
			.forEach(idx ->{
				indexes.add(new RdbmsIndex(idx));
			});
		
		return indexes;
	}
	
}
