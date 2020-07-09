package edu.baylor.ecs.rad.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the result after performing REST API discovery.
 * It wraps the {@link RequestContext},
 * a list of {@link RestEntityContext},
 * and the {@link RestFlowContext}.
 *
 * @author Dipta Das
 */

@Getter
@Setter
@ToString
public class ResponseContext {
    private RequestContext request;
    private List<RestEntityContext> restEntityContexts = new ArrayList<>();
    private RestFlowContext restFlowContext;
}
