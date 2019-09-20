package com.csiro.webservices.app.beans;

import java.util.ArrayList;

public class Workflow {
	
	private String workflowId ;
	private String revisionId;
	private ArrayList<ProgramBean> programs;
	private ArrayList<ControllerBean> controllers;
	
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	
	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}
	
	public void setPrograms(ArrayList<ProgramBean> programs) {
		this.programs = programs;
	}
	
	public void setControllers(ArrayList<ControllerBean> controllers) {
		this.controllers = controllers;
	}
	
	public String getWorkflowId() {
		return this.workflowId;
	}
	
	public String getRevisionId() {
		return this.revisionId;
	}
	
	public ArrayList<ProgramBean> getPrograms() {
		return this.programs;
	}
	
	public ArrayList<ControllerBean> getControllers(){
		return this.controllers;
	}
}
