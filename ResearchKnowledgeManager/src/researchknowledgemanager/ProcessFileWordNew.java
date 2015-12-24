/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.FileInputStream;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 *
 * @author Xable Enterprises
 */
public class ProcessFileWordNew
{

    XWPFWordExtractor file;

    boolean validType = false;

    public ProcessFileWordNew(String inputFile) throws Exception
    {
        file = new XWPFWordExtractor(new XWPFDocument(new FileInputStream(inputFile)));
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
