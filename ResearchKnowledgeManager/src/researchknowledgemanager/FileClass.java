/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.*;
import java.util.*;

public class FileClass
{
    String FileName;
    
    //Array of Tag Names
    Vector<String> associatedTags;
    
    FileClass(String name)
    {
        FileName = name;
    }
    
    FileClass(File file)
    {
        FileName = file.toString();
    }
    
    String toString(String delimeter, boolean debug)
    {
        String buffer = this.FileName;
        for(int i = 0; i < associatedTags.size(); i++)
        {
            buffer += delimeter + associatedTags.get(i);
        }
        
        if(debug)
        {
            System.out.println("The data for this file is as follows");
            System.out.println(buffer);
        }
        
        return buffer;
    }
    
    boolean removeTag(String deletedTag)
    {
        return this.associatedTags.remove(deletedTag);
    }
    
    void addTag(String[] tagList)
    {
        for(int i = 0; i < tagList.length; i++)
        {   if(!this.associatedTags.contains(tagList[i]))
            {
                this.associatedTags.addElement(tagList[i]);
            }
        }
    }
}
