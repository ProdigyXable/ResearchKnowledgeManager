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
    String indexAllReturn = "";
    String indexNewReturn = "";
    FileWriter indexFileWrite;
    UserInterface ui;

    Indexer(boolean debugFlag, File file, long lastModified, UserInterface ui) {
        this.debug = debugFlag;
        this.lastOpened = lastModified;
        this.ui = ui;
    }

    int computeMaxFolderChild(File file) {

        File[] buffer = file.listFiles();
        int returnValue = 0;

        ui.progressBar.setString("Checking " + file);
        if (buffer == null) {
            if (debug) {
                System.err.println("File in question is " + file);
                System.err.println("File can Read = " + file.canRead());
                System.err.println("File can Execute = " + file.canExecute());
                System.err.println("File can Write = " + file.canWrite());
                System.err.println("File is hidden = " + file.isHidden());
                System.err.println("There was a problem with the folder specified! -> " + file.toString());
            }
            return 0;
        } else {
            for (File data : buffer) {
                synchronized (ui.executingThread) {
                    if (this.ui.rm.actionStatus == ResearchKnowledgeManager.activeState.PAUSED) {
                        try {
                            ui.executingThread.wait();
                        } catch (InterruptedException ex) {
                            System.err.println("Interrupt exception encountered!!!");
                        }
                    }
                }
                if (!data.isHidden()) {
                    if (data.isDirectory()) {
                        returnValue += computeMaxFolderChild(data);
                    } else {
                        returnValue++;

                    }
                }

            }
            return returnValue;
        }
    }

    String indexFilesAll(File path, boolean print, ResearchKnowledgeManager rm) {
        File[] result = path.listFiles();
        String validResults = "";

        if (result == null) {
            if (this.debug) {
                System.err.println("Input \"path\" parameter for indexFiles() is not a valid directory! --- " + path.toString());
            }

            rm.ui.newMessage(path.toString() + " is not a valid directory path!");
            rm.ui.newMessage("Exiting out of the current action...");
            rm.ui.newMessage(rm.lineSeparator);

        } else {
            // Iterates through the Files in the File[] result array
            for (int i = 0; i < result.length; i++) {

                // Used to allow the user to pause/resume mid function
                synchronized (ui.executingThread) {
                    if (rm.actionStatus == ResearchKnowledgeManager.activeState.PAUSED) {
                        try {
                            ui.executingThread.wait();
                        } catch (InterruptedException ex) {
                            System.err.println("Interrupt exception encountered!!!");
                        }
                    }
                }
                rm.ui.progressBar.setValue(rm.ui.progressBar.getValue() + 1);

                // Outputs an error if path is not a folder
                if (!result[i].isDirectory()) {
                    if (this.debug && print) {
                        System.out.println("File found -> " + result[i].toString());
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
        rm.updateLastModified();
        return validResults;
    }

    String indexFilesNew(File path, boolean print, ResearchKnowledgeManager rm) {
        String validResults = "";
        File[] potentialNew = path.listFiles();

        if (potentialNew == null) {
            if (this.debug) {
                System.err.println("Input \"path\" parameter for indexFiles() is not a valid directory! --- " + path.toString());
            }

            rm.ui.newMessage(path.toString() + " is not a valid directory path!");
            rm.ui.newMessage("Exiting out of the current action...");
            rm.ui.newMessage(rm.lineSeparator);

        } else {//Loop to detect newly modified files and add them into the file vector
            for (int i = 0; i < potentialNew.length; i++) {

                // Used to allow the user to pause/resume mid function
                synchronized (ui.executingThread) {
                    if (rm.actionStatus == ResearchKnowledgeManager.activeState.PAUSED) {
                        try {
                            ui.executingThread.wait();
                        } catch (InterruptedException ex) {
                            System.err.println("Interrupt exception encountered!!!");
                        }
                    }
                }

                rm.ui.progressBar.setValue(rm.ui.progressBar.getValue() + 1);

                if (!potentialNew[i].isDirectory() && potentialNew[i].lastModified() > this.lastOpened) {
                    validResults += potentialNew[i].toString() + this.indexParseDelimeter;

                    rm.ui.newMessage("New file detected! ->" + potentialNew[i].toString());

                    // Files can be preemptively put into tags based on their file names here
                    // We can also search the content of files here if needed
                } else if (potentialNew[i].isDirectory()) {
                    validResults += this.indexFilesNew(potentialNew[i], print, rm) + indexParseDelimeter;
                }
            }
        }
        rm.updateLastModified();
        return validResults;
    }

    boolean saveIndexAll(File saveDirectory) {

        return saveIndexAll(this.indexAllReturn, saveDirectory);
    }

    boolean saveIndexAll(String files, File saveDirectory) {
        return saveIndexAll(files.split(this.indexParseDelimeter), saveDirectory);
    }

    boolean saveIndexAll(String[] fileList, File saveDirectory) {
        try {
            String filename = saveDirectory.toString() + "\\" + indexFileName;
            indexFileWrite = new FileWriter(filename, false);

            for (int i = 0; i < fileList.length; i++) {
                this.indexFileWrite.write(fileList[i].toLowerCase() + this.indexParseDelimeter);
            }

            ui.newMessage("Successfully added files to the index!");
            ui.newMessage(ui.rm.lineSeparator);

            this.indexFileWrite.close();
            return true;

        } catch (IOException ex) {

            ui.newMessage("An error has occured when trying to add files to the system's index");
            if (debug) {
                System.err.println(ex.getMessage());
            }

            ui.newMessage(ui.rm.lineSeparator);

            this.indexFileWrite = null;
            return false;
        }

    }

    boolean saveIndexNew(File saveDirectory) {
        return saveIndexNew(this.indexNewReturn, saveDirectory);
    }

    boolean saveIndexNew(String files, File saveDirectory) {
        return saveIndexNew(files.split(this.indexParseDelimeter), saveDirectory);
    }

    boolean saveIndexNew(String[] fileList, File saveDirectory) {
        try {
            indexFileWrite = new FileWriter(saveDirectory.toString() + "\\" + indexFileName, true);

            for (int i = 0; i < fileList.length; i++) {
                this.indexFileWrite.write(fileList[i] + this.indexParseDelimeter);
            }

            ui.newMessage("Successfully added new files to the index!");
            ui.newMessage(ui.rm.lineSeparator);

            this.indexFileWrite.close();
            return true;
        } catch (IOException ex) {

            ui.newMessage("An error has occured when trying to add new files to the system's index");
            if (debug) {
                System.err.println(ex.getMessage());
            }
            ui.newMessage(ui.rm.lineSeparator);

            this.indexFileWrite = null;
            return false;
        }

    }
}
