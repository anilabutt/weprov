package com.csiro.dataset;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.json.JSONException;

import com.csiro.webservices.app.beans.ControllerBean;
import com.csiro.webservices.app.beans.PortBean;
import com.csiro.webservices.app.beans.ProgramBean;
import com.csiro.webservices.app.beans.Workflow;
import com.csiro.webservices.logic.RevisionProvenance;
import com.csiro.webservices.store.WeProvData;
import com.csiro.webservices.store.WeProvOnt;

public class WorkflowComparison {
	public Model provModel  = TDBFactory.createDataset().getDefaultModel();
	 public Property rdfTypeProperty = provModel.getProperty(WeProvOnt.rdfType);
	 public Resource Revision = provModel.getResource(WeProvOnt.Revision);
	 public Property wasPartOf = provModel.getProperty(WeProvOnt.wasPartOf);
	
	public Model getRevisionProvenance(Workflow w1, Workflow w2) throws JSONException {
		
		provModel.add(compareWorkflow(w1, w2)) ;
	
		
		return provModel;
	}

	public Model compareWorkflow(Workflow w1, Workflow w2) throws JSONException {
		TavernaRevisionProvenance rp = new TavernaRevisionProvenance();
		Model _model = TDBFactory.createDataset().getDefaultModel();
		Resource _revision = _model.getResource(WeProvData.revision + w1.getRevisionId()+ "/" + w1.getWorkflowId() );
		
		System.out.println("************************************************************************");
		
		boolean same=false;
		
		boolean id = compareId(w1.getWorkflowId(), w2.getWorkflowId());
		boolean title = compareTitle(w1.getWorkflowTitle(), w2.getWorkflowTitle());
		boolean desc = compareDescription(w1.getWorkflowDescription(), w2.getWorkflowDescription());
		boolean author = compareAuthor(w1.getAuthor(), w2.getAuthor());
		HashMap<String, ArrayList<String>> pDifference = compareProgramList(w1.getPrograms(), w2.getPrograms(), _revision.toString());
		HashMap<String, ArrayList<String>> inport = comparePort(w1.getInports(), w2.getInports());
		HashMap<String, ArrayList<String>> outport = comparePort(w1.getOutports(), w2.getOutports());
		HashMap<String, ArrayList<ControllerBean>>  controller = compareControllerList(w1.getControllers(), w2.getControllers());
	
		if((id==true) && 
				(title == true) && 
				(desc == true) && 
				(author == true) && 
				(pDifference.get("w1Diffw2").size() == 0 && pDifference.get("w2Diffw1").size() ==0) &&
				(inport.get("w1Diffw2").size() == 0 && inport.get("w2Diffw1").size() ==0) &&
				(outport.get("w1Diffw2").size() == 0 && outport.get("w2Diffw1").size() ==0) &&
				(controller.get("w1Diffw2").size() == 0 && controller.get("w2Diffw1").size() ==0) ) 
		{
			same=true;
			System.out.println("For workflow : " + w1.getWorkflowTitle()+ "   " + w2.getWorkflowTitle());
			System.out.println("For workflow : " + w1.getWorkflowId()+ "   " + w2.getWorkflowId());
			System.out.println(" . . . Same . . .");
			//_model = rp.generateWorkflowRevisionRecords(w1, w2);
			
		} else {
			System.out.println("For workflow : " + w1.getWorkflowTitle()+ "   " + w2.getWorkflowTitle());
			System.out.println("For workflow : " + w1.getWorkflowId()+ "   " + w2.getWorkflowId());
			System.out.println("... Different ...");
			
			_model = rp.generateWorkflowRevisionRecords(w1, w2);
			_model.add(rp.generateRevisionRDF(w1, w2));


		if(id == false) {
			System.out.println("id is changed ! ");
			_model.add(rp.generateRenameRecords(w1, w2, "id"));
		} if(title == false) {
			System.out.println("Title is changed ! ");
			_model.add(rp.generateRenameRecords(w1, w2, "title"));
		} if(desc == false) {
			System.out.println("Description is changed ! ");
			_model.add(rp.generateRenameRecords(w1, w2, "description"));
		} if(author == false) {
			System.out.println("Author is changed ! ");
			_model.add(rp.generateRenameRecords(w1, w2, "author"));
		} 

		
		//Update programs 
		
		System.out.println("Checking Programs ..............");
		
		//System.out.println("w1 Programs ..............");
		
		if(pDifference.get("w1Diffw2").size()==0) {
			//System.out.println("w1 has not added or deleted any program");
		} else {
			System.out.println("Following programs are added in w1 .... ");
			for(int i=0; i < pDifference.get("w1Diffw2").size(); i++) {
					System.out.println(pDifference.get("w1Diffw2").get(i));
					_model.add(rp.generateProgramGenerationRecords(w1, w2, pDifference.get("w1Diffw2").get(i)));
			}
		} 
		
		if(pDifference.get("w2Diffw1").size()==0) {
			//System.out.println("w1 has not deleted following program from previous version");
		} else {
			System.out.println("Following programs are deleted from w1.... ");
			for(int i=0; i < pDifference.get("w2Diffw1").size(); i++) {
				System.out.println(pDifference.get("w2Diffw1").get(i));
				_model.add(rp.generateProgramInvalidationRecords(w1, w2, pDifference.get("w2Diffw1").get(i)));
			}
		} 
		
		
		//Update Inports
		
		System.out.println("Checking inputs ..............");
		
		if(inport.get("w1Diffw2").size()==0) {
			//System.out.println("w1 has not added or deleted any inputs");
		} else {
			System.out.println("Following inputs are added in w1 .... ");
			for(int i=0; i < inport.get("w1Diffw2").size(); i++) {
				System.out.println(inport.get("w1Diffw2").get(i));
				_model.add(rp.generatePortGenerationRecords(w1, w2, inport.get("w1Diffw2").get(i)));
				
			}
		} 
		
		if(inport.get("w2Diffw1").size()==0) {
			//System.out.println("w1 has not deleted any inputs from previous version");
		} else {
			System.out.println("Following inputs are deleted .... ");
			for(int i=0; i < inport.get("w2Diffw1").size(); i++) {
				System.out.println(inport.get("w2Diffw1").get(i));
				_model.add(rp.generatePortInvalidationRecords(w1, w2, inport.get("w2Diffw1").get(i)));
			}
		}
		
		
		//Update Outputs
		
		System.out.println("Checking outputs ..............");
				if(outport.get("w1Diffw2").size()==0) {
					//System.out.println("w1 has not added any output");
				} else {
					System.out.println("Following outputs are added .... ");
					for(int i=0; i < outport.get("w1Diffw2").size(); i++) {
					System.out.println(outport.get("w1Diffw2").get(i));
					
					_model.add(rp.generatePortGenerationRecords(w1, w2, outport.get("w1Diffw2").get(i)));
					
					}
				} 
				
				if(outport.get("w2Diffw1").size()==0) {
					//System.out.println("w1 has not deleted any output from previous version");
				} else {
					System.out.println("Following outputs are deleted .... ");
					for(int i=0; i < outport.get("w2Diffw1").size(); i++) {
						System.out.println(outport.get("w2Diffw1").get(i));
						_model.add(rp.generatePortInvalidationRecords(w1, w2, outport.get("w2Diffw1").get(i)));
					}
				} 
				
		 //Update Controllers
				
				System.out.println("Checking Controllers ..............");
						if(controller.get("w1Diffw2").size()==0) {
							//System.out.println("w1 has not added any controller");
						} else {
							//System.out.println("Following controllers are added .... ");
							for(int i=0; i < controller.get("w1Diffw2").size(); i++) {
								System.out.print(controller.get("w1Diffw2").get(i).getSource().getProgramId() + "."+ controller.get("w1Diffw2").get(i).getSource().getPortId());
								System.out.println("-->"+ controller.get("w1Diffw2").get(i).getTarget().getProgramId() + "."+ controller.get("w1Diffw2").get(i).getTarget().getPortId());

								_model.add(rp.generateControllerGenerationRecords(w1, w2, controller.get("w1Diffw2").get(i)));
							}
						} 
						
						if(controller.get("w2Diffw1").size()==0) {
							//System.out.println("w1 has not deleted any controllers from previous version");
						} else {
							//System.out.println("Following controllers are deleted .... ");
							for(int i=0; i < controller.get("w2Diffw1").size(); i++) {
								System.out.print(controller.get("w2Diffw1").get(i).getSource().getProgramId() + "."+ controller.get("w2Diffw1").get(i).getSource().getPortId());
								System.out.println("-->"+controller.get("w2Diffw1").get(i).getTarget().getProgramId() + "."+ controller.get("w2Diffw1").get(i).getTarget().getPortId());
								_model.add(rp.generateControllerInvalidationRecords(w1, w2, controller.get("w2Diffw1").get(i)));
							}
						} 
						
		}
		
					//_model.write(System.out, "TTL");
		
					provModel.add(_model);
					System.out.println("-------------------------------------------------------------");
					return _model;

	}
	
