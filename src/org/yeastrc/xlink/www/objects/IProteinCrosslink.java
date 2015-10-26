package org.yeastrc.xlink.www.objects;

public interface IProteinCrosslink {

	public IProtein getProtein1();
	public IProtein getProtein2();
	
	public int getProtein1Position();
	public int getProtein2Position();
	
}
