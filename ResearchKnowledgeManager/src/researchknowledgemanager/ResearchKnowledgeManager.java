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
    static boolean debug = true;
    
    void debugMode(boolean status)
    {
       if(status)
        {
            System.out.println("System is running in debug mode");
        } 

    }
 
    public static void main(String[] args)
    {
        ResearchKnowledgeManager ResearchManager = new ResearchKnowledgeManager();
    }
    
}
