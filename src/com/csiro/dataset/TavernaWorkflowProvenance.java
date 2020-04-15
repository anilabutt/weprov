package com.csiro.dataset;

import java.util.ArrayList;

import com.csiro.webservices.app.beans.ControllerBean;
import com.csiro.webservices.app.beans.ControllerConnection;
import com.csiro.webservices.app.beans.PortBean;
import com.csiro.webservices.app.beans.ProgramBean;
import com.csiro.webservices.app.beans.Workflow;
import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.evolution.listener.WorkflowListener;
import com.csiro.webservices.store.WeProvData;
import com.csiro.webservices.store.WeProvOnt;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Model;

public class TavernaWorkflowProvenance{
	
	// A temporary model to add rdf for this JSON
	
	public Model _model = ModelFactory.createDefaultModel();
	//Get Classes and Properties of weprov model
	
	  Resource Agent = _model.getResource(WeProvOnt.Agent);
	  Resource Activity = _model.getResource(WeProvOnt.Activity);
	  Resource Entity = _model.getResource(WeProvOnt.Entity);
	  
	  Resource Workflow = _model.getResource(WeProvOnt.Workflow);
	  Resource Program = _model.getResource(WeProvOnt.Program);
	  Resource Model = _model.getResource(WeProvOnt.Model);
	  Resource Port = _model.getResource(WeProvOnt.Port);
	  Resource Channel = _model.getResource(WeProvOnt.Channel);
	  Resource Param = _model.getResource(WeProvOnt.Param);
	  Resource Controller = _model.getResource(WeProvOnt.Controller);
	  	
	  
	//Property Declaration

		//General Properties
		Property rdfTypeProperty = _model.getProperty(WeProvOnt.rdfType);
		Property title = _model.getProperty(WeProvOnt.title);
		Property description = _model.getProperty(WeProvOnt.description);
		Property location = _model.getProperty(WeProvOnt.location);
			
		// Associations
		
		Property hasSubProgram = _model.getProperty(WeProvOnt.hasSubProgram);
		Property hasInPort = _model.getProperty(WeProvOnt.hasInPort);
		Property hasOutPort = _model.getProperty(WeProvOnt.hasOutPort);
		Property host = _model.getProperty(WeProvOnt.host);
		Property connectsTo = _model.getProperty(WeProvOnt.connectsTo);
		Property hasDefaultParam = _model.getProperty(WeProvOnt.hasDefaultParam);
		Property controlledBy = _model.getProperty(WeProvOnt.controlledBy);
		Property controls = _model.getProperty(WeProvOnt.controls);
		Property hasController = _model.getProperty(WeProvOnt.hasController);
		
		Property agent = _model.getProperty(WeProvOnt.agent);
		Property entity = _model.getProperty(WeProvOnt.entity);
		Property activity = _model.getProperty(WeProvOnt.activity);
		
		// Properties
		Property value = _model.getProperty(WeProvOnt.value);
		Property label = _model.getProperty(WeProvOnt.label);
		Property foafname = _model.getProperty(WeProvOnt.foafname);
		
		Property version= _model.getProperty(WeProvOnt.version);
		Property revision = _model.getProperty(WeProvOnt.revision);

	public TavernaWorkflowProvenance() {
		
	}
	
