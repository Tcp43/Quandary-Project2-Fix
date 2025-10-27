package ast;

public class RandomIntExpr extends Expr {

    public RandomIntExpr(Location loc) {
        super(loc);
    }

    @Override
    public String toString() {
        return "randomInt()";
    }
}
