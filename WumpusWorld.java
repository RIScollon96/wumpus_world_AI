import java.util.*;
import java.io.*;

public class WumpusWorld {
    //FLAGS 
    public static final long PIT_FLAG       = 1;
    public static final long WUMPUS_FLAG    = 2;
    public static final long GOLD_FLAG      = 4;
    public static final long BREEZE_FLAG    = 8;
    public static final long STENCH_FLAG    = 16;
    public static final long GLITTER_FLAG   = 32;
    public static final long BUMP_FLAG      = 64;
    public static final long SCREAM_FLAG    = 128;
    public static final long SUPMUW_FLAG    = 256;
    public static final long MOO_FLAG       = 512;
    public static final long OUT_OF_BOUND_FLAG  = 1024;
    
    //DIRECTIONS
    private static final int FACING_NORT    = 1;
    private static final int FACING_EAST    = 2;
    private static final int FACING_WEST    = 3;
    private static final int FACING_SOUTH   = 4;

    /*
     * Actions
     */
    public static final int GO_FORWARD      = 1;
    public static final int TURN_LEFT       = 2;
    public static final int TURN_RIGHT      = 3;
    public static final int GRAB            = 4;
    public static final int SHOOT           = 5;
    public static final int CLIMB           = 6;

    public static final String STR_GO_FORWARD   = "walk";
    public static final String STR_TURN_LEFT    = "left";
    public static final String STR_TURN_RIGHT   = "right";
    public static final String STR_GRAB         = "grab";
    public static final String STR_SHOOT        = "shoot";
    public static final String STR_CLIMB        = "climb";


    private long     perceptions = 0;
    //costs (ARROW COST IS SET TO 10)
    private double  actionCost  = 1.0;
    private double  outWithGold = 1000.0;
    private double  killCost    = 1000.0;
    //WORLD DIMENSIONS
    private int     worldDimensionX = 10;
    private int     worldDimensionY = 10;
    //AGENT INFO
    private int     agentPosX   = 0;
    private int     agentPosY   = 0;
    private int     agentDir    = FACING_EAST;
    private boolean agentSafe   = true;
    private double  agentScore  = 0.0;
    private boolean agentArrow  = true;
    private boolean agentGold   = false;
    private boolean agentOut    = false;
    //ANIMATIONS OPTION 
    private boolean animation   = true;
    //WORLD VARIABLE
    private static long world[];
    
    PrintStream out = System.out;
    Scanner reader;
    