	public Model generateSpecificationRDF(Workflow workflow, Resource parentProgramInstance) {
		
					
		//Parse the JSON object
						
		String _workflowId = workflow.getWorkflowId();
		String _workflowDesc = workflow.getWorkflowDescription();
		String _workflowTitle = workflow.getWorkflowTitle();
		String _workflowLocation= workflow.getWorkflowLocation();
		String _workflowRevision = workflow.getRevisionId();
		
		
		//Add this detail to the model
		//System.out.println(_workflowDesc);
		Resource workflowInstance = _model.getResource(WeProvData.workflow+_workflowId);
		
		workflowInstance.addProperty(rdfTypeProperty, Workflow);
		workflowInstance.addProperty(description , _model.createLiteral(_workflowDesc));
		workflowInstance.addProperty(title, _model.createLiteral(_workflowTitle));	
		workflowInstance.addProperty(revision, _workflowRevision);
		
		if( _workflowLocation.equals("")) { 
			
		} else {
			workflowInstance.addProperty(location, _workflowLocation);
		}
						
		if(parentProgramInstance!=null) {
			parentProgramInstance.addProperty(hasSubProgram, workflowInstance);
		}
		
		
		if (workflow.getPrograms().size()>0) {
			
			ArrayList<ProgramBean> programArr = workflow.getPrograms();
		
			for(int programIdx=0; programIdx<programArr.size() ; programIdx++) {
			
				ProgramBean program = programArr.get(programIdx);
			
				String _programId = program.getProgramId();
										
				/** 
				 * Generate RDF for Program details
				 **/
			
				Resource programInstance = _model.getResource(WeProvData.program+_programId); 
					
				//add properties
				programInstance.addProperty(rdfTypeProperty, Program); //declare type of program
				programInstance.addProperty(foafname, _model.createLiteral(_programId)); // add program id	
				programInstance.addProperty(rdfTypeProperty, _model.getResource(WeProvOnt.weprov+program.getType())); 
			
				//add association
				workflowInstance.addProperty(hasSubProgram, programInstance); // link program with workflow
				
				if(program.getType().equalsIgnoreCase("workflow")) {
					generateSpecificationRDF(program.getWorkflow(), programInstance);
				}
			}
		} 
		
		/* Extract Inports detail from WorkflowObject */
		
		if (workflow.getInports().size()>0) {
			
			ArrayList<PortBean> _inportsArr  = workflow.getInports();
		
			for(int _inportIdx=0; _inportIdx < _inportsArr.size(); _inportIdx++) {
				
				PortBean _inport = _inportsArr.get(_inportIdx);
				String _inPortId = _inport.getPortId();
				
				//Add Inports first
				
				Resource portInstance = _model.getResource(WeProvData.port+ workflow.getWorkflowId() + "/" + _inPortId); 
				
				//add properties
				portInstance.addProperty(rdfTypeProperty, Port); //declare type of Port
				portInstance.addProperty(foafname, _model.createLiteral(_inPortId)); // add port id	
					
				//add associations
				workflowInstance.addProperty(hasInPort, portInstance);				
			}
		}			
		
		if (workflow.getOutports().size()>0) {

			ArrayList<PortBean> _outportsArr  = workflow.getOutports();
				
			for(int _outportIdx=0; _outportIdx < _outportsArr.size(); _outportIdx++) {
					
				PortBean _outport = _outportsArr.get(_outportIdx);
				String _outPortId = _outport.getPortId();
					
				//Add Outports first
					
				Resource portInstance = _model.getResource(WeProvData.port+ workflow.getWorkflowId() +"/"+_outPortId); 
					
				//add properties
				portInstance.addProperty(rdfTypeProperty, Port); //declare type of Port
				portInstance.addProperty(label, _model.createLiteral(_outPortId)); // add port id	
						
				//add associations
				workflowInstance.addProperty(hasOutPort, portInstance);	
				//workflowInstance.addProperty(hasOutPort, portInstance);
				
			}
		}
		
		
		if (workflow.getControllers().size()>0) {
		
			ArrayList<ControllerBean> _controllerArr = workflow.getControllers();
			
			for(int _controllerIdx=0; _controllerIdx < _controllerArr.size(); _controllerIdx++) {
				
				ControllerBean _controller = _controllerArr.get(_controllerIdx);
				
				ControllerConnection _source = _controller.getSource();
				String _sourcePort = _source.getPortId();
				String _sourceProgram = _source.getProgramId();
				
				Resource sourceProgramInstance = _model.getResource(WeProvData.program+_sourceProgram); 
				
				ControllerConnection _target = _controller.getTarget();
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
				
				Resource controllerInstance = _model.getResource(WeProvData.controller+link);
				
				controllerInstance.addProperty(rdfTypeProperty, Controller);
				controllerInstance.addProperty(label, _model.createLiteral(link));
				sourceProgramInstance.addProperty(controlledBy, controllerInstance );
				controllerInstance.addProperty(controls, targetProgramInstance);	
				workflowInstance.addProperty(hasController, controllerInstance);
			}
		}		
		return _model;	
	}
}


