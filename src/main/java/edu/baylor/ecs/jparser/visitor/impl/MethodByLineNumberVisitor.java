package edu.baylor.ecs.jparser.visitor.impl;

import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.context.AnalysisContext;
import edu.baylor.ecs.jparser.component.impl.*;
import edu.baylor.ecs.jparser.visitor.IComponentVisitor;

import java.util.Optional;

/**
 * EXPERIMENTAL
 */
@Deprecated
public class MethodByLineNumberVisitor implements IComponentVisitor {

    private MethodInfoComponent method;
    private int lineNumber;

    public MethodByLineNumberVisitor(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public void visit(InterfaceComponent component) {
        Optional<Component> result = component.getMethods().stream()
                .filter(x -> ((MethodInfoComponent) x).getLineBegin() == lineNumber).findFirst();
        result.ifPresent(value -> this.method = (MethodInfoComponent) value);
    }

    @Override
    public void visit(ClassComponent component) {
        Optional<Component> result = component.getMethods().stream()
                .filter(x -> ((MethodInfoComponent) x).getLineBegin() == lineNumber).findFirst();
        result.ifPresent(value -> this.method = (MethodInfoComponent) value);
    }

    @Override
    public void visit(MethodInfoComponent component) {
        if (component.getLineBegin() == this.lineNumber) {
            this.method = component;
        }
    }

    @Override
    public void visit(ModuleComponent component) {
        Optional<Component> result = component.getMethods().stream()
                .filter(x -> ((MethodInfoComponent) x).getLineBegin() == lineNumber).findFirst();
        result.ifPresent(value -> this.method = (MethodInfoComponent) value);
    }

    @Override
    public void visit(AnalysisContext component) {

    }

    @Override
    public void visit(AnnotationComponent component) {
    }

    @Override
    public void visit(DirectoryComponent component) {
    }

    @Override
    public void visit(FieldComponent component) {
    }

    @Override
    public void visit(MethodParamComponent component) {
    }

    @Override
    public void accept(IComponentVisitor visitor) {
    }
}
