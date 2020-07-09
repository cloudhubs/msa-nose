package edu.baylor.ecs.msanose.model.context;

import edu.baylor.ecs.msanose.model.persistency.DatabaseType;
import edu.baylor.ecs.msanose.model.standards.BusinessType;
import edu.baylor.ecs.msanose.model.standards.PresentationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@NoArgsConstructor @AllArgsConstructor
@Data public class TooManyStandardsContext {
    Set<PresentationType> presentationTypes;
    Set<BusinessType> businessTypes;
    Set<DatabaseType> dataTypes;
}
