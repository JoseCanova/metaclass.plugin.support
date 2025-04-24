package org.nanotek.metaclass.schema.crawler;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A "better of worlds" camelToSnake and SnakeToCamel, 
 * it does handle (not designed for) with malformed camel or snake cases.
 */
public record SnakeToCamelCaseTranslator(String snakeCaseOrigin) implements Supplier<String>{

	public static String from (String origin) {
		return new SnakeToCamelCaseTranslator(origin).get();
	}
	
	@Override
	public String get() {
		return snakeToCamel();
	}

	private String snakeToCamel()
	{
		var str = snakeCaseOrigin.toLowerCase();
//		 Capitalize first letter of string
		str = str.substring(0, 1).toLowerCase()
				+ str.substring(1);

		// Run a loop till string
		// string contains underscore
		while (str.contains("_")) {

			// Replace the first occurrence
			// of letter that present after
			// the underscore, to capitalize
			// form of next letter of underscore
			str = str
					.replaceFirst(
							"_[a-z]",
							String.valueOf(
									Character.toUpperCase(
											str.charAt(
													str.indexOf("_") + 1))));
			Pattern pat = Pattern.compile("_[0-9]+");
			Matcher mat = pat.matcher(str);
			if (mat.find()) {
				String subsequence = mat.group();
			str = str
					.replaceFirst(
							"_[0-9]+",subsequence.substring(1));
			}
		}

//		// Return string
		return str;
	}
}
