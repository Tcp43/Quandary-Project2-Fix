package ast;

public class IdentExpr extends Expr {
  final String varName;

  public IdentExpr(String varName, Location loc) {
    super(loc);
    this.varName = varName;
  }

  public String getVarName() { return varName; }

  @Override
  public String toString() { return null; }
}