package com.csiro.webservices.app.beans;

import java.util.ArrayList;

public class ProgramBean {

	private String programId;
	private String modelId;
	private ArrayList<PortBean> inports; 
	private ArrayList<PortBean> outports;
	
	public void setProgramId(String programId) {
		this.programId= programId;
	}
	
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
	
	public void setInports(ArrayList<PortBean> inports) {
		this.inports = inports;
	}
	
	public void setOutports(ArrayList<PortBean> outports) {
		this.outports = outports;
	}
	
	public String getProgramId() {
		return this.programId;
	}
	
	public String getModelId() {
		return this.modelId;
	}
	
	public ArrayList<PortBean> getInports() {
		return this.inports;
	}
	
	public ArrayList<PortBean> getOutports() {
		return this.outports;
	}
}
