/* Copyright (c) 2020, CSIRO. All rights reserved.
 * Use is subject to license terms.
 */

package com.csiro.webservices.rest;

import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.store.*;

import java.io.ByteArrayInputStream;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;



/**
 * A generic REST service
 * @author Anila Butt
 */

public abstract class GenericService extends LoggerService {

	/** Default triple store */
	protected TripleStore store;
	
	//private Model tdbModel =  TDBFactory.createModel(Configuration.getProperty(Configuration.STORE_PATH));
	/**
	 * Initializes this service and logging.
	 * 
	 * @see {@link LoggerService#LoggerService(String)}
	 */
	public GenericService(String loggerName) {
		super(loggerName);
		store = TripleStore.getDefaultStore();
	}
	
	/**
	 * Adds the input RDF into the default triple store
	 * @param rdfdump RDF triples
	 * @param lang Serialization language of the input RDF
	 * @see {@link com.hp.hpl.jena.rdf.model.Model#read(java.io.InputStream, String, String)}
	 */
	public String add(String rdfdump, String lang) {
		Model model = _readModel(rdfdump, lang, true);
		//Model modifiedModel = removeDuplicate(model);
		//Model finalModel = removeStoredResources(modifiedModel);
		long tripleCount = store.insert(model);
		return "" + tripleCount;
	}
	
	public String add(Model _model) {
		long tripleCount = store.insert(_model);
		return "" + tripleCount;
	}
	
	public boolean contains(Resource res) {
		return store.exists(res);
	}
	
	/**
	 * get the input RDF from the default triple store
	 * @param className: type of resourceId
	 * @param resourceId: id of particular type.
	 * @return Description of particular resourceId
	 */
	public String getResource(String className, String resourceId) {
		String query = "DESCRIBE <http://ifkm.nust.edu.pk/"+className+"/" + resourceId + ">";
		return store.execDescribe(query, OntMediaType.LANG_XML);
	}
	
	/**
	 * Retrieves RDF as JSON for the specified resource
	 * 
	 * @param resourceType Resource class name as defined in IFKM ontology
	 * @param resourceId Relative id of the resource
	 * @return RDF resultset as JSON
	 */
	public String getResourceAsJSON(String resourceType, String resourceId) {
		return getResourceAsJSON(resourceType, resourceType.toLowerCase(), resourceId);
	}
	
