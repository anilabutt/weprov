package com.csiro.webservices.app.beans;

public class ControllerConnection {
	private String portId;
	private String programId;
	
	public void setPortId(String portId) {
		this.portId=portId;
	}
	
	public void setProgramId(String programId) {
		this.programId=programId;
	}
	
	public String getPortId() {
		return this.portId;
	}
	
	public String getProgramId() {
		return this.programId;
	}
}
