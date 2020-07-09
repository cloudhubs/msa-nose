package edu.baylor.ecs.jparser.component;

import edu.baylor.ecs.jparser.visitor.IComponentVisitor;

public interface IComponent {

    void accept(IComponentVisitor visitor);

}
