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
import javax.swing.SwingUtilities;

/**
 * The ResearchKnowledgeManager is the main class for this system. This class
 * has the main() method. All other classes work explicitly in tandem with this
 * class.
 *
 * @author Samuel Benton
 */
public class ResearchKnowledgeManager
{

    /**
     * Used to represent various states the program can be in.
     * <br>
     * + Ready - A task is loaded into the system but has not started execution
     * <br>
     * + Active - A task is loaded into the system and is current executing
     * <br>
     * + Inactive - No task is loaded into the system
     * <br>
     * + Paused - A task is loaded into the system and has already started
     * executing but is currently paused
     * <br> <br>
     * <u>Use setState() to change the state of the system.</u>
     *
     * @see #setState(activeState state)
     */
    public enum activeState
    {

        INACTIVE, ACTIVE, PAUSED

    }

    final void sortTagClass(Vector<TagClass> data)
    {
        for (TagClass data1 : data)
        {
            data1.associatedFiles.sort(null);
            data1.keywords.sort(null);
        }

        data.sort(null);
        this.ui.updateTagTree();
    }

    final void sortFileClass(Vector<FileClass> data)
    {
        for (FileClass data1 : data)
        {
            data1.associatedTags.sort(null);
        }

        data.sort(null);
        this.ui.updateFileTree();
    }

    /**
     * Stores activeState values. The object used to represent various program
     * states
     *
     * @see activeState
     * @see #setState(activeState)
     */
    activeState actionStatus = activeState.INACTIVE;

    /**
     * Used to facilitate debugging within the system. When enabled, extra
     * messages are printed to the STDIN console. <br><br><i>Set this variable
     * running the system (during development only)</i>
     */
    boolean debug = false;

    /**
     * Used when creating ResearchKnowledgeManager objects. If false after the
     * constructor ends, something failed during the object's initialization
     * process.
     */
    boolean initializeStatus = false;

    /**
     * Represents the last time the project index the repository folder (in
     * milliseconds)
     */
    long lastOpened = 0;

    /**
     * Stores the file path of the system's "APP Data" folder
     */
    File dataDirectory;

    /**
     * Stores the file path of the system's repository folder.
     */
    File repositoryFolder;

    Vector<FileClass> missingFiles;

    // Various objects used throughout the program
    /**
     * Instance of the Indexer class for the system
     *
     * @see Indexer
     */
    Indexer fileIndexer;

    /**
     * Contains all the TagClass data in one object
     *
     * @see TagClass
     */
    Vector<TagClass> Tags = new Vector<>();

    /**
     * Contains all the FileClass data in one object see FileClass
     *
     * @see FileClass
     */
    Vector<FileClass> Files = new Vector<>();

    /**
     * Instance of an UserInterface for the system
     */
    UserInterface ui;

    /**
     * Simple string used to delineate/group related messages passed to
     * UserInterface.newMessage()
     *
     * @see UserInterface#newMessage(String)
     */
    String lineSeparator = "------------------------------";

    //String of filenames used throughout the program
    /**
     * Represents the folder's name within the APP DATA folder
     */
    String repoParentFolderName = "\\Research Knowledge Manager\\";

    /**
     * Represents the name of the file which stores the system's repository
     * folder file path
     */
    String repoFileName = "repo.txt";

    /**
     * Represents the name of the file which stores the system's lastOpened data
     *
     * @see #lastOpened
     */
    String lastModifiedFileName = "lastmod.txt";

    /**
     * Represents the name of the file which stores the system's tag data
     *
     * @see #Tags
     * @see #Files
     */
    String tagDataFileName = "system.data.txt";

    /**
     * Sets actionStatus to the input state and then invokes handleState()
     *
     * @param state the activeState to change actionStatus to
     * @see activeState
     * @see UserInterface#handleState()
     */
    public void setState(activeState state)
    {
        actionStatus = state;
        ui.handleState();
    }

