/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.util.Vector;

public class TagClass {

    String TagName;
    int incrementCount = 20;

    // Array of Filenames
    Vector<String> associatedFiles;

    // Associated keywords for the tag
    Vector<String> keywords;

    TagClass(String Name) {
        this.TagName = Name.toLowerCase();
        this.associatedFiles = new Vector<>(incrementCount);
        this.keywords = new Vector<>(incrementCount);
    }

    String toString(String delimeter, boolean debug) {
        String buffer = this.TagName;
        for (int i = 0; i < associatedFiles.size(); i++) {
            buffer += delimeter + associatedFiles.get(i).toLowerCase();
        }

        buffer += System.lineSeparator() + "keywords";

        for (int i = 0; i < keywords.size(); i++) {
            buffer += delimeter + keywords.get(i).toLowerCase();
        }

        if (debug) {
            System.out.println("The data for this tag is as follows:");
            System.out.println(buffer);
        }

        return buffer;
    }

    void addKeywords(String[] newKeywords) {
        this.keywords.trimToSize();

        for (int i = 0; i < newKeywords.length; i++) {
            if (!this.keywords.contains(newKeywords[i].toLowerCase())) {
                this.keywords.add(newKeywords[i].toLowerCase());
            }
        }
    }

    void cleanFiles() {
        this.associatedFiles.clear();
    }

    void cleanKeywords() {
        this.keywords.clear();
    }

    void addFiles(String[] newFiles) {
        this.associatedFiles.trimToSize();

        for (int i = 0; i < newFiles.length; i++) {
            if (!this.associatedFiles.contains(newFiles[i].toLowerCase())) {
                this.associatedFiles.add(newFiles[i].toLowerCase());
            }
        }
    }

    void addFiles(String newFile) {
        this.associatedFiles.trimToSize();
        if (!this.associatedFiles.contains(newFile.toLowerCase())) {
            this.associatedFiles.add(newFile.toLowerCase());
        }
    }

    boolean removeKeyword(String deletedKeyword) {
        return this.keywords.remove(deletedKeyword.toLowerCase());
    }

    boolean removeFile(String deletedFile) {
        return this.associatedFiles.remove(deletedFile.toLowerCase());
    }

    int keywordSize() {
        return this.keywords.size();
    }

    int associatedFilesSize() {
        return this.associatedFiles.size();
    }
}
