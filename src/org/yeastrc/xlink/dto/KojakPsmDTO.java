package org.yeastrc.xlink.dto;


public class KojakPsmDTO {
	
	private int id;
	private int kojakFileId;
	
	private int scanNumber;


	private String obsMass;
	private int charge;
	private String psmMass;
	private String ppmError;
	
	private String score;
	private String dscore;
	private String pepDiff;
	
	private String peptide1;
	private String link1;
	private String protein1;
	private String peptide2;
	private String link2;
	private String protein2;

	private String linkerMass;

	private String corr;
	private String label;
	private String normRank;
	private String modMass;

	private String ret_time;



	/**
	 * Clone of method 'equals', does not include 'id' property
	 * @param obj
	 * @return
	 */
	public boolean dataEquals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KojakPsmDTO other = (KojakPsmDTO) obj;
		if (charge != other.charge)
			return false;
		if (corr == null) {
			if (other.corr != null)
				return false;
		} else if (!corr.equals(other.corr))
			return false;
		if (dscore == null) {
			if (other.dscore != null)
				return false;
		} else if (!dscore.equals(other.dscore))
			return false;
		if (kojakFileId != other.kojakFileId)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (link1 == null) {
			if (other.link1 != null)
				return false;
		} else if (!link1.equals(other.link1))
			return false;
		if (link2 == null) {
			if (other.link2 != null)
				return false;
		} else if (!link2.equals(other.link2))
			return false;
		if (linkerMass == null) {
			if (other.linkerMass != null)
				return false;
		} else if (!linkerMass.equals(other.linkerMass))
			return false;
		if (modMass == null) {
			if (other.modMass != null)
				return false;
		} else if (!modMass.equals(other.modMass))
			return false;
		if (normRank == null) {
			if (other.normRank != null)
				return false;
		} else if (!normRank.equals(other.normRank))
			return false;
		if (obsMass == null) {
			if (other.obsMass != null)
				return false;
		} else if (!obsMass.equals(other.obsMass))
			return false;
		if (pepDiff == null) {
			if (other.pepDiff != null)
				return false;
		} else if (!pepDiff.equals(other.pepDiff))
			return false;
		if (peptide1 == null) {
			if (other.peptide1 != null)
				return false;
		} else if (!peptide1.equals(other.peptide1))
			return false;
		if (peptide2 == null) {
			if (other.peptide2 != null)
				return false;
		} else if (!peptide2.equals(other.peptide2))
			return false;
		if (ppmError == null) {
			if (other.ppmError != null)
				return false;
		} else if (!ppmError.equals(other.ppmError))
			return false;
		if (protein1 == null) {
			if (other.protein1 != null)
				return false;
		} else if (!protein1.equals(other.protein1))
			return false;
		if (protein2 == null) {
			if (other.protein2 != null)
				return false;
		} else if (!protein2.equals(other.protein2))
			return false;
		if (psmMass == null) {
			if (other.psmMass != null)
				return false;
		} else if (!psmMass.equals(other.psmMass))
			return false;
		if (ret_time == null) {
			if (other.ret_time != null)
				return false;
		} else if (!ret_time.equals(other.ret_time))
			return false;
		if (scanNumber != other.scanNumber)
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		return true;

	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + charge;
		result = prime * result + ((corr == null) ? 0 : corr.hashCode());
		result = prime * result + ((dscore == null) ? 0 : dscore.hashCode());
		result = prime * result + id;
		result = prime * result + kojakFileId;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((link1 == null) ? 0 : link1.hashCode());
		result = prime * result + ((link2 == null) ? 0 : link2.hashCode());
		result = prime * result
				+ ((linkerMass == null) ? 0 : linkerMass.hashCode());
		result = prime * result + ((modMass == null) ? 0 : modMass.hashCode());
		result = prime * result
				+ ((normRank == null) ? 0 : normRank.hashCode());
		result = prime * result + ((obsMass == null) ? 0 : obsMass.hashCode());
		result = prime * result + ((pepDiff == null) ? 0 : pepDiff.hashCode());
		result = prime * result
				+ ((peptide1 == null) ? 0 : peptide1.hashCode());
		result = prime * result
				+ ((peptide2 == null) ? 0 : peptide2.hashCode());
		result = prime * result
				+ ((ppmError == null) ? 0 : ppmError.hashCode());
		result = prime * result
				+ ((protein1 == null) ? 0 : protein1.hashCode());
		result = prime * result
				+ ((protein2 == null) ? 0 : protein2.hashCode());
		result = prime * result + ((psmMass == null) ? 0 : psmMass.hashCode());
		result = prime * result + scanNumber;
		result = prime * result + ((score == null) ? 0 : score.hashCode());
		return result;
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KojakPsmDTO other = (KojakPsmDTO) obj;
		if (charge != other.charge)
			return false;
		if (corr == null) {
			if (other.corr != null)
				return false;
		} else if (!corr.equals(other.corr))
			return false;
		if (dscore == null) {
			if (other.dscore != null)
				return false;
		} else if (!dscore.equals(other.dscore))
			return false;
		if (id != other.id)
			return false;
		if (kojakFileId != other.kojakFileId)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (link1 == null) {
			if (other.link1 != null)
				return false;
		} else if (!link1.equals(other.link1))
			return false;
		if (link2 == null) {
			if (other.link2 != null)
				return false;
		} else if (!link2.equals(other.link2))
			return false;
		if (linkerMass == null) {
			if (other.linkerMass != null)
				return false;
		} else if (!linkerMass.equals(other.linkerMass))
			return false;
		if (modMass == null) {
			if (other.modMass != null)
				return false;
		} else if (!modMass.equals(other.modMass))
			return false;
		if (normRank == null) {
			if (other.normRank != null)
				return false;
		} else if (!normRank.equals(other.normRank))
			return false;
		if (obsMass == null) {
			if (other.obsMass != null)
				return false;
		} else if (!obsMass.equals(other.obsMass))
			return false;
		if (pepDiff == null) {
			if (other.pepDiff != null)
				return false;
		} else if (!pepDiff.equals(other.pepDiff))
			return false;
		if (peptide1 == null) {
			if (other.peptide1 != null)
				return false;
		} else if (!peptide1.equals(other.peptide1))
			return false;
		if (peptide2 == null) {
			if (other.peptide2 != null)
				return false;
		} else if (!peptide2.equals(other.peptide2))
			return false;
		if (ppmError == null) {
			if (other.ppmError != null)
				return false;
		} else if (!ppmError.equals(other.ppmError))
			return false;
		if (protein1 == null) {
			if (other.protein1 != null)
				return false;
		} else if (!protein1.equals(other.protein1))
			return false;
		if (protein2 == null) {
			if (other.protein2 != null)
				return false;
		} else if (!protein2.equals(other.protein2))
			return false;
		if (psmMass == null) {
			if (other.psmMass != null)
				return false;
		} else if (!psmMass.equals(other.psmMass))
			return false;
		if (scanNumber != other.scanNumber)
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
			return false;
		return true;
	}




	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getKojakFileId() {
		return kojakFileId;
	}

	public void setKojakFileId(int kojakFileId) {
		this.kojakFileId = kojakFileId;
	}
	public int getScanNumber() {
		return scanNumber;
	}



	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}



	public int getCharge() {
		return charge;
	}



	public void setCharge(int charge) {
		this.charge = charge;
	}


	public String getObsMass() {
		return obsMass;
	}

	public void setObsMass(String obsMass) {
		this.obsMass = obsMass;
	}


	public String getPsmMass() {
		return psmMass;
	}

	public void setPsmMass(String psmMass) {
		this.psmMass = psmMass;
	}

	public String getPpmError() {
		return ppmError;
	}

	public void setPpmError(String ppmError) {
		this.ppmError = ppmError;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getDscore() {
		return dscore;
	}

	public void setDscore(String dscore) {
		this.dscore = dscore;
	}

	public String getPepDiff() {
		return pepDiff;
	}

	public void setPepDiff(String pepDiff) {
		this.pepDiff = pepDiff;
	}

	public String getPeptide1() {
		return peptide1;
	}

	public void setPeptide1(String peptide1) {
		this.peptide1 = peptide1;
	}

	public String getLink1() {
		return link1;
	}

	public void setLink1(String link1) {
		this.link1 = link1;
	}

	public String getProtein1() {
		return protein1;
	}

	public void setProtein1(String protein1) {
		this.protein1 = protein1;
	}

	public String getPeptide2() {
		return peptide2;
	}

	public void setPeptide2(String peptide2) {
		this.peptide2 = peptide2;
	}

	public String getLink2() {
		return link2;
	}

	public void setLink2(String link2) {
		this.link2 = link2;
	}

	public String getProtein2() {
		return protein2;
	}

	public void setProtein2(String protein2) {
		this.protein2 = protein2;
	}

	public String getLinkerMass() {
		return linkerMass;
	}

	public void setLinkerMass(String linkerMass) {
		this.linkerMass = linkerMass;
	}

	
	public String getCorr() {
		return corr;
	}



	public void setCorr(String corr) {
		this.corr = corr;
	}



	public String getLabel() {
		return label;
	}



	public void setLabel(String label) {
		this.label = label;
	}



	public String getNormRank() {
		return normRank;
	}



	public void setNormRank(String normRank) {
		this.normRank = normRank;
	}



	public String getModMass() {
		return modMass;
	}



	public void setModMass(String modMass) {
		this.modMass = modMass;
	}


	public String getRet_time() {
		return ret_time;
	}



	public void setRet_time(String ret_time) {
		this.ret_time = ret_time;
	}


}

//	CREATE TABLE kojak_psm (
//		  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//		  kojak_file_id INT UNSIGNED NOT NULL,
//		  scan_number INT NOT NULL,
//		  obs_mass VARCHAR(200) NOT NULL,
//		  charge SMALLINT NOT NULL,
//		  psm_mass VARCHAR(200) NOT NULL,
//		  ppm_error VARCHAR(200) NOT NULL,
//		  score VARCHAR(200) NOT NULL,
//		  dscore VARCHAR(200) NOT NULL,
//		  pep_diff VARCHAR(200) NULL,
//		  peptide_1 VARCHAR(2000) NOT NULL,
//		  link_1 VARCHAR(200) NOT NULL,
//		  protein_1 VARCHAR(2000) NOT NULL,
//		  peptide_2 VARCHAR(2000) NOT NULL,
//		  link_2 VARCHAR(200) NOT NULL,
//		  protein_2 VARCHAR(2000) NOT NULL,
//		  linker_mass VARCHAR(200) NOT NULL,
//		  corr VARCHAR(200) NULL,
//		  label VARCHAR(200) NULL,
//		  norm_rank VARCHAR(200) NULL,
//		  mod_mass VARCHAR(200) NULL,
