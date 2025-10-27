package interpreter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import parser.ParserWrapper;
import ast.*;

public class Interpreter {

    // Process return codes
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_PARSING_ERROR = 1;
    public static final int EXIT_STATIC_CHECKING_ERROR = 2;
    public static final int EXIT_DYNAMIC_TYPE_ERROR = 3;
    public static final int EXIT_NIL_REF_ERROR = 4;
    public static final int EXIT_QUANDARY_HEAP_OUT_OF_MEMORY_ERROR = 5;
    public static final int EXIT_DATA_RACE_ERROR = 6;
    public static final int EXIT_NONDETERMINISM_ERROR = 7;

    static private Interpreter interpreter;

    public static Interpreter getInterpreter() {
        return interpreter;
    }

    public static void main(String[] args) {
        String gcType = "NoGC"; // default for skeleton, which only supports NoGC
        long heapBytes = 1 << 14;
        int i = 0;
        String filename;
        long quandaryArg;
        try {
            for (; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-")) {
                    if (arg.equals("-gc")) {
                        gcType = args[i + 1];
                        i++;
                    } else if (arg.equals("-heapsize")) {
                        heapBytes = Long.valueOf(args[i + 1]);
                        i++;
                    } else {
                        throw new RuntimeException("Unexpected option " + arg);
                    }
                } else {
                    if (i != args.length - 2) {
                        throw new RuntimeException("Unexpected number of arguments");
                    }
                    break;
                }
            }
            filename = args[i];
            quandaryArg = Long.valueOf(args[i + 1]);
        } catch (Exception ex) {
            System.out.println("Expected format: quandary [OPTIONS] QUANDARY_PROGRAM_FILE INTEGER_ARGUMENT");
            System.out.println("Options:");
            System.out.println("  -gc (MarkSweep|Explicit|NoGC)");
            System.out.println("  -heapsize BYTES");
            System.out.println("BYTES must be a multiple of the word size (8)");
            return;
        }

        Program astRoot = null;
        Reader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            astRoot = ParserWrapper.parse(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            Interpreter.fatalError("Uncaught parsing error: " + ex, Interpreter.EXIT_PARSING_ERROR);
        }

