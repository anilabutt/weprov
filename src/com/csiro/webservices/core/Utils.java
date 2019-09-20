/*
 * Copyright (c) 2020, CSIRO. All rights reserved.
 * Use is subject to license terms.
 */
package com.csiro.webservices.core;

import java.security.SecureRandom;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Utility methods
 * 
 * @author Anila Butt
 *
 */
@Path("/util")
public class Utils {
	
	/** A strong random number generator */
	private static SecureRandom random = new SecureRandom();
	
	/** Default size (in bytes) of the unique id */
	private static final byte UID_LENGTH = 0x0f;

	/**
	 * Creates a strong and unique ID.
	 * @return An ID as string
	 */
	@GET @Path("/uid") @Produces("text/plain")
	public String getUID() {
		// Ensure unique behavior for random numbers
		random.setSeed(System.currentTimeMillis());
		
        // Generate random bytes
		byte[] randomBytes = new byte[UID_LENGTH];
        random.nextBytes(randomBytes);
        
        StringBuilder result = new StringBuilder();
		for(byte b:randomBytes) {
			// Reduce the byte to 4-bits 
			int test = b & 0x0f;
			// Store its HEXA representation in the result string
			result.append(Long.toHexString(test));
		}
		return result.toString();
	}
}