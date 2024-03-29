/**
 * /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles
 * at the University of Florida, Fall 2017.
 *
 * This software is solely for the educational benefit of students
 * enrolled in the course during the Fall 2017 semester.
 *
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 *
 *  @Beverly A. Sanders, 2017
 */

package cop5556fa17;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static cop5556fa17.Scanner.Kind.*;
import static org.junit.Assert.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();


	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token.
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */

	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 *
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(scanner.new Token(kind, pos, length, line, pos_in_line), t);
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 *
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token check(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}

	/**
	 * Simple test case with a (legal) empty program
	 *
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}

	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 *
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 *
	 * Note that if we were reading the input from a file, as we will want to do
	 * later, the end of line character would be inserted by the text editor.
	 * Showing the input will let you check your input is what you think it is.
	 *
	 * @throws LexicalException
	 */
	@Test
	public void semiColonTest() throws LexicalException {
		String input = ";;\n ;; ";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNext(scanner, SEMI, 5, 1, 2, 3);
		checkNextIsEOF(scanner);
	}

	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it a String literal
	 * that is missing the closing ".
	 *
	 * Note that the outer pair of quotation marks delineate the String literal
	 * in this test program that provides the input to our Scanner.  The quotation
	 * mark that is actually included in the input must be escaped, \".
	 *
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.
	 *
	 * The test will work without putting the try-catch block around
	 * new Scanner(input).scan(); but then you won't be able to check
	 * or display the thrown exception.
	 *
	 * @throws LexicalException
	 */
	@Test
	public void failUnclosedStringLiteral() throws LexicalException {
		String input = "\" greetings  ";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(13,e.getPos());
			throw e;
		}
	}

	@Test
	public void wrongTerminationTest() throws LexicalException {
		String input = "\0\n\r";
		show(input);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}
	}

	@Test
	public void singleForwardFlash() throws LexicalException {
		String input = "/";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_DIV, 0, 1, 1, 1);
		checkNextIsEOF(scanner);
	}

	@Test
	public void identifierTest() throws LexicalException {
		String input = "truef _a1$ x1 $1 2a";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, IDENTIFIER, 0, 5, 1, 1);
		checkNext(scanner, IDENTIFIER, 6, 4, 1, 7);
		checkNext(scanner, IDENTIFIER, 11, 2, 1, 12);
		checkNext(scanner, IDENTIFIER, 14, 2, 1, 15);
		checkNext(scanner, INTEGER_LITERAL, 17, 1, 1, 18);
		checkNext(scanner, KW_a, 18, 1, 1, 19);
		checkNextIsEOF(scanner);
	}

	@Test
	public void booleanTest() throws LexicalException {
		String input = "true false";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, BOOLEAN_LITERAL, 0, 4, 1, 1);
		checkNext(scanner, BOOLEAN_LITERAL, 5, 5, 1, 6);
		checkNextIsEOF(scanner);
	}

	@Test
	public void keywordTest() throws LexicalException {
		String input = "x X y Y r R a A Z DEF_X DEF_Y SCREEN cart_x cart_y polar_a polar_r abs sin cos atan log image int boolean url file";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		int pos = 0;
		int posInLine = 1;
		int line = 1;
		String[] keyword = input.split(" ");
		for(int index=4,i=0;index < 30;index+=1,i+=1){

			Scanner.Kind kind = Scanner.Kind.values()[index];
			checkNext(scanner, kind, pos, keyword[i].length(), line, posInLine);
			pos += (keyword[i].length()+1);
			posInLine = pos +1;
		}
		checkNextIsEOF(scanner);
	}

	@Test
	public void separatorTest() throws LexicalException {
		String input = "()[];,";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		int pos = 0;
		int posInLine = 1;
		int line = 1;
		for(int index=51;index < 57;index+=1){
			Scanner.Kind kind = Scanner.Kind.values()[index];
			checkNext(scanner, kind, pos, 1, line, posInLine);
			pos += 1;
			posInLine = pos +1;
		}
		checkNextIsEOF(scanner);
	}

	@Test
	public void operatorWithWhiteSpaceTest() throws LexicalException {
		String input = "= >\t<\f! ? : == != >= <= & | + - * / % ** @ -> <-";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		int pos = 0;
		int posInLine = 1;
		int line = 1;
		String[] keyword = input.split("[ \t\f]");
		for(int index=30,i=0;index < 51;index+=1,i+=1){

			Scanner.Kind kind = Scanner.Kind.values()[index];
			checkNext(scanner, kind, pos, keyword[i].length(), line, posInLine);
			pos += (keyword[i].length()+1);
			posInLine = pos +1;
		}
		checkNextIsEOF(scanner);
	}

	@Test
	public void overflowTest() throws LexicalException {
		String input = "3527459316";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}
	}

	@Test
	public void stringInvalidEscapeTest() throws LexicalException {
		String input = "\"vivek\\b\\t\\n\\f\\r\\\"\\'gade\\\\\\w\"";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}

	}

	@Test
	public void stringInvalidEscapeTerminationTest() throws LexicalException {
		String input = "\"vivek\\b\\t\\n\\f\\r\\\"\\'gade\\\\\\";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}

	}

	@Test
	public void stringLineTerminatorTest() throws LexicalException {
		String input = "\"vivek\n\"";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}

	}

	@Test
	public void stringLiteralLineTerminatorExtraTest() throws LexicalException {
		String input = "\"vivek\r\"";
		show(input);
		thrown.expect(LexicalException.class);
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			throw e;
		}

	}
}