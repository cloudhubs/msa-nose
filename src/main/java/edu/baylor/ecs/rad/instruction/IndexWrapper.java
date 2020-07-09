package edu.baylor.ecs.rad.instruction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class wraps the index of bytecode instruction with instruction type and instruction value.
 *
 * @author Dipta Das
 */

@Getter
@Setter
@AllArgsConstructor
@ToString
public class IndexWrapper {
    private int index;
    private String type;
    private Object value;
}
