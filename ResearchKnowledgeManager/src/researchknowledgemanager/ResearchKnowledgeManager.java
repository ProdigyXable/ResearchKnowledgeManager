/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;
import java.io.*;

public class ResearchKnowledgeManager
{
    // Used for easy debuggins within the system
    boolean debug = true;
    boolean initializeStatus = false;
    
    // Used to get the system's appdata
    File saveDirectory = new File(System.getenv("APPDATA")+"\\Research Knowledge Manager\\");
    File repositoryFolder;
    Indexer fileIndexer;
    
    //String of filenames
    String repoFileName = "repo.txt";
    
    public class Indexer
    {
        boolean debug;
        String indexParseDelimeter = "\t\r\n";
        String indexFileName = "RKM_Manager_Index.txt";
        
        FileWriter fileWrite;
        
        Indexer(boolean debugFlag, File file)
        {
            this.debug = debugFlag;
            
            try 
            {
                fileWrite = new FileWriter(file.getCanonicalPath()+"\\" + indexFileName , false);
            } 
            
            catch (IOException ex)
            {
                if(this.debug)
                {                    
                    System.err.println("AN ERROR HAS OCCURED!\n" + ex.getMessage() + "\t Indexer()");
                }
            }
        }
   
        String indexFiles(File path, boolean print)
        {
           File[] result = path.listFiles();
           String validResults = "";

           if(result == null)
           {
               if(this.debug)
               {
                    System.err.println("Input \"path\" parameter for indexFiles() is not a valid directory! --- " + path.toString() + "\t indexFiles()");
               }
            }

           else
           {
               for(int i = 0; i < result.length; i++)
               {
                   // Outputs an error if path is not a folder
                   if(!result[i].isDirectory())
                   {
                       if(this.debug && print)
                       {
                           try 
                           {
                               System.out.println(result[i].getCanonicalPath());
                           } 
                           
                           catch (IOException ex)
                           {
                               if(this.debug)
                               {    
                                    System.err.println(ex.getMessage() + "\t indexFiles()");
                               }
                           }
                       }

                       validResults += result[i].toString() + this.indexParseDelimeter;
                   }
                   
                   else
                   {
                       // Recursively iterate all the files in a folder
                       validResults += this.indexFiles(result[i], print);                 
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
                
                if(this.debug)
                {
                    System.out.println("Save unsuccessful!");
                    System.err.println("AN ERROR HAS OCCURED!" + ex.getMessage() + "\t saveIndex()");
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
    
    
    boolean askForRepository(File file) throws IOException
    {
        String userInput;
        if(this.debug)
        {
            System.out.println("Asking for the repository file now!");
        }
        
        // Application logic for user input here...
        // .
        // .
        // .
        
        userInput = "C:\\Users\\Xable Enterprises\\Downloads\\TESTDOCS\\TESTDOCS\\";
        File bufferFile = new File(userInput);
        
        if(bufferFile.exists() && bufferFile.isDirectory())
        {
            this.repositoryFolder = bufferFile;
            FileWriter saveRepo = new FileWriter(file);
            saveRepo.write(userInput);
            saveRepo.close();
            return true;
        }
        
        else
        {
            if(this.debug)
            {
                System.out.println("userInput either does not exist or is not a directory! askForRepository(File file)");
            }
            
            return false;
        }
    }
        
    boolean loadrepoFolder()
    {
        try
        {
            File saveDirectoryFile = new File(this.saveDirectory.getCanonicalFile()+ "\\" + this.repoFileName);
            if(!this.saveDirectory.exists())
            {
                this.saveDirectory.mkdir();
                if(this.debug)
                {
                    System.out.println("Creating the appropriate directory for the system's files");
                }
            }
            else
            {
                if(this.debug)
                {
                    System.out.println("The AppData Folder \"" + this.saveDirectory.getCanonicalPath() + "\" exists!");
                }
            }
            
            if(!saveDirectoryFile.exists())
            {
                if(this.debug)
                {
                    System.out.println("Repository file does not exist! Creating file now...");
                }
                
                saveDirectoryFile.createNewFile();
                return askForRepository(saveDirectoryFile);
            }
            
            else
            {
                if(this.debug)
                {
                    System.out.println("Loading data from " + saveDirectoryFile.getCanonicalPath());
                }
                
                char[] repoFolder = new char[(int)saveDirectoryFile.length()];
                FileReader saveDirectoryRead = new FileReader(saveDirectoryFile);
                saveDirectoryRead.read(repoFolder);
                this.repositoryFolder = new File(new String(repoFolder));
                
                if(this.debug)
                {
                    System.out.println("Data has been found! Attempting to load \"" + new String(repoFolder) + "\" as the default repository folder!");
                    
                    if(this.repositoryFolder.exists() && this.repositoryFolder.isDirectory())
                    {
                        System.out.println("Successfully loaded a valid repository folder path!");
                    }
                    
                    else
                    {
                        System.err.println("An invalid repository folder path was loaded! Rejecting the path!");
                        repositoryFolder = null;
                        return false;
                    }
                }               
                
                return true;
            }
        }
        
        catch (IOException ex)
        {
                
                if(this.debug)
                {
                    System.err.println("There is a problem default data for the loadRepoFolder() function!");
                    System.err.println("AN ERROR HAS OCCURED!" + ex.getMessage());
                }
                
                return false;
        }
    }
    
    final boolean initializeData()
    {
        return this.loadrepoFolder();
    }
    
    ResearchKnowledgeManager()
    {
        this.debugMode();
        this.fileIndexer = new Indexer(this.debug, this.saveDirectory);
        this.initializeStatus = this.initializeData();
        
        if(!initializeStatus && debug)
        {
            System.err.println("Something went wrong in the initialization process! ResearchKnowledgeManager()");
        }
    }
 
    public static void main(String[] args)
    {
        ResearchKnowledgeManager researchManager = new ResearchKnowledgeManager();
        
        if(researchManager.initializeStatus)
        {
            // Testing the indexing capabilities. This should list all the files in the folder
            String[] indexArray = researchManager.fileIndexer.indexFiles(researchManager.repositoryFolder, false).split(researchManager.fileIndexer.indexParseDelimeter);
            researchManager.fileIndexer.saveIndex(indexArray);
        }
        
        else
        {
            if(researchManager.debug)
            {
                System.err.println("Invalid initialization process detected!!! Terminating this instance of ResearchKnowledgeManager");
            }
            
            researchManager = null;
        }
    }
}
