package ast;

public class CompCond extends Cond {

    public static final int EQ = 0;
    public static final int NE = 1;
    public static final int LT = 2;
    public static final int GT = 3;
    public static final int LE = 4;
    public static final int GE = 5;

    private final Expr leftExpr;
    private final int operator;
    private final Expr rightExpr;

    public CompCond(Expr leftExpr, int operator, Expr rightExpr, Location loc) {
        super(loc);
        this.leftExpr = leftExpr;
        this.operator = operator;
        this.rightExpr = rightExpr;
    }

    public Expr getLeftExpr() {
        return leftExpr;
    }

    public Expr getRightExpr() {
        return rightExpr;
    }

    public int getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return "CompCond(" + leftExpr + "," + opToString(operator) + "," + rightExpr + ")";
    }

    private static String opToString(int op) {
        switch (op) {
            case EQ:
                return "==";
            case NE:
                return "!=";
            case LT:
                return "<";
            case GT:
                return ">";
            case LE:
                return "<=";
            case GE:
                return ">=";
            default:
                return "?";
        }
    }
}