	public String getWorkflowAsJSON(String workflowId) {
		String sparql ="PREFIX ifkm:<http://purl.org/ontology/ifkm#>" +
        "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
      //  "PREFIX nust:<"+Configuration.NS_RES+uriSuffix+"/>" +
        "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
        "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
        "PREFIX weprov:<"+Configuration.NS_WEPROV+">"+
        "PREFIX wedata:<"+Configuration.NS_RES+"workflow/"+">"+
        "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#>"+
		"PREFIX prov:<http://www.w3.org/ns/prov#>"+


					      "SELECT DISTINCT ?programId ?inportId ?outportId ?channelInId ?paramId ?paramValue ?channelOutId ?controllerId "
					        + " WHERE {" 
					        + "wedata:abc rdf:type provone:Workflow;" 
					        + "provone:hasSubProgram ?program. ?program rdfs:label ?programId."
					        + "?program provone:hasInPort ?inport. ?inport rdfs:label ?inportId."
					        	+ "OPTIONAL {?inport provone:connectsTo ?channelIn. ?channelIn rdfs:label ?channelInId.} "
					        	+ "OPTIONAL {?inport provone:hasDefaultParam ?param. ?param rdfs:label ?paramId."
					        		+ "?param prov:value ?paramValue. }"
					        + "?program provone:hasOutPort ?outport. ?outport rdfs:label ?outportId. "
					        	+ "?outport provone:connectsTo ?channelOut. ?channelOut rdfs:label ?channelOutId. "
					        + "OPTIONAL  {?program provone:controlledBy/rdfs:label ?controllerId. }"
					        + "}";
		
        logger.info("Prepared SPARQL query successfully");
		logger.info(sparql);
		TripleStore store = TripleStore.getDefaultStore();
		return store.execSelect(sparql);
	}
	
	
	public Model getWorkflowAsModel(String workflowId) {
		String sparql ="PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
		        "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
		        "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+
		        "PREFIX weprov:<"+Configuration.NS_WEPROV+">"+
		        "PREFIX wedata:<"+Configuration.NS_RES+"workflow/"+">"+
		        "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#>"+
				"PREFIX prov:<http://www.w3.org/ns/prov#>"+

		        "Construct {"
		        + "wedata:"+workflowId+ " rdf:type provone:Workflow; rdfs:label ?label. " 
		        + "wedata:"+workflowId+ " provone:hasSubProgram ?program. ?program rdfs:label ?programId. ?program rdf:type ?programType."
		        + "?program weprov:host ?model. ?model rdfs:label ?modelId. ?model rdf:type ?modelType."
		        + "?program provone:hasInPort ?inport. ?inport rdfs:label ?inportId. ?inport rdf:type ?inPortType."
		        	+ "?inport provone:connectsTo ?channelIn. ?channelIn rdfs:label ?channelInId. ?channelIn rdf:type ?channelInType."
		        	+ "?inport provone:hasDefaultParam ?param. ?param rdfs:label ?paramId. ?param prov:value ?paramValue. ?param rdf:type ?paramType."
		        + "?program provone:hasOutPort ?outport. ?outport rdfs:label ?outportId. ?outport rdf:type ?outPortType."
		        	+ "?outport provone:connectsTo ?channelOut. ?channelOut rdfs:label ?channelOutId. ?channelOut rdf:type ?channelOutType."
		        + "?program provone:controlledBy ?controller. ?controller rdfs:label ?controllerId. ?controller rdf:type ?controllerType. ?controller provone:controls ?anotherProgram."
		        + "}"
		        + " WHERE {" 
		        + "wedata:"+workflowId+ " rdf:type provone:Workflow; rdfs:label ?label. " 
		        + " OPTIONAL { wedata:"+workflowId+"  provone:hasSubProgram ?program. ?program rdfs:label ?programId. ?program rdf:type ?programType."
		        + "?program weprov:host ?model. ?model rdfs:label ?modelId. ?model rdf:type ?modelType."
		        + "?program  provone:hasInPort	 ?inport. ?inport rdfs:label ?inportId. ?inport rdf:type ?inPortType."
		        	+ "OPTIONAL {?inport provone:connectsTo ?channelIn. ?channelIn rdfs:label ?channelInId. ?channelIn rdf:type ?channelInType.} "
		        	+ "OPTIONAL {?inport provone:hasDefaultParam ?param. ?param rdfs:label ?paramId. ?param rdf:type ?paramType."
		        		+ "?param prov:value ?paramValue. }"
		        + "?program provone:hasOutPort ?outport. ?outport rdfs:label ?outportId. ?outport rdf:type ?outPortType."
		        	+ "?outport provone:connectsTo ?channelOut. ?channelOut rdfs:label ?channelOutId. ?channelOut rdf:type ?channelOutType."
		        + "OPTIONAL  {?program provone:controlledBy ?controller. "
		        	+ "?controller rdfs:label ?controllerId. "
		        	+ "?controller rdf:type ?controllerType."
		        	+ "?controller provone:controls ?anotherProgram. } } "
		        + "}";
		
        logger.info("Prepared SPARQL query successfully");
		logger.info(sparql);
		TripleStore store = TripleStore.getDefaultStore();
		return store.execConstruct(sparql);
	}
	
	public String getResourceAsJSON(String resourceType, String uriSuffix, String resourceId) {
		String sparql ="PREFIX ifkm:<http://purl.org/ontology/ifkm#>" +
        "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
        "PREFIX nust:<"+Configuration.NS_RES+uriSuffix+"/>" +
        "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
        "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+

        "SELECT DISTINCT ?property ?propLabel ?value ?label ?display WHERE {" +
        "ifkm:"+resourceType+" rdfs:subClassOf ?restriction." +
        "?restriction owl:onProperty ?property." +
        "?property ifkm:z-index ?index." +
        " OPTIONAL {?property ifkm:display ?display.}" +
        "?property rdfs:label ?propLabel." +
        "OPTIONAL {nust:"+resourceId+" ?property ?value." +
        "OPTIONAL {?value ?propType ?label. FILTER ((?propType = ifkm:title)||(?propType = rdfs:label))}}." +
        //only for publication authors
       // "OPTIONAL {nust:"+resourceId+" ifkm:author ?value. ?value ?prop ?personId. ?personId ifkm:name ?name. ?personId rdf:type ifkm:Person.}" +
        "FILTER (?index >= 0 && (bound(?value))) "+
        "} ORDER BY (?index)" ;
		
        logger.info("Prepared SPARQL query successfully");
		logger.info(sparql);
		TripleStore store = TripleStore.getDefaultStore();
		return store.execSelect(sparql);
	}
	
