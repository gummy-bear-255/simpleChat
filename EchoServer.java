// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import common.*;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
	
  /**
   * The key to set loginID info.
   */
  final private String key = "loginKey";
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the server.
   */
  ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   * @param serverUI The interface type variable.
   */
  public EchoServer(int port, ChatIF serverUI) throws IOException
  {
    super(port);
    this.serverUI = serverUI;
  }

  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	serverUI.display("Message received: " + msg + " from " + client.getInfo(key));
	String msgStr = (String)msg;
    if(msgStr.startsWith("#login")) {
    	
    	String loginID = msgStr.substring(7);
    	client.setInfo(key, loginID);
    	serverUI.display(client.getInfo(key) +" has logged on.");
    	this.sendToAllClients(client.getInfo(key) + " has logged on.");
    }
    else{
    	this.sendToAllClients(client.getInfo(key) + "> " + msg);
    }
    
  }
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServerUI(String message)
  {
	  if(message.startsWith("#")) {
    	handleCommand(message);
	  }
	  else {
		  serverUI.display("SERVER MSG> " + message);
		  this.sendToAllClients("SERVER MSG> " + message);
	  }
  }
  
  /**
   * This method handles all commands (data starting with #) coming from the UI            
   *
   * @param cmd The command from the UI.    
   */
  public void handleCommand(String cmd) {
	  if(cmd.equals("#quit")) {
		  serverUI.display("The server will quit.");
		  quit();
	  }
	  else if(cmd.equals("#stop")) {
		  stopListening();
	  }
	  else if(cmd.startsWith("#close")) {
		  try {
			  close();
		  }
		  catch(IOException e){
			  serverUI.display("Could not close server.");
		  }
	  }
	  else if(cmd.startsWith("#setport")) {
		  if(this.getNumberOfClients() == 0 && !this.isListening()) {
			  try {
				  setPort(Integer.parseInt(cmd.substring(9)));
				  serverUI.display("Port changed to " + getPort());
			  }
			  catch(Exception e) {
				  serverUI.display("Unexpected error while setting server port.");
			  }
		  }
		  else {
			  serverUI.display("Server is still open.");
			  
		  }
	  }
	  else if(cmd.equals("#start")) {
		  if(!this.isListening()) {
			  try {
					listen();
				} catch (Exception e) {
					serverUI.display("Could not listen for clients.");
				}
		  }
		  else {
			  serverUI.display("Server is already started.");
		  }
	  }
	  else if(cmd.equals("#getport")) {
		  serverUI.display("Port : " + getPort());
	  }
	  else {
		  serverUI.display("Not a valid command!");
	  }
  }
  /**
   * This method terminates the server.
   */
  public void quit()
  {
    try
    {
      close();
    }
    catch(IOException e) {}
    System.exit(0);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	  serverUI.display
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
	  serverUI.display
      ("Server has stopped listening for connections.");
  }
  
  /**
   * Implementation of the hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  serverUI.display("A new client has connected to the server.");
  }
  
  /**
   * Implementation of the hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  serverUI.display(client.getInfo(key) + " has disconnected.");
  }
  
  /**
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(
    ConnectionToClient client, Throwable exception) {
	  serverUI.display(client.getInfo(key) + " has disconnected.");
  }
}
//End of EchoServer class
