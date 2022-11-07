import java.io.*;
import java.util.Scanner;
import common.*;

/**
 * This class constructs the UI for a Echo server.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ClientConsole. 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version September 2020
 */
public class ServerConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the server that created this Console.
   */
  EchoServer server;
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ServerConsole UI.
   *
   * @param port The port to connect on.
   */
  public ServerConsole(int port) 
  {
    try 
    {
      server= new EchoServer(port, this);
      
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    try
    {
      server.listen(); //Start listening for connections
    }
    catch (Exception ex)
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the server's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        server.handleMessageFromServerUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Server UI.
   *
   * @param args[0] The port to connect to.
   */
  public static void main(String[] args) 
  {
    int port = 0;

    try
    {
      port = Integer.parseInt(args[0]);
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      port = DEFAULT_PORT;
    }
    catch(NumberFormatException ne) {
    	port = DEFAULT_PORT;
    }
    
    ServerConsole server = new ServerConsole(port);
    server.accept();  //Wait for console data
  }
}
//End of ServerConsole class
