/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class ResearchKnowledgeManager {

    // Used for easy debugging within the system
    boolean debug = true;
    boolean initializeStatus = false;
    long lastOpened = 0;

    // Used to get the system's appdata
    File saveDirectory = new File(System.getenv("APPDATA") + "\\Research Knowledge Manager\\");
    File repositoryFolder;

    Indexer fileIndexer;
    Vector<TagClass> Tags;
    Vector<FileClass> Files;

    //String of filenames
    String repoFileName = "repo.txt";
    String lastModifiedFileName = "lastmod.txt";

    void exit() {
        try {
            FileWriter fileWrite = new FileWriter(saveDirectory.toString() + "\\" + this.lastModifiedFileName, false);
            fileWrite.write(((Long) System.currentTimeMillis()).toString());
            fileWrite.close();
        } catch (IOException ex) {
            if (debug) {
                System.err.println(ex.getMessage());
            }
        }
    }

    void debugMode() {
        if (this.debug) {
            System.err.println("System is executing INSIDE debug mode!\n");
        } else {
            System.err.println("System is executing OUTSIDE of debug mode\n");
        }
    }

    boolean askForRepository(File file) throws IOException {
        String userInput;
        if (this.debug) {
            System.out.println("Asking for the repository file now!");
        }

        // Application logic for user input here...
        // .
        // .
        // .
        userInput = "C:\\Users\\Xable Enterprises\\Downloads\\TESTDOCS\\TESTDOCS\\";
        File bufferFile = new File(userInput);

        if (bufferFile.exists() && bufferFile.isDirectory()) {
            this.repositoryFolder = bufferFile;
            FileWriter saveRepo = new FileWriter(file);
            saveRepo.write(userInput);
            saveRepo.close();
            return true;
        } else {
            if (this.debug) {
                System.out.println("userInput either does not exist or is not a directory! askForRepository(File file)");
            }

            return false;
        }
    }

    boolean loadRepoFolder() {
        try {
            File saveDirectoryFile = new File(this.saveDirectory.toString() + "\\" + this.repoFileName);
            if (!this.saveDirectory.exists()) {
                this.saveDirectory.mkdir();
                if (this.debug) {
                    System.out.println("Creating the appropriate directory for the system's files");
                }
            } else {
                if (this.debug) {
                    System.out.println("The AppData Folder \"" + this.saveDirectory.toString() + "\" exists!");
                }
            }

            if (!saveDirectoryFile.exists()) {
                if (this.debug) {
                    System.out.println("Repository file does not exist! Creating file now...");
                }

                saveDirectoryFile.createNewFile();
                return askForRepository(saveDirectoryFile);
            } else {
                if (this.debug) {
                    System.out.println("Loading data from " + saveDirectoryFile.toString());
                }

                char[] repoFolder = new char[(int) saveDirectoryFile.length()];

                FileReader saveDirectoryRead = new FileReader(saveDirectoryFile);
                saveDirectoryRead.read(repoFolder);

                this.repositoryFolder = new File(new String(repoFolder));

                if (this.debug) {
                    System.out.println("Data has been found! Attempting to load \"" + new String(repoFolder) + "\" as the default repository folder!");

                    if (this.repositoryFolder.exists() && this.repositoryFolder.isDirectory()) {
                        System.out.println("Successfully loaded a valid repository folder path!");
                    } else {
                        System.err.println("An invalid repository folder path was loaded! Rejecting the path!");
                        repositoryFolder = null;
                        return false;
                    }
                }

                return true;
            }
        } catch (IOException ex) {

            if (this.debug) {
                System.err.println("There is a problem default data for the loadRepoFolder() function!");
                System.err.println("AN ERROR HAS OCCURED!" + ex.getMessage());
            }

            return false;
        }
    }

    boolean loadTime() {
        File timeDataFile = new File(this.saveDirectory.toString() + "\\" + this.lastModifiedFileName);

        // Executes when the relevant time file does not exist
        if (!timeDataFile.exists()) {
            try {
                if (this.debug) {
                    System.out.println("LastModified file does not exist! Creating file now...");
                }

                timeDataFile.createNewFile();
                this.lastOpened = this.fileIndexer.lastOpened = 0;

                return true;
            } catch (IOException ex) {
                if (debug) {
                    System.err.println(ex.getMessage());
                }

                return false;
            }
        } // Occurs when the relevant time file does exist
        else {
            FileReader timeDataRead;

            try {
                timeDataRead = new FileReader(timeDataFile);
                char[] longData = new char[(int) timeDataFile.length()];

                timeDataRead.read(longData);

                if (debug) {
                    System.out.println("Input from \"lastmod.txt\" is " + Long.parseLong(new String(longData)));
                }

                return true;
            } catch (FileNotFoundException ex) {
                if (debug) {
                    System.err.println(ex.getMessage());
                }

                return false;
            } catch (IOException ex) {
                if (debug) {
                    System.err.println(ex.getMessage());
                }

                return false;
            }
        }
    }

    void updateLastModified() {
        this.lastOpened = this.fileIndexer.lastOpened = System.currentTimeMillis();
        if (debug) {
            System.out.println(System.currentTimeMillis());
        }
    }

    final boolean initializeData() {
        return this.loadRepoFolder() && this.loadTime();
    }

    ResearchKnowledgeManager() {
        this.debugMode();
        this.fileIndexer = new Indexer(this.debug, this.saveDirectory, this.lastOpened);
        this.Tags = new Vector<>();
        this.Files = new Vector<>();
        this.initializeStatus = this.initializeData();

        if (!initializeStatus && debug) {
            System.err.println("Something went wrong in the initialization process! ResearchKnowledgeManager()");
        }
    }

    public static void main(String[] args) {
        ResearchKnowledgeManager rm = new ResearchKnowledgeManager();

        String[] buffer = rm.fileIndexer.indexFilesAll(rm.repositoryFolder, false, rm).split(rm.fileIndexer.indexParseDelimeter);
        rm.fileIndexer.saveIndexAll(buffer, rm.saveDirectory);
        return;
    }
}