	public boolean compareId(String id1, String id2) {
		boolean comparison = false; 
		if (id1.equalsIgnoreCase(id2) ){
			//System.out.println("Workflow Ids are same ..... \n" + id1+ "\n" + id2);
			comparison = true;
		} else {
			//System.out.println("Workflow Ids are different ..... \n" + id1+ "\n" + id2);
		}
		return comparison;
	}
	
	public boolean compareTitle(String t1, String t2) {
		boolean comparison = false; 
		if (t1.equalsIgnoreCase(t2) ){
			comparison = true;
		} else {}
		return comparison;
	}
	
	public boolean compareDescription(String des1, String des2) {
		boolean comparison = false; 
		if (des1.equalsIgnoreCase(des2) ){
			comparison = true;
		} else {}
		return comparison;
	}
	
	public boolean compareAuthor(String author1, String author2) {
		boolean comparison = false; 
		if (author1.equalsIgnoreCase(author2) ){
			//System.out.println("Workflow authors are same ..... \n" + author1+ "\n" + author2);
			comparison = true;
		} else {
			//System.out.println("Workflow authors are different ..... \n" + author1+ "\n" + author2);
		}
		return comparison;
	}
	
	public boolean compareVersion(String v1, String v2) {
		boolean comparison = false; 
		if (v1.equalsIgnoreCase(v2) ){
			//System.out.println("Workflow versions are same ..... \n" + v1+ "\n" + v2);
			comparison = true;
		} else {
			//System.out.println("Workflow versions are different ..... \n" + v1+ "\n" + v2);
		}
		return comparison;
	}
	
	
	

