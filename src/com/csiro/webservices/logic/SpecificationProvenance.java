package com.csiro.webservices.logic;

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

public class SpecificationProvenance{

	public SpecificationProvenance() {
		
	}
	
	public Model generateSpecificationRDF(String json) throws JSONException {
		// A temporary model to add rdf for this JSON
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		
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
				
			// Associations
			
			Property hasSubProgram = _model.getProperty(WeProvOnt.hasSubProgram);
			Property hasInPort = _model.getProperty(WeProvOnt.hasInPort);
			Property hasOutPort = _model.getProperty(WeProvOnt.hasOutPort);
			Property host = _model.getProperty(WeProvOnt.host);
			Property connectsTo = _model.getProperty(WeProvOnt.connectsTo);
			Property hasDefaultParam = _model.getProperty(WeProvOnt.hasDefaultParam);
			Property controlledBy = _model.getProperty(WeProvOnt.controlledBy);
			Property controls = _model.getProperty(WeProvOnt.controls);
			
			Property agent = _model.getProperty(WeProvOnt.agent);
			Property entity = _model.getProperty(WeProvOnt.entity);
			Property activity = _model.getProperty(WeProvOnt.activity);
			
			// Properties
			Property value = _model.getProperty(WeProvOnt.value);
			Property label = _model.getProperty(WeProvOnt.label);
			Property foafname = _model.getProperty(WeProvOnt.foafname);
			
			
			
		//Parse the JSON object
		
		JSONObject obj = new JSONObject(json);
				
		String _workflowId = obj.getString("workflowId");
		
		//Add this detail to the model
		
		Resource workflowInstance = _model.getResource(WeProvData.workflow+_workflowId);
		workflowInstance.addProperty(rdfTypeProperty, Workflow);
		workflowInstance.addProperty(label, _model.createLiteral(_workflowId));	
		
		
		if (obj.has("programs")) {
			
		JSONArray programArr = obj.getJSONArray("programs");
		
		for(int programIdx=0; programIdx<programArr.length() ; programIdx++) {
			JSONObject _program = programArr.getJSONObject(programIdx);
			String _programId = _program.getString("programId");
										
			/** 
			 * Generate RDF for Program details
			 **/
			
			Resource programInstance = _model.getResource(WeProvData.program+_programId); 
					
			//add properties
			programInstance.addProperty(rdfTypeProperty, Program); //declare type of program
			programInstance.addProperty(label, _model.createLiteral(_programId)); // add program id	
			
			//add association
			workflowInstance.addProperty(hasSubProgram, programInstance); // link program with workflow
			
			if (_program.has("modelId")) {
				
				String _modelId = _program.getString("modelId");
				
				/** 
				 * Generate RDF for Model of Program
				 **/
				
				Resource modelInstance = _model.getResource(WeProvData.model+_modelId); 
				
				//add properties
				modelInstance.addProperty(rdfTypeProperty, Model); //declare type of model
				modelInstance.addProperty(label, _model.createLiteral(_modelId)); // add model id	
				
				//add association
				programInstance.addProperty(host, modelInstance); // link model with program
			}
				
			/* Extract Inports detail from JSON */
			
			if(_program.has("inPorts")) {
				
				JSONArray _inportsArr = _program.getJSONArray("inPorts");
			
				for(int _inportIdx=0; _inportIdx < _inportsArr.length(); _inportIdx++) {
					
					JSONObject _inport = _inportsArr.getJSONObject(_inportIdx);
					String _inPortId = _inport.getString("portId");
					
					//Add Inports first
					
					Resource portInstance = _model.getResource(WeProvData.port+_programId+"_"+_inPortId); 
					
					//add properties
					portInstance.addProperty(rdfTypeProperty, Port); //declare type of Port
					portInstance.addProperty(label, _model.createLiteral(_inPortId)); // add port id	
						
					//add associations
					programInstance.addProperty(hasInPort, portInstance);
					
					if(_inport.has("channel")) {
						String inPortChannel = _inport.getString("channel");					
						//Now Add Channel
						Resource channelInstance = _model.getResource(WeProvData.channel+inPortChannel); 
						channelInstance.addProperty(rdfTypeProperty, Channel);
						channelInstance.addProperty(label, _model.createLiteral(inPortChannel));
						
						portInstance.addProperty(connectsTo, channelInstance);
						
					} else if(_inport.has("params")) {
						
						JSONArray _paramArr = _inport.getJSONArray("params");
						
						for(int _paramIdx=0; _paramIdx< _paramArr.length(); _paramIdx++) {
							JSONObject _param = _paramArr.getJSONObject(_paramIdx);
							String _paramId = _param.getString("paramId");
							String _paramValue = _param.getString("paramValue");
							
							Resource paramInstance = _model.getResource(WeProvData.param+_paramId); 
							paramInstance.addProperty(rdfTypeProperty, Entity);
							paramInstance.addProperty(rdfTypeProperty, Param);
							paramInstance.addProperty(label, _model.createLiteral(_paramId));
							paramInstance.addProperty(value, _model.createLiteral(_paramValue));
						
							portInstance.addProperty(hasDefaultParam, paramInstance);
						}	
					}					
				}
			}
			
			if(_program.has("inPorts")) {

				JSONArray _outportsArr = _program.getJSONArray("outPorts");
				
				for(int _outportIdx=0; _outportIdx < _outportsArr.length(); _outportIdx++) {
					
					JSONObject _outport = _outportsArr.getJSONObject(_outportIdx);
					String _outPortId = _outport.getString("portId");
					String _outPortChannel = _outport.getString("channel");
					
					//Add Outports first
					
					Resource portInstance = _model.getResource(WeProvData.port+_programId+"_"+_outPortId); 
					
					//add properties
					portInstance.addProperty(rdfTypeProperty, Port); //declare type of Port
					portInstance.addProperty(label, _model.createLiteral(_outPortId)); // add port id	
						
					//add associations
					programInstance.addProperty(hasOutPort, portInstance);
					
					//Now Add Channel
					Resource channelInstance = _model.getResource(WeProvData.channel+ _outPortChannel); 
					channelInstance.addProperty(rdfTypeProperty, Channel);
					channelInstance.addProperty(label, _model.createLiteral(_outPortChannel));
					
					portInstance.addProperty(connectsTo, channelInstance);
					}
				}
			}
		}
		
		if (obj.has("controllers")) {
		
			JSONArray _controllerArr = obj.getJSONArray("controllers");
			
			for(int _controllerIdx=0; _controllerIdx < _controllerArr.length(); _controllerIdx++) {
				
				JSONObject _controller = _controllerArr.getJSONObject(_controllerIdx);
				
				JSONObject _source = _controller.getJSONObject("source");
				String _sourcePort = _source.getString("port");
				String _sourceProgram = _source.getString("program");
				
				Resource sourceProgramInstance = _model.getResource(WeProvData.program+_sourceProgram); 
				
				JSONObject _target = _controller.getJSONObject("target");
				String _targetPort = _target.getString("port");
				String _targetProgram = _target.getString("program");
				
				Resource targetProgramInstance = _model.getResource(WeProvData.program+_targetProgram); 
				
				Resource controllerInstance = _model.getResource(WeProvData.controller+_sourceProgram+_sourcePort+"_"+_targetProgram+_targetPort);
				
				controllerInstance.addProperty(rdfTypeProperty, Controller);
				controllerInstance.addProperty(label, _model.createLiteral(_sourceProgram+"|"+_sourcePort+"|"+_targetProgram+"|"+_targetPort));
				sourceProgramInstance.addProperty(controlledBy, controllerInstance );
				controllerInstance.addProperty(controls, targetProgramInstance);			
			}
		}
		
		return _model;	
}
	
	
}


