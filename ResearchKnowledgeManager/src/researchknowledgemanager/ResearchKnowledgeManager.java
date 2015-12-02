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
import java.util.List;
import java.util.Vector;

public class ResearchKnowledgeManager {

    enum activeState {

        INACTIVE, READY, ACTIVE, PAUSED
    }

    // Used for easy debugging within the system
    boolean debug = false;
    activeState actionStatus = activeState.INACTIVE;
    boolean initializeStatus = false;
    long lastOpened = 0;

    // Used to get the system's appdata
    File dataDirectory;
    File repositoryFolder;

    Indexer fileIndexer;
    Vector<TagClass> Tags = new Vector<>();
    Vector<FileClass> Files = new Vector<>();
    UserInterface ui;
    String lineSeparator = "------------------------------";

    //String of filenames
    String repoParentFolderName = "\\Research Knowledge Manager\\";
    String repoFileName = "repo.txt";
    String lastModifiedFileName = "lastmod.txt";
    String tagDataFileName = "system.data.txt";

    void setState(activeState state) {
        actionStatus = state;
        ui.handleState();
    }

    void saveTime() throws IOException {
        FileWriter fileWrite = new FileWriter(dataDirectory.getAbsolutePath() + "\\" + this.lastModifiedFileName, false);
        fileWrite.write(Long.toString(this.lastOpened));
        fileWrite.close();

        if (this.debug) {
            System.out.println("Time successfully saved with saveTime()!");
        }
    }

    void saveRepoDirectory() throws IOException {
        if (this.repositoryFolder.exists() && this.repositoryFolder.isDirectory()) {

            FileWriter saveRepo = new FileWriter(this.dataDirectory.getAbsoluteFile() + "\\" + this.repoFileName);

            saveRepo.write(this.repositoryFolder.getAbsolutePath());
            saveRepo.close();

            if (this.debug) {
                System.out.println("Saving the repository folder filepath data was successful!");
            }

        } else {
            System.err.println("INVALID REPOSITORY FOLDER PATH!");

        }

    }

    void saveTags() throws IOException {
        String buffer = "";

        for (int i = 0; i < Tags.size(); i++) {
            buffer += Tags.get(i).toStringSpecial(this.fileIndexer.indexParseDelimeter);
        }

        FileWriter fileWrite = new FileWriter(dataDirectory.getAbsolutePath() + "\\" + this.tagDataFileName);
        fileWrite.write(buffer);
        fileWrite.close();

    }

    boolean loadTags() {
        ui.newMessage("Loading tag data. This may take a while...");

        File tagDataFile = new File(this.dataDirectory.getAbsolutePath() + "\\" + this.tagDataFileName);
        String fileData = "";
        // Executes when the relevant time file does not exist
        if (!tagDataFile.exists()) {
            ui.newMessage("Tag data successfully loaded!");

            return true;
        } else {
            try {

                FileReader tagDataFileReader = new FileReader(tagDataFile);
                if (tagDataFile.length() > 0) {
                    char[] tagDataArray = new char[(int) tagDataFile.length()];

                    tagDataFileReader.read(tagDataArray);
                    fileData = String.copyValueOf(tagDataArray);

                    // Data input parsing begins here
                    String[] candidates = fileData.split("Tag: ");

                    if (debug) {
                        System.err.println("Possible saved tags: " + candidates.length);
                    }

                    // Contains all the possible tags in the system
                    for (int i = 0; i < candidates.length - 1; i++) {

                        // Splits string into before/after keywords
                        String[] dataSplit = candidates[i + 1].split("\\" + this.fileIndexer.indexParseDelimeter + "keywords\\" + this.fileIndexer.indexParseDelimeter);

                        // Splits string into before keyword section into filenames
                        String[] confirmedFiles = dataSplit[0].split("\\" + this.fileIndexer.indexParseDelimeter);
                        this.addTagClass(new TagClass(confirmedFiles[0], this));

                        for (int k = 0; k < confirmedFiles.length - 1; k++) {
                            this.Tags.get(this.Tags.size() - 1).addFiles(confirmedFiles[k + 1]);
                        }

                        if (dataSplit.length > 1) {
                            confirmedFiles = dataSplit[1].split("\\" + this.fileIndexer.indexParseDelimeter);
                            this.Tags.get(this.Tags.size() - 1).addKeywords(confirmedFiles);
                        }

                    }
                }
                this.ui.refreshTagItemList();
                tagDataFileReader.close();

            } catch (FileNotFoundException ex) {

            } catch (IOException ex) {

            }

        }
        ui.newMessage("Tag data successfully loaded!");
        return true;

    }

