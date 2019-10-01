package com.csiro.webservices.logic;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csiro.webservices.config.Configuration;

public class WorkflowSpecificationProvenance{

	public static String weprov = Configuration.NS_WEPROV; //"http://www.csiro.au/digiscape/weprov#";
	public static String wedata = Configuration.NS_RES;
	public static String weprovdata = Configuration.NS_EVORES;
	public static String provone = "http://purl.dataone.org/provone/2015/01/15/ontology#";
	public static String prov = "http://www.w3.org/ns/prov#";
	public static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static String foaf = "http://xmlns.com/foaf/0.1/";
	
	public WorkflowSpecificationProvenance() {
		
	}
	
	public Model generateSpecificationRDF(String json) throws JSONException {
		// A temporary model to add rdf for this JSON
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		
		//Get Classes and Properties of weprov model
		
		  Resource Agent = _model.getResource(prov + "Agent");
		  Resource Activity = _model.getResource(prov+"Activity");
		  Resource Entity = _model.getResource(prov+"Entity");
		  
		  Resource Workflow = _model.getResource(provone+"Workflow");
		  Resource Program = _model.getResource(provone+"Program");
		  Resource Model = _model.getResource(weprov+"Model");
		  Resource Port = _model.getResource(provone+"Port");
		  Resource Channel = _model.getResource(provone+"Channel");
		  Resource Controller = _model.getResource(provone+"Controller");
		  	
		  
		//Property Declaration

			//General Properties
			Property rdfTypeProperty = _model.getProperty(rdf+"type");
				
			// Associations
			
			Property hasSubProgram = _model.getProperty(provone+"hasSubProgram");
			Property hasInPort = _model.getProperty(provone+"hasInPort");
			Property hasOutPort = _model.getProperty(provone+"hasOutPort");
			Property host = _model.getProperty(weprov+"host");
			Property connectsTo = _model.getProperty(provone+"connectsTo");
			Property hasDefaultParam = _model.getProperty(provone+"hasDefaultParam");
			Property controlledBy = _model.getProperty(provone+"controlledBy");
			Property controls = _model.getProperty(provone+"controls");
			
			Property agent = _model.getProperty(prov+"agent");
			Property entity = _model.getProperty(prov+"entity");
			Property activity = _model.getProperty(prov+"activity");
			
			// Properties
			Property value = _model.getProperty(prov+"value");
			Property label = _model.getProperty(rdfs+"label");
			Property foafname = _model.getProperty(foaf+"name");
			
		//Parse the JSON object
		
		JSONObject obj = new JSONObject(json);
				
		String _workflowId = obj.getString("workflowId");
		
		//Add this detail to the model
		
		Resource workflowInstance = _model.getResource(wedata+"workflow/"+_workflowId);
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
			
			Resource programInstance = _model.getResource(wedata+"program/"+_programId); 
					
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
				
				Resource modelInstance = _model.getResource(wedata+"model/"+_modelId); 
				
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
					
					Resource portInstance = _model.getResource(wedata+"port/"+_programId+"_"+_inPortId); 
					
					//add properties
					portInstance.addProperty(rdfTypeProperty, Port); //declare type of Port
					portInstance.addProperty(label, _model.createLiteral(_inPortId)); // add port id	
						
					//add associations
					programInstance.addProperty(hasInPort, portInstance);
					
					if(_inport.has("channel")) {
						String inPortChannel = _inport.getString("channel");					
						//Now Add Channel
						Resource channelInstance = _model.getResource(wedata+"channel/"+inPortChannel); 
						channelInstance.addProperty(rdfTypeProperty, Channel);
						channelInstance.addProperty(label, _model.createLiteral(inPortChannel));
						
						portInstance.addProperty(connectsTo, channelInstance);
						
					} else if(_inport.has("params")) {
						
						JSONArray _paramArr = _inport.getJSONArray("params");
						
						for(int _paramIdx=0; _paramIdx< _paramArr.length(); _paramIdx++) {
							JSONObject _param = _paramArr.getJSONObject(_paramIdx);
							String _paramId = _param.getString("paramId");
							String _paramValue = _param.getString("paramValue");
							
							Resource paramInstance = _model.getResource(wedata+"param/"+_paramId); 
							paramInstance.addProperty(rdfTypeProperty, Entity);
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
					
					Resource portInstance = _model.getResource(wedata+"port/"+_programId+"_"+_outPortId); 
					
					//add properties
					portInstance.addProperty(rdfTypeProperty, Port); //declare type of Port
					portInstance.addProperty(label, _model.createLiteral(_outPortId)); // add port id	
						
					//add associations
					programInstance.addProperty(hasOutPort, portInstance);
					
					//Now Add Channel
					Resource channelInstance = _model.getResource(wedata+"channel/"+ _outPortChannel); 
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
				
				Resource sourceProgramInstance = _model.getResource(wedata+"program/"+_sourceProgram); 
				
				JSONObject _target = _controller.getJSONObject("target");
				String _targetPort = _target.getString("port");
				String _targetProgram = _target.getString("program");
				
				Resource targetProgramInstance = _model.getResource(wedata+"program/"+_targetProgram); 
				
				Resource controllerInstance = _model.getResource(wedata+"controller/"+_sourceProgram+_sourcePort+"_"+_targetProgram+_targetPort);
				
				controllerInstance.addProperty(rdfTypeProperty, Controller);
				controllerInstance.addProperty(label, _model.createLiteral(_sourceProgram+"|"+_sourcePort+"|"+_targetProgram+"|"+_targetPort));
				sourceProgramInstance.addProperty(controlledBy, controllerInstance );
				controllerInstance.addProperty(controls, targetProgramInstance);			
			}
		}
		
		return _model;	
}
	
	
}


