package edu.baylor.ecs.jparser.factory.container;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.ContainerComponent;
import edu.baylor.ecs.jparser.component.impl.MethodInfoComponent;
import edu.baylor.ecs.jparser.factory.annotation.AnnotationFactory;
import edu.baylor.ecs.jparser.factory.methodinfo.MethodInfoFactory;
import edu.baylor.ecs.jparser.model.ContainerStereotype;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContainerFactory implements IContainerFactory {

    private static Long idEnumerator = 0L;

    protected Long getId() {
        idEnumerator++;
        return idEnumerator;
    }

    /**
     * Should only be called if needing to reset enumerator when running
     */
    public void resetIdEnumerator() {
        idEnumerator = 0L;
    }

    protected ContainerStereotype createStereotype(ClassOrInterfaceDeclaration cls) {
        return ContainerStereotype.FABRICATED;
    }

    protected List<Component> createMethods(ClassOrInterfaceDeclaration cls, ContainerComponent parent) {
        List<Component> mds = new ArrayList<>();
        if (!cls.isInterface()) {
            List<MethodDeclaration> consts = cls.getMethods();
            consts.forEach(x -> {
                MethodInfoComponent wrap = MethodInfoFactory.getInstance().createMethodInfoWrapper(x, parent);
                mds.add(wrap);
            });
        }
        return mds;
    }

    protected List<Component> createConstructors(ClassOrInterfaceDeclaration cls, Component parent) {
        List<Component> mds = new ArrayList<>();
        if (!cls.isInterface()) {
            List<ConstructorDeclaration> consts = cls.getConstructors();
            consts.forEach(x -> {
                MethodInfoComponent wrap = MethodInfoFactory.getInstance()
                        .createMethodInfoWrapperFromConstructor(x, parent);
                mds.add(wrap);
            });
        }
        return mds;
    }

    protected List<Component> initAnnotations(Component parent, ClassOrInterfaceDeclaration cls) {
        return AnnotationFactory.createAnnotationComponents(parent, cls.getAnnotations());
    }

}
