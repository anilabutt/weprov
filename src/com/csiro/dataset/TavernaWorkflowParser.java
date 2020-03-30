package com.csiro.dataset;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.csiro.webservices.app.beans.ControllerBean;
import com.csiro.webservices.app.beans.ControllerConnection;
import com.csiro.webservices.app.beans.PortBean;
import com.csiro.webservices.app.beans.ProgramBean;
import com.csiro.webservices.app.beans.Workflow;


public class TavernaWorkflowParser {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
		     String inputFilePath = "C:\\Users\\but21c\\eclipse-workspace\\weprov\\src\\taverna_workflow.txt";
		     String inputFilePath2 = "C:\\Users\\but21c\\eclipse-workspace\\weprov\\src\\taverna_workflow2.txt";
		     
		      String filepath = "C:\\Users\\but21c\\Dropbox\\CSIRO_Postdoc\\CSIRO_Papers\\EvolutionModelling\\dataset\\workflow1\\workflow\\_deprecated__Probabilistic_Model_Checking__PMC___compute_results-v1.xml";

		     Workflow workflow1 = getWorkflow(inputFilePath, "2");
		     Workflow workflow2 = getWorkflow(inputFilePath2, "1");
		     
		     printThisWorkflow(workflow2);
		     
		     
		    // Workflow workflow = getWorkflow(filepath, "1");
		    // System.out.println(workflow1.getWorkflowId());
		     //System.out.println(workflow2.getWorkflowId());
		     
		    /*** WorkflowComparison cm = new WorkflowComparison();	     
		     
		     Model model = cm.getRevisionProvenance(workflow1, workflow2);
		     model.write(System.out, "TTL"); ***/
		     
		     //cm.compareWorkflow(workflow1, workflow2);
		    // cm._model.write(System.out, "TTL"); 
		     
		     //System.out.println();
		     
		     //getSpecProvenance
		     
		       /*Model specModel = 	getWorkflowSpecificationProvenance(workflow1);
		       specModel.write(System.out, "TTL");
		     	*/	   
		     
		     //getCreationProvenance
		     
		       // Model model = getWorkflowCreationProvenance(workflow);
		     	 // model.write(System.out, "TTL"); 
		    
		     
		     //getWorkflowRevisionProvenance
		    // Model model = getWorkflowRevisionProvenance(workflow1, workflow2);
		     
