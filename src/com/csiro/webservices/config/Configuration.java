/*
 * Copyright (c) 2020, CSIRO. All rights reserved.
 * Use is subject to license terms.
 */
package com.csiro.webservices.config;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Configuration interface for this API.
 * <p>
 * Usage Example: Configuration.getProperty(Configuration.STORE_PATH)
 * <p>
 * See {@link Configuration#getProperty(String)}
 * 
 * @author Anila Butt
 */
public class Configuration {
	
	/** Namespace for WEPROV */
	public static final String NS_WEPROV = "http://purl.org/ontology/weprov#";

	/** Namespace for instance resources */
	public static final String NS_RES = "http://weprov.csiro.au/";
	
	/** Namespace for instance resources */
	public static final String NS_EVORES = "http://weprov.csiro.au/evolution/";

	/** Prefix for WEPROV namespace */
	public static final String PREFIX_WEPROV = "prefix.weprov";
	
	/** Prefix for instance resources */
	public static final String PREFIX_DATA = "prefix.data";
	
	/** Prefix for instance resources */
	public static final String PREFIX_EVODATA = "prefix.evodata";

	/** Property key for full-text index path */
	public static final String INDEX_PATH = "index.path";

	/** Property key for triple store path */
	public static final String STORE_PATH = "store.path";
	
	/** Property key for triple store path */
	public static final String SCHEMA_PATH = "schema.path";
	
	/** Property key for ontology path */
	public static final String ONT_PATH = "ontology.path";
	
	/** Property key for image path */
	public static final String IMAGE_PATH = "image.path";
	
	/** Default instance of this class */
	private static Configuration instance;
	
	/** Default logger */
	private Logger logger;
	
	/** Default properties */
	private Properties properties = new Properties();
	
	/**
	 * External classes should invoke static methods
	 * instead of instantiating this class.
	 * 
	 * @see #getProperty(String)
	 */
	private Configuration() {
		logger = Logger.getLogger(getClass().getName());
		try {
			// Try loading configuration file otherwise fall back to a default path
			properties.load(getClass().getResourceAsStream("config.properties"));
		} catch (IOException iox) {
			properties.setProperty(STORE_PATH, "/usr/share/ifkm/store");
			logger.severe("Error in reading store configuration "+iox);
		}
	}
	
	/**
	 * Returns default configuration object.
	 */
	private static Configuration getDefaults() {
		if(instance==null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	/**
	 * Gets value of the specified configuration property.
	 * 
	 * @param key Configuration key
	 */
	public static String getProperty(String key) {
		Configuration config = Configuration.getDefaults();
		return config.properties.getProperty(key);
	}
}
