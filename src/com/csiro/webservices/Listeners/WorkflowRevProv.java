package com.csiro.webservices.Listeners;

import java.util.GregorianCalendar;
import java.util.HashMap;

import org.json.JSONException;

import com.csiro.webservices.app.beans.ControllerBean;
import com.csiro.webservices.app.beans.ControllerConnection;
import com.csiro.webservices.app.beans.Workflow;
import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.store.WeProvData;
import com.csiro.webservices.store.WeProvOnt;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


public class WorkflowRevProv{

	// A temporary model to add rdf for this JSON
	public Model _model = ModelFactory.createDefaultModel();
	
	//Get Classes and Properties of weprov model
	
	 public Resource Agent = _model.getResource(WeProvOnt.Agent);
	 public Resource Activity = _model.getResource(WeProvOnt.Activity);		  
	 public Resource Revision = _model.getResource(WeProvOnt.Revision);		
	 public Resource Modification = _model.getResource(WeProvOnt.Modification);
	 public Resource Renaming = _model.getResource(WeProvOnt.Renaming);
	  
	 public Resource Generation = _model.getResource(WeProvOnt.Generation);
	 public Resource Invalidation = _model.getResource(WeProvOnt.Invalidation);
	  

	 //Property Declaration

	 //General Properties
	 public Property rdfTypeProperty = _model.getProperty(WeProvOnt.rdfType);
	 // Associations				
	 public Property wasAssociatedWith = _model.getProperty(WeProvOnt.wasAssociatedWith);
	 public Property hadGeneration = _model.getProperty(WeProvOnt.hadGeneration);
	 public Property hadInvalidation = _model.getProperty(WeProvOnt.hadInvalidation);
	 public Property qualifiedRevision = _model.getProperty(WeProvOnt.qualifiedRevision);
		
	 public Property qualifiedGeneration = _model.getProperty(WeProvOnt.qualifiedGeneration);
	 public Property activity = _model.getProperty(WeProvOnt.activity);
	 public Property entity = _model.getProperty(WeProvOnt.entity);
		
	 // Properties
	 public Property atTime = _model.getProperty(WeProvOnt.atTime);
	 public Property startedAtTime = _model.getProperty(WeProvOnt.startedAtTime);
	 public Property foafname = _model.getProperty(WeProvOnt.foafname);
	 public Property versionProperty = _model.getProperty(WeProvOnt.version);
	 public Property wasPartOf = _model.getProperty(WeProvOnt.wasPartOf);
	 public Property wasRevisionOf = _model.getProperty(WeProvOnt.wasRevisionOf);
	 public Property revision = _model.getProperty(WeProvOnt.revision);
		
	 public String revisionId="";
	 
	 public WorkflowRevProv() {				
	 }
	
	/*public Model generateWorkflowRevisionRecords(Workflow cwId, Workflow pwId) {
				
		// A temporary model to add rdf for this JSON		
		Model _model = ModelFactory.createDefaultModel();
				
		Resource cWorkflow = _model.createResource(WeProvData.workflow + cwId.getWorkflowId()+"-"+revisionId);
		Resource pWorkflow = _model.createResource(WeProvData.workflow + pwId.getWorkflowId()+"-"+pwId.getRevisionId());
		cWorkflow.addProperty(wasRevisionOf, pWorkflow);
			
		_model.write(System.out, "TTL");	
		return _model;
	}*/
	
