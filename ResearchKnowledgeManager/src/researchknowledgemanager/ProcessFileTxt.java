/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author Xable Enterprises
 */
public class ProcessFileTxt
{

    String inputFilepath;

    boolean validType = false;

    public ProcessFileTxt(String inputFile) throws Exception
    {
        this.inputFilepath = inputFile;
        this.validType = true;
    }

    boolean testType()
    {
        return this.validType;
    }

    String getText() throws IOException
    {
        if (this.testType())
        {

            return new String(Files.readAllBytes(Paths.get(inputFilepath)));
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
            ProcessFileTxt buffer = new ProcessFileTxt("C:\\Users\\Xable Enterprises\\Desktop\\Mass Sound Effect Library\\sonniss - please read.txt");

            System.out.println(buffer.getText());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
