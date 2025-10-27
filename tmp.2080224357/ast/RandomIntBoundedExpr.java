package ast;

public class RandomIntBoundedExpr extends Expr {
    final Expr bound;

    public RandomIntBoundedExpr(Expr bound, Location loc) {
        super(loc);
        this.bound = bound;
    }

    public Expr getBound() { return bound; }
}
