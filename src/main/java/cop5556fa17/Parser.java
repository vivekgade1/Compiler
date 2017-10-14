package cop5556fa17;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;

import java.util.ArrayList;

import static cop5556fa17.Scanner.Kind.EOF;

public class Parser {

    @SuppressWarnings("serial")
    public class SyntaxException extends Exception {
        Token t;

        public SyntaxException(Token t, String message) {
            super(message);
            this.t = t;
        }

    }

    Scanner scanner;
    Token t;

    Parser(Scanner scanner) {
        this.scanner = scanner;
        t = scanner.nextToken();
    }

    /**
     * Main method called by compiler to parser input. Checks for EOF
     *
     * @throws SyntaxException
     */
    public Program parse() throws SyntaxException {
        Program ast_tree_node = program();
        matchEOF();
        return ast_tree_node;
    }

    public void match(Kind k) throws SyntaxException {

        System.out.println("t.kind:::::" + t.kind + "k:::::::::::" + k);
        if (t.kind == k) {
            t = scanner.nextToken();
        } else {
            System.out.println("flag");
            throw new SyntaxException(t, "unmatched error");
        }
    }

    /**
     * Program ::= IDENTIFIER ( Declaration SEMI | Statement SEMI )*
     *
     * Program is start symbol of our grammar.
     *
     * @throws SyntaxException
     */
    Program program() throws SyntaxException {
        // TODO implement this
        ArrayList<ASTNode> ast_node_arr = new ArrayList<ASTNode> ();
        Token ft =t;
        match(Kind.IDENTIFIER);
        while (t.kind == Kind.KW_int || t.kind == Kind.KW_boolean || t.kind == Kind.KW_image || t.kind == Kind.KW_url
                || t.kind == Kind.KW_file || t.kind == Kind.IDENTIFIER) {
            if (t.kind == Kind.KW_int || t.kind == Kind.KW_boolean || t.kind == Kind.KW_image || t.kind == Kind.KW_url
                    || t.kind == Kind.KW_file) {
                ast_node_arr.add(declaration());
                match(Kind.SEMI);
            }

            if (t.kind == Kind.IDENTIFIER) {
                ast_node_arr.add(checkForStatement());
                match(Kind.SEMI);

            }
        }
        return new Program(ft, ft, ast_node_arr);
    }
    // expressions

    Expression primary() throws SyntaxException {
        Token ft = t;
        if (t.kind == Kind.INTEGER_LITERAL) {
            match(Kind.INTEGER_LITERAL);
            return new Expression_IntLit(ft, ft.intVal());
        }
        else if (t.kind == Kind.LPAREN) {
            match(Kind.LPAREN);
            Expression ex = expression();
            match(Kind.RPAREN);
            return ex;
        } else if (t.kind == Kind.BOOLEAN_LITERAL) {
            boolean bool_value = false;
            if(t.getText().equals("true")) bool_value = true;
            match(Kind.BOOLEAN_LITERAL);
            return new Expression_BooleanLit(ft, bool_value);
        }

        else if (t.kind == Kind.KW_sin || t.kind == Kind.KW_cos

                || t.kind == Kind.KW_atan || t.kind == Kind.KW_abs || t.kind == Kind.KW_cart_x
                || t.kind == Kind.KW_cart_y || t.kind == Kind.KW_polar_a || t.kind == Kind.KW_polar_r) {

            return functionApplication();

        } else {

            throw new SyntaxException(t, "Primary Error");
        }

    }

    private Expression functionApplication() throws SyntaxException {
        // TODO Auto-generated method stub
        Token ft = t;
        checkFunctionName();

        if (t.kind == Kind.LPAREN) {
            match(Kind.LPAREN);
            Expression ex = expression();
            match(Kind.RPAREN);
            return new Expression_FunctionAppWithExprArg(ft, ft.kind, ex);

        } else if (t.kind == Kind.LSQUARE) {

            match(Kind.LSQUARE);
            Index indx = selector();
            match(Kind.RSQUARE);
            return new Expression_FunctionAppWithIndexArg(ft, ft.kind, indx);

        }

        else {
            throw new SyntaxException(t, "failed in Function Application");
        }

    }

