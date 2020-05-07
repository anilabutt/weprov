package com.csiro.webservices.logic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;

import com.csiro.webservices.Listeners.WorkflowComparison;
import com.csiro.webservices.Listeners.WorkflowCrProv;
import com.csiro.webservices.Listeners.WorkflowSpecProv;
import com.csiro.webservices.app.beans.ControllerBean;
import com.csiro.webservices.app.beans.ProgramBean;
import com.csiro.webservices.app.beans.Workflow;
import com.csiro.webservices.mappings.RdfToWorkflow;
import com.csiro.webservices.mappings.XmlToWorkflow;
import com.csiro.webservices.rest.GenericService;
import com.csiro.webservices.store.QuadStore;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ProvenanceLogger extends GenericService {
			
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
			
			//JSONObject obj = new JSONObject(workflowBuilder.toString());
			
			//get workflow data from xml file
			XmlToWorkflow xmltoworkflow = new XmlToWorkflow();
			Workflow workflow = xmltoworkflow.getWorkflow(workflowBuilder.toString() );			
			//printThisWorkflow(workflow);
			
			//Generate Specification Provenance
			WorkflowSpecProv prov = new WorkflowSpecProv();			
			Model specModel = prov.generateSpecificationRDF(workflow, null);
			
			//get workflow Id
			String thisWorkflowId = workflow.getWorkflowId();
			
			thisWorkflowId = "http://weprov.csiro.au/workflow/" +thisWorkflowId;
			//System.out.println("thisWorkflowId : " + thisWorkflowId);
			
			//Retrieve previous version of this workflow
			Model rel_model = getWorkflowModel(thisWorkflowId);
			//rel_model.write(System.out, "TTL"); 
			
			//If previous version does not exist then this is a new workflow 
			if(rel_model.isEmpty()==true) {
				System.out.println("This workflow does not exist");
				
				//Store Specification Provenance into virtuoso database. 
				  QuadStore.getDefaultStore().insertSpecs(specModel);
				 
				 //Generate Generation Provenance
			     Model creationModel = getWorkflowCreationProvenance(workflow);
			     QuadStore.getDefaultStore().insertEvol(creationModel);	 
				
			} else {
				System.out.println("This workflow already exist");
				//rel_model.write(System.out, "TTL"); 
				//printThisWorkflow(workflow);
				
				RdfToWorkflow rdftoworkflow = new RdfToWorkflow();
				Workflow w = rdftoworkflow.getWorkflowfromRdf(rel_model, thisWorkflowId);
				
				String revId = getRevisionId(thisWorkflowId)+"";     
				
				WorkflowComparison cm = new WorkflowComparison();	
				Model comparison_model = cm.getRevisionProvenance(workflow, w, revId);
				
				System.out.println("Is this model empty? " + comparison_model.size());
				comparison_model.write(System.out, "TTL"); 
				
				//Add the revision provenance in the database
				QuadStore.getDefaultStore().insertEvol(comparison_model);	
								
				//delete the previous version
				QuadStore.getDefaultStore().deleteSpecs(rel_model);
				
				//Store current specifications into virtuoso database. 
				QuadStore.getDefaultStore().insertSpecs(specModel);
			}

		} catch (Exception e) {
			logger.info("Can not add because ... " + e);
		}	
	 return response;
	}
	
	
	public static Model getWorkflowCreationProvenance (Workflow workflow) throws JSONException {
		
		WorkflowCrProv cp = new WorkflowCrProv();
		 HashMap<String, String> partOfRel = new HashMap<String, String>();
		 ArrayList<String> subWorkflowList = new ArrayList<String>();
		 //model.write(System.out, "TTL");
		 
		 		 
		 Model _model = ModelFactory.createDefaultModel();
		 
		 ArrayList<ProgramBean> programs = workflow.getPrograms();
		
		 //System.out.println("Program Size  " + workflow.getPrograms());
		
		 for(int i=0; i <programs.size(); i++) {				 
			 ProgramBean program = programs.get(i);
			 //System.out.println(program.getProgramId()+ "	----   " + program.getType());
			 if(program.getType().equalsIgnoreCase("workflow")) {
				// System.out.println("------------------------------");
				 subWorkflowList.add(program.getWorkflow().getWorkflowId());
				// System.out.println("------------------------------" + program.getWorkflow().getWorkflowId());
				 _model.add(getWorkflowCreationProvenance(program.getWorkflow()));
			 }
		 }
		 
		_model.add(cp.generateCreationRDF(workflow.getWorkflowId(), workflow.getAuthor(), subWorkflowList ));
		 
		 return _model;
	}
	
	
	public static void printThisWorkflow (Workflow workflow) throws JSONException {
		
		 System.out.println("workflowId : " + workflow.getWorkflowId());
		 System.out.println("workflowTitle : " + workflow.getWorkflowTitle());
		 
		 for(int i=0; i<workflow.getInports().size(); i++) {
			 System.out.println("Input : " +workflow.getInports().get(i).getPortId());
		 } for(int i=0; i<workflow.getOutports().size(); i++) {
			 System.out.println("Output : " + workflow.getOutports().get(i).getPortId());
		 } for(int i=0; i<workflow.getControllers().size(); i++) {
			 ControllerBean cont = workflow.getControllers().get(i);
			 
			 System.out.println("Controller : " + cont.getSource().getProgramId()+"." +cont.getSource().getPortId()+":" + cont.getTarget().getProgramId()+"."+cont.getTarget().getPortId());
		 } 
		 
		 ArrayList<ProgramBean> programs = workflow.getPrograms();
		 
		 for(int i=0; i <programs.size(); i++) {				 
			 ProgramBean program = programs.get(i);
			 System.out.println(program.getProgramId()+ "	" + program.getType());
			 if(program.getType().equalsIgnoreCase("workflow")) {
				 System.out.println("------------------------------");
				 printThisWorkflow(program.getWorkflow());
			 }
		 }
		
	}
	
	public static int getRevisionId(String thisWorkflowId) {
		int revisionId = 0;
		ResultSet rs = getRevisionCount(thisWorkflowId);
		
        if(rs.hasNext()) {
        	QuerySolution qs= rs.next();
        	String prevRevisionId = qs.getLiteral("rev").toString(); 
			revisionId = Integer.parseInt(prevRevisionId);
		}         
        revisionId++;
        		
        return revisionId; 
	}
	
	 

}
