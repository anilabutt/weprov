package com.csiro.webservices.app.beans;

import java.util.ArrayList;

public class ProgramBean {

	private String programId="";
	private String modelId="";
	private String type="";
	private Workflow workflow= new Workflow();

	
	public void setProgramId(String programId) {
		this.programId= programId;
	}
	
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}
	
	public String getProgramId() {
		return this.programId;
	}
	
	public String getModelId() {
		return this.modelId;
	}
	
	public String getType() {
		return this.type;
	}
	
	public Workflow getWorkflow() {
		return this.workflow;
	}
	

}
