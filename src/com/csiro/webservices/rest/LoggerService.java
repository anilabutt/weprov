/*
 * Copyright (c) 20220, CSIRO. All rights reserved.
 * Use is subject to license terms.
 */
package com.csiro.webservices.rest;

import java.util.logging.Logger;

/**
 * Base class to implement logging requirements.
 * 
 * @author Anila Butt
 */
public abstract class LoggerService {

	/** Default logger */
	protected Logger logger;
	
	/**
	 * Initializes logging for this service using its child class reference
	 * 
	 * @param loggerName A name for the logger. Child classes should preferably send
	 * their class name by invoking {@link Class#getName()} method.
	 */
	public LoggerService(String loggerName) {
		logger = Logger.getLogger(loggerName);
	}

}