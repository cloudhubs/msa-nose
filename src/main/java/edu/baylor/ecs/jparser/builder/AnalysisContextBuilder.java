package edu.baylor.ecs.jparser.builder;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.context.AnalysisContext;
import edu.baylor.ecs.jparser.component.impl.ClassComponent;
import edu.baylor.ecs.jparser.component.impl.DirectoryComponent;
import edu.baylor.ecs.jparser.component.impl.InterfaceComponent;
import edu.baylor.ecs.jparser.component.impl.ModuleComponent;
import edu.baylor.ecs.jparser.model.InstanceType;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class AnalysisContextBuilder {

    private String filePath;
    private List<String> classNames;
    private List<String> interfaceNames;
    private boolean succeeded = false;
    private Map<ModuleComponent, String> packageMap;
    private List<Component> classesAndInterfaces;
    private List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarations;
    private List<ClassComponent> classes;
    private List<InterfaceComponent> interfaces;
    private List<MethodDeclaration> methodDeclarations;
    private List<Component> methods;
    private List<ModuleComponent> modules;
    private DirectoryComponent directoryComponent;
    private InstanceType instanceType;

    public AnalysisContext build() {
        AnalysisContext context = new AnalysisContext();
        context.setClassOrInterfaceDeclarations(this.classOrInterfaceDeclarations);
        context.setClassesAndInterfaces(this.classesAndInterfaces); // Must be run first
        context.setSucceeded(this.succeeded);
        context.setInterfaceNames(this.interfaceNames);
        context.setRootPath(this.filePath);
        context.setClassNames(this.classNames);
        context.setPackageMap(this.packageMap);
        context.setMethodDeclarations(this.methodDeclarations);
        context.setMethods(this.methods);
        context.setModules(this.modules);
        context.setDirectoryGraph(this.directoryComponent);
        context.setClasses(this.classes);
        context.setInterfaces(this.interfaces);
        context.setInstanceType(this.instanceType);
        return context;
    }

    public AnalysisContextBuilder withClasses(List<ClassComponent> cls) {
        this.classes = cls;
        return this;
    }

    public AnalysisContextBuilder withInterfaces(List<InterfaceComponent> infs) {
        this.interfaces = infs;
        return this;
    }

    public AnalysisContextBuilder withRootPath(String path) {
        this.filePath = path;
        return this;
    }

    public AnalysisContextBuilder withPackageMap(Map<ModuleComponent, String> map) {
        this.packageMap = map;
        return this;
    }

    public AnalysisContextBuilder withClassNames(List<String> classNames) {
        this.classNames = classNames;
        return this;
    }

    public AnalysisContextBuilder withInterfaceNames(List<String> interfaceNames) {
        this.interfaceNames = interfaceNames;
        return this;
    }

    public AnalysisContextBuilder isSucceeded() {
        this.succeeded = true;
        return this;
    }

    public AnalysisContextBuilder withClassesAndInterfaces(List<Component> cls) {
        this.classesAndInterfaces = cls;
        return this;
    }

    public AnalysisContextBuilder withClassOrInterfaceDeclarations(List<ClassOrInterfaceDeclaration> cls) {
        this.classOrInterfaceDeclarations = cls;
        return this;
    }

    public AnalysisContextBuilder withModules(List<ModuleComponent> modules) {
        this.modules = modules;
        return this;
    }

    public AnalysisContextBuilder withMethods(List<Component> methods) {
        this.methods = methods.stream().filter(x -> x.getInstanceType().equals(InstanceType.METHODCOMPONENT))
                .collect(Collectors.toList()); // Only allow those which are definitively methods.
        return this;
    }

    public AnalysisContextBuilder withMethodDeclarations(List<MethodDeclaration> methods) {
        this.methodDeclarations = methods;
        return this;
    }

    public AnalysisContextBuilder withDirectoryGraph(DirectoryComponent root) {
        this.directoryComponent = root;
        return this;
    }

    public AnalysisContextBuilder withInstanceType(InstanceType type) {
        this.instanceType = type;
        return this;
    }
}