    /**
     * Saves lastOpened to the system's stored files. The file name is
     * lastModifiedFileName
     *
     * @see #lastOpened
     * @see #lastModifiedFileName
     * @throws IOException thrown if there is an issue with writing the
     * necessary data
     */
    public void saveTime() throws IOException
    {
        try (FileWriter fileWrite = new FileWriter(dataDirectory.getAbsolutePath() + "\\" + this.lastModifiedFileName, false))
        {
            fileWrite.write(Long.toString(this.lastOpened));
        }

        if (this.debug)
        {
            System.out.println("Time successfully saved with saveTime()!");
        }
    }

    /**
     * Saves repositoryFolder to the system's stored files. The file name is
     * repoFileName
     *
     * @see #repoFileName
     * @see #repositoryFolder
     * @throws IOException thrown if there is an issue with writing the
     * necessary data
     */
    public void saveRepoDirectory() throws IOException
    {
        if (this.repositoryFolder.exists() && this.repositoryFolder.isDirectory())
        {

            try (FileWriter saveRepo = new FileWriter(this.dataDirectory.getAbsoluteFile() + "\\" + this.repoFileName))
            {
                saveRepo.write(this.repositoryFolder.getAbsolutePath());
            }

            if (this.debug)
            {
                System.out.println("Saving the repository folder filepath data was successful!");
            }

        }
        else
        {
            System.err.println("INVALID REPOSITORY FOLDER PATH!");

        }

    }

    /**
     * Saves the system's tag data. The file name is tagDataFileName
     *
     * @see #Files
     * @see #Tags
     * @see #tagDataFileName
     * @throws IOException thrown if there is an issue with wriging the
     * necessary save data
     */
    public void saveTags() throws IOException
    {
        String buffer = "";

        for (TagClass Tag : Tags)
        {
            buffer += Tag.toStringSpecial(this.fileIndexer.indexParseDelimiter);
        }

        try (FileWriter fileWrite = new FileWriter(dataDirectory.getAbsolutePath() + "\\" + this.tagDataFileName))
        {
            fileWrite.write(buffer);
        }
    }

    /**
     * Loads tag and file data from the system's stored file in tagDataFileName
     *
     * @return <b>true</b> if successful.<br> <b>false</b> otherwise
     */
    public boolean loadTags()
    {
        // Resets each vector
        Tags.clear();
        Files.clear();

        ui.newMessage("Searching for tag data. This may take a while...");

        File tagDataFile = new File(this.dataDirectory.getAbsolutePath() + "\\" + this.tagDataFileName);
        String fileData;
        // Executes when the relevant time file does not exist
        if (!tagDataFile.exists())
        {
            ui.newMessage("Tag data successfully found! Loading tag data now...");

            return true;
        }
        else
        {
            try
            {

                try (FileReader tagDataFileReader = new FileReader(tagDataFile))
                {
                    if (tagDataFile.length() > 0)
                    {
                        char[] tagDataArray = new char[(int) tagDataFile.length()];

                        tagDataFileReader.read(tagDataArray);
                        fileData = String.copyValueOf(tagDataArray);

                        // Data input parsing begins here
                        String[] candidates = fileData.split("Tag: ");

                        if (debug)
                        {
                            System.err.println("Possible saved tags: " + candidates.length);
                        }

                        // Contains all the possible tags in the system
                        for (int i = 0; i < candidates.length - 1; i++)
                        {

                            // Splits string into before/after keywords
                            String[] dataSplit = candidates[i + 1].split("\\" + this.fileIndexer.indexParseDelimiter + "keywords\\" + this.fileIndexer.indexParseDelimiter);

                            // Splits string into before keyword section into filenames
                            String[] confirmedFiles = dataSplit[0].split("\\" + this.fileIndexer.indexParseDelimiter);
                            this.addTagClass(new TagClass(confirmedFiles[0], this));

                            for (int k = 0; k < confirmedFiles.length - 1; k++)
                            {
                                this.Tags.get(this.Tags.size() - 1).addFiles(confirmedFiles[k + 1]);
                            }

                            if (dataSplit.length > 1)
                            {
                                confirmedFiles = dataSplit[1].split("\\" + this.fileIndexer.indexParseDelimiter);
                                this.Tags.get(this.Tags.size() - 1).addKeywords(confirmedFiles);
                            }

                        }
                    }
                    try
                    {
                        SwingUtilities.invokeAndWait(() ->
                        {
                            this.ui.refreshTagItemList();
                        });
                    }
                    catch (Exception ex)
                    {
                        System.err.println("Exception found!\n" + ex);
                    }
                }
            }
            catch (Exception ex)
            {
                System.err.println("Exception found!" + ex);
            }

        }

        ui.newMessage("Tag data successfully loaded!");
        return true;

    }

