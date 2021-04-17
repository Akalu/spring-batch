package org.springboot.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Contains the information about single record in the CSV file
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MovieInfo {
	
	private String id;
	private String title;
	private String description;

}