    //CONSTRUCTOR FOR WUMPUS WORLD
    public WumpusWorld(String fileName, String animate) {

        this.worldDimensionX = worldDimensionX;
        this.worldDimensionY = worldDimensionY;
        
        
        world = new long[worldDimensionX * worldDimensionY];
        Arrays.fill(world, 0);
        
        if(animate.equals("n"))
        {
            animation = false;
        }
        try
        {
            FileIO f = new FileIO(fileName, 1);
            String string = f.readLine();
            while (string!=null)
            {
                /*
                COORDINATES NEED TO START WITH 0,0 and go to 9,9
                */
                String[] stringArray = string.split(",");
                if (stringArray.length == 3)
                {
                    int cordinate1 = Integer.parseInt(stringArray[0]);
                    int cordinate2 = Integer.parseInt(stringArray[1]);
                    cordinate2 = cordinate2 * worldDimensionX;
                    cordinate1 += cordinate2;
                    String item = stringArray[2].toLowerCase();
                    //get coordinates for up,down,left,right
                    int s1 = cordinate1 - worldDimensionX;
                    int s2 = cordinate1 + worldDimensionX;
                    int s3 = cordinate1 - 1;
                    int s4 = cordinate1 + 1;
                    //gets other four coordinates for those diagonal
                    int s5 = cordinate1 - worldDimensionX + 1;
                    int s6 = cordinate1 + worldDimensionX + 1;
                    int s7 = cordinate1 - worldDimensionX - 1;
                    int s8 = cordinate1 + worldDimensionX - 1;
                    
                    if(item.equals("wumpus"))//sets WUMPUS_FLAG and STENCH_FLAG
                    {
                        world[cordinate1] |= WUMPUS_FLAG;
                        if (s1 >= 0) world[s1] |= STENCH_FLAG;
                        if (s2 < world.length)  world[s2] |= STENCH_FLAG;
                        if (s3 >= 0 && (cordinate1 % worldDimensionX != 0)) world[s3] |= STENCH_FLAG;
                        if (s4 < world.length && (cordinate1 % worldDimensionX != worldDimensionX-1)) world[s4] |= STENCH_FLAG;
                    }
                    else if(item.equals("pit"))//sets PIT_FLAG and BREEZE_FLAG
                    {
                        world[cordinate1] |= PIT_FLAG;
                        if (s1 >= 0) world[s1] |= BREEZE_FLAG;
                        if (s2 < world.length)  world[s2] |= BREEZE_FLAG;
                        if (s3 >= 0 && (cordinate1 % worldDimensionX != 0)) world[s3] |=BREEZE_FLAG;
                        if (s4 < world.length && (cordinate1 % worldDimensionX != worldDimensionX-1)) world[s4] |= BREEZE_FLAG;
                    }                       
                    else if(item.equals("supmuw"))//sets SUPMUW_FLAG and MOO_FLAGS
                    {
                        world[cordinate1] |= SUPMUW_FLAG;
                        if (s1 >= 0) world[s1] |= MOO_FLAG;
                        if (s2 < world.length)  world[s2] |= MOO_FLAG;
                        if (s3 >= 0 && (cordinate1 % worldDimensionX != 0)) world[s3] |=MOO_FLAG;
                        if (s4 < world.length && (cordinate1 % worldDimensionX != worldDimensionX-1)) world[s4] |= MOO_FLAG;
                        //moos are also diagonal from the supmuw position
                        if (s5 >= 0 && (cordinate1 % worldDimensionX != worldDimensionX-1)) world[s5] |= MOO_FLAG;
                        if (s6 < world.length && (cordinate1 % worldDimensionX != worldDimensionX-1)) world[s6] |= MOO_FLAG;
                        if (s7 >= 0 && (cordinate1 % worldDimensionX != 0)) world[s7] |= MOO_FLAG;
                        if (s8 < world.length && (cordinate1 % worldDimensionX != 0)) world[s8] |= MOO_FLAG;
                    }
                    else if(item.equals("gold")) //sets GOLD and GLITTER_FLAGS
                    {
                        world[cordinate1] |= GOLD_FLAG;
                        world[cordinate1] |= GLITTER_FLAG;
                    }
                    else if(item.equals("bound")) //sets OUT_OF_BOUND_FLAGS
                    {
                        world[cordinate1] |= OUT_OF_BOUND_FLAG;
                    }
                    
                }
                string = f.readLine();
            }
        }
        catch (FileIOException fioe)
        {
            
        }
        catch (NumberFormatException nfe)
        {
            
        }
        
        perceptions |= world[0];
        
        reader = new Scanner(System.in);
    }
    /*
     * Get positions for WorldDimensions and AgentDimensions
     */
    public int getX()
    {
        return worldDimensionX;
    }
    
    public int getY()
    {
        return worldDimensionY;
    }
    
    public int getAgentX()
    {
        return agentPosX;
    }
    
