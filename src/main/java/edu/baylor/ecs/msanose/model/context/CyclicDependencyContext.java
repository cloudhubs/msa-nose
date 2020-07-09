package edu.baylor.ecs.msanose.model.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Data public class CyclicDependencyContext {
    List<DependencyContext> cycles;
}
