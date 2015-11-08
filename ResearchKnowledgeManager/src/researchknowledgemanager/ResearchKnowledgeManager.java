/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;
import java.io.*;
import java.util.*;

public class ResearchKnowledgeManager
{
    boolean debug = false;
    File repositoryFolder = new File("C:\\Users\\Xable Enterprises\\Downloads\\TESTDOCS\\TESTDOCS\\");
    Indexer fileIndexer;
    
    public class Indexer
    {
        boolean debug;
        String indexParseDelimeter = "\t\r\n";
        String indexFileName = "RKM_Manager_Index.txt";
        FileWriter fileWrite;
        
        Indexer (boolean debugFlag, File file)
        {
            this.debug = debugFlag;
            
            try 
            {
                fileWrite = new FileWriter(file.getCanonicalPath()+"\\" + indexFileName , false);
            } 
            
            catch (IOException ex)
            {
                if(debug)
                {
                    System.err.println(ex.getMessage());
                }
            }
        }
   
        String indexFiles(File path)
        {
           File[] result = path.listFiles();
           String validResults = "";

           if(result == null)
           {
               if(this.debug)
               {
                    System.err.println("Input \"path\" parameter for indexFiles() is not a valid directory! --- " + path.toString());
               }
            }

           else
           {
               for(int i = 0; i < result.length; i++)
               {
                   // Outputs an error if path is not a folder
                   if(!result[i].isDirectory())
                   {
                       if(this.debug)
                       {
                           try 
                           {
                               System.out.println(result[i].getCanonicalPath());
                           } 
                           catch (IOException ex)
                           {
                               System.err.println(ex.getMessage());
                           }
                       }

                       validResults += result[i].toString() + this.indexParseDelimeter;
                   }
                   
                   else
                   {
                       // Recursively iterate all the files in a folder
                       validResults += this.indexFiles(result[i]);                 
                   }
               }
           }
           
           return validResults;
        }
        
        boolean saveIndex(String[] fileList)
        {
            try 
            {
                for(int i = 0; i < fileList.length; i++)
                {
                    this.fileWrite.write(fileList[i] + this.indexParseDelimeter);
                }        
                        
                if(this.debug)
                {
                    System.out.println("Save successful!");
                }
                
                this.fileWrite.flush();
                return true;
            } 
            
            catch (IOException ex) 
            {
                System.err.println(ex.getMessage());
                
                if(this.debug)
                {
                    System.out.println("Save unsuccessful!");
                }
            }
            
            return false;
        }
    }
    
    void debugMode()
    {
        if(this.debug)
        {
          System.err.println("System is executing INSIDE debug mode!\n");
        } 

        else
        {
          System.err.println("System is executing OUTSIDE of debug mode\n");
        }
    }
        
    ResearchKnowledgeManager()
    {
        this.debugMode();
        fileIndexer = new Indexer(this.debug, this.repositoryFolder);
    }
 
    public static void main(String[] args)
    {
        ResearchKnowledgeManager researchManager = new ResearchKnowledgeManager();
        
        // Testing the indexing capabilities. This should list all the files in the folder
        String[] indexArray = researchManager.fileIndexer.indexFiles(researchManager.repositoryFolder).split(researchManager.fileIndexer.indexParseDelimeter);    
        
        researchManager.fileIndexer.saveIndex(indexArray);
    }
}
