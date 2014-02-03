
public interface IDatabaseSystem {

	/** Return the total number of messages in the mail drop **/
	public int getNumberOfMessagesInMailDrop();
	
	/** Return the total number of deleted messages in the mail drop **/
	public int getNumberOfDeletedMessages();
	
	/** Return the total size of all of the messages in the mail drop **/
	public int getSizeOfMailDrop();
	
	/** Returns false if the user argument does not match the user information stored. **/
	public boolean authorizedUser(String user);

	/** Returns true if the password argument matches the stored password **/
	public boolean authorizedPassword(String pass);
	
	/** Returns the size of a message as an integer **/
	public int getSizeOfMessage(int messageNumber);
	
	/** Return a string that contains the contents of a message **/
	public String getMessageContent(int messageNumber);
	
	/** This method will remove all the messages that have been stored in the deletion list **/
	public void removeMarkedMessages();
	
	/** This method will return the heading of a message and a set amount of lines from the body of the message, using
	 * the parameter 'numberOfLines'. If the numberOfLines entered is higher than the lines in the body of the text, then
	 * the whole message will be returned as a string.
	 */
	public String getPartOfMessage(int messageNumber, int numberOfLines);
	
	/** Adds a message number to a deletion list **/
	public void addToDeletionList(int messageNumber);

	/** This method will accept a messageNumber and return false if that message is in the deletion list **/
	public boolean isMessageMarked(int messageNumber);

	/** Remove all message numbers from the deletion list **/
	public void unmarkAllMessages();

	/** Returns the uniqueID of a message as a string**/
	public String getMessageUniqueID(int messageNo);
	
	/** Return a list of all messages (that are not in the deletion list) in the mail drop and their uniqueIDs as a string**/
	public String getUniqueIDList();
	
	/** Return a list of all messages (that are not in the deletion list) in the mail drop and their size, in octets, as 
	 * a String**/
	public String getScanListing();
	
}
