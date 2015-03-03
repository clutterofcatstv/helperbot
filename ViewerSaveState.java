import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.*;
import java.io.*;

public class ViewerSaveState extends TimerTask 
{
    private Vector backupList;
    private boolean backupStatus;
    JSONArray backupOutput = new JSONArray();
    String theFileName;
    
    //take a flag to save viewer state

    public ViewerSaveState(Vector tmpVector,boolean tmpStatus,String tmpFileName)
    {
        backupList = (Vector) tmpVector.clone();
        backupStatus = tmpStatus;
        theFileName = tmpFileName;
    }

    public void run()
    {
        System.out.println("starting backup");
        
        for (int i = 0;i < backupList.size();i++)
        {
            JSONObject tmpObj = new JSONObject();
            TwitchViewer tmpVwr = (TwitchViewer) backupList.get(i);
            
            tmpObj.put("name",tmpVwr.getName());
            tmpObj.put("points",tmpVwr.getPoints());
            tmpObj.put("tickets",tmpVwr.getTickets());
            tmpObj.put("watching",tmpVwr.getWatching());
            
            backupOutput.add(tmpObj);
        }
        
        backupList.clear();
        
        try {
            // Assume default encoding.
            FileWriter fileWriter = new FileWriter(theFileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
            backupOutput.writeJSONString(bufferedWriter);

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println(
                "Error writing to file '"
                + theFileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
        System.out.println("backup complete");
    }
}
