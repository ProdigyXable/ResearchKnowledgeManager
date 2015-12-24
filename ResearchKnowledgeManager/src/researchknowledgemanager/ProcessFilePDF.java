/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author Xable Enterprises
 */
public class ProcessFilePDF implements AutoCloseable
{

    PDDocument pdDoc = null;

    PDFTextStripper pdfStripper = new PDFTextStripper();

    boolean validType = false;

    public ProcessFilePDF(String inputFile) throws Exception
    {
        this.pdDoc = PDDocument.load(new File(inputFile));
        this.validType = true;
    }

    @Override
    public void close() throws Exception
    {
        this.pdDoc.close();
    }

    boolean testType()
    {
        return this.validType;
    }

    String getText() throws IOException
    {
        if (this.testType())
        {
            return pdfStripper.getText(this.pdDoc);
        }

        else
        {
            return null;
        }

    }

    // Used to demo test the class
    public static void main(String args[])
    {
        try
        {
            ProcessFilePDF buffer = new ProcessFilePDF("C:\\Users\\Xable Enterprises\\Desktop\\SamBenton_TechnicalResume_2015.pdf");

            //System.out.println(buffer.getText());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
