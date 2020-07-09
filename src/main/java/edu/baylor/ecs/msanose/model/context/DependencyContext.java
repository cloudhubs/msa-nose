package edu.baylor.ecs.msanose.model.context;

import edu.baylor.ecs.msanose.model.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Data public class DependencyContext {
    List<String> microservices;
    List<Pair> edges;
}