	public String getResourceListAsJSON(String resourceType) {

		String sparql ="PREFIX ifkm:<http://purl.org/ontology/ifkm#>" +
        "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
        "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
        "PREFIX owl:<http://www.w3.org/2002/07/owl#>"+

        "SELECT DISTINCT ?uri ?title WHERE {" +
        "?uri rdf:type ifkm:"+resourceType+"." +
        "?uri ifkm:title ?title. } " +
        "ORDER BY ASC(?title) " ;
		
        logger.info("Prepared SPARQL query successfully");
		logger.info(sparql);
		TripleStore store = TripleStore.getDefaultStore();
		return store.execSelect(sparql);
	}
	
	/**
	 * Adds a single statement into default triple store
	 * 
	 * @param rdfdump Input RDF statement
	 * @param lang Serialization language of the input RDF
	 * @param deleteExisting If true, existing (subject, property) combinations will be deleted. 
	 */
	public void addStatement(String rdfdump, String lang, boolean deleteExisting) {
		Model model = _readModel(rdfdump, lang, true);
		if(deleteExisting) {
			StmtIterator iter = model.listStatements();
			while(iter.hasNext()) {
				Statement stmt = iter.next();
				if((stmt.getPredicate().toString()).equalsIgnoreCase("http://purl.org/ontology/ifkm#playRole")){					
				}
				else {
				store.remove(stmt.getSubject(), stmt.getPredicate(), null);
				}
			}
		}
		store.insert(model);
	}
	
	
	/**
	 * Adds a single statement into default triple store
	 * 
	 * @param rdfdump Input RDF statement
	 * @param lang Serialization language of the input RDF
	 * @param deleteExisting If true, existing (subject, property) combinations will be deleted. 
	 */
	public void removeStatements(String id) {
		Model deleteModel = TDBFactory.createDataset().getDefaultModel();
		Resource rs = deleteModel.getResource(id);
		store.remove(null, null, rs);
		store.remove(rs, null, null);
	}
	/**
	 * Remove Model from default triple store
	 * 
	 * @param rdfdump Input RDF statement
	 * @param lang Serialization language of the input RDF
	 * @param deleteExisting If true, existing (subject, property) combinations will be deleted. 
	 */
	public void removeModel(String rdfdump, String lang) {
		Model model = _readModel(rdfdump, lang);
		store.remove(model);	
		}
	
	/**
	 * Retrieves list of typed resources/instances
	 * 
	 * @param type RDF type of the resources
	 * @param lang Serialization format of the response
	 * @return Description of all resources in specified serialization format
	 */
	public String getResourcesList(String type) {
		String query = "PREFIX ifkm:<"+Configuration.NS_WEPROV+">" +
			"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
			"DESCRIBE ?instance "+
			"WHERE "+
			"{?instance rdf:type ifkm:"+type+".}";
  		return store.execDescribe(query, OntMediaType.LANG_XML);
	}
	
	/**
	 * Creates an RDF Model object from the given RDF dump
	 * 
	 * @param rdfdump Input RDF data
	 * @param lang Serialization language of the input RDF
	 * @return Jena model object
	 * @deprecated use {@link #_readModel(String, String, boolean)}
	 */
	protected final Model _readModel(String rdfdump, String lang) {
		return _readModel(rdfdump, lang, true);
	}
	
