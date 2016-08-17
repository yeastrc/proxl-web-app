package org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs;

public class DroppedPeptideCount {

	private static int droppedPeptideCount = 0;
	private static int droppedPsmCount = 0;
	
	public static void incrementDroppedPeptideCount() {
		DroppedPeptideCount.droppedPeptideCount++;
	}
	public static void incrementDroppedPsmCount() {
		DroppedPeptideCount.droppedPsmCount++;
	}

	
	public static int getDroppedPeptideCount() {
		return droppedPeptideCount;
	}
	public static void setDroppedPeptideCount(int droppedPeptideCount) {
		DroppedPeptideCount.droppedPeptideCount = droppedPeptideCount;
	}
	public static int getDroppedPsmCount() {
		return droppedPsmCount;
	}
	public static void setDroppedPsmCount(int droppedPsmCount) {
		DroppedPeptideCount.droppedPsmCount = droppedPsmCount;
	}

	
}
