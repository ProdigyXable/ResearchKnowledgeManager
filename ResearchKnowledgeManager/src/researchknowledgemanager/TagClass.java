/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.File;
import java.util.Vector;

/**
 * The TagClass is used to store data of a given tag. All tags have a name and a
 * set of files (FileClass) associated with the TagClass. All string data for
 * the TagClass is lowercased for simplicity
 *
 * @author Samuel Benton
 * @see FileClass
 */
public class TagClass implements Comparable
{

    /**
     * Represents the name of the Tag. Used extensively throughout the system.
     */
    String TagName;

    int incrementCount = 20;

    /**
     * An instance of the ResearchKnowledgeManager
     *
     * @see ResearchKnowledgeManager
     */
    ResearchKnowledgeManager rm;

    /**
     * Vector of files names associated with a Tag
     *
     */
    Vector<String> associatedFiles;

    /**
     * Vector of keywords associated with a Tag.
     */
    Vector<String> keywords;

    TagClass(String Name, ResearchKnowledgeManager rkm)
    {
        this.TagName = Name.toLowerCase().trim();
        this.associatedFiles = new Vector<>(incrementCount);
        this.keywords = new Vector<>(incrementCount);
        rm = rkm;

    }

    /**
     * Simply returns the TagClass' TagName
     *
     * @return this TagClass' "TagName"
     * @see #TagName
     */
    public String toString()
    {
        return (this.TagName);
    }

    public int compareTo(Object o)
    {
        return this.TagName.compareTo(((TagClass) o).toString());
    }

    /**
     * Special function used to get all the content/data from a TagClass. Each
     * set of data is delimited by "delimiter"
     *
     * @param delimiter Delimiter used to separate the various content of the
     * TagClass
     * @return the content of the TagClass as a string
     */
    String toStringSpecial(String delimiter)
    {
        String buffer = "Tag: " + this.TagName;
        for (String associatedFile : associatedFiles)
        {
            if ((new File(associatedFile)).exists())
            {
                buffer += delimiter + associatedFile.toLowerCase().trim();
            }
        }

        if (this.keywords.size() > 0)
        {
            buffer += delimiter + "keywords";

            for (String keyword : keywords)
            {
                buffer += delimiter + keyword.toLowerCase().trim();
            }
            buffer += System.lineSeparator();
        }
        else
        {
            if (rm.debug)
            {
                System.err.println();
                System.err.println("This tags does not contain any keywords!");
                System.err.println();
            }
        }

        if (rm.debug)
        {
            System.out.println("The data for this tag is as follows:");
            System.out.println("----------");
            System.out.println(buffer);
        }

        return buffer;
    }

    /**
     * Adds a keyword to the TagClass. A keyword will not be added if it already
     * exists in the TagClass
     *
     * @param newKeyword The keyword to add to the TagClass (the keyword will be
     * lowercased and trimmed)
     */
    public void addKeywords(String newKeyword)
    {
        if (!this.keywords.contains(newKeyword.toLowerCase().trim()))
        {
            this.keywords.add(newKeyword.toLowerCase().trim());
        }
    }

    /**
     * Adds a set of keywords to the TagClass. A keyword will not be added if it
     * already exists in the TagClass
     *
     * @param newKeywords The set of keywords to add to the TagClass (each
     * keyword will be lowercased and trimmed)
     */
    public void addKeywords(String[] newKeywords)
    {
        for (String newKeyword : newKeywords)
        {
            addKeywords(newKeyword);
        }
    }

    /**
     * Removes all associated files with the tag
     */
    public void cleanFiles()
    {
        this.associatedFiles.clear();
    }

    /**
     * Removes all associated keywords with the tag
     */
    public void cleanKeywords()
    {
        this.keywords.clear();
    }

    /**
     * Adds a set of files to the current TagClass. A file will not be added if
     * it is already associated with the Tag
     *
     * @param newFiles The set of files to be associated with the Tag
     */
    void addFiles(String[] newFiles)
    {
        this.associatedFiles.trimToSize();

        for (String newFile : newFiles)
        {
            addFiles(newFile);
        }
    }

    /**
     * Adds a file to the current TagClass. A file will not be added if it is
     * already associated with the Tag
     *
     * @param newFile The file to be associated with the given TagClass instance
     */
    void addFiles(String newFile)
    {

        if (!this.associatedFiles.contains(newFile.toLowerCase().trim()))
        {
            this.associatedFiles.add(newFile.toLowerCase().trim());

            //Search for the specified file and append a tag to it in the file class
            for (FileClass File : rm.Files)
            {
                if (File.FileName.equals(newFile.toLowerCase().trim()))
                {
                    if (rm.debug)
                    {
                        System.err.println("Exising file found within the tag structure. Tag added to this file");
                    }
                    File.addTag(this.TagName);
                    File.associatedTags.sort(null);
                    return;
                }
            }
            rm.addFileClass(new FileClass(newFile, rm));

            rm.Files.get(rm.Files.size() - 1).addTag(this.TagName);
            rm.ui.refreshTagItemList();
            rm.ui.updateFileTree();
            rm.ui.updateTagTree();
        }
        else
        {
            if (rm.debug)
            {
                System.err.println("FILE NOT FOUND. Adding file now\t" + newFile);
            }

        }

    }

    /**
     * Disassociates a keyword and a tag
     *
     * @param deletedKeyword The keyword to disassociate from a tag
     * @return <b>true</b> if the keyword was removed<br><b>false</b> if the
     * element keyword did not exist in the Tag
     */
    boolean removeKeyword(String deletedKeyword)
    {
        return this.keywords.remove(deletedKeyword.toLowerCase().trim());
    }

    /**
     * Disassociates a file from this tag
     *
     * @param deletedFile The file (not folder, but an actual file) to
     * disassociate the tag from
     * @param doOther if true, disassociates this tag from the input file
     * @return <b>true</b> if the file was removed<br><b>false</b> if the
     * element file was not associated with the tag
     */
    boolean removeFile(String deletedFile, boolean doOther)
    {
        boolean status = this.associatedFiles.remove(deletedFile.toLowerCase().trim());

        if (doOther)
        {
            //Search for the specified file and remove the file from the tag
            for (FileClass File : rm.Files)
            {
                if (File.FileName.equals(deletedFile.toLowerCase().trim()))
                {
                    if (rm.debug)
                    {
                        System.err.println("Existing file found within the tag structure. Preparing to remove file...");
                    }
                    File.removeTag(this.TagName, false);
                    return status;
                }
                else
                {
                    // Tag.get(j) does not match the input tagItem
                }
            }
        }

        return status;
    }

    /**
     *
     * @return The number of keywords associated with the Tag
     */
    public int keywordSize()
    {
        return this.keywords.size();
    }

    /**
     *
     * @return The number of files associated with the tag
     */
    public int associatedFilesSize()
    {
        return this.associatedFiles.size();
    }

}
