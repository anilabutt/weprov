package notusing;
import java.util.*;

import com.hp.hpl.jena.rdf.model.RDFNode;

import virtuoso.jena.driver.VirtGraph;

import com.csiro.webservices.store.QuadStore;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class TestQuadStore {

	public static void main(String[] args) {
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


		QuadStore.getDefaultStore().insertSpecs(triples);
		
		//QuadStore.getDefaultStore().deleteSpecs(triples);
		
		/*QuadStore store = QuadStore.getDefaultStore();
		
		String query = "SELECT * FROM <http://weprov.csiro.au/> WHERE { ?s ?p ?o } ";
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
  	  
				

	}

}
