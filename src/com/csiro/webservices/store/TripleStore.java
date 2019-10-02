/*
 * Copyright (c) 2020, CSIRO, All rights reserved.
 * Use is subject to license terms.
 */
package com.csiro.webservices.store;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;


import com.csiro.webservices.config.Configuration;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.tdb.TDBFactory;


/**
 * RDF triple store interface for the API. Services can't instantiate this store, instead they
 * should use {@link #getDefaultStore} method to get its singleton.
 *
 * @author AnilaButt
 */
public class TripleStore {

	/** Default instance of the triple store */
	private static TripleStore defaultStore;

	/** Default logger */
	private Logger logger;
	
	/** RDF model of the triple store */
	private Model schemaModel;
	
	/** RDF model of the triple store */
	private Model baseModel;
	
	/** RDF model of the triple store */
	private Model provModel;
	
	/** Inference model of the triple store */
	private InfModel infModel;
	
	
	/**
	 * Gets default triple store instance to avoid synchronization faults 
	 */
	public static TripleStore getDefaultStore() {
		if(defaultStore==null) {
			defaultStore = new TripleStore();
		}
		return defaultStore;
	}
	

	/**
	 * Initializes this triple store
	 */
	private TripleStore() {
		logger = Logger.getLogger(getClass().getName());
		
		// Unlock lucence index if the server didn't cleanly shutdown
		File lock = new File(Configuration.getProperty(Configuration.INDEX_PATH)+"/write.lock");
		if(lock.exists()) lock.delete();
		
		// Load TDB store
		Dataset baseDataSet= TDBFactory.createDataset(Configuration.getProperty(Configuration.STORE_PATH));
		baseModel = baseDataSet.getDefaultModel();
		baseModel = baseDataSet.getNamedModel(Configuration.NS_RES);

		provModel = baseDataSet.getNamedModel(Configuration.NS_EVORES);
		
//		// Load schema+ontologies
//		schemaModel = TDBFactory.createDataset().getDefaultModel();
//		loadSchema();
//		Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
//        reasoner = reasoner.bindSchema(schemaModel);
//        
//        infModel = ModelFactory.createInfModel(reasoner, baseModel);

		logger.info("TDB triple store initialized");
	}
	
	private void loadSchema() {
		logger = Logger.getLogger(getClass().getName());
		File dir = new File(Configuration.getProperty(Configuration.SCHEMA_PATH));
		File[] files = dir.listFiles();
		for(File file:files) {
			try {
				logger.info(file.getAbsolutePath());
				schemaModel.read(new FileInputStream(file),"");
				logger.info("Schema file loaded successfully");
			} catch(Exception e) {
				logger.info("Cant load schema file: "+e);
			}
		}
		logger.info("TDB triple store initialized");
	}
	
	
	/**
	 * Inserts the input model into triple store
	 * 
	 * @param model Input model to be inserted into this triple store
	 * @return Number of triples inserted into this triple store 
	 */
	public long insert(Model model) {
		try {
			long initialSize = baseModel.size();
			//infModel.register(larqBuilder);
			baseModel.add(model);
			baseModel.commit();
			//larqBuilder.closeWriter();
            //infModel.unregister(larqBuilder);
	        long newTriples = baseModel.size()-initialSize;
			logger.info("Added "+ newTriples +" triples into store");
			return newTriples;
		} catch(Exception exp) {
			logger.severe("Can't add new triples into store because of "+exp);
			return 0;
		}
	}
	
	/**
	 * Inserts the input model into triple store
	 * 
	 * @param model Input model to be inserted into this triple store
	 * @return Number of triples inserted into this triple store 
	 */
	public long insertProvRecord(Model model) {
		try {
			long initialSize = provModel.size();
			//infModel.register(larqBuilder);
			provModel.add(model);
			provModel.commit();
			//larqBuilder.closeWriter();
            //infModel.unregister(larqBuilder);
	        long newTriples = provModel.size()-initialSize;
			logger.info("Added "+ newTriples + " triples into prov store ");
			return newTriples;
		} catch(Exception exp) {
			logger.severe("Can't add new triples into store because of " + exp);
			return 0;
		}
	}
	
	
	
	
	/**
	 * Inserts the input statement into triple store
	 * 
	 * @param stmt Input Statement to be inserted into this triple store
	 * @return Number of triples inserted
	 */
	public long insert(Resource subj, Property prop, RDFNode obj) {
		try {
			long initialSize = infModel.size();
			//infModel.register(larqBuilder);
			infModel.add(subj, prop, obj);
			infModel.commit();
			//larqBuilder.closeWriter();
            //infModel.unregister(larqBuilder);
			long addTriples = initialSize - infModel.size();
			logger.info("Added "+addTriples+" triples from store");
			return addTriples;
		} catch(Exception exp) {
			logger.severe("Can't delete triples from store because of "+exp);			
			return 0;
		}
	}
	/**
	 * Deletes the given model from the store
	 * 
	 * @param model RDF model to be deleted from this store
	 * @return Number of triples deleted from the store
	 */
	public void remove(Model model) {
		try {
			long initialSize = infModel.size();
			infModel.remove(model);
			infModel.commit();
			long deletedTriples = initialSize - infModel.size();
			logger.info("Removed "+deletedTriples+" triples from store");
			//return deletedTriples;
		} catch(Exception exp) {
			logger.severe("Can't delete triples from store because of "+exp);
			//return 0;
		}
	}
	
