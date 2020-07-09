package edu.baylor.ecs.jparser.component.impl;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.javaparser.ast.CompilationUnit;
import edu.baylor.ecs.jparser.component.ClassOrInterfaceComponent;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.model.InstanceType;
import edu.baylor.ecs.jparser.visitor.IComponentVisitor;
import lombok.Data;

import java.util.List;

/**
 * this.subComponents is a MetaSubComponent. When you use .getSubComponents() on a Class or Interface component,
 * You must also use getAnnotations() or getMethods() to specify which subcomponent you desire.
 */
@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ClassComponent extends ClassOrInterfaceComponent {

    @JsonIgnore
    protected CompilationUnit compilationUnit;
    private List<Component> constructors;
    private List<FieldComponent> fieldComponents;

    public ClassComponent() {
        this.instanceType = InstanceType.CLASSCOMPONENT;
    }

    public Component getMethodByLineNumber(int line) {
        for (Component m : methods) {
            if (m.asMethodInfoComponent().getLineBegin() == line) {
                return m;
            }
        }
        return null;
    }

    public Component getConstructorByLineNumber(int line) {
        for (Component m : constructors) {
            if (m.asMethodInfoComponent().getLineBegin() == line) {
                return m;
            }
        }
        return null;
    }

    public Component getClassFieldByName(String name) {
        for (Component f : fieldComponents) {
            if (f.asFieldComponent().getFieldName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    public Component getMethodByName(String name) {
        for (Component m : methods) {
            if (m.asMethodInfoComponent().getMethodName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Does the same thing as getInstanceName() however this is more intuitive for users.
     * @return
     */
    @JsonIgnore
    public String getClassName() {
        return this.containerName;
    }

    @Override
    public void accept(IComponentVisitor visitor) {
        visitor.accept(visitor);
    }
}
