package com.csiro.webservices.logic;

import java.util.GregorianCalendar;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.json.JSONException;

import com.csiro.webservices.config.Configuration;


public class RevisionProvenance{

	public static String weprov = Configuration.NS_WEPROV; //"http://www.csiro.au/digiscape/weprov#";
	public static String wedata = Configuration.NS_RES;
	public static String weprovdata = Configuration.NS_EVORES;
	public static String provone = "http://purl.dataone.org/provone/2015/01/15/ontology#";
	public static String prov = "http://www.w3.org/ns/prov#";
	public static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static String foaf = "http://xmlns.com/foaf/0.1/";
	
	public RevisionProvenance() {
		
	}
	
	public Model generateRDF(String entityType, String entityId, String actorId) throws JSONException {
		// A temporary model to add rdf for this JSON
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		
		//Get Classes and Properties of weprov model
		
		  Resource Agent = _model.getResource(prov + "Agent");
		  Resource Activity = _model.getResource(prov+"Activity");
		  
		  Resource Revision = _model.getResource(prov+"Revision");
		
		  Resource Modification = _model.getResource(weprov+"Modification");
		
		  int version= 0; 
		  
		//Property Declaration

			//General Properties
			Property rdfTypeProperty = _model.getProperty(rdf+"type");
				
			// Associations
			
			
			
			Property wasAssociatedWith = _model.getProperty(prov+"wasAssociatedWith");
			
			Property qualifiedRevision = _model.getProperty(prov+"qualifiedRevision");
			
			Property activity = _model.getProperty(prov+"activity");
			
			// Properties
			Property atTime = _model.getProperty(prov+"atTime");
			Property startedAtTime = _model.getProperty(prov+"startedAtTime");
			Property foafname = _model.getProperty(foaf+"name");
			
			
			//Add this detail to the model
			
			Resource entityInstance = _model.getResource(wedata+entityType+"/"+entityId);
		

		
		/**
		 * Revision evolution provenance here
		 **/
		
		Resource _modification = _model.getResource(weprovdata +"modification/"+version+"/"+entityType+"/"+entityId);
		_modification.addProperty(rdfTypeProperty, Modification);
		_modification.addProperty(rdfTypeProperty, Activity);
		_modification.addProperty(startedAtTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		
		Resource _revision = _model.getResource(weprovdata +"revision/"+version+"/"+entityType+"/"+entityId );
		_revision.addProperty(rdfTypeProperty, Revision);
		_revision.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_revision.addProperty(activity, _modification);
		
		entityInstance.addProperty(qualifiedRevision, _revision);

		
		if ( actorId.equals("")) {
			Resource agentInstance = _model.getResource(wedata+"agent/"+actorId);
			agentInstance.addProperty(rdfTypeProperty, Agent);
			agentInstance.addProperty(foafname, _model.createLiteral(actorId));	
			_modification.addProperty(wasAssociatedWith, agentInstance);
		}
		
		return _model;
		
	}
	
	
}


