package com.csiro.dataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.jena.rdf.model.Model;
import org.json.JSONException;

import com.csiro.webservices.app.beans.Workflow;

public class GenerateDataSet {
   
	public static void main(String args[]) {
		//getSpecificationProv() ;
		//getCreationProv() ;
		getEvolutionProv();
	}
	
	public static void getSpecificationProv() {
		
		TavernaWorkflowParser twp = new TavernaWorkflowParser();
		 
		try {
			String path = "C:\\Users\\but21c\\Dropbox\\CSIRO_Postdoc\\CSIRO_Papers\\EvolutionModelling\\dataset";
			String[] directories = getWorkflowDirectory(path);
		 
			for(int i=0; i <directories.length; i++) {
			   
			  String filePath = path +"\\"+ directories[i]+"\\workflow"; 			  
			  String spPath = path +"\\"+ directories[i] + "\\spec-prov";
			  File file = new File(spPath);
		      file.mkdir();

		      File versionfile = new File(filePath);
			  String[] files = versionfile.list();

			  System.out.println(filePath + "	" + files.length);
			  
			  for(int j=0; j <files.length; j++) {
				  
				   Workflow workflow = TavernaWorkflowParser.getWorkflow(filePath+"\\"+files[j], j+"");
				   Model model = getWorkflowSpecificationProvenance(workflow);
				   String specFileName = "";
				  
				  if(files[j].endsWith(".xml")) {
					  specFileName = files[j].replace(".xml", ".ttl");
				  } else if(files[j].endsWith(".scufl")){
					  specFileName = files[j].replace(".scufl", ".ttl");
				  }
				  
				  FileOutputStream fout=new FileOutputStream(spPath+"\\"+specFileName);
			      model.write(fout, "TTL");
				  
			  }
			}
		} catch(Exception e) {
			
			System.out.println("Following exception " + e);
		}
   }
	
	public static void getCreationProv() {
		
		TavernaWorkflowParser twp = new TavernaWorkflowParser();
		 
		try {
			String path = "C:\\Users\\but21c\\Dropbox\\CSIRO_Postdoc\\CSIRO_Papers\\EvolutionModelling\\dataset";
			String[] directories = getWorkflowDirectory(path);
		 
			for(int i=0; i <directories.length; i++) {
			   
			  String filePath = path +"\\"+ directories[i]+"\\workflow"; 			  
			  String spPath = path +"\\"+ directories[i] + "\\evo-prov";
			  File file = new File(spPath);
		      file.mkdir();

		      File versionfile = new File(filePath);
			  String[] files = versionfile.list();

			  System.out.println(filePath + "	" + files.length);
			  
			  for(int j=0; j <1; j++) {
				  System.out.println(files[j]);
				   Workflow workflow = TavernaWorkflowParser.getWorkflow(filePath+"\\"+files[j], j+"");
				   Model model = TavernaWorkflowParser.getWorkflowCreationProvenance(workflow);
				   String specFileName = "";
				  
				  if(files[j].endsWith(".xml")) {
					  specFileName = files[j].replace(".xml", ".ttl");
				  } else if(files[j].endsWith(".scufl")){
					  specFileName = files[j].replace(".scufl", ".ttl");
				  }
				  
				  FileOutputStream fout=new FileOutputStream(spPath+"\\"+specFileName);
			      model.write(fout, "TTL");
				  
			  }
			}
		} catch(Exception e) {
			
			System.out.println("Following exception " + e);
		}

   }
	
	
	public static void getEvolutionProv() {
		
		WorkflowComparison cm = new WorkflowComparison();	 
		 
		try {
			String path = "C:\\Users\\but21c\\Dropbox\\CSIRO_Postdoc\\CSIRO_Papers\\EvolutionModelling\\dataset";
			String[] directories = getWorkflowDirectory(path);
		 
			for(int i=0; i <directories.length; i++) {
			   
			  String filePath = path +"\\"+ directories[i]+"\\workflow"; 			  
			  String spPath = path +"\\"+ directories[i] + "\\evo-prov";
			  File file = new File(spPath);
		      file.mkdir();

		      File versionfile = new File(filePath);
			  String[] files = versionfile.list();

			  System.out.println(filePath + "	" + files.length);
			  
			  if(files.length<2) {
				  
			  } else {
				  for(int j=1; j <files.length; j++) {
					  System.out.println(files[j]);
					  Workflow workflow1 = TavernaWorkflowParser.getWorkflow(filePath+"\\"+files[j], j+"");
					  Workflow workflow2 = TavernaWorkflowParser.getWorkflow(filePath+"\\"+files[j-1], j-1+"");
					  Model model = cm.getRevisionProvenance(workflow1, workflow2);
					  String specFileName = "";
				  
					  if(files[j].endsWith(".xml")) {
						  specFileName = files[j].replace(".xml", ".ttl");
					  } else if(files[j].endsWith(".scufl")){
						  specFileName = files[j].replace(".scufl", ".ttl");
					  }
				  
					  FileOutputStream fout=new FileOutputStream(spPath+"\\"+specFileName);
					  model.write(fout, "TTL");
				  }
			  }
			}
		} catch(Exception e) {
			
			System.out.println("Following exception " + e);
		}

   }
	
	public static Model getWorkflowSpecificationProvenance (Workflow workflow) throws JSONException {
		
		TavernaWorkflowProvenance prov = new TavernaWorkflowProvenance();
		Model model = prov.generateSpecificationRDF(workflow, null);	
		
		return model;
	}

   public static String[] getWorkflowDirectory(String path) {
	   
	   File file = new File(path);
	   String[] directories = file.list(new FilenameFilter() {
	     @Override
	     public boolean accept(File current, String name) {
	       return new File(current, name).isDirectory();
	     }
	   });
	   return directories;
   }

   public static void moveFiles() {	
	   
	   String path = "C:\\Users\\but21c\\Dropbox\\CSIRO_Postdoc\\CSIRO_Papers\\EvolutionModelling\\dataset";
	   
	   String[] directories = getWorkflowDirectory(path);
	 
	   //System.out.println(directories.length);
	 
	   for(int i=0; i <directories.length; i++) {
		   
		  String filePath = path +"\\"+ directories[i];		 		  
		  
		//System.out.println(filePath);
		  //Create a new folder in this path
		  
		  String wfPath = filePath + "\\workflow";
		 // System.out.println(wfPath);
		  File file = new File(wfPath);
	      file.mkdir();
		  
		  // get all the files within this directory
	      
	      File versionfile = new File(path+"\\"+directories[i]);
		  String[] files = versionfile.list();

		  for(int j=0; j <files.length; j++) {
			   
			  File afile =new File(filePath+"\\"+files[j]);
			  
			  if(afile.renameTo(new File(filePath+"\\workflow\\" + afile.getName()))){
		       		System.out.println("File is moved successful!");
		        }else{
		       		System.out.println("File is failed to move!");
		        }
		  }    
	      
	   }

   }
}