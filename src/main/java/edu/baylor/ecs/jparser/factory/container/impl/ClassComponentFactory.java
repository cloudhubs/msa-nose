package edu.baylor.ecs.jparser.factory.container.impl;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.impl.ClassComponent;
import edu.baylor.ecs.jparser.component.impl.ModuleComponent;
import edu.baylor.ecs.jparser.factory.classfield.ClassFieldComponentFactory;
import edu.baylor.ecs.jparser.factory.container.AbstractContainerFactory;
import edu.baylor.ecs.jparser.model.ContainerType;
import edu.baylor.ecs.jparser.model.InstanceType;
import edu.baylor.ecs.jparser.model.LanguageFileType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClassComponentFactory extends AbstractContainerFactory {

    private static ClassComponentFactory INSTANCE;

    public final ContainerType TYPE = ContainerType.CLASS;

    private Map<ClassOrInterfaceDeclaration, Component> classOrInterfaceDeclarationComponentMap;

    private ClassComponentFactory() {
    }

    public static ClassComponentFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassComponentFactory();
        }
        return INSTANCE;
    }

    @Override
    public Component createComponent(ModuleComponent parent, ClassOrInterfaceDeclaration cls, CompilationUnit unit) {
        ClassComponent output = new ClassComponent();
        List<Component> annotations = initAnnotations(output, cls);
        List<ClassComponent> subClasses = createSubClasses(cls);
        output.setAnalysisUnit(unit);
        output.setAnnotations(annotations);
        output.setContainerType(ContainerType.CLASS);
        output.setCls(cls);
        output.setCompilationUnit(unit);
        output.setId(getId());
        output.setInstanceName(cls.getName().asString() + "::ClassComponent");
        output.setContainerName(cls.getName().asString());
        output.setInstanceType(InstanceType.CLASSCOMPONENT);
        output.setMethodDeclarations(cls.getMethods());
        output.setPackageName(unit.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse("N/A"));
        output.setParent(parent);
        output.setStereotype(createStereotype(cls));
        output.setId(getId());
        output.setFieldComponents(ClassFieldComponentFactory.createClassField(output, cls.getFields()));
        output.setRawSource(cls.toString());
        output.setPath(parent.getPath() + "/" + cls.getNameAsString() + "."
                + LanguageFileType.fromString(parent.getLanguage()).asString().toLowerCase()); //TODO: Use appropriate directory separater for OS
        List<Component> methods = createMethods(cls, output);
        List<Component> constructors = createConstructors(cls, output);
        output.setRawSource(cls.toString());
        output.setLineCount(cls.getEnd().map(x -> x.line).orElse(-1));
        output.setMethods(methods);
        output.setConstructors(constructors);
        List<Component> subComponents = new ArrayList<>();
        subComponents.addAll(methods);
        subComponents.addAll(constructors);
        subComponents.addAll(annotations);
        output.setSubComponents(subComponents);
        return output;
    }

    private List<ClassComponent> createSubClasses(ClassOrInterfaceDeclaration cls) {
        // TODO
        return null;
    }

}
