
public class TwitchViewer
{
    // instance variables - replace the example below with your own
    private String viewerName;
    private Double viewerPoints;
    private Integer viewerTickets;
    private boolean viewerWatching;
    private String viewerGOption;

    /**
     * Constructor for objects of class TwitchViewer
     */
    public TwitchViewer(String tmpName,Double tmpPoints,Integer tmpTickets)
    {
        viewerName = tmpName;
        viewerPoints = tmpPoints;
        viewerTickets = tmpTickets;
        viewerWatching = false;
    }

    public String getName()
    {
        return viewerName;
    }
    
    public boolean getWatching()
    {
        return viewerWatching;
    }

    public Double getPoints()
    {
        return viewerPoints;
    }

    public Integer getTickets()
    {
        return viewerTickets;
    }
    
    public void setWatching(boolean tmpWatching)
    {
        viewerWatching = tmpWatching;
    }
    
    public void addPoints(Double newPoints)
    {
        viewerPoints = viewerPoints + newPoints;
    }
    
    public void subtractPoints(Double newPoints)
    {
        viewerPoints = viewerPoints - newPoints;
        if (viewerPoints < 0)
        {
            viewerPoints = 0.0;
        }
    }
    
    public void setGOption(String tmpOption)
    {
        viewerGOption = tmpOption;
    }
    
    public String getGOption()
    {
        return viewerGOption;
    }
    
    public void buyTickets(Integer numTickets,Integer ticketCost)
    {
        System.out.println(numTickets);
        System.out.println(ticketCost);
        if (viewerPoints >= numTickets * ticketCost)
        {
            System.out.println("if 1");
            System.out.println(numTickets);
            System.out.println(ticketCost);
            viewerPoints = viewerPoints - (numTickets * ticketCost);
            viewerTickets = numTickets;
        }
        else if (viewerPoints >= ticketCost)
        {
            System.out.println("if 2");
            System.out.println(numTickets);
            System.out.println(ticketCost);
            System.out.println(viewerPoints);
            Double tmpCost = viewerPoints / ticketCost.doubleValue();
            System.out.println(ticketCost.doubleValue());
            System.out.println(tmpCost);
            viewerTickets = tmpCost.intValue();
            System.out.println(viewerTickets);
            viewerPoints = viewerPoints - (viewerTickets * ticketCost);
        }
        else
        {
            System.out.println("if 3");
        }
        System.out.println("");
    }
    
   public boolean equals(Object other)
   {
      if (other == null)
      {
         return false;
      }

      if (this.getClass() != other.getClass())
      {
         return false;
      }
      
      TwitchViewer tmpTMP = (TwitchViewer) other;
      
      if (this.getName().equalsIgnoreCase(tmpTMP.getName()))
      {
          return true;
      }
      
      return false;
   }
}
