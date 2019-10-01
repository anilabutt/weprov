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


public class CreationProvenance{

	public static String weprov = Configuration.NS_WEPROV; //"http://www.csiro.au/digiscape/weprov#";
	public static String wedata = Configuration.NS_RES;
	public static String weprovdata = Configuration.NS_EVORES;
	public static String provone = "http://purl.dataone.org/provone/2015/01/15/ontology#";
	public static String prov = "http://www.w3.org/ns/prov#";
	public static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static String foaf = "http://xmlns.com/foaf/0.1/";
	
	public CreationProvenance() {
		
	}
	
	public Model generateEvolProvenance(String entityType, String entityId, String actorId) throws JSONException {
		// A temporary model to add rdf for this JSON
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		
		//Get Classes and Properties of weprov model
		
		  Resource Agent = _model.getResource(prov + "Agent");
		  Resource Activity = _model.getResource(prov+"Activity");		  
		  Resource Generation = _model.getResource(prov+"Generation");		
		  Resource Creation = _model.getResource(weprov+"Creation");		  
		  
		//Property Declaration

			//General Properties
			Property rdfTypeProperty = _model.getProperty(rdf+"type");
				
			// Associations		
			Property wasAssociatedWith = _model.getProperty(prov+"wasAssociatedWith");
			Property wasGeneratedBy = _model.getProperty(prov+"wasGeneratedBy");						
			Property qualifiedGeneration = _model.getProperty(prov+"qualifiedGeneration");
			
			
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
			Property foafname = _model.getProperty(foaf+"name");
			

		
		//Add this detail to the model
		
		Resource entityInstance = _model.getResource(wedata+entityType+"/"+entityId);
		
		
		/**
		 * Generate evolution provenance here
		 **/
		
		Resource _creation = _model.getResource(weprovdata +"creation/"+ entityType+"/"+entityId);
		_creation.addProperty(rdfTypeProperty, Creation);
		_creation.addProperty(rdfTypeProperty, Activity);
		_creation.addProperty(startedAtTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		
		Resource _generation = _model.getResource(weprovdata +"generation/"+ entityType+"/"+entityId );
		_generation.addProperty(rdfTypeProperty, Generation);
		_generation.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_generation.addProperty(activity, _creation);
		
		entityInstance.addProperty(wasGeneratedBy, _creation);
		entityInstance.addProperty(qualifiedGeneration, _generation);
				
		if ( actorId.equals("")) {
			Resource agentInstance = _model.getResource(wedata+"agent/"+actorId);
			agentInstance.addProperty(rdfTypeProperty, Agent);
			agentInstance.addProperty(foafname, _model.createLiteral(actorId));	
			_creation.addProperty(wasAssociatedWith, agentInstance);
		}
		

		return _model;
		
	}
	
	
}


