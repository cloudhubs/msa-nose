package edu.baylor.ecs.jparser.factory.container.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.ContainerComponent;
import edu.baylor.ecs.jparser.component.impl.ClassComponent;
import edu.baylor.ecs.jparser.component.impl.DirectoryComponent;
import edu.baylor.ecs.jparser.component.impl.InterfaceComponent;
import edu.baylor.ecs.jparser.component.impl.ModuleComponent;
import edu.baylor.ecs.jparser.factory.container.AbstractContainerFactory;
import edu.baylor.ecs.jparser.factory.container.ComponentFactoryProducer;
import edu.baylor.ecs.jparser.model.ContainerType;
import edu.baylor.ecs.jparser.model.InstanceType;
import edu.baylor.ecs.jparser.model.ModuleStereotype;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleComponentFactory extends AbstractContainerFactory {

    private static ModuleComponentFactory INSTANCE;

    private ModuleComponentFactory() {
    }

    public static ModuleComponentFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModuleComponentFactory();
        }
        return INSTANCE;
    }

    /**
     * Returns null because Modules have no class or interface declaration
     * @param cls
     * @param unit
     * @return
     */
    @Override
    @Deprecated
    public Component createComponent(ModuleComponent parent, ClassOrInterfaceDeclaration cls, CompilationUnit unit) {
        return null;
    }

    public ModuleComponent createComponent(Component parent, Component root) {
        DirectoryComponent dir;
        if (root instanceof DirectoryComponent) {
            dir = (DirectoryComponent) root;
        } else {
            return null;
        }
        long id = getId();
        ModuleComponent module = new ModuleComponent();
        module.setLanguage(dir.getLanguage());
        module.setPath(dir.getPath());
        List<ClassOrInterfaceDeclaration> allClassOrInterfaces = new ArrayList<>();
        List<Component> allCOIComponents = new ArrayList<>();
        for (File f : dir.getFiles()) {
            CompilationUnit unit = createCompilationUnit(f);
            List<ClassOrInterfaceDeclaration> cls = createClassOrInterfaceDeclarations(unit);
            allClassOrInterfaces.addAll(cls);
            allCOIComponents.addAll(createClassesAndInterfaces(module, unit, cls));
        }
        List<ClassComponent> ccd = allCOIComponents.stream()
                .filter(x -> x instanceof ClassComponent)
                .map(x -> (ClassComponent) x).collect(Collectors.toList());
        List<InterfaceComponent> icd = allCOIComponents.stream()
                .filter(x -> x instanceof InterfaceComponent)
                .map(x -> (InterfaceComponent)x).collect(Collectors.toList());
        module.setParent(parent);
        module.setInstanceType(InstanceType.MODULECOMPONENT);
        module.setPackageName(dir.getPath()+"::"+id);
        module.setContainerName(dir.getPath());
        module.setInstanceName(dir.getPath()+"::ModuleComponent::"+id); //TODO: Perhaps not this
        module.setClassOrInterfaceDeclarations(allClassOrInterfaces);
        module.setClassesAndInterfaces(allCOIComponents);
        module.setClasses(ccd);
        module.setInterfaces(icd);
        module.setClassNames(createClassNames(allClassOrInterfaces));
        module.setInterfaceNames(createInterfaceNames(allClassOrInterfaces));
        module.setModuleStereotype(ModuleStereotype.FABRICATED);
        module.setMethods(extractMethodComponents(allCOIComponents));
        module.setMethodDeclarations(extractMethods(allCOIComponents));
        module.setId(id);
        return module;
    }

    private List<MethodDeclaration> extractMethods(List<Component> cois) {
        List<MethodDeclaration> allmethods = new ArrayList<>();
        for (Component e : cois) {
            if (e.getInstanceType() == InstanceType.CLASSCOMPONENT
                    || e.getInstanceType() == InstanceType.INTERFACECOMPONENT){
                allmethods.addAll(((ContainerComponent)e).getMethodDeclarations());
            }
        }
        return allmethods;
    }

    private List<Component> extractMethodComponents(List<Component> cois) {
        List<Component> allmethods = new ArrayList<>();
        for (Component e : cois) {
            if (e.getInstanceType() == InstanceType.CLASSCOMPONENT
                    || e.getInstanceType() == InstanceType.INTERFACECOMPONENT){
                allmethods.addAll(((ContainerComponent) e).getMethods());
                //allmethods.addAll() TODO: Include sub methods / method calls
            }
        }
        return allmethods;
    }

    private CompilationUnit createCompilationUnit(File file) {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        CompilationUnit unit = null;
        try {
            unit = StaticJavaParser.parse(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // TODO: Logger
        }
        return unit;
    }

    private List<String> createClassNames(List<ClassOrInterfaceDeclaration> cls) {
        List<String> output = new ArrayList<>(); // TODO: Best Practice
        for (ClassOrInterfaceDeclaration c : cls) {
            if (!c.isInterface()) {
                output.add(c.getNameAsString());
            }
        }
        return output;
    }

    private List<String> createInterfaceNames(List<ClassOrInterfaceDeclaration> cls) {
        List<String> output = new ArrayList<>();
        for (ClassOrInterfaceDeclaration c : cls) {
            if (c.isInterface()) {
                output.add(c.getNameAsString());
            }
        }
        return output;
    }

    //TODO: Module
    private List<Component> createClassesAndInterfaces(ModuleComponent module, CompilationUnit unit, List<ClassOrInterfaceDeclaration> classOrInterfaces) {
        List<Component> clsList = new ArrayList<>();
        for(ClassOrInterfaceDeclaration cls : classOrInterfaces) {
            ContainerType type = cls.isInterface() ? ContainerType.INTERFACE : ContainerType.CLASS;
            AbstractContainerFactory factory = ComponentFactoryProducer.getFactory(type);
            assert factory != null;
            Component coi = factory.createComponent(module, cls, unit);
            clsList.add(coi);
        }
        return clsList;
    }

    private List<ClassOrInterfaceDeclaration> createClassOrInterfaceDeclarations(CompilationUnit unit) {
        List<ClassOrInterfaceDeclaration> output = new ArrayList<>();
        unit.accept(new VoidVisitorAdapter<Object>(){
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Object arg){
                super.visit(n, arg);
                output.add(n);
            }
        }, null);
        return output;
    }


}
