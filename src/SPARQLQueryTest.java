import java.util.ArrayList;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.store.TripleStore;

import org.apache.jena.query.QuerySolution;

public class SPARQLQueryTest {

	public static void main(String[] args) throws JSONException {
				
				Dataset baseDataSet= TDBFactory.createDataset(Configuration.getProperty(Configuration.STORE_PATH));
				Model baseModel = baseDataSet.getNamedModel(Configuration.NS_EVORES); 
				Resource subject = baseModel.getResource("http://weprov.csiro.au/workflow/abc");
				Property prop = baseModel.getProperty("http://purl.org/ontology/weprov#version");
				Statement stmt = baseModel.getProperty(subject, prop);
				System.out.println(stmt.getLiteral().getLexicalForm());
			
				
//				JSONObject resultObj  =  new JSONObject();
//				String sparql = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
//						"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
//				        "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
//				        "PREFIX weprov:<http://weprov.csiro.au/workflow/>"+
//				        //"PREFIX weprov:<http://weprov.csiro.au/>"+
//				        "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#>"+
//						"PREFIX prov:<http://www.w3.org/ns/prov#>"+
//
////				        "SELECT DISTINCT ?programId ?inportId ?outportId ?channelInId ?paramId ?paramValue ?outportId ?channelOutId ?controllerId WHERE {" +
////				        "weprov:abc rdf:type ?type;"
////				         +"	provone:hasSubProgram ?program. ?program rdfs:label ?programId."
////				        + "?program provone:hasInPort ?inport. ?inport rdfs:label ?inportId."
////				        	+ "OPTIONAL {?inport provone:connectsTo ?channelIn. ?channelIn rdfs:label ?channelInId.} "
////				        	+ "OPTIONAL {?inport provone:hasDefaultParam ?param. ?param rdfs:label ?paramId."
////				        		+ "?param prov:value ?paramValue. }"
////				        + "?program provone:hasOutPort ?outport. ?outport rdfs:label ?outportId. "
////				        	+ "?outport provone:connectsTo ?channelOut. ?channelOut rdfs:label ?channelOutId. "
////					        + "OPTIONAL  {?program provone:controlledBy/rdfs:label ?controllerId. }"
////				        + "}";
//
//"SELECT * WHERE {?s ?p ?o}";
//				
////				String sparql = "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#>"
////				+ "select ?begin ?midI  ?midJ ?end "
////						+ "where {"
////						+ "?begin provone:controlledBy+ ?counter ."
////						+ "?counter provone:controls+ ?midI ."
////						+ "FILTER NOT EXISTS { [] provone:controls ?begin }"
////						+ "?midI provone:controlledBy* ?midJ ."
////						+ "?midJ provone:controls* ?end ."
////						+ "FILTER NOT EXISTS { ?end provone:controlledBy [] }"
////						+ "}"
////						+ "group by ?begin ?end ?midI ?midJ "
////						+ "order by ?begin ?end count(?counter)";
////				
//				QueryExecution qexec = QueryExecutionFactory.create(sparql,Syntax.syntaxSPARQL_11, baseModel);
//				ResultSet results = qexec.execSelect();
//				ResultSetFormatter.out(System.out, results);
//				
////				String sparql ="PREFIX ifkm:<http://purl.org/ontology/ifkm#>" +
////				        "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
////				      //  "PREFIX nust:<"+Configuration.NS_RES+uriSuffix+"/>" +
////				        "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
////				        "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
////				        "PREFIX weprov:<"+Configuration.NS_WEPROV+">"+
////				        "PREFIX wedata:<"+Configuration.NS_RES+"workflow/"+">"+
////				        "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#>"+
////						"PREFIX prov:<http://www.w3.org/ns/prov#>"+
////
////
////									        "Construct {"
////									        + "wedata:abc provone:hasSubProgram ?program. ?program rdfs:label ?programId."
////									        + "?program weprov:host ?model. ?model rdfs:label ?modelId."
////									        + "?program provone:hasInPort ?inport. ?inport rdfs:label ?inportId."
////									        	+ "?inport provone:connectsTo ?channelIn. ?channelIn rdfs:label ?channelInId. "
////									        	+ "?inport provone:hasDefaultParam ?param. ?param rdfs:label ?paramId. ?param prov:value ?paramValue."
////									        + "?program provone:hasOutPort ?outport. ?outport rdfs:label ?outportId. "
////									        	+ "?outport provone:connectsTo ?channelOut. ?channelOut rdfs:label ?channelOutId. "
////									        + "?program provone:controlledBy ?controller. ?controller rdfs:label ?controllerId. "
////									        + "}"
////									        + " WHERE {" 
////									        + "wedata:abc rdf:type provone:Workflow;" 
////									        + "provone:hasSubProgram ?program. ?program rdfs:label ?programId."
////									        + "?program weprov:host ?model. ?model rdfs:label ?modelId."
////									        + "?program  provone:hasInPort	 ?inport. ?inport rdfs:label ?inportId."
////									        	+ "OPTIONAL {?inport provone:connectsTo ?channelIn. ?channelIn rdfs:label ?channelInId.} "
////									        	+ "OPTIONAL {?inport provone:hasDefaultParam ?param. ?param rdfs:label ?paramId."
////									        		+ "?param prov:value ?paramValue. }"
////									        + "?program provone:hasOutPort ?outport. ?outport rdfs:label ?outportId. "
////									        	+ "?outport provone:connectsTo ?channelOut. ?channelOut rdfs:label ?channelOutId. "
////									        + "OPTIONAL  {?program provone:controlledBy ?controller. ?controller rdfs:label ?controllerId. }"
////									        + "}";
////						
////				QueryExecution qexec = QueryExecutionFactory.create(sparql,Syntax.syntaxSPARQL_11, baseModel);
////				Model results = qexec.execConstruct();
////				
////				//results.listStatements(null, results.createProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasSubProgram"), null);
////				Property hasSubProgram = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasSubProgram");
////				Property hasInPorts = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasInPort");
////				Property hasOutPorts = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasOutPort");
////				Property connectsTo = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#connectsTo");
////				Property hasDefaultParam = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasDefaultParam");
////				Property host = results.getProperty(Configuration.NS_WEPROV+"host");
////				Property label = results.getProperty("http://www.w3.org/2000/01/rdf-schema#"+"label");
////				Property value = results.getProperty("http://www.w3.org/ns/prov#"+"value");
////				
////				
////				
////				//Get workflow Id
////				
////				Resource workflowId = (Resource) results.listSubjectsWithProperty(hasSubProgram).next();
////				resultObj.put("workflowId", workflowId.toString());
////				resultObj.put("userId", "");
////				resultObj.put("time", "");
////		
////				NodeIterator programIter = results.listObjectsOfProperty(hasSubProgram);
////				JSONArray programArr = new JSONArray();
////				
////				
////		        try {
////		            while (programIter.hasNext()) {
////		            	Resource program = (Resource) programIter.next();
////		                
////		                JSONObject _program = new JSONObject();
////		                _program.put("programId", results.listObjectsOfProperty(program, label).next());
////		                
////		                Resource modelId = (Resource) results.listObjectsOfProperty(program, host).next();
////		                _program.put("modelId", results.listObjectsOfProperty(modelId, label).next());
////		                
////		                
////		                System.out.println(program + "		" + modelId);
////		                
////		                NodeIterator inportsIterator = results.listObjectsOfProperty(program,  hasInPorts);
////		                JSONArray _inportsArr = new JSONArray();
////		                
////		                while(inportsIterator.hasNext()) {
////		                	Resource inport = (Resource)inportsIterator.next();
////		                	//System.out.println("\t" +inport);
////		                	JSONObject _inport = new JSONObject();
////		                	_inport.put("portId", results.listObjectsOfProperty(inport, label).next());
////		                	
////		                	NodeIterator channelIterator = results.listObjectsOfProperty((Resource) inport,  connectsTo);
////		                	if (channelIterator.hasNext()) {
////		                		while(channelIterator.hasNext()) {
////		                			Resource channel = (Resource)channelIterator.next();
////		                			System.out.println("\t\t" + channel);
////		                			_inport.put("channel", results.listObjectsOfProperty(channel, label).next());
////		                		}
////		                	} else {
////		                		NodeIterator paramIterator = results.listObjectsOfProperty((Resource) inport,  hasDefaultParam);
////		                		JSONArray params = new JSONArray();
////		                		while(paramIterator.hasNext()) {
////		                			Resource param = (Resource)paramIterator.next();
////		                			JSONObject _param = new JSONObject();
////		                			String paramId = (results.listObjectsOfProperty(param, label).next()).toString();
////		                			String paramValue = (results.listObjectsOfProperty(param, value).next()).toString();
////		                			_param.put("paramId", paramId);
////		                			_param.put("paramValue", "paramValue");
////		                			params.put(_param);
////		                		}
////		                		_inport.put("params", params);
////		                	}
////		                	
////		                	_inportsArr.put(_inport);
////		                }
////		                
////		                _program.put("inPorts", _inportsArr);
////		                
////		                NodeIterator outportsIterator = results.listObjectsOfProperty((Resource) program,  hasOutPorts);
////		                JSONArray _outportsArr = new JSONArray();
////		                
////		                while(outportsIterator.hasNext()) {
////		                	Resource port = (Resource)outportsIterator.next();
////		                	JSONObject _outport = new JSONObject();
////		                	_outport.put("portId", results.listObjectsOfProperty(port, label).next());
////		                	
////		                	System.out.println("\t" +port);
////		                	
////		                	NodeIterator channelIterator = results.listObjectsOfProperty((Resource) port,  connectsTo);
////		                	while(channelIterator.hasNext()) {
////			                	Resource channel = (Resource)channelIterator.next();
////			                	System.out.println("\t\t" + channel);
////			                	_outport.put("channel", results.listObjectsOfProperty(channel, label).next());
////		                	}
////		                	
////		                	_outportsArr.put(_outport);
////		                }
////		                _program.put("outPorts", _outportsArr);
////		                programArr.put(_program);
////		            }
////		        } finally {
////		            if (programIter != null)
////		            	programIter.close();
////		        }
////		        resultObj.put("program", programArr);
////		        System.out.println(resultObj);
////		        System.out.println(workflowId.toString());
////
	}
	
	
	public void getLiteral(String subject, String property) {
		Dataset baseDataSet= TDBFactory.createDataset(Configuration.getProperty(Configuration.STORE_PATH));
		Model provModel = baseDataSet.getNamedModel(Configuration.NS_EVORES); 
		Statement stmt = provModel.getRequiredProperty(provModel.getResource(subject), provModel.getProperty(property));
		System.out.println(stmt.getSubject().toString() + "\t" + stmt.getPredicate().toString() + "\t" +stmt.getLiteral());
	}
	
}