	/**
	 * Removes all the statements matching (s, p, o) from this store
	 * 
	 * @param subj Subject part of the triple
	 * @param prop Property part of the triple
	 * @param obj Object/Value part of the triple
	 * @return Number of triples deleted from the store
	 */
	public long remove(Resource subj, Property prop, RDFNode obj) {
		try {
			long initialSize = infModel.size();
			infModel.removeAll(subj, prop, obj);
			infModel.commit();
			long deletedTriples = initialSize - infModel.size();
			logger.info("Removed "+deletedTriples+" triples from store");
			return deletedTriples;
		} catch(Exception exp) {
			logger.severe("Can't delete triple from store because of "+exp);
			return 0;
		}
	}
	
	// does m contain r as s, p or o?
	public boolean exists(Resource res) {
		logger.info("Checking existance as a resource for " + res);
		return baseModel.containsResource(res);
	}
	
	public boolean existsAsSubject(Resource sub) {
		// does m contain r as a subject?
		logger.info("Checking existance as a subject for " + sub);
		return baseModel.contains(sub, null, (RDFNode) null);
	}
	
	
	public String getLiteral(Resource subject, Property property) {
		Statement stmt = provModel.getRequiredProperty(subject, property);
		String value = stmt.getLiteral().getLexicalForm();
		return value;
	}
	
	/**
	 * Executes given describe query
	 *
	 * @param query The SPARQL describe query
	 * @param lang Serialization language (see {@link OntMediaType})
	 * @return The resulting model serialized in the specified language
	 */
//	public String execDescribe(String query, String lang) {
//		QueryExecution qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11,infModel);
//		logger.info("Query parsed successfully");
//		String str = ""; //FIXME not very elegant
//		try {
//			Model result = qexec.execDescribe();
//			logger.info("Describe query returned "+result.size()+" triples");
//			ByteArrayOutputStream byteout = new ByteArrayOutputStream();
//			result.write(byteout, lang);
//			logger.info("Model serialized successfully");
//			str = byteout.toString();
//		} catch (Exception exp) {
//			logger.info("Can't process describe query because of "+exp);
//		} finally {
//			qexec.close();
//		}
//		return str;
//	}
	
