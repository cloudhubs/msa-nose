package edu.baylor.ecs.msanose.model.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data public class ESBContext {
    List<MicroserviceContext> candidateESBs;

    public ESBContext(){
        this.candidateESBs = new ArrayList<>();
    }
}
