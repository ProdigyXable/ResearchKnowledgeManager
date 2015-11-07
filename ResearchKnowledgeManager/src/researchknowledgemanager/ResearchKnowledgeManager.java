/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

/**
 *
 * @author Xable Enterprises
 */
public class ResearchKnowledgeManager
{
    static boolean debugMode = true;
    
    void debugMode(boolean status)
    {
        if(status)
        {
            System.err.println("System is executing INSIDE debug mode!");
        } 
       
        else
        {
            System.err.println("System is executing OUTSIDE of debug mode");
        }

    }
    
    ResearchKnowledgeManager()
    {
        this.debugMode(debugMode);
    }
 
    public static void main(String[] args)
    {
        ResearchKnowledgeManager researchManager = new ResearchKnowledgeManager();
    }
    
}
