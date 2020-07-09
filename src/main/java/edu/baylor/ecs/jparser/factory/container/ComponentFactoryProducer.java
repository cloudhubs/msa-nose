package edu.baylor.ecs.jparser.factory.container;

import edu.baylor.ecs.jparser.factory.container.impl.ClassComponentFactory;
import edu.baylor.ecs.jparser.factory.container.impl.InterfaceComponentFactory;
import edu.baylor.ecs.jparser.factory.container.impl.ModuleComponentFactory;
import edu.baylor.ecs.jparser.model.ContainerType;

@Deprecated
public class ComponentFactoryProducer {

    public static AbstractContainerFactory getFactory(ContainerType coi) {
        switch(coi) {
            case CLASS: return ClassComponentFactory.getInstance();
            case INTERFACE: return InterfaceComponentFactory.getInstance();
            case MODULE: return ModuleComponentFactory.getInstance();
            default: return null;
        }
    }

}
