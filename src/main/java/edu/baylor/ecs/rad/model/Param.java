package edu.baylor.ecs.rad.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class generalizes several HTTP parameters.
 * This generalization includes path parameters and query parameters.
 *
 * @author Dipta Das
 */

@Getter
@Setter
@ToString
public class Param {
    private String name;
    private String defaultValue;

    public Param(String name) {
        this.name = name;
    }
}
