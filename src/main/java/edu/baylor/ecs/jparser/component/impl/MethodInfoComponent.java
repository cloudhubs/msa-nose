package edu.baylor.ecs.jparser.component.impl;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.model.AccessorType;
import edu.baylor.ecs.jparser.model.InstanceType;
import edu.baylor.ecs.jparser.visitor.IComponentVisitor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MethodInfoComponent extends Component {

    @JsonIgnore
    private String rawSource;
    @JsonIgnore
    private List<String> rawSourceStripped;
    @JsonIgnore
    private List<String> statements; //

    private Long id;
    private AccessorType accessor; //
    @JsonProperty(value = "method_name")
    private String methodName; //
    @JsonProperty(value = "return_type")
    private String returnType; //
    @JsonProperty(value = "parameters")
    private List<MethodParamComponent> methodParams; //
    @JsonProperty(value = "static_method")
    private boolean staticMethod; //
    @JsonProperty(value = "abstract_method")
    private boolean abstractMethod; //
    @JsonProperty(value = "subroutines")
    private List<MethodInfoComponent> subMethods; //
    private List<Component> annotations; //
    private int lineCount;
    private int lineBegin;
    private int lineEnd;

    public MethodInfoComponent() {
        this.instanceType = InstanceType.METHODCOMPONENT;
    }

    public List<Component> getAnnotationByNameContains(String name) {
        if (annotations == null)
            return null;
        List<Component> list = new ArrayList<>();
        for (Component a : annotations) {
            // If the name is equal or the name skipping the @ is equal
            if (a.asAnnotationComponent().getAsString().contains(name)) { // Should never be null
                list.add(a);
            }
        }
        return list;
    }

    public Component getAnnotationByName(String name) {
        if (annotations == null)
            return null;
        for (Component a : annotations) {
            // If the name is equal or the name skipping the @ is equal
            if (a.asAnnotationComponent().getAsString().equals(name) ||
                    a.asAnnotationComponent().getAsString().substring(1).equals(name)) {
                return a;
            }
        }
        return null;
    }

    @Override
    public String getPath() {
        if (parent != null) {
            if (parent.getPath() != null)
                return parent.getPath() + "::MethodDeclaration::" + this.methodName; //TODO: Add line numbers?
            else
                return null;
        } else {
            return null;
        }
    }

    @Override
    public String getPackageName() {
        if (parent != null) {
            if (parent.getPackageName() != null)
                return parent.getPackageName();
            else
                return null;
        } else {
            return null;
        }
    }

    @Override
    public void accept(IComponentVisitor visitor) {
        visitor.visit(this);
        for (Component e : this.subComponents) {
            e.accept(visitor);
        }
    }
}
