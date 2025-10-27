package ast;

public class AssignStmt extends Stmt {
  public final String varName;
  public final Expr expr;

  public AssignStmt(String varName, Expr expr, Location loc) {
    super(loc);
    this.varName = varName;
    this.expr = expr;
  }

  public String getVarName() {
    return varName;
  }

  public Expr getExpr() {
    return expr;
  }

  @Override
  public String toString() {
    return null;
  }
}