    /**
     * Saves all the needed system data. Called only when the system is about to
     * exit / quit.
     */
    public void exit()
    {
        try
        {
            this.saveTime();
            this.saveRepoDirectory();
            this.saveTags();

        }
        catch (IOException ex)
        {

            ui.newMessage("Something went wrong when this program was closing!");
            ui.newMessage("Closing the force closing the program will destroy any changes made since the program last opened");
            ui.newMessage("Try clicking the \"Safely Close Program\" menu option!");

            if (debug)
            {
                System.err.println("Something went wrong in the exiting saving process!");
                System.err.println(ex.getMessage());
            }
        }
    }

    /**
     * Prints a status message describing the debug state of the system
     */
    public final void debugMode()
    {
        if (this.debug)
        {
            ui.newMessage("System is executing within DEBUG MODE");

        }
        else
        {
            ui.newMessage("System is executing normally");
        }
        ui.newMessage(this.lineSeparator);
    }

    /**
     * Displays a dialog box asking the user for the system's repository folder.
     * Input validation and verification is guaranteed through this method.
     *
     * @return <b>true</b> if successful.<br><b>false</b> otherwise.
     * @throws IOException thrown if there is an issue with writing in this
     * function
     */
    public boolean askForRepository() throws IOException
    {
        String userInput;
        if (this.debug)
        {
            ui.newMessage("Prompting user for repository folder");
        }

        UserRepositoryPrompt buffer;
        File bufferFile;
        boolean invalidInput;

        do
        {
            if (this.repositoryFolder != null)
            {
                buffer = new UserRepositoryPrompt(this.ui, true, this.repositoryFolder.getAbsolutePath());
            }
            else
            {
                buffer = new UserRepositoryPrompt(this.ui, true, null);
            }
            userInput = buffer.userInput;

            if (debug)
            {
                System.err.println("User input is " + userInput);
            }

            bufferFile = new File(userInput);
            invalidInput = !(bufferFile.exists() && bufferFile.isDirectory());

            if (invalidInput && buffer.successfulExit)
            {
                ui.newMessage("Invalid user input! Input does not exist or is not a directory!");
                ui.newMessage(this.lineSeparator);
            }

        }
        while (buffer.successfulExit && invalidInput);

        if (buffer.successfulExit)
        {
            this.repositoryFolder = bufferFile;
            try (FileWriter saveRepo = new FileWriter(this.dataDirectory.getAbsoluteFile() + "\\" + this.repoFileName))
            {
                saveRepo.write(userInput);
            }

            ui.newMessage("User input received and validated for repository folder -> " + repositoryFolder.getAbsolutePath());
            ui.newMessage(this.lineSeparator);

            ui.FileExplorerTree.setModel(new FileTreeModelTree(repositoryFolder));
            return true;
        }
        else
        {
            ui.newMessage("User action cancelled...");
            ui.newMessage(this.lineSeparator);
        }

        return false;
    }

