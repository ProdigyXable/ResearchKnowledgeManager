/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchknowledgemanager;

import static java.lang.Math.ceil;
import java.util.Vector;

/**
 *
 * @author Xable Enterprises
 */
public class ProcessFileTest
{

    ResearchKnowledgeManager rm;

    ProcessFileTest(ResearchKnowledgeManager r)
    {
        rm = r;
    }

    boolean isProcessableFile(FileClass f)
    {
        String[] arrayBuffer = f.toString().split("\\.");

        if (arrayBuffer == null)
        {
            System.err.println("ERROR IN isProcessableFile()!");
            return false;
        }
        return arrayBuffer[arrayBuffer.length - 1].matches("ppt.?|xls.?|doc.?|pdf|htm.?|mht");
    }

    String getFileContent(FileClass f) throws Exception
    {
        String[] arrayBuffer = f.toString().split("\\.");
        if (arrayBuffer[arrayBuffer.length - 1].matches("ppt"))
        {
            ProcessFilePowerPointOld buffer = new ProcessFilePowerPointOld(f.toString());
            return buffer.getText();
        }

        else if (arrayBuffer[arrayBuffer.length - 1].matches("pptx"))
        {
            ProcessFilePowerPointNew buffer = new ProcessFilePowerPointNew(f.toString());
            return buffer.getText();
        }

        else if (arrayBuffer[arrayBuffer.length - 1].matches("doc"))
        {
            ProcessFileWordOld buffer = new ProcessFileWordOld(f.toString());
            return buffer.getText();
        }

        else if (arrayBuffer[arrayBuffer.length - 1].matches("docx"))
        {
            ProcessFileWordNew buffer = new ProcessFileWordNew(f.toString());
            return buffer.getText();
        }

        else if (arrayBuffer[arrayBuffer.length - 1].matches("xls"))
        {
            ProcessFileExcelOld buffer = new ProcessFileExcelOld(f.toString());
            return buffer.getText();
        }

        else if (arrayBuffer[arrayBuffer.length - 1].matches("xlsx"))
        {
            ProcessFileExcelNew buffer = new ProcessFileExcelNew(f.toString());
            return buffer.getText();

        }

        else if (arrayBuffer[arrayBuffer.length - 1].matches("pdf"))
        {
            try (ProcessFilePDF buffer = new ProcessFilePDF(f.toString()))
            {
                return buffer.getText();
            }
        }

        else if (arrayBuffer[arrayBuffer.length - 1].matches("htm.?|mht"))
        {
            ProcessFileHTML buffer = new ProcessFileHTML(f.toString());
            return buffer.getText();

        }

        else
        {
            return null;
        }
    }

    boolean TagSearch(Vector<String> keywordVector, String documentContent, float sensitivity)
    {
        synchronized (this.rm.ui.executingThread)
        {
            if (keywordVector.size() > 0)
            {
                String[] individualWordArray = documentContent.split("[(\\s)(\\p{Punct})]+");
                if (sensitivity == 0)
                {
                    sensitivity = 1;
                }
                else
                {
                    sensitivity = (float) ceil((float) (individualWordArray.length * sensitivity) / 1000);
                }

                int foundCount = 0;

                for (String s : individualWordArray)
                {
                    for (String t : keywordVector)
                    {
                        if (this.rm.actionStatus == ResearchKnowledgeManager.activeState.PAUSED)
                        {
                            try
                            {
                                rm.ui.executingThread.wait();
                            }
                            catch (InterruptedException ex)
                            {
                                System.err.println("Interrupted Exception!!!");
                            }
                        }

                        if (t.equalsIgnoreCase(s))
                        {
                            foundCount++;
                        }
                    }
                }

                // System.err.println(foundCount);
                return foundCount >= sensitivity;
            }

            else
            {
                rm.ui.newMessage("This tag has no keywords! Search could not be completed...");
                return false;
            }

        }
    }

    public static void main(String[] args)
    {
        ProcessFileTest buffer = new ProcessFileTest(null);
        Vector<String> keywords = new Vector<>();
        keywords.add("RIP");
        keywords.add("cryptography");
        keywords.add("hijacking");

        ProcessFilePowerPointNew pbuffer;
        try
        {
            pbuffer = new ProcessFilePowerPointNew("C:\\Users\\Xable Enterprises\\Downloads\\4390ch8-sec.pptx");
            String content = pbuffer.getText();
            System.out.println(buffer.TagSearch(keywords, content, 5));
        }
        catch (Exception ex)
        {
            // Logger.getLogger(ProcessFileTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
