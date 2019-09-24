/*
 * Copyright (c) 2020, CSIRO. All rights reserved.
 * Use is subject to license terms.
 */

package com.csiro.webservices.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Model;
import org.json.JSONException;
import org.json.JSONObject;

import com.csiro.webservices.app.RDFModel_to_JSON;
import com.csiro.webservices.app.WorkflowCreation;
import com.csiro.webservices.store.OntMediaType;


/**
 * REST services for Course
 * 
 * @author anila
 */

@Path("/workflow")
public class Workflow extends GenericService{

	/**
	 * Default constructor to initializes logging by its parent.
	 */
	public Workflow() {
		super(Workflow.class.getName());
	}
	
	
	/**
	 * Service to add info about workflow  
	 * @param Workflow in JSON format
	 * @return number of triples added into the store
	 * @throws JSONException 
	 * */
	@POST @Produces(MediaType.TEXT_HTML) @Consumes(MediaType.APPLICATION_JSON)
	public String addWorkflow(InputStream incomingData) throws JSONException {
		StringBuilder workflowBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				workflowBuilder.append(line);
			}
			
			WorkflowCreation RDFlogger =  new WorkflowCreation();
			
			
			Model _model = RDFlogger.generateRDF(workflowBuilder.toString());
			
			logger.info(_model.size()+"");
			return add(_model);
			
		} catch (Exception e) {
			logger.info("Error : - " + e);
			return e+"";
		}

	}

	/**
	 * Service to describe a particular Course   
	 * @param courseId of person
	 * @return Description of Course in specified serialization format
	 * */
	@GET @Path("/{workflowId}") @Produces(MediaType.APPLICATION_JSON)
	public String getWorkflow(@PathParam("workflowId") String workflowId) {
		try {
			Model _model = getWorkflowAsModel(workflowId);
			
			RDFModel_to_JSON rdfToJson = new RDFModel_to_JSON();
			
			return rdfToJson.tansformInputJSON(_model).toString();
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
}
