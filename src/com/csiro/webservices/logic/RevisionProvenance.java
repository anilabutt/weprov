package com.csiro.webservices.logic;

import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.json.JSONException;

import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.store.WeProvData;
import com.csiro.webservices.store.WeProvOnt;


public class RevisionProvenance{

	public RevisionProvenance() {
		
	}
	
	public Model generateRevisionRDF(String entityId, String actorId, String version, String partOfRev) throws JSONException {
		// A temporary model to add rdf for this JSON
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		
		//Get Classes and Properties of weprov model
		
		  Resource Agent = _model.getResource(WeProvOnt.Agent);
		  Resource Activity = _model.getResource(WeProvOnt.Activity);		  
		  Resource Revision = _model.getResource(WeProvOnt.Revision);		
		  Resource Modification = _model.getResource(WeProvOnt.Modification);
		
  
		//Property Declaration

			//General Properties
			Property rdfTypeProperty = _model.getProperty(WeProvOnt.rdfType);
				
			// Associations			
			
			Property wasAssociatedWith = _model.getProperty(WeProvOnt.wasAssociatedWith);
			Property hadGeneration = _model.getProperty(WeProvOnt.hadGeneration);
			Property qualifiedRevision = _model.getProperty(WeProvOnt.qualifiedRevision);
			Property activity = _model.getProperty(WeProvOnt.activity);
			
			// Properties
			
			Property atTime = _model.getProperty(WeProvOnt.atTime);
			Property startedAtTime = _model.getProperty(WeProvOnt.startedAtTime);
			Property foafname = _model.getProperty(WeProvOnt.foafname);
			Property versionProperty = _model.getProperty(WeProvOnt.version);
			Property wasPartOf = _model.getProperty(WeProvOnt.wasPartOf);
			
			//Add this detail to the model
			
			Resource entityInstance = _model.getResource(entityId);
		

		
		/**
		 * Revision evolution provenance here
		 **/
		
		Resource _modification = _model.getResource(WeProvData.modification+version+"/"+entityId.replace(WeProvData.wedata, ""));
		_modification.addProperty(rdfTypeProperty, Modification);
		_modification.addProperty(rdfTypeProperty, Activity);
		_modification.addProperty(startedAtTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		
		Resource _revision = _model.getResource(WeProvData.revision+version+"/"+entityId.replace(WeProvData.wedata, "") );
		_revision.addProperty(rdfTypeProperty, Revision);
		_revision.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_revision.addProperty(activity, _modification);
		
		entityInstance.addProperty(qualifiedRevision, _revision);
		entityInstance.addProperty(versionProperty, _model.createTypedLiteral(version));
		
		if ( !actorId.equals("")) {
			Resource agentInstance = _model.getResource(actorId);
			agentInstance.addProperty(rdfTypeProperty, Agent);
			agentInstance.addProperty(foafname, _model.createLiteral(actorId));	
			_modification.addProperty(wasAssociatedWith, agentInstance);
		}
		
		if(!partOfRev.equals("")) {
			Resource _pRevision = _model.getResource(partOfRev);
			_pRevision.addProperty(wasPartOf, _pRevision);
		}
		
		return _model;
		
	}
	
	
}


