
public class DatabaseSystem implements IDatabaseSystem {

	private final String USERNAME = "test", PASSWORD = "password", OK = "+OK ";

	public int getNumberOfMessagesInMailDrop() {
		return 2;
	}

	public int getNumberOfDeletedMessages() {
		return 0;
	}

	public int getSizeOfMailDrop() {
		return 0;

	}

	public boolean authorizedUser(String user) {

		if (user.equals(USERNAME)) {

			return true;

		}
		return false;
	}

	public int getSizeOfMessage(int messageNumber) {
		return 0;
	}

	public String getMessageContent(int messageNumber) {
		return "<the POP3 server sends the entire message here>";
	}

	public void removeMarkedMessages() {

	}

	public String getPartOfMessage(int messageNumber, int numberOfLines) {
		return "<the POP3 server sends the headers of the message, a blank line, and the first " + numberOfLines + " lines of the body of the message>";
	}

	public boolean authorizedPassword(String pass) {

		if (pass.equals(PASSWORD)) {

			return true;

		}
		return false;
	}

	public void addToDeletionList(int messageNumber) {
	}

	public boolean isMessageMarked(int messageNumber) {
		return false;
	}

	public void unmarkAllMessages() {
	}

	public String getMessageUniqueID(int messageNo) {
		return null;
	}

	public String getUniqueIDList() {

		// get the total number of messages
		int numberOfMessages = getNumberOfMessagesInMailDrop();

		StringBuilder message = new StringBuilder();

		message.append(OK);

		if (numberOfMessages > 0) {

			for (int messageNo = 1; messageNo < numberOfMessages + 1; messageNo++) {
				// loop through each of the messages in the mail drop, and skip every message that has been marked as deleted
				if (isMessageMarked(messageNo) == false) {
					// get the unique id for every message
					message.append("\n" + messageNo + " " + getMessageUniqueID(messageNo));
				}

			}
			// add the '.' at the end to show that the program has completed its output.
			message.append("\n.");

		}
		// convert the string builder type to a string
		return message.toString();
	}

	public String getScanListing() {
		// uses the same logic as getUniqueIDList.

		int numberOfMessages = getNumberOfMessagesInMailDrop();

		StringBuilder listString = new StringBuilder();

		listString.append(OK + numberOfMessages + " messages (" + getSizeOfMailDrop() + " octets)");

		if (numberOfMessages > 0) {

			for (int messageNo = 1; messageNo < numberOfMessages + 1; messageNo++) {

				if (isMessageMarked(messageNo) == false) {
					listString.append("\n" + messageNo + " " + getSizeOfMessage(messageNo));
				}
			}

			listString.append("\n.");

		}

		return listString.toString();

	}

}
