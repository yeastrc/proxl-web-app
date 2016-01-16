package org.yeastrc.xlink.www.objects;

public class ChainProteinPair {

	public String getChain1() {
		return chain1;
	}
	public void setChain1(String chain1) {
		this.chain1 = chain1;
	}
	public String getChain2() {
		return chain2;
	}
	public void setChain2(String chain2) {
		this.chain2 = chain2;
	}
	public int getProtein1() {
		return protein1;
	}
	public void setProtein1(int protein1) {
		this.protein1 = protein1;
	}
	public int getProtein2() {
		return protein2;
	}
	public void setProtein2(int protein2) {
		this.protein2 = protein2;
	}
	
	private String chain1;
	private String chain2;
	private int protein1;
	private int protein2;
	
}