    public int getAgentY()
    {
        return agentPosY;
    }
    //Called by makeAction(String action). Actually calls action to be used
    public void makeAction(int action) {
        perceptions = 0;
        switch (action) {
            case GO_FORWARD : goForward(); break;
            case TURN_LEFT  : turnLeft(); break;
            case TURN_RIGHT : turnRight(); break;
            case GRAB       : grab(); break;
            case SHOOT      : shoot(); break;
            case CLIMB      : climb(); break;
        }
        //decrement score after action
        agentScore -= actionCost;
        int agentPos = agentPosY*worldDimensionX + agentPosX;
        perceptions |= world[agentPos];
        
        
        if((perceptions & SUPMUW_FLAG) !=0 && (perceptions & PIT_FLAG) != 0)
        {
            //Do nothing because SUPMUW and PIT doesn't do anything.
        }
        else if((perceptions & SUPMUW_FLAG) != 0 && (perceptions & STENCH_FLAG) != 0)
        {
            //Kills you because SUPMUW acts like WUMPUS if it smells a STENCH
            agentScore -= killCost;
            agentSafe = false;
            agentOut = true;
        }
        else if ((perceptions & PIT_FLAG) != 0 || (perceptions & WUMPUS_FLAG) != 0) 
        {
            //Kills you if there is a PIT or a WUMPUS
            agentScore -= killCost;
            agentSafe = false;
            agentOut = true;
        }
        else if((perceptions & SUPMUW_FLAG) != 0)//assume supmuw leaves once given food
        {
            //Once you get food from the SUPMUW, assume its gone
            agentScore += 100;
            world[agentPos] &= (0xFFFFFFFFL ^ SUPMUW_FLAG);
            int s1 = agentPos - worldDimensionX;
            int s2 = agentPos + worldDimensionX;
            int s3 = agentPos - 1;
            int s4 = agentPos + 1;
            int s5 = agentPos - worldDimensionX + 1;
            int s6 = agentPos + worldDimensionX + 1;
            int s7 = agentPos - worldDimensionX - 1;
            int s8 = agentPos + worldDimensionX - 1;

            if (s1 >= 0) world[s1] &= (0xFFFFFFFFL ^ MOO_FLAG);
            if (s2 < world.length)  world[s2] &= (0xFFFFFFFFL ^ MOO_FLAG);
            if (s3 >= 0 && (agentPos % worldDimensionX != 0)) world[s3] &= (0xFFFFFFFFL ^ MOO_FLAG);
            if (s4 < world.length && (agentPos % worldDimensionX != worldDimensionX-1)) world[s4] &= (0xFFFFFFFFL ^ MOO_FLAG);
            
            if (s5 >= 0 && (agentPos % worldDimensionX != worldDimensionX-1)) world[s5] &= (0xFFFFFFFFL ^ MOO_FLAG);
            if (s6 < world.length && (agentPos % worldDimensionX != worldDimensionX-1)) world[s6] &= (0xFFFFFFFFL ^ MOO_FLAG);
            if (s7 >= 0 && (agentPos % worldDimensionX != 0)) world[s7] &= (0xFFFFFFFFL ^ MOO_FLAG);
            if (s8 < world.length && (agentPos % worldDimensionX != 0)) world[s8] &= (0xFFFFFFFFL ^ MOO_FLAG);
        }
    }
    
    public void makeAction(String action) //determine whether to do animation or not
    {
        if(animation == false)
        {
            if (STR_GO_FORWARD.equals(action)) makeAction(GO_FORWARD);
            else if (STR_TURN_LEFT.equals(action)) makeAction(TURN_LEFT);
            else if (STR_TURN_RIGHT.equals(action)) makeAction(TURN_RIGHT);
            else if (STR_GRAB.equals(action)) makeAction(GRAB);
            else if (STR_SHOOT.equals(action)) makeAction(SHOOT);
            else if (STR_CLIMB.equals(action)) makeAction(CLIMB);
        }
        else if(animation == true)
        {
           if (STR_GO_FORWARD.equals(action))
            {
                Animations.display("walk");
                makeAction(GO_FORWARD);
            }
            else if (STR_TURN_LEFT.equals(action))
            {
                Animations.display("left");
                makeAction(TURN_LEFT);
            }
            else if (STR_TURN_RIGHT.equals(action))
            {
                Animations.display("right");
                makeAction(TURN_RIGHT);
            }
            else if (STR_GRAB.equals(action))
            {
                if ((world[agentPosY*worldDimensionX + agentPosX] & GOLD_FLAG) != 0)
                    Animations.display("grab");
                else
                    Animations.display("no_gold");
                makeAction(GRAB);
            }
            else if (STR_SHOOT.equals(action))
            {
                if (agentArrow)
                    Animations.display("shoot");
                else
                    Animations.display("no_arrow");
                makeAction(SHOOT);
            }
            else if (STR_CLIMB.equals(action))
            {
                Animations.display("climb");
                makeAction(CLIMB);
            }
        }
    }
    