	/**
	 * Creates an RDF Model object from the given RDF dump
	 * 
	 * @param rdfdump Input RDF data
	 * @param lang Serialization language of the input RDF
	 * @param unescapeHtml If <code>true</code>, unescapes literals containing entity escapes to Unicode characters. 
	 * @return Jena model object
	 */
	protected final Model _readModel(String rdfdump, String lang, boolean unescapeHtml) {
		if(unescapeHtml) {
			rdfdump = StringEscapeUtils.unescapeHtml3(rdfdump);
		}
		ByteArrayInputStream bytein = new ByteArrayInputStream(rdfdump.getBytes());
		Model model = ModelFactory.createDefaultModel();
		model.read(bytein, null, lang);
		return model;
	}
	
//	/**
//	 * Identify duplicate resources from an input RDF Model
//	 * 
//	 * @param Input RDF data model 
//	 * @return Jena model object
//	 */
//	 private Model removeDuplicate(Model model) {
//
//	        String queryString ="PREFIX ifkm:<http://purl.org/ontology/ifkm#>" +
//	        "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
//	        "Select Distinct ?resource1 ?resource2 ?type WHERE {" +
//	        "?resource1 rdf:type ?type." +
//	        "?resource2 rdf:type ?type." +
//	        "?resource1 ifkm:name ?title." +
//	        "?resource2 ifkm:name ?title." +
//	        "FILTER (?resource1 != ?resource2) }";
//	        
//	        QueryExecution qexec = QueryExecutionFactory.create(queryString, model);
//	        
//	        try {
//	        	 ResultSet result = qexec.execSelect();
//	             while(result.hasNext()){
//	                	QuerySolution qs= result.next();
//	                	Resource resource1 = qs.getResource("resource1");
//	                  	Resource resource2 = qs.getResource("resource2");
//	                    mergeInstances(resource1, resource2, model);
//	                    replaceInstances(resource1, resource2, model);
//	             	}
//	        	} catch(Exception e) { }
//	            qexec.close();
//	        	return model;
//		}
//	
//	    private void mergeInstances(Resource newResource, Resource oldResource, Model model ) {
//
//	    	String queryString ="PREFIX ifkm:<http://purl.org/ontology/ifkm#>" +
//	   		"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
//	      	"Construct {<"+oldResource.toString()+"> ?prop ?role.}" +
//	      	"WHERE {<"+oldResource.toString()+"> ?prop ?role.}" ;
//
//	    	QueryExecution qexec = QueryExecutionFactory.create(queryString, model);
//	    	try {
//            	Model person = qexec.execConstruct();
//            	StmtIterator iter = person.listStatements();
//			    while(iter.hasNext()) {
//            		Statement stmt = iter.next();
//            		model.remove(stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
//            		model.add(newResource, stmt.getPredicate(), stmt.getObject());}
//	    	}catch(Exception e) {}
//	    }
//
//	     private void replaceInstances(Resource newResource, Resource oldResource , Model model ) {
//
//	    	String query ="PREFIX ifkm:<http://purl.org/ontology/ifkm#>" +
//	      	"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
//	      	"Construct {?subject ?predicate <"+oldResource+">.}" +
//	      	"WHERE {?subject ?predicate <"+oldResource+">.}" ;
//
//	    	QueryExecution qexec1 = QueryExecutionFactory.create(query, model);
//		    try {
//	        		Model _model = qexec1.execConstruct();
//	        		StmtIterator iter = _model.listStatements();
//	        		while(iter.hasNext()) {
//	        			Statement stmt = iter.next();
//	        			model.remove(stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
//	        			model.add(stmt.getSubject(), stmt.getPredicate(), newResource);
//	        		}
//	        		model.remove(oldResource, null, null);
//		        }catch(Exception e){}
//	    }
//	     
//	    private Model removeStoredResources (Model model) {
//	    
//	    	logger.info("In remove stored resources ");
//	    	String sparql ="PREFIX ifkm:<http://purl.org/ontology/ifkm#>" +
//				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"+
//				"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
//				"SELECT ?uri ?type ?label WHERE {"+
//				"?uri rdf:type ?type."+
//				"?uri ?prop ?label."+
//				"}";
//	    	QueryExecution qexec = QueryExecutionFactory.create(sparql, model);
//	        try {
//	        	ResultSet result = qexec.execSelect();
//	        	while(result.hasNext()){
//	        	  	logger.info("In remove stored resources ");
//	        		QuerySolution qs= result.next();
//	        		Resource oldResource = qs.getResource("uri");
//	        		System.out.println("Resource 1 of resultSet : " + oldResource.toString());
//	        		Resource type = qs.getResource("type");
//	        		System.out.println("Resource 1 of resultSet : " + type.toString());
//	        		Literal lab = qs.getLiteral("label");
//	        		String label = lab.getLexicalForm();
//	        		System.out.println("Resource 2 of resultSet : " + label);
//	        		String uri = labelBaseDetection(type.toString(), label);
//	        		if(uri.equals("")){
//	        		}else {
//	        			Resource newResource= model.getResource(uri);
//	        			replaceInstances(newResource, oldResource , model );
//	        		}	        			
//	            }
//	        } catch(Exception e){}
//	            
//	        qexec.close();
//	        return model;
//	    }
//	    private String labelBaseDetection(String type, String label) {
//	    	String output=null;
//	    	String sparql ="PREFIX ifkm:<http://purl.org/ontology/ifkm#> " +
//	    	"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "+
//	    	"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
//	    	"SELECT ?uri WHERE {"+
//	    	"?uri rdf:type <"+type+">."+
//	    	"?uri ?pred ?label." +
//	    	" FILTER (?label = \""+ label +"\"@en"+" || ?label="+"\""+ label +"\"^^<http://www.w3.org/2001/XMLSchema#string>)" +
//	    	"}";
//	    	QueryExecution qexec = QueryExecutionFactory.create(sparql, tdbModel);
//	    	try {
//	    		ResultSet result = qexec.execSelect();
//	    		while(result.hasNext()){
//	    			QuerySolution qs= result.next();
//	    			Resource uri = qs.getResource("uri");
//	    			System.out.println("Resource 1 of resultSet : " + uri.toString());
//	    			output=uri.toString();
//		        }
//	    	}catch(Exception e){}
//	    		return output;
//			}	    
	}