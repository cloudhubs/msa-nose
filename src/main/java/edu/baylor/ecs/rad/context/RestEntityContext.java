package edu.baylor.ecs.rad.context;

import edu.baylor.ecs.rad.model.RestEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class wraps the resource path with a list of {@link edu.baylor.ecs.rad.model.RestEntity}
 * resulted after performing the REST API discovery analysis for that specific resource.
 *
 * @author Dipta Das
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RestEntityContext {
    private String resourcePath;
    private List<RestEntity> restEntities = new ArrayList<>();
}
