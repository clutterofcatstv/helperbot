import org.jibble.pircbot.*;
import java.io.*;
import java.util.*;

public class HelperBotMain {
    private String botName;
    private String botPassword;
    
    /**
     * Constructor for objects of class helperBotMain
     */
    public HelperBotMain()
    {
        
    }
    
    public String getBotName()
    {
        return(botName);
    }
    
    public String getBotPassword()
    {
        return(botPassword);
    }
    
    public static void main(String[] args) throws Exception {
        //helperBotMain mainBot = new helperBotMain();
        //mainBot.getSettings();
        
        //System.out.println(mainBot.getBotName());
        //System.out.println(mainBot.getBotPassword());
        
        // Now start our bot up.
        HelperBot bot = new HelperBot();
        
        // Enable debugging output.
        //bot.setVerbose(true);
        bot.startBot();
        
        // Connect to the IRC server.
        // reset here: http://www.twitchapps.com/tmi/
        //bot.connect("irc.twitch.tv",6667,"000");

        // Join the #pircbot channel.
        //bot.joinChannel("#clutterofcats");
        
        bot.sendMessage("#clutterofcats","Hello channel!");
        //bot.updateUsers();
        
        
    }
    
    
}