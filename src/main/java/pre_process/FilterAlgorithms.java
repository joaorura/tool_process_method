package pre_process;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilterAlgorithms {
    private CompilationUnit compilationUnit;
    private HashMap<String, ArrayList<String>> mapAlgorithm = new HashMap<>();
    private HashMap<String, String> mapMethods = new HashMap<>();
    private ArrayList<String> answer = null;

    static class MethodCallVisitor extends VoidVisitorAdapter<Void> {
        private String method;
        private HashMap<String, ArrayList<String>> map;

        public MethodCallVisitor(HashMap<String, ArrayList<String>> map, String method) {
            this.map = map;
            this.method = method;
        }

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            String scope = n.getScope().toString();
            ArrayList<String> arrayList;

            if (scope.equals("Optional.empty")) {
                arrayList = map.get(method);
                arrayList.add(n.getName().toString());
            }

            super.visit(n, arg);
        }
    }

    public FilterAlgorithms(String text) {
        this.compilationUnit = StaticJavaParser.parse(String.valueOf(text));
    }

    public ArrayList<String> getAlgorithms() {
        if(this.answer == null) {
            this.answer = new ArrayList<>();

            compilationUnit.findAll(MethodDeclaration.class).forEach(c -> {
                String method = c.getName().toString();
                String code = c.toString();

                this.mapMethods.put(method, code);

                this.mapAlgorithm.put(method, new ArrayList<>());

                c.accept(new MethodCallVisitor(this.mapAlgorithm, method), null);
            });

            for (Map.Entry<String, ArrayList<String>> pair : mapAlgorithm.entrySet()) {
                StringBuilder stringBuilder = new StringBuilder();
                String str_aux;

                for(String method : pair.getValue()) {
                    str_aux = this.mapMethods.get(method);
                    stringBuilder.append(str_aux).append("\n");
                }

                str_aux = this.mapMethods.get(pair.getKey());
                stringBuilder.append(str_aux);

                answer.add(stringBuilder.toString());
            }
        }

        return (ArrayList<String>) this.answer.clone();
    }
}