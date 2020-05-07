package com.csiro.webservices.mappings;
import java.util.ArrayList;

import org.json.JSONException;
import org.w3c.dom.Node;

import com.csiro.webservices.app.beans.*;
import com.csiro.webservices.store.WeProvData;
import com.csiro.webservices.store.WeProvOnt;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RdfToWorkflow {

	private Workflow workflow = new Workflow();
	private Model model = ModelFactory.createDefaultModel();
	
	//Get Classes and Properties of weprov model
	
	  Resource Agent = model.getResource(WeProvOnt.Agent);
	  Resource Activity = model.getResource(WeProvOnt.Activity);
	  Resource Entity = model.getResource(WeProvOnt.Entity);
	  
	  Resource Workflow = model.getResource(WeProvOnt.Workflow);
	  Resource Program = model.getResource(WeProvOnt.Program);
	  Resource Model = model.getResource(WeProvOnt.Model);
	  Resource Port = model.getResource(WeProvOnt.Port);
	  Resource Channel = model.getResource(WeProvOnt.Channel);
	  Resource Param = model.getResource(WeProvOnt.Param);
	  Resource Controller = model.getResource(WeProvOnt.Controller);
	  	
	  
	//Property Declaration

		//General Properties
		Property rdfTypeProperty = model.getProperty(WeProvOnt.rdfType);
		Property title = model.getProperty(WeProvOnt.title);
		Property description = model.getProperty(WeProvOnt.description);
		Property location = model.getProperty(WeProvOnt.location);
			
		// Associations
		
		Property hasSubProgram = model.getProperty(WeProvOnt.hasSubProgram);
		Property hasInPort = model.getProperty(WeProvOnt.hasInPort);
		Property hasOutPort = model.getProperty(WeProvOnt.hasOutPort);
		Property host = model.getProperty(WeProvOnt.host);
		Property connectsTo = model.getProperty(WeProvOnt.connectsTo);
		Property hasDefaultParam = model.getProperty(WeProvOnt.hasDefaultParam);
		Property controlledBy = model.getProperty(WeProvOnt.controlledBy);
		Property controls = model.getProperty(WeProvOnt.controls);
		Property hasController = model.getProperty(WeProvOnt.hasController);
		
		Property agent = model.getProperty(WeProvOnt.agent);
		Property entity = model.getProperty(WeProvOnt.entity);
		Property activity = model.getProperty(WeProvOnt.activity);
		
		// Properties
		Property value = model.getProperty(WeProvOnt.value);
		Property label = model.getProperty(WeProvOnt.label);
		Property foafname = model.getProperty(WeProvOnt.foafname);
		
		Property version= model.getProperty(WeProvOnt.version);
		Property revision = model.getProperty(WeProvOnt.revision);
		
	
	public Workflow getWorkflowfromRdf(Model _model, String baseWorkflow) {
		
		System.out.println("Inside getWorkflowfromRdf ... ");
		model.add(_model);
		//model.write(System.out, "TTL");
		workflow = getComponentsOfThisWorkflow(baseWorkflow);
		
		return workflow;
	}
	
	
	private Workflow getComponentsOfThisWorkflow (String baseWorkflow) {
	    
		Workflow workflow = new Workflow();
		
		
		//Parse the Jena model
		
				String _workflowId = baseWorkflow;
				String _workflowDesc = "";
				String _workflowTitle = "";
				String _workflowRevision = "";
				String _workflowAuthor = "";
				
				Resource workflowInstance = model.getResource(_workflowId);

				String[] idtokens = _workflowId.split("\\/");
				String id = idtokens[idtokens.length-1];
				//System.out.println(" WorkflowId " + id);
				
				workflow.setWorkflowId(id);
				
				NodeIterator descIter = model.listObjectsOfProperty(workflowInstance, description);				
				while (descIter.hasNext()) {
					_workflowDesc = descIter.nextNode().toString(); }				
				workflow.setWorkflowDescription(_workflowDesc);
				
				NodeIterator titleIter = model.listObjectsOfProperty(workflowInstance, title);				
				while (titleIter.hasNext()) {
					_workflowTitle = titleIter.nextNode().toString(); }				
				workflow.setWorkflowTitle(_workflowTitle);
				
				NodeIterator revisionIter = model.listObjectsOfProperty(workflowInstance, revision);				
				while (revisionIter.hasNext()) {
					_workflowRevision = revisionIter.nextNode().toString(); }				
				workflow.setRevisionId(_workflowRevision);
				
				NodeIterator authorIter = model.listObjectsOfProperty(workflowInstance, agent);		
				while(authorIter.hasNext()) {
					Resource authorURI = (Resource) authorIter.nextNode();
					NodeIterator labelIterator = model.listObjectsOfProperty(authorURI, foafname);
					_workflowAuthor = labelIterator.nextNode().toString();
					workflow.setAuthor(_workflowAuthor);
				}

				//Add inports
					
				ArrayList<PortBean> _inportsArr  = new ArrayList<PortBean>();
				
				NodeIterator inportIterator = model.listObjectsOfProperty(workflowInstance, hasInPort);
				
				while (inportIterator.hasNext()) {
					Resource inport = (Resource) inportIterator.nextNode();
					NodeIterator labelIterator = model.listObjectsOfProperty(inport, label);
					PortBean inportBean = new PortBean();
					inportBean.setPortId(labelIterator.nextNode().toString());
					//System.out.println(inportLabel);	
					_inportsArr.add(inportBean);
				}
				
				workflow.setInports(_inportsArr);
				
				
				//Add outports
				
				ArrayList<PortBean> _outportsArr  = new ArrayList<PortBean>();
				
				NodeIterator outportIterator = model.listObjectsOfProperty(workflowInstance, hasOutPort);
				
				while (outportIterator.hasNext()) {
					Resource outport = (Resource) outportIterator.nextNode();
					NodeIterator labelIterator = model.listObjectsOfProperty(outport, label);
					PortBean outportBean = new PortBean();
					outportBean.setPortId(labelIterator.nextNode().toString());
					_outportsArr.add(outportBean);
				}
				
				workflow.setOutports(_outportsArr);
				
				//add controllers
				ArrayList<ControllerBean> _controllerArr = workflow.getControllers();
				
				NodeIterator controllerIterator = model.listObjectsOfProperty(workflowInstance, hasController);
				
				while (controllerIterator.hasNext()) {
					String controllerURI = controllerIterator.nextNode().toString();
					String[] controllerLabels = controllerURI.split("\\/");
					String label = controllerLabels[controllerLabels.length-1];
					
					String[] controllertokens = label.split(":");
					String source = controllertokens[0];
					String sink =  controllertokens[1];
					
					ControllerBean controller = new ControllerBean();
	    			ControllerConnection linkSource = new ControllerConnection();	
	    			ControllerConnection linkSink = new ControllerConnection();	
					
	    			if(source.contains(".")) {
	    				String process = source.split("\\.")[0];
	    				String output = source.split("\\.")[1];	   				
	    				    				
	    				linkSource.setProgramId(process);
	    				linkSource.setPortId(output);	    				
	    				//System.out.println( process + "	---> " + output);
	    			} else {
	    				linkSource.setProgramId("");
	    				linkSource.setPortId(source);
	    				//System.out.println( "	---> " + source);
	    			} 
	    			 
	    			if(sink.contains(".")) {
		    			String process = sink.split("\\.")[0];
		    			String input = sink.split("\\.")[1];		    				
		    					    				
		    			linkSink.setProgramId(process);
		    			linkSink.setPortId(input);
		    			//System.out.println( process + "	---> " + input);
		    		} else {
	    				linkSink.setProgramId("");
	    				linkSink.setPortId(sink);
		    			//System.out.println( "	---> " + sink);
		    		}
	    			controller.setSource(linkSource);
	    			controller.setTarget(linkSink);	    			
	    			_controllerArr.add(controller);
				}
				workflow.setControllers(_controllerArr);
				
				//addPrograms 
				
				ArrayList<ProgramBean> _programArray= new ArrayList<ProgramBean>();
				
				NodeIterator programIterator = model.listObjectsOfProperty(workflowInstance, hasSubProgram);
				
				while (programIterator.hasNext()) {
					
					ProgramBean program  = new ProgramBean();
					Resource programURI = (Resource) programIterator.nextNode();
					NodeIterator labelIterator = model.listObjectsOfProperty(programURI, label);
					String programId = labelIterator.nextNode().toString();
					program.setProgramId(programId);
					//System.out.println(programId);
					
					if(model.contains(programURI, hasSubProgram) ){						
						NodeIterator subWorkflowIter = model.listObjectsOfProperty(programURI, hasSubProgram);
						while(subWorkflowIter.hasNext()) {	
							String workflowURI = subWorkflowIter.nextNode().toString();
							Workflow subWorkflow = getComponentsOfThisWorkflow(workflowURI);
							program.setType("workflow");
							program.setWorkflow(subWorkflow);
						}
					}
					_programArray.add(program);					
				}
				workflow.setPrograms(_programArray);
				
		return workflow;
	}
	
	
	
	
	
}
