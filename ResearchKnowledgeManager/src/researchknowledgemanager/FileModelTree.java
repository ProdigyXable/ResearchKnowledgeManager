/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import static java.lang.Integer.max;
import java.util.Vector;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Xable Enterprises
 */
public class FileModelTree implements TreeModel {

    Vector<FileClass> Files;
    String treeRootName = "List of Files and Associated Tags";
    String nullLeafName = "No tags associated with this file...";
    String emptyTree = "No files detected within the system...";

    FileModelTree(Vector<FileClass> data) {
        this.Files = data;
    }

    public Object getRoot() {

        return this.treeRootName;
    }

    public Object getChild(Object parent, int index) {
        if (parent.equals(treeRootName)) {
            if (Files.size() > 0) {
                return Files.get(index);
            } else {
                return emptyTree;
            }
        } else if (parent.getClass() == FileClass.class) {

            for (int i = 0; i < Files.size(); i++) {
                if (Files.get(i) == parent) {
                    if (Files.get(i).associatedTags.size() > 0) {
                        return Files.get(i).associatedTags.get(index);
                    } else {
                        return nullLeafName;
                    }
                }
            }
        }

        System.err.println("NULL CHILD DETECTED!");
        return null;

    }

    public int getChildCount(Object parent) {
        if (parent.equals(treeRootName)) {
            if (Files.size() > 0) {
                return Files.size();
            } else {
                return 1;
            }
        } else if (parent.getClass() == FileClass.class) {
            for (int i = 0; i < Files.size(); i++) {
                if (Files.get(i) == parent) {
                    return max(Files.get(i).associatedTags.size(), 1);
                }
            }
        }
        System.err.println("Child not found!!!");
        return -1;
    }

    public boolean isLeaf(Object node) {
        if (node.equals(treeRootName)) {
            return false;
        } else {
            return (node.getClass() == String.class);
        }

    }

    public int getIndexOfChild(Object parent, Object child) {
        if (child.equals(emptyTree)) {
            return 0;
        }

        if (isLeaf(child)) {
            for (int i = 0; i < ((FileClass) parent).associatedTags.size(); i++) {
                if (((String) child).equals(((FileClass) parent).associatedTags.get(i))) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < Files.size(); i++) {
                if (((FileClass) child).equals(Files.get(i))) {
                    return i;
                }
            }
        }

        return -1;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    public void addTreeModelListener(TreeModelListener l) {

    }

    public void removeTreeModelListener(TreeModelListener l) {

    }

}
