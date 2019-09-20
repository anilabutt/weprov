package com.csiro.webservices.app.beans;

import java.util.ArrayList;

public class PortBean {
	
	private String portId = "";
	private String channel = "";
	private ArrayList<ParamBean> params = new ArrayList<ParamBean>(); 
	
	public void setPortId(String portId) {
		this.portId = portId;
	}
	
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public void setParams (ArrayList<ParamBean> params) {
		this.params = params;
	}
	
	
	public String getPortId() {
		return this.portId;
	}
	
	public String getChannel() {
		return this.channel;
	}
	
	public ArrayList<ParamBean> getParams() {
		return this.params;
	}
}
