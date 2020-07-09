package edu.baylor.ecs.msanose.model.wrongCuts;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityPair {
    private String path;
    private int entityCount;
}