    /**
     * Loads the repository folder file path from the system's stored file in
     * repoFileName
     *
     * @see #repoFileName
     * @return <b>true</b> if successful.<br> <b>false</b> otherwise
     */
    public boolean loadRepoFolder()
    {
        try
        {
            File saveDirectoryFile = new File(this.dataDirectory.getAbsolutePath() + "\\" + this.repoFileName);

            if (!this.dataDirectory.exists())
            {
                this.dataDirectory.mkdir();

                ui.newMessage("No repository folder detected... Creating files for the system.");

            }
            else
            {
                ui.newMessage("Repository folder detected!");
            }

            if (!saveDirectoryFile.exists())
            {
                if (this.debug)
                {
                    System.err.println("Repository file does not exist! Creating file now...");
                }

                saveDirectoryFile.createNewFile();
                do
                {
                }
                while (!this.askForRepository());

                this.ui.autoIndexAll();

                return true;
            }
            else
            {

                ui.newMessage("Loading repository folder data... ");

                char[] repoFolder = new char[(int) saveDirectoryFile.length()];

                try (FileReader saveDirectoryRead = new FileReader(saveDirectoryFile))
                {
                    saveDirectoryRead.read(repoFolder);

                    String buffer = new String(repoFolder);
                    if (!buffer.isEmpty())
                    {
                        this.repositoryFolder = new File(buffer);

                        if (this.repositoryFolder.exists() && this.repositoryFolder.isDirectory())
                        {

                            ui.newMessage("Successfully loaded the repository folder");

                        }
                        else
                        {

                            ui.newMessage("An invalid repository folder path was loaded! Rejecting the path!");

                            repositoryFolder = null;
                            return false;
                        }
                    }
                    else
                    {
                        ui.newMessage("Empty data detected... Prompting the user for input");
                        do
                        {
                        }
                        while (!this.askForRepository());
                    }
                }
                return true;
            }
        }
        catch (IOException ex)
        {
            ui.newMessage("Error - An problem has occured while reading the saved repository folder");
            if (this.debug)
            {
                System.err.println("There is a problem with the default data for the loadRepoFolder() function!");
                System.err.println("AN ERROR HAS OCCURED!" + ex.getMessage());
            }

            return false;
        }
    }

    /**
     * Loads lastOpened from the system's stored file in lastModifiedFileName
     *
     * @see #lastModifiedFileName
     * @see #lastOpened
     * @return <b>true</b> if successful.<br> <b>false</b> otherwise
     */
    public boolean loadTime()
    {
        File timeDataFile = new File(this.dataDirectory.getAbsolutePath() + "\\" + this.lastModifiedFileName);

        // Executes when the relevant time file does not exist
        if (!timeDataFile.exists())
        {
            try
            {
                if (this.debug)
                {
                    ui.newMessage("No time file detected... Creating file for the system.");
                }

                timeDataFile.createNewFile();
                this.lastOpened = this.fileIndexer.lastOpened = 0;

                return true;
            }
            catch (IOException ex)
            {
                ui.newMessage("Error - An error has occured while creating the file time file!");
                if (this.debug)
                {
                    System.err.println(ex.getMessage());
                }

                this.lastOpened = this.fileIndexer.lastOpened = 0;
                return false;
            }
        } // Occurs when the relevant time file does exist
        else
        {
            FileReader timeDataRead;

            try
            {
                timeDataRead = new FileReader(timeDataFile);
                char[] longData = new char[(int) timeDataFile.length()];

                timeDataRead.read(longData);
                timeDataRead.close();

                ui.newMessage("Reading saved time data");
                this.lastOpened = this.fileIndexer.lastOpened = Long.parseLong(new String(longData));
                ui.newMessage("Time data successfully loaded!");

                return true;
            }
            catch (FileNotFoundException ex)
            {

                ui.newMessage("Error - A file was not found -> " + timeDataFile.getAbsolutePath());
                if (debug)
                {
                    System.err.println(ex.getMessage());
                }

                return false;
            }
            catch (IOException ex)
            {
                ui.newMessage("Error - An IOexception has occured -> " + timeDataFile.getAbsolutePath());

                if (debug)
                {
                    System.err.println(ex.getMessage());
                }

                return false;
            }
        }
    }

    /**
     * Adds a TagClass to the system if it does not already exist in the system.
     *
     * @param newTagClass The new TagClass to be added to the system
     * @see TagClass
     * @see Tags
     */
    void addTagClass(TagClass newTagClass)
    {
        boolean inStatus = false;

        for (TagClass Tag : Tags)
        {
            if (Tag.TagName.toLowerCase().trim().equals(newTagClass.TagName.toLowerCase().trim()))
            {
                inStatus = true;
                break;
            }
        }

        if (inStatus == false)
        {
            this.Tags.add(newTagClass);
        }
    }

