/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.File;
import java.io.FileNotFoundException;
import org.jsoup.Jsoup;

/**
 *
 * @author Xable Enterprises
 */
public class ProcessFileHTML
{

    org.jsoup.nodes.Document documentBuffer;

    boolean validType = false;

    public ProcessFileHTML(String inputFile) throws Exception
    {
        if ((new File(inputFile)).exists())
        {
            documentBuffer = Jsoup.parse(new File(inputFile), null);
            validType = true;
        }

        else
        {
            throw new FileNotFoundException();
        }
    }

    boolean testType()
    {
        return this.validType;
    }

    String getText()
    {
        if (this.testType())
        {

            try
            {
                return documentBuffer.body().text();
            }
            catch (NullPointerException e)
            {
                return null;
            }

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
            ProcessFileHTML buffer = new ProcessFileHTML("C:\\Users\\Xable Enterprises\\Documents\\TESTDOCS\\TESTDOCS\\-- WFA Library --\\Labor Markets\\Facts for Features 2010 Sept_ 6 - U_S_ Census Bureau.mht");

            System.out.println(buffer.getText());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
