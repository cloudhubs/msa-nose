package edu.baylor.ecs.rad.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class defines a request to perform REST API discovery.
 * It consists path to compiled JAVA artifacts, organization path
 * and output path to generate rest flow graph.
 *
 * @author Dipta Das
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RequestContext {
    private String pathToCompiledMicroservices;
    private String organizationPath;
    private String outputPath;
}