    /**
     * Adds a FileClass to the system if it does not already exist in the
     * system.
     *
     * @param newFileClass The new FileClass to be added to the system
     * @see FileClass
     * @see Files
     */
    void addFileClass(FileClass newFileClass)
    {

        boolean inStatus = false;

        for (FileClass File : Files)
        {
            // Do not use .contains()
            // Comparisions are based solely on filename.
            if (File.FileName.toLowerCase().trim().equals(newFileClass.FileName.toLowerCase().trim()))
            {
                inStatus = true;
                break;
            }
        }

        if (inStatus == false)
        {
            this.Files.add(newFileClass);

        }
    }

    /**
     *
     * @param inputTags The list of tags (TagClasses) which must be associated
     * with a file to pass the search query. A FileClass is accepted only if all
     * tags from the input are associated with the FileClass
     *
     * @return Returns the vector FileClasses which contain all of the tags in
     * inputTags
     */
    public Vector<FileClass> findTagsComplete(List<TagClass> inputTags)
    {

        Vector<FileClass> searchResults = new Vector<>();

        for (FileClass File : Files)
        {
            boolean bufferResult = true;
            for (TagClass inputTag : inputTags)
            {
                // We can do partial tag completion if we replace &= with |=!
                bufferResult &= File.associatedTags.contains(inputTag.toString());
            }
            if (bufferResult)
            {
                searchResults.add(File);
            }
        }

        return searchResults;
    }

    /**
     * Updates lastOpend to the current time
     *
     * @see #lastOpened
     */
    public void updateLastModified()
    {
        this.lastOpened = System.currentTimeMillis();

        if (debug)
        {
            System.out.println("The last modified time has been updated! Current time is " + System.currentTimeMillis());
        }
    }

    /**
     * Initializes the system with all the data stored locally by the system
     *
     * @return <b>true</b> if initialization is succeeds<br><b>false</b> if
     * initialization fails
     */
    final boolean initializeData()
    {
        this.dataDirectory = new File(System.getenv("APPDATA") + this.repoParentFolderName);

        boolean buffer = this.loadRepoFolder() && this.loadTime() && this.loadTags();
        ui.newMessage(this.lineSeparator);
        ui.refreshTagItemList();
        this.ui.enableUI();

        return buffer;
    }

    /**
     * Constructor. Initializes everything that needs to be initialize for the
     * system
     */
    public ResearchKnowledgeManager()
    {
        this.ui = new UserInterface(this);
        this.ui.setVisible(true);

        this.fileIndexer = new Indexer(this.debug, this.lastOpened, this.ui);
        this.debugMode();

        this.initializeStatus = this.initializeData();
        this.ui.FileExplorerTree.setModel(new FileTreeModelTree(this.repositoryFolder));

        if (!initializeStatus)
        {
            ui.newMessage("Error - Something went wrong in the initialization process!");
            ui.newMessage("Please restart the system or choose the \"Clean Data Files\" menu option");
        }
        else
        {
            this.sortFileClass(Files);
            this.sortTagClass(Tags);
        }

        this.missingFiles = new Vector<>();

        for (FileClass f : this.Files)
        {
            File buffer = new File(f.toString());

            if (!buffer.exists())
            {
                this.missingFiles.add(f);
            }
        }

        if (!this.missingFiles.isEmpty())
        {
            this.ui.ErrorHandleFunction(missingFiles, UserInterface.ErrorType.MISSING);
        }

        this.missingFiles.clear();
    }

    // Not designed to recursively delete folders or items within folders
    /**
     * Removes all of the system's locally stored files from the device. Used to
     * let the user erase all data associated with the Research Knowledge
     * Manager. Can also be used in the event the user cannot start the program.
     */
    void clean()
    {
        boolean status = true;
        File[] buffer = this.dataDirectory.listFiles();

        for (File fileBuffer : buffer)
        {
            status &= fileBuffer.delete();
        }

        if (status)
        {
            ui.newMessage("Clean operation successful! Restart this program...");
        }

        else
        {
            ui.newMessage("Clean operational not fully completed. Some files could not be deleted.");
            ui.newMessage("Please try manually deleting the files after closing this program. File path  ->" + this.dataDirectory.getAbsolutePath());
        }

        ui.newMessage(this.lineSeparator);
    }

    public static void main(String[] args)
    {
        ResearchKnowledgeManager rm = new ResearchKnowledgeManager();

        return;
    }

}
