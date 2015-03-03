import java.util.*;

public class BotMessenger extends TimerTask
{
    String channel;
    HelperBot theBot;
    Vector messageQueue;
    
    public BotMessenger(HelperBot tmpBot)
    {
        this.theBot = tmpBot;
    }
    public void run() {
        /*
         * check message type
         * if points request print multiple (5)
         * remove all messages that have been printed
         * (use a temporary string)
         * 
         * Get the actual value of points per user
         */
        Vector tmpQueue = this.theBot.getQueue();
        String tmpChannel = this.theBot.getChannel();
        if (tmpQueue.size() > 0)
        {
            String tmpMessage = (String) tmpQueue.remove(0);
            if (tmpMessage.substring(0,2).equals("p#"))
            {
                String tmpFullLine = "Points - " + tmpMessage.substring(2,tmpMessage.length());
                
                Integer tmpIndex = 0;
                
                if (tmpQueue.size() > 0)
                {
                    String tmpCheck = (String) tmpQueue.firstElement();
                    while (tmpQueue.size() > 0 && tmpCheck.substring(0,2).equals("p#") && tmpIndex < 5)
                    {
                        tmpMessage = (String) tmpQueue.remove(0);
                        tmpFullLine = tmpFullLine + ", " + tmpMessage.substring(2,tmpMessage.length());
                        if (tmpQueue.size() > 0)
                        {
                            tmpCheck = (String) tmpQueue.firstElement();
                        }
                        tmpIndex++;
                    }
                }
                
                this.theBot.sendMessage(tmpChannel,tmpFullLine);
                System.out.println("Points Message: " + tmpFullLine);
            }
            else
            {
                this.theBot.sendMessage(tmpChannel,tmpMessage);
                System.out.println("System Message: " + tmpChannel);
            }
        }
        else
        {
            System.out.println("No Messages Found");
        }
    }
}