    private void goForward() //if its out of bounds or there is a OUT OF BOUND FLAG in front of you, get BUMP FLAG
    {
        switch(agentDir) { //different for each direction
            case FACING_NORT : {
                if (agentPosY == worldDimensionY-1) perceptions |= BUMP_FLAG;
                else if((world[agentPosX+agentPosY*10+10] & OUT_OF_BOUND_FLAG) != 0) perceptions |= BUMP_FLAG;
                else agentPosY++;
            } break;
            case FACING_EAST : {
                if (agentPosX == worldDimensionX-1) perceptions |= BUMP_FLAG;
                else if((world[agentPosX+agentPosY*10+1] & OUT_OF_BOUND_FLAG) != 0) perceptions |= BUMP_FLAG;
                else agentPosX++;
            } break;
            case FACING_WEST : {
                if (agentPosX == 0) perceptions |= BUMP_FLAG;
                else if((world[agentPosX+agentPosY*10-1] & OUT_OF_BOUND_FLAG) != 0) perceptions |= BUMP_FLAG;
                else agentPosX--;
            } break;
            case FACING_SOUTH : {
                if (agentPosY == 0) perceptions |= BUMP_FLAG;
                else if((world[agentPosX+agentPosY*10-10] & OUT_OF_BOUND_FLAG) != 0) perceptions |= BUMP_FLAG;
                else agentPosY--;
            } break;
        }
    }
    
    private void turnLeft() { //changing direction depending on your current directions
        switch (agentDir) {
            case FACING_NORT : agentDir = FACING_WEST; break;
            case FACING_EAST : agentDir = FACING_NORT; break;
            case FACING_SOUTH: agentDir = FACING_EAST; break;
            case FACING_WEST : agentDir = FACING_SOUTH; break;
        }
    }

    private void turnRight() {//changing direction depending on your current directions
        switch (agentDir) {
            case FACING_NORT : agentDir = FACING_EAST; break;
            case FACING_EAST : agentDir = FACING_SOUTH; break;
            case FACING_SOUTH: agentDir = FACING_WEST; break;
            case FACING_WEST : agentDir = FACING_NORT; break;
        }
    }

    private void grab() { //See if GOLD FLAG is there. if not don't do anyting
        if ((world[agentPosY*worldDimensionX + agentPosX] & GOLD_FLAG) != 0) 
        {
            agentGold = true;
            world[agentPosY*worldDimensionX + agentPosX] &= (0xFFFFFFFFL ^   GOLD_FLAG);
            world[agentPosY*worldDimensionX + agentPosX] &= (0xFFFFFFFFL ^ GLITTER_FLAG);
        }
    }

