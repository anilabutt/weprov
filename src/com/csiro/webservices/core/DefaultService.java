/*
 * Copyright (c) 2020, CSIRO. All rights reserved.
 * Use is subject to license terms.
 */
package com.csiro.webservices.core;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.csiro.webservices.rest.GenericService;
import com.csiro.webservices.store.OntMediaType;
import com.csiro.webservices.store.TripleStore;


/**
 * Default implementation of GET (single & list), POST, PUT, & DELETE methods
 * 
 * @author Anila Butt
 *
 */
public abstract class DefaultService extends GenericService {
	
	/**
	 * Initializes this service and logging.
	 * 
	 * @see {@link LoggerService#LoggerService(String)}
	 */
	public DefaultService(String loggerName) {
		super(loggerName);
	}
	
	/**
	 * Prepares SPARQL query to list resources
	 * @param filter Any filter to be applied on the listing
	 * @return SPARQL query to list resources
	 */
	protected abstract String prepareListQuery(String filter);

	/**
	 * Lists all resources
	 * 
	 * @param filter Any filter to be applied on the listing
	 * @return List of resources as SPARQL result set encoded in JSON
	 * @see #prepareListQuery(String)
	 */
	@GET @Path("/list") @Produces("application/json")
	public String list(@QueryParam("filter")String filter) {
		String sparql = prepareListQuery(filter);
		logger.info("Prepared SPARQL query successfully");
		logger.info(sparql);
		TripleStore store = TripleStore.getDefaultStore();
		return store.execSelect(sparql);
	}
	
	/**
	 * Prepares SPARQL query to retrieve a single resource
	 * 
	 * @param uri Fully qualified URI of the resource
	 * @return SPARQL query
	 */
	protected abstract String prepareResourceQuery(String uri);
	
	/**
	 * Retrieves a single resource given its UID
	 * 
	 * @param uid Unique ID of the resource
	 * @return A resource as SPARQL result set encoded in JSON
	 * @see #prepareResourceQuery(String)
	 */
	@GET @Path("/{uid}") @Produces("application/json")
	public String get(@PathParam("uid")String uid) {
		String sparql = prepareResourceQuery(uid);
		logger.info("Prepared SPARQL query successfully");
		logger.info(sparql);
		TripleStore store = TripleStore.getDefaultStore();
		return store.execSelect(sparql);
	}
	
	/**
	 * Adds a new resource in the triple store
	 * 
	 * @param n3 Resource meta-data in RDF/N3
	 * @return OK or other HTTP response
	 */
	@POST @Consumes(OntMediaType.MIME_N3)
	public Response post(String n3) {
		logger.info("N3 data received\n" + n3);
		String tripleCount = add(n3, OntMediaType.LANG_N3);
		return Response.ok(tripleCount + " triples inserted").build();
	}
	
	/**
	 * Updates an existing resource
	 * 
	 * @param uid Unique ID of the resource
	 * @param n3 Updated resource meta-data in RDF/N3
	 * @return OK or other HTTP response
	 */
	@PUT @Path("/{uid}") @Consumes(OntMediaType.MIME_N3)
	public Response put(@PathParam("uid")String uid, String n3) {
		logger.info("N3 data received\n" + n3);
		addStatement(n3, OntMediaType.LANG_N3, true);
		return Response.ok("Resource updated").build();
	}
	
}