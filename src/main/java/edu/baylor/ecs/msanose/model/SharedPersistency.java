package edu.baylor.ecs.msanose.model;

import edu.baylor.ecs.msanose.model.persistency.DatabaseInstance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
@Data public class SharedPersistency {
    String msaA;
    String msaB;
    DatabaseInstance persistency;
}