    private void shoot() { //Shoots an arrow that can kill a WUMPUS or SUPMUW
        if (!agentArrow) return; //if no arrow, can't shoot
        
        int start = 0, end = 0, arrowX = agentPosX, arrowY = agentPosY, dx=0, dy=0;
        switch (agentDir) 
        {
            case FACING_NORT : start = agentPosY; end=worldDimensionY; dx=0; dy=1; break;
            case FACING_EAST : start = agentPosX; end = worldDimensionX; dx=1; dy=0; break;
            case FACING_SOUTH: start = 0; end = agentPosY; dx=0; dy=-1; break;
            case FACING_WEST : start = 0; end = agentPosX; dx=-1; dy=0; break;
        }
        
        boolean wumpusKilled = false;
        boolean supmuwKilled = false;
        int pos = start;
        while (pos++ < end) 
        {
            arrowX += dx;
            arrowY += dy;
            
            int arrowPos = arrowY * worldDimensionX + arrowX;
            if ((world[arrowPos] & WUMPUS_FLAG) != 0) {
                perceptions |= SCREAM_FLAG;
                wumpusKilled = true;
            }
            else if ((world[arrowPos] & SUPMUW_FLAG) != 0)
            {
                perceptions |= SCREAM_FLAG;
                supmuwKilled = true;
            }

            if (wumpusKilled) { //if wumpus dies, get rid of flags
                world[arrowPos] &= (0xFFFFFFFFL ^ WUMPUS_FLAG);
                
                int s1 = arrowPos - worldDimensionX;
                int s2 = arrowPos + worldDimensionX;
                int s3 = arrowPos - 1;
                int s4 = arrowPos + 1;

                if (s1 >= 0) world[s1] &= (0xFFFFFFFFL ^ STENCH_FLAG);
                if (s2 < world.length)  world[s2] &= (0xFFFFFFFFL ^ STENCH_FLAG);
                if (s3 >= 0 && (arrowPos % worldDimensionX != 0)) world[s3] &= (0xFFFFFFFFL ^ STENCH_FLAG);
                if (s4 < world.length && (arrowPos % worldDimensionX != worldDimensionX-1)) world[s4] &= (0xFFFFFFFFL ^ STENCH_FLAG);
                break;
            }
            else if (supmuwKilled) //if supmuw dies, get rid of flags
            {
                world[arrowPos] &= (0xFFFFFFFFL ^ SUPMUW_FLAG);
                int s1 = arrowPos - worldDimensionX;
                int s2 = arrowPos + worldDimensionX;
                int s3 = arrowPos - 1;
                int s4 = arrowPos + 1;
                
                
                int s5 = arrowPos - worldDimensionX + 1;
                int s6 = arrowPos + worldDimensionX + 1;
                int s7 = arrowPos - worldDimensionX - 1;
                int s8 = arrowPos + worldDimensionX - 1;

                if (s1 >= 0) world[s1] &= (0xFFFFFFFFL ^ MOO_FLAG);
                if (s2 < world.length)  world[s2] &= (0xFFFFFFFFL ^ MOO_FLAG);
                if (s3 >= 0 && (arrowPos % worldDimensionX != 0)) world[s3] &= (0xFFFFFFFFL ^ MOO_FLAG);
                if (s4 < world.length && (arrowPos % worldDimensionX != worldDimensionX-1)) world[s4] &= (0xFFFFFFFFL ^ MOO_FLAG);
                
                if (s5 >= 0 && (arrowPos % worldDimensionX != worldDimensionX-1)) world[s5] &= (0xFFFFFFFFL ^ MOO_FLAG);
                if (s6 < world.length && (arrowPos % worldDimensionX != worldDimensionX-1)) world[s6] &= (0xFFFFFFFFL ^ MOO_FLAG);
                if (s7 >= 0 && (arrowPos % worldDimensionX != 0)) world[s7] &= (0xFFFFFFFFL ^ MOO_FLAG);
                if (s8 < world.length && (arrowPos % worldDimensionX != 0)) world[s8] &= (0xFFFFFFFFL ^ MOO_FLAG);
                break;
            }
        }
        agentArrow = false;
        agentScore -= 10;
    }
    
    private void climb() { //attempt to climb out. only woorks at 0,0
        if (agentPosX == 0 && agentPosY == 0) {
            if (agentGold) agentScore += outWithGold;
            agentOut = true;
        }
    }
    
    public double getAgentScore() {return agentScore;}
    public boolean isAgentOut() {return agentOut;}
    public boolean isAgentOk() {return agentSafe;}

    public String getPerceptions() //get string of perceptions
    {
        boolean stench = (perceptions & STENCH_FLAG) != 0;
        boolean breeze = (perceptions & BREEZE_FLAG) != 0;
        boolean glitter = (perceptions & GLITTER_FLAG) != 0;
        boolean bump = (perceptions & BUMP_FLAG) != 0;
        boolean scream = (perceptions & SCREAM_FLAG) != 0;
        boolean moo = (perceptions & MOO_FLAG) != 0;
        
        String st, br, g, bu, sc, m;
        
        st = ((stench) ? "stench" : "nothing");
        br = ((breeze) ? "breeze" : "nothing");
        g = ((glitter) ? "glitter" : "nothing");
        bu = ((bump) ? "bump" : "nothing");
        sc = ((scream) ? "scream" : "nothing");
        m = ((moo) ? "moo" : "nothing");

        return "perception(,"+st+","+br+","+g+","+bu+","+sc+","+m+",)";
    } 
    
