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

    //Array of Tag Names
    Vector<String> associatedTags;

    FileClass(String name) {
        FileName = name.toLowerCase();
        associatedTags = new Vector<>(incrementCount);

    }

    FileClass(File file) {
        FileName = file.toString().toLowerCase();
    }

    String toString(String delimeter, boolean debug) {
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
            if (!this.associatedTags.contains(tagList[i].toLowerCase())) {
                this.associatedTags.addElement(tagList[i].toLowerCase());
            }
        }
    }

    void addTag(String tagList) {
        if (!this.associatedTags.contains(tagList.toLowerCase())) {
            this.associatedTags.addElement(tagList.toLowerCase());
        }
    }
}
