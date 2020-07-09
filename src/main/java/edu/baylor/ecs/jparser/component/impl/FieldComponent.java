package edu.baylor.ecs.jparser.component.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.model.AccessorType;
import edu.baylor.ecs.jparser.visitor.IComponentVisitor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FieldComponent extends Component {

    private List<Component> annotations;
    /**
     * This field is a list because you may declare multiple variables on one line (int x, y, z)
     */
    private List<String> variables;
    @JsonProperty(value = "field_name")
    private String fieldName;
    private AccessorType accessor;
    @JsonProperty(value = "static")
    private boolean staticField;
    @JsonProperty(value = "final")
    private boolean finalField;
    @JsonProperty(value = "default_value_string")
    private String stringifiedDefaultValue;
    private String type;

    @Override
    public void accept(IComponentVisitor visitor) {

    }
}
