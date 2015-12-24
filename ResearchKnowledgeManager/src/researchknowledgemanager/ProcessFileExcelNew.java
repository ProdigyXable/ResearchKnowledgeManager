/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.FileInputStream;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Xable Enterprises
 */
public class ProcessFileExcelNew
{

    XSSFExcelExtractor file;

    boolean validType = false;

    public ProcessFileExcelNew(String inputFile) throws Exception
    {

        file = new XSSFExcelExtractor(new XSSFWorkbook(new FileInputStream(inputFile)));
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
