package MySQL.Demoo;

import java.util.Scanner;

public class App 
{
    public static void main(String[] args) 
    {
    	 Scanner input = new Scanner(System.in);
    	 
    	 while(true)
    	 {
    		 String currCommand = input.nextLine();
    		 
    		 Commands[] c = Commands.values();
    		 
    		 currCommand = currCommand.toLowerCase();
    		 
    		 for(Commands cc: c)
    		 {
    			 if(cc.getName().equals(currCommand))
    			 {
    				 cc.executeCommand(null);
    			 }
    		 }
    	 }
    }
}
