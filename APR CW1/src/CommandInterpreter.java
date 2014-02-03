public class CommandInterpreter implements ICommandInterpreter {
	
	/** The interpreter chooses a response based on the type of command and the current status of the program. I have used the 
	 * String handleInput(String input) method to produce these commands, along with some other private helper methods. I have created
	 * an instance of the mock database class so that every time a new instance of this class is created, a database instance will have
	 *  also been created, this is because the database class is the main class and without it, some of the POP3 commands will be useless.
	 */
	
	// handled most of my error checks in this class to make it easier to implement the database class.

	// final strings for the pop3 commands
	private final String USER = "USER", QUIT = "QUIT", STAT = "STAT", LIST = "LIST", RETR = "RETR", DELE = "DELE";
	private final String NOOP = "NOOP", RSET = "RSET", TOP = "TOP", UIDL = "UIDL", PASS = "PASS";
	// final strings for the positive and negative responses
	private final String ERROR = "-ERR ", OK = "+OK ";
	// final strings for states of program
	private final String AUTHORIZATION_STATE = "AUTHORIZATION";
	private final String TRANSACTION_STATE = "TRANSACTION";
	private final String UPDATE_STATE = "UPDATE";

	// final string to hold the current state of the program
	private String interpreterState;
	// boolean used after user is confirmed, so that you cannot enter the pass command before confirming user
	private boolean userConfirmed;
	DatabaseSystem dbs;

	public CommandInterpreter() {

		userConfirmed = false;

		interpreterState = AUTHORIZATION_STATE;
		// creating an instance of my mock database class
		dbs = new DatabaseSystem();

		// this is in the constructor so that it displays as soon as a new instance of this class is made
		System.out.println(OK +"POP3 server ready.");
	}

	public String handleInput(String input) {

		try {

			switch(interpreterState) {

			case AUTHORIZATION_STATE:

				return authorizationInput(input);

			case TRANSACTION_STATE:

				return transactionInput(input);

				// since commands cannot be entered in an UPDATE state, i have not included an update case here
				// but i have included an error response just in case
				
			default:
				return ERROR;
			}

		} catch (Exception e) {

			// this will apply if there is an error during runtime that i have not already planned for. 
			// this includes entering a string as an argument when the system is expecting an int e.g.LIST h
			// an error will be thrown at this "messageNumber = Integer.parseInt(stringInput[1]);"

			return ERROR + "There has been an error processing the command." + input;

		}
	}

	private int spacesInInput(String input) {

		// calculate amount of spaces in a string

		return (input.length() - input.replaceAll(" ", "").length());

	}

	private boolean isValidSpacing(String input, int inputLength) {

		// a command should have 1 less space than the combined parts of the command, based on the specification, 
		// so i have used this as a basis for this method

		if (spacesInInput(input) == inputLength - 1) {

			return true;

		} else {

			return false;

		}
	}

	private int getNumberOfUnmarkedMessages() {

		return (dbs.getNumberOfMessagesInMailDrop() - dbs.getNumberOfDeletedMessages());

	}

	private boolean doesMessageExist(int messageNumber, int numberOfMessages) {

		// based on my planned implementation of the database class, the following conditions are used to determine
		// whether or not a message can be manipulated

		if (messageNumber > numberOfMessages || messageNumber < 1 || dbs.isMessageMarked(messageNumber) == true) {
			return false;
		} else {
			return true;
		}
	}

	private String authoriseUser(String input){

		// take everything after the first 5 characters in a string and check if this matches the 
		// database user e.g. from (USER test), "test" will be taken

		if (dbs.authorizedUser(input.substring(5))) {

			// confirm that the user has logged on and wait for the password
			userConfirmed = true;

			return OK + "Welcome " + input.substring(5) + ", please enter your password." + input;

		} else {

			return ERROR + "The user " + input + " does not exist." + input;

		}
	}

	private String authorisePassword(String input){

		// this uses the same logic as the authoriseUser method

		if (dbs.authorizedPassword(input.substring(5))) {

			interpreterState = TRANSACTION_STATE;

			return OK + "Maildrop locked and ready." + input;

		} else {

			userConfirmed = false;

			return ERROR + "Invalid password." + input;

		}
	}

	private String transactionInput(String input) {
		// the following cases of commands should only work in the transaction state

		// split the string into sections without spaces

		String[] stringInput = input.split(" ");

		//get the total amount of messages currently in the mail drop
		int numberOfMessages = dbs.getNumberOfMessagesInMailDrop();

		int messageNumber;

		// allows commands to be entered upper or lower case
		// stringInput is the array that holds the sections of the string
		switch(stringInput[0].toUpperCase()) {

		case STAT:

			// if STAT is the only non space string and there are 0 spaces then this will be true
			if (stringInput.length == 1 && isValidSpacing(input, stringInput.length)) {

				return OK + getNumberOfUnmarkedMessages() + " " + dbs.getSizeOfMailDrop();

			} else {

				return ERROR + "Incorrect number of arguments." + input;

			}

		case LIST:

			// LIST can have an optional argument e.g. LIST 1 so there will be two no spaces strings (LIST and 1)
			// and only 1 space between them should be allowed
			if (stringInput.length == 2 && isValidSpacing(input, stringInput.length)) {

				// convert the string argument to an integer
				messageNumber = Integer.parseInt(stringInput[1]);

				// check if message is not deleted and validate the argument passed to LIST
				if (doesMessageExist(messageNumber, numberOfMessages)) {

					return OK + messageNumber + " " + dbs.getSizeOfMessage(messageNumber);

				} else {

					return ERROR + "No such message. " + numberOfMessages + " messages in maildrop." + input;

				}

				// if only "LIST" is input as a command	
			} else if (stringInput.length == 1 && isValidSpacing(input, stringInput.length)){

				return dbs.getScanListing();

			} else {

				return ERROR + "Invalid command entered." + input;
			}

		case RETR:

			if (stringInput.length == 2 && isValidSpacing(input, stringInput.length)) {

				messageNumber = Integer.parseInt(stringInput[1]);

				if (doesMessageExist(messageNumber, numberOfMessages)) {

					return OK + dbs.getSizeOfMessage(messageNumber) + " octets\n" + 
							dbs.getMessageContent(messageNumber) + "\n.";

				} else {

					return ERROR + "No such message. " + numberOfMessages + " messages in maildrop." + input;

				}

			} else {

				return ERROR + "Incorrect number of arguments." + input;

			}

		case DELE:

			if (stringInput.length == 2 && isValidSpacing(input, stringInput.length)) {

				messageNumber = Integer.parseInt(stringInput[1]);

				// validate the message number string argument
				if (messageNumber > numberOfMessages || messageNumber < 1) {

					return ERROR + "No such message." + input;

					// check if the message has already been marked as deleted
				} else if (dbs.isMessageMarked(messageNumber) == false) {

					// add the message to the deletion list
					dbs.addToDeletionList(messageNumber);

					return OK + "Message " + messageNumber + " deleted." + input;

				} else {

					return ERROR + "Message " + messageNumber + " already deleted." + input;

				}

			} else {

				return ERROR + "Incorrect number of arguments." + input;

			}

		case NOOP:

			if (stringInput.length == 1 && isValidSpacing(input, stringInput.length)) {

				return OK + input;

			} else {

				return ERROR + "Too many arguments." + input;

			}

		case RSET:

			if (stringInput.length == 1 && isValidSpacing(input, stringInput.length)) {

				// this will remove all message numbers in the deletion list
				dbs.unmarkAllMessages();

				return OK + "Maildrop has " + getNumberOfUnmarkedMessages() + " messages (" + dbs.getSizeOfMailDrop() + " octets)." + input;

			} else {

				return ERROR + "Incorrect number of arguments." + input;

			}

		case TOP:

			// top can have 2 arguments e.g. "TOP 100 1"
			if (stringInput.length == 3 && isValidSpacing(input, stringInput.length)) {

				messageNumber = Integer.parseInt(stringInput[1]);

				int numberOfLines = Integer.parseInt(stringInput[2]);

				if (doesMessageExist(messageNumber, numberOfMessages)) {

					return OK + "\n" + dbs.getPartOfMessage(messageNumber, numberOfLines) + "\n." + input;

				} else {

					return ERROR + "No such message. " + numberOfMessages + " messages in maildrop." + input;

				}

			} else {

				return ERROR + "Incorrect number of arguments." + input;

			}

		case UIDL:

			if (stringInput.length == 2 && isValidSpacing(input, stringInput.length)) {

				messageNumber = Integer.parseInt(stringInput[1]);

				if (doesMessageExist(messageNumber, numberOfMessages)) {

					return OK + messageNumber + " " + dbs.getMessageUniqueID(messageNumber);

				} else {

					return ERROR + "No such message. " + numberOfMessages + " messages in maildrop." + input;

				}

			} else if (stringInput.length == 1 && isValidSpacing(input, stringInput.length)){

				return dbs.getUniqueIDList();

			} else {

				return ERROR + "Invalid command entered." + input;
			}

		case QUIT:

			if (stringInput.length == 1 && isValidSpacing(input, stringInput.length)) {
				// only in the transaction state can the quit command change the state to update
				interpreterState = UPDATE_STATE;

				try {

					// this try/catch statement is specific to this command so that you know that there 
					// was an error removing a message rather than just knowing that an error has occurred
					dbs.removeMarkedMessages();

				} catch(Exception e) {

					return ERROR + "An error occurred while deleting messages." + input;

				}

				return OK + "POP3 server closing." + input;

			} else {

				return ERROR + "Incorrect number of arguments." + input;

			}

		default:
			// any other command not accepted in the transaction state such as USER should produce this error
			return ERROR + "Invalid command for current state." + input;
		}

	}

	private String authorizationInput(String input) {

		String[] stringInput = input.split(" ");

		switch(stringInput[0].toUpperCase()) {

		case USER:
			// allow the command USER to be input if the user has not already been confirmed
			if (userConfirmed == false) {

				// this 'if' statement is different from the commands in the transaction state because spaces 
				//can be valid for the user name e.g.(USER te st), so there can be more than 2 parts to the command.
				if (stringInput.length > 1) {

					return authoriseUser(input);

				} else {

					// there could be one part but a space can be considered to be a user name
					if (spacesInInput(input) > 1) {

						return authoriseUser(input);

					} else {

						return ERROR + "Invalid command." + input;
					}
				}

			} else {

				return ERROR + "Invalid command " + input + " password required.";
			}

		case PASS:

			// only allow the password to be entered if the user name has been entered directly before
			if (userConfirmed == true) { 
				// uses the same logic as the user case
				if (stringInput.length > 1) {

					return authorisePassword(input);

				} else {

					if (spacesInInput(input) > 1) {

						return authorisePassword(input);

					} else {
					
						userConfirmed = false;
						return ERROR + "Invalid command " + input + ".";
					}
				}

			} else {

				return ERROR + "Invalid command " + input + " username required.";
			}

		case QUIT:
			// do not allow any spacing
			if (stringInput.length == 1 && isValidSpacing(input, stringInput.length)) {
				
				// reset the user authorisation, since instance cannot close itself
				userConfirmed = false;
				// confirm that the user has quit but do not change the state
				return OK + "POP3 server closing.";

			} else {

				return ERROR + "Incorrect number of arguments." + input;

			}

		default:
			return ERROR + "Invalid command for current state." + input;
		}
	}
}

