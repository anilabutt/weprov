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

public class PortListener extends GenericService {

		
	/**
	 * Default constructor to initializes logging by its parent.
	 */
	
	public PortListener() {
		super(PortListener.class.getName());
	}
	
	//Generating Program Creation Provenance as Part of Workflow Creation Provenance
	
	public Model getPortCreationProvenance(Model model, String actorId, String creatingEntity) throws JSONException {
			
		CreationProvenance creation = new CreationProvenance();
		
		Resource programId = ModelFactory.createDefaultModel().createResource(creatingEntity);
		Property hasInPort = ModelFactory.createDefaultModel().createProperty(WeProvOnt.hasInPort);
		Property hasOutPort = ModelFactory.createDefaultModel().createProperty(WeProvOnt.hasOutPort);
		
		
		Model _model = ModelFactory.createDefaultModel();
		
		HashMap<String, String> partOfRel = new HashMap<String, String>();		
		partOfRel.put("generationId", creatingEntity);
		
		
		int portCount = 0;
		
		if (model.contains(programId, hasInPort) ) {
			
			NodeIterator iter = model.listObjectsOfProperty(programId, hasInPort);
			
			while(iter.hasNext()) {
				RDFNode portId = iter.next();	

				System.out.println("InPorts : " + portId.toString());
				_model.add(creation.generateCreationRDF(portId.toString(), actorId, partOfRel));
				
				
				ChannelListener cListener = new ChannelListener();
				_model.add(cListener.getChannelCreationProvenance(model, actorId, creatingEntity, portId.toString()));
				
				ParamListener pmListener = new ParamListener();
				_model.add(pmListener.getParamCreationProvenance(model, actorId, creatingEntity, portId.toString()));
				
				portCount++;
			}
		}
			
		if (model.contains(programId, hasOutPort) ) {
				
			NodeIterator outiter = model.listObjectsOfProperty(programId, hasOutPort);

			while(outiter.hasNext()) {
				
				RDFNode portId = outiter.next();
				System.out.println("OutPorts : " + portId.toString());
				_model.add(creation.generateCreationRDF(portId.toString(), actorId, partOfRel));	
				ChannelListener cListener = new ChannelListener();
				_model.add(cListener.getChannelCreationProvenance(model, actorId, creatingEntity, portId.toString()));				
				portCount++;
			}
			
			System.out.println(portCount + " Port(s) Added ... ");
		} 
			
		return _model;
	}

	//Generating Program Creation Provenance as Part of Workflow Revision Provenance
	
	public Model getPortCreationProvenance(Difference diff, String actorId, String creatingEntity, String version) throws JSONException {
		
		CreationProvenance creation = new CreationProvenance();
		
//		Node port = NodeFactory.createURI(WeProvOnt.Port)
		Node programId = NodeFactory.createURI(creatingEntity);
		Node hasInPort = NodeFactory.createURI(WeProvOnt.hasInPort);
		Node hasOutPort = NodeFactory.createURI(WeProvOnt.hasOutPort);
		
		Model _model = ModelFactory.createDefaultModel();
	
		HashMap<String, String> partOfRel = new HashMap<String, String>();		
		
		String revisionId = WeProvData.revision + version +"/" +creatingEntity.replace(WeProvData.wedata, "");
		
		partOfRel.put("revisionId", revisionId);
		
		
		if (diff.contains(programId, hasInPort, null) ) {
			
			ExtendedIterator<Triple> iter = diff.find(programId, hasInPort, null);
			
			while(iter.hasNext()) {
				Triple t = iter.next();	

				System.out.println("InPorts : " + t.getObject().toString());
				_model.add(creation.generateCreationRDF(t.getObject().toString(), actorId, partOfRel));
				
				
				ChannelListener cListener = new ChannelListener();
				_model.add(cListener.getChannelCreationProvenance(diff, actorId, creatingEntity, version, t.getObject().toString()));
				
				ParamListener pmListener = new ParamListener();
				_model.add(pmListener.getParamCreationProvenance(diff, actorId, creatingEntity, version, t.getObject().toString()));

			}
		}
			
		if (diff.contains(programId, hasOutPort, null) ) {
			
			ExtendedIterator<Triple> iter = diff.find(programId, hasOutPort, null);
			
			while(iter.hasNext()) {
				Triple t = iter.next();	

				System.out.println("OutPorts : " + t.getObject().toString());
				_model.add(creation.generateCreationRDF(t.getObject().toString(), actorId, partOfRel));
				
				
				ChannelListener cListener = new ChannelListener();
				_model.add(cListener.getChannelCreationProvenance(diff, actorId, creatingEntity, version, t.getObject().toString()));
				
				ParamListener pmListener = new ParamListener();
				_model.add(pmListener.getParamCreationProvenance(diff, actorId, creatingEntity, version, t.getObject().toString()));
			}
		}
		return _model;
	}
}
