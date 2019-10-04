package com.csiro.webservices.evolution.listener;

import java.util.HashMap;

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
import com.csiro.webservices.store.WeProvOnt;
//import com.csiro.webservices.rest.Workflow;

public class WorkflowListener extends GenericService {

	public ProgramListener plistener = new ProgramListener();
	public ControllerListener clistener = new ControllerListener();
	
	
	/**
	 * Default constructor to initializes logging by its parent.
	 */
	public WorkflowListener() {
		super(WorkflowListener.class.getName());
	}
		
	public boolean previousVersionExists(String workflowId) throws JSONException {
		
		Model _model = ModelFactory.createDefaultModel();
		
		Resource workflowInstance = _model.getResource(workflowId);
	
		return contains(workflowInstance);
	}
		
	public Model getWorkflowCreationProvenance(String entityId, String actorId, Model model) {
		
		logger.info("Creating a new workflow / component .... ");
		
		Model wModel = ModelFactory.createDefaultModel();
		CreationProvenance creationProvenance =  new CreationProvenance();
		HashMap<String, String> partOfRel = new HashMap<String, String>();
		
		try {
			//Generate provenance of workflow creation
			wModel = creationProvenance.generateCreationRDF(entityId, actorId, partOfRel);		
			logger.info(" Workflow Creation Provenance Model Size .. " + wModel.size());
			
			//Generate provenance of programs (of this workflow) creation			
			Model pModel = plistener.getProgramCreationProvenance(model, actorId, entityId);
			logger.info(" Program Creation Provenance Model Size .. " + pModel.size());
			
			//Generate provenance of programs (of this workflow) creation			
			Model cModel = clistener.getControllerCreationProvenance(model, actorId, entityId);
			logger.info(" Contoller Creation Provenance Model Size .. " + pModel.size());


			wModel.add(pModel);
			wModel.add(cModel);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return wModel;
	}
	
	public Model revisionProv(String entityId, String actorId, String version) {
		logger.info("Revising an existing workflow / component .... ");
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		RevisionProvenance RDFCreator =  new RevisionProvenance();
		
		try {
			_model = RDFCreator.generateRevisionRDF(entityId, actorId, version, null);
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
			
			Model revisionModel = revision.generateRevisionRDF(entity, actor, version,null );
			
			Model pProvModel = plistener.getProgramCreationProvenance(newOldDiff, actor , entity );
			
			ControllerListener clistener = new ControllerListener();
			
			Model cprovModel = clistener.getControllerCreationProvenance(newOldDiff, actor, entity);
			
			revisionModel.add(pProvModel);
			revisionModel.add(cprovModel);
			
			StmtIterator iter = revisionModel.listStatements();
			
			while (iter.hasNext() ) {
				Statement t = iter.next();
				System.out.println(t.getSubject() + "\t" +t.getPredicate()+"\t" +t.getObject());
			}
				
		}
		return model;
		
	}
	

}
