package org.yeastrc.xlink.dto;

/**
 * linker table
 */
public class LinkerDTO {

	private int id;
	private String abbr;
	private String name;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAbbr() {
		return abbr;
	}
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

//CREATE TABLE `linker` (
//`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
//`abbr` varchar(255) NOT NULL,
//`name` varchar(255) DEFAULT NULL,
//PRIMARY KEY (`id`),
//UNIQUE KEY `abbr` (`abbr`)
//) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
//