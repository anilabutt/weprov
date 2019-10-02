package com.csiro.webservices.evolution.listener;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.compose.Difference;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;
import org.json.JSONObject;

import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.logic.CreationProvenance;
import com.csiro.webservices.rest.GenericService;
//import com.csiro.webservices.rest.Workflow;

public class ProgramListener extends GenericService {

	public static String wedata = Configuration.NS_RES;
	
	/**
	 * Default constructor to initializes logging by its parent.
	 */
	public ProgramListener() {
		super(ProgramListener.class.getName());
	}
		
	public Model addProgramEvolution(Difference diff, String actorId, Resource revisionId) throws JSONException {
		
		CreationProvenance creation = new CreationProvenance();
		
		Node program = NodeFactory.createURI("http://purl.dataone.org/provone/2015/01/15/ontology#Program");
		
		Model _model = ModelFactory.createDefaultModel();
		
		Property hadGeneration = model.getProperty(prov+"hadGeneration");
		
		if (diff.contains(null, null, program)) {
			
			ExtendedIterator<Triple> iter = diff.find(null, null, program);
			
			int programCount = 0;
			
			while(iter.hasNext()) {
				Triple tr= iter.next();
				//System.out.println(tr.getSubject());
				String entityId = tr.getSubject().toString();
				
				_model.add(creation.generateCreationRDF(entityId, actorId));
				programCount++;
			}
			
			System.out.println(programCount + " Program(s) Added ... ");
		} 
		return _model;
	}

}
