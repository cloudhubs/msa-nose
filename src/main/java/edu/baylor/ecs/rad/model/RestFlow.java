package edu.baylor.ecs.rad.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * This class defines REST communications between a client and a list of servers.
 *
 * @author Dipta Das
 */

@Getter
@Setter
@ToString
public class RestFlow {
    private String resourcePath;
    private String className;
    private String methodName;

    private List<RestEntity> servers;
}