    void exit() {
        try {
            this.saveTime();
            this.saveRepoDirectory();
            this.saveTags();

        } catch (IOException ex) {

            ui.newMessage("Something went wrong when this program was closing!");
            ui.newMessage("Closing the force closing the program will destroy any changes made since the program last opened");
            ui.newMessage("Try clicking the \"Safely Close Program\" menu option!");

            if (debug) {
                System.err.println("Something went wrong in the exiting saving process!");
                System.err.println(ex.getMessage());
            }
        }
    }

    void debugMode() {
        if (this.debug) {
            ui.newMessage("System is executing within DEBUG MODE");

        } else {
            ui.newMessage("System is executing normally");
        }
        ui.newMessage(this.lineSeparator);
    }

    boolean askForRepository(File saveFileLocation) throws IOException {
        String userInput;
        if (this.debug) {
            ui.newMessage("Prompting user for repository folder");
        }

        UserRepositoryPrompt buffer;
        File bufferFile;
        boolean invalidInput;

        do {
            if (this.repositoryFolder != null) {
                buffer = new UserRepositoryPrompt(this.ui, true, this.repositoryFolder.getAbsolutePath());
            } else {
                buffer = new UserRepositoryPrompt(this.ui, true, null);
            }
            userInput = buffer.userInput;

            if (debug) {
                System.err.println("User input is " + userInput);
            }

            bufferFile = new File(userInput);
            invalidInput = !(bufferFile.exists() && bufferFile.isDirectory());

            if (invalidInput && buffer.successfulExit) {
                ui.newMessage("Invalid user input! Input does not exist or is not a directory!");
                ui.newMessage(this.lineSeparator);
            }

        } while (buffer.successfulExit && invalidInput);

        if (buffer.successfulExit) {
            this.repositoryFolder = bufferFile;
            FileWriter saveRepo = new FileWriter(this.dataDirectory.getAbsoluteFile() + "\\" + this.repoFileName);
            saveRepo.write(userInput);
            saveRepo.close();

            ui.newMessage("User input received and validated for repository folder -> " + repositoryFolder.getAbsolutePath());
            ui.newMessage(this.lineSeparator);

            ui.FileExplorerTree.setModel(new FileTreeModelTree(repositoryFolder));
            return true;
        } else {
            ui.newMessage("User action cancelled...");
            ui.newMessage(this.lineSeparator);
        }

        return false;
    }

    boolean loadRepoFolder() {
        try {
            File saveDirectoryFile = new File(this.dataDirectory.getAbsolutePath() + "\\" + this.repoFileName);

            if (!this.dataDirectory.exists()) {
                this.dataDirectory.mkdir();

                ui.newMessage("No repository folder detected... Creating files for the system.");

            } else {
                ui.newMessage("Repository folder detected!");
            }

            if (!saveDirectoryFile.exists()) {
                if (this.debug) {
                    System.err.println("Repository file does not exist! Creating file now...");
                }

                saveDirectoryFile.createNewFile();
                do {
                } while (!this.askForRepository(saveDirectoryFile));
                return true;
            } else {

                ui.newMessage("Loading repository folder data... ");

                char[] repoFolder = new char[(int) saveDirectoryFile.length()];

                FileReader saveDirectoryRead = new FileReader(saveDirectoryFile);
                saveDirectoryRead.read(repoFolder);

                String buffer = new String(repoFolder);
                if (!buffer.equals("")) {
                    this.repositoryFolder = new File(buffer);

                    if (this.repositoryFolder.exists() && this.repositoryFolder.isDirectory()) {

                        ui.newMessage("Successfully loaded the repository folder");

                    } else {

                        ui.newMessage("An invalid repository folder path was loaded! Rejecting the path!");

                        repositoryFolder = null;
                        return false;
                    }
                } else {
                    ui.newMessage("Empty data detected... Prompting the user for input");
                    do {
                    } while (!this.askForRepository(saveDirectoryFile));
                }

                saveDirectoryRead.close();
                return true;
            }
        } catch (IOException ex) {
            ui.newMessage("Error - An problem has occured while reading the saved repository folder");
            if (this.debug) {
                System.err.println("There is a problem with the default data for the loadRepoFolder() function!");
                System.err.println("AN ERROR HAS OCCURED!" + ex.getMessage());
            }

            return false;
        }
    }

