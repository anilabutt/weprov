package com.csiro.webservices.evolution.listener;

import java.util.HashMap;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.compose.Difference;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;
import org.json.JSONObject;

import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.logic.CreationProvenance;
import com.csiro.webservices.rest.GenericService;
import com.csiro.webservices.store.WeProvData;
import com.csiro.webservices.store.WeProvOnt;
//import com.csiro.webservices.rest.Workflow;

public class ParamListener extends GenericService {

		
	/**
	 * Default constructor to initializes logging by its parent.
	 */
	
	public ParamListener() {
		super(ParamListener.class.getName());
	}
	
	//Generating Program Creation Provenance as Part of Workflow Creation Provenance
	
	public Model getParamCreationProvenance(Model model, String actorId, String creatingEntity, String port) throws JSONException {
			
		CreationProvenance creation = new CreationProvenance();
		
		Property defaultParam = ModelFactory.createDefaultModel().createProperty(WeProvOnt.hasDefaultParam);	
		Resource portId =  ModelFactory.createDefaultModel().createResource(port);
		
		Model _model = ModelFactory.createDefaultModel();
		
		HashMap<String, String> partOfRel = new HashMap<String, String>();		
		partOfRel.put("generationId", creatingEntity);
		
		
		int count = 0;
		
		if (model.contains(portId, defaultParam) ) {
			
			NodeIterator iter = model.listObjectsOfProperty(portId, defaultParam);
			
			while(iter.hasNext()) {
				RDFNode param = iter.next();	

				System.out.println("Param : " + param.toString());
				_model.add(creation.generateCreationRDF(param.toString(), actorId, partOfRel));		
				count++;
			}
		}
		return _model;
	}

	//Generating Program Creation Provenance as Part of Workflow Revision Provenance
	
	public Model getParamCreationProvenance(Difference diff, String actorId, String creatingEntity, String version, String port) throws JSONException {
		
		CreationProvenance creation = new CreationProvenance();
		
		Node defaultParam = NodeFactory.createURI(WeProvOnt.hasDefaultParam);	
		Node portId =  NodeFactory.createURI(port);
		
		Model _model = ModelFactory.createDefaultModel();
		
		HashMap<String, String> partOfRel = new HashMap<String, String>();		
		
		String revisionId = WeProvData.revision + version +"/" +creatingEntity.replace(WeProvData.wedata, "");
		
		partOfRel.put("revisionId", revisionId);
		
		
		if (diff.contains(portId, defaultParam, null)) {
			
			ExtendedIterator<Triple> iter = diff.find(portId, defaultParam, null);
			
			int count = 0;
			
			while(iter.hasNext()) {
				
				Triple tr= iter.next();
				String entityId = tr.getObject().toString();
				_model.add(creation.generateCreationRDF(entityId, actorId, partOfRel));
				count++;
			}
			
			System.out.println(count + " Param(s) Added ... ");
		} 
		return _model;
	}
	
	
	
}
