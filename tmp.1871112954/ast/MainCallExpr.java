package ast;

public class MainCallExpr extends Expr {
    final Expr arg;

    public MainCallExpr(Expr arg, Location loc) {
        super(loc);
        this.arg = arg;
    }

    public Expr getArg() {
        return arg;
    }

    @Override
    public String toString() {
        return "main(" + arg + ")";
    }
}
