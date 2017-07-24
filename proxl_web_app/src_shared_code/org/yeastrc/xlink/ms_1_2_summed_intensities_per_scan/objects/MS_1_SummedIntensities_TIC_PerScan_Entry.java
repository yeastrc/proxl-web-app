package org.yeastrc.xlink.ms_1_2_summed_intensities_per_scan.objects;

import java.math.BigDecimal;

/**
 * Entry in MS_1_SummedIntensities_TIC_PerScan_JSONRoot
 * 
 * Data per ms1 scan:
 * 
 * Scan Number (sn)
 * Retention Time (rt)
 * Total Ion Current (tic)
 *
 */
public class MS_1_SummedIntensities_TIC_PerScan_Entry {

	private int sn;
	private BigDecimal rt;
	private double tic;
	
	/**
	 * Scan Number
	 * @return
	 */
	public int getSn() {
		return sn;
	}
	/**
	 * Scan Number
	 * @param sn
	 */
	public void setSn(int sn) {
		this.sn = sn;
	}
	/**
	 * Retention Time
	 * @return
	 */
	public BigDecimal getRt() {
		return rt;
	}
	/**
	 * Retention Time
	 * @param rt
	 */
	public void setRt(BigDecimal rt) {
		this.rt = rt;
	}
	/**
	 * Total Ion Current
	 * @return
	 */
	public double getTic() {
		return tic;
	}
	/**
	 * Total Ion Current
	 * @param tic
	 */
	public void setTic(double tic) {
		this.tic = tic;
	}
}
