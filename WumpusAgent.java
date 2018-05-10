import java.util.*;
public class WumpusAgent
{
    /*
     * DIRECTIONS
     */
    private static final int north = 1;
    private static final int east = 2;
    private static final int south = 3;
    private static final int west = 4;
   /*
    *  FLAGS
    */
    private static final int PIT_FLAG           =1;
    private static final int WUMPUS_FLAG        =2;
    private static final int SUPMUW_FLAG        =4;
    private static final int OUT_OF_BOUND_FLAG  =8;
    private static final int SAFE_FLAG          =16;
    private static final int VISITED_FLAG       =32;
    
    //Two-D-Array to contain world
    private static int world[][];
    //Agent position
    private int agentX = 0;
    private int agentY = 0;
    //Agent Direction
    private int dir = 2;
    //Know where Wumpus or Sumpuw is
    private boolean knowWumpus = false;
    private boolean safeSupmuw = false;
    //Output into WumpusWorld.java
    private String output = "";
    //LinkedList of LinkedList that holds integers that correspond with 
    //  coordinates that will point the way out
    private LinkedList<LinkedList<Integer>> pathOut = new LinkedList<LinkedList<Integer>>();
    //Whether or not to keep going back once you start going back or not
    private boolean goBack = false;
    //Boolean that says whether or not we've gone back far enough to say we go out
    private boolean climbOut = false;
    
    public WumpusAgent(int x, int y)
    {
      world = new int[x][y];
      //create new world
      //set everything to world to 0
      for(int i =0; i < x; i++)
      {
         for(int j =0; j < y; j++)
         {
            world[i][j] = 0x00000000; //set all to zero
         }
      }
      //set starting position to VISITED and SAFE
      world[0][0] |= VISITED_FLAG;
      world[0][0] |= SAFE_FLAG;
      //add starting coordinate to pathout
      LinkedList<Integer> cord = new LinkedList<Integer>();
      cord.add(agentX);
      cord.add(agentY);
      pathOut.push(cord);
    }
    
