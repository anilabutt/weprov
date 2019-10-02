package com.csiro.webservices.evolution.listener;

import org.apache.jena.graph.Triple;
import org.apache.jena.graph.compose.Difference;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;

import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.logic.CreationProvenance;
import com.csiro.webservices.logic.RevisionProvenance;
import com.csiro.webservices.rest.GenericService;
//import com.csiro.webservices.rest.Workflow;

public class EvolutionListener extends GenericService {

	public static String wedata = Configuration.NS_RES;
	public static String prov = "http://www.w3.org/ns/prov#";
	
	/**
	 * Default constructor to initializes logging by its parent.
	 */
	public EvolutionListener() {
		super(EvolutionListener.class.getName());
	}
		
	public boolean previousVersionExists(String workflowId) throws JSONException {
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		
		Resource workflowInstance = _model.getResource(workflowId);
	
		return contains(workflowInstance);
	}
	
	public Model creationProv(String entityId, String actorId) {
		
		logger.info("Creating a new workflow / component .... ");
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		CreationProvenance RDFCreator =  new CreationProvenance();
		
		try {
			_model = RDFCreator.generateCreationRDF(entityId, actorId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return _model;
	}
	
	public Model revisionProv(String entityId, String actorId, String version) {
		logger.info("Revising an existing workflow / component .... ");
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		RevisionProvenance RDFCreator =  new RevisionProvenance();
		
		try {
			_model = RDFCreator.generateRDF(entityId, actorId, version);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return _model;		
	}
	
	public Model evolutionActivity(Model _current, Model _previous, String entity, String actor, String version) throws JSONException {
		
		Model model = ModelFactory.createDefaultModel();
		
		Difference newOldDiff = new Difference ( _current.getGraph(), _previous.getGraph());
		Difference oldNewDiff = new Difference ( _previous.getGraph(), _current.getGraph());
		
		if(newOldDiff.isEmpty() && oldNewDiff.isEmpty()) {
			
			logger.info("New Version is SameAs old one ");
		
		} else if(!newOldDiff.isEmpty() && oldNewDiff.isEmpty() ) {
			
			RevisionProvenance revision = new RevisionProvenance();
			
			Model revisionModel = revision.generateRDF(entity, actor, version );
			
			Resource entityId = model.getResource(entity);
			Property qualifiedRevision = model.getProperty(prov+"qualifiedRevision");
			//Property hadGeneration = model.getProperty(prov+"hadGeneration");
			
			Resource revisionId = (Resource)revisionModel.getProperty(entityId, qualifiedRevision).getObject();
			//Resource revisionId = (Resource)revisionModel.getProperty(entityId, qualifiedRevision).getObject();
			
			ProgramListener plistener = new ProgramListener();
			
			Model pProvModel = plistener.addProgramEvolution(newOldDiff, actor , revisionId );
			
			ControllerListener clistener = new ControllerListener();
			
			Model cprovModel = clistener.addControllerEvolution(newOldDiff, actor, revisionId);
			

				
		}
		return model;
		
	}
	

}
