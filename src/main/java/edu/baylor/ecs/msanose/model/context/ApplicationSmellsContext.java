package edu.baylor.ecs.msanose.model.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor @AllArgsConstructor
@Data public class ApplicationSmellsContext {
    UnversionedAPIContext unversionedAPIContext;
    HardCodedEndpointsContext hardCodedEndpointsContext;
    ESBContext esbContext;
    boolean APIGateway;
    boolean cyclicDependency;
    SharedPersistencyContext sharedPersistencyContext;
    SharedLibraryContext sharedLibraryContext;
    MicroservicesGreedyContext microservicesGreedyContext;
    WrongCutsContext wrongCutsContext;
    InappropriateServiceIntimacyContext inappropriateServiceIntimacyContext;
    TooManyStandardsContext tooManyStandardsContext;
    Map<String, Long> times;
}
