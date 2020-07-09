package edu.baylor.ecs.msanose.model.hardcodedEndpoint;

import edu.baylor.ecs.rad.model.RestEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class HardcodedEndpoint {
    private RestEntity restEntity;
    private HardcodedEndpointType type;
}
