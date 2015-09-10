package org.yeastrc.xlink.dto;

/**
 * linker linker_monolink_mass
 *
 */
public class LinkerMonolinkMassDTO {

	private int linkerId;
	private double mass;
	
	
	public int getLinkerId() {
		return linkerId;
	}
	public void setLinkerId(int linkerId) {
		this.linkerId = linkerId;
	}
	public double getMass() {
		return mass;
	}
	public void setMass(double mass) {
		this.mass = mass;
	}

}

//CREATE TABLE `linker_monolink_mass` (
//`linker_id` int(10) unsigned NOT NULL,
//`mass` double NOT NULL,
//KEY `linker_id` (`linker_id`),
//KEY `mass` (`mass`),
//CONSTRAINT `linker_monolink_mass_ibfk_1` FOREIGN KEY (`linker_id`) REFERENCES `linker` (`id`) ON DELETE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=latin1;
