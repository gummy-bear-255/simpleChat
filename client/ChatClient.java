// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  /**
   * The client ID variable.
   */
  String loginID;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param loginID The client to connect.
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if(message.startsWith("#")) {
    		handleCommand(message);
    	}
    	else {
    		sendToServer(message);
    	}
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method handles all commands (data starting with #) coming from the UI            
   *
   * @param cmd The command from the UI.    
   */
  private void handleCommand(String cmd) {
	  if(cmd.equals("#quit")) {
		  clientUI.display("The client will quit.");
		  quit();
	  }
	  else if(cmd.equals("#logoff")) {
		  clientUI.display("The client will disconnect.");
		  try {
			  if(this.isConnected()) {
				  this.closeConnection();
			  }
			  else {
				  clientUI.display("Client already disconnected.");
			  }
		} catch (IOException e) {
			clientUI.display("Client was never connected.");
		}
	  }
	  else if(cmd.startsWith("#sethost")) {
		  if(this.isConnected()) {
			  clientUI.display("Client needs to disconnect before setting host.");
		  }
		  else {
			  try {
				  setHost(cmd.substring(9));
				  clientUI.display("Host changed to " + getHost());
			  }
			  catch(Exception e) {
				  clientUI.display("Unexpected error while setting client host.");
			  }
		  }
	  }
	  else if(cmd.startsWith("#setport")) {
		  if(this.isConnected()) {
			  clientUI.display("Client needs to disconnect before setting port.");
		  }
		  else {
			  try {
				  setPort(Integer.parseInt(cmd.substring(9)));
				  clientUI.display("Port changed to " + getPort());
			  }
			  catch(Exception e) {
				  clientUI.display("Unexpected error while setting client port.");
			  }
		  }
	  }
	  else if(cmd.equals("#login")) {
		  if(this.isConnected()) {
			  clientUI.display("Client is already logged in.");
		  }
		  else {
			  try {
				openConnection();
			} catch (IOException e) {
				clientUI.display("Connection could not be established.");
			}
		  }
	  }
	  else if(cmd.equals("#gethost")) {
		  clientUI.display("Host : " + getHost());
	  }
	  else if(cmd.equals("#getport")) {
		  clientUI.display("Port : " + getPort());
	  }
	  else {
		  clientUI.display("Not a valid command!");
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  /**
	 * Implementation of the hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overridden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	@Override
	protected void connectionClosed() {
  		clientUI.display("The connection has been closed.");
	}

	/**
	 * Implementation of the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  	@Override
	protected void connectionException(Exception exception) {
  		clientUI.display("The server has shut down.");
  		System.exit(0);
	}
  	
  	/**
	 * Implementation of the hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
  	@Override
	protected void connectionEstablished() {
  		try {
			sendToServer("#login " + loginID);
		} catch (IOException e) {
			clientUI.display("Connection could not be established.");
		}
	}
}
//End of ChatClient class
