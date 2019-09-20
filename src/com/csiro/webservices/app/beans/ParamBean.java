package com.csiro.webservices.app.beans;

public class ParamBean {
	
	private String paramId = "";
	private String paramValue = "";
	
	public void setParamId(String paramId) {
		this.paramId = paramId;
	}
	
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
	public String getParamId() {
		return this.paramId;
	}
	
	public String getParamValue() {
		return this.paramValue;
	}
}
