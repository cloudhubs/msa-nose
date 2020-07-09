package edu.baylor.ecs.jparser.model;

public enum ModuleStereotype {
    /**
     * Only the following below are used. Bottom row is potential future expansion.
     */
    FABRICATED, CONTROLLER, SERVICE, RESPONSE, ENTITY, REPOSITORY,
    /**
     * https://pdfs.semanticscholar.org/3804/b305f34eb49c669c896cdb736d57dd6fc585.pdf
     * Interesting paper on microservice package stereotypes
     */
    BOUNDED, SPECIFICATION, CLOSURE_OF_OPERATIONS, AGGREGATION;
}
