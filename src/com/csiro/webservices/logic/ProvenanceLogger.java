package com.csiro.webservices.logic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
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
			
			String _actorId = "";
			if (obj.has("userId")){
				obj.getString("userId");
			}
//			logger.info(obj.toString());
			
			
			// Get workflow specification provenance 
			
			WorkflowSpecificationProvenance specProv = new WorkflowSpecificationProvenance();
			Model _cSpecModel = specProv.generateSpecificationRDF(workflowBuilder.toString()); 
			
			//Check if Workflow previous version exists
			EvolutionListener listener = new EvolutionListener();
			
			boolean pVersion = listener.previousVersionExists(_workflowId);
			
			if(!pVersion) {
				add(_cSpecModel);
				newWorkflowEvo("workflow", _workflowId, _actorId);
			} else {
				Model _pSpecModel = getWorkflowAsModel(_workflowId);
				
				listener.evolutionActivity(_cSpecModel, _pSpecModel);
//				logger.info(" Previous Version from Store .... ");
//				StmtIterator iter = _pSpecModel.listStatements();
//				
//				while (iter.hasNext()) {
//					Statement tr = iter.next();
//					System.out.println(tr.getSubject()  + "\t" + tr.getPredicate() + "\t" + tr.getObject());
//				}
//				
//				Thread.sleep(2000);
//				
//				logger.info(" Current Version from Specs .... ");
//				
//				StmtIterator siter = _cSpecModel.listStatements();
//				
//				while (siter.hasNext()) {
//					Statement tr = siter.next();
//					System.out.println(tr.getSubject()  + "\t" + tr.getPredicate() + "\t" + tr.getObject());
//				}
//				
//				Thread.sleep(2000);
//				
//				logger.info(" New Information in Current Version .... ");
//				
//				boolean currentVerDiffFound = listener.getDifference(_cSpecModel, _pSpecModel);
//				
//				logger.info(" Information Missing in Current Version .... ");
//				
//				boolean previousVerDiffFound = listener.getDifference(_pSpecModel, _cSpecModel);	
			}
			
		} catch (Exception e) {
			logger.info("Can not add because ... " + e);
		}	
	 return response;
	}
	
	
	private String newWorkflowEvo(String entityType, String entityId, String actorId) {
		
		logger.info("Adding a new workflow .... ");
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		CreationProvenance RDFCreator =  new CreationProvenance();
		
		try {
			_model = RDFCreator.generateEvolProvenance(entityType, entityId, actorId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return add(_model);
	}
}
