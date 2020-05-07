/*
 * Copyright (c) 2020, CSIRO. All rights reserved.
 * Use is subject to license terms.
 */

package com.csiro.webservices.rest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;

import com.csiro.webservices.logic.ProvenanceLogger;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * REST services for Course
 * 
 * @author anila
 */

@Path("/workflows")
public class Workflow extends ProvenanceLogger {

	/**
	 * Default constructor to initializes logging by its parent.
	 */
	public Workflow() {
		super(Workflow.class.getName());
	}
	
	
	/**
	 * Service to add info about workflow  
	 * @param Workflow in JSON format
	 * @return number of triples added into the store
	 * @throws JSONException 
	 * */
	@POST 
	@Produces(MediaType.TEXT_HTML) 
	@Consumes(MediaType.APPLICATION_XML)
	public String addWorkflow(InputStream incomingData) throws JSONException {
		
		return addWorkflowProvenance(incomingData);
	}

	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getAllWorkflows() {
		
		Model _model = getWorkflows();
		
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		_model.write(byteout, "TTL");
		logger.info("Model serialized successfully");
		String str = byteout.toString();
		
		return str;
		//return "<html> " + "<title>" + "Hello World RESTful Jersey"
				//+ "</title>" + "<body><h1>" + "Hello World RESTful Jersey"
				//+ "</body></h1>" + "</html> ";
	}
	
	/**
	 * Service to describe a particular Workflow   
	 * @param workflowId of workflow
	 * @return Description of workflow in specified serialization format
	 * */
	@GET @Path("/{workflowId}") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getWorkflowDetail(@PathParam("workflowId") String workflowId) {
		try {
			Model _model = ModelFactory.createDefaultModel();
			
			String workflow = "http://weprov.csiro.au/workflow/"+workflowId+"";
			//System.out.println(workflow);
			
			_model = getWorkflowModel(workflow);
			
			ByteArrayOutputStream byteout = new ByteArrayOutputStream();
			_model.write(byteout, "TTL");
			logger.info("Model serialized successfully");
			String str = byteout.toString();
			
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	
	@GET @Path("/evolution") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getWorkflowsForAgent(@QueryParam("agent") String agentName) {
		try {
			
			WeprovCompetencyQueries cq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
			String str = cq.getWorkflowsForAgent(agentName);
			
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	@GET @Path("/evolution/collaborators") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getCollaborators() {
		try {			
			WeprovCompetencyQueries cq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
			String str = cq.getCollaborations();
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	/**
	 * Service to describe a particular Course   
	 * @param courseId of person
	 * @return Description of Course in specified serialization format
	 * */
	@GET @Path("/{workflowId}/generation") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getWorkflowGenerationProvenance(@PathParam("workflowId") String workflowId) {
		try {
						
			String workflow = "http://weprov.csiro.au/workflow/"+workflowId+"";
			//System.out.println(workflow);
			WeprovCompetencyQueries cq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
			String str = cq.getGenerationProvenance(workflow);
			
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	
	@GET @Path("/{workflowId}/generation/agents") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getWorkflowCreator(@PathParam("workflowId") String workflowId) {
		try {
						
			String workflow = "http://weprov.csiro.au/workflow/"+workflowId+"";
			//System.out.println(workflow);
			WeprovCompetencyQueries cq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
			String str = cq.getWorkflowCreator(workflow);
			
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	
	@GET @Path("/{workflowId}/revisions") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getWorkflowRevision(@QueryParam("rNo") String revision, @PathParam("workflowId") String workflowId) {
		try {
						
			String workflow = "http://weprov.csiro.au/workflow/"+workflowId+"";
			//System.out.println(workflow);
			WeprovCompetencyQueries cq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
			String str = cq.getRevisionProvenance(workflow, revision);
			
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	@GET @Path("/{workflowId}/revisions/time") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getWorkflowRevisionDuringADuration(@PathParam("workflowId") String workflowId, @QueryParam("s") String startTime, @QueryParam("e") String endTime) {
		try {
						
			String workflow = "http://weprov.csiro.au/workflow/"+workflowId+"";
			WeprovCompetencyQueries cq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
			System.out.println(startTime +"\t" + endTime);
			String str = cq.getAllRevisionsBetweenADuration(workflow, startTime, endTime);
			
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	@GET @Path("/{workflowId}/revisions/components") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getWorkflowUnstableComponents(@PathParam("workflowId") String workflowId) {
		try {
						
			String workflow = "http://weprov.csiro.au/workflow/"+workflowId+"";
			WeprovCompetencyQueries cq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
			
			String str = cq.getWorkflowUnstableComponents(workflow);
			
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	@GET @Path("/{workflowId}/revisions/agents") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getWorkflowRevisior(@QueryParam("rNo") String revision, @PathParam("workflowId") String workflowId) {
		try {
						
			String workflow = "http://weprov.csiro.au/workflow/"+workflowId+"";
			//System.out.println(workflow);
			WeprovCompetencyQueries cq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
			String str = cq.getWorkflowRevisior(workflow, revision);
			
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	@GET @Path("/{workflowId}/evolution") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getWorkflowEvolutionProvenance(@PathParam("workflowId") String workflowId) {
		try {
						
			String workflow = "http://weprov.csiro.au/workflow/"+workflowId+"";
			//System.out.println(workflow);
			WeprovCompetencyQueries cq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
			String str = cq.getCompleteProvenance(workflow);
			
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	@GET @Path("/{workflowId}/evolution/time") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getWorkflowEvolutionTime(@PathParam("workflowId") String workflowId) {
		try {
						
			String workflow = "http://weprov.csiro.au/workflow/"+workflowId+"";
			//System.out.println(workflow);
			WeprovCompetencyQueries cq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
			String str = cq.getWorkflowEvolutionTime(workflow);
			
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	@GET @Path("/{workflowId}/evolution/agents") 
	@Produces(MediaType.TEXT_PLAIN)
	public String getWorkflowParticipation(@PathParam("workflowId") String workflowId) {
		try {
						
			String workflow = "http://weprov.csiro.au/workflow/"+workflowId+"";
			//System.out.println(workflow);
			WeprovCompetencyQueries cq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
			String str = cq.getWorkflowParticipants(workflow);
			
			return str;
			
		} catch (Exception exp) {
			return ""+exp;
		}
	}
	
	
	//(@QueryParam("uri") String uri) 
}
