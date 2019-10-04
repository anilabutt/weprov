package com.csiro.webservices.store;


public class WeProvOnt {
	
	public static final String weprov = "http://www.csiro.au/digiscape/weprov#";
	public static final String provone = "http://purl.dataone.org/provone/2015/01/15/ontology#";
	public static final String prov = "http://www.w3.org/ns/prov#";
	public static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String foaf = "http://xmlns.com/foaf/0.1/";
	
	//Class declaration
		
		  public static final String Agent =  prov + "Agent" ;
		  public static final String Activity =  prov+"Activity" ;
		  
		  public static final String Workflow =  provone+"Workflow" ;
		  public static final String Program =  provone+"Program" ;
		  public static final String Model =  weprov+"Model" ;
		  public static final String Port =  provone+"Port" ;
		  public static final String Channel =  provone+"Channel" ;
		  public static final String Controller =  provone+"Controller" ;
		  
		  public static final String Invalidation =  prov+"Invalidation" ;
		  public static final String Usage =  prov+"Usage" ;
		  public static final String Generation =  prov+"Generation" ;
		
		  public static final String Derivation = prov+"Derivation" ;
		  public static final String Revision =  prov+"Revision" ;
		
		  public static final String Creation =  weprov+"Creation" ;
		  public static final String Deletion =  weprov+"Deletion" ;
		  public static final String Modification =  weprov+"Modification" ;
		  public static final String Renaming =  weprov+"Renaming" ;


	  	//public static final String  Declaration

		//General Properties
		public static final String  rdfType  =  rdf+"type" ;
			
		// Associations
		
		public static final String  hasSubProgram =  provone+"hasSubProgram" ;
		public static final String  hasInPort =  provone+"hasInPort" ;
		public static final String  hasOutPort =  provone+"hasOutPort" ;
		public static final String  host =  weprov+"host" ;
		public static final String  connectsTo =  provone+"connectsTo" ;
		public static final String  hasDefaultParam =  provone+"hasDefaultParam" ;
		public static final String  controlledBy =  provone+"controlledBy" ;
		public static final String  controls =  provone+"controls" ;

		public static final String  wasAssociatedWith =  prov+"wasAssociatedWith" ;
		public static final String  wasGeneratedBy =  prov+"wasGeneratedBy" ;
		public static final String  wasInvalidatedBy =  prov+"wasInvalidatedBy" ;
		public static final String  wasDerivationOf =  prov+"wasDerivationOf" ;
		public static final String  wasRevisionOf =  prov+"wasRevisionOf" ;
		public static final String  wasPartOf =  weprov+"wasPartOf" ;
		
		public static final String  qualifiedGeneration =  prov+"qualifiedGeneration" ;
		public static final String  qualifiedInvalidation =  prov+"qualifiedInvalidation" ;
		public static final String  qualifiedDerivation =  prov+"qualifiedDerivation" ;
		public static final String  qualifiedUsage =  prov+"qualifiedUsage" ;
		public static final String  qualifiedRevision =  prov+"qualifiedRevision" ;
				
		public static final String  hadGeneration =  prov+"hadGeneration" ;
		public static final String  hadUsage =  prov+"hadUsage" ;
		public static final String  hadActivity =  prov+"hadActivity" ;
		public static final String  hadInvalidation =  prov+"hadInvalidation" ;
		
		public static final String  agent =  prov+"agent" ;
		public static final String  entity =  prov+"entity" ;
		public static final String  activity =  prov+"activity" ;
					
		// Properties
		public static final String  workflowId =  weprov+"workflowId" ;
		public static final String  versionId =  weprov+"versionId" ;
		public static final String  programId =  weprov+"programId" ;
		public static final String  modelId =  weprov+"modelId" ;
		public static final String  portId =  weprov+"portId" ;
		public static final String  channelId =  weprov+"channelId" ;
		public static final String  paramId =  weprov+"paramId" ;
		public static final String  atTime =  prov+"atTime" ;
		public static final String  startedAtTime =  prov+"startedAtTime" ;
		public static final String  endedAtTime =  prov+"endedAtTime" ;
		public static final String  value =  prov+"value" ;
		public static final String  foafname = foaf+"name";
		public static final String version = weprov+"version";

}
