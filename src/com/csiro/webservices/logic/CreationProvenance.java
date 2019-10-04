package com.csiro.webservices.logic;

import java.util.GregorianCalendar;
import java.util.HashMap;

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
import com.csiro.webservices.store.WeProvData;
import com.csiro.webservices.store.WeProvOnt;


public class CreationProvenance{

	public CreationProvenance() {
		
	}
	
	public Model generateCreationRDF(String entityId, String actorId, HashMap<String, String> partOfRel) throws JSONException {
		
		// A temporary model to add rdf for this JSON

		Model _model = TDBFactory.createDataset().getDefaultModel();
				
		//Get Classes and Properties of weprov model
		
		  Resource Agent = _model.getResource(WeProvOnt.Agent);
		  Resource Activity = _model.getResource(WeProvOnt.Activity);		  
		  Resource Generation = _model.getResource(WeProvOnt.Generation);		
		  Resource Creation = _model.getResource(WeProvOnt.Creation);		  
		  
		//Property Declaration

			//General Properties
			Property rdfTypeProperty = _model.getProperty(WeProvOnt.rdfType);
				
			// Associations		
			Property wasAssociatedWith = _model.getProperty(WeProvOnt.wasAssociatedWith);
			Property wasGeneratedBy = _model.getProperty(WeProvOnt.wasGeneratedBy);						
			Property qualifiedGeneration = _model.getProperty(WeProvOnt.qualifiedGeneration);
			Property wasPartOf = _model.getProperty(WeProvOnt.wasPartOf);
			
			
			Property hadGeneration = _model.getProperty(WeProvOnt.hadGeneration);
			
			Property agent = _model.getProperty(WeProvOnt.agent);
			Property entity = _model.getProperty(WeProvOnt.entity);
			Property activity = _model.getProperty(WeProvOnt.activity);
			
			// Properties
			Property atTime = _model.getProperty(WeProvOnt.atTime);
			Property startedAtTime = _model.getProperty(WeProvOnt.startedAtTime);
			Property endedAtTime = _model.getProperty(WeProvOnt.endedAtTime);
			Property foafname = _model.getProperty(WeProvOnt.foafname);
			Property versionProperty = _model.getProperty(WeProvOnt.version);
			

		
		//Add this detail to the model
		
		Resource entityInstance = _model.getResource(entityId);
		
		
		/**
		 * Generate evolution provenance here
		 **/
		
		Resource _creation = _model.getResource(WeProvData.creation+ entityId.replace(WeProvData.wedata, ""));
		_creation.addProperty(rdfTypeProperty, Creation);
		_creation.addProperty(rdfTypeProperty, Activity);
		_creation.addProperty(startedAtTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		
		Resource _generation = _model.getResource(WeProvData.generation+ entityId.replace(WeProvData.wedata, "") );
		_generation.addProperty(rdfTypeProperty, Generation);
		_generation.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_generation.addProperty(activity, _creation);
		
		entityInstance.addProperty(wasGeneratedBy, _creation);
		entityInstance.addProperty(qualifiedGeneration, _generation);
		entityInstance.addProperty(versionProperty, _model.createTypedLiteral(1));
				
		if ( !actorId.equals("")) {
			Resource agentInstance = _model.getResource(actorId);
			agentInstance.addProperty(rdfTypeProperty, Agent);
			agentInstance.addProperty(foafname, _model.createLiteral(actorId));	
			_creation.addProperty(wasAssociatedWith, agentInstance);
		}
		
		if(partOfRel.containsKey("revisionId")) {
			String id = partOfRel.get("revisionId");
			Resource _revision = _model.getResource(WeProvData.revision+ id.replace(WeProvData.wedata, ""));
			_revision.addProperty(hadGeneration, _generation);
		}
		
		if (partOfRel.containsKey("generationId")) {
			String id = partOfRel.get("generationId");
			Resource _parentGeneration = _model.getResource(WeProvData.generation+ id.replace(WeProvData.wedata, ""));
			_generation.addProperty(wasPartOf, _parentGeneration);
		}
		
		
		return _model;
		
	}
	
	
}


