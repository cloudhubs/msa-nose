package edu.baylor.ecs.msanose.model.context;

import edu.baylor.ecs.msanose.model.SharedPersistency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data public class SharedPersistencyContext {
    List<SharedPersistency> sharedPersistencies;

    public SharedPersistencyContext(){
        this.sharedPersistencies = new ArrayList<>();
    }
}