	public HashMap<String, ArrayList<String>> comparePort(ArrayList<PortBean> plist1, ArrayList<PortBean> plist2) {
		
		HashMap<String, ArrayList<String>> difference = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> w1Diffw2 = new ArrayList<String>();
		ArrayList<String> w2Diffw1 = new ArrayList<String>();
			
			for(int i=0; i<plist1.size(); i++) {

				PortBean port1 = plist1.get(i) ;
				
				w1Diffw2.add(port1.getPortId());
				
				for(int j=0; j<plist2.size(); j++) {
					
					PortBean port2 = plist2.get(j) ;
					
					if(port1.getPortId().equalsIgnoreCase(port2.getPortId())) {
						w1Diffw2.remove(port1.getPortId());
						break;
					} else { }					
				}
			}
			//System.out.println("w1Diffw2 : " + w1Diffw2);
			difference.put("w1Diffw2", w1Diffw2);
			
			for(int i=0; i<plist2.size(); i++) {

				PortBean port1 = plist2.get(i) ;
				
				w2Diffw1.add(port1.getPortId());
				
				for(int j=0; j<plist1.size(); j++) {
					
					PortBean port2 = plist1.get(j) ;
					
					if(port1.getPortId().equalsIgnoreCase(port2.getPortId())) {
						w2Diffw1.remove(port1.getPortId());
						break;
					} else { }					
				}
			}
			//System.out.println("w2Diffw1 : " + w2Diffw1);
			difference.put("w2Diffw1", w2Diffw1);
		
			return difference;
	}
	
	public HashMap<String, ArrayList<ControllerBean>> compareControllerList(ArrayList<ControllerBean> plist1, ArrayList<ControllerBean> plist2) {
	
		HashMap<String, ArrayList<ControllerBean>> difference = new HashMap<String, ArrayList<ControllerBean>>();
		
		ArrayList<ControllerBean> w1Diffw2 = new ArrayList<ControllerBean>();
		ArrayList<ControllerBean> w2Diffw1 = new ArrayList<ControllerBean>();
				
			for(int i=0; i<plist1.size(); i++) {

				ControllerBean link1 = plist1.get(i) ;
				
				w1Diffw2.add(link1);
				
				for(int j=0; j<plist2.size(); j++) {
					
					ControllerBean link2 = plist2.get(j) ;
					
					if(compareController(link1, link2)) {
						w1Diffw2.remove(link1);
						break;
					} else { }					
				}
			}

			difference.put("w1Diffw2", w1Diffw2);
			
			for(int i=0; i<plist2.size(); i++) {

				ControllerBean link1 = plist2.get(i) ;
				
				w2Diffw1.add(link1);
				
				for(int j=0; j<plist1.size(); j++) {
					
					ControllerBean link2 = plist1.get(j) ;
					
					if(compareController(link1, link2)) {
						w2Diffw1.remove(link1);
						break;
					} else { }					
				}
			}
			
			difference.put("w2Diffw1", w2Diffw1);
		
			return difference;
	}
	
