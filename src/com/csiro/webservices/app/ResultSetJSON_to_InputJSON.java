package com.csiro.webservices.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.rdf.model.Model;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultSetJSON_to_InputJSON {

	public JSONObject generateRDF(String json) throws JSONException {
		JSONObject latestStoredWorkflow = new JSONObject();
	
		JSONObject resultSetObj = new JSONObject(json);

        JSONArray programsArray = new JSONArray();
        
        
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>> programsInfo = new  HashMap<String, HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>>();    
        //HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> programDetail = new HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>();
        
        JSONArray resultset = resultSetObj.getJSONObject("results").getJSONArray("bindings");
         
        
       // System.out.println(resultset);  
        for(int resultIdx=0; resultIdx<resultset.length() ; resultIdx++) {
        	JSONObject record = resultset.getJSONObject(resultIdx);
        	String program =  record.getJSONObject("programId").getString("value");
        	HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> programDetail = new HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>();
        	
        	
        	if(programsInfo.containsKey(program)) {
        		programDetail = programsInfo.get(program);
        		
        	} else {
        		programsInfo.put(program, null);
        		System.out.println(program);
        	}
        	
        	
        	//System.out.println(program);        	
        }
		
		
		return latestStoredWorkflow;
	}
}
