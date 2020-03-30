package com.csiro.dataset;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class CreateDirectory {
   public static void main(String args[]) {
	   
	  moveFiles();
	   /*String path = "C:\\Users\\but21c\\Dropbox\\CSIRO_Postdoc\\CSIRO_Papers\\EvolutionModelling\\dataset";
	   
	   String[] directories = getWorkflowDirectory(path);
	 
	   System.out.println(directories.length);
	 
	   for(int i=0; i <directories.length; i++) {
		   
		  System.out.print(directories[i]);		 		  
		  
		  File file = new File(path+"\\"+directories[i]);
		  String[] files = file.list();
		  
		  System.out.println("     " + files.length);

		  for(int j=0; j <files.length; j++) {
			  System.out.println(files[j]);		 
		  }
		  
		   
	   }*/

   }
   
   public static void createDirectories() {
	   String path = "C:\\Users\\but21c\\Downloads\\";
	      
	   for(int i=1; i<501;i++) {
		   //Creating a File object
		   File file = new File(path+ "workflow" +i);
		   //Creating the directory
		   boolean bool = file.mkdir();
	   }
   }
   
   
   public static String[] getWorkflowDirectory(String path) {
	   
	   File file = new File(path);
	   String[] directories = file.list(new FilenameFilter() {
	     @Override
	     public boolean accept(File current, String name) {
	       return new File(current, name).isDirectory();
	     }
	   });
	   System.out.println(Arrays.toString(directories));
	   return directories;
   }

   public static void moveFiles() {	
     try{
    	 
    	String path = "C:\\Users\\but21c\\Dropbox\\CSIRO_Postdoc\\CSIRO_Papers\\EvolutionModelling\\dataset\\workflow1\\";
    	 
        File afile =new File(path+"_deprecated__Probabilistic_Model_Checking__PMC___compute_results-v1.xml");
     		
        File file = new File(path+ "workflow");
        file.mkdir();
        if(afile.renameTo(new File(path+"workflow\\" + afile.getName()))){
       		System.out.println("File is moved successful!");
        }else{
       		System.out.println("File is failed to move!");
        }
       	    
     }catch(Exception e){
     	e.printStackTrace();
     }
   }
}