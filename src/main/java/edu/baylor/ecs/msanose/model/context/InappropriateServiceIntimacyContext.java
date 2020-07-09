package edu.baylor.ecs.msanose.model.context;

import edu.baylor.ecs.msanose.model.SharedIntimacy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data public class InappropriateServiceIntimacyContext {

    private List<SharedIntimacy> sharedIntimacies;
    private int count;

    public InappropriateServiceIntimacyContext(){
        this.sharedIntimacies = new ArrayList<>();
        this.count = 0;
    }

    public void addSharedIntimacy(SharedIntimacy sharedIntimacy){
        this.sharedIntimacies.add(sharedIntimacy);
        this.count++;
    }
}
