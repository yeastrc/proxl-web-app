package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * This version of VennDiagramData is for rendering out as JSON in the format the venn.js code requires
 *
 *
 */
public class VennDiagramDataToJSON {

	/**
	 * Individual labels and counts
	 */
	private List<VennDiagramDataSetEntry> sets;
	/**
	 * Counts for the intersection of the labels.  The array "sets" in each element references the indexes of the entries in "sets" above
	 */
	private List<VennDiagramDataAreaEntry> areas;
	
	
	
	public List<VennDiagramDataSetEntry> getSets() {
		return sets;
	}


	public void setSets(List<VennDiagramDataSetEntry> sets) {
		this.sets = sets;
	}


	public List<VennDiagramDataAreaEntry> getAreas() {
		return areas;
	}


	public void setAreas(List<VennDiagramDataAreaEntry> areas) {
		this.areas = areas;
	}


	/**
	 * sets property
	 *
	 */
	public static class VennDiagramDataSetEntry {
		
		private String label;
		private int size;
		
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
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
			VennDiagramDataSetEntry other = (VennDiagramDataSetEntry) obj;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			return true;
		}
		
		
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public int getSize() {
			return size;
		}
		public void setSize(int size) {
			this.size = size;
		}
		
	}

	
	/**
	 * areas property
	 *
	 */
	public static class VennDiagramDataAreaEntry {
		
		private List<Integer> sets; //  The sets are positional numbers based on the indexes of data in "sets" above
		private int size;
		
		
		public List<Integer> getSets() {
			return sets;
		}
		public void setSets(List<Integer> sets) {
			this.sets = sets;
		}
		public int getSize() {
			return size;
		}
		public void setSize(int size) {
			this.size = size;
		}
		
	}
		
}



//sets: Array[3]
//  0: Object
//	label: "A"
//	size: 17
//
//
//areas: Array[4]
//  0: Object
//	sets: Array[2]
//		0: 0
//		1: 1
//	size: 14
//1: Object
//sets: Array[2]
//0: 0
//1: 2
//length: 2
//__proto__: Array[0]
//size: 4
//__proto__: Object
//2: Object
//sets: Array[2]
//0: 1
//1: 2
//length: 2
//__proto__: Array[0]
//size: 3
//__proto__: Object
//3: Object
//sets: Array[3]
//0: 0
//1: 1
//2: 2
//length: 3
//__proto__: Array[0]
//size: 2