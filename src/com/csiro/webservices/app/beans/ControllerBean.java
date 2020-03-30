package com.csiro.webservices.app.beans;

public class ControllerBean {
	private ControllerConnection source= new ControllerConnection();
	private ControllerConnection target= new ControllerConnection();
	
	public void setSource(ControllerConnection source) {
		this.source =source;
	}
	
	public void setTarget(ControllerConnection target) {
		this.target = target;
	}
	
	public ControllerConnection getSource() {
		return this.source;
	}
	
	public ControllerConnection getTarget() {
		return this.target;
	}
}
