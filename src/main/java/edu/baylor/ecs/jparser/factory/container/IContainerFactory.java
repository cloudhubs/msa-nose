package edu.baylor.ecs.jparser.factory.container;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.impl.ModuleComponent;

public interface IContainerFactory {

    Component createComponent(ModuleComponent parent, ClassOrInterfaceDeclaration cls, CompilationUnit unit);

}
