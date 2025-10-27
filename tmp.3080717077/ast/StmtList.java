package ast;

public class StmtList extends Stmt {
    private final Stmt first;
    private final StmtList rest; // may be null

    public StmtList(Stmt first, StmtList rest, Location loc) {
        super(loc);
        this.first = first;
        this.rest = rest;
    }

    public Stmt getFirst() {
        return first;
    }

    public StmtList getRest() {
        return rest;
    }

    @Override
    public String toString() {
        return (first == null ? "" : first.toString()) +
                (rest == null ? "" : "\n" + rest.toString());
    }
}
