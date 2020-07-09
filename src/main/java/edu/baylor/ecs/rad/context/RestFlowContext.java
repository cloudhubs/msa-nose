package edu.baylor.ecs.rad.context;

import edu.baylor.ecs.rad.model.RestFlow;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains a list of {@link edu.baylor.ecs.rad.model.RestFlow}
 * resulted after performing the REST flow analysis for all microservices.
 *
 * @author Dipta Das
 */

@Getter
@ToString
public class RestFlowContext {
    private List<RestFlow> restFlows = new ArrayList<>();
}
