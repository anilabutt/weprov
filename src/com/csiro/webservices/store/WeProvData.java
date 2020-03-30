package com.csiro.webservices.store;

import com.csiro.webservices.config.Configuration;

public class WeProvData {
	
	public static final String wedata = Configuration.NS_RES;
	public static final String weprovdata = Configuration.NS_EVORES;
	
	// data
	
	public static final String workflow = wedata+"workflow/";
	public static final String program = wedata+"program/";
	public static final String model = wedata+"model/";
	public static final String port = wedata+"port/";
	public static final String channel = wedata+"channel/";
	public static final String param = wedata+"param/";

	public static final String controller = wedata+"controller/";
	public static final String agent = wedata+"agent/";
	
	public static final String generation = weprovdata +"generation/";
	public static final String invalidation = weprovdata +"invalidation/";
	
	
	public static final String creation = weprovdata +"creation/";
	public static final String modification = weprovdata +"modification/";
	public static final String renaming = weprovdata +"renaming/";
	public static final String revision = weprovdata +"revision/";
	
	
	
	
}
