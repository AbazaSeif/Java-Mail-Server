import org.junit.*;

import static org.junit.Assert.*;

public class TestClass {

	CommandInterpreter com;

	@Before
	public void setUp() {

		com = new CommandInterpreter();
	}

	@Test
	public void testAuthorization() {
		// check that this still produces an error regardless of string splitting using spaces
		assertEquals("-ERR Invalid command.USER ", com.handleInput("USER "));
		// there should be one space separating USER and the argument, so the other space is considered as
		// the argument, hence the different responses for different number of spaces
		assertEquals("-ERR The user USER   does not exist.USER  ", com.handleInput("USER  "));
		// same space check
		assertEquals("-ERR Invalid command for current state. USER", com.handleInput(" USER"));

		assertEquals("-ERR Invalid command.USER", com.handleInput("USER"));
		// check that program gives same error regardless of lower or upper case
		assertEquals("-ERR Invalid command.user", com.handleInput("user"));
		// username has to match test
		assertEquals("-ERR The user USER best does not exist.USER best", com.handleInput("USER best"));
		// just to show that other commands cannot be entered state
		assertEquals("-ERR Invalid command for current state.APOP 1", com.handleInput("APOP 1"));
		// spaces are still considered
		assertEquals("-ERR The user user test  does not exist.user test ", com.handleInput("user test "));

		assertEquals("+OK Welcome test, please enter your password.user test", com.handleInput("user test"));
		// once a password has been denied, further password attempts will fail without user confirmation
		assertEquals("-ERR Invalid command PASS.", com.handleInput("PASS"));

		assertEquals("-ERR Invalid command PASS  username required.", com.handleInput("PASS "));

		assertEquals("-ERR Invalid command PASS   username required.", com.handleInput("PASS  "));

		assertEquals("-ERR Invalid command for current state. PASS", com.handleInput(" PASS"));

		assertEquals("-ERR Invalid command PASS password username required.", com.handleInput("PASS password"));
		// test has been accepted as a username
		assertEquals("+OK Welcome test, please enter your password.USER test", com.handleInput("USER test"));

		assertEquals("-ERR Invalid password.PASS pass word", com.handleInput("PASS pass word"));

		assertEquals("+OK Welcome test, please enter your password.USER test", com.handleInput("USER test"));
		// password has been accepted
		assertEquals("+OK Maildrop locked and ready.PASS password", com.handleInput("PASS password"));
	}