    boolean loadTime() {
        File timeDataFile = new File(this.dataDirectory.getAbsolutePath() + "\\" + this.lastModifiedFileName);

        // Executes when the relevant time file does not exist
        if (!timeDataFile.exists()) {
            try {
                if (this.debug) {
                    ui.newMessage("No time file detected... Creating file for the system.");
                }

                timeDataFile.createNewFile();
                this.lastOpened = this.fileIndexer.lastOpened = 0;

                return true;
            } catch (IOException ex) {
                ui.newMessage("Error - An error has occured while creating the file time file!");
                if (this.debug) {
                    System.err.println(ex.getMessage());
                }

                this.lastOpened = this.fileIndexer.lastOpened = 0;
                return false;
            }
        } // Occurs when the relevant time file does exist
        else {
            FileReader timeDataRead;

            try {
                timeDataRead = new FileReader(timeDataFile);
                char[] longData = new char[(int) timeDataFile.length()];

                timeDataRead.read(longData);
                timeDataRead.close();

                ui.newMessage("Reading saved time data");
                this.lastOpened = this.fileIndexer.lastOpened = Long.parseLong(new String(longData));
                ui.newMessage("Time data successfully loaded!");

                return true;
            } catch (FileNotFoundException ex) {

                ui.newMessage("Error - A file was not found -> " + timeDataFile.getAbsolutePath());
                if (debug) {
                    System.err.println(ex.getMessage());
                }

                return false;
            } catch (IOException ex) {
                ui.newMessage("Error - An IOexception has occured -> " + timeDataFile.getAbsolutePath());

                if (debug) {
                    System.err.println(ex.getMessage());
                }

                return false;
            }
        }
    }

    void addTagClass(TagClass newTagClass) {
        boolean inStatus = false;

        for (int i = 0; i < Tags.size(); i++) {
            if (Tags.get(i).TagName.equals(newTagClass.TagName)) {
                inStatus = true;
                break;
            }
        }

        if (inStatus == false) {
            this.Tags.add(newTagClass);
        }
    }

    void addFileClass(FileClass newFileClass) {
        boolean inStatus = false;

        for (int i = 0; i < Files.size(); i++) {
            if (Files.get(i).FileName.equals(newFileClass.FileName)) {
                inStatus = true;
                break;
            }
        }

        if (inStatus == false) {
            this.Files.add(newFileClass);

        }
    }

    Vector<FileClass> findTagsComplete(List<TagClass> inputTags) {

        Vector<FileClass> searchResults = new Vector<>();

        for (int i = 0; i < Files.size(); i++) {
            boolean bufferResult = true;
            for (int j = 0; j < inputTags.size(); j++) {

                // We can do partial tag completion if we replace &= with |=!
                bufferResult &= Files.get(i).associatedTags.contains(inputTags.get(j).toString());
            }

            if (bufferResult) {
                searchResults.add(Files.get(i));
            }
        }

        return searchResults;
    }

    void updateLastModified() {
        this.lastOpened = System.currentTimeMillis();

        if (debug) {
            System.out.println("The last modified time has been updated! Current time is " + System.currentTimeMillis());
        }
    }

    final boolean initializeData() {
        this.dataDirectory = new File(System.getenv("APPDATA") + this.repoParentFolderName);

        boolean buffer = this.loadRepoFolder() && this.loadTime() && this.loadTags();
        ui.newMessage(this.lineSeparator);
        ui.refreshTagItemList();

        return buffer;
    }

    ResearchKnowledgeManager() {

        // Only Temporary while testing the TagTreeModel
        ui = new UserInterface(this);
        ui.setVisible(true);

        this.fileIndexer = new Indexer(this.debug, this.dataDirectory, this.lastOpened, ui);
        this.debugMode();

        this.initializeStatus = this.initializeData();
        ui.FileExplorerTree.setModel(new FileTreeModelTree(this.repositoryFolder));

        if (!initializeStatus) {
            ui.newMessage("Error - Something went wrong in the initialization process!");
            ui.newMessage("Please restart the system or choose the \"Clean Data Files\" menu option");
        }
    }

    // Not designed to recursively delete folders or items within folders
    void clean() {
        boolean status = true;
        File[] buffer = this.dataDirectory.listFiles();

        for (int i = 0; i < buffer.length; i++) {
            status &= buffer[i].delete();
        }

        if (status) {
            ui.newMessage("Clean operation successful! Restart this program...");
        } else {
            ui.newMessage("Clean operational not fully completed. Some files could not be deleted.");
            ui.newMessage("Please try manually deleting the files after closing this program ->" + this.dataDirectory.getAbsolutePath());
        }

        ui.newMessage(this.lineSeparator);
    }

    public static void main(String[] args) {
        ResearchKnowledgeManager rm = new ResearchKnowledgeManager();
        return;
    }

}