    private Index selector() throws SyntaxException {
        Token ft = t;
        Expression ex0 = expression();
        match(Kind.COMMA);
        Expression ex1 = expression();
        return new Index(ft, ex0, ex1);
    }

    ASTNode checkForStatement() throws SyntaxException {
        Token ft = t;
        match(Kind.IDENTIFIER);

        if (t.kind == Kind.OP_RARROW) {
            return imageOutStatement(ft);
        } else if (t.kind == Kind.OP_LARROW) {
            return imageInStatement(ft);
        }

        else if(t.kind.equals(Kind.OP_ASSIGN)|| t.kind.equals(Kind.LSQUARE)){
            LHS l = leftHandSide(ft);
            match(Kind.OP_ASSIGN);
            Expression ex = expression();
            return new Statement_Assign(ft, l, ex);
        }else throw new SyntaxException(t, "Syntax error "+t);
    }

    public Expression unaryExpressionNotPlusMinus() throws SyntaxException {
        Token ft = t;
        switch (t.kind){
            case KW_X:
                match(Kind.KW_X);
                return new Expression_PredefinedName(ft, ft.kind);
            //break;
            case KW_x:
                match(Kind.KW_x);
                return new Expression_PredefinedName(ft, ft.kind);
            //break;
            case KW_y:
                match(Kind.KW_y);
                return new Expression_PredefinedName(ft, ft.kind);
            //break;
            case KW_Y:
                match(Kind.KW_Y);
                return new Expression_PredefinedName(ft, ft.kind);
            //break;
            case KW_Z:
                match(Kind.KW_Z);
                return new Expression_PredefinedName(ft, ft.kind);
            //break;
            case KW_R:
                match(Kind.KW_R);
                return new Expression_PredefinedName(ft, ft.kind);
            //break;
            case KW_r:
                match(Kind.KW_r);
                return new Expression_PredefinedName(ft, ft.kind);
            //break;
            case KW_a:
                match(Kind.KW_a);
                return new Expression_PredefinedName(ft, ft.kind);
            //break;
            case KW_A:
                match(Kind.KW_A);
                return new Expression_PredefinedName(ft, ft.kind);
            //break;
            case KW_DEF_X:
                match(Kind.KW_DEF_X);
                return new Expression_PredefinedName(ft, ft.kind);
            //break;
            case KW_DEF_Y:
                match(Kind.KW_DEF_Y);
                return new Expression_PredefinedName(ft, ft.kind);
            //break;
            case IDENTIFIER:
                return identPixelSelectorExpression();
            //break;

            case INTEGER_LITERAL:
            case LPAREN:
            case KW_sin:
            case KW_cos:
            case KW_atan:
            case KW_abs:
            case KW_cart_x:
            case KW_cart_y:
            case KW_polar_a:
            case KW_polar_r:
            case BOOLEAN_LITERAL:
                return primary();
            //break;
            case OP_EXCL:
                match(Kind.OP_EXCL);
                return unaryExpression();
            //break;
            default:
                throw new SyntaxException(t, " The error in Unary Expression Not PlusMinus");

        }

    }

    private Expression unaryExpression() throws SyntaxException {
        // TODO Auto-generated method stub
        if (t.kind == Kind.OP_PLUS) {

            match(Kind.OP_PLUS);
            return unaryExpression();

        }

        else if (t.kind == Kind.OP_MINUS) {

            match(Kind.OP_MINUS);
            return unaryExpression();

        }

        else if (t.kind == Kind.OP_EXCL || t.kind == Kind.KW_x || t.kind == Kind.KW_y || t.kind == Kind.KW_r
                || t.kind == Kind.KW_a || t.kind == Kind.KW_X || t.kind == Kind.KW_Y ||

                t.kind == Kind.KW_Z || t.kind == Kind.KW_A || t.kind == Kind.KW_R || t.kind == Kind.KW_DEF_X
                || t.kind == Kind.KW_DEF_Y || t.kind == Kind.IDENTIFIER

                ||

                t.kind == Kind.INTEGER_LITERAL || t.kind == Kind.LPAREN || t.kind == Kind.KW_sin
                || t.kind == Kind.KW_cos || t.kind == Kind.KW_atan || t.kind == Kind.KW_abs || t.kind == Kind.KW_cart_x
                || t.kind == Kind.KW_A.KW_cart_y || t.kind == Kind.KW_polar_a || t.kind == Kind.KW_polar_r
                || t.kind == Kind.BOOLEAN_LITERAL || t.kind == Kind.INTEGER_LITERAL || t.kind == Kind.LPAREN)

        {
            return unaryExpressionNotPlusMinus();

        }

        else
            throw new SyntaxException(t, "ERROR In Unary Expression");
    }