	@Test
	public void testTransaction() {
		com.handleInput("USER test");
		com.handleInput("pass password");
		// user commands do not work once a user has been locked
		assertEquals("-ERR Invalid command for current state.USER ", com.handleInput("USER "));

		// STAT

		// testing of STAT with paramters
		assertEquals("-ERR Incorrect number of arguments.STAT 1 ", com.handleInput("STAT 1 "));

		assertEquals("-ERR Incorrect number of arguments.STAT 1", com.handleInput("STAT 1"));
		// i have added 2 as a dummmy value for the number of messages in the mail drop to use for testing purposes
		assertEquals("+OK 2 0", com.handleInput("STAT"));
		// a series of space testing. testing the other transaction commands for this will be unnecessary
		// since they all use the same method to valid spacing.
		assertEquals("-ERR Invalid command for current state. STAT", com.handleInput(" STAT"));

		// LIST

		assertEquals("-ERR Invalid command entered.LIST 1 2", com.handleInput("LIST 1 2"));
		// error handling when parameter is not an int
		assertEquals("-ERR There has been an error processing the command.LIST g", com.handleInput("LIST g"));

		assertEquals("-ERR Invalid command entered.LIST ", com.handleInput("LIST "));

		assertEquals("-ERR Invalid command entered.LIST  ", com.handleInput("LIST  "));

		assertEquals("-ERR No such message. 2 messages in maildrop.LIST 3", com.handleInput("LIST 3"));

		assertEquals("+OK 2 0", com.handleInput("LIST 2"));
		//prints out a multi-line response
		assertEquals("+OK 2 messages (0 octets)\n1 0\n2 0\n.", com.handleInput("LIST"));

		// RETR

		assertEquals("+OK 0 octets\n<the POP3 server sends the entire message here>\n.", com.handleInput("RETR 2"));
		// test to see if removing a space will still cause the program to function properly
		assertEquals("-ERR Invalid command for current state.RETR3", com.handleInput("RETR3"));

		assertEquals("-ERR No such message. 2 messages in maildrop.RETR 3", com.handleInput("RETR 3"));

		assertEquals("-ERR Incorrect number of arguments.RETR", com.handleInput("RETR"));

		assertEquals("-ERR Incorrect number of arguments.RETR 2 3", com.handleInput("RETR 2 3"));

		// DELE

		// for the rest of the transition commands, i will be using the same tests to see if they function properly
		// these tests just check the responses are correct.
		// i didn't see the need to do an official test of the state changes since it would be pretty obvious
		// that they work if i am able to input state specific commands. 
		assertEquals("-ERR Incorrect number of arguments.DELE", com.handleInput("DELE"));

		assertEquals("-ERR Incorrect number of arguments.DELE 2 3", com.handleInput("DELE 2 3"));

		assertEquals("-ERR Incorrect number of arguments.DELE  ", com.handleInput("DELE  "));

		assertEquals("+OK Message 1 deleted.DELE 1", com.handleInput("DELE 1"));

		assertEquals("-ERR No such message.DELE 0", com.handleInput("DELE 0"));

		// NOOP

		// case sensitive tests to see if the same response is given
		assertEquals("-ERR Too many arguments.NOOP 1", com.handleInput("NOOP 1"));

		assertEquals("-ERR Too many arguments.noop 1", com.handleInput("noop 1"));

		assertEquals("-ERR Too many arguments.noop ", com.handleInput("noop "));

		assertEquals("+OK noop", com.handleInput("noop"));

		assertEquals("+OK NOOP", com.handleInput("NOOP"));

		// RSET

		assertEquals("+OK Maildrop has 2 messages (0 octets).RSET", com.handleInput("RSET"));

		assertEquals("-ERR Incorrect number of arguments.RSET 1", com.handleInput("RSET 1"));

		assertEquals("-ERR Incorrect number of arguments.RSET  ", com.handleInput("RSET  "));

		// TOP

		assertEquals("-ERR Incorrect number of arguments.TOP ", com.handleInput("TOP "));

		assertEquals("-ERR Incorrect number of arguments.TOP 1 ", com.handleInput("TOP 1 "));

		assertEquals("+OK \n" + 
				"<the POP3 server sends the headers of the message, a blank line, and the first 2 lines of the body of the message>" +
				"\n.TOP 1 2", com.handleInput("TOP 1 2"));

		assertEquals("-ERR Incorrect number of arguments.TOP 1 2 3", com.handleInput("TOP 1 2 3"));

		assertEquals("-ERR Incorrect number of arguments.TOP 1  3", com.handleInput("TOP 1  3"));

		assertEquals("-ERR There has been an error processing the command.TOP i 3", com.handleInput("TOP i 3"));

		// UIDL

		// these tests for UIDL are very similar to LIST because they both use the same logic
		assertEquals("-ERR Invalid command entered.UIDL 1 2", com.handleInput("UIDL 1 2"));

		assertEquals("-ERR There has been an error processing the command.UIDL g", com.handleInput("UIDL g"));

		assertEquals("-ERR Invalid command entered.UIDL ", com.handleInput("UIDL "));

		assertEquals("-ERR Invalid command entered.UIDL  ", com.handleInput("UIDL  "));

		assertEquals("-ERR No such message. 2 messages in maildrop.UIDL 3", com.handleInput("UIDL 3"));
		// null represents the unqiueID
		assertEquals("+OK 2 null", com.handleInput("UIDL 2"));

		assertEquals("+OK \n1 null\n2 null\n.", com.handleInput("UIDL"));

	}

	@Test
	public void testQuitAndMiscellaneous() {
		
		// handling of empty string and space only commands
		assertEquals("-ERR There has been an error processing the command.    ", com.handleInput("    "));
		assertEquals("-ERR Invalid command for current state.", com.handleInput(""));

		// QUIT in AUTHORIZATION state

		assertEquals("+OK Welcome test, please enter your password.user test", com.handleInput("user test"));

		assertEquals("-ERR Incorrect number of arguments.QUIT ", com.handleInput("QUIT "));

		assertEquals("-ERR Incorrect number of arguments.QUIT  1", com.handleInput("QUIT  1"));

		assertEquals("-ERR Invalid command for current state. QUIT  ", com.handleInput(" QUIT  "));

		assertEquals("-ERR Invalid command for current state.QU IT", com.handleInput("QU IT")); 

		assertEquals("+OK POP3 server closing.", com.handleInput("QUIT"));
		// since you cannot quit the command interpreter class without system.out, the system just resets the
		// user confirmation without changing the state
		assertEquals("+OK Welcome test, please enter your password.USER test", com.handleInput("USER test"));
		// check to see state has not changed to transaction
		assertEquals("-ERR Invalid command for current state.STAT", com.handleInput("STAT"));

		assertEquals("-ERR Invalid command user test password required.", com.handleInput("user test"));

		assertEquals("+OK Maildrop locked and ready.pass password", com.handleInput("pass password"));

		// QUIT in TRANSACTION state

		// program has quit and should not be in the QUIT state.
		assertEquals("+OK POP3 server closing.QUIT", com.handleInput("QUIT"));

		// this is just for testing

		// STAT does not work in the update state
		assertEquals("-ERR ", com.handleInput("STAT"));
		// and neither does user.
		assertEquals("-ERR ", com.handleInput("USER"));
		// just a test to show that you will not get a positive response from any response while in the update state.
		assertEquals("-ERR ", com.handleInput("QUIT"));
	}

	public static void main(String[] args) {
	}

}
