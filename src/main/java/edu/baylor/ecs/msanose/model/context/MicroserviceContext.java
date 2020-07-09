package edu.baylor.ecs.msanose.model.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Data public class MicroserviceContext {
    String name;
    List<String> inputs;
    List<String> outputs;

    public MicroserviceContext(String name){
        this.name = name;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }
}
