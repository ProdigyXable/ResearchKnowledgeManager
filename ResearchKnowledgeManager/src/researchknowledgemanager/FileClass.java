/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.File;
import java.util.Vector;

public class FileClass {

    String FileName;
    int incrementCount = 20;
    ResearchKnowledgeManager rm;

    //Array of Tag Names
    Vector<String> associatedTags;

    FileClass(String name, ResearchKnowledgeManager rkm) {
        FileName = name.toLowerCase();
        associatedTags = new Vector<>(incrementCount);
        rm = rkm;

    }

    FileClass(File file, ResearchKnowledgeManager rkm) {
        FileName = file.toString().toLowerCase();
        rm = rkm;
    }

    public String toString() {
        return (this.FileName);
    }

    String toStringSpecial(String delimeter, boolean debug) {
        String buffer = this.FileName;
        for (int i = 0; i < associatedTags.size(); i++) {
            buffer += delimeter + associatedTags.get(i).toLowerCase();
        }

        if (debug) {
            System.out.println("The data for this file is as follows");
            System.out.println(buffer);
        }

        return buffer;
    }

    boolean removeTag(String deletedTag) {
        return this.associatedTags.remove(deletedTag.toLowerCase());
    }

    void cleanTags() {
        this.associatedTags.clear();
    }

    void addTag(String[] tagList) {
        for (int i = 0; i < tagList.length; i++) {
            addTag(tagList[i]);
        }
    }

    void addTag(String tagItem) {
        if (!this.associatedTags.contains(tagItem.toLowerCase())) {
            this.associatedTags.addElement(tagItem.toLowerCase());

            //Search for the specified file and append a tag to it in the file class
            for (int j = 0; j < rm.Tags.size(); j++) {

                if (rm.Tags.get(j).TagName.equals(tagItem.toLowerCase())) {

                    if (rm.debug) {
                        System.err.println("Existing tag found within the file structure. File added to this Tag");
                    }

                    rm.Tags.get(j).addFiles(this.FileName);
                    return;
                } else {
                    System.err.println("Somethingi unknown happened...");
                }

            }

            rm.addTagClass(new TagClass(tagItem, rm));
            rm.Tags.get(rm.Tags.size() - 1).addFiles(this.FileName);
            rm.ui.refreshTagItemList();

        } else {
            if (rm.debug) {
                System.err.println("Tag NOT FOUND. Adding Tag now\t" + tagItem);
            }

        }
    }
}
