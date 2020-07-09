package edu.baylor.ecs.msanose.model.context;

import edu.baylor.ecs.msanose.model.hardcodedEndpoint.HardcodedEndpoint;
import edu.baylor.ecs.msanose.model.hardcodedEndpoint.HardcodedEndpointType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data public class HardCodedEndpointsContext {

    private List<HardcodedEndpoint> hardcodedEndpoints;
    private int totalHardcodedEndpoints;
    private int totalHardcodedPorts;
    private int totalHardcodedIP;

    public HardCodedEndpointsContext(){
        this.hardcodedEndpoints = new ArrayList<>();
        this.totalHardcodedEndpoints = 0;
        this.totalHardcodedPorts = 0;
        this.totalHardcodedIP = 0;
    }

    public void addHardcodedEndpoint(HardcodedEndpoint hardcodedEndpoint){
        this.hardcodedEndpoints.add(hardcodedEndpoint);
        if(hardcodedEndpoint.getType().equals(HardcodedEndpointType.PORT)){
            this.totalHardcodedPorts++;
        }
        if(hardcodedEndpoint.getType().equals(HardcodedEndpointType.IP)){
            this.totalHardcodedIP++;
        }
        this.totalHardcodedEndpoints++;
    }
}
