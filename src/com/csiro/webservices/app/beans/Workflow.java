package com.csiro.webservices.app.beans;

import java.util.ArrayList;

public class Workflow {
	
	private String workflowId="" ;
	private String workflowDescription="";
	private String workflowTitle="";
	private String author="";
	private String revisionId=""; 
	private ArrayList<PortBean> inports= new ArrayList<PortBean>(); 
	private ArrayList<PortBean> outports= new ArrayList<PortBean>();
	private ArrayList<ProgramBean> programs= new ArrayList<ProgramBean>();
	private ArrayList<ControllerBean> controllers= new ArrayList<ControllerBean>();
	private String workflowLocation="";
	
	
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	
	public void setWorkflowDescription(String description) {
		this.workflowDescription = description;
	}
	
	public void setWorkflowTitle(String title) {
		this.workflowTitle = title;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}

	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}
	
	
	public void setInports(ArrayList<PortBean> inports) {
		this.inports = inports;
	}
	
	public void setOutports(ArrayList<PortBean> outports) {
		this.outports = outports;
	}
	
	public void setPrograms(ArrayList<ProgramBean> programs) {
		this.programs = programs;
	}
	
	public void setControllers(ArrayList<ControllerBean> controllers) {
		this.controllers = controllers;
	}
	
	public void setWorkflowLocation(String location) {
		this.workflowLocation = location;
	}
	
	public String getWorkflowId() {
		return this.workflowId;
	}
	
	public String getWorkflowDescription() {
		return this.workflowDescription;
	}
	
	public String getWorkflowTitle() {
		return this.workflowTitle;
	}
	
	public String getAuthor() {
		return this.author;
	}

	
	public String getRevisionId() {
		return this.revisionId;
	}
	
	public ArrayList<PortBean> getInports() {
		return this.inports;
	}
	
	public ArrayList<PortBean> getOutports() {
		return this.outports;
	}
	
	public ArrayList<ProgramBean> getPrograms() {
		return this.programs;
	}
	
	public ArrayList<ControllerBean> getControllers(){
		return this.controllers;
	}
	
	public String getWorkflowLocation() {
		return this.workflowLocation;
	}
}
