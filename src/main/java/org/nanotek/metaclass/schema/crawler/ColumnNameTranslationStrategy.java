package org.nanotek.metaclass.schema.crawler;


public class ColumnNameTranslationStrategy {

	
	public String processNameTranslationStrategy(String name) {
		String newName = name.replaceAll("\\_[$&%.]+", "_");
		return newName;
	}
}
