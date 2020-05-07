/* Copyright (c) 2020, CSIRO. All rights reserved.
 * Use is subject to license terms.
 */

package com.csiro.webservices.rest;

import com.csiro.webservices.store.*;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;




/**
 * A generic REST service
 * @author Anila Butt
 */

public abstract class GenericService extends LoggerService {

	/** Default triple store */
	protected QuadStore store;
	
   /**
	 * Initializes this service and logging.
	 * 
	 * @see {@link LoggerService#LoggerService(String)}
	 */
	public GenericService(String loggerName) {
		super(loggerName);
		store = QuadStore.getDefaultStore();
	}
	
	
	
	/**
	 * Retrieves RDF as JSON for the specified resource
	 * 
	 * @param resourceType Resource class name as defined in IFKM ontology
	 * @param resourceId Relative id of the resource
	 * @return RDF resultset as JSON
	 */

	
	public static Model getWorkflowModel(String workflowId) {
		String sparql = "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"PREFIX dcterm:<http://purl.org/dc/terms/> " + 
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " + 
				"PREFIX workflow:<http://weprov.csiro.au/workflow/> " +
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/>" + 
				"CONSTRUCT " + 
				"{?workflowId a provone:Workflow; " + 
				"?property ?propertyValue. ?propertyValue rdfs:label ?label; foaf:name ?name; a ?propertyValueType. " + 
				"?propertyValue provone:controls ?aProgram. " + 
				"?workflowId provone:hasSubProgram ?program. ?program a ?programType; rdfs:label ?programLabel. "+ 
				"?program provone:hasSubProgram ?subWorkflow. " + 
				"?program provone:controlledBy ?aController. " + 
				"}"+
				"FROM <http://weprov.csiro.au/>  " + 
				"WHERE {?workflowId a provone:Workflow; " + 
				"?property ?propertyValue. "+ 
				"OPTIONAL {?propertyValue rdfs:label ?label; a ?propertyValueType.} "+ 
				"OPTIONAL {?propertyValue foaf:name ?name; a ?propertyValueType.}" + 
				"OPTIONAL {?propertyValue provone:controls ?aProgram.} " + 
				"?workflowId provone:hasSubProgram ?program. ?program a ?programType; rdfs:label ?programLabel. "+ 
				"OPTIONAL {?program provone:hasSubProgram ?subWorkflow.} "+ 
				"OPTIONAL {?program provone:controlledBy ?aController.} "+ 
				"FILTER (?workflowId = ?workflow)" + 
				"{" + 
				"	SELECT ?workflow " + 
				"	WHERE {	" + 
				"	<"+workflowId+">  (provone:hasSubProgram/provone:hasSubProgram)*  ?workflow." + 
				"   }" + 
				" }" + 				
				"}";
		
		//logger.info("Prepared SPARQL query successfully");
		//logger.info(sparql);
		QuadStore store = QuadStore.getDefaultStore();
		return store.execConstruct(sparql);
	}
	
	public Model getWorkflows() {
		
		String sparql = "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"CONSTRUCT {?workflow a provone:Workflow.?workflow provone:hasSubProgram ?subWorkflow.} " + 
				"FROM <http://weprov.csiro.au/> " + 
				"WHERE {?workflow a provone:Workflow. " +
				"?workflow (provone:hasSubProgram/provone:hasSubProgram)+ ?subWorkflow"+
				"}";		
		
		logger.info("Prepared SPARQL query successfully");
		logger.info(sparql);
		QuadStore store = QuadStore.getDefaultStore();
		return store.execConstruct(sparql);
	}
	
	public static ResultSet getRevisionCount(String workflowId) {
		String revisionCount="";
		
		String sparql = "PREFIX prov:<http://www.w3.org/ns/prov#>"+
				"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#>" + 
				"SELECT ?rev "+
				"FROM <http://weprov.csiro.au/evolution/>  " + 
				"WHERE {?revision a prov:Revision; "+
				"		 prov:wasRevisionOf <"+workflowId+">; "+
				"		 weprov:revision ?rev} "+
				"ORDER BY DESC(?rev) "+
				"LIMIT 1";
		
		 //logger.info("Prepared SPARQL query successfully");
		 //logger.info(sparql);
		 QuadStore store = QuadStore.getDefaultStore();
		 ResultSet rs = store.execSelect(sparql);  
		 return store.execSelect(sparql);
	}
	}