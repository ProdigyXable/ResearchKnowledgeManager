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
public class TagModelTree implements TreeModel {

    Vector<TagClass> Tags;
    String treeRootName = "List of Tags and Associated Files";
    String nullLeafName = "No files associated with this tag...";
    String emptyTree = "No tags detected within the system...";

    TagModelTree(Vector<TagClass> data) {

        this.Tags = data;
    }

    public boolean isHead(Object node) {
        return node.equals(treeRootName);
    }

    public Object getRoot() {
        return treeRootName;
    }

    public Object getChild(Object parent, int index) {
        if (parent.equals(treeRootName)) {
            if (Tags.size() > 0) {
                return Tags.get(index);
            } else {
                return emptyTree;
            }
        } else if (parent.getClass() == TagClass.class) {

            for (int i = 0; i < Tags.size(); i++) {
                if (Tags.get(i) == parent) {
                    if (Tags.get(i).associatedFiles.size() > 0) {
                        return Tags.get(i).associatedFiles.get(index);
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
            if (Tags.size() > 0) {
                return Tags.size();
            } else {
                return 1;
            }
        } else if (parent.getClass() == TagClass.class) {
            for (int i = 0; i < Tags.size(); i++) {
                if (Tags.get(i) == parent) {
                    return max(Tags.get(i).associatedFiles.size(), 1);
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

    // Probably not working as intended
    // Need to make sure this is working considers both scenarioes
    public int getIndexOfChild(Object parent, Object child) {
        if (child.equals(emptyTree)) {
            return 0;
        }

        if (isLeaf(child)) {
            for (int i = 0; i < ((TagClass) parent).associatedFiles.size(); i++) {
                if (((String) child).equals(((TagClass) parent).associatedFiles.get(i))) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < Tags.size(); i++) {
                if (((TagClass) child).equals(Tags.get(i))) {
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
