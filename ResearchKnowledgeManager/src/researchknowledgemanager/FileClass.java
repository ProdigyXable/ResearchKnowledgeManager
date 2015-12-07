/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.File;
import java.util.Vector;

/**
 * The FileClass is used to store data of a given file. All files have a name
 * and a set of files (TagClass) associated with the FileClass. All string data
 * for the FileClass is lowercased for simplicity
 *
 * @author Samuel Benton
 * @see TagClass
 */
public class FileClass implements Comparable
{

    /**
     * Represents the name of the File. Used extensively throughout the system.
     */
    String FileName;

    int incrementCount = 20;

    ResearchKnowledgeManager rm;

    /**
     * Vector of tags associated with a file
     */
    Vector<String> associatedTags;

    FileClass(String name, ResearchKnowledgeManager rkm)
    {
        FileName = name.toLowerCase().trim();
        associatedTags = new Vector<>(incrementCount);
        rm = rkm;

    }

    FileClass(File file, ResearchKnowledgeManager rkm)
    {
        FileName = file.toString().toLowerCase().trim();
        rm = rkm;
    }

    /**
     * Simply returns the FileClass' FileName
     *
     * @return this FileClass' "FileName"
     * @see #FileName
     */
    public String toString()
    {
        return (this.FileName);
    }

    public int compareTo(Object o)
    {
        return this.FileName.compareTo(((FileClass) o).toString());
    }

    /**
     * Special function used to get all the content/data from a FileClass. Each
     * set of data is delimited by "delimiter"
     *
     * @param delimiter Delimiter used to separate the various content of the
     * fileClass
     * @return the content of the FileClass as a string
     */
    String toStringSpecial(String delimiter)
    {
        String buffer = this.FileName;
        for (int i = 0; i < associatedTags.size(); i++)
        {
            buffer += delimiter + associatedTags.get(i).toLowerCase().trim();
        }
        buffer += System.lineSeparator();

        if (rm.debug)
        {
            System.out.println("The data for this file is as follows");
            System.out.println(buffer);
        }

        return buffer;
    }

    /**
     * Disassociates a tag from this file
     *
     * @param deletedTag The tag to disassociate the file from
     * @param doOther if true, disassociates this file from the input Tag
     * @return <b>true</b> if the tag was removed<br><b>false</b> if the element
     * tag was not associated with the file
     */
    boolean removeTag(String deletedTag, boolean doOther)
    {
        boolean status = this.associatedTags.remove(deletedTag.toLowerCase().trim());

        if (doOther)
        {
            //Search for the specified file and remove the tag from the file
            for (int j = 0; j < rm.Tags.size(); j++)
            {

                if (rm.Tags.get(j).TagName.equals(deletedTag.toLowerCase().trim()))
                {

                    if (rm.debug)
                    {
                        System.err.println("Existing tag found within the file structure. Preparing to remove tag...");
                    }

                    rm.Tags.get(j).removeFile(this.FileName, false);

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
     * Removes all associated tags with the file
     */
    void cleanTags()
    {
        this.associatedTags.clear();
    }

    /**
     * Adds a set of tags to the current FileClass. A tag will not be added if
     * it is already associated with the file
     *
     * @param tagList The set of tags to be associated with the File
     */
    void addTag(String[] tagList)
    {
        for (int i = 0; i < tagList.length; i++)
        {
            addTag(tagList[i]);
        }
    }

    /**
     * Adds a tag to the current FileClass. A tag will not be added if it is
     * already associated with the file
     *
     * @param tagItem The tag to be associated with the File
     */
    void addTag(String tagItem)
    {
        if (!this.associatedTags.contains(tagItem.toLowerCase().trim()))
        {
            this.associatedTags.addElement(tagItem.toLowerCase().trim());

            //Search for the specified file and append a tag to it in the file class
            for (int j = 0; j < rm.Tags.size(); j++)
            {

                if (rm.Tags.get(j).TagName.equals(tagItem.toLowerCase().trim()))
                {

                    if (rm.debug)
                    {
                        System.err.println("Existing tag found within the file structure. File added to this Tag");
                    }

                    rm.Tags.get(j).addFiles(this.FileName);
                    rm.Tags.get(j).associatedFiles.sort(null);

                    return;
                }
                else
                {
                    // Tag.get(j) does not match the input tagItem
                }

            }

            rm.addTagClass(new TagClass(tagItem, rm));
            rm.Tags.get(rm.Tags.size() - 1).addFiles(this.FileName);
            rm.ui.refreshTagItemList();
            rm.ui.updateFileTree();
            rm.ui.updateTagTree();

        }
        else
        {
            if (rm.debug)
            {
                System.err.println("Tag NOT FOUND. Adding Tag now...\t" + tagItem);
            }

        }
    }

    /**
     * @return The number of tags associated with the file
     */
    public int associatedTagSize()
    {
        return this.associatedTags.size();
    }

}