		    // model.write(System.out, "TTL"); 

		     
			} catch (Exception e) {
			         e.printStackTrace();
			}
	}
	
	
	public static Workflow getWorkflow(String filepath, String revision) {
		
		Workflow workflow = new Workflow();
		// TODO Auto-generated method stub
		try {
		     File inputFile = new File(filepath);
		     DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		     DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		     Document document = dBuilder.parse(inputFile);
		     document.getDocumentElement().normalize();
		
		     //System.out.println(" documentElement :  " + document.getDocumentElement());
		     
		     org.w3c.dom.Node baseNode = document.getDocumentElement();     
		     
		     workflow = getComponentsOfThisWorkflow(baseNode,"", revision);
		     
		     
			} catch (Exception e) {
			         e.printStackTrace();
			} finally {
				return workflow;
			}	
	}
	
	public static Workflow getComponentsOfThisWorkflow (Node baseNode, String programName, String revision) throws JSONException {
	    
		Workflow workflow = new Workflow();
		//String id = "";
		
		ArrayList<PortBean> inputs = new ArrayList<PortBean>();
		ArrayList<PortBean> outputs = new ArrayList<PortBean>();
		ArrayList<ProgramBean> programs= new ArrayList<ProgramBean>();
		ArrayList<ControllerBean> controllers=new ArrayList<ControllerBean>();

		/* I was trying to print the name of processor that contains this workflow
		 * 
		 * if(baseNode.getParentNode().getParentNode() != null) {
			System.out.println(" Details of the Workflow : " + baseNode.getParentNode().getParentNode().getAttributes().getNamedItem("name")); }
		 *	
		 */
		
		/************
		 * Get the immidiate child tags of this workflow. 
		 * getChildNodes() returns workflow details, its inputs and outputs, involved processes and their links with each others. 
		 *  
		 ************/
		
		NodeList firstNodeList = baseNode.getChildNodes(); 
		String version= "";
	
		/*if(	hasAttribute(baseNode, "version") ) { 
			version = baseNode.getAttributes().getNamedItem("version").getTextContent();
		}*/
		
		//workflow.setVersionId(version);
		workflow.setRevisionId(revision);
		
		//System.out.println("Workflow version: " + version);
	    
		//Iterate the list to get different components of this workflow
		
	     for (int nodeCount = 0; nodeCount < firstNodeList.getLength(); nodeCount++) { 
	    	 
	    	 org.w3c.dom.Node node = firstNodeList.item(nodeCount); 	    	 
	    	 
	    	 if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) { 
	    		 
	    		 String nodeTag = node.getNodeName(); 	 
	    		 
	    		 /************  
	    		  * Find Workflow Details
	    		  ************/
	    		 
	    		 //System.out.println(node.getAttributes().getLength());
	    		 
	    		/* for(int i=0; i<node.getAttributes().getLength(); i++) {
	    			 //System.out.println(node.getAttributes().item(i));
	    		 }*/
	    		 
	    		 
	    		 if (nodeTag.contains("description")) {	    			 
	    			   
	    			 
	    			 String workflowId = node.getAttributes().getNamedItem("lsid").getTextContent();
	    			// workflowId = workflowId+"-v"+revision;
	    			 workflow.setWorkflowId(workflowId);
	    			// System.out.println("Workflow Id : " + workflowId);
	    			 
	    			 String workflowTitle = node.getAttributes().getNamedItem("title").getTextContent();
	    			 workflow.setWorkflowTitle(workflowTitle);
	    			// System.out.println("Workflow Title : " + workflowTitle);
	    			 
	    			 String author = node.getAttributes().getNamedItem("author").getTextContent();
	    			 workflow.setAuthor(author);
	    			// System.out.println("Workflow Author : " + author);
	    			 
	    			 String workflowDescription = firstNodeList.item(1).getTextContent();
	    			 workflow.setWorkflowDescription(workflowDescription);
	    			// System.out.println("Workflow Description: " + workflowDescription);
	    			 
	    			 
	    		 }  
	    		 
	    		 if(nodeTag.contains("location")) {
	    			 String workflowDescription = firstNodeList.item(1).getTextContent();
	    			 workflow.setWorkflowDescription(workflowDescription);
	    			// System.out.println("Workflow Description: contains location " + workflowDescription);
	    		 }
	    		 
	    		 /************ 
	    		  * Add input(ports) of this workflow
	    		  ************/
	    		 
	    		 if (nodeTag.contains("source")) {
	    			 String input = node.getAttributes().getNamedItem("name").getTextContent();
	    			 PortBean inport = new PortBean();
	    			 inport.setPortId(input);
	    			 inputs.add(inport);
	    			 //System.out.println("inputs:	" + inputs);
	    		 } if (nodeTag.contains("sink")) {
	    			 String output = node.getAttributes().getNamedItem("name").getTextContent();
	    			 PortBean outport = new PortBean();
	    			 outport.setPortId(output);
	    			 outputs.add(outport);
	    			 //System.out.println("output:	" + output);
	    		 }
	    		 	    		    		 
	    		 /************ 
	    		  * Add programs of this workflow 
	    		  ************/
	    		 
	    		 if (nodeTag.contains("processor")) {

	    			 ProgramBean program = new ProgramBean();
	    			 String name = node.getAttributes().getNamedItem("name").getTextContent();
	    			 program.setProgramId(name);
	    			 //System.out.println(name);
	    			 
	    			 Element processorElement = (Element)node;
	    			// System.out.println(" childElement :  " + processorElement);
	    			 
	    			 org.w3c.dom.NodeList processorChildList = processorElement.getChildNodes();
	    			 
	    			 for (int j = 0; j < processorChildList.getLength(); j++) { 		    		    	 
	    		    	 
	    				 Node childNode = processorChildList.item(j); 
	    		    	 

	    				 
	    		    	 if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) { 
	    		    		 
	    		    		 String childNodeTag = childNode.getNodeName();
	    		    		 
	    		    		 //String newName = tab + name;
	    		    		 if(childNodeTag.equalsIgnoreCase("s:workflow")) {
	    		    			program.setType("workflow");
	    		    			 //System.out.println(newName);	    		    			 
	    		    			NodeList sculfChild = childNode.getChildNodes();	    		    			
	    		    			
	    		    			for (int k=0; k < sculfChild.getLength(); k++) {
	    		    				
	    		    				if(sculfChild.item(k).getNodeName().contains("scufllocation")) {
	    		    					Workflow processWorkflow = new Workflow();
	    		    					processWorkflow.setWorkflowLocation(sculfChild.item(k).getTextContent());
	    		    					String title[] = sculfChild.item(k).getTextContent().split("\\/");
	    		    					processWorkflow.setWorkflowId(title[title.length-1]);
	    		    					program.setWorkflow(processWorkflow);
	    		    					//System.out.println("        " +sculfChild.item(k).getTextContent() );
	    		    				} else if(sculfChild.item(k).getNodeName().contains("scufl") ) { 
	    		    					Workflow processWorkflow = getComponentsOfThisWorkflow(sculfChild.item(k), name, revision); 
	    		    					program.setWorkflow(processWorkflow);
	    		    				}
	    		    			}
	    		    		 } else if(childNodeTag.equalsIgnoreCase("s:beanshell")) {
	    		    			 program.setType("beanshell");
	    		    			 //System.out.println(newName + " is a beanshell");
	    		    		 } else if(childNodeTag.equalsIgnoreCase("s:stringconstant")) {
	    		    			 program.setType("stringconstant");
	    		    			//System.out.println(newName + " is a stringconstant");
	    		    		 } else if (childNodeTag.equalsIgnoreCase("s:arbitrarywsdl")){
	    		    			 program.setType("arbitrarywsdl");
	    		    			 //System.out.println(newName + " is a arbitrarywsdl ");
	    		    		 } else if (childNodeTag.equalsIgnoreCase("s:wsdl")){
	    		    			 program.setType("wsdl");
	    		    			 //System.out.println(newName + " is a arbitrarywsdl ");
	    		    		 } else if (childNodeTag.equalsIgnoreCase("s:local")){
	    		    			 program.setType("local");
	    		    			 //System.out.println(newName + " is a local ");
	    		    		 } else if (childNodeTag.equalsIgnoreCase("s:localworker")){
	    		    			 program.setType("localworker");
	    		    			 //System.out.println(newName + " is a local ");
	    		    		 } else if (childNodeTag.equalsIgnoreCase("s:biomart")){
	    		    			 program.setType("biomart");
	    		    			 //System.out.println(newName + " is a local ");
	    		    		 } else if (childNodeTag.equalsIgnoreCase("s:soaplab")){
	    		    			 program.setType("soaplab");
	    		    			 //System.out.println(newName + " is a local ");
	    		    		 }
	    		    	}
	    			 }
	    			 programs.add(program);
	    		 }
	    		 
	    		 /************ 
	    		  * Add links of this workflow 
	    		  ************/
	    		 if (nodeTag.contains("link")) {
	    			 String source = node.getAttributes().getNamedItem("source").getTextContent();
	    			 String sink = node.getAttributes().getNamedItem("sink").getTextContent();
	    			 
	    			 ControllerBean controller = new ControllerBean();
	    			 ControllerConnection linkSource = new ControllerConnection();	
	    			 ControllerConnection linkSink = new ControllerConnection();	
	    			 
	    			// System.out.println("Links:	" );
	    			 
	    			if(source.contains(":")) {
	    				String process = source.split(":")[0];
	    				String output = source.split(":")[1];	    				
	    				    				
	    				linkSource.setProgramId(process);
	    				linkSource.setPortId(output);	    				
	    				//System.out.println( process + "	---> " + output);
	    			} else {
	    				linkSource.setProgramId("");
	    				linkSource.setPortId(source);
	    				//System.out.println( "	---> " + source);
	    			} 
	    			 
	    			if(sink.contains(":")) {
		    			String process = sink.split(":")[0];
		    			String input = sink.split(":")[1];		    				
		    					    				
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
	    			
	    			controllers.add(controller);
	    		 } 
	    		 
	    	 }
	     }
	     
    	 workflow.setInports(inputs);
		 workflow.setOutports(outputs);
		 workflow.setPrograms(programs);
		 workflow.setControllers(controllers);			

		 return workflow;
	}
	
	public static Model getWorkflowSpecificationProvenance (Workflow workflow) throws JSONException {
				
		TavernaWorkflowProvenance prov = new TavernaWorkflowProvenance();
		Model model = prov.generateSpecificationRDF(workflow, null);	
		
		return model;
	}
	
	public static Model getWorkflowCreationProvenance (Workflow workflow) throws JSONException {
		
		TavernaCreationProvenance cp = new TavernaCreationProvenance();
		 HashMap<String, String> partOfRel = new HashMap<String, String>();
		 ArrayList<String> subWorkflowList = new ArrayList<String>();
		 //model.write(System.out, "TTL");
		 
		 		 
		 Model _model = TDBFactory.createDataset().getDefaultModel();
		 
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
		 
		_model.add(cp.generateCreationRDF(workflow.getWorkflowId(), workflow.getAuthor(), subWorkflowList, workflow.getRevisionId()));
		 
		 return _model;
	}
	
	public static Model getWorkflowRevisionProvenance (Workflow cWf, Workflow pWf ) throws JSONException {

		
		WorkflowComparison cm = new WorkflowComparison();
		cm.compareWorkflow(cWf, pWf);
		
		Model model = TDBFactory.createDataset().getDefaultModel();
		 	 
		return model;
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
		 		 		
		/*String sparql = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
		        "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
		        "PREFIX weprov:<http://www.csiro.au/digiscape/weprov#>"+
		        //"PREFIX weprov:<http://weprov.csiro.au/>"+
		        "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#>"+
				"PREFIX prov:<http://www.w3.org/ns/prov#>"+

				"SELECT * "
				+ "WHERE { "
				+ "?o weprov:hasController ?v"
				+ "}";

	
		QueryExecution qexec = QueryExecutionFactory.create(sparql,Syntax.syntaxSPARQL_11, model);
		ResultSet results = qexec.execSelect();
		ResultSetFormatter.out(System.out, results);*/
		
	}
	
	
	public static boolean hasAttribute(Node element, String value) {
	    NamedNodeMap attributes = element.getAttributes();
	    for (int i = 0; i < attributes.getLength(); i++) {
	        Node node = attributes.item(i);
	        if (value.equals(node.getNodeValue())) {
	            return true;
	        }
	    }
	    return false;
	}

}