    public void printWorld() //prints each location and what flags are there
    {
        long temp = 0;
        for(int k = 0; k < world.length; k++)
        {
            temp = world[k];
            if((temp & PIT_FLAG) != 0)
                System.out.println(""+k+"PIT");
            if((temp & WUMPUS_FLAG) != 0)
                System.out.println(""+k+"WUMPUS");
            if((temp & GOLD_FLAG) != 0)
                System.out.println(""+k+"GOLD");
            if((temp & BREEZE_FLAG) != 0)
                System.out.println(""+k+"BREEZE");
            if((temp & STENCH_FLAG) != 0)
                System.out.println(""+k+"STENCH");
            if((temp & GLITTER_FLAG) != 0)
                System.out.println(""+k+"GLITTER");
            if((temp & SUPMUW_FLAG) != 0)
                System.out.println(""+k+"SUPMUW");
            if((temp & MOO_FLAG) != 0)
                System.out.println(""+k+"MOO");
            if((temp & OUT_OF_BOUND_FLAG) != 0)
                System.out.println(""+k+"OUT_OF_BOUND");
        }
    }
    
    public final void setOut(PrintStream out) {
        this.out = out;
    }

    public final PrintStream getOut() {
        return out;
    }
    
    public void readAction() {
        String action = reader.nextLine();
        makeAction(action);
    }
    
    public String readLine() {
        return reader.nextLine();
    }
    
    
    /*
     * Pass in 3 arguments. 1st should be file name, 2nd should be y/n for animations
     *  3 should be y/n for wait. If the 2nd is y for animation, then timere will be set
     *  by default
     * Prints perceptions, score, and current position after every action.
     */
    public static void main(String args[]) {
        int n = 10;
        String fileThing = "DEFAULT";
        String an = "y";
        String wait = "y";
        if (args.length == 3)
        {
            try
            {
                fileThing = args[0];
                an = args[1];
                wait = args[2];
            }
            catch (Exception e)
            {
                
            }
        }
        System.out.println("SIMULATION_STARTED");
        for (int i = 0; i < n; i++) 
        {
            WumpusWorld world = new WumpusWorld(fileThing,an);
            WumpusAgent agent = new WumpusAgent(world.getX(),world.getY());
            
            System.out.println("");

            System.out.println("EPISODE_STARTED");
            
            
            
            String perc = world.getPerceptions();
            System.out.println("AGENT_SCORE=" + world.agentScore);
            System.out.println("" + world.getAgentX() +"," + world.getAgentY() + "");
            System.out.println( perc );
            while ( world.isAgentOk() & !world.isAgentOut() ) 
            {
                //world.readAction();
                
                String actions = agent.makeAction(perc);
                String[] tokens = actions.split(",");
                for(int j=1;j<tokens.length;j++)
                {
                    world.makeAction(tokens[j]);
                    
                    perc = world.getPerceptions();
                    System.out.println("AGENT_SCORE=" + world.agentScore);
                    System.out.println("" + world.getAgentX() +"," + world.getAgentY() + "");
                    System.out.println( perc );
                    if(wait.equals("y") || an.equals("y"))
                    {
                        try 
                        {
                            Thread.sleep(1000);
                        }
                        catch(InterruptedException e)
                        {
                            
                        }
                    }
                   
                }
                //parse string
            }
            System.out.println("EPISODE_ENDED, AGENT_SCORE=" + world.agentScore);
            System.out.println("Enter 'newgame' to play again!");
            String line = world.readLine();
            if ( !"newgame".equals(line) ) break;
        }
    }
}