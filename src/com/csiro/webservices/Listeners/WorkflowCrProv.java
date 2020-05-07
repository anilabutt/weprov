package com.csiro.webservices.Listeners;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.json.JSONException;

import com.csiro.webservices.store.WeProvData;
import com.csiro.webservices.store.WeProvOnt;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


public class WorkflowCrProv{

	public WorkflowCrProv() {
		
	}
	
	public Model generateCreationRDF(String entityId, String actorId, ArrayList<String> partOfRel) throws JSONException {
		
		// A temporary model to add rdf for this JSON

		
		//System.out.println("entityId : " + entityId);
		Model _model = ModelFactory.createDefaultModel();
				
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
			Property generated = _model.getProperty(WeProvOnt.generated);
			
			// Properties
			Property atTime = _model.getProperty(WeProvOnt.atTime);
			Property startedAtTime = _model.getProperty(WeProvOnt.startedAtTime);
			Property endedAtTime = _model.getProperty(WeProvOnt.endedAtTime);
			Property foafname = _model.getProperty(WeProvOnt.foafname);
			//Property revision = _model.getProperty(WeProvOnt.revision);
			

		
		//Add this detail to the model
		
		Resource entityInstance = _model.getResource(WeProvData.workflow + entityId);
		
		
		/**
		 * Generate evolution provenance here
		 **/
		
		Resource _creation = _model.getResource(WeProvData.creation+entityId);
		_creation.addProperty(rdfTypeProperty, Creation);
		_creation.addProperty(rdfTypeProperty, Activity);
		_creation.addProperty(startedAtTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		
		Resource _generation = _model.getResource(WeProvData.generation+ entityId);
		_generation.addProperty(rdfTypeProperty, Generation);
		_generation.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_generation.addProperty(activity, _creation);
		_generation.addProperty(generated, entityInstance);
		
		entityInstance.addProperty(wasGeneratedBy, _creation);
		entityInstance.addProperty(qualifiedGeneration, _generation);
		//entityInstance.addProperty(revision, _model.createTypedLiteral(revisionId));
				
		if ( actorId.equalsIgnoreCase("")) {
			
		} else {
			Resource agentInstance = _model.getResource(WeProvData.agent+actorId.replace(" ", "_"));
			agentInstance.addProperty(rdfTypeProperty, Agent);
			agentInstance.addProperty(foafname, _model.createLiteral(actorId));	
			_creation.addProperty(wasAssociatedWith, agentInstance);
		}
		
		/*if(partOfRel.containsKey("revisionId")) {
			String id = partOfRel.get("revisionId");
			Resource _revision = _model.getResource(id);
			_revision.addProperty(hadGeneration, _generation);
		}*/
		
		for (int i= 0; i< partOfRel.size(); i++) {
			String id = partOfRel.get(i);
			//System.out.println("id : " +  id);
			Resource _parentGeneration = _model.getResource(WeProvData.generation+ id);
			_parentGeneration.addProperty(wasPartOf, _generation);
		}
		
		
		return _model;
		
	}
	
	
}


