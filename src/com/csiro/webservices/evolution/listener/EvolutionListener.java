package com.csiro.webservices.evolution.listener;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.compose.Difference;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;
import org.json.JSONObject;

import com.csiro.webservices.config.Configuration;
import com.csiro.webservices.logic.CreationProvenance;
import com.csiro.webservices.rest.GenericService;
//import com.csiro.webservices.rest.Workflow;

public class EvolutionListener extends GenericService {

	public static String wedata = Configuration.NS_RES;
	
	/**
	 * Default constructor to initializes logging by its parent.
	 */
	public EvolutionListener() {
		super(EvolutionListener.class.getName());
	}
		
	public boolean previousVersionExists(String _workflowId) throws JSONException {
		
		Model _model = TDBFactory.createDataset().getDefaultModel();
		
		Resource workflowInstance = _model.getResource(wedata+"workflow/"+_workflowId);
	
		return contains(workflowInstance);
	}
	
	public void evolutionActivity(Model _current, Model _previous) throws JSONException {
		
		Difference newOldDiff = new Difference ( _current.getGraph(), _previous.getGraph());
		Difference oldNewDiff = new Difference ( _previous.getGraph(), _current.getGraph());
		
		if(newOldDiff.isEmpty() && oldNewDiff.isEmpty()) {
			System.out.println("New Version is SameAs old one ");
		} else if(oldNewDiff.isEmpty() && !newOldDiff.isEmpty()) {
			
			ProgramListener plistener = new ProgramListener();
			
			plistener.addProgramEvolution(newOldDiff);
			
			ControllerListener clistener = new ControllerListener();
			
			clistener.addControllerEvolution(newOldDiff);
			
				
		}
		
	}
	
	public boolean getDifference(Model _current, Model _previous) {
		
		boolean diffFound = false;
		Difference diff = new Difference ( _current.getGraph(), _previous.getGraph());
		
		ExtendedIterator<Triple> iter = diff.find();
		
		if (iter.hasNext()) {
			
			while (iter.hasNext()) {
				Triple tr = iter.next();
				System.out.println(tr.getSubject()  + "\t" + tr.getPredicate() + "\t" + tr.getObject()); 
			}
			
			diffFound = true;
		}
		return diffFound;
	}

}
