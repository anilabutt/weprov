
import java.util.*;

import org.json.JSONException;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.csiro.webservices.Listeners.*;
import com.csiro.webservices.app.beans.Workflow;
import com.csiro.webservices.rest.GenericService;
import com.csiro.webservices.rest.WeprovCompetencyQueries;
import com.csiro.webservices.store.QuadStore;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class TestQuadStore extends GenericService{

	public TestQuadStore(String loggerName) {
		super(loggerName);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws JSONException {
		// TODO Auto-generated method stub

		String url;

		Node foo1 = Node.createURI("http://example.org/#foo1");
		Node bar1 = Node.createURI("http://example.org/#bar1");
		Node baz1 = Node.createURI("http://example.org/#baz1");

		Node foo2 = Node.createURI("http://example.org/#foo2");
		Node bar2 = Node.createURI("http://example.org/#bar2");
		Node baz2 = Node.createURI("http://example.org/#baz2");

		Node foo3 = Node.createURI("http://example.org/#foo3");
		Node bar3 = Node.createURI("http://example.org/#bar3");
		Node baz3 = Node.createURI("http://example.org/#baz3");

		
		Node node1 = Node.createURI("http://weprov.csiro.au/controller/CountDiseases");
				Node node2 = Node.createURI("http://www.csiro.au/digiscape/weprov/hasController");
				Node node3 = Node.createURI("http://weprov.csiro.au/controller/Count"); 
		List <Triple> triples = new ArrayList <Triple> ();

		//VirtGraph graph = new VirtGraph ("http://weprov.csiro.au/","jdbc:virtuoso://localhost:1111", "dba", "dba");

		//System.out.println("Add 3 triples to graph <Example3>.");


		triples.add(new Triple(foo1, bar1, baz1));
		triples.add(new Triple(foo2, bar2, baz2));
		triples.add(new Triple(foo3, bar3, baz3));
		
		
		/*graph.add(new Triple(foo1, bar1, baz1));
		graph.add(new Triple(foo2, bar2, baz2));
		graph.add(new Triple(foo3, bar3, baz3));*/
		
		//graph.add(new Triple(node1, node2, node3));
		
		
		//triples.add(new Triple(node1, node2, node3));


		//QuadStore.getDefaultStore().insertSpecs(triples);
		
		//QuadStore.getDefaultStore().deleteSpecs(triples);
		
		QuadStore store = QuadStore.getDefaultStore();
		
		/*String query = "SELECT * FROM <http://weprov.csiro.au/> WHERE { ?s ?p ?o } ";
		//System.out.println(query);
		
		ResultSet results = store.execSelect(query);
		while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            //  RDFNode graph = result.get("graph");
            RDFNode s = result.get("s");
            RDFNode p = result.get("p");
            RDFNode o = result.get("o");
            System.out.println( " { " + s + " " + p + " " + o + " . }");
  	  	}*/
		
		String workflowId = "http://weprov.csiro.au/workflow/urn:lsid:net.sf.taverna:wfDefinition:618ac202-acf6-4695-bdc6-ca0078be3649";
		/*String query = "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"PREFIX dcterm:<http://purl.org/dc/terms/> " + 
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " + 
				"PREFIX workflow:<http://weprov.csiro.au/workflow/> " + 
				"CONSTRUCT " + 
				"{"+workflowId+" a provone:Workflow; " + 
				"?property ?propertyValue. ?propertyValue rdfs:label ?label; a ?propertyValueType. " + 
				" ?propertyValue provone:controls ?aProgram. " + 
				""+workflowId+" provone:hasSubProgram ?program. ?program a ?programType; rdfs:label ?programLabel. "
				+ " ?program provone:hasSubProgram ?subWorkflow. "
				+ " ?program provone:controlledBy ?aController. " + 
				"}"+
				"FROM <http://weprov.csiro.au/>  " + 
				"WHERE {"+workflowId+" a provone:Workflow; " + 
				"?property ?propertyValue. OPTIONAL {?propertyValue rdfs:label ?label; a ?propertyValueType.} " + 
				"OPTIONAL {?propertyValue provone:controls ?aProgram.} " + 
				""+workflowId+" provone:hasSubProgram ?program. ?program a ?programType; rdfs:label ?programLabel. "
				+ "OPTIONAL {?program provone:hasSubProgram ?subWorkflow.} "
				+ "OPTIONAL {?program provone:controlledBy ?aController.} " + 				
				"}";*/
		
		//String workflowId = "<http://weprov.csiro.au/workflow/urn:lsid:net.sf.taverna:wfDefinition:618ac202-acf6-4695-bdc6-ca0078be3649>";
		/*String query = "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"PREFIX dcterm:<http://purl.org/dc/terms/> " + 
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " + 
				"PREFIX workflow:<http://weprov.csiro.au/workflow/> " + 
				"CONSTRUCT " + 
				"{?workflowId a provone:Workflow; " + 
				"?property ?propertyValue. ?propertyValue rdfs:label ?label; a ?propertyValueType. " + 
				" ?propertyValue provone:controls ?aProgram. " + 
				"?workflowId provone:hasSubProgram ?program. ?program a ?programType; rdfs:label ?programLabel. "
				+ " ?program provone:hasSubProgram ?subWorkflow. "
				+ " ?program provone:controlledBy ?aController. " + 
				"}"+
				"FROM <http://weprov.csiro.au/>  " + 
				"WHERE {?workflowId a provone:Workflow; " + 
				"?property ?propertyValue. OPTIONAL {?propertyValue rdfs:label ?label; a ?propertyValueType.} " + 
				"OPTIONAL {?propertyValue provone:controls ?aProgram.} " + 
				"?workflowId provone:hasSubProgram ?program. ?program a ?programType; rdfs:label ?programLabel. "
				+"OPTIONAL {?program provone:hasSubProgram ?subWorkflow.} "
				+"OPTIONAL {?program provone:controlledBy ?aController.} "
				+"FILTER (?workflowId = ?workflow)" + 
				"	{" + 
				" 		SELECT ?workflow " + 
				"  		WHERE {	" + 
						"<"+workflowId+">  (provone:hasSubProgram/provone:hasSubProgram)*  ?workflow." + 
				"    	}" + 
				" 	}" + 				
				"}";
  	  
		Model model = store.execConstruct(query);
		model.write(System.out, "TTL");

		System.out.println("Does this exist : " + model.isEmpty());*/
		
		
		/*String sparql = "PREFIX provone:<http://purl.dataone.org/provone/2015/01/15/ontology#> " + 
				"CONSTRUCT {?workflow a provone:Workflow.?workflow provone:hasSubProgram ?subWorkflow.} " + 
				"FROM <http://weprov.csiro.au/> " + 
				"WHERE {?workflow a provone:Workflow. " +
				"?workflow (provone:hasSubProgram/provone:hasSubProgram)+ ?subWorkflow"+
				"}";	
		
		System.out.println(sparql);
		Model model = store.execConstruct(sparql);
		model.write(System.out, "TTL");*/
			//ResultSet rs = getRevisionCount(workflowId);
            /*while(rs.hasNext()) {
            	QuerySolution qs= rs.next();
    			Literal uri = qs.getLiteral("rev");
    			System.out.println("Resource 1 of resultSet : " + uri.toString());
            } */   
		
		//RdfToWorkflow rdftoworkflow = new RdfToWorkflow();
		//Workflow w = rdftoworkflow.getWorkflowfromRdf(model, workflowId);
		
		//XmlParser df = new XmlParser();
		//df.printThisWorkflow(w);
		
		
		//RevisionId test
		WeprovCompetencyQueries wcq = new WeprovCompetencyQueries("WeprovCompetencyQueries");
		//System.out.println(wcq.getCurrentVersionNumber(workflowId));
		//System.out.println(wcq.getRevisionProvenance(workflowId, "1"));
		//System.out.println(wcq.getWorkflowParticipants(workflowId));
		//System.out.println(wcq.getWorkflowRevisior(workflowId, "1"));
		//System.out.println(wcq.getGenerationProvenance(workflowId));
		//System.out.println(wcq.getWorkflowCreator(workflowId));
		//System.out.println(wcq.getEvolution(workflowId));
		//System.out.println(wcq.getWorkflowsForAgent("roos"));
		//System.out.println(wcq.getWorkflowEvolutionTime(workflowId));
		//System.out.println(wcq.findAgentParticipationInRepositoryDuration("roos"));
		//System.out.println(wcq.getWorkflowUnstableComponents(workflowId));
		System.out.println(wcq.getAllRevisionsBetweenADuration(workflowId, "2020-05-05T02:20:22.201Z", "2020-05-05T02:21:00.751Z"));
	}
}