	/**
	 * Executes given describe query
	 *
	 * @param query The SPARQL describe query
	 * @param lang Serialization language (see {@link OntMediaType})
	 * @return The resulting model serialized in the specified language
	 */
	public boolean execAsk(String query) {
		QueryExecution qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11,infModel);
		logger.info("Query parsed successfully");
		boolean result = false;
		try {
			result = qexec.execAsk();
			logger.info("Model serialized successfully");
		} catch (Exception exp) {
			logger.info("Can't process describe query because of "+exp);
		} finally {
			qexec.close();
		}
		return result;
	}
	/**
	 * Executes given construct type query
	 *
	 * @param query The SPARQL construct query
	 * @param lang Serialization language (see {@link OntMediaType})
	 * @return The resulting model in Turtle serialization
	 */
	public String execConstruct(String query, String lang) {
		QueryExecution qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11, infModel);
		logger.info("Parsed query successfully");
		String str = ""; //FIXME not very elegant
		try {
			Model result = qexec.execConstruct();
			logger.info("Construct query returned "+result.size()+" triples");
			ByteArrayOutputStream byteout = new ByteArrayOutputStream();
			result.write(byteout, lang);
			logger.info("Model serialized successfully");
			str = byteout.toString();
		} catch (Exception exp) {
			logger.info("Can't process construct query because of "+exp);
		} finally {
			qexec.close();
		}
		return str;
	}
	
	/**
	 * Executes given describe query
	 *
	 * @param query The SPARQL describe query
	 * @param lang Serialization language (see {@link OntMediaType})
	 * @return The resulting model serialized in the specified language
	 */
	public String execSelect(String query, String lang) {
		QueryExecution qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11,infModel);
		logger.info("Query parsed successfully");
		String str = ""; //FIXME not very elegant
		try {
			 ResultSet results = qexec.execSelect();
			 logger.info("Query returned "+results.getRowNumber()+" bindings");
			 ByteArrayOutputStream byteout = new ByteArrayOutputStream();
             //ResultSetFormatter.outputAsRDF(byteout, lang, results);
             logger.info("Model serialized successfully");
			 str = byteout.toString();
		} catch (Exception exp) {
			logger.info("Can't process describe query because of "+exp);
		} finally {
			qexec.close();
		}
		return str;
	}
	
	/**
	 * Executes given SELECT query
	 *
	 * @param query The SPARQL describe query
	 * @return The resulting model serialized as JSON
	 */
	
	public String execSelect(String query) {
		QueryExecution qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11,baseModel);
		logger.info("Query parsed successfully");
		String str = ""; //FIXME not very elegant
		try {
			 ResultSet results = qexec.execSelect();
			 logger.info("Query returned "+results.getRowNumber()+" bindings");
			 ByteArrayOutputStream resultOutputStream = new ByteArrayOutputStream();
             ResultSetFormatter.outputAsJSON(resultOutputStream, results);
             logger.info("Model serialized successfully");
			 str = resultOutputStream.toString();
		} catch (Exception exp) {
			logger.info("Can't process describe query because of "+exp);
		} finally {
			qexec.close();
		}
		return str;
	}
	
	
	public ResultSet execProvSelect(String query) {
		QueryExecution qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11,provModel);
		logger.info("Query parsed successfully");
		ResultSet results = qexec.execSelect();
		logger.info("Query returned "+results.getRowNumber()+" bindings");
		return results;
	}
	
	/**
	 * Executes SELECT query and returns <code>ResultSet</code> object.
	 * 
	 * @param query SPARQL SELECT query
	 * @return ResultSet instance
	 */
	public ResultSet executeSelect(String query) {
		QueryExecution qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11,infModel);
		logger.info("Query parsed successfully");
		ResultSet results = qexec.execSelect();
		logger.info("Query returned "+results.getRowNumber()+" bindings");
		return results;
	}
	
	public String execSelectonSchema(String query) {
		QueryExecution qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11, schemaModel);
		logger.info("Query parsed successfully");
		String str = ""; //FIXME not very elegant
		try {
			 ResultSet results = qexec.execSelect();
			 logger.info("Query returned "+results.getRowNumber()+" bindings");
			 ByteArrayOutputStream resultOutputStream = new ByteArrayOutputStream();
             ResultSetFormatter.outputAsJSON(resultOutputStream, results);
             logger.info("Model serialized successfully");
			 str = resultOutputStream.toString();
		} catch (Exception exp) {
			logger.info("Can't process describe query because of "+exp);
		} finally {
			qexec.close();
		}
		return str;
	}
	
	
	/**
	 * Executes given Construct query
	 *
	 * @param query The SPARQL describe query
	 * @param lang Serialization language (see {@link OntMediaType})
	 * @return The resulting ResultSet
	 */
	public Model execConstruct(String query) {
		Model _model = TDBFactory.createDataset().getDefaultModel();
		QueryExecution qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11,baseModel);
		logger.info("Query parsed successfully");
		try {
			 _model = qexec.execConstruct();
		} catch (Exception exp) {
			logger.info("Can't process describe query because of "+exp);
		} finally {
			qexec.close();
		}
		return _model;
	}
	
	/**
	 * Executes given Describe query
	 *
	 * @param query The SPARQL describe query
	 * @return The resulting Model
	 */
	public Model execDescribe(String query, String queryModel) {
		Model _model = TDBFactory.createDataset().getDefaultModel();
		QueryExecution qexec = null;
		if (queryModel.equalsIgnoreCase("provModel") ){
			qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11, provModel);
		} else if (queryModel.equalsIgnoreCase("baseModel")) {
			qexec = QueryExecutionFactory.create(query,Syntax.syntaxSPARQL_11, baseModel);
		}
		logger.info("Query parsed successfully");
		try {
			 _model = qexec.execDescribe();
		} catch (Exception exp) {
			logger.info("Can't process describe query because of "+exp);
		} finally {
			qexec.close();
		}
		return _model;
	}
	

}