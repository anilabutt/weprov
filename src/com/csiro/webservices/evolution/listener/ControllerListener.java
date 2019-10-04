package com.csiro.webservices.evolution.listener;

import java.util.HashMap;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.compose.Difference;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;
import com.csiro.webservices.logic.CreationProvenance;
import com.csiro.webservices.rest.GenericService;
import com.csiro.webservices.store.WeProvData;
import com.csiro.webservices.store.WeProvOnt;

public class ControllerListener extends GenericService {


	/**
	 * Default constructor to initializes logging by its parent.
	 */
	public ControllerListener() {
		super(ControllerListener.class.getName());
	}
	
	//Generating Controller Creation Provenance as Part of Workflow Creation Provenance
	
	public Model getControllerCreationProvenance(Model model, String actorId, String creatingEntity) throws JSONException {
		
		CreationProvenance creation = new CreationProvenance();
		
		Resource controller = ModelFactory.createDefaultModel().createResource(WeProvOnt.Controller);
		
		Model _model = ModelFactory.createDefaultModel();
		
		HashMap<String, String> partOfRel = new HashMap<String, String>();		
		partOfRel.put("generationId", creatingEntity);
		
		if (model.contains(null, null, controller)) {
			
			StmtIterator iter = model.listStatements(null, null, controller);			
			int controllerCount = 0;			
			while(iter.hasNext()) {				
				Statement tr= iter.next();
				String entityId = tr.getSubject().toString();
				_model.add(creation.generateCreationRDF(entityId, actorId, partOfRel));				
				controllerCount++;
			}
			
			System.out.println(controllerCount + " Controller(s) Added ... ");
		} 
		return _model;
	}
	
	
	//Generating Controller Creation Provenance as PartOf Workflow Revision Provenance
	
	public Model getControllerCreationProvenance(Difference diff, String actorId, String creatingEntity, String version) throws JSONException {
		
		CreationProvenance creation = new CreationProvenance();
		Model _model = ModelFactory.createDefaultModel();
		
		Node controller = NodeFactory.createURI(WeProvOnt.Controller);
		
		HashMap<String, String> partOfRel = new HashMap<String, String>();	
		
		String revisionId = WeProvData.revision + version +"/" +creatingEntity.replace(WeProvData.wedata, "");
				
		partOfRel.put("revisionId", revisionId);
		
		if (diff.contains(null, null, controller)) {
			
			ExtendedIterator<Triple> iter = diff.find(null, null, controller);
			int controllerCount = 0;
			while(iter.hasNext()) {
				Triple tr= iter.next();
				String entityId = tr.getSubject().toString();
				_model.add(creation.generateCreationRDF(entityId, actorId, partOfRel));
				controllerCount++;
			}
			System.out.println(controllerCount + " Controller(s) Added ... ");
		}
		return _model;
	}

}
