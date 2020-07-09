package edu.baylor.ecs.jparser.factory.annotation;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.impl.AnnotationComponent;
import edu.baylor.ecs.jparser.model.AnnotationValuePair;
import edu.baylor.ecs.jparser.model.InstanceType;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class AnnotationFactory {

    // TODO: Basic, naive, doesn't provide as much info
    public static AnnotationComponent createAnnotationFromString(Component parent, String annotation) {
        AnnotationComponent an = new AnnotationComponent();
        an.setParent(parent);
        an.setPath(parent.getPath());
        an.setMetaModelFieldName(annotation);
        an.setInstanceName(annotation+"::AnnotationComponent");
        an.setAsString(annotation);
        an.setInstanceType(InstanceType.ANNOTATIONCOMPONENT);
        an.setAsString(annotation);
        return an;
    }

    public static List<Component> createAnnotationComponents(Component parent, NodeList<AnnotationExpr> annotations2) {
        List<Component> annotations = new ArrayList<>();
        for (AnnotationExpr exp : annotations2) {
            AnnotationComponent y = new AnnotationComponent();
            if (exp instanceof NormalAnnotationExpr) {
                List<AnnotationValuePair> pairs = new ArrayList<>();
                NormalAnnotationExpr normalized = (NormalAnnotationExpr) exp;
                normalized.getPairs().accept(new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MemberValuePair m, Object arg) {
                        super.visit(m, arg);
                        pairs.add(new AnnotationValuePair(m.getName().toString(), m.getValue().toString()));
                    }
                }, null);
                y.setAnnotationValuePairList(pairs);
            } else if (exp instanceof SingleMemberAnnotationExpr) {
                SingleMemberAnnotationExpr normalized = (SingleMemberAnnotationExpr) exp;
                y.setAnnotationValue(normalized.getMemberValue().toString());
            } else if (exp instanceof MarkerAnnotationExpr) {
                y.setAnnotationValue(exp.getNameAsString());
            }
            y.setAnnotation(exp);
            y.setParent(parent);
            y.setInstanceType(InstanceType.ANNOTATIONCOMPONENT); // TODO: Redundant
            y.setInstanceName(exp.getMetaModel().getMetaModelFieldName()+"::AnnotationComponent");
            y.setAnnotationMetaModel(exp.getMetaModel().toString());
            y.setAsString(exp.getName().asString());
            y.setMetaModelFieldName(exp.getMetaModel().getMetaModelFieldName());
            y.setPath(parent.getPath());
            y.setPackageName(exp.findCompilationUnit().flatMap(CompilationUnit::getPackageDeclaration) // TODO: Use elsewhere, Exponential slowdown?
                    .flatMap(pkg -> Optional.of(pkg.getNameAsString())).orElse("N/A"));
            annotations.add(y);
        }
        return annotations;
    }

}
