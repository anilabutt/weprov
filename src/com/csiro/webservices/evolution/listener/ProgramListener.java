package com.csiro.webservices.evolution.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
				
				PortListener pListener = new PortListener();
				_model.add(pListener.getPortCreationProvenance(model, actorId, entityId));						
				
				programCount++;
			}
			
			System.out.println(programCount + " Program(s) Added ... ");
		} 
		return _model;
	}
	
	
	public Model getProgramCreationProvenance(Difference diff, String actorId, String creatingEntity, String version) throws JSONException {
		
		
		CreationProvenance creation = new CreationProvenance();
		
		Node program = NodeFactory.createURI(WeProvOnt.Program);
//		Node 
		
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
				
				PortListener pListener = new PortListener();
				_model.add(pListener.getPortCreationProvenance(diff, actorId, entityId, version));
				
				programCount++;
			}
			
			System.out.println(programCount + " Program(s) Added ... ");
		} 
		return _model;
	}

	public Model getProgramModificationProvenance(Difference diff, String actorId, String creatingEntity, String version) throws JSONException {
		
		CreationProvenance creation = new CreationProvenance();
		
		Node port = NodeFactory.createURI(WeProvOnt.Port);
		Node rdfType = NodeFactory.createURI(WeProvOnt.rdfType);
		
		Model _model = ModelFactory.createDefaultModel();
		
		HashMap<String, String> partOfRel = new HashMap<String, String>();		
		
		String revisionId = WeProvData.revision + version +"/" +creatingEntity.replace(WeProvData.wedata, "");
		
		partOfRel.put("revisionId", revisionId);
		
		
		if (diff.contains(null, rdfType, port)) {
			
			ExtendedIterator<Triple> iter = diff.find(null, rdfType, port);
			
			int protCount = 0;
			
			while(iter.hasNext()) {
				Triple tr= iter.next();
				//System.out.println(tr.getSubject());
				String entityId = tr.getSubject().toString();				
				_model.add(creation.generateCreationRDF(entityId, actorId, partOfRel));
				
				PortListener pListener = new PortListener();
				_model.add(pListener.getPortCreationProvenance(diff, actorId, entityId, version));
				
				protCount++;
			}
			
			System.out.println(protCount + " Program(s) Added ... ");
		} 
		return _model;
	}

	
	//Generating Program Creation Provenance as Part of Workflow Revision Provenance
	
	public Model findProgramEvolutionActivity(Difference diff, String actorId, String creatingEntity, String version) throws JSONException {
		
		CreationProvenance creation = new CreationProvenance();
		
		Node program = NodeFactory.createURI(WeProvOnt.Program);
		Node Channel = NodeFactory.createURI(WeProvOnt.Channel);
		Node Port = NodeFactory.createURI(WeProvOnt.Port);
		
		Node inPort = NodeFactory.createURI(WeProvOnt.hasInPort);
		Node outPort = NodeFactory.createURI(WeProvOnt.hasOutPort);
		Node connectsTo = NodeFactory.createURI(WeProvOnt.connectsTo);
		Node hosts = NodeFactory.createURI(WeProvOnt.host);
		Node rdfType = NodeFactory.createURI(WeProvOnt.rdfType);

		Node rdfsLabel = NodeFactory.createURI(WeProvOnt.label);
		
		
		Model _model = ModelFactory.createDefaultModel();
		
		HashMap<String, String> partOfRel = new HashMap<String, String>();		
		
		String revisionId = WeProvData.revision + version +"/" +creatingEntity.replace(WeProvData.wedata, "");
		
		partOfRel.put("revisionId", revisionId);
			
		if (diff.contains(null, inPort, null) || diff.contains(null, outPort, null)) {
			ExtendedIterator<Triple> iter = diff.find(null, inPort, null);
			ExtendedIterator<Triple> iter2 = diff.find(null, outPort, null);

			List<Triple> triples = iter.toList();
			while (iter2.hasNext())
			{
				Triple t= iter2.next();
					triples.add(t);
			}
			
			List<String> visitedPrograms = new ArrayList<String>();
			
			for(Triple tr: triples) {
				String entityId = tr.getSubject().toString();
				Node entityURI = NodeFactory.createURI(entityId);
				if(!visitedPrograms.contains(entityId)) {	
					if(entityId.startsWith(WeProvData.program)) { 
					//program Id exists: A program has been added or updated
					
						if(diff.contains(entityURI, rdfType, program) ){
							System.out.println(entityURI + " : A program is added  ");
							_model.add( getProgramCreationProvenance(diff, actorId, creatingEntity, version));			
						} else {
							System.out.println(entityURI + " : A program is updated  ");
						}
						visitedPrograms.add(entityId);
					}
				}
			}
		}
				
//				if(entityId.startsWith(WeProvData.model)) {
//					System.out.println("Model Added : " + entityId);
//					//Check if Port and Channel are added for a new Program or as a result of a Program update
//					if(diff.contains(null, hosts, entityURI)) {
//						ExtendedIterator<Triple> progIter = diff.find(null, hosts, entityURI);
//						while(progIter.hasNext()) {
//							Node progURI = progIter.next().getSubject();
//							if(diff.contains(progURI, rdfType, program)) {
//								System.out.println("Program added : " + progURI.toString());
//							} else {
//								System.out.println("Program Updated : " + progURI.toString());
//							}
//						}
//					}
//					
//				} else if(entityId.startsWith(WeProvData.channel)) {
//						System.out.println("Channel Added : " + entityId);	
//						//check if channel added for a new port or for an old port
//						if (diff.contains(null, connectsTo, entityURI)) {
//							ExtendedIterator<Triple> portIter = diff.find(null, connectsTo, entityURI);
//							while(portIter.hasNext()) {
//								Node portURI = portIter.next().getSubject();
//								//if portId connects to this channel and has an rdf type property then a port is also added
//								if(diff.contains(portURI, rdfType, Port)) {
//									System.out.println("Port added : " + portURI.toString());
//									
//									//Check if Port and Channel are added for a new Program or as a result of a Program update
//									if(diff.contains(null, inPort, portURI) || diff.contains(null, outPort, portURI)) {
//										ExtendedIterator<Triple> progIter = diff.find(null, inPort, portURI);
//										while(progIter.hasNext()) {
//											Node progURI = progIter.next().getSubject();
//											if(diff.contains(progURI, rdfType, program)) {
//												System.out.println("Program added : " + progURI.toString());
//											} else {
//												System.out.println("Program Updated : " + progURI.toString());
//											}
//										}
//									}					
//									
//								} else {
//									System.out.println("Port Updated : " + portURI.toString());
//								}
//							}
//						}
//						
//				} else if(entityId.startsWith(WeProvData.param)) {
//					System.out.println("Param Added : " + entityId);
//				} else if(entityId.startsWith(WeProvData.port)) {
//					System.out.println("Port : " + entityId);
//				} else if(entityId.startsWith(WeProvData.program)) {
//					System.out.println("Program : " + entityId);
//				} 

//		if (diff.contains(null, null, program) || diff.contains(null, inPort, null) || diff.contains(null, outPort, null)) {
//			
//			ExtendedIterator<Triple> iter = diff.find(null, null, program);
//			
//			int programCount = 0;
//			
//			ArrayList<String> programs = new ArrayList<String>();
//			
//			while(iter.hasNext()) {
//				Triple tr= iter.next();
//				//System.out.println(tr.getSubject());
//				String entityId = tr.getSubject().toString();				
//				_model.add(creation.generateCreationRDF(entityId, actorId, partOfRel));
//				
//				PortListener pListener = new PortListener();
//				pListener.getPortCreationProvenance(diff, actorId, entityId, version);
//				
//				programs.add(entityId);
//				programCount++;
//			}
//			
//			
//			ExtendedIterator<Triple> ipIter = diff.find(null,  inPort, null);
//			while(ipIter.hasNext()) {
//				Triple tr= ipIter.next();
//				//System.out.println(tr.getSubject());
//				String entityId = tr.getSubject().toString();
//				if(programs.contains(entityId)) {
//					//Program has already been added
//				} else {
//					//Revise this program
//					//An inport has been added
//					programs.add(entityId);
//				} 
//			}
//			
//			ExtendedIterator<Triple> opIter = diff.find(null,  outPort, null);
//			while(opIter.hasNext()) {
//				Triple tr= opIter.next();
//				//System.out.println(tr.getSubject());
//				String entityId = tr.getSubject().toString();
//				if(programs.contains(entityId)) {
//					//Program has already been added
//				} else {
//					//Revise this program
//					//An outport has been added
//					
//				} 
//			}
//			
//			
//			System.out.println(programCount + " Program(s) Added ... ");
//		} 
		return _model;
	}
	
	
	
	
}
