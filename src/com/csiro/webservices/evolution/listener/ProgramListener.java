package com.csiro.webservices.evolution.listener;

import java.util.HashMap;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.compose.Difference;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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

public class ProgramListener extends GenericService {

		
	/**
	 * Default constructor to initializes logging by its parent.
	 */
	
	public ProgramListener() {
		super(ProgramListener.class.getName());
	}
	
	//Generating Program Creation Provenance as Part of Workflow Creation Provenance
	
	public Model getProgramCreationProvenance(Model model, String actorId, String creatingEntity) throws JSONException {
			
		CreationProvenance creation = new CreationProvenance();
		
		Resource program = ModelFactory.createDefaultModel().createResource(WeProvOnt.Program);
		
		Model _model = ModelFactory.createDefaultModel();
		
		HashMap<String, String> partOfRel = new HashMap<String, String>();		
		partOfRel.put("generationId", creatingEntity);
		
		if (model.contains(null, null, program)) {
			
			StmtIterator iter = model.listStatements(null, null, program);
			
			int programCount = 0;
			
			while(iter.hasNext()) {
				
				Statement tr= iter.next();
				String entityId = tr.getSubject().toString();
				_model.add(creation.generateCreationRDF(entityId, actorId, partOfRel));				
				programCount++;
			}
			
			System.out.println(programCount + " Program(s) Added ... ");
		} 
		return _model;
	}

	//Generating Program Creation Provenance as Part of Workflow Revision Provenance
	
	public Model getProgramCreationProvenance(Difference diff, String actorId, String creatingEntity, String version) throws JSONException {
		
		CreationProvenance creation = new CreationProvenance();
		
		Node program = NodeFactory.createURI(WeProvOnt.Program);
		
		Model _model = ModelFactory.createDefaultModel();
		
		HashMap<String, String> partOfRel = new HashMap<String, String>();		
		
		String revisionId = WeProvData.revision + version +"/" +creatingEntity.replace(WeProvData.wedata, "");
		
		partOfRel.put("revisionId", revisionId);
		
		
		if (diff.contains(null, null, program)) {
			
			ExtendedIterator<Triple> iter = diff.find(null, null, program);
			
			int programCount = 0;
			
			while(iter.hasNext()) {
				Triple tr= iter.next();
				//System.out.println(tr.getSubject());
				String entityId = tr.getSubject().toString();
				
				_model.add(creation.generateCreationRDF(entityId, actorId, partOfRel));
				programCount++;
			}
			
			System.out.println(programCount + " Program(s) Added ... ");
		} 
		return _model;
	}
	
	public Model getProgramRevisionProvenance(Difference diff, String actorId, String creatingEntity, String version) throws JSONException {
		
		CreationProvenance creation = new CreationProvenance();
		
		Node program = NodeFactory.createURI(WeProvOnt.Program);
		
		Model _model = ModelFactory.createDefaultModel();
		
		HashMap<String, String> partOfRel = new HashMap<String, String>();		
		
		String revisionId = WeProvData.revision + version +"/" +creatingEntity.replace(WeProvData.wedata, "");
		
		partOfRel.put("revisionId", revisionId);
		
		
		if (diff.contains(null, null, program)) {
			
			ExtendedIterator<Triple> iter = diff.find();
			
			int programCount = 0;
			
			while(iter.hasNext()) {
				Triple tr= iter.next();
				//System.out.println(tr.getSubject());
				String entityId = tr.getSubject().toString();
				if(entityId.startsWith(WeProvData.))
				diff.remove(tr.getSubject(), null, null);
			}
			
			System.out.println(programCount + " Program(s) Added ... ");
		} 
		return _model;
	}
	
	
}