    public Expression multiplyExpression() throws SyntaxException {
        Expression ex0 = null;
        Expression ex1 = null;
        Token ft = t;
        ex0 = unaryExpression();

        while (t.kind == Kind.OP_TIMES || t.kind == Kind.OP_DIV || t.kind == Kind.OP_MOD) {
            Token op = t;
            if (t.kind == Kind.OP_TIMES) {
                match(Kind.OP_TIMES);
            } else if (t.kind == Kind.OP_DIV) {
                match(Kind.OP_DIV);
            } else if (t.kind == Kind.OP_MOD) {
                match(Kind.OP_MOD);
            }
            ex1 = unaryExpression();
            ex0 = new Expression_Binary(ft, ex0, op, ex1);

        }
        return ex0;
    }

    public Expression addExpression() throws SyntaxException {
        Token ft = t;
        Expression ex0 = null;
        Expression ex1 = null;

        ex0 =  multiplyExpression();

        while (t.kind == Kind.OP_PLUS || t.kind == Kind.OP_MINUS) {
            Token op = t;
            if (t.kind == Kind.OP_PLUS) {
                match(Kind.OP_PLUS);
            } else if (t.kind == Kind.OP_MINUS) {
                match(Kind.OP_MINUS);
            }
            ex1 = multiplyExpression();
            ex0 = new Expression_Binary(ft, ex0, op, ex1);
        }
        return ex0;

    }

    public Expression relExpression() throws SyntaxException {
        Token ft = t;
        Expression ex0 = null;
        Expression ex1 = null;
        ex0 = addExpression();

        while (t.kind == Kind.OP_LT || t.kind == Kind.OP_GT || t.kind == Kind.OP_LE || t.kind == Kind.OP_GE) {
            Token op = t;
            if (t.kind == Kind.OP_LT) {
                match(Kind.OP_LT);
            } else if (t.kind == Kind.OP_GT) {
                match(Kind.OP_GT);
            } else if (t.kind == Kind.OP_LE) {
                match(Kind.OP_LE);
            } else if (t.kind == Kind.OP_GE) {
                match(Kind.OP_GE);
            }
            ex1 = addExpression();
            ex0 = new Expression_Binary(ft, ex0, op, ex1);
        }
        return ex0;

    }

    public Expression equalExpression() throws SyntaxException {
        Token ft = t;
        Expression ex0 = null;
        Expression ex1 = null;
        ex0 = relExpression();

        while (t.kind == Kind.OP_EQ || t.kind == Kind.OP_NEQ) {
            Token op = t;
            if (t.kind == Kind.OP_EQ) {
                match(Kind.OP_EQ);
            } else if (t.kind == Kind.OP_NEQ) {
                match(Kind.OP_NEQ);
            }
            ex1 = relExpression();
            ex0 = new Expression_Binary(ft, ex0, op, ex1);

        }
        return ex0;

    }

    public Expression andExpression() throws SyntaxException {

        Token ft = t;
        Expression ex0 = null;
        Expression ex1 = null;

        ex0 = equalExpression();

        while (t.kind == Kind.OP_AND) {

            if (t.kind == Kind.OP_AND) {
                Token op = t;
                match(Kind.OP_AND);
                ex1 = equalExpression();
                ex0 = new Expression_Binary(ft, ex0, op, ex1);
            }

        }
        return ex0;

    }

    public Expression orExpression() throws SyntaxException {
        Token ft = t;
        Expression ex0 = null;
        Expression ex1 = null;
        ex0 = andExpression();

        while (t.kind == Kind.OP_OR) {
            if (t.kind == Kind.OP_OR) {
                match(Kind.OP_OR);
                ex1 = andExpression();
                ex0 = new Expression_Binary(ft, ex0, t, ex1);
            }
        }
        return ex0;

    }

