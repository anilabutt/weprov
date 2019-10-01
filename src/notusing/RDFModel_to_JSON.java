package notusing;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csiro.webservices.config.Configuration;

public class RDFModel_to_JSON {

	public JSONObject tansformInputJSON(Model results) throws JSONException {

		JSONObject resultObj = new JSONObject();
		
		Property hasSubProgram = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasSubProgram");
		Property hasInPorts = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasInPort");
		Property hasOutPorts = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasOutPort");
		Property connectsTo = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#connectsTo");
		Property hasDefaultParam = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasDefaultParam");
		Property host = results.getProperty(Configuration.NS_WEPROV+"host");
		Property label = results.getProperty("http://www.w3.org/2000/01/rdf-schema#"+"label");
		Property value = results.getProperty("http://www.w3.org/ns/prov#"+"value");
		
			
		//Get workflow Id
		Resource workflowId = (Resource) results.listSubjectsWithProperty(hasSubProgram).next();
		resultObj.put("workflowId", workflowId.toString());
		resultObj.put("userId", "");
		resultObj.put("time", "");

		NodeIterator programIter = results.listObjectsOfProperty(hasSubProgram);
		JSONArray programArr = new JSONArray();
		
		
        try {
            while (programIter.hasNext()) {
            	Resource program = (Resource) programIter.next();
                
                JSONObject _program = new JSONObject();
                _program.put("programId", results.listObjectsOfProperty(program, label).next());
                
                Resource modelId = (Resource) results.listObjectsOfProperty(program, host).next();
                _program.put("modelId", results.listObjectsOfProperty(modelId, label).next());
                
                NodeIterator inportsIterator = results.listObjectsOfProperty(program,  hasInPorts);
                JSONArray _inportsArr = new JSONArray();
                
                while(inportsIterator.hasNext()) {
                	Resource inport = (Resource)inportsIterator.next();
                	
                	JSONObject _inport = new JSONObject();
                	_inport.put("portId", results.listObjectsOfProperty(inport, label).next());
                	
                	NodeIterator channelIterator = results.listObjectsOfProperty((Resource) inport,  connectsTo);
                	if (channelIterator.hasNext()) {
                		while(channelIterator.hasNext()) {
                			Resource channel = (Resource)channelIterator.next();
                			_inport.put("channel", results.listObjectsOfProperty(channel, label).next());
                		}
                	} else {
                		NodeIterator paramIterator = results.listObjectsOfProperty((Resource) inport,  hasDefaultParam);
                		JSONArray params = new JSONArray();
                		while(paramIterator.hasNext()) {
                			Resource param = (Resource)paramIterator.next();
                			JSONObject _param = new JSONObject();
                			String paramId = (results.listObjectsOfProperty(param, label).next()).toString();
                			String paramValue = (results.listObjectsOfProperty(param, value).next()).toString();
                			_param.put("paramId", paramId);
                			_param.put("paramValue", paramValue);
                			params.put(_param);
                		}
                		_inport.put("params", params);
                	}
                	
                	_inportsArr.put(_inport);
                }
                
                _program.put("inPorts", _inportsArr);
                
                NodeIterator outportsIterator = results.listObjectsOfProperty((Resource) program,  hasOutPorts);
                JSONArray _outportsArr = new JSONArray();
                
                while(outportsIterator.hasNext()) {
                	Resource port = (Resource)outportsIterator.next();
                	JSONObject _outport = new JSONObject();
                	_outport.put("portId", results.listObjectsOfProperty(port, label).next());
                	
                	NodeIterator channelIterator = results.listObjectsOfProperty((Resource) port,  connectsTo);
                	while(channelIterator.hasNext()) {
	                	Resource channel = (Resource)channelIterator.next();
	                	_outport.put("channel", results.listObjectsOfProperty(channel, label).next());
                	}
                	
                	_outportsArr.put(_outport);
                }
                _program.put("outPorts", _outportsArr);
                programArr.put(_program);
            }
        } finally {
            if (programIter != null)
            	programIter.close();
        }
        resultObj.put("program", programArr);
		
		return resultObj;
	}
}
