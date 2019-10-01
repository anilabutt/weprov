package com.csiro.webservices.logic;

import java.util.GregorianCalendar;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csiro.webservices.config.Configuration;


public class WorkflowRevisionProvenance{

	public static String weprov = Configuration.NS_WEPROV; //"http://www.csiro.au/digiscape/weprov#";
	public static String wedata = Configuration.NS_RES;
	public static String weprovdata = Configuration.NS_EVORES;
	public static String provone = "http://purl.dataone.org/provone/2015/01/15/ontology#";
	public static String prov = "http://www.w3.org/ns/prov#";
	public static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static String foaf = "http://xmlns.com/foaf/0.1/";
	
	public WorkflowRevisionProvenance() {
		
	}
	
	public Model generateRDF(String json) throws JSONException {
		// A temporary model to add rdf for this JSON
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		
		//Get Classes and Properties of weprov model
		
		  Resource Agent = _model.getResource(prov + "Agent");
		  Resource Activity = _model.getResource(prov+"Activity");
		  
		  Resource Invalidation = _model.getResource(prov+"Invalidation");
		  Resource Usage = _model.getResource(prov+"Usage");
		  Resource Generation = _model.getResource(prov+"Generation");
		
		  Resource Derivation = _model.getResource(prov+"Derivation");
		  Resource Revision = _model.getResource(prov+"Revision");
		
		  Resource Creation = _model.getResource(weprov+"Creation");
		  Resource Deletion = _model.getResource(weprov+"Deletion");
		  Resource Modification = _model.getResource(weprov+"Modification");
		  Resource Renaming = _model.getResource(weprov+"Renaming");
		
		  
		//Property Declaration

			//General Properties
			Property rdfTypeProperty = _model.getProperty(rdf+"type");
				
			// Associations
			
			
			
			Property wasAssociatedWith = _model.getProperty(prov+"wasAssociatedWith");
			Property wasGeneratedBy = _model.getProperty(prov+"wasGeneratedBy");
			Property wasInvalidatedBy = _model.getProperty(prov+"wasInvalidatedBy");
			Property wasDerivationOf = _model.getProperty(prov+"wasDerivationOf");
			Property wasRevisionOf = _model.getProperty(prov+"wasRevisionOf");
			
			Property qualifiedGeneration = _model.getProperty(prov+"qualifiedGeneration");
			Property qualifiedInvalidation = _model.getProperty(prov+"qualifiedInvalidation");
			Property qualifiedDerivation = _model.getProperty(prov+"qualifiedDerivation");
			Property qualifiedUsage = _model.getProperty(prov+"qualifiedUsage");
			Property qualifiedRevision = _model.getProperty(prov+"qualifiedRevision");
			
			Property hadGeneration = _model.getProperty(prov+"hadGeneration");
			Property hadUsage = _model.getProperty(prov+"hadUsage");
			Property hadActivity = _model.getProperty(prov+"hadActivity");
			Property hadInvalidation = _model.getProperty(prov+"hadInvalidation");
			
			Property agent = _model.getProperty(prov+"agent");
			Property entity = _model.getProperty(prov+"entity");
			Property activity = _model.getProperty(prov+"activity");
			
			// Properties
			Property atTime = _model.getProperty(prov+"atTime");
			Property startedAtTime = _model.getProperty(prov+"startedAtTime");
			Property endedAtTime = _model.getProperty(prov+"endedAtTime");
			Property value = _model.getProperty(prov+"value");
			Property label = _model.getProperty(rdfs+"label");
			Property foafname = _model.getProperty(foaf+"name");
			
		//Parse the JSON object
		
		JSONObject obj = new JSONObject(json);
				
		String _workflowId = obj.getString("workflowId");
		String _userId = obj.getString("userId");
		String _time = obj.getString("time");

		
		//Add this detail to the model
		
		Resource workflowInstance = _model.getResource(wedata+"workflow/"+_workflowId);
		
		Resource agentInstance = _model.getResource(wedata+"agent/"+_userId);
		agentInstance.addProperty(rdfTypeProperty, Agent);
		agentInstance.addProperty(foafname, _model.createLiteral(_userId));		

		
		
		/**
		 * Generate evolution provenance here
		 **/
		
		Resource _creation = _model.getResource(weprovdata +"creation/"+ _workflowId);
		_creation.addProperty(rdfTypeProperty, Creation);
		_creation.addProperty(startedAtTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_creation.addProperty(wasAssociatedWith, agentInstance);
		
		Resource _generation = _model.getResource(weprovdata +"generation/"+ _workflowId );
		_generation.addProperty(rdfTypeProperty, Generation);
		_generation.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_generation.addProperty(activity, _creation);
		
		workflowInstance.addProperty(wasGeneratedBy, _creation);
		workflowInstance.addProperty(qualifiedGeneration, _generation);
		
//				
//		System.out.println(_model.size()+"");
//		String query = "SELECT * WHERE {?subject ?predicate ?object}";
//		QueryExecution qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11, _model);
//		ResultSet results = qexec.execSelect();
//		ResultSetFormatter.out(System.out, results);
		return _model;
		
	}
	
	
}


