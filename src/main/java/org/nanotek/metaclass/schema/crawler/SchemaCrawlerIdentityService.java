package org.nanotek.metaclass.schema.crawler;

import org.nanotek.meta.model.classification.MetaClassIdentityClassifier;
import org.nanotek.meta.model.rdbms.RdbmsMetaClass;

public class SchemaCrawlerIdentityService 
implements MetaClassIdentityClassifier{
	
	public void classifyMetaClass(RdbmsMetaClass metaClass) {
		classifyIdentity(metaClass)
		.ifPresentOrElse(classification -> {
			metaClass.setIdentityClassification(classification.type().name());
		}, () -> {throw new RuntimeException("invalid classification");});
	}
	
}
