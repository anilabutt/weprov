package com.csiro.webservices.evolution.listener;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.compose.Difference;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;
import org.json.JSONObject;

import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.logic.CreationProvenance;
import com.csiro.webservices.rest.GenericService;
//import com.csiro.webservices.rest.Workflow;

public class ControllerListener extends GenericService {

	public static String wedata = Configuration.NS_RES;
	
	/**
	 * Default constructor to initializes logging by its parent.
	 */
	public ControllerListener() {
		super(ControllerListener.class.getName());
	}
		
	public Model addControllerEvolution(Difference diff, String actorId) throws JSONException {
		
		CreationProvenance creation = new CreationProvenance();
//		Node program = NodeFactory.createURI("http://purl.dataone.org/provone/2015/01/15/ontology#Program");
		Model _model = ModelFactory.createDefaultModel();
		
		Node controller = NodeFactory.createURI("http://purl.dataone.org/provone/2015/01/15/ontology#Controller");
		
		if (diff.contains(null, null, controller)) {
			
			ExtendedIterator<Triple> iter = diff.find(null, null, controller);
			int controllerCount = 0;
			while(iter.hasNext()) {
				Triple tr= iter.next();
				String entityId = tr.getSubject().toString();
				_model.add(creation.generateCreationRDF(entityId, actorId));
//				System.out.println(tr.getSubject());
				controllerCount++;
			}
			System.out.println(controllerCount + " Controller(s) Added ... ");
		}
		return _model;
	}

}
