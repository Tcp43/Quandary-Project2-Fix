package ast;

import java.util.List;

public class FunctionDecl extends ASTNode {
    final String name;
    final List<String> paramNames;
    final StmtList body;

    public FunctionDecl(String name, List<String> paramNames, StmtList body, Location loc) {
        super(loc);
        this.name = name;
        this.paramNames = paramNames;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<String> getParamNames() {
        return paramNames;
    }

    public StmtList getBody() {
        return body;
    }
}
