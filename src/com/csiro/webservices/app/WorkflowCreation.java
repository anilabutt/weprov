package com.csiro.webservices.app;

import java.util.GregorianCalendar;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csiro.webservices.config.Configuration;
import com.sun.media.jfxmedia.logging.Logger;

public class WorkflowCreation{

	public static String weprov = Configuration.NS_WEPROV; //"http://www.csiro.au/digiscape/weprov#";
	public static String wedata = Configuration.NS_RES;
	public static String provone = "http://purl.dataone.org/provone/2015/01/15/ontology#";
	public static String prov = "http://www.w3.org/ns/prov#";
	public static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static String foaf = "http://xmlns.com/foaf/0.1/";
	
	
	public Model generateRDF(String json) throws JSONException {
		// A temporary model to add rdf for this JSON
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		
		//Get Classes and Properties of weprov model
		
		  Resource Agent = _model.getResource(prov + "Agent");
		  Resource Activity = _model.getResource(prov+"Activity");
		  
		  Resource Workflow = _model.getResource(provone+"Workflow");
		  Resource Program = _model.getResource(provone+"Program");
		  Resource Model = _model.getResource(weprov+"Model");
		  Resource Port = _model.getResource(provone+"Port");
		  Resource Channel = _model.getResource(provone+"Channel");
		  Resource Controller = _model.getResource(provone+"Controller");
		  
		  Resource Invalidation = _model.getResource(prov+"Invalidation");
		  Resource Usage = _model.getResource(prov+"Usage");
		  Resource Generation = _model.getResource(prov+"Generation");
		
		  Resource Derivation = _model.getResource(prov+"Derivation");
		  Resource Revision = _model.getResource(prov+"Revision");
		
		  Resource Creation = _model.getResource(weprov+"Creation");
		  Resource Deletion = _model.getResource(weprov+"Deletion");
		  Resource Modification = _model.getResource(weprov+"Modification");
		  Resource Renaming = _model.getResource(weprov+"Renaming");
		
		  
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
			
			Property wasAssociatedWith = _model.getProperty(prov+"wasAssociatedWith");
			Property wasGeneratedBy = _model.getProperty(prov+"wasGeneratedBy");
			Property wasInvalidatedBy = _model.getProperty(prov+"wasInvalidatedBy");
			Property wasDerivationOf = _model.getProperty(prov+"wasDerivationOf");
			Property wasRevisionOf = _model.getProperty(prov+"wasRevisionOf");
			
			Property qualifiedGeneration = _model.getProperty(prov+"qualifiedGeneration");
			Property qualifiedInvalidation = _model.getProperty(prov+"qualifiedInvalidation");
			Property qualifiedDerivation = _model.getProperty(prov+"qualifiedDerivation");
			Property qualifiedUsage = _model.getProperty(prov+"qualifiedUsage");
			Property qualifiedRevision = _model.getProperty(prov+"qualifiedRevision");
			
			Property hadGeneration = _model.getProperty(prov+"hadGeneration");
			Property hadUsage = _model.getProperty(prov+"hadUsage");
			Property hadActivity = _model.getProperty(prov+"hadActivity");
			Property hadInvalidation = _model.getProperty(prov+"hadInvalidation");
			
			Property agent = _model.getProperty(prov+"agent");
			Property entity = _model.getProperty(prov+"entity");
			Property activity = _model.getProperty(prov+"activity");
			
			// Properties
			Property atTime = _model.getProperty(prov+"atTime");
			Property startedAtTime = _model.getProperty(prov+"startedAtTime");
			Property endedAtTime = _model.getProperty(prov+"endedAtTime");
			Property value = _model.getProperty(prov+"value");
			Property label = _model.getProperty(rdfs+"label");
			Property foafname = _model.getProperty(foaf+"name");
			
		//Parse the JSON object
		
		JSONObject obj = new JSONObject(json);
				
		String _workflowId = obj.getString("workflowId");
		String _userId = obj.getString("userId");
		String _time = obj.getString("time");

		
		//Add this detail to the model
		
		Resource workflowInstance = _model.getResource(wedata+"workflow/"+_workflowId);
		workflowInstance.addProperty(rdfTypeProperty, Workflow);
		workflowInstance.addProperty(label, _model.createLiteral(_workflowId));	
		
		Resource agentInstance = _model.getResource(wedata+"agent/"+_userId);
		agentInstance.addProperty(rdfTypeProperty, Agent);
		agentInstance.addProperty(foafname, _model.createLiteral(_userId));		
		
		JSONArray programArr = obj.getJSONArray("programs");
		
		for(int programIdx=0; programIdx<programArr.length() ; programIdx++) {
			JSONObject _program = programArr.getJSONObject(programIdx);
			String _programId = _program.getString("programId");
			String _modelId = _program.getString("modelId");
							
			/** 
			 * Generate RDF for Program details
			 **/
			Resource programInstance = _model.getResource(wedata+"program/"+_programId); 
					
			//add properties
			programInstance.addProperty(rdfTypeProperty, Program); //declare type of program
			programInstance.addProperty(label, _model.createLiteral(_programId)); // add program id	
			
			//add association
			workflowInstance.addProperty(hasSubProgram, programInstance); // link program with workflow
			
			
			/** 
			 * Generate RDF for Model of Program
			 **/
			
			Resource modelInstance = _model.getResource(wedata+"model/"+_modelId); 
			
			//add properties
			modelInstance.addProperty(rdfTypeProperty, Model); //declare type of model
			modelInstance.addProperty(label, _model.createLiteral(_modelId)); // add model id	
			
			//add association
			programInstance.addProperty(host, modelInstance); // link model with program
			
			
			/* Extract Inports detail from JSON */
			
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
						paramInstance.addProperty(label, _model.createLiteral(_paramId));
						paramInstance.addProperty(value, _model.createLiteral(_paramValue));
					
						portInstance.addProperty(hasDefaultParam, paramInstance);
					}	
				}					
			}
			
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
		
		
		/**
		 * Generate evolution provenance here
		 **/
		
		Resource _creation = _model.getResource(wedata +"creation/"+ _workflowId);
		_creation.addProperty(rdfTypeProperty, Creation);
		_creation.addProperty(startedAtTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_creation.addProperty(wasAssociatedWith, agentInstance);
		
		Resource _generation = _model.getResource(wedata +"generation/"+ _workflowId );
		_generation.addProperty(rdfTypeProperty, Generation);
		_generation.addProperty(atTime, _model.createTypedLiteral(GregorianCalendar.getInstance()));
		_generation.addProperty(activity, _creation);
		
		workflowInstance.addProperty(wasGeneratedBy, _creation);
		workflowInstance.addProperty(qualifiedGeneration, _generation);
		
				
		System.out.println(_model.size()+"");
		String query = "SELECT * WHERE {?subject ?predicate ?object}";
		QueryExecution qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11, _model);
		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results);
		return _model;
		
	}
	
	
}


