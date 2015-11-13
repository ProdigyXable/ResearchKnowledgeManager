/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Indexer {

    boolean debug;
    long lastOpened;
    String indexParseDelimeter = "\t";
    String indexFileName = "RKM_Manager_Index.txt";
    FileWriter fileWrite;

    Indexer(boolean debugFlag, File file, long lastModified) {
        this.debug = debugFlag;
        this.lastOpened = lastModified;
    }

    String indexFilesAll(File path, boolean print, ResearchKnowledgeManager rm) {
        File[] result = path.listFiles();
        String validResults = "";

        if (result == null) {
            if (this.debug) {
                System.err.println("Input \"path\" parameter for indexFiles() is not a valid directory! --- " + path.toString() + "\t indexFiles()");
            }
        } else {
            // Iterates through the Files in the File[] result array
            for (int i = 0; i < result.length; i++) {
                // Outputs an error if path is not a folder
                if (!result[i].isDirectory()) {
                    if (this.debug && print) {
                        System.out.println(result[i].toString());
                    }

                    // Files can be preemptively put into tags based on their file names here
                    // We can also search the content of files here if needed
                    validResults += result[i].toString() + this.indexParseDelimeter;
                } else {
                    // Recursively iterate all the files in a folder
                    validResults += this.indexFilesAll(result[i], print, rm);
                }
            }
        }

        return validResults;
    }

    String indexFilesNew(File path, boolean print, ResearchKnowledgeManager rm) {
        String validResults = "";
        File[] potentialNew = path.listFiles();

        //Loop to detect newly modified files and add them into the file vector
        for (int i = 0; i < potentialNew.length; i++) {
            if (!potentialNew[i].isDirectory() && potentialNew[i].lastModified() > this.lastOpened) {
                validResults += potentialNew[i].toString() + this.indexParseDelimeter;

                if (debug) {
                    System.err.println("\t" + potentialNew[i].toString() + " is detected as a new file!");
                }

                // Files can be preemptively put into tags based on their file names here
                // We can also search the content of files here if needed
            } else if (potentialNew[i].isDirectory()) {
                validResults += this.indexFilesNew(potentialNew[i], print, rm) + indexParseDelimeter;
            }
        }

        return validResults;
    }

    boolean saveIndexAll(String[] fileList, File saveDirectory) {
        try {
            String filename = saveDirectory.toString() + "\\" + indexFileName;
            fileWrite = new FileWriter(filename, false);

            for (int i = 0; i < fileList.length; i++) {
                this.fileWrite.write(fileList[i].toLowerCase() + this.indexParseDelimeter);
            }

            if (this.debug) {
                System.out.println("\nsaveIndexAll save function successful!");
                System.err.println("Saving data to \t" + filename);
            }

            this.fileWrite.flush();
            return true;
        } catch (IOException ex) {

            if (this.debug) {
                System.out.println("saveIndexAll save function unsuccessful!");
                System.err.println("AN ERROR HAS OCCURRED trying to prepare a file for writing!\n" + ex.getMessage() + "\t Indexer()");
            }
        }

        this.fileWrite = null;
        return false;
    }

    boolean saveIndexNew(String[] fileList, File saveDirectory) {
        try {
            fileWrite = new FileWriter(saveDirectory.toString() + "\\" + indexFileName, true);

            for (int i = 0; i < fileList.length; i++) {
                this.fileWrite.write(fileList[i] + this.indexParseDelimeter);
            }

            if (this.debug) {
                System.out.println("saveIndexNew save function successful!");
            }

            this.fileWrite.flush();
            return true;
        } catch (IOException ex) {

            if (this.debug) {
                System.out.println("saveindexNew save function unsuccessful!");
                System.err.println("AN ERROR HAS OCCURRED trying to prepare a file for writing!\n" + ex.getMessage() + "\t Indexer()");
            }
        }

        this.fileWrite = null;
        return false;
    }
}
