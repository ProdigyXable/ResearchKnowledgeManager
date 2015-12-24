/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import org.apache.poi.hslf.extractor.PowerPointExtractor;

/**
 *
 * @author Xable Enterprises
 */
public class ProcessFilePowerPointOld
{

    PowerPointExtractor file;

    boolean validType = false;

    public ProcessFilePowerPointOld(String inputFile) throws Exception
    {
        file = new PowerPointExtractor(inputFile);
        validType = true;
    }

    boolean testType()
    {
        return validType;
    }

    String getText()
    {
        if (this.testType())
        {
            return file.getText();
        }

        else
        {
            return null;
        }

    }

    // Used to demo test the class
    public static void main(String args[])
    {

    }

}