	public HashMap<String, ArrayList<String>> compareProgramList(ArrayList<ProgramBean> plist1, ArrayList<ProgramBean> plist2, String revisionId) throws JSONException {
		//boolean comparison = false; 
		
		HashMap<String, ArrayList<String>> difference = new HashMap<String, ArrayList<String>>();
			
		ArrayList<String> w1Diffw2 = new ArrayList<String>();
		ArrayList<String> w2Diffw1 = new ArrayList<String>();
		ArrayList<String> w1Samew2 = new ArrayList<String>();
		
			for(int i=0; i<plist1.size(); i++) {

				ProgramBean prog1 = plist1.get(i) ;
				
				w1Diffw2.add(prog1.getProgramId());
				
				for(int j=0; j<plist2.size(); j++) {
					
					ProgramBean prog2 = plist2.get(j) ;
					
				//	Model _proModel = compareProgram(prog1, prog2);
					
					//_proModel.write(System.out, "TTL");
					//StmtIterator stmt = _proModel.listStatements();
					
					//while(stmt.hasNext()) {
						
					//}
					if(compareProgram(prog1, prog2, revisionId)==true) {
						w1Diffw2.remove(prog1.getProgramId());
						w1Samew2.add(prog2.getProgramId());
						break;
					} else { }					
				}
			}
			
			//System.out.println("w1Diffw2 : " + w1Diffw2);
			//System.out.println("w1Samew2 : " + w1Samew2);
			
			
			
			difference.put("w1Diffw2", w1Diffw2);
			
			
			//System.out.println("Starting W2 Programs - W1 Programs");
			
			
			for(int i=0; i<plist2.size(); i++) {

				ProgramBean prog1 = plist2.get(i) ;
				
				if(w1Samew2.contains(prog1.getProgramId()) ) {
					
				} else if(w1Diffw2.contains(prog1.getProgramId()) ) {
					//System.out.println(w1Samew2.contains(prog1.getProgramId()));
				} 
				else {
					w2Diffw1.add(prog1.getProgramId());
					
					for(int j=0; j<plist1.size(); j++) {
						
						ProgramBean prog2 = plist1.get(j) ;
						
						//Model _proModel = compareProgram(prog1, prog2);
						//_proModel.write(System.out, "TTL");
						if(w1Diffw2.contains(prog2.getProgramId()) ) {
							
						} else if(compareProgram(prog1, prog2, revisionId) == true) {
							w2Diffw1.remove(prog1.getProgramId());
							break;
						} else { }					
					}
				}

			}
			//System.out.println("w2Diffw1 : " + w2Diffw1);
			difference.put("w2Diffw1", w2Diffw1);
		return difference;
	}
	
	public boolean compareProgram(ProgramBean p1, ProgramBean p2, String revisionId) throws JSONException {
		
		boolean comparison = false; 
		//System.out.println("***" + p1.getProgramId() + "	" + p2.getProgramId() + "***"  );
		Model model= TDBFactory.createDataset().getDefaultModel();
		Resource revision = model.createResource(revisionId);
		
		if(p1.getType()=="workflow" && p2.getType()=="workflow") {
			if(p1.getProgramId().equalsIgnoreCase(p2.getProgramId()) ){
				//System.out.println("***" + p1.getProgramId() + "	" + p2.getProgramId() + "***"  );
				model = compareWorkflow(p1.getWorkflow(), p2.getWorkflow()); 

				StmtIterator stmt = model.listStatements();				
				
				if(stmt.hasNext()) {
					ResIterator rs = model.listSubjectsWithProperty(rdfTypeProperty, Revision); 
					while (rs.hasNext()) {
						provModel.add(rs.next(), wasPartOf, revision);
					}
					
				}  else {comparison=true;}
				
				} else if( p1.getWorkflow().getWorkflowId().equalsIgnoreCase(p2.getWorkflow().getWorkflowId()) ) {
					//System.out.println("***" + p1.getProgramId() + "	" + p2.getProgramId() + "***"  );

					model =compareWorkflow(p1.getWorkflow(), p2.getWorkflow()); 
					StmtIterator stmt = model.listStatements();
					if(stmt.hasNext()) {
						ResIterator rs = model.listSubjectsWithProperty(rdfTypeProperty, Revision); 
						while (rs.hasNext()) {
							provModel.add(rs.next(), wasPartOf, revision);
						}
					}  else {comparison=true;}
				}
			
						
		} else if(p1.getProgramId().equalsIgnoreCase(p2.getProgramId()) && p1.getType().equalsIgnoreCase(p2.getType())) {	
			//System.out.println("***" + p1.getProgramId() + "	" + p2.getProgramId() + "***"  );
			comparison = true;
		} else {

		}

		
		//System.out.println(" Are these two Programs Same : " + comparison);
		return comparison;
	}
	
	public boolean compareController(ControllerBean c1, ControllerBean c2) {
		boolean comparison = false; 
		 
		if(c1.getSource().getProgramId().equalsIgnoreCase(c2.getSource().getProgramId()) && c1.getSource().getPortId().equalsIgnoreCase(c2.getSource().getPortId())) {
			
			if(c1.getTarget().getProgramId().equalsIgnoreCase(c2.getTarget().getProgramId()) && c1.getTarget().getPortId().equalsIgnoreCase(c2.getTarget().getPortId())) {
				//System.out.println(c1.getSource().getProgramId().equalsIgnoreCase(c2.getSource().getProgramId()+"."+);
				
				comparison = true;
			}
		}

		return comparison;
	}
	
	
	
}
