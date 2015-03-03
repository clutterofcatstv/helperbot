import java.util.*;
import org.jibble.pircbot.*;

public class BotUserTracking extends TimerTask
{
    HelperBot theBot;
    
    public BotUserTracking(HelperBot tmpBot)
    {
        this.theBot = tmpBot;
    }
    public void run() {
        System.out.println("incrementing points");
       
        //add users to list if not in the list
        /*System.out.println(this.theBot.getChannel());
        User[] users = (User[]) this.theBot.getUsers(this.theBot.getChannel());
        System.out.println(users.length);
        for(int i=0; i < users.length; i++)
        {
            System.out.println("User: " + users[i].getNick());
        }*/
        
        Vector tmpViewerList = this.theBot.getViewerList();
        for (int i=0;i<tmpViewerList.size();i++)
        {
            TwitchViewer tmpViewer = (TwitchViewer) tmpViewerList.get(i);
            System.out.println(tmpViewer.getName() + " watching: " + tmpViewer.getWatching());
            if (tmpViewer.getWatching())
            {
                tmpViewer.addPoints(1.0);
            }
        }
    }
}
