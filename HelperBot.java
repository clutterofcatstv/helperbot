/*

options for viewer storage:
SQLite (does not require installation)
yaml file (flat file)


RAFFLES/GAMBLING
    - stream owner start with !raffle or !gamble
    - takes point cost, total tickets, options to gamble on

    - set the state of the bot - none, raffle open, or gamble open
    - while in the correct state, accept user ticket purchases
    - bot confirms ticket purchases in point requests
    
    - allow stream owner to close the raffle/gamble
    - reset bot status
    - if raffle, pick a random ticket, announce the winner
    - winner has to claim prize (?)
    - if gamble, divvy out points between people that joined according to how many points they gambled by a percentage
    
    owner: !gamble open 1 5 [win|lose]
    
    user: !gamble 5 lose
    
    user: !gamble options
    
    owner: !gamble close lose
    
    owner: !raffle open 1 5
    
    user: !raffle 5
    
    owner: !raffle close
    
    update (2015/3/2)
    create classes to handle bets/gambling?
    update settings to JSON
    store twitchviewer tickets as total point cost
    
*/



import org.jibble.pircbot.*;
import org.json.simple.*;
import java.io.*;
import java.util.*;

public class HelperBot extends PircBot
{
    // bot status: d = default, r = raffle, g = gamble
    private Character botStatus = 'd';
    private Integer ticketTotal;
    private Integer ticketCost;
    private String botName;
    private String botPass;
    private String botChannel;
    private String pointsName;
    private Double pointsRate;
    private Vector viewerList = new Vector(5);
    private Vector messageQueue = new Vector(0);
    private Timer botMessageTimer = new Timer();
    private Timer botUserTimer = new Timer();
    private Timer botSaveTimer = new Timer();
    
    public HelperBot()
    {
    }
    
    public void startBot() throws Exception
    {
        this.getSettings();
        this.initViewers();
        this.setName(this.botName);
        this.connect("irc.twitch.tv",6667,this.botPass);
        this.joinChannel(this.botChannel);
        botMessageTimer.schedule(new BotMessenger(this), 7000, 7000);
        botUserTimer.schedule(new BotUserTracking(this), 6000, 60000);
        botSaveTimer.schedule(new ViewerSaveState(this.viewerList,true,"backup.txt"),10000,600000);
    }
    
    public String getChannel()
    {
        return this.botChannel;
    }
    
    public Vector getQueue()
    {
        return this.messageQueue;
    }
    
    public Vector getViewerList()
    {
        return this.viewerList;
    }
    