        interpreter = new Interpreter(astRoot, quandaryArg);
        interpreter.initMemoryManager(gcType, heapBytes);
        String returnValueAsString = interpreter.executeRoot(astRoot, quandaryArg).toString();
        System.out.println("Interpreter returned " + returnValueAsString);
    }

    public static void fatalError(String message, int exitCode) {
        System.err.println(message);
        System.exit(exitCode);
    }

    final Program astRoot;
    final Random random;

    final Map<String, FunctionDecl> functions = new HashMap<>();

    private Map<String, Object> env;

    private Interpreter(Program astRoot, long quandaryArg) {
        this.astRoot = astRoot;
        this.random = new Random(quandaryArg);
        this.env = new HashMap<String, Object>();

        
    }

    void initMemoryManager(String gcType, long heapBytes) {
        if (gcType.equals("Explicit")) {
            throw new RuntimeException("Explicit not implemented");
        } else if (gcType.equals("MarkSweep")) {
            throw new RuntimeException("MarkSweep not implemented");
        } else if (gcType.equals("RefCount")) {
            throw new RuntimeException("RefCount not implemented");
        } else if (gcType.equals("NoGC")) {
            // Nothing to do
        }
    }

    Object executeRoot(Program astRoot, long arg) {
        env.put(astRoot.getArgName(), arg);
        return execute(astRoot.getStmtList());
    }

    private Object callMain(long newArg) {
        Map<String, Object> savedEnv = env;
        try {
            env = new HashMap<>();
            env.put(astRoot.getArgName(), newArg);
            return execute(astRoot.getStmtList());
        } finally {
            env = savedEnv;
        }
    }

    private Object callFunction(FunctionDecl func, java.util.List<Long> argVals) {
        Map<String, Object> savedEnv = env;
        try {
            env = new HashMap<>();
            java.util.List<String> params = func.getParamNames();
            if (params.size() != argVals.size()) {
                fatalError("Function " + func.getName() + " arity mismatch: expected "
                        + params.size() + " got " + argVals.size(), EXIT_PARSING_ERROR);
            }
            for (int i = 0; i < params.size(); i++) {
                env.put(params.get(i), argVals.get(i));
            }
            return execute(func.getBody());
        } finally {
            env = savedEnv;
        }
    }

    public Object execute(Stmt stmt) {
        if (stmt instanceof StmtList) {
            StmtList sl = (StmtList) stmt;
            Object retVal = execute(sl.getFirst());
            if (retVal != null)
                return retVal;
            if (sl.getRest() != null)
                return execute(sl.getRest());
            return null;

        } else if (stmt instanceof DeclStmt) {
            DeclStmt declStmt = (DeclStmt) stmt;
            env.put(declStmt.getVarName(), evaluate(declStmt.getExpr()));
            return null;

        } else if (stmt instanceof AssignStmt) {
            AssignStmt as = (AssignStmt) stmt;
            env.put(as.getVarName(), evaluate(as.getExpr()));
            return null;

        } else if (stmt instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt) stmt;
            if (evaluate(ifStmt.getCond())) {
                return execute(ifStmt.getThenStmt());
            } else if (ifStmt.getElseStmt() != null) {
                return execute(ifStmt.getElseStmt());
            }
            return null;

        } else if (stmt instanceof PrintStmt) {
            System.out.println(evaluate(((PrintStmt) stmt).getExpr()));
            return null;

        } else if (stmt instanceof ReturnStmt) {
            return evaluate(((ReturnStmt) stmt).getExpr());
        }

        throw new RuntimeException();
    }

    Object evaluate(Expr expr) {
        if (expr instanceof ConstExpr) {
            return ((ConstExpr) expr).getValue();
        } else if (expr instanceof IdentExpr) {
            return env.get(((IdentExpr) expr).getVarName());
        } else if (expr instanceof UnaryMinusExpr) {
            return -((long) evaluate(((UnaryMinusExpr) expr).getExpr()));
        } else if (expr instanceof RandomIntExpr) {
            return (long) random.nextInt();
        } else if (expr instanceof MainCallExpr) {
            long v = (long) evaluate(((MainCallExpr) expr).getArg());
            return callMain(v);

        } else if (expr instanceof RandomIntBoundedExpr) {
            long n = (long) evaluate(((RandomIntBoundedExpr) expr).getBound());
            if (n <= 0)
                return 0L;
            if (n > Integer.MAX_VALUE)
                n = Integer.MAX_VALUE;
            return (long) random.nextInt((int) n);

        } else if (expr instanceof CallExpr) {
            CallExpr ce = (CallExpr) expr;
            FunctionDecl fd = functions.get(ce.getFuncName());
            if (fd == null) {
                fatalError("Undefined function: " + ce.getFuncName(), EXIT_PARSING_ERROR);
            }
            java.util.ArrayList<Long> values = new java.util.ArrayList<>();
            for (Expr e : ce.getArgs()) {
                values.add((Long) evaluate(e));
            }
            return callFunction(fd, values);

        } else if (expr instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr) expr;
            switch (be.getOperator()) {
                case BinaryExpr.PLUS:
                    return (long) evaluate(be.getLeftExpr()) + (long) evaluate(be.getRightExpr());
                case BinaryExpr.MINUS:
                    return (long) evaluate(be.getLeftExpr()) - (long) evaluate(be.getRightExpr());
                case BinaryExpr.TIMES:
                    return (long) evaluate(be.getLeftExpr()) * (long) evaluate(be.getRightExpr());
                default:
                    throw new RuntimeException();
            }
        }

        throw new RuntimeException();
    }

    boolean evaluate(Cond cond) {
        if (cond instanceof CompCond) {
            CompCond compCond = (CompCond) cond;
            switch (compCond.getOperator()) {
                case CompCond.EQ:
                    return (long) evaluate(compCond.getLeftExpr()) == (long) evaluate(compCond.getRightExpr());
                case CompCond.NE:
                    return (long) evaluate(compCond.getLeftExpr()) != (long) evaluate(compCond.getRightExpr());
                case CompCond.LT:
                    return (long) evaluate(compCond.getLeftExpr()) < (long) evaluate(compCond.getRightExpr());
                case CompCond.GT:
                    return (long) evaluate(compCond.getLeftExpr()) > (long) evaluate(compCond.getRightExpr());
                case CompCond.LE:
                    return (long) evaluate(compCond.getLeftExpr()) <= (long) evaluate(compCond.getRightExpr());
                case CompCond.GE:
                    return (long) evaluate(compCond.getLeftExpr()) >= (long) evaluate(compCond.getRightExpr());
            }

        } else if (cond instanceof LogicalCond) {
            LogicalCond logicalCond = (LogicalCond) cond;
            switch (logicalCond.getOperator()) {
                case LogicalCond.AND:
                    return evaluate(logicalCond.getLeftCond()) && evaluate(logicalCond.getRightCond());
                case LogicalCond.OR:
                    return evaluate(logicalCond.getLeftCond()) || evaluate(logicalCond.getRightCond());
                case LogicalCond.NOT:
                    return !evaluate(logicalCond.getLeftCond());
            }
        }

        throw new RuntimeException();
    }
}