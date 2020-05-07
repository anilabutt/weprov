package com.csiro.webservices.rest;

import java.io.ByteArrayOutputStream;


import com.csiro.webservices.store.QuadStore;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class WeprovCompetencyQueries extends GenericService{

	public WeprovCompetencyQueries(String loggerName) {
		super(loggerName);
		// TODO Auto-generated constructor stub
	}

	
	/** Get the current version number of the workflow
	 * Current version is the number of revisions plus the first version
	 * Get revision count from evolution database and add one in the count */
	
	public String getCurrentVersionNumber(String workflowId) {
		String versionNumber="0";
		
		ResultSet rs = getRevisionCount(workflowId);
		while(rs.hasNext()) {
        	QuerySolution qs= rs.next();
        	String revisionCount = qs.getLiteral("rev").toString(); 
        	
        	int versionCount = Integer.parseInt(revisionCount) + 1;
        	versionNumber= versionCount+"";
		}   
		
		return versionNumber;
	}
	
	/** Get Generation Provenance */
	
	public String getGenerationProvenance(String workflowId) {
		
		String sparql ="PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"PREFIX dcterm:<http://purl.org/dc/terms/> " + 
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " + 
				"PREFIX prov:<http://www.w3.org/ns/prov#> " + 
				"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/> " + 				
				"CONSTRUCT { " + 
				"?creation a weprov:Creation; a ?activityType; prov:startedAtTime ?startTime. " + 
				"?creation prov:wasAssociatedWith ?agent. ?agent a ?agentType; foaf:name ?agentName. " + 
				"?generation a prov:Generation; prov:atTime ?genTime; prov:activity ?creation; prov:generated ?workflow. ?workflow prov:qualifiedGeneration ?generation; prov:wasGeneratedBy ?creation. ?aGeneration weprov:wasPartOf ?generation." + 
				"} " + 
				"FROM <http://weprov.csiro.au/> " + 
				"FROM <http://weprov.csiro.au/evolution/> "+
				"WHERE { " + 
				"?creation a weprov:Creation; a ?activityType; prov:startedAtTime ?startTime. " + 
				" OPTIONAL {?creation prov:wasAssociatedWith ?agent. ?agent a ?agentType; foaf:name ?agentName} " + 
				"?generation a prov:Generation; prov:atTime ?genTime; prov:activity ?creation; prov:generated ?workflowId.  " +
				" OPTIONAL {?aGeneration weprov:wasPartOf ?generation.} "+
				"?workflowId prov:qualifiedGeneration ?generation; prov:wasGeneratedBy ?creation."+
				"FILTER (?workflowId = ?workflow) " + 
				"{ SELECT ?workflow WHERE { <"+workflowId+">  (provone:hasSubProgram/provone:hasSubProgram)*  ?workflow.} }  " + 
				"}";
		
				logger.info("Prepared SPARQL query successfully");
				logger.info(sparql);
				
				QuadStore store = QuadStore.getDefaultStore();
				Model model = store.execConstruct(sparql);
				
				System.out.println(model.size());
				
				ByteArrayOutputStream byteout = new ByteArrayOutputStream();
				model.write(byteout, "TTL");
				logger.info("Model serialized successfully");
				String str = byteout.toString();
				
				return str;
	}
	
	
	/** CQ1: Identify different types of changes in a revision of the workflow.*/ 
	
	public String getCompleteProvenance(String workflowId) {
		
		String sparql ="PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"PREFIX dcterm:<http://purl.org/dc/terms/> " + 
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " + 
				"PREFIX prov:<http://www.w3.org/ns/prov#> " + 
				"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/> " + 				
				
				"CONSTRUCT { " + 
				"?creation a weprov:Creation; a ?activityType; prov:startedAtTime ?startTime. " + 
				"?creation prov:wasAssociatedWith ?agent. ?agent a ?agentType; foaf:name ?agentName. " + 
				"?generation a prov:Generation; prov:atTime ?genTime; prov:activity ?creation; prov:generated ?workflow. ?workflow prov:qualifiedGeneration ?generation; prov:wasGeneratedBy ?creation. ?aGeneration weprov:wasPartOf ?generation." + 
				"?modification a weprov:Modification; a ?activityType; prov:startedAtTime ?startTime. " + 
				"?modification prov:wasAssociatedWith ?agent. ?agent a ?agentType; foaf:name ?agentName. " + 
				"?renaming prov:activity ?modification; a weprov:Renaming; dcterm:title ?title; dcterm:description ?description; prov:agent ?renamedAgent. " + 
				"?revision a prov:Revision; prov:atTime ?revTime; weprov:revision ?revisionNo; prov:activity ?modification; prov:wasRevisionOf ?workflowId. ?workflowId prov:qualifiedRevision ?revision. ?revision weprov:wasPartOf ?pRevision.  " + 
				"?revision prov:hadGeneration ?gen. ?gen a prov:Generation; prov:atTime ?genTime; prov:activity ?modification. ?entity prov:qualifiedGeneration ?gen. " + 
				"?revision prov:hadInvalidation ?invalid. ?invalid a prov:Invalidation; prov:atTime ?invalidTime; prov:activity ?modification; prov:entity ?invalidEntity."+
				"} " + 				
				"FROM <http://weprov.csiro.au/> " + 
				"FROM <http://weprov.csiro.au/evolution/> "+
				"WHERE {  " + 
				"{ ?creation a weprov:Creation; a ?activityType; prov:startedAtTime ?startTime. OPTIONAL {?creation prov:wasAssociatedWith ?agent. ?agent a ?agentType; foaf:name ?agentName} ?generation a prov:Generation; prov:atTime ?genTime; prov:activity ?creation; prov:generated ?workflowId. OPTIONAL {?aGeneration weprov:wasPartOf ?generation.} ?workflowId prov:qualifiedGeneration ?generation; prov:wasGeneratedBy ?creation.  " + 
				"FILTER (?workflowId = ?workflow) { SELECT ?workflow WHERE { <http://weprov.csiro.au/workflow/urn:lsid:net.sf.taverna:wfDefinition:618ac202-acf6-4695-bdc6-ca0078be3649>  (provone:hasSubProgram/provone:hasSubProgram)*  ?workflow.} }  }  " + 
				"UNION { " + 
				"{?modification a weprov:Modification; a ?activityType; prov:startedAtTime ?startTime. OPTIONAL {?modification prov:wasAssociatedWith ?agent. ?agent a ?agentType; foaf:name ?agentName} OPTIONAL {?renaming prov:activity ?modification; a weprov:Renaming. OPTIONAL{?renaming dcterm:title ?title}. OPTIONAL{?renaming dcterm:description ?description}. OPTIONAL{?renaming prov:agent ?renamedAgent}} ?revision a prov:Revision; prov:atTime ?revTime; weprov:revision ?revisionNo; prov:activity ?modification; prov:wasRevisionOf ?workflowId. ?workflowId prov:qualifiedRevision ?revision. OPTIONAL {?revision weprov:wasPartOf ?pRevision}  OPTIONAL {?revision prov:hadGeneration ?gen. ?gen a prov:Generation; prov:atTime ?genTime; prov:activity ?modification. ?entity prov:qualifiedGeneration ?gen.} OPTIONAL {?revision prov:hadInvalidation ?invalid. ?invalid a prov:Invalidation; prov:atTime ?invalidTime; prov:activity ?modification; prov:entity ?invalidEntity.} } " + 
				"FILTER (?workflowId = ?workflow) { SELECT ?workflow WHERE { <http://weprov.csiro.au/workflow/urn:lsid:net.sf.taverna:wfDefinition:618ac202-acf6-4695-bdc6-ca0078be3649>  (provone:hasSubProgram/provone:hasSubProgram)*  ?workflow.} }} " + 
				"}";
		
		logger.info("Prepared SPARQL query successfully");
		logger.info(sparql);
		
		QuadStore store = QuadStore.getDefaultStore();
		Model model = store.execConstruct(sparql);
		
		System.out.println(model.size());
		
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		model.write(byteout, "TTL");
		logger.info("Model serialized successfully");
		String str = byteout.toString();
		
		return str;
		
	}
	
	
	/** CQ2: Identify different types of changes in a revision of the workflow.*/ 
	
	public String getRevisionProvenance(String workflowId, String revision) {
		
		Model model = ModelFactory.createDefaultModel();
		
		String sparql ="PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"PREFIX dcterm:<http://purl.org/dc/terms/> " + 
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " + 
				"PREFIX prov:<http://www.w3.org/ns/prov#> " + 
				"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/> " + 
				
				"CONSTRUCT {?modification a weprov:Modification; a ?activityType; prov:startedAtTime ?startTime. " + 
				"?modification prov:wasAssociatedWith ?agent. ?agent a ?agentType; foaf:name ?agentName. " + 
				"?renaming prov:activity ?modification; a weprov:Renaming; dcterm:title ?title; dcterm:description ?description; prov:agent ?renamedAgent. " + 
				"?revision a prov:Revision; prov:atTime ?revTime; weprov:revision \""+ revision + "\"; prov:activity ?modification; prov:wasRevisionOf ?workflowId. ?workflowId prov:qualifiedRevision ?revision. ?revision weprov:wasPartOf ?pRevision.  " + 
				"?revision prov:hadGeneration ?gen. ?gen a prov:Generation; prov:atTime ?genTime; prov:activity ?modification. ?entity prov:qualifiedGeneration ?gen. " + 
				"?revision prov:hadInvalidation ?invalid. ?invalid a prov:Invalidation; prov:atTime ?invalidTime; prov:activity ?modification; prov:entity ?invalidEntity.} " + 
				"FROM <http://weprov.csiro.au/> " + 
				"FROM <http://weprov.csiro.au/evolution/> "+
				"WHERE { " + 
				"?modification a weprov:Modification; a ?activityType; prov:startedAtTime ?startTime. " + 
				"OPTIONAL {?modification prov:wasAssociatedWith ?agent. ?agent a ?agentType; foaf:name ?agentName} " + 
				"OPTIONAL {?renaming prov:activity ?modification; a weprov:Renaming. OPTIONAL{?renaming dcterm:title ?title}. OPTIONAL{?renaming dcterm:description ?description}. OPTIONAL{?renaming prov:agent ?renamedAgent}} " + 
				"?revision a prov:Revision; prov:atTime ?revTime; weprov:revision \""+ revision + "\"; prov:activity ?modification; prov:wasRevisionOf ?workflowId. ?workflowId prov:qualifiedRevision ?revision. OPTIONAL {?revision weprov:wasPartOf ?pRevision}  " + 
				"OPTIONAL {?revision prov:hadGeneration ?gen. ?gen a prov:Generation; prov:atTime ?genTime; prov:activity ?modification. ?entity prov:qualifiedGeneration ?gen.} " + 
				"OPTIONAL {?revision prov:hadInvalidation ?invalid. ?invalid a prov:Invalidation; prov:atTime ?invalidTime; prov:activity ?modification; prov:entity ?invalidEntity.} " + 
				"FILTER (?workflowId = ?workflow) " + 
				" { SELECT ?workflow  " + 
				"   WHERE { <"+workflowId+">  (provone:hasSubProgram/provone:hasSubProgram)*  ?workflow.} }  " + 
				"}";
		
				logger.info("Prepared SPARQL query successfully");
				logger.info(sparql);
				
				QuadStore store = QuadStore.getDefaultStore();
				model = store.execConstruct(sparql);
				
				System.out.println(model.size());
				
				ByteArrayOutputStream byteout = new ByteArrayOutputStream();
				model.write(byteout, "TTL");
				logger.info("Model serialized successfully");
				String str = byteout.toString();
				
				return str;
	}
	
	
/** CQ4:  Report the reason(s) of divergent results of two executions of a workflow.*/ 
	
	public String getAllRevisionsBetweenADuration(String workflowId, String firstTime, String lastTime) {
		
		Model model = ModelFactory.createDefaultModel();
		
		String sparql ="PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"PREFIX dcterm:<http://purl.org/dc/terms/> " + 
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " + 
				"PREFIX prov:<http://www.w3.org/ns/prov#> " + 
				"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/> " + 
				
				"CONSTRUCT {?modification a weprov:Modification; a ?activityType; prov:startedAtTime ?startTime. " + 
				"?modification prov:wasAssociatedWith ?agent. ?agent a ?agentType; foaf:name ?agentName. " + 
				"?renaming prov:activity ?modification; a weprov:Renaming; dcterm:title ?title; dcterm:description ?description; prov:agent ?renamedAgent. " + 
				"?revision a prov:Revision; prov:atTime ?revTime; weprov:revision ?revisionNo; prov:activity ?modification; prov:wasRevisionOf ?workflowId. ?workflowId prov:qualifiedRevision ?revision. ?revision weprov:wasPartOf ?pRevision.  " + 
				"?revision prov:hadGeneration ?gen. ?gen a prov:Generation; prov:atTime ?genTime; prov:activity ?modification. ?entity prov:qualifiedGeneration ?gen. " + 
				"?revision prov:hadInvalidation ?invalid. ?invalid a prov:Invalidation; prov:atTime ?invalidTime; prov:activity ?modification; prov:entity ?invalidEntity.} " + 
				"FROM <http://weprov.csiro.au/> " + 
				"FROM <http://weprov.csiro.au/evolution/> "+
				"WHERE { " + 
				"?modification a weprov:Modification; a ?activityType; prov:startedAtTime ?startTime.  " + 
				"OPTIONAL {?modification prov:wasAssociatedWith ?agent. ?agent a ?agentType; foaf:name ?agentName}  " + 
				"OPTIONAL {?renaming prov:activity ?modification; a weprov:Renaming. OPTIONAL {?renaming dcterm:title ?title}. OPTIONAL{?renaming dcterm:description ?description}. OPTIONAL{?renaming prov:agent ?renamedAgent}}  " + 
				"?revision a prov:Revision; prov:atTime ?time; weprov:revision ?revisionNo; prov:activity ?modification; prov:wasRevisionOf ?workflowId. ?workflowId prov:qualifiedRevision ?revision.  " + 
				"OPTIONAL {?revision weprov:wasPartOf ?pRevision}   " + 
				"OPTIONAL {?revision prov:hadGeneration ?gen. ?gen a prov:Generation; prov:atTime ?genTime; prov:activity ?modification. ?entity prov:qualifiedGeneration ?gen.}  " + 
				"OPTIONAL {?revision prov:hadInvalidation ?invalid. ?invalid a prov:Invalidation; prov:atTime ?invalidTime; prov:activity ?modification; prov:entity ?invalidEntity.}   " + 
				"FILTER ((?time >= \""+firstTime+"\"^^xsd:dateTime) && (?time <= \""+lastTime+"\"^^xsd:dateTime) ) " + 
				"FILTER (?workflowId = ?workflow) " + 
				"{ SELECT ?workflow  " + 
				"WHERE { <"+workflowId+">  (provone:hasSubProgram/provone:hasSubProgram)*  ?workflow.} }  " + 
				"}";
		
				logger.info("Prepared SPARQL query successfully");
				logger.info(sparql);
				
				QuadStore store = QuadStore.getDefaultStore();
				model = store.execConstruct(sparql);
				
				System.out.println(model.size());
				
				ByteArrayOutputStream byteout = new ByteArrayOutputStream();
				model.write(byteout, "TTL");
				logger.info("Model serialized successfully");
				String str = byteout.toString();
				
				return str;
	}
	
	/** CQ5:  Which one is the most unstable component of the workflow? */ 
	
	public String getWorkflowUnstableComponents(String workflowId) {
		
		Model model = ModelFactory.createDefaultModel();
		
		String sparql ="PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"PREFIX prov:<http://www.w3.org/ns/prov#> " + 
				"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
				"CONSTRUCT {?workflowId weprov:hasRevisions ?revisions} " + 
				"FROM <http://weprov.csiro.au/evolution/> "+
				"WHERE { " + 
				"	SELECT ?workflowId (max(?revCount) As ?revisions) " + 
				"	FROM <http://weprov.csiro.au/> " + 
				"	FROM <http://weprov.csiro.au/evolution/> "+
				"	WHERE { " + 
				"		?modification a weprov:Modification. " + 
				"		?revision a prov:Revision; weprov:revision ?revCount; prov:activity ?modification; prov:wasRevisionOf ?workflowId.  " + 
				"	FILTER (?workflowId = ?workflow) " + 
				"		{ "+
				"			SELECT ?workflow  " + 
				"   		WHERE { <"+workflowId+">  (provone:hasSubProgram/provone:hasSubProgram)*  ?workflow.} "+
				"		}  " + 
				"	  } Group By (?workflowId)  " + 
				"}";
		
				logger.info("Prepared SPARQL query successfully");
				logger.info(sparql);
				
				QuadStore store = QuadStore.getDefaultStore();
				model = store.execConstruct(sparql);
				
				System.out.println(model.size());
				
				ByteArrayOutputStream byteout = new ByteArrayOutputStream();
				model.write(byteout, "TTL");
				logger.info("Model serialized successfully");
				String str = byteout.toString();
				
				return str;
	}
	
	/** CQ6: Identify all the agents who have participated in the design of the workflow.*/ 
	
	public String getWorkflowParticipants(String workflowId) {
		
		Model model = ModelFactory.createDefaultModel();
		
		String sparql ="PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"PREFIX prov:<http://www.w3.org/ns/prov#> " + 
				"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
				"Construct " +
				"{ ?activity a ?type; prov:wasAssociatedWith ?agentId. ?agentId foaf:name ?agent. } " + 
				"FROM <http://weprov.csiro.au/evolution/> "+
				"Where{ " + 
				"    VALUES ?type {weprov:Creation weprov:Invalidation weprov:Modification} " + 
				"    ?activity a ?type; " + 
				"    prov:wasAssociatedWith ?agentId. ?agentId foaf:name ?agent. " + 
				"	 { 	{<"+workflowId+"> prov:wasGeneratedBy ?activity.} UNION " +
				"		{<"+workflowId+"> prov:wasInvalidatedBy ?activity.} UNION " +
				"		{<"+workflowId+"> prov:qualifiedRevision/prov:activity ?activity.} " +
				"	 }" + 
				"}";
		
				logger.info("Prepared SPARQL query successfully");
				logger.info(sparql);
				
				QuadStore store = QuadStore.getDefaultStore();
				model = store.execConstruct(sparql);
				
				System.out.println(model.size());
				
				ByteArrayOutputStream byteout = new ByteArrayOutputStream();
				model.write(byteout, "TTL");
				logger.info("Model serialized successfully");
				String str = byteout.toString();
				
				return str;
	}
	
	/**  CQ7: Who is responsible for a change (revision) in the workflow */
	
	public String getWorkflowRevisior(String workflowId, String revision) {
		String sparql = "PREFIX prov:<http://www.w3.org/ns/prov#> " + 
				"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/> " + 
				"Construct { ?revision prov:wasAssociatedWith ?agent. ?agent foaf:name ?agent. } " + 
				"FROM <http://weprov.csiro.au/evolution/> "+
				"WHERE { " + 
				"?modification a weprov:Modification. " + 
				"OPTIONAL {?modification prov:wasAssociatedWith ?agent. ?agent a ?agentType; foaf:name ?agentName} " + 
				"?revision a prov:Revision; weprov:revision \""+ revision + "\"; prov:activity ?modification; prov:wasRevisionOf <"+workflowId+">. " + 
				"}";
		
		logger.info("Prepared SPARQL query successfully");
		logger.info(sparql);
		
		QuadStore store = QuadStore.getDefaultStore();
		Model model = store.execConstruct(sparql);
		
		System.out.println(model.size());
		
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		model.write(byteout, "N3");
		logger.info("Model serialized successfully");
		String str = byteout.toString();
		
		return str;
		
	}
	
	/** CQ8: Who participated in the first version of the workflow?**/
	
	public String getWorkflowCreator(String workflowId) {
		String sparql = "PREFIX prov:<http://www.w3.org/ns/prov#> " + 
				"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/>  " + 
				"CONSTRUCT { ?generation prov:wasAssociatedWith ?agent; prov:generated <"+workflowId+">. ?agent foaf:name ?agent. } " + 
				"FROM <http://weprov.csiro.au/evolution/> "+
				"WHERE {  " + 
				"?creation a weprov:Creation.  " + 
				"?generation a prov:Generation; prov:activity ?creation; prov:generated <"+workflowId+">.  " + 
				"OPTIONAL {?creation prov:wasAssociatedWith ?agent. ?agent foaf:name ?agentName.}  " + 
				"}";
		
		logger.info("Prepared SPARQL query successfully");
		logger.info(sparql);
		
		QuadStore store = QuadStore.getDefaultStore();
		Model model = store.execConstruct(sparql);
		
		System.out.println(model.size());
		
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		model.write(byteout, "N3");
		logger.info("Model serialized successfully");
		String str = byteout.toString();
		
		return str;
		
	}
	
	/**CQ9: Find all workflows (or versions of a workflow) where an agent has participated.*/
			
		public String getWorkflowsForAgent(String name) {
			String sparql = "PREFIX prov:<http://www.w3.org/ns/prov#> " + 
					"		PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
					"		PREFIX foaf:<http://xmlns.com/foaf/0.1/>  " + 
					"		CONSTRUCT {?workflowId prov:wasAttributedTo ?agentId. ?agentId foaf:name ?name.} " + 
					"		FROM <http://weprov.csiro.au/evolution/> "+
					"		WHERE { " +
					" 		{?workflowId prov:wasGeneratedBy|prov:wasInvalidatedBy ?activity.} UNION "+
					"		{?workflowId (prov:qualifiedDerivation|prov:qualifiedRevision)/prov:activity ?activity.} " + 
					"		?activity prov:wasAssociatedWith ?agentId. ?agentId foaf:name ?name. " + 
					"		FILTER regex(?name, \""+name+"\", \"i\") " + 
					"		}";
			
			logger.info("Prepared SPARQL query successfully");
			logger.info(sparql);
			
			QuadStore store = QuadStore.getDefaultStore();
			Model model = store.execConstruct(sparql);
			
			System.out.println(model.size());
			
			ByteArrayOutputStream byteout = new ByteArrayOutputStream();
			model.write(byteout, "N3");
			logger.info("Model serialized successfully");
			String str = byteout.toString();
			
			return str;
			
		}
		
	/** CQ10: Find the collaborative researchers and their collaboration count of designing workflows together.
	 * Users need to know researchers (i.e., agents) who most often collaborate and their count of collaborative workflows.
	 * */
		public String getCollaborations() {
		
			String sparql = "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"PREFIX prov:<http://www.w3.org/ns/prov#> " + 
				"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/>  " + 
				"CONSTRUCT { " + 
				"?workflow prov:wasAttributedTo ?agentId1. " + 
				"?workflow prov:wasAttributedTo ?agentId2 }  " + 
				"FROM <http://weprov.csiro.au/evolution/> " +
				"WHERE { " + 
				"{?workflow prov:wasGeneratedBy|prov:wasInvalidatedBy ?activity.} UNION {?workflow (prov:qualifiedDerivation|prov:qualifiedRevision)/prov:activity ?activity.} ?activity prov:wasAssociatedWith ?agentId1. " + 
				"{?workflow prov:wasGeneratedBy|prov:wasInvalidatedBy ?activity2.} UNION {?workflow (prov:qualifiedDerivation|prov:qualifiedRevision)/prov:activity ?activity2.} ?activity2 prov:wasAssociatedWith ?agentId2. " + 
				"FILTER (?agentId1 != ?agentId2) " + 
				"}";
		
			logger.info("Prepared SPARQL query successfully");
			logger.info(sparql);
			
			QuadStore store = QuadStore.getDefaultStore();
			Model model = store.execConstruct(sparql);
		
			System.out.println(model.size());
		
			ByteArrayOutputStream byteout = new ByteArrayOutputStream();
			model.write(byteout, "TTL");
			logger.info("Model serialized successfully");
			String str = byteout.toString();
		
			return str;
		}
		
		public String getCollaborationCount() {
			
			String sparql = "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
					"PREFIX prov:<http://www.w3.org/ns/prov#> " + 
					"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
					"PREFIX foaf:<http://xmlns.com/foaf/0.1/>  " + 
					"CONSTRUCT { " + 
					"?workflow prov:wasAttributedTo ?agentId1. " + 
					"?workflow prov:wasAttributedTo ?agentId2 }  " + 
					"FROM <http://weprov.csiro.au/evolution/> " +
					"WHERE { " + 
					"{?workflow prov:wasGeneratedBy|prov:wasInvalidatedBy ?activity.} UNION {?workflow (prov:qualifiedDerivation|prov:qualifiedRevision)/prov:activity ?activity.} ?activity prov:wasAssociatedWith ?agentId1. " + 
					"{?workflow prov:wasGeneratedBy|prov:wasInvalidatedBy ?activity2.} UNION {?workflow (prov:qualifiedDerivation|prov:qualifiedRevision)/prov:activity ?activity2.} ?activity2 prov:wasAssociatedWith ?agentId2. " + 
					"FILTER (?agentId1 != ?agentId2) " + 
					"}";
			
			logger.info("Prepared SPARQL query successfully");
			logger.info(sparql);
			
			QuadStore store = QuadStore.getDefaultStore();
			Model model = store.execConstruct(sparql);
			
			String queryStr = "SELECT * WHERE {?s ?p ?o}";
			Query query = QueryFactory.create(queryStr);
			QueryExecution qexec = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = qexec.execSelect();
			
			while(resultSet.hasNext()) {
				QuerySolution rs = resultSet.nextSolution();
				System.out.println(rs.getResource("s") + "/t" + rs.getResource("p") + "/t" + rs.getResource("o") + "/t");
			}
			
			return sparql;
		}
		
	/** CQ11: When a (first/last) version of the workflow is created? 
	 * When different versions of a workflow are created.
	 * */
	
		public String getWorkflowEvolutionTime(String workflowId) {
			
			Model model = ModelFactory.createDefaultModel();
			
			String sparql ="PREFIX prov:<http://www.w3.org/ns/prov#> " + 
					"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
					"PREFIX foaf:<http://xmlns.com/foaf/0.1/>  " + 
					"CONSTRUCT {"+
					"<"+workflowId+"> prov:qualifiedGeneration ?generation. ?generation prov:atTime ?generationTime. " + 
					"<"+workflowId+"> prov:qualifiedDerivation ?derivation. ?derivation prov:atTime ?derivationTime. " + 
					"<"+workflowId+"> prov:qualifiedRevision ?revision. ?revision prov:atTime ?revisionTime. " + 
					"<"+workflowId+"> prov:qualifiedInvalidation ?invalidation. ?invalidation prov:atTime ?invalidationTime. " + 
					"} " + 
					"FROM <http://weprov.csiro.au/evolution/> "+
					"WHERE { " +
					"{<"+workflowId+"> prov:qualifiedGeneration ?generation. ?generation prov:atTime ?generationTime} UNION "+
					"{<"+workflowId+"> prov:qualifiedDerivation ?derivation. ?derivation prov:atTime ?derivationTime} UNION "+
					"{<"+workflowId+"> prov:qualifiedRevision ?revision. ?revision prov:atTime ?revisionTime} UNION "+
					"{<"+workflowId+"> prov:qualifiedInvalidation ?invalidation. ?invalidation prov:atTime ?invalidationTime}"+
					"} ";
			
					logger.info("Prepared SPARQL query successfully");
					logger.info(sparql);
					
					QuadStore store = QuadStore.getDefaultStore();
					model = store.execConstruct(sparql);
					
					System.out.println(model.size());
					
					ByteArrayOutputStream byteout = new ByteArrayOutputStream();
					model.write(byteout, "TTL");
					logger.info("Model serialized successfully");
					String str = byteout.toString();
					
					return str;
		}
		
		/** CQ12: (a) How long an agent participated in a workflow (or repository of a workflow)?  */
		
		public String findAgentParticipationInWorflowDuration(String workflowId, String agentName) {
				
			String sparql ="PREFIX prov:<http://www.w3.org/ns/prov#> " + 
					"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
					"PREFIX foaf:<http://xmlns.com/foaf/0.1/>  " + 
					"CONSTRUCT {<"+workflowId+"> prov:wasAttributedTo ?agentId. ?agentId prov:startedAtTime ?startTime; prov:endedAtTime ?endTime.}  " + 
					"FROM <http://weprov.csiro.au/evolution/> "+
					"WHERE { " + 
					"SELECT ?agentId (min(?time) AS ?startTime)  (max(?time) AS ?endTime) " + 
					"FROM <http://weprov.csiro.au/evolution/> "+
					"WHERE { " + 
					"    { {<"+workflowId+"> prov:wasGeneratedBy|prov:wasInvalidatedBy ?activity.} UNION  " + 
					"      {<"+workflowId+"> (prov:qualifiedDerivation|prov:qualifiedRevision)/prov:activity ?activity.} }  " + 
					"?activity prov:startedAtTime ?time; prov:wasAssociatedWith ?agentId. ?agentId foaf:name ?name. " + 
					"FILTER regex(?name, \""+agentName+"\", \"i\") " + 
					"} GROUP BY ?agentId " + 
					"} ";
			
					logger.info("Prepared SPARQL query successfully");
					logger.info(sparql);
					
					QuadStore store = QuadStore.getDefaultStore();
					Model model = store.execConstruct(sparql);
					
					System.out.println(model.size());
					
					ByteArrayOutputStream byteout = new ByteArrayOutputStream();
					model.write(byteout, "TTL");
					logger.info("Model serialized successfully");
					String str = byteout.toString();
					
					return str;
		}
		
		
		/** CQ12: (b) How long an agent participated in a repository of the workflow  */
		
		public String findAgentParticipationInRepositoryDuration(String agentName) {
				
			String sparql ="PREFIX prov:<http://www.w3.org/ns/prov#> " + 
					"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
					"PREFIX foaf:<http://xmlns.com/foaf/0.1/>  " + 
					"CONSTRUCT {?workflow prov:wasAttributedTo ?agentId. ?agentId prov:startedAtTime ?startTime; prov:endedAtTime ?endTime.}  " + 
					"FROM <http://weprov.csiro.au/evolution/> "+
					"WHERE { " + 
					"SELECT ?agentId (min(?time) AS ?startTime)  (max(?time) AS ?endTime) " + 
					"FROM <http://weprov.csiro.au/evolution/> "+
					"WHERE { " + 
					"    { {?workflow prov:wasGeneratedBy|prov:wasInvalidatedBy ?activity.} UNION  " + 
					"      {?workflow (prov:qualifiedDerivation|prov:qualifiedRevision)/prov:activity ?activity.} }  " + 
					"?activity prov:startedAtTime ?time; prov:wasAssociatedWith ?agentId. ?agentId foaf:name ?name. " + 
					"FILTER regex(?name, \""+agentName+"\", \"i\") " + 
					"} GROUP BY ?agentId " + 
					"} ";
			
					logger.info("Prepared SPARQL query successfully");
					logger.info(sparql);
					
					QuadStore store = QuadStore.getDefaultStore();
					Model model = store.execConstruct(sparql);
					
					System.out.println(model.size());
					
					ByteArrayOutputStream byteout = new ByteArrayOutputStream();
					model.write(byteout, "TTL");
					logger.info("Model serialized successfully");
					String str = byteout.toString();
					
					return str;
		}
		
	/*public String getEvolution(String workflowId) {
		String sparql = "PREFIX prov:<http://www.w3.org/ns/prov#> " + 
				"PREFIX weprov:<http://www.csiro.au/digiscape/weprov#> " + 
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/> " + 
				"Construct { ?s ?p ?o } " + 
				"FROM <http://weprov.csiro.au/evolution/> "+
				"WHERE { ?s ?p ?o }";
		
		logger.info("Prepared SPARQL query successfully");
		logger.info(sparql);
		
		QuadStore store = QuadStore.getDefaultStore();
		Model model = store.execConstruct(sparql);
		
		System.out.println(model.size());
		
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		model.write(byteout, "N3");
		logger.info("Model serialized successfully");
		String str = byteout.toString();
		
		return str;
		
	}*/
	
	
}