    public String makeAction(String perc) //takes in perceptions and changes flag in the world
    {
       String[] tokens = perc.split(",");
       boolean stench = (!tokens[1].equals("nothing"));
       boolean breeze = (!tokens[2].equals("nothing"));
       boolean glitter = (!tokens[3].equals("nothing"));
       boolean bump = (!tokens[4].equals("nothing"));
       boolean scream = (!tokens[5].equals("nothing"));
       boolean moo = (!tokens[6].equals("nothing"));
       output = "";
       //shouldn't bump into outer walls. Only into OUT OF BOUND
       // Need to change agent position if bump because won't know it bumped
       // till now meaning it assumed it already moved into the block
       if(bump)
       { 
           
           if(dir == north) 
           {
                world[agentX][agentY] = OUT_OF_BOUND_FLAG;
                agentY--;
           }
           if(dir == east)
           {
                world[agentX][agentY] = OUT_OF_BOUND_FLAG;
                agentX--;
           }
           if(dir == south)
           {
               world[agentX][agentY] = OUT_OF_BOUND_FLAG;
               agentY++;
           }
           if(dir == west)
           {
               world[agentX][agentY] = OUT_OF_BOUND_FLAG;
               agentX++;
           }
           pathOut.pop();
       }
       //determine where the Wumpus is using the stench. Once you find it, shoot it
       //if you don't know where it is, then simply mark all possible areas to Wumpus
       if(stench) 
       {
           if(knowWumpus)
           {} //do nothing. You know where it is. 
           else
           { //you don't know where it is so you set percautions
               if(!outOfBounds(agentX+1,agentY))
               {
                    if((world[agentX+1][agentY] & SAFE_FLAG) == 0)
                    {
                        if((world[agentX+1][agentY] & WUMPUS_FLAG) != 0)
                        {
                            knowWumpus(agentX+1,agentY);
                            return output;
                        }
                        world[agentX+1][agentY] |= WUMPUS_FLAG;
                    }
               }
               if(!outOfBounds(agentX,agentY+1))                 
               {
                   if((world[agentX][agentY+1] & SAFE_FLAG) == 0)
                   {
                       if((world[agentX][agentY+1] & WUMPUS_FLAG) != 0)
                       {
                            knowWumpus(agentX,agentY+1);
                            return output;
                        }
                       world[agentX][agentY+1] |= WUMPUS_FLAG;
                   }
               }
               if(!outOfBounds(agentX-1,agentY))
               {
                   if((world[agentX-1][agentY] & SAFE_FLAG) == 0)
                   {
                       if((world[agentX-1][agentY] & WUMPUS_FLAG) != 0)
                       {
                            knowWumpus(agentX-1,agentY);
                            return output;
                        }
                       world[agentX-1][agentY] |= WUMPUS_FLAG;
                   }
               }
               if(!outOfBounds(agentX,agentY-1)) 
               {
                   if((world[agentX][agentY-1] & SAFE_FLAG) == 0)
                   {
                       if((world[agentX][agentY-1] & WUMPUS_FLAG) != 0)
                       {
                            knowWumpus(agentX,agentY-1);
                            return output;
                        }
                       world[agentX][agentY-1] |= WUMPUS_FLAG;
                   }
                }
           }
       }
       if(breeze) //if not out of bounds and not marked as safe set world to pit
       {
           if(!outOfBounds(agentX+1,agentY))
           {
               if((world[agentX+1][agentY] & SAFE_FLAG) == 0)
                    world[agentX+1][agentY] |= PIT_FLAG;
           }
           if(!outOfBounds(agentX,agentY+1))
           {
               if((world[agentX][agentY+1] & SAFE_FLAG) == 0)
                    world[agentX][agentY+1] |= PIT_FLAG;
           }
           if(!outOfBounds(agentX-1,agentY))
           {
               if((world[agentX-1][agentY] & SAFE_FLAG) == 0)
                    world[agentX-1][agentY] |= PIT_FLAG;
           }
           if(!outOfBounds(agentX,agentY-1))
           {
               if((world[agentX][agentY-1] & SAFE_FLAG) == 0)
                     world[agentX][agentY-1] |= PIT_FLAG;
           }
       }
       if(glitter) //if glitter, grab and getout
       {
           output += ",grab";
           getOut();
           return output;
       }
       
       //if moo but Supmuw is safe then set to SUPMUW_FLAG which assumes not safe
       if(moo && !safeSupmuw)
       {
           if(!outOfBounds(agentX+1,agentY))
           {
                if((world[agentX+1][agentY] & SAFE_FLAG) == 0)
                    world[agentX+1][agentY] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX,agentY+1)) 
           {
                if((world[agentX][agentY+1] & SAFE_FLAG) == 0)
                    world[agentX][agentY+1] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX-1,agentY))
           {
                if((world[agentX-1][agentY] & SAFE_FLAG) == 0)
                    world[agentX-1][agentY] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX,agentY-1))
           {
                if((world[agentX][agentY-1] & SAFE_FLAG) == 0)
                    world[agentX][agentY-1] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX+1,agentY+1))
           {
                if((world[agentX+1][agentY+1] & SAFE_FLAG) == 0)
                    world[agentX+1][agentY+1] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX-1,agentY+1))
           {
                if((world[agentX-1][agentY+1] & SAFE_FLAG) == 0)
                    world[agentX-1][agentY+1] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX-1,agentY-1))
           {
               if((world[agentX-1][agentY-1] & SAFE_FLAG) == 0)
                    world[agentX-1][agentY-1] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX+1,agentY-1))
           {
                if((world[agentX+1][agentY-1] & SAFE_FLAG) == 0)
                    world[agentX+1][agentY-1] |= SUPMUW_FLAG;
           }
       }
       //if moo and safeSupmuw, then set all surrounding blocks to possible SUPMUW
       if(moo && safeSupmuw)
       {
           if(!outOfBounds(agentX+1,agentY))
           {
                    world[agentX+1][agentY] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX,agentY+1)) 
           {
                world[agentX][agentY+1] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX-1,agentY))
           {
               world[agentX-1][agentY] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX,agentY-1))
           {
                    world[agentX][agentY-1] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX+1,agentY+1))
           {
                    world[agentX+1][agentY+1] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX-1,agentY+1))
           {
                    world[agentX-1][agentY+1] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX-1,agentY-1))
           {
                    world[agentX-1][agentY-1] |= SUPMUW_FLAG;
           }
           if(!outOfBounds(agentX+1,agentY-1))
           {
                    world[agentX+1][agentY-1] |= SUPMUW_FLAG;
           }
       }
       //if SUPMUW isn't safe yet, then don't set there positions to safe
       if(!moo && !breeze && !stench && !bump && !safeSupmuw)
       {
           if(!outOfBounds(agentX+1,agentY))
           {
               world[agentX+1][agentY] |= SAFE_FLAG;
               world[agentX+1][agentY] &= (0xFFFFFFFF ^ PIT_FLAG);
               world[agentX+1][agentY] &= (0xFFFFFFFF ^ WUMPUS_FLAG);
               world[agentX+1][agentY] &= (0xFFFFFFFF ^ SUPMUW_FLAG);
           }
           if(!outOfBounds(agentX,agentY+1))
           {
               world[agentX][agentY+1] |= SAFE_FLAG;
               world[agentX][agentY+1] &= (0xFFFFFFFF ^ PIT_FLAG);
               world[agentX][agentY+1] &= (0xFFFFFFFF ^ WUMPUS_FLAG);
               world[agentX][agentY+1] &= (0xFFFFFFFF ^ SUPMUW_FLAG);
           }
           if(!outOfBounds(agentX-1,agentY))
           {
               world[agentX-1][agentY] |= SAFE_FLAG;
               world[agentX-1][agentY] &= (0xFFFFFFFF ^ PIT_FLAG);
               world[agentX-1][agentY] &= (0xFFFFFFFF ^ WUMPUS_FLAG);
               world[agentX-1][agentY] &= (0xFFFFFFFF ^ SUPMUW_FLAG);
           }
           if(!outOfBounds(agentX,agentY-1))
           {
               world[agentX][agentY-1] |= SAFE_FLAG;
               world[agentX][agentY-1] &= (0xFFFFFFFF ^ PIT_FLAG);
               world[agentX][agentY-1] &= (0xFFFFFFFF ^ WUMPUS_FLAG);
               world[agentX][agentY-1] &= (0xFFFFFFFF ^ SUPMUW_FLAG);
           }
        }
        //if SUPMUW is safe, then don't worry about moos, only PITs
       if(!breeze && !bump && safeSupmuw)
       {
           if(!outOfBounds(agentX+1,agentY))
           {
               world[agentX+1][agentY] |= SAFE_FLAG;
               world[agentX+1][agentY] &= (0xFFFFFFFF ^ PIT_FLAG);
           }
           if(!outOfBounds(agentX,agentY+1))
           {
               world[agentX][agentY+1] |= SAFE_FLAG;
               world[agentX][agentY+1] &= (0xFFFFFFFF ^ PIT_FLAG);
           }
           if(!outOfBounds(agentX-1,agentY))
           {
               world[agentX-1][agentY] |= SAFE_FLAG;
               world[agentX-1][agentY] &= (0xFFFFFFFF ^ PIT_FLAG);
           }
           if(!outOfBounds(agentX,agentY-1))
           {
               world[agentX][agentY-1] |= SAFE_FLAG;
               world[agentX][agentY-1] &= (0xFFFFFFFF ^ PIT_FLAG);
           }
        }
       return makeAction(); 
    }
     
    //function that actually makes decision
    public String makeAction()
    {
        
        LinkedList<Integer> cord = new LinkedList<Integer>();
        /*if one of the areas is out of bounds then don't go there
         * if its not out of bounds and is safe and not visited then move there
         */
        if(!outOfBounds(agentX+1, agentY))
        {
           if((world[agentX+1][agentY] & SAFE_FLAG) != 0 && (world[agentX+1][agentY] & VISITED_FLAG) == 0 && (world[agentX+1][agentY] & OUT_OF_BOUND_FLAG) == 0)
           {
               
                cord.add(agentX);
                cord.add(agentY);
                  
                pathOut.push(cord);
               makeTurns(east);
               agentX++;
                world[agentX][agentY] |= VISITED_FLAG;
                output += ",walk";
                return output;
           }
        }
        if(!outOfBounds(agentX, agentY+1))
        {
           if((world[agentX][agentY+1] & SAFE_FLAG) != 0 && (world[agentX][agentY+1] & VISITED_FLAG) == 0 && (world[agentX][agentY+1] & OUT_OF_BOUND_FLAG) == 0)
           {
               cord.add(agentX);
                cord.add(agentY);
                  
                pathOut.push(cord);
               makeTurns(north);
               agentY++;
                world[agentX][agentY] |= VISITED_FLAG;
                output += ",walk";
                
                return output;
           }
        }
        if(!outOfBounds(agentX, agentY-1) )
        {
           if((world[agentX][agentY-1] & SAFE_FLAG) != 0 && (world[agentX][agentY-1] & VISITED_FLAG) == 0 && (world[agentX][agentY-1] & OUT_OF_BOUND_FLAG) == 0)
           {
               
                cord.add(agentX);
                cord.add(agentY);
                  
                pathOut.push(cord);
               makeTurns(south);
               agentY--;
                world[agentX][agentY] |= VISITED_FLAG;
                output += ",walk";
                
                
                return output;
           }
        }
        if(!outOfBounds(agentX-1, agentY) )
        {
           if((world[agentX-1][agentY] & SAFE_FLAG) != 0 && (world[agentX-1][agentY] & VISITED_FLAG) == 0 && (world[agentX-1][agentY] & OUT_OF_BOUND_FLAG) == 0)
           {
               
                cord.add(agentX);
                cord.add(agentY);
                  
                pathOut.push(cord);
               makeTurns(west);
               agentX--;
                world[agentX][agentY] |= VISITED_FLAG;
                output += ",walk";
                
                return output;
           }
        }
        //if all directions are either not safe or already visited then go back
        goBack();
        goBack = true; //variable allows where not going back between two blocks
        if(climbOut)
        {
            return output;
        }
        output += ",walk";
        
        return output;
    }
    
    public void goBack()
    {   
            //if agent ends up going back to 0,0 at any point, it climbs out
            if(agentX == 0 && agentY == 0 && ((world[agentX+1][agentY] & VISITED_FLAG) != 0) && ((world[agentX][agentY+1] & VISITED_FLAG) != 0))
            {
                output += ",climb";
                climbOut = true;
            }
            //if not at 0,0
            /*
            else
            {
                if(dir == east)
                {
                    if(!outOfBounds(agentX,agentY+1) && (world[agentX][agentY+1] & VISITED_FLAG) != 0)
                    {
                        makeTurns(north);
                        agentY++;
                    }
                    else if(!outOfBounds(agentX,agentY-1) && (world[agentX][agentY-1] & VISITED_FLAG) != 0)
                    {
                        makeTurns(south);
                        agentY--;
                    }
                    else if(!outOfBounds(agentX+1,agentY) && (world[agentX+1][agentY] & VISITED_FLAG) != 0)
                    {
                        agentX++;
                    }
                }
                else if(dir == west)
                {
                    if(!outOfBounds(agentX,agentY+1) && (world[agentX][agentY+1] & VISITED_FLAG) != 0)
                    {
                        makeTurns(north);
                        agentY++;
                    }
                    else if(!outOfBounds(agentX,agentY-1) && (world[agentX][agentY-1] & VISITED_FLAG) != 0)
                    {
                        makeTurns(south);
                        agentY--;
                    }
                    else if(!outOfBounds(agentX-1,agentY) && (world[agentX-1][agentY] & VISITED_FLAG) != 0)
                    {
                        agentX--;
                    }
                }
                else  if(dir == north)
                {
                    if(!outOfBounds(agentX,agentY+1) && (world[agentX][agentY+1] & VISITED_FLAG) != 0)
                    {
                        agentY++;
                    }
                    else if(!outOfBounds(agentX-1,agentY) && (world[agentX-1][agentY] & VISITED_FLAG) != 0)
                    {
                        makeTurns(west);
                        agentX--;
                    }
                    else if(!outOfBounds(agentX+1,agentY) && (world[agentX+1][agentY] & VISITED_FLAG) != 0)
                    {
                        makeTurns(east);
                        agentX++;
                    }
                }
                else  if(dir == south)
                {
                    if(!outOfBounds(agentX,agentY-1) && (world[agentX][agentY-1] & VISITED_FLAG) != 0)
                    {
                        agentY--;
                    }
                    else if(!outOfBounds(agentX-1,agentY) && (world[agentX-1][agentY] & VISITED_FLAG) != 0)
                    {
                        makeTurns(west);
                        agentX--;
                    }
                    else if(!outOfBounds(agentX+1,agentY) && (world[agentX+1][agentY] & VISITED_FLAG) != 0)
                    {
                        makeTurns(east);
                        agentX++;
                    }
                }
            }
            */
           LinkedList<Integer> coordinates = pathOut.pop();
           int x = coordinates.pop();
           int y = coordinates.pop();
           if(agentX-1 == x) 
            {
                makeTurns(west);
                agentX--;
            }
            else if(agentX+1 == x)
            {
                 makeTurns(east);
                 agentX++;
            }
            else if(agentY+1 == y)
            {
                 makeTurns(north);
                 agentY++;
            }
            else if(agentY-1 == y)
            {
                 makeTurns(south);
                 agentY--;
            }
           
           
        
    }
    
    public void makeTurns(int newDir) //make turns based on new Direction and current Direction
    {
        if(dir == newDir)
            return;
        if(dir == east)
        {
            if(newDir == west)
                output += ",left,left";
            else if(newDir == north)
                output += ",left";
            else if(newDir == south)
                output += ",right";
        }
        if(dir == west)
        {
            if(newDir == east)
                output += ",left,left";
            else if(newDir == north)
                output += ",right";
            else if(newDir == south)            
                output += ",left";
        }
        if(dir == north)
        {
            if(newDir == west)
               output += ",left";
            else if(newDir == east)
               output += ",right";
            else if(newDir == south)
                output += ",left,left";
        }
        if(dir == south)
        {
            if(newDir == west)
                output += ",right";
            else if(newDir == north)
                output += ",left,left";
            else if(newDir == east)
                output += ",left";
        }
        dir = newDir; //set direction to current direction
    }
    
    public boolean outOfBounds(int x,int y) //check if outside bounds of cave
    {
        if(x < 0 || x > 9 || y < 0 || y >9) return true;
        return false;
    }
    //what happens where it knows WUMPUS location. It kills it and sets SUPMUWS to safe
    
    public void knowWumpus(int x,int y) 
    {
        for(int i = 0; i<10; i++)
        {
            for(int j = 0; j<10; j++)
            {
                world[i][j] &= (0xFFFFFFFFL ^ WUMPUS_FLAG);
            }
        }
        
        //finds direction to wumpus and shoots 
        if(agentX-1 == x) 
        {
            makeTurns(west);
            output += ",shoot";
        }
        else if(agentX+1 == x)
        {
             makeTurns(east);
             output += ",shoot";
        }
        else if(agentY+1 == y)
        {
             makeTurns(north);
             output += ",shoot";
        }
        else if(agentY-1 == y)
        {
             makeTurns(south);
             output += ",shoot";
        }
        knowWumpus = true;
        safeSupmuw = true;
        safeSupmuw();
    }
    
    public void safeSupmuw(){//once the WUMPUS is dead, set SUPMUWS to safe if not PIT
        for(int i = 0; i<10; i++)
        {
            for(int j = 0; j<10; j++)
            {
                if( world[i][j] != 0x00000000) //at block 
                {
                    if((world[i][j] & PIT_FLAG) == 0 && (world[i][j] & SUPMUW_FLAG) != 0)      
                    {
                        world[i][j] |= SAFE_FLAG; //set to safe;
                    }
                }
            }
        }
    }
    
    public void getOut()//uses pathOut to find pathOut. Used when found GOLD
    {
        
        LinkedList<Integer> cordsUp = new LinkedList<Integer>();
        LinkedList<Integer> cordsDown = new LinkedList<Integer>();
        LinkedList<Integer> cordsLeft = new LinkedList<Integer>();
        LinkedList<Integer> cordsRight = new LinkedList<Integer>();
        while((agentX != 0) || (agentY != 0))
        {
           cordsUp.add(agentX);
           cordsUp.add(agentY+1);
           
           cordsDown.add(agentX);
           cordsDown.add(agentY-1);
           
           cordsLeft.add(agentX-1);
           cordsLeft.add(agentY);
           
           cordsRight.add(agentX+1);
           cordsRight.add(agentY);
           /*
            * Checks for all directions from the current position in the Stack
            * If it finds One of those directions in the stack then the AI goes to it
            * and pops off everythig on the stack till at new location. 
            */
           
           
           if(pathOut.contains(cordsLeft)) 
           {
               makeTurns(west);
               output += ",walk";
               agentX--;
               while(!pathOut.peek().equals(cordsLeft))
               {
                   pathOut.pop();
               }
           }
           else if(pathOut.contains(cordsDown))
           {
               makeTurns(south);
               output += ",walk";
               agentY--;
               while(!pathOut.peek().equals(cordsDown))
               {
                   pathOut.pop();
               }
           }
           else if(pathOut.contains(cordsUp))
           {
               makeTurns(north);
               output += ",walk";
               agentY++;
               while(!pathOut.peek().equals(cordsUp))
               {
                   pathOut.pop();
               }
           }
           else if(pathOut.contains(cordsRight))
           {
               makeTurns(east);
               output += ",walk";
               agentX++;
               while(!pathOut.peek().equals(cordsRight))
               {
                   pathOut.pop();
               }
           }
           cordsUp.clear();
           
           cordsDown.clear();
           
           cordsLeft.clear();
           
           cordsRight.clear();
        }
        //pop 0,0 off stack
        pathOut.pop();
        //call climb
        output += ",climb";
    }
}