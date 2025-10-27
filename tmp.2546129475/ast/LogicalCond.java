package ast;

public class LogicalCond extends Cond {

    public static final int AND = 1;
    public static final int OR  = 2;
    public static final int NOT = 3;

    private final Cond cond1;
    private final int operator;
    private final Cond cond2; // null for NOT

    public LogicalCond(Cond cond1, int operator, Cond cond2, Location loc) {
        super(loc);
        this.cond1 = cond1;
        this.operator = operator;
        this.cond2 = cond2;
    }

    public Cond getLeftCond()  { return cond1; }
    public Cond getRightCond() { return cond2; }
    public int  getOperator()  { return operator; }

    @Override
    public String toString() {
        if (operator == NOT) return "(!" + cond1 + ")";
        return "(" + cond1 + " " + (operator == AND ? "&&" : "||") + " " + cond2 + ")";
    }
}
