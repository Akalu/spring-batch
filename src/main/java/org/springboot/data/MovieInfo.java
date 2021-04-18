package org.springboot.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contains the information about single record in the CSV file
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieInfo {
	
	private String id;
	private String title;
	private String description;
	
	@Override
	public String toString() {
		return "MovieInfo [id=" + id + ", title=" + title + ", description=" + limit(description, 40) + "]";
	}
	
	private static String limit(String str, int len) {
		if (str == null) {
			return null;
		}
		return str.length() < len ? str : str.substring(0, len) + " ...";
	}
	

}
