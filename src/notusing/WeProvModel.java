package notusing;

import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class WeProvModel {
	
	public static String weprov = "http://www.csiro.au/digiscape/weprov#";
	public static String provone = "http://purl.dataone.org/provone/2015/01/15/ontology#";
	public static String prov = "http://www.w3.org/ns/prov#";
	public static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	
	public ArrayList<Resource> getResourcesList(Model _model) {
	
		ArrayList<Resource> resourceList = new ArrayList<Resource>();
		//Class declaration
		
		  Resource Agent = _model.getResource(prov + "Agent");
		  Resource Activity = _model.getResource(prov+"Activity");
		  
		  Resource Workflow = _model.getResource(provone+"Workflow");
		  Resource Program = _model.getResource(provone+"Program");
		  Resource Model = _model.getResource(weprov+"Model");
		  Resource Port = _model.getResource(provone+"Port");
		  Resource Channel = _model.getResource(provone+"Channel");
		  Resource Controller = _model.getResource(provone+"Controller");
		  
		  Resource Invalidation = _model.getResource(prov+"Invalidation");
		  Resource Usage = _model.getResource(prov+"Usage");
		  Resource Generation = _model.getResource(prov+"Generation");
		
		  Resource Derivation = _model.getResource(prov+"Derivation");
		  Resource Revision = _model.getResource(prov+"Revision");
		
		  Resource Creation = _model.getResource(weprov+"Creation");
		  Resource Deletion = _model.getResource(weprov+"Deletion");
		  Resource Modification = _model.getResource(weprov+"Modification");
		  Resource Renaming = _model.getResource(weprov+"Renaming");
		  
		  
		  resourceList.add(Agent);
		  resourceList.add(Activity);
		  resourceList.add(Workflow);
		  resourceList.add(Program);
		  resourceList.add(Model);
		  resourceList.add(Port);
		  resourceList.add(Channel);
		  resourceList.add(Controller);
		  resourceList.add(Invalidation);
		  resourceList.add(Usage);
		  resourceList.add(Generation);
		  resourceList.add(Derivation);
		  resourceList.add(Revision);
		  resourceList.add(Creation);
		  resourceList.add(Deletion);
		  resourceList.add(Modification);
		  resourceList.add(Renaming);
		  		  
		  return resourceList;
		  
	}
	
	public ArrayList<Property> getPropertyList(Model _model) {
	  	//Property Declaration
	  
		ArrayList<Property> propertyList = new ArrayList<Property>();

		//General Properties
		Property rdfTypeProperty = _model.getProperty(rdf+"type");
			
		propertyList.add(rdfTypeProperty);
			
		// Associations
		
		Property hasSubProgram = _model.getProperty(provone+"hasSubProgram");
		Property hasInPort = _model.getProperty(provone+"hasInPort");
		Property hasOutPort = _model.getProperty(provone+"hasOutPort");
		Property host = _model.getProperty(weprov+"host");
		Property connectsTo = _model.getProperty(provone+"connectsTo");
		Property hasDefaultParam = _model.getProperty(provone+"hasDefaultParam");
		Property controlledBy = _model.getProperty(provone+"controlledBy");
		Property controls = _model.getProperty(provone+"controls");
		
		propertyList.add(hasSubProgram);
		propertyList.add(hasInPort);
		propertyList.add(hasOutPort);
		propertyList.add(host);
		propertyList.add(connectsTo);
		propertyList.add(hasDefaultParam);
		propertyList.add(controlledBy);
		propertyList.add(controls);
		
		Property wasAssociatedWith = _model.getProperty(prov+"wasAssociatedWith");
		Property wasGeneratedBy = _model.getProperty(prov+"wasGeneratedBy");
		Property wasInvalidatedBy = _model.getProperty(prov+"wasInvalidatedBy");
		Property wasDerivationOf = _model.getProperty(prov+"wasDerivationOf");
		Property wasRevisionOf = _model.getProperty(prov+"wasRevisionOf");
		
		propertyList.add(wasAssociatedWith);
		propertyList.add(wasGeneratedBy);
		propertyList.add(wasInvalidatedBy);
		propertyList.add(wasDerivationOf);
		propertyList.add(wasRevisionOf);			
		
		Property qualifiedGeneration = _model.getProperty(prov+"qualifiedGeneration");
		Property qualifiedInvalidation = _model.getProperty(prov+"qualifiedInvalidation");
		Property qualifiedDerivation = _model.getProperty(prov+"qualifiedDerivation");
		Property qualifiedUsage = _model.getProperty(prov+"qualifiedUsage");
		Property qualifiedRevision = _model.getProperty(prov+"qualifiedRevision");
		
		propertyList.add(qualifiedGeneration);
		propertyList.add(qualifiedInvalidation);
		propertyList.add(qualifiedDerivation);
		propertyList.add(qualifiedUsage);
		propertyList.add(qualifiedRevision);			
		
		Property hadGeneration = _model.getProperty(prov+"hadGeneration");
		Property hadUsage = _model.getProperty(prov+"hadUsage");
		Property hadActivity = _model.getProperty(prov+"hadActivity");
		Property hadInvalidation = _model.getProperty(prov+"hadInvalidation");
		
		propertyList.add(hadGeneration);
		propertyList.add(hadUsage);
		propertyList.add(hadActivity);
		propertyList.add(hadInvalidation);
		
		Property agent = _model.getProperty(prov+"agent");
		Property entity = _model.getProperty(prov+"entity");
		Property activity = _model.getProperty(prov+"activity");
					
		propertyList.add(agent);
		propertyList.add(entity);
		propertyList.add(activity);
		
		// Properties
		Property workflowId = _model.getProperty(weprov+"workflowId");
		Property versionId = _model.getProperty(weprov+"versionId");
		Property programId = _model.getProperty(weprov+"programId");
		Property modelId = _model.getProperty(weprov+"modelId");
		Property portId = _model.getProperty(weprov+"portId");
		Property channelId = _model.getProperty(weprov+"channelId");
		Property paramId = _model.getProperty(weprov+"paramId");
		Property atTime = _model.getProperty(prov+"atTime");
		Property startedAtTime = _model.getProperty(prov+"startedAtTime");
		Property endedAtTime = _model.getProperty(prov+"endedAtTime");
		Property value = _model.getProperty(prov+"value");
		
		propertyList.add(workflowId);
		propertyList.add(versionId);
		propertyList.add(programId);
		propertyList.add(modelId);
		propertyList.add(portId);
		propertyList.add(channelId);
		propertyList.add(paramId);
		propertyList.add(atTime);
		propertyList.add(startedAtTime);
		propertyList.add(endedAtTime);
		propertyList.add(value);
		
		return propertyList;
	}
}
