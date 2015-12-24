/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.FileInputStream;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

/**
 *
 * @author Xable Enterprises
 */
public class ProcessFilePowerPointNew
{

    XSLFPowerPointExtractor file;

    boolean validType = false;

    public ProcessFilePowerPointNew(String inputFile) throws Exception
    {
        file = new XSLFPowerPointExtractor(new XMLSlideShow(new FileInputStream(inputFile)));
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
