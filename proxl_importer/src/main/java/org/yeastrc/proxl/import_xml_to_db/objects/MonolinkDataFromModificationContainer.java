package org.yeastrc.proxl.import_xml_to_db.objects;

/**
 * Initial Monolink Data from a Modification object
 *
 */
public class MonolinkDataFromModificationContainer {

	private int position;
	private boolean is_N_Terminal;
	private boolean is_C_Terminal;
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public boolean isIs_N_Terminal() {
		return is_N_Terminal;
	}
	public void setIs_N_Terminal(boolean is_N_Terminal) {
		this.is_N_Terminal = is_N_Terminal;
	}
	public boolean isIs_C_Terminal() {
		return is_C_Terminal;
	}
	public void setIs_C_Terminal(boolean is_C_Terminal) {
		this.is_C_Terminal = is_C_Terminal;
	}
}
