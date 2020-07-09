package edu.baylor.ecs.jparser.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import edu.baylor.ecs.jparser.model.ContainerStereotype;
import edu.baylor.ecs.jparser.model.ContainerType;
import lombok.Data;

import java.util.List;

@Data
public abstract class ClassOrInterfaceComponent extends ContainerComponent {

    @JsonIgnore
    protected ClassOrInterfaceDeclaration cls;
    @JsonIgnore
    protected CompilationUnit analysisUnit;
    @JsonIgnore
    protected List<MethodDeclaration> methodDeclarations;
    @JsonIgnore
    protected String rawSource;

    @JsonProperty
    protected String path;
    @JsonProperty(value = "declaration_type")
    protected ContainerType containerType;
    protected List<Component> annotations;
    protected ContainerStereotype stereotype;

}
