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
    String indexParseDelimeter = "|";
    String indexFileName = "RKM_Manager_Index.txt";
    String indexAllReturn = "";
    String indexNewReturn = "";
    FileWriter indexFileWrite;
    UserInterface ui;

    // String of known Tag mames
    String READABLE = "Content Readable";
    String NOREADABLE = "Content Unreadable";

    String NOMICROSOFT = "Non Microsoft File";
    String MICROSOFT = "Microsoft File";

    String WORD = "Word Document";
    String EXCEL = "Excel Document";
    String POWERPOINT = "PowerPoint Presentation";

    String HTML = "HTML Document";
    String TEXT = "Basic Text File";
    String XML = "XML File";
    String PDF = "Adobe PDF";
    String IMAGE = "Image File";

    void tagOnFileName(File file) {
        if (testReadableFileTypes(file)) {

        } else {
            if (!ui.rm.Files.contains(new FileClass(file.toString(), ui.rm))) {
                ui.rm.addFileClass(new FileClass(file.toString(), ui.rm));
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(NOREADABLE);

            } else {
                for (int i = 0; i < ui.rm.Files.size(); i++) {
                    if (ui.rm.Files.get(i).toString().equals(file.toString())) {
                        ui.rm.Files.get(i).addTag(NOREADABLE);

                    }
                }
            }

        }
        fileTestIMAGE(file);
    }

    boolean testReadableFileTypes(File file) {
        return fileTestPDF(file) | fileTestWORD(file) | fileTestEXCEL(file) | fileTestPPT(file) | fileTestTXT(file) | fileTestXML(file) | fileTestHTML(file);
    }

    void fileTestIMAGE(File file) {
        String fileExtension = file.getName().split("\\.")[file.getName().split("\\.").length - 1];
        if (fileExtension.matches("png|gif|tif|jp.*g")) {
            if (!ui.rm.Files.contains(new FileClass(file.toString(), ui.rm))) {
                ui.rm.addFileClass(new FileClass(file.toString(), ui.rm));
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(IMAGE);
                return;

            } else {
                for (int i = 0; i < ui.rm.Files.size(); i++) {
                    if (ui.rm.Files.get(i).toString().equals(file.toString())) {
                        ui.rm.Files.get(i).addTag(IMAGE);

                        return;
                    }
                }
            }
        }

        return;
    }

    boolean fileTestHTML(File file) {
        String fileExtension = file.getName().split("\\.")[file.getName().split("\\.").length - 1];
        if (fileExtension.matches("htm.*|mht.*?")) {
            if (!ui.rm.Files.contains(new FileClass(file.toString(), ui.rm))) {
                ui.rm.addFileClass(new FileClass(file.toString(), ui.rm));
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(READABLE);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(NOMICROSOFT);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(HTML);
                return true;

            } else {
                for (int i = 0; i < ui.rm.Files.size(); i++) {
                    if (ui.rm.Files.get(i).toString().equals(file.toString())) {
                        ui.rm.Files.get(i).addTag(READABLE);
                        ui.rm.Files.get(i).addTag(NOMICROSOFT);
                        ui.rm.Files.get(i).addTag(HTML);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    boolean fileTestXML(File file) {
        String fileExtension = file.getName().split("\\.")[file.getName().split("\\.").length - 1];
        if (fileExtension.matches("xml|xps")) {
            if (!ui.rm.Files.contains(new FileClass(file.toString(), ui.rm))) {
                ui.rm.addFileClass(new FileClass(file.toString(), ui.rm));
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(READABLE);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(NOMICROSOFT);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(XML);
                return true;

            } else {
                for (int i = 0; i < ui.rm.Files.size(); i++) {
                    if (ui.rm.Files.get(i).toString().equals(file.toString())) {
                        ui.rm.Files.get(i).addTag(READABLE);
                        ui.rm.Files.get(i).addTag(NOMICROSOFT);
                        ui.rm.Files.get(i).addTag(XML);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    boolean fileTestTXT(File file) {
        String fileExtension = file.getName().split("\\.")[file.getName().split("\\.").length - 1];
        if (fileExtension.matches("txt|rtf")) {
            if (!ui.rm.Files.contains(new FileClass(file.toString(), ui.rm))) {
                ui.rm.addFileClass(new FileClass(file.toString(), ui.rm));
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(READABLE);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(NOMICROSOFT);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(TEXT);
                return true;

            } else {
                for (int i = 0; i < ui.rm.Files.size(); i++) {
                    if (ui.rm.Files.get(i).toString().equals(file.toString())) {
                        ui.rm.Files.get(i).addTag(READABLE);
                        ui.rm.Files.get(i).addTag(NOMICROSOFT);
                        ui.rm.Files.get(i).addTag(TEXT);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    boolean fileTestPPT(File file) {
        String fileExtension = file.getName().split("\\.")[file.getName().split("\\.").length - 1];
        if (fileExtension.matches("ppt.*|pot.*")) {
            if (!ui.rm.Files.contains(new FileClass(file.toString(), ui.rm))) {
                ui.rm.addFileClass(new FileClass(file.toString(), ui.rm));
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(READABLE);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(MICROSOFT);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(POWERPOINT);
                return true;

            } else {
                for (int i = 0; i < ui.rm.Files.size(); i++) {
                    if (ui.rm.Files.get(i).toString().equals(file.toString())) {
                        ui.rm.Files.get(i).addTag(READABLE);
                        ui.rm.Files.get(i).addTag(MICROSOFT);
                        ui.rm.Files.get(i).addTag(POWERPOINT);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    boolean fileTestEXCEL(File file) {
        String fileExtension = file.getName().split("\\.")[file.getName().split("\\.").length - 1];
        if (fileExtension.matches("xls.?|xl.?")) {
            if (!ui.rm.Files.contains(new FileClass(file.toString(), ui.rm))) {
                ui.rm.addFileClass(new FileClass(file.toString(), ui.rm));
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(READABLE);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(MICROSOFT);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(EXCEL);
                return true;

            } else {
                for (int i = 0; i < ui.rm.Files.size(); i++) {
                    if (ui.rm.Files.get(i).toString().equals(file.toString())) {
                        ui.rm.Files.get(i).addTag(READABLE);
                        ui.rm.Files.get(i).addTag(MICROSOFT);
                        ui.rm.Files.get(i).addTag(EXCEL);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    boolean fileTestWORD(File file) {
        String fileExtension = file.getName().split("\\.")[file.getName().split("\\.").length - 1];
        if (fileExtension.matches("doc.*")) {
            if (!ui.rm.Files.contains(new FileClass(file.toString(), ui.rm))) {
                ui.rm.addFileClass(new FileClass(file.toString(), ui.rm));
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(READABLE);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(MICROSOFT);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(WORD);
                return true;

            } else {
                for (int i = 0; i < ui.rm.Files.size(); i++) {
                    if (ui.rm.Files.get(i).toString().equals(file.toString())) {
                        ui.rm.Files.get(i).addTag(READABLE);
                        ui.rm.Files.get(i).addTag(MICROSOFT);
                        ui.rm.Files.get(i).addTag(WORD);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    boolean fileTestPDF(File file) {
        String fileExtension = file.getName().split("\\.")[file.getName().split("\\.").length - 1];
        if (fileExtension.equals("pdf")) {
            if (!ui.rm.Files.contains(new FileClass(file.toString(), ui.rm))) {
                ui.rm.addFileClass(new FileClass(file.toString(), ui.rm));
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(READABLE);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(NOMICROSOFT);
                ui.rm.Files.get(ui.rm.Files.size() - 1).addTag(PDF);
                return true;

            } else {
                for (int i = 0; i < ui.rm.Files.size(); i++) {
                    if (ui.rm.Files.get(i).toString().equals(file.toString())) {
                        ui.rm.Files.get(i).addTag(PDF);
                        ui.rm.Files.get(i).addTag(READABLE);
                        return true;
                    }
                }
            }
        }

        return false;
    }

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
                    tagOnFileName(result[i]);
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

        if (potentialNew == null) {
            if (this.debug) {
                System.err.println("Input \"path\" parameter for indexFiles() is not a valid directory! --- " + path.toString());
            }

            rm.ui.newMessage(path.toString() + " is not a valid directory path!");
            rm.ui.newMessage("Exiting out of the current action...");
            rm.ui.newMessage(rm.lineSeparator);

        } else {

            //Loop to detect newly modified files and add them into the file vector
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
                if (!potentialNew[i].isDirectory()) {
                    if (potentialNew[i].lastModified() > this.lastOpened) {
                        validResults += potentialNew[i].toString() + this.indexParseDelimeter;

                        tagOnFileName(potentialNew[i]);
                        rm.ui.newMessage("New file detected! ->" + potentialNew[i].toString());

                        // Files can be preemptively put into tags based on their file names here
                        // We can also search the content of files here if needed
                    }
                } else if (potentialNew[i].isDirectory()) {
                    validResults += this.indexFilesNew(potentialNew[i], print, rm) + indexParseDelimeter;
                }
            }
        }

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
                this.indexFileWrite.write(fileList[i].toLowerCase().trim() + this.indexParseDelimeter);
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
