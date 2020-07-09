package edu.baylor.ecs.jparser.factory.container.impl;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.impl.InterfaceComponent;
import edu.baylor.ecs.jparser.component.impl.ModuleComponent;
import edu.baylor.ecs.jparser.factory.container.AbstractContainerFactory;
import edu.baylor.ecs.jparser.model.ContainerType;
import edu.baylor.ecs.jparser.model.InstanceType;
import edu.baylor.ecs.jparser.model.LanguageFileType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
public class InterfaceComponentFactory extends AbstractContainerFactory {

    private static InterfaceComponentFactory INSTANCE;

    public final ContainerType TYPE = ContainerType.INTERFACE;

    private InterfaceComponentFactory() {
    }

    public static InterfaceComponentFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InterfaceComponentFactory();
        }
        return INSTANCE;
    }

    @Override
    public Component createComponent(ModuleComponent parent, ClassOrInterfaceDeclaration cls, CompilationUnit unit) {
        InterfaceComponent output = new InterfaceComponent();
        List<Component> annotations = initAnnotations(output, cls);
        output.setAnalysisUnit(unit);
        output.setAnnotations(annotations);
        output.setContainerType(ContainerType.CLASS);
        output.setCls(cls);
        output.setCompilationUnit(unit);
        output.setId(getId());
        output.setInstanceName(cls.getNameAsString() + "::InterfaceComponent");
        output.setInstanceType(InstanceType.CLASSCOMPONENT);
        output.setMethodDeclarations(cls.getMethods());
        output.setContainerName(cls.getNameAsString());
        output.setPackageName("N/A"); // TODO: Set package name
        output.setParent(parent);
        output.setStereotype(createStereotype(cls));
        output.setId(getId());
        output.setRawSource(cls.toString());
        output.setPath(parent.getPath() + "/" + cls.getNameAsString() + "."
                + LanguageFileType.fromString(parent.getLanguage().toLowerCase()).asString()); //TODO: Use appropriate directory separater for OS
        List<Component> methods = createMethods(cls, output);
        List<Component> constructors = createConstructors(cls, output);
        output.setMethods(methods);
        List<Component> subComponents = new ArrayList<>();
        subComponents.addAll(methods);
        subComponents.addAll(constructors);
        subComponents.addAll(annotations);
        output.setSubComponents(subComponents);
        return output;
    }

}