	public Model generateRevisionRDF(Workflow cwId, Workflow pwId, String revId) throws JSONException {
		
		revisionId = revId;
		//Add this detail to the model
		
		Resource entityInstance = _model.getResource(WeProvData.workflow + cwId.getWorkflowId());
	
		
		/**
		 * Revision evolution provenance here
		 **/
		
		Resource _modification = _model.getResource(WeProvData.modification+revisionId+"/"+cwId.getWorkflowId());
		_modification.addProperty(rdfTypeProperty, Modification);
		_modification.addProperty(rdfTypeProperty, Activity);
		_modification.addProperty(startedAtTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		
		Resource _revision = _model.getResource(WeProvData.revision + revisionId+ "/" + cwId.getWorkflowId() );
		_revision.addProperty(wasRevisionOf, entityInstance);
		_revision.addProperty(rdfTypeProperty, Revision);
		_revision.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_revision.addProperty(activity, _modification);
		_revision.addProperty(revision, _model.createLiteral(revisionId));
		
		entityInstance.addProperty(qualifiedRevision, _revision);
		//entityInstance.addProperty(revision, _model.createTypedLiteral(revisionId));
		
		if ( !cwId.getAuthor().equals("")) {
			Resource agentInstance = _model.getResource(WeProvData.agent+cwId.getAuthor().replace(" " ,"_"));
			agentInstance.addProperty(rdfTypeProperty, Agent);
			agentInstance.addProperty(foafname, _model.createLiteral(cwId.getAuthor()));	
			_modification.addProperty(wasAssociatedWith, agentInstance);
		}
		
		
		//Resource _pRevision = _model.getResource(pwId.getRevisionId());
		//_pRevision.addProperty(wasPartOf, _pRevision);

		return _model;
		
	}
	
	public Model generateRenameRecords(Workflow cwId, Workflow pwId, String property) {
		
		// A temporary model to add rdf for this JSON		
		Model _model = ModelFactory.createDefaultModel();
		
		
		Resource _modification = _model.getResource(WeProvData.modification+revisionId+"/"+cwId.getWorkflowId());
		Resource _renaming = _model.getResource(WeProvData.renaming+revisionId+"/"+cwId.getWorkflowId());
		
		_renaming.addProperty(rdfTypeProperty, Renaming);
		_renaming.addProperty(activity, _modification);
		
		if(property.equalsIgnoreCase("id")) {
			Property propertyURI=_model.createProperty(WeProvOnt.workflowId);
			String value = pwId.getWorkflowId();
			_renaming.addProperty(propertyURI, value);
		} if(property.equalsIgnoreCase("title")) {
			Property propertyURI=_model.createProperty(WeProvOnt.title);
			String value = pwId.getWorkflowTitle();
			_renaming.addProperty(propertyURI, value);
		} if(property.equalsIgnoreCase("description")) {
			Property propertyURI=_model.createProperty(WeProvOnt.description);
			String value = pwId.getWorkflowDescription();
			_renaming.addProperty(propertyURI, value);
		} if(property.equalsIgnoreCase("author")) {
			Property propertyURI=_model.createProperty(WeProvOnt.agent);
			String value = pwId.getAuthor();
			_renaming.addProperty(propertyURI, value);
		} /*if(property.equalsIgnoreCase("version")) {
			Property propertyURI=_model.createProperty(WeProvOnt.version);
			String value = pwId.getVersionId();
			_renaming.addProperty(propertyURI, value);
		} */

		return _model;
	}
	
	public Model generatePortInvalidationRecords(Workflow cwId, Workflow pwId, String name) {
		
		// A temporary model to add rdf for this JSON		
		Model _model = ModelFactory.createDefaultModel();
		
		Resource uri = _model.getResource(WeProvData.port+ cwId.getWorkflowId() + "/" + name); 
		
		Resource _modification = _model.getResource(WeProvData.modification+revisionId+"/"+cwId.getWorkflowId());
		Resource _invalidation = _model.getResource(WeProvData.invalidation+ revisionId+"/"+cwId.getWorkflowId() );
		_invalidation.addProperty(rdfTypeProperty, Invalidation);
		_invalidation.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_invalidation.addProperty(activity, _modification);
		_invalidation.addProperty(entity, uri);
		
		Resource _revision = _model.getResource(WeProvData.revision + revisionId+ "/" + cwId.getWorkflowId() );
		_revision.addProperty(hadInvalidation, _invalidation);
		_revision.addProperty(revision, _model.createLiteral(revisionId));
		
		return _model;
	}
	
    public Model generateProgramInvalidationRecords(Workflow cwId, Workflow pwId, String name) {
		
		// A temporary model to add rdf for this JSON		
		Model _model = ModelFactory.createDefaultModel();
		
		Resource uri = _model.getResource(WeProvData.program + name); 
		
		Resource _modification = _model.getResource(WeProvData.modification+revisionId+"/"+cwId.getWorkflowId());
		Resource _invalidation = _model.getResource(WeProvData.invalidation+ revisionId+"/"+cwId.getWorkflowId() );
		_invalidation.addProperty(rdfTypeProperty, Invalidation);
		_invalidation.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_invalidation.addProperty(activity, _modification);
		_invalidation.addProperty(entity, uri);
		
		Resource _revision = _model.getResource(WeProvData.revision + revisionId+ "/" + cwId.getWorkflowId() );
		_revision.addProperty(hadInvalidation, _invalidation);
		_revision.addProperty(revision, _model.createLiteral(revisionId));
		
		return _model;
	}
    
    public Model generateControllerInvalidationRecords(Workflow cwId, Workflow pwId, ControllerBean controller) {
		
		// A temporary model to add rdf for this JSON		
		Model _model = ModelFactory.createDefaultModel();
		 
		ControllerConnection _source = controller.getSource();
		String _sourcePort = _source.getPortId();
		String _sourceProgram = _source.getProgramId();
		
		ControllerConnection _target = controller.getTarget();
		String _targetPort = _target.getPortId();
		String _targetProgram = _target.getProgramId();
		
		String source, sink;
		if(_sourceProgram.equalsIgnoreCase("")) {
			source = _sourcePort;
		} else {
			source = _sourceProgram+"."+_sourcePort;
		}
		
		if(_targetProgram.equalsIgnoreCase("")) {
			sink = _targetPort;
		} else {
			sink = _targetProgram+"."+_targetPort;
		}
		String link = source+":"+sink;
		
		Resource uri = _model.getResource(WeProvData.controller+link);
		
		
		Resource _modification = _model.getResource(WeProvData.modification+revisionId+"/"+cwId.getWorkflowId());
		Resource _invalidation = _model.getResource(WeProvData.invalidation+ revisionId+"/"+cwId.getWorkflowId() );
		_invalidation.addProperty(rdfTypeProperty, Invalidation);
		_invalidation.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_invalidation.addProperty(activity, _modification);
		_invalidation.addProperty(entity, uri);
		
		Resource _revision = _model.getResource(WeProvData.revision + revisionId+ "/" + cwId.getWorkflowId() );
		_revision.addProperty(hadInvalidation, _invalidation);
		_revision.addProperty(revision, _model.createLiteral(revisionId));
		
		return _model;
	}

    public Model generatePortGenerationRecords(Workflow cwId, Workflow pwId, String name) {
		
		// A temporary model to add rdf for this JSON		
		Model _model = ModelFactory.createDefaultModel();
		
		Resource uri = _model.getResource(WeProvData.port+ cwId.getWorkflowId() + "/" + name); 
		
		Resource _modification = _model.getResource(WeProvData.modification+revisionId+"/"+cwId.getWorkflowId());
		Resource _generation = _model.getResource(WeProvData.generation+ revisionId+"/"+cwId.getWorkflowId() );
		_generation.addProperty(rdfTypeProperty, Generation);
		_generation.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_generation.addProperty(activity, _modification);
		uri.addProperty(qualifiedGeneration, _generation);
		
		Resource _revision = _model.getResource(WeProvData.revision + revisionId+ "/" + cwId.getWorkflowId() );
		_revision.addProperty(hadGeneration, _generation);
		_revision.addProperty(revision, _model.createLiteral(revisionId));
		
		return _model;
	}
	
    public Model generateProgramGenerationRecords(Workflow cwId, Workflow pwId, String name) {
		
		// A temporary model to add rdf for this JSON		
		Model _model = ModelFactory.createDefaultModel();
		
		Resource uri = _model.getResource(WeProvData.program + name); 
		
		Resource _modification = _model.getResource(WeProvData.modification+revisionId+"/"+cwId.getWorkflowId());
		Resource _generation = _model.getResource(WeProvData.generation+ revisionId+"/"+cwId.getWorkflowId() );
		_generation.addProperty(rdfTypeProperty, Generation);
		_generation.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_generation.addProperty(activity, _modification);
		uri.addProperty(qualifiedGeneration, _generation);
		
		Resource _revision = _model.getResource(WeProvData.revision + revisionId+ "/" + cwId.getWorkflowId() );
		_revision.addProperty(hadGeneration, _generation);
		_revision.addProperty(revision, _model.createLiteral(revisionId));
		
		return _model;
	}
    
    public Model generateControllerGenerationRecords(Workflow cwId, Workflow pwId, ControllerBean controller) {
		
		// A temporary model to add rdf for this JSON		
		Model _model = ModelFactory.createDefaultModel();
		 
		ControllerConnection _source = controller.getSource();
		String _sourcePort = _source.getPortId();
		String _sourceProgram = _source.getProgramId();
		
		Resource sourceProgramInstance = _model.getResource(WeProvData.program+_sourceProgram); 
		
		ControllerConnection _target = controller.getTarget();
		String _targetPort = _target.getPortId();
		String _targetProgram = _target.getProgramId();
		
		Resource targetProgramInstance = _model.getResource(WeProvData.program+_targetProgram); 
		
		String source, sink;
		if(_sourceProgram.equalsIgnoreCase("")) {
			source = _sourcePort;
		} else {
			source = _sourceProgram+"."+_sourcePort;
		}
		
		if(_targetProgram.equalsIgnoreCase("")) {
			sink = _targetPort;
		} else {
			sink = _targetProgram+"."+_targetPort;
		}
		String link = source+":"+sink;
		
		Resource uri = _model.getResource(WeProvData.controller+link);
		
		
		Resource _modification = _model.getResource(WeProvData.modification+revisionId+"/"+cwId.getWorkflowId());
		Resource _generation = _model.getResource(WeProvData.generation+ revisionId+"/"+cwId.getWorkflowId() );
		_generation.addProperty(rdfTypeProperty, Generation);
		_generation.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_generation.addProperty(activity, _modification);
		uri.addProperty(qualifiedGeneration, _generation);
		
		Resource _revision = _model.getResource(WeProvData.revision + revisionId+ "/" + cwId.getWorkflowId() );
		_revision.addProperty(hadGeneration, _generation);
		_revision.addProperty(revision, _model.createLiteral(revisionId));
		
		return _model;
	}

}


