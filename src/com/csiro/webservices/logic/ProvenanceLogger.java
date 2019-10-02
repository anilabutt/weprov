package com.csiro.webservices.logic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.evolution.listener.EvolutionListener;
import com.csiro.webservices.rest.GenericService;

public class ProvenanceLogger extends GenericService {

	public static String wedata = Configuration.NS_RES;
	public static String weprov = Configuration.NS_WEPROV;

			
	public ProvenanceLogger(String loggerName) {
		super(ProvenanceLogger.class.getName());
		// TODO Auto-generated constructor stub
	}

	
	public String addWorkflowProvenance(InputStream incomingData) throws JSONException {
		
		String response = "";
		StringBuilder workflowBuilder = new StringBuilder();
		
		
		try {
			
			//get JSON string from input stream
			
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				workflowBuilder.append(line);
			}
			
			JSONObject obj = new JSONObject(workflowBuilder.toString());
			
			String _workflowId = obj.getString("workflowId");
			String workflowURIString = wedata+"workflow/"+_workflowId;
			
			String actorIdURIString = "";
			
			if (obj.has("userId")){
				actorIdURIString = wedata+"agent/"+obj.getString("userId");
			}
			
			
			
			// Get workflow specification provenance 
			
			SpecificationProvenance specProv = new SpecificationProvenance();
			Model _cSpecModel = specProv.generateSpecificationRDF(workflowBuilder.toString()); 
			
			//Check if Workflow previous version exists
			EvolutionListener listener = new EvolutionListener();
			
			boolean pVersion = listener.previousVersionExists(workflowURIString);
			
			
			//If workflow does not exist in the store.
			
			if(!pVersion) {
			
				// Add Workflow Specification Provenance
				addWorkflow(_cSpecModel);
				
				//Add workflow Evolution Provenance - Generation of Workflow: Workflow Creation Activity
				Model provModel = listener.creationProv(workflowURIString, actorIdURIString);
				addProvenance(provModel);
			
			} else {
				
				Model _pSpecModel = getWorkflowAsModel(_workflowId);
				//Model _workflowDescription = getResource(workflowURIString, "provModel");
				String version = getPropertyValue(_pSpecModel.getResource(workflowURIString), _pSpecModel.getProperty(weprov+"version"));
				
				logger.info("pervious version number : " + version);
				
				int currentVersion = Integer.parseInt(version) + 1;
				
				logger.info("pervious version number : " + currentVersion);
				
				Model model = listener.evolutionActivity(_cSpecModel, _pSpecModel, workflowURIString, actorIdURIString, currentVersion+"");


			}
			
		} catch (Exception e) {
			logger.info("Can not add because ... " + e);
		}	
	 return response;
	}
	
	

}
