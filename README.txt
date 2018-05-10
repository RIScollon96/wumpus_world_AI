Wumplus Agent

Made by Robert Scollon and Nicholas Horsman

WumpusWorld.java starting code developed by:
https://github.com/Abel7x/wumpus
FileIO.class & FileIOException.class are from Dr. Boshart's Object Oriented Programming class 

Assumptions:
	Once the supmuw gives the agent food, it leaves the caverns
	
	When the wumpus dies, it no longer emits a scent
	
	If a supmuw is both in a pit, and beside the wumpus, the supmuw ignores the wumpus,
	and keeps the agent out of the pit, without harming it.
		
	The agent prioritizes its own safety over guessing
	
	User's terminal is set to 80 x 25 (81 x 25 for Windows)
	
	The agent starts at 0,0
	
	Although the world outputs the agent's position in the cave, the agent can not know it;
	All the agent knows from the cave are its perceptions
	
	There is only ever one wumpus and one supmuw
	
	Every action costs 1 point even if it doesn't do anything i.e. shooting with no arrow,
	grabbing with no gold
	
	Using the arrow costs 10 points, and dying costs 1000
	
	The agent does not actively seek out the supmuw, it only avoids it until it knows it is safe
	
To compile:
	Our files are written in java, so in order to compile simply type:
	
	javac WumpusWorld.java
	
	The associated class files will be created automatically
	
To run: 
	Type:
	
	java WumpusWorld <filename> <animations> <delay>
	
	Where:
		filename is a txt file describing the world, formatted like so:
			1,0,pit
			6,8,wumpus
			9,9,supmuw
			x,y,item
			etc
			
			Acceptable items are "pit", "wumpus", "gold", "supmuw", "bound"
			An example file is supplied in "TheWorld.txt"
			
		animations is a single character 'y' or 'n' *
			If 'y', then the world provides animations as well as perceptions for each action the agent performs
			If 'n', then the world simply provides perceptions
			
		delay is also a single character 'y' or 'n' **
			If 'y', then the agent waits one second before performing each action
			If 'n', then the agent performs all actions without waiting
			
	Usage Notes: 
	*For best animation experience, it is recommended to set terminal size to 80 x 25 (81 x 25 if using the windows terminal)
	**In order to retain consistency, when animations are on the program always uses the delay. This overwrites the user's choice, so
	only set delay to n if not viewing animations