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


import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.store.TripleStore;

import org.apache.jena.query.QuerySolution;

public class SPARQLQueryTest {

	public static void main(String[] args) {
				
				Dataset baseDataSet= TDBFactory.createDataset(Configuration.getProperty(Configuration.STORE_PATH));
				Model baseModel = baseDataSet.getDefaultModel(); 
				
			
//				String sparql = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
//						"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
//				        "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
//				        "PREFIX weprov:<http://weprov.csiro.au/workflow/>"+
//				        //"PREFIX weprov:<http://weprov.csiro.au/>"+
//				        "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#>"+
//						"PREFIX prov:<http://www.w3.org/ns/prov#>"+
//
//				        "SELECT DISTINCT ?programId ?inportId ?outportId ?channelInId ?paramId ?paramValue ?outportId ?channelOutId ?controllerId WHERE {" +
//				        "weprov:abc rdf:type ?type;"
//				         +"	provone:hasSubProgram ?program. ?program rdfs:label ?programId."
//				        + "?program provone:hasInPort ?inport. ?inport rdfs:label ?inportId."
//				        	+ "OPTIONAL {?inport provone:connectsTo ?channelIn. ?channelIn rdfs:label ?channelInId.} "
//				        	+ "OPTIONAL {?inport provone:hasDefaultParam ?param. ?param rdfs:label ?paramId."
//				        		+ "?param prov:value ?paramValue. }"
//				        + "?program provone:hasOutPort ?outport. ?outport rdfs:label ?outportId. "
//				        	+ "?outport provone:connectsTo ?channelOut. ?channelOut rdfs:label ?channelOutId. "
//					        + "OPTIONAL  {?program provone:controlledBy/rdfs:label ?controllerId. }"
//				        + "}";
				
//				String sparql = "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#>"
//				+ "select ?begin ?midI  ?midJ ?end "
//						+ "where {"
//						+ "?begin provone:controlledBy+ ?counter ."
//						+ "?counter provone:controls+ ?midI ."
//						+ "FILTER NOT EXISTS { [] provone:controls ?begin }"
//						+ "?midI provone:controlledBy* ?midJ ."
//						+ "?midJ provone:controls* ?end ."
//						+ "FILTER NOT EXISTS { ?end provone:controlledBy [] }"
//						+ "}"
//						+ "group by ?begin ?end ?midI ?midJ "
//						+ "order by ?begin ?end count(?counter)";
//				
//				QueryExecution qexec = QueryExecutionFactory.create(sparql,Syntax.syntaxSPARQL_11, baseModel);
//				ResultSet results = qexec.execSelect();
				//ResultSetFormatter
				
				String sparql ="PREFIX ifkm:<http://purl.org/ontology/ifkm#>" +
				        "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
				      //  "PREFIX nust:<"+Configuration.NS_RES+uriSuffix+"/>" +
				        "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
				        "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
				        "PREFIX weprov:<"+Configuration.NS_WEPROV+">"+
				        "PREFIX wedata:<"+Configuration.NS_RES+"workflow/"+">"+
				        "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#>"+
						"PREFIX prov:<http://www.w3.org/ns/prov#>"+


									        "Construct {"
									        + "wedata:abc provone:hasSubProgram ?program. ?program rdfs:label ?programId."
									        + "?program weprov:host ?model. ?model rdfs:label ?modelId."
									        + "?program provone:hasInPort ?inport. ?inport rdfs:label ?inportId."
									        	+ "?inport provone:connectsTo ?channelIn. ?channelIn rdfs:label ?channelInId. "
									        	+ "?inport provone:hasDefaultParam ?param. ?param rdfs:label ?paramId. ?param prov:value ?paramValue."
									        + "?program provone:hasOutPort ?outport. ?outport rdfs:label ?outportId. "
									        	+ "?outport provone:connectsTo ?channelOut. ?channelOut rdfs:label ?channelOutId. "
									        + "?program provone:controlledBy ?controller. ?controller rdfs:label ?controllerId. "
									        + "}"
									        + " WHERE {" 
									        + "wedata:abc rdf:type provone:Workflow;" 
									        + "provone:hasSubProgram ?program. ?program rdfs:label ?programId."
									        + "?program weprov:host ?model. ?model rdfs:label ?modelId."
									        + "?program  provone:hasInPort	 ?inport. ?inport rdfs:label ?inportId."
									        	+ "OPTIONAL {?inport provone:connectsTo ?channelIn. ?channelIn rdfs:label ?channelInId.} "
									        	+ "OPTIONAL {?inport provone:hasDefaultParam ?param. ?param rdfs:label ?paramId."
									        		+ "?param prov:value ?paramValue. }"
									        + "?program provone:hasOutPort ?outport. ?outport rdfs:label ?outportId. "
									        	+ "?outport provone:connectsTo ?channelOut. ?channelOut rdfs:label ?channelOutId. "
									        + "OPTIONAL  {?program provone:controlledBy ?controller. ?controller rdfs:label ?controllerId. }"
									        + "}";
						
				QueryExecution qexec = QueryExecutionFactory.create(sparql,Syntax.syntaxSPARQL_11, baseModel);
				Model results = qexec.execConstruct();
				
				//results.listStatements(null, results.createProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasSubProgram"), null);
				Property hasSubProgram = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasSubProgram");
				Property hasInPorts = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasInPort");
				Property hasOutPorts = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasOutPort");
				Property connectsTo = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#connectsTo");
				Property hasDefaultParam = results.getProperty("http://purl.dataone.org/provone/2015/01/15/ontology#hasDefaultParam");
				Property host = results.getProperty(Configuration.NS_WEPROV+"host");
				
				NodeIterator programIter = results.listObjectsOfProperty(hasSubProgram);
		        try {
		            while (programIter.hasNext()) {
		                RDFNode program = programIter.next();
		               
		                Resource modelId = (Resource) results.listObjectsOfProperty((Resource)program, host).next();
		                
		                System.out.println(program + "		" + modelId);
		                
		                NodeIterator inportsIterator = results.listObjectsOfProperty((Resource) program,  hasInPorts);
		                
		                while(inportsIterator.hasNext()) {
		                	Resource inport = (Resource)inportsIterator.next();
		                	System.out.println("\t" +inport);
		                	
		                	NodeIterator channelIterator = results.listObjectsOfProperty((Resource) inport,  connectsTo);
		                	if (channelIterator.hasNext()) {
		                		while(channelIterator.hasNext()) {
		                			Resource channel = (Resource)channelIterator.next();
		                			System.out.println("\t\t" + channel);
		                		}
		                	} else {
		                		NodeIterator paramIterator = results.listObjectsOfProperty((Resource) inport,  hasDefaultParam);
		                		while(paramIterator.hasNext()) {
		                			Resource param = (Resource)paramIterator.next();
		                			System.out.println("\t\t" + param);
		                		}
		                	}
		                }
		                
		                NodeIterator outportsIterator = results.listObjectsOfProperty((Resource) program,  hasOutPorts);
		                
		                while(outportsIterator.hasNext()) {
		                	Resource port = (Resource)outportsIterator.next();
		                	System.out.println("\t" +port);
		                	
		                	NodeIterator channelIterator = results.listObjectsOfProperty((Resource) port,  connectsTo);
		                	while(channelIterator.hasNext()) {
			                	Resource channel = (Resource)channelIterator.next();
			                	System.out.println("\t\t" + channel);
		                	}	
		                }	                
		            }
		        } finally {
		            if (programIter != null)
		            	programIter.close();
		        }
				
				
//				  ArrayList<ProgramBean> programs= new ArrayList<ProgramBean>();
//	              while (results.hasNext()){
//		        	
//	            	  QuerySolution qs = results.next();
//	            	
//	            	  String program = qs.getLiteral("?programId").getLexicalForm();
//	            	  if(programs)
//	            	  
//	            	  System.out.println(outport);
//	              }	
	}
	
}
