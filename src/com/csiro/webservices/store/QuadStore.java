/*
 * Copyright (c) 2015, ANU and/or its constituents or affiliates. All rights reserved.
 * Use is subject to license terms.
 */

package com.csiro.webservices.store;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import com.csiro.webservices.config.Configuration;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.query.ResultSet;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtInfGraph;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;
 
/**
 * RDF triple store interface for the API. Services can't instantiate this store, instead they
 * should use {@link #getDefaultStore} method to get its singleton.
 * 
 * @author anila butt
 */
public class QuadStore {

	/** Default instance of the triple store */
	private static QuadStore defaultStore;

	/** Default logger */
	private Logger logger;
	
	/** Virtuoso graph for the ontology*/
	private VirtModel  metamodel = null;
	
	private VirtModel specmodel = null;
	
	private VirtModel evomodel = null;
	
	private VirtModel execmodel= null;
	
	private VirtGraph connection = null;
	
	private String connectionURL = null;
	
	private String username = null;
	
	private String password = null;
	
	/** Sesame Repository connection */
	//private RepositoryConnection connection= null;
	/**
	 * Gets default triple store instance to avoid synchronization faults 
	 */
	public static QuadStore getDefaultStore() {
		if(defaultStore==null) {
			defaultStore = new QuadStore();
		}
		return defaultStore;
	}
	
	/**
	 * Initializes this triple store
	 */
	private QuadStore() {
		logger = Logger.getLogger(getClass().getName());
	
		connectionURL = "jdbc:virtuoso://" + Configuration.getProperty(Configuration.VIRTUOSO_INSTANCE) + ":" + Configuration.getProperty(Configuration.VIRTUOSO_PORT);
		logger.info("Connection URL :" + connectionURL);
		
		username = Configuration.getProperty(Configuration.VIRTUOSO_USERNAME);
		logger.info("VIRTUOSO USER :" + username);
		
		password = Configuration.getProperty(Configuration.VIRTUOSO_PASSWORD);
		logger.info("VIRTUOSO PASSWORD :" + password);
	
		
		connection = new VirtGraph("jdbc:virtuoso://localhost:1111", "dba", "dba");

		String ontologyprofix = Configuration.getProperty(Configuration.PREFIX_WEPROV);
		metamodel = VirtModel.openDatabaseModel(ontologyprofix, connectionURL, username , password );
		
		String specsprofix = Configuration.getProperty(Configuration.PREFIX_DATA);
		
		System.out.println(specsprofix);
		specmodel = VirtModel.openDatabaseModel(specsprofix, connectionURL, username , password );
		
		String evoprofix = Configuration.getProperty(Configuration.PREFIX_EVODATA);
		evomodel = VirtModel.openDatabaseModel(evoprofix, connectionURL, username , password);
		
		String execprofix = Configuration.getProperty(Configuration.PREFIX_DATA);
		execmodel = VirtModel.openDatabaseModel(execprofix, connectionURL, username , password);
		
		
		//connection = new VirtGraph(connectionURL, username , password );		
		//logger.info("Connection establised . . . " +connection.getCount());	
	}
	
	//insert triples into a graph	
	private void insert(Model _model, VirtModel model) {
		try {
			logger.info("triple list size " + model.size());			
			model.add(_model);
            logger.info("Triple loaded successfully");			
		} catch (Exception e) {
			logger.severe("Error[" + e + "]");
		}
	}
	
	public void insertOntology(Model model) {
		this.insert(model, metamodel);
	}
	
	public void insertSpecs(Model model) {
		this.insert(model, specmodel);
	}
	
	public void insertEvol(Model model) {
		this.insert(model, evomodel);
	}
	
	public void insertExec(Model model) {
		this.insert(model, execmodel);
	}


	//delete this model from virtmode
	private void delete(Model _model, VirtModel model) {
			try {
				model.remove(_model);
	            logger.info("Triple deleted successfully");			
			} catch (Exception e) {
				logger.severe("Error[" + e + "]");
			}
		}
	
	public void deleteOntology(Model model) {
		this.delete(model, metamodel);
	}
	
	public void deleteSpecs(Model model) {
		this.delete(model, specmodel);
	}
	
	public void deleteEvol(Model model) {
		this.delete(model, evomodel);
	}
	
	public void deleteExec(Model model) {
		this.delete(model, execmodel);
	}

	
	
	/**
	 * Executes given Graph type query
	 *
	 * @param query The SPARQL construct or describe query
	 * @return The resulting graph as a string
	 * @throws MalformedQueryException 
	 * @throws RepositoryException 
	 */
   	public Model execConstruct(String query) {
   		//VirtGraph connection = new VirtGraph("jdbc:virtuoso://localhost:1111", "dba", "dba");
   		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, connection);
   		
   		Model _model = null;
       logger.info("Query parsed successfully");
        try {
        	_model = vqe.execConstruct();
        } catch (Exception exp) {
        		logger.info("Can't process construct query because of "+exp);
        } finally {
        	vqe.close();
        	}
        return _model;
   	}
   	
   	
   	/**
	 * Executes given Graph type query
	 *
	 * @param query The SPARQL construct or describe query
	 * @return The resulting graph as a string
	 * @throws MalformedQueryException 
	 * @throws RepositoryException 
	 */
   	public boolean execAsk(String query) {
   		
   		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, connection);
   		
   	    boolean value = false;
   	    logger.info("Query String : "+ query);
        logger.info("Query parsed successfully");
        try {
        	value = vqe.execAsk();
        } catch (Exception exp) {
        		logger.info("Can't process ask query because of "+exp);
        } finally {
        	vqe.close();
        	}
        return value;
   	}
   	
   	
   	public ResultSet execSelect(String query) {
   		
   		logger.info(query);
   		
   		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, connection);

        logger.info("Query parsed successfully");
        
   		ResultSet results = null;

      try {
    	  results = vqe.execSelect();
    	  
    	  logger.info("Result Set contains :" + results.getResultVars().toString() + results.getRowNumber());

      	  } catch (Exception exp) {
      		  logger.info("Can't process select query because of "+exp);
      	  } finally {
      		  //vqe.close();
      	  }
      return results;
 	}
}
   	
