/*
 * Copyright (c) 2020, CSIRO. All rights reserved.
 * Use is subject to license terms.
 */

package com.csiro.webservices.store;

/**
 * Different MIME types and serialization formats supported by browsers and triplestore 
 * 
 * @author AnilaButt
 */
public class OntMediaType {
	
	/** RDF/XML MIME-type */
	public static final String MIME_RDF_XML = "application/rdf+xml";
	
	/** Pre-registration turtle MIME type */
	public static final String MIME_TURTLE_APP = "application/x-turtle";
	
	/** Requested turtle MIME type */
	public static final String MIME_TURTLE_TEXT = "text/turtle";
	
	/** Unofficial RDF/N3 MIME type */
	public static final String MIME_N3 = "text/n3";
	
	/** XML serialization of RDF supported by Jena */
	public static final String LANG_XML = "RDF/XML";
	
	/** Abbreviated XML serialization of RDF supported by Jena */
	public static final String LANG_XML_ABBREV = "RDF/XML-ABBREV";
	
	/** N-Triple serialization of RDF supported by Jena */
	public static final String LANG_NT = "N-TRIPLE";

	/** Turtle serialization of RDF supported by Jena */
	public static final String LANG_TURTLE = "TURTLE";
	
	/** N3 serialization of RDF supported by Jena */
	public static final String LANG_N3 = "N3";

}
