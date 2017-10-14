package cop5556fa17;

import cop5556fa17.Parser.SyntaxException;
import cop5556fa17.Scanner.LexicalException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static cop5556fa17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ParserTest {

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
     * Simple test case with an empty parse.  This test
     * expects an SyntaxException because all legal parses must
     * have at least an identifier
     *
     * @throws LexicalException
     * @throws SyntaxException
     */
    @Test
    public void testEmpty() throws LexicalException, SyntaxException {
        String input = ""; // The input is the empty string. Parsing should fail
        show(input); // Display the input
        Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
        // initialize it
        show(scanner); // Display the tokens
        Parser parser = new Parser(scanner); //Create a parser
        thrown.expect(SyntaxException.class);
        try {
            ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
        } catch (SyntaxException e) {
            show(e);  //catch the exception and show it
            throw e;  //rethrow for Junit
        }
    }


    @Test
    public void testNameOnly() throws LexicalException, SyntaxException {
        String input = "prog";  //Legal program with only a name
        show(input);            //display input
        Scanner scanner = new Scanner(input).scan();   //Create scanner and create token list
        show(scanner);    //display the tokens
        Parser parser = new Parser(scanner);   //create parser
        Program ast = parser.parse();          //parse program and get AST
        show(ast);                             //Display the AST
        assertEquals(ast.name, "prog");        //Check the name field in the Program object
        assertTrue(ast.decsAndStatements.isEmpty());   //Check the decsAndStatements list in the Program object.  It should be empty.
    }

    @Test
    public void testDec1() throws LexicalException, SyntaxException {
        String input = "prog int k;";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        Program ast = parser.parse();
        show(ast);
        assertEquals(ast.name, "prog");
        //This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
        Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
                .get(0);
        assertEquals(KW_int, dec.type.kind);
        assertEquals("k", dec.name);
        assertNull(dec.e);
    }


    @Test
    public void testcaseparse() throws SyntaxException, LexicalException {
        String input = "prog \"abcded\" boolean a=true;";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            ASTNode ast=parser.parse();  //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }


    @Test
    public void testcase1() throws SyntaxException, LexicalException {
        String input = "isBoolean boolean ab=true; boolean cd==true; abcd=true ? return true: return false;"; //Should fail for ==
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            Program ast=parser.program();  //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testcase5() throws SyntaxException, LexicalException {
        String input = "isBoolean boolean ab=true; boolean cd==true; abcd=true ? return true: return false;"; //Should fail for =
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            Program ast=parser.program();  //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testcase2() throws SyntaxException, LexicalException {
        String input = "isUrl url filepng=\"abcd\"; \n @expr=12; url awesome=@expr; \n url filepng=abcdefg";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        Program ast=parser.program();  //Parse the program
        show(ast);
        assertEquals(ast.name,"isUrl");
        assertEquals(ast.decsAndStatements.size(),1);
        Declaration_SourceSink dss=(Declaration_SourceSink)ast.decsAndStatements.get(0);
        assertEquals(dss.name,"filepng");
        assertEquals(dss.type,KW_url);
        Source_StringLiteral s=(Source_StringLiteral)dss.source;
        assertEquals(s.fileOrUrl,"abcd");
    }

    @Test
    public void testcase3() throws SyntaxException, LexicalException {
        String input = "isUrl url filepng=\"abcd\"; \n @expr=12; url awesome=@expr; \n url filepng=abcdefg";
        show(input);
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        try {
            ASTNode ast=parser.parse();  //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }

    @Test
    public void testcase4() throws SyntaxException, LexicalException {
        String input =  "imageProgram image imageName;"
                + "\n imageName->abcdpng; "
                + "\n imageName -> SCREEN; "
                + "\n imageName <- \"awesome\";"
                + "\n imageName <- @express; \n"
                + "\n imageName <- abcdpng;";  // Image related Test cases
        Scanner scanner = new Scanner(input).scan();
        show(scanner);
        Parser parser = new Parser(scanner);
        Program ast=parser.program();  //Parse the program
        show(ast);
        assertEquals(ast.name,"imageProgram");

        //Declaration statement start
        Declaration_Image dv1=(Declaration_Image)ast.decsAndStatements.get(0);
        assertEquals(dv1.name,"imageName");
        assertNull(dv1.xSize);
        assertNull(dv1.ySize);
        assertNull(dv1.source);

        Statement_Out dv2=(Statement_Out)ast.decsAndStatements.get(1);
        assertEquals(dv2.name,"imageName");
        Sink_Ident si2=(Sink_Ident)dv2.sink;
        assertEquals(si2.name,"abcdpng");}
}
