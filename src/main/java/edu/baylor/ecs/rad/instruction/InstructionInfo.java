package edu.baylor.ecs.rad.instruction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class wraps a bytecode instruction along with its opcode and position.
 *
 * @author Dipta Das
 */

@Getter
@Setter
@AllArgsConstructor
@ToString
public class InstructionInfo {
    private int pos;
    private String opcode;
    private Object instruction;
}
