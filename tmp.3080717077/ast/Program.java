package ast;

import java.io.PrintStream;
import java.util.List;

public class Program extends ASTNode {

  // final Expr expr;

  final String argName;
  final StmtList stmtList;
  final List<FunctionDecl> functions;

  public Program(String argName, StmtList stmtList, Location loc) {
    super(loc);
    this.argName = argName;
    this.stmtList = stmtList;
    this.functions = java.util.Collections.emptyList();
  }

  public Program(String argName, StmtList stmtList, List<FunctionDecl> functions, Location loc) {
    super(loc);
    this.argName = argName;
    this.stmtList = stmtList;
    this.functions = functions;
  }

  public String getArgName() {
    return argName;
  }

  public StmtList getStmtList() {
    return stmtList;
  }

  public void println(PrintStream ps) {
    ps.println();
  }

  public List<FunctionDecl> getFunctions() {
    return functions;
  }
}
