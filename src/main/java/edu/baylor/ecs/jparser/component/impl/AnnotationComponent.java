package edu.baylor.ecs.jparser.component.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.model.AnnotationValuePair;
import edu.baylor.ecs.jparser.visitor.IComponentVisitor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AnnotationComponent extends Component {

    @JsonIgnore
    private AnnotationExpr annotation;

    @JsonProperty(value = "name")
    private String asString;

    private String annotationMetaModel;
    private String metaModelFieldName;
    @JsonProperty(value = "key_value_pairs")
    private List<AnnotationValuePair> annotationValuePairList;
    @JsonProperty(value = "value")
    private String annotationValue;

    public AnnotationComponent() {
        this.annotationValuePairList = new ArrayList<>();
    }

    public void setAsString(String inp) {
        this.asString = (inp.startsWith("@") ? inp : "@" + inp);
    }

    public String getAsString() {
        return this.asString;
    }

    /**
     * Determine the parameter type (single, multi, none) for a given annotation.
     * @return "none" for MarkerAnnotationExpr, "single" for SingleMemberAnnotationExpr, "multi" for NormalAnnotationExpr
     */
    @JsonProperty(value = "annotation_param_type")
    public String annotationParamType() {
        if (annotation instanceof MarkerAnnotationExpr) {
            return "none";
        } else if (annotation instanceof SingleMemberAnnotationExpr) {
            return "single";
        } else if (annotation instanceof NormalAnnotationExpr) {
            return "multi";
        } else {
            return "unknown";
        }
    }

    @Override
    public void accept(IComponentVisitor visitor) {

    }
}