    private Expression identPixelSelectorExpression() throws SyntaxException {
        // TODO Auto-generated method stub
        Token ft = t;
        match(Kind.IDENTIFIER);
        Expression_PixelSelector ep = indentOrPixelNext(ft);
        if(ep == null){
            return new Expression_Ident(ft, ft);
        }
        return ep;
    }

    Expression_PixelSelector indentOrPixelNext(Token ft) throws SyntaxException {
        // TODO Auto-generated method stub
        if(t.kind.equals(Kind.LSQUARE)){
            match(Kind.LSQUARE);
            Index i = selector();
            match(Kind.RSQUARE);
            return new Expression_PixelSelector(ft, ft, i);
        }
        return null;

    }

    private LHS leftHandSide(Token ft) throws SyntaxException {

        if (t.kind == Kind.LSQUARE) {

            match(Kind.LSQUARE);
            Index indx = lHSSelector();
            match(Kind.RSQUARE);
            return new LHS(ft, ft, indx);
        }
        return new LHS(ft, ft, null);
    }

    // TODO Auto-generated method stub

    private Statement_Out imageOutStatement(Token ft) throws SyntaxException {
        // TODO Auto-generated method stub
        match(Kind.OP_RARROW);
        Sink s = sink();
        return new Statement_Out(ft, ft, s);
    }

    private Sink sink() throws SyntaxException {
        // TODO Auto-generated method stub
        Token ft = t;
        if (t.kind.equals(Kind.IDENTIFIER)){
            match(Kind.IDENTIFIER);
            return new Sink_Ident(ft, ft);

        }
        else if (t.kind.equals(Kind.KW_SCREEN)){
            match(Kind.KW_SCREEN);
            return new Sink_SCREEN(ft);
        }
        else
            throw new SyntaxException(t, "There is a Sink exception");
    }

    private Statement_In imageInStatement(Token ft) throws SyntaxException {
        // TODO Auto-generated method stub
        match(Kind.OP_LARROW);
        Source s = source();
        return new Statement_In(ft,ft,s);
    }

    /**
     * Expression ::= OrExpression OP_Q Expression OP_COLON Expression |
     * OrExpression
     *
     * Our test cases may invoke this routine directly to support incremental
     * development.
     *
     * @throws SyntaxException
     */
    Expression expression() throws SyntaxException {
        // TODO implement this.
        Token ft = t;
        Expression condition = orExpression();
        Expression true_condition = null;
        Expression false_condition = null;
        if (t.kind == Kind.OP_Q) {

            match(Kind.OP_Q);
            true_condition = expression();
            match(Kind.OP_COLON);
            false_condition = expression();
            return new Expression_Conditional(ft, condition, true_condition, false_condition);
        }
        return condition;

    }

    void checkFunctionName() throws SyntaxException {
        if (t.kind == Kind.KW_sin)
            match(Kind.KW_sin);

        else if (t.kind == Kind.KW_cos)
            match(Kind.KW_cos);
        else if (t.kind == Kind.KW_atan)
            match(Kind.KW_atan);

        else if (t.kind == Kind.KW_abs)
            match(Kind.KW_abs);
        else if (t.kind == Kind.KW_cart_x)
            match(Kind.KW_cart_x);
        else if (t.kind == Kind.KW_cart_y)
            match(Kind.KW_cart_y);
        else if (t.kind == Kind.KW_polar_a)
            match(Kind.KW_polar_a);
        else if (t.kind == Kind.KW_polar_r)
            match(Kind.KW_polar_r);
        else
            throw new SyntaxException(t, "function name didn't match");
    }

    Declaration declaration() throws SyntaxException {

        if (t.kind == Kind.KW_int) {
            return variableDeclartion();
        }

        else if (t.kind == Kind.KW_boolean) {
            return variableDeclartion();
        }

        else if (t.kind == Kind.KW_image) {
            return imageDeclartion();

        }
        else if (t.kind == Kind.KW_url || t.kind == Kind.KW_file) {
            return sourceSinkDeclaration();

        } else
            throw new SyntaxException(t, "Declartion error");
    }

