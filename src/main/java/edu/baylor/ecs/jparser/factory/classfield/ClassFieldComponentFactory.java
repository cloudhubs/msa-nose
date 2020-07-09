package edu.baylor.ecs.jparser.factory.classfield;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.impl.ClassComponent;
import edu.baylor.ecs.jparser.component.impl.FieldComponent;
import edu.baylor.ecs.jparser.factory.annotation.AnnotationFactory;
import edu.baylor.ecs.jparser.model.AccessorType;
import edu.baylor.ecs.jparser.model.InstanceType;

import java.util.ArrayList;
import java.util.List;

public class ClassFieldComponentFactory {

    public static List<FieldComponent> createClassField(ClassComponent parent, List<FieldDeclaration> declarations) {
        List<FieldComponent> output = new ArrayList<>();
        for(FieldDeclaration f : declarations) {
            FieldComponent field = new FieldComponent();
            field.setAccessor(AccessorType.fromString(f.getAccessSpecifier().asString()));
            f.getVariables().accept(new VoidVisitorAdapter<Object>() {
                @Override
                public void visit(VariableDeclarator v, Object arg) {
                    super.visit(v, arg);
                    field.setFieldName(v.getName().asString()); // TODO: Does this override for lines like "int x, y, z"?
                    field.setType(v.getTypeAsString());
                }
            }, null);
            for (Modifier m : f.getModifiers()) {
                switch(m.getKeyword()) {
                    case PUBLIC: field.setAccessor(AccessorType.PUBLIC); break;
                    case FINAL: field.setFinalField(true); break;
                    case STATIC: field.setStaticField(true); break;
                    case PROTECTED: field.setAccessor(AccessorType.PROTECTED); break;
                    case PRIVATE: field.setAccessor(AccessorType.PRIVATE); break;
                    case DEFAULT: field.setAccessor(AccessorType.DEFAULT); break;
                }
            }
            field.setInstanceName(parent.getInstanceName() + "::FieldComponent::" + field.getFieldName());
            field.setInstanceType(InstanceType.FIELDCOMPONENT);
            field.setPackageName(parent.getPackageName());
            field.setPath(parent.getPath());
            List<Component> annotations = AnnotationFactory.createAnnotationComponents(field, f.getAnnotations());
            field.setAnnotations(annotations);
            output.add(field);
        }
        return output;
    }

}
