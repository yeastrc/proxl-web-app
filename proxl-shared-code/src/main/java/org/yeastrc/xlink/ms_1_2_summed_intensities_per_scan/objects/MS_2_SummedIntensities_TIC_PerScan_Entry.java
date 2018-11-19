package org.yeastrc.xlink.ms_1_2_summed_intensities_per_scan.objects;

import java.math.BigDecimal;

/**
 * Entry in MS_2_SummedIntensities_TIC_PerScan_JSONRoot
 * 
 * Data per ms2 scan:
 * 
 * Scan Number (sn)
 * Retention Time (rt)
 * Total Ion Current (tic)
 *
 */
public class MS_2_SummedIntensities_TIC_PerScan_Entry {

	private int sn;
	private BigDecimal rt;
	private double tic;
	private int ms1SN;
	
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
	public int getMs1SN() {
		return ms1SN;
	}
	public void setMs1SN(int ms1sn) {
		ms1SN = ms1sn;
	}
}
