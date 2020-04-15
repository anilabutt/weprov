package com.csiro.dataset;

import java.util.ArrayList;
import java.util.List;

import com.csiro.webservices.app.beans.ControllerBean;
import com.csiro.webservices.app.beans.ControllerConnection;
import com.csiro.webservices.app.beans.PortBean;
import com.csiro.webservices.app.beans.ProgramBean;
import com.csiro.webservices.app.beans.Workflow;
import com.csiro.webservices.store.WeProvData;
import com.csiro.webservices.store.WeProvOnt;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

public class WorkflowProvenance{
	
	// A temporary model to add rdf for this JSON
	
	//public Model _model = TDBFactory.createDataset().getDefaultModel();
	//Get Classes and Properties of weprov model
	
	  List<Triple> triples = new ArrayList<Triple>();
	
	  Node Agent = Node.createURI(WeProvOnt.Agent);
	  Node Activity = Node.createURI(WeProvOnt.Activity);
	  Node Entity = Node.createURI(WeProvOnt.Entity);
	  
	  Node Workflow = Node.createURI(WeProvOnt.Workflow);
	  Node Program = Node.createURI(WeProvOnt.Program);
	  Node Model = Node.createURI(WeProvOnt.Model);
	  Node Port = Node.createURI(WeProvOnt.Port);
	  Node Channel = Node.createURI(WeProvOnt.Channel);
	  Node Param = Node.createURI(WeProvOnt.Param);
	  Node Controller = Node.createURI(WeProvOnt.Controller);
	  	
	  
	//Property Declaration

		//General Properties
		Node rdfTypeProperty = Node.createURI(WeProvOnt.rdfType);
		Node title = Node.createURI(WeProvOnt.title);
		Node description = Node.createURI(WeProvOnt.description);
		Node location = Node.createURI(WeProvOnt.location);
			
		// Associations
		
		Node hasSubProgram = Node.createURI(WeProvOnt.hasSubProgram);
		Node hasInPort = Node.createURI(WeProvOnt.hasInPort);
		Node hasOutPort = Node.createURI(WeProvOnt.hasOutPort);
		Node host = Node.createURI(WeProvOnt.host);
		Node connectsTo = Node.createURI(WeProvOnt.connectsTo);
		Node hasDefaultParam = Node.createURI(WeProvOnt.hasDefaultParam);
		Node controlledBy = Node.createURI(WeProvOnt.controlledBy);
		Node controls = Node.createURI(WeProvOnt.controls);
		Node hasController = Node.createURI(WeProvOnt.hasController);
		
		Node agent = Node.createURI(WeProvOnt.agent);
		Node entity = Node.createURI(WeProvOnt.entity);
		Node activity = Node.createURI(WeProvOnt.activity);
		
		// Properties
		Node value = Node.createURI(WeProvOnt.value);
		Node label = Node.createURI(WeProvOnt.label);
		Node foafname = Node.createURI(WeProvOnt.foafname);
		
		Node version= Node.createURI(WeProvOnt.version);
		Node revision = Node.createURI(WeProvOnt.revision);

	public WorkflowProvenance() {
		
	}
	