    private Declaration_Image imageDeclartion() throws SyntaxException {
        Token ft = t;
        match(Kind.KW_image);
        Expression ex0 = null;
        Expression ex1 = null;
        Source s = null;
        if (t.kind == Kind.LSQUARE) {
            match(Kind.LSQUARE);
            ex0 = expression();
            match(Kind.COMMA);
            ex1 = expression();
            match(Kind.RSQUARE);
        }
        Token name = t;
        match(Kind.IDENTIFIER);
        if (t.kind == Kind.OP_LARROW) {
            match(Kind.OP_LARROW);
            s = source();
        }
        return new Declaration_Image(ft, ex0, ex1, name, s);

    }

    public Declaration_SourceSink sourceSinkDeclaration() throws SyntaxException {
        // TODO Auto-generated method stub
        Token ft = t;
        sourceSinkType();
        Token name = t;
        if(t.kind == Kind.KW_url){
            match(Kind.KW_url);
        }else if(t.kind == Kind.KW_file){
            match(Kind.KW_file);
        }
        match(Kind.IDENTIFIER);
        match(Kind.OP_ASSIGN);
        Source s = source();
        return new Declaration_SourceSink(ft, ft, name, s);

    }

    public Declaration_Variable variableDeclartion() throws SyntaxException {
        // TODO Auto-generated method stub
        Token ft = t;
        varType();
        Token name = t;
        match(Kind.IDENTIFIER);
        Expression ex = null;
        if (t.kind == Kind.OP_ASSIGN) {
            match(Kind.OP_ASSIGN);
            ex = expression();

        }
        return new Declaration_Variable(ft, ft, name, ex);
    }

    void varType() throws SyntaxException {
        if (t.kind == Kind.KW_int)
            match(Kind.KW_int);
        else if (t.kind == Kind.KW_boolean)
            match(Kind.KW_boolean);
        else
            throw new SyntaxException(t, "varType error");

    }

    Source source() throws SyntaxException {
        Token ft = t;
        if (t.kind == Kind.STRING_LITERAL) {
            String s = t.getText();
            match(Kind.STRING_LITERAL);
            return new Source_StringLiteral(ft, s);
        } else if (t.kind == Kind.OP_AT) {

            match(Kind.OP_AT);
            Expression ex = expression();
            return new Source_CommandLineParam(ft, ex);
        }

        else if (t.kind == Kind.IDENTIFIER) {
            Token name = t;
            match(Kind.IDENTIFIER);
            return new Source_Ident(ft, name);
        }

        else
            throw new SyntaxException(t, " error at source");

    }

    void sourceSinkType() throws SyntaxException {
        if (t.kind == Kind.KW_url) {
            match(Kind.KW_url);
        } else if (t.kind == Kind.KW_file) {
            match(Kind.KW_file);
        }

        else
            throw new SyntaxException(t, " error at source");

    }

    Index lHSSelector() throws SyntaxException {

        match(Kind.LSQUARE);
        if (t.kind == Kind.KW_x) {
            Index indx = xySelector();
            match(Kind.RSQUARE);
            return indx;
        } else if (t.kind == Kind.KW_r) {
            Index indx = raSelector();
            match(Kind.RSQUARE);
            return indx;
        } else
            throw new SyntaxException(t, "Lhs selector error");


    }

    public Index raSelector() throws SyntaxException {
        // TODO Auto-generated method stub
        Token ft = t;
        match(Kind.KW_r);
        Expression ex0 = new Expression_PredefinedName(ft,ft.kind);
        match(Kind.COMMA);
        Token ex = t;
        match(Kind.KW_A);
        Expression ex1 = new Expression_PredefinedName(ex,ex.kind);
        return new Index(ft, ex0, ex1);
    }

    public Index xySelector() throws SyntaxException {
        Token ft = t;
        match(Kind.KW_x);
        Expression ex0 = new Expression_PredefinedName(ft,ft.kind);
        match(Kind.COMMA);
        Token ex = t;
        Expression ex1 = new Expression_PredefinedName(ex,ex.kind);
        match(Kind.KW_y);
        return new Index(ft, ex0, ex1);
    }

    /**
     * Only for check at end of program. Does not "consume" EOF so no attempt to
     * get nonexistent next Token.
     *
     * @return
     * @throws SyntaxException
     */
    private Token matchEOF() throws SyntaxException {
        if (t.kind == EOF) {
            return t;
        }
        String message = "Expected EOL at " + t.line + ":" + t.pos_in_line;
        throw new SyntaxException(t, message);
    }
}
