package ast;

public class UnaryMinusExpr extends Expr {
    private final Expr expr;

    public UnaryMinusExpr(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "-(" + expr.toString() + ")";
    }
}