	public List<Triple> generateSpecificationRDF(Workflow workflow, Node parentProgramInstance) {
		
					
		//Parse the JSON object
						
		String _workflowId = workflow.getWorkflowId();
		String _workflowDesc = workflow.getWorkflowDescription();
		String _workflowTitle = workflow.getWorkflowTitle();
		String _workflowLocation= workflow.getWorkflowLocation();
		String _workflowRevision = workflow.getRevisionId();
		
		
		//Add this detail to the model
		//System.out.println(_workflowDesc);
		Node workflowInstance = Node.createURI(WeProvData.workflow+_workflowId);
		
		triples.add(new Triple(workflowInstance,rdfTypeProperty, Workflow));
		triples.add(new Triple(workflowInstance, description , Node.createLiteral(_workflowDesc)));
		triples.add(new Triple(workflowInstance, title, Node.createLiteral(_workflowTitle)));	
		triples.add(new Triple(workflowInstance, revision, Node.createLiteral(_workflowRevision)));
		
		if( _workflowLocation.equals("")) { 
			
		} else {
			triples.add(new Triple(workflowInstance, location, Node.createLiteral(_workflowLocation)));
		}
						
		if(parentProgramInstance!=null) {
			triples.add(new Triple(parentProgramInstance, hasSubProgram, workflowInstance));
		}
		
		
		if (workflow.getPrograms().size()>0) {
			
			ArrayList<ProgramBean> programArr = workflow.getPrograms();
		
			for(int programIdx=0; programIdx<programArr.size() ; programIdx++) {
			
				ProgramBean program = programArr.get(programIdx);
			
				String _programId = program.getProgramId();
										
				/** 
				 * Generate RDF for Program details
				 **/
			
				Node programInstance = Node.createURI(WeProvData.program+_programId); 
					
				//add properties
				triples.add(new Triple(programInstance, rdfTypeProperty, Program)); //declare type of program
				triples.add(new Triple(programInstance, foafname, Node.createLiteral(_programId))); // add program id	
				triples.add(new Triple(programInstance, rdfTypeProperty, Node.createURI(WeProvOnt.weprov+program.getType()))); 
			
				//add association
				triples.add(new Triple(workflowInstance,hasSubProgram, programInstance)); // link program with workflow
				
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
				
				Node portInstance = Node.createURI(WeProvData.port+ workflow.getWorkflowId() + "/" + _inPortId); 
				
				//add properties
				triples.add(new Triple(portInstance, rdfTypeProperty, Port)); //declare type of Port
				triples.add(new Triple(portInstance, foafname, Node.createLiteral(_inPortId))); // add port id	
					
				//add associations
				triples.add(new Triple(workflowInstance, hasInPort, portInstance));				
			}
		}			
		
		if (workflow.getOutports().size()>0) {

			ArrayList<PortBean> _outportsArr  = workflow.getOutports();
				
			for(int _outportIdx=0; _outportIdx < _outportsArr.size(); _outportIdx++) {
					
				PortBean _outport = _outportsArr.get(_outportIdx);
				String _outPortId = _outport.getPortId();
					
				//Add Outports first
					
				Node portInstance = Node.createURI(WeProvData.port+ workflow.getWorkflowId() +"/"+_outPortId); 
					
				//add properties
				triples.add(new Triple(portInstance, rdfTypeProperty, Port)); //declare type of Port
				triples.add(new Triple(portInstance, label, Node.createLiteral(_outPortId))); // add port id	
						
				//add associations
				triples.add(new Triple(workflowInstance, hasOutPort, portInstance));
				
			}
		}
		
		
		if (workflow.getControllers().size()>0) {
		
			ArrayList<ControllerBean> _controllerArr = workflow.getControllers();
			
			for(int _controllerIdx=0; _controllerIdx < _controllerArr.size(); _controllerIdx++) {
				
				ControllerBean _controller = _controllerArr.get(_controllerIdx);
				
				ControllerConnection _source = _controller.getSource();
				String _sourcePort = _source.getPortId();
				String _sourceProgram = _source.getProgramId();
				
				Node sourceProgramInstance = Node.createURI(WeProvData.program+_sourceProgram); 
				
				ControllerConnection _target = _controller.getTarget();
				String _targetPort = _target.getPortId();
				String _targetProgram = _target.getProgramId();
				
				Node targetProgramInstance = Node.createURI(WeProvData.program+_targetProgram); 
				
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
				
				Node controllerInstance = Node.createURI(WeProvData.controller+link);
				
				triples.add(new Triple(controllerInstance, rdfTypeProperty, Controller));
				triples.add(new Triple(controllerInstance, label, Node.createLiteral(link)));
				triples.add(new Triple(controllerInstance, controlledBy, controllerInstance ));
				triples.add(new Triple(controllerInstance, controls, targetProgramInstance));	
				triples.add(new Triple(controllerInstance, hasController, controllerInstance));
			}
		}		
		return triples;	
	}
}


