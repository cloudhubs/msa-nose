package edu.baylor.ecs.jparser.builder;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.impl.MethodInfoComponent;
import edu.baylor.ecs.jparser.component.impl.ModuleComponent;
import edu.baylor.ecs.jparser.model.ContainerStereotype;
import edu.baylor.ecs.jparser.model.InstanceType;
import edu.baylor.ecs.jparser.model.ModuleStereotype;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class ModuleBuilder {

    private ModuleStereotype moduleStereotype;
    private CompilationUnit compilationUnit;
    private ContainerStereotype stereotype;
    private List<MethodDeclaration> methodDeclarations;
    private List<Component> methods;
    private String instanceName;
    private InstanceType instanceType;
    private String packageName;
    private ModuleComponent parent;
    private String path;

    public ModuleComponent build() {
        ModuleComponent module = new ModuleComponent();
        module.setModuleStereotype(moduleStereotype);
        module.setStereotype(stereotype);
        module.setMethodDeclarations(methodDeclarations);
        module.setMethods(methods);
        module.setInstanceName(instanceName);
        module.setPackageName(packageName);
        module.setParent(parent);
        module.setPath(path);
        module.setSubComponents(methods);
        module.setInstanceType(instanceType);
        return module;
    }

    private List<Component> asComponents(List<MethodInfoComponent> methods) {
        return new ArrayList<>(methods);
    }

    public ModuleBuilder withModuleStereotype(ModuleStereotype stereotype) {
        this.moduleStereotype = stereotype;
        return this;
    }

    public ModuleBuilder withCompilationUnit(CompilationUnit unit) {
        this.compilationUnit = unit;
        return this;
    }

    public ModuleBuilder withStereotype(ContainerStereotype stereotype) {
        this.stereotype = stereotype;
        return this;
    }

    public ModuleBuilder withMethodDeclarations(List<MethodDeclaration> d) {
        this.methodDeclarations = d;
        return this;
    }

    public ModuleBuilder withMethods(List<Component> d) {
        this.methods = d;
        return this;
    }

    public ModuleBuilder withInstanceName(String name) {
        this.instanceName = name;
        return this;
    }

    public ModuleBuilder withInstanceType(InstanceType type) {
        this.instanceType = type;
        return this;
    }

    public ModuleBuilder withPackageName(String name) {
        this.packageName = name;
        return this;
    }

    public ModuleBuilder withParent(ModuleComponent parent) {
        this.parent = parent;
        return this;
    }

    public ModuleBuilder withPath(String path) {
        this.path = path;
        return this;
    }

}
