package com.csiro.webservices.mappings;

import java.io.File;
import java.util.ArrayList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.csiro.webservices.app.beans.ControllerBean;
import com.csiro.webservices.app.beans.ControllerConnection;
import com.csiro.webservices.app.beans.PortBean;
import com.csiro.webservices.app.beans.ProgramBean;
import com.csiro.webservices.app.beans.Workflow;

import java.io.StringReader;
import org.xml.sax.InputSource;

public class XmlToWorkflow {

	public static Workflow getWorkflow(String xmlStr) {
		
		Workflow workflow = new Workflow();
		// TODO Auto-generated method stub
		try {
		    /* File inputFile = new File(filepath);
		     DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		     DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		     Document document = dBuilder.parse(inputFile);
		     document.getDocumentElement().normalize();*/
		
		     //System.out.println(" documentElement :  " + document.getDocumentElement());
			 Document document = convertStringToXMLDocument( xmlStr );
		     org.w3c.dom.Node baseNode = document.getDocumentElement();     		     
		     workflow = getComponentsOfThisWorkflow(baseNode, ""); 	
		     return workflow;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public static Workflow getComponentsOfThisWorkflow (Node baseNode, String programName) throws JSONException {
	    
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
		//workflow.setRevisionId(revision);
		
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
	    			 String workflowId = node.getAttributes().getNamedItem("id").getTextContent();
	    			// workflowId = workflowId+"-v"+revision;
	    			 workflow.setWorkflowId(workflowId);
	    			 //System.out.println("Workflow Id : " + workflowId);
	    			 
	    			 String workflowTitle = node.getAttributes().getNamedItem("title").getTextContent();
	    			 workflow.setWorkflowTitle(workflowTitle);
	    			 //System.out.println("Workflow Title : " + workflowTitle);
	    			 
	    			 String author = node.getAttributes().getNamedItem("author").getTextContent();
	    			 workflow.setAuthor(author);
	    			 //System.out.println("Workflow Author : " + author);
	    			 
	    			 String workflowDescription = firstNodeList.item(1).getTextContent();
	    			 workflow.setWorkflowDescription(workflowDescription);
	    			 //System.out.println("Workflow Description: " + workflowDescription);	    			 
	    		 }  

	    		 /************ 
	    		  * Add input(ports) of this workflow
	    		  ************/
	    		 
	    		 if (nodeTag.contains("inport")) {
	    			 String input = node.getAttributes().getNamedItem("name").getTextContent();
	    			 PortBean inport = new PortBean();
	    			 inport.setPortId(input);
	    			 inputs.add(inport);
	    			 //System.out.println("inputs:	" + input);
	    		 } if (nodeTag.contains("outport")) {
	    			 String output = node.getAttributes().getNamedItem("name").getTextContent();
	    			 PortBean outport = new PortBean();
	    			 outport.setPortId(output);
	    			 outputs.add(outport);
	    			 //System.out.println("output:	" + output);
	    		 }
	    		 	    		    		 
	    		 /************ 
	    		  * Add programs of this workflow 
	    		  ************/
	    		 
	    		 if (nodeTag.contains("program")) {

	    			 ProgramBean program = new ProgramBean();
	    			 String name = node.getAttributes().getNamedItem("name").getTextContent();
	    			 program.setProgramId(name);
	    			 //System.out.println("Program name : " + name);
	    			 
	    			 Element processorElement = (Element)node;
	    			 //System.out.println(" childElement :  " + processorElement);
	    			 
	    			 org.w3c.dom.NodeList processorChildList = processorElement.getChildNodes();
	    			 
	    			 for (int j = 0; j < processorChildList.getLength(); j++) { 		    		    	 
	    		    	 
	    				 Node childNode = processorChildList.item(j); 
	    		    	 

	    				 
	    		    	 if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) { 
	    		    		 
	    		    		 String childNodeTag = childNode.getNodeName();
	    		    		 
	    		    		 //String newName = tab + name;
	    		    		 if(childNodeTag.equalsIgnoreCase("ds:workflow")) {
	    		    			program.setType("workflow");
	    		    			 //System.out.println(newName);	    		    			 
	    		    			NodeList child = childNode.getChildNodes();	    		    			
	    		    			
	    		    			for (int k=0; k < child.getLength(); k++) {
	    		    				
	    		    				if(child.item(k).getNodeName().contains("weprov") ) { 
	    		    					Workflow processWorkflow = getComponentsOfThisWorkflow(child.item(k), name); 
	    		    					program.setWorkflow(processWorkflow);
	    		    				}
	    		    			}
	    		    		 } 
	    		    	}
	    			 }
	    			 programs.add(program);
	    		 }
	    		 
	    		 /************ 
	    		  * Add links of this workflow 
	    		  ************/
	    		 if (nodeTag.contains("controller")) {
	    			 String source = node.getAttributes().getNamedItem("source").getTextContent();
	    			 String sink = node.getAttributes().getNamedItem("target").getTextContent();
	    			
	    			 //System.out.println(source +"    "+sink);
	    			 ControllerBean controller = new ControllerBean();
	    			 ControllerConnection linkSource = new ControllerConnection();	
	    			 ControllerConnection linkSink = new ControllerConnection();	
	    			 
	    			// System.out.println("Links:	" );
	    			 
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
	
	 private static Document convertStringToXMLDocument(String xmlString) 
	    {
	        //Parser that produces DOM object trees from XML content
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	         
	        //API to obtain DOM Document instance
	        DocumentBuilder builder = null;
	        try
	        {
	            //Create DocumentBuilder with default configuration
	            builder = factory.newDocumentBuilder();
	             
	            //Parse the content to Document object
	            StringReader streader = new StringReader(xmlString);
	            InputSource is = new InputSource(streader);
	            Document doc = builder.parse(is);
	            return doc;
	        } 
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	        return null;
	    }
	
}
