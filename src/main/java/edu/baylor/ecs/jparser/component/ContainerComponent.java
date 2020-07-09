package edu.baylor.ecs.jparser.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.javaparser.ast.body.MethodDeclaration;
import edu.baylor.ecs.jparser.model.AccessorType;
import edu.baylor.ecs.jparser.model.ContainerStereotype;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ContainerComponent extends Component {

    protected Long id;
    protected AccessorType accessor;
    protected ContainerStereotype stereotype;
    protected List<Component> methods;
    protected String containerName;
    protected int lineCount;
    protected String rawSource;

    @JsonIgnore
    protected List<MethodDeclaration> methodDeclarations;

}
