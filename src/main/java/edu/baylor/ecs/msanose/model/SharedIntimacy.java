package edu.baylor.ecs.msanose.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
@Data public class SharedIntimacy {
    String msaA;
    String msaB;
    double similarity;
}
