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
    ResearchKnowledgeManager rm;

    // Array of Filenames
    Vector<String> associatedFiles;

    // Associated keywords for the tag
    Vector<String> keywords;

    TagClass(String Name, ResearchKnowledgeManager rkm) {
        this.TagName = Name.toLowerCase().trim();
        this.associatedFiles = new Vector<>(incrementCount);
        this.keywords = new Vector<>(incrementCount);
        rm = rkm;

    }

    public String toString() {
        return (this.TagName);
    }

    String toStringSpecial(String delimeter) {
        String buffer = "Tag: " + this.TagName;
        for (int i = 0; i < associatedFiles.size(); i++) {
            buffer += delimeter + associatedFiles.get(i).toLowerCase().trim();
        }

        if (this.keywords.size() > 0) {
            buffer += delimeter + "keywords";

            for (int i = 0; i < keywords.size(); i++) {
                buffer += delimeter + keywords.get(i).toLowerCase().trim();
            }
            buffer += System.lineSeparator();
        } else {
            if (rm.debug) {
                System.err.println();
                System.err.println("This tags does not contain any keywords!");
                System.err.println();
            }
        }

        if (rm.debug) {
            System.out.println("The data for this tag is as follows:");
            System.out.println("----------");
            System.out.println(buffer);
        }

        return buffer;
    }

    void addKeywords(String newKeyword) {
        if (!this.keywords.contains(newKeyword.toLowerCase().trim())) {
            this.keywords.add(newKeyword.toLowerCase().trim());
        }
    }

    void addKeywords(String[] newKeywords) {

        for (int i = 0; i < newKeywords.length; i++) {
            addKeywords(newKeywords[i]);
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
            addFiles(newFiles[i]);
        }
    }

    void addFiles(String newFile) {

        if (!this.associatedFiles.contains(newFile.toLowerCase().trim())) {
            this.associatedFiles.add(newFile.toLowerCase().trim());

            //Search for the specified file and append a tag to it in the file class
            for (int j = 0; j < rm.Files.size(); j++) {

                if (rm.Files.get(j).FileName.equals(newFile.toLowerCase().trim())) {

                    if (rm.debug) {
                        System.err.println("Exising file found within the tag structure. Tag added to this file");
                    }

                    rm.Files.get(j).addTag(this.TagName);
                    return;
                }

            }
            rm.addFileClass(new FileClass(newFile, rm));
            rm.ui.updateFileTree();
            rm.ui.updateTagTree();
            rm.Files.get(rm.Files.size() - 1).addTag(this.TagName);

        } else {
            if (rm.debug) {
                System.err.println("FILE NOT FOUND. Adding file now\t" + newFile);
            }

        }
    }

    boolean removeKeyword(String deletedKeyword) {
        return this.keywords.remove(deletedKeyword.toLowerCase().trim());
    }

    boolean removeFile(String deletedFile) {
        return this.associatedFiles.remove(deletedFile.toLowerCase().trim());
    }

    int keywordSize() {
        return this.keywords.size();
    }

    int associatedFilesSize() {
        return this.associatedFiles.size();
    }
}