    private void getSettings()
    {
        // The name of the file to open.
        String fileName = "settings.txt";

        // This will reference one line at a time
        String line = null;
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) 
            {
                
                if (line.indexOf("twitch account name:") != -1)
                {
                    this.botName = line.substring(("twitch account name:").length()+1,line.length());
                }
                else if (line.indexOf("twitch password:") != -1)
                {
                    this.botPass = line.substring(("twitch password:").length()+1,line.length());
                }
                else if (line.indexOf("twitch channel to join:") != -1)
                {
                    this.botChannel = line.substring(("twitch channel to join:").length()+1,line.length());
                }
            }   

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");
            // Or we could just do this: 
            // ex.printStackTrace();
        }
    }
    
    private void initViewers()
    {
        //possibly swap to fileinputstream
        
        // The name of the file to open.
        String fileName = "viewers.txt";

        // This will reference one line at a time
        String line = null;
        String wholeFile = "";
        String tmpName = "";
        Double tmpPoints = 0.0;
        TwitchViewer tmpVwr;
        
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) 
            {
                wholeFile = wholeFile + line;
            }   

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        
        Object obj = JSONValue.parse(wholeFile);
        JSONArray tmpArray = (JSONArray)obj;
        JSONObject tmpJSON;
        for (int i = 0;i < tmpArray.size();i++)
        {
            tmpJSON = (JSONObject)tmpArray.get(i);
            tmpName = (String)tmpJSON.get("name");
            tmpPoints = (Double)tmpJSON.get("points");
            tmpVwr = new TwitchViewer(tmpName,tmpPoints,0);
            viewerList.addElement(tmpVwr);
        }
    }
    
    public void addUser(String tmpViewer)
    {
        TwitchViewer tmpTVwr = new TwitchViewer(tmpViewer,0.0,0);
        int tmpIndex = viewerList.indexOf(tmpTVwr);
        
        if (tmpIndex == -1)
        {
            viewerList.addElement(tmpTVwr);
        }
    }

    public void onMessage(String channel, String sender, String login, String hostname, String message){
        this.addUser(sender);
        String tmpMessage;
        String messageArgs[] = message.split(" ");
        
        if (messageArgs[0].equalsIgnoreCase("!points")) 
        {
            TwitchViewer tmpTMP = new TwitchViewer(sender,0.0,0);
            int tmpIndex = viewerList.indexOf(tmpTMP);
            TwitchViewer tmpVwr = (TwitchViewer) viewerList.get(tmpIndex);
            Double tmpPoints = tmpVwr.getPoints();
            Integer intPoints = tmpPoints.intValue();
            
            System.out.println(sender + ": " + intPoints.toString());
            String tmpStr = "p#" + sender + ": " + intPoints.toString();
            if (this.messageQueue.indexOf(tmpStr) == -1)
            {
                this.messageQueue.addElement(tmpStr);
            }
        }
        else if (messageArgs[0].equalsIgnoreCase("!add") && sender.equalsIgnoreCase("clutterofcats"))
        {
            //add points to a specific user
            //add options to add points to all users or all currently watching users
            if (messageArgs.length > 2)
            {
                TwitchViewer searchViewer = new TwitchViewer(messageArgs[1],0.0,0);
                TwitchViewer tmpViewer = (TwitchViewer) this.viewerList.get(this.viewerList.indexOf(searchViewer));
                tmpViewer.addPoints(Double.parseDouble(messageArgs[2]));
            }
        }
        else if (messageArgs[0].equalsIgnoreCase("!gamble") && sender.equalsIgnoreCase("clutterofcats"))
        {
            //check for variables after the main function
            if (botStatus.equals('d') && messageArgs.length > 3)
            {
                System.out.println("Gamble Open");
                botStatus = 'g';
                ticketCost = Integer.parseInt(messageArgs[1]);
                ticketTotal = Integer.parseInt(messageArgs[2]);
                
                //track option list? add gamble option to viewer class
            }
            else if (botStatus.equals('g'))
            {
                System.out.println("Gamble Closed");
                botStatus = 'd';
                ticketCost = 0;
                ticketTotal = 0;
            }
        }
        else if (messageArgs[0].equalsIgnoreCase("!raffle") && sender.equalsIgnoreCase("clutterofcats"))
        {
            //check for variables after the main function
            if (botStatus.equals('d') && messageArgs.length > 2)
            {
                System.out.println("Raffle Open");
                botStatus = 'r';
                ticketCost = Integer.parseInt(messageArgs[1]);
                ticketTotal = Integer.parseInt(messageArgs[2]);
            }
            else if (botStatus.equals('r'))
            {
                System.out.println("Raffle closed");
                botStatus = 'd';
                ticketCost = 0;
                ticketTotal = 0;
            }
        }
        else if (messageArgs[0].equalsIgnoreCase("!gamble") && botStatus.equals('g'))
        {
            System.out.println("Gamble Joined");
            //function to buy tickets
        }
        else if (messageArgs[0].equalsIgnoreCase("!raffle") && botStatus.equals('r') && messageArgs.length > 1)
        {
            System.out.println("Raffle Joined");
            //function to buy tickets
            
            Integer tmpTickets = Integer.parseInt(messageArgs[1]);
            
            if (tmpTickets <= ticketTotal && tmpTickets > 0)
            {
                TwitchViewer searchViewer = new TwitchViewer(sender,0.0,0);
                TwitchViewer tmpViewer = (TwitchViewer) this.viewerList.get(this.viewerList.indexOf(searchViewer));
                tmpViewer.buyTickets(tmpTickets,ticketCost);
            }
        }
        else if (messageArgs[0].equalsIgnoreCase("!close") && sender.equalsIgnoreCase("clutterofcats"))
        {
            // move most of this to a function that includes file,
            sendMessage(channel,"Bye channel!");
            System.out.println("Closed");
            disconnect();
            botMessageTimer.cancel();
            botUserTimer.cancel();
            botSaveTimer.purge();
            
            //try and get this to run with its own thread
            botSaveTimer.schedule(new ViewerSaveState(this.viewerList,false,"viewers.txt"),100,1000000);
            
            try 
            {
                Thread.sleep(4000);
            } 
            catch(InterruptedException ex) 
            {
                Thread.currentThread().interrupt();
            }
            
            botSaveTimer.cancel();
        }
    }
    
    public void onJoin(String channel, String sender, String login, String hostname) 
    {
        this.addUser(sender);
        
        TwitchViewer searchViewer = new TwitchViewer(sender,0.0,0);
        TwitchViewer tmpViewer = (TwitchViewer) this.viewerList.get(this.viewerList.indexOf(searchViewer));
        tmpViewer.setWatching(true);
    }
    
    public void onPart(String channel, String sender, String login, String hostname) 
    {
        this.addUser(sender);
        
        TwitchViewer searchViewer = new TwitchViewer(sender,0.0,0);
        TwitchViewer tmpViewer = (TwitchViewer) this.viewerList.get(this.viewerList.indexOf(searchViewer));
        tmpViewer.setWatching(false);
    }
}
