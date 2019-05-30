package org.yeastrc.proxl.import_xml_to_db.unified_reported_peptide.objects;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.yeastrc.xlink.base.constants.UnifiedReportedPeptideConstants;

/**
 * 
 *
 */
public class UnifiedRpSinglePeptideDynamicMod implements Comparable<UnifiedRpSinglePeptideDynamicMod> {

	private int position;
	private double mass;
	private BigDecimal massRounded;
	
	private boolean is_N_Terminal;
	private boolean is_C_Terminal;

	/*
	 * Order by position and then mass
	 */
	@Override
	public int compareTo(UnifiedRpSinglePeptideDynamicMod o) {
		
		if ( o.position != position ) {

			return position - o.position;
		}
		
		return massRounded.compareTo( o.massRounded );
			
//			if ( mass < o.mass ) {
//				
//				return -1;
//			} else if ( mass == o.mass ) {
//				
//				return 0;
//			} else {
//				return 1;
//			}
		
	}

	public double getMass() {
		return mass;
	}
	public void setMass(double mass) {
		this.mass = mass;
		
		BigDecimal modMassBD = BigDecimal.valueOf( mass );
		
		massRounded = modMassBD.setScale( UnifiedReportedPeptideConstants.DECIMAL_POSITIONS_ROUNDED_TO, RoundingMode.HALF_UP );
	}


	public BigDecimal getMassRounded() {
		return massRounded;
	}

	
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

	public void setMassRounded(BigDecimal massRounded) {
		this.massRounded = massRounded;
	}
	

}
