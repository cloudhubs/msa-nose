package edu.baylor.ecs.jparser.factory.methodinfo;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import edu.baylor.ecs.jparser.builder.MethodInfoBuilder;
import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.ContainerComponent;
import edu.baylor.ecs.jparser.component.impl.AnnotationComponent;
import edu.baylor.ecs.jparser.component.impl.MethodInfoComponent;
import edu.baylor.ecs.jparser.component.impl.MethodParamComponent;
import edu.baylor.ecs.jparser.factory.annotation.AnnotationFactory;
import edu.baylor.ecs.jparser.model.AccessorType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This is the class used for generating MethodInfoComponents from Java Parser MethodDeclaration objects.
 * The main entry point is "createMethodInfoWrapper(MethodDeclaration, ContainerComponent)".
 * Pass in the MethodDeclaration and the class or interface component that contains it.
 */
public class MethodInfoFactory {

    private static MethodInfoFactory INSTANCE;

    // The ID to increment
    private static long idEnumerator = 0L;
    // The mapping from Expression strings to MethodInfoComponents
    private static Map<String, MethodInfoComponent> methodInfoExpressions;
    // The mapping from method declarations to method info components
    private static Map<MethodDeclaration, MethodInfoComponent> methodInfoDeclarations;
    // The mapping from constructor declarations to method info components
    private static Map<ConstructorDeclaration, MethodInfoComponent> constructorInfoDeclarations;

    public static MethodInfoFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MethodInfoFactory();
        }
        return INSTANCE;
    }

    private MethodInfoFactory() {
        methodInfoDeclarations = new HashMap<>();
        methodInfoExpressions = new HashMap<>();
        constructorInfoDeclarations = new HashMap<>();
    }

    private long iterateId() {
        idEnumerator++;
        return idEnumerator;
    }

    public void resetIdEnumerator() {
        idEnumerator = 0L;
    }

    public MethodInfoComponent createMethodInfoWrapper(MethodDeclaration dec, ContainerComponent parent) {
        MethodInfoComponent output = new MethodInfoComponent();
        if (methodInfoDeclarations.containsKey(dec)) {
            output = methodInfoDeclarations.get(dec);
        } else {
            long id = iterateId();
            List<Component> annotations = generateAnnotations(output, dec);
            List<MethodParamComponent> parameters = generateMethodParamComponents(dec.getParameters());
            List<MethodInfoComponent> subMethods = generateSubmethods(dec);
            List<Component> subComponents = flattenSubComponentList(annotations, parameters, subMethods);
            int begin, end, count;
            begin = dec.getName().getBegin().map(x -> x.line).orElse(-1); // Get beginning line of constructor
            end = dec.getEnd().map(x -> x.line).orElse(-1); // Get final line of constructor
            count = (begin > -1 && end > -1 && end > begin) ? end - begin : -1;
            String rawSource = dec.getBody().isPresent() ? dec.getBody().get().toString() : "N/A";
            List<String> rawSourceStripped = new ArrayList<>();
            rawSourceStripped = Arrays.stream(rawSource.split("\n")).collect(Collectors.toList());
            output = new MethodInfoBuilder().withParentComponent(parent)
                    .withAccessor(AccessorType.fromString(dec.getAccessSpecifier().asString()))
                    .withAnnotations(annotations)
                    .withMethodParams(parameters)
                    .withSubMethods(subMethods)
                    .withMethodName(dec.getNameAsString())
                    .withReturnType(dec.getTypeAsString())
                    .asStaticMethod(dec.isStatic())
                    .asAbstractMethod(dec.isAbstract())
                    .withId(id)
                    .withRawSource(dec.getBody().isPresent() ? dec.getBody().get().toString() : "N/A")
                    .withStatements(dec.getBody().isPresent() ? dec.getBody().get().getStatements().stream()
                            .map(Node::toString).collect(Collectors.toList()) : new ArrayList<>())
                    .withInstanceName(parent.getInstanceName() + "::MethodInfoComponent::" + id)
                    .withPath(parent.getPath() + "::" + dec.getNameAsString())
                    .withPackageName(parent.getPackageName() + "." + dec.getNameAsString())
                    .withSubComponents(subComponents)
                    .withLineCount(count)
                    .withLineBegin(begin)
                    .withLineEnd(end)
                    .withRawSourceStripped(rawSourceStripped)
                    .build();
            methodInfoDeclarations.put(dec, output);
        }
        return output;
    }

    private List<Component> flattenSubComponentList(List<Component> annotations,
                                                          List<MethodParamComponent> parameters,
                                                          List<MethodInfoComponent> subMethods) {
        List<List<Component>> unflattenedMap = new ArrayList<>();
        unflattenedMap.add(annotations.stream().map(x -> (Component) x).collect(Collectors.toList()));
        unflattenedMap.add(parameters.stream().map(x -> (Component) x).collect(Collectors.toList()));
        unflattenedMap.add(subMethods.stream().map(x -> (Component) x).collect(Collectors.toList()));
        return unflattenedMap.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public MethodInfoComponent createMethodInfoWrapperFromConstructor(ConstructorDeclaration dec, Component parent) {
        MethodInfoComponent output = new MethodInfoComponent();
        if (constructorInfoDeclarations.containsKey(dec)) {
            output = constructorInfoDeclarations.get(dec);
        } else {
            long id = iterateId();
            List<Component> annotations = generateAnnotationsConstructor(output, dec);
            List<MethodParamComponent> parameters = generateMethodParamComponents(dec.getParameters());
            List<MethodInfoComponent> subMethods = generateSubmethodsConstructor(dec);
            List<Component> subComponents = flattenSubComponentList(annotations, parameters, subMethods);
            int begin, end, count;
            begin = dec.getName().getBegin().map(x -> x.line).orElse(-1); // Get beginning line of constructor
            end = dec.getName().getEnd().map(x -> x.line).orElse(-1); // Get final line of constructor
            count = (begin > -1 && end > -1 && end > begin) ? end - begin : -1;
            output = new MethodInfoBuilder().withParentComponent(parent)
                    .withAccessor(AccessorType.fromString(dec.getAccessSpecifier().asString()))
                    .withAnnotations(annotations)
                    .withMethodParams(parameters)
                    .withSubMethods(subMethods)
                    .withMethodName(dec.getNameAsString())
                    .withReturnType("N/A")
                    .asStaticMethod(dec.isStatic())
                    .asAbstractMethod(dec.isAbstract())
                    .withId(id)
                    .withRawSource(dec.getBody().toString())
                    .withStatements(dec.getBody().getStatements().stream()
                            .map(Node::toString).collect(Collectors.toList()))
                    .withInstanceName(parent.getInstanceName() + "::MethodInfoComponent::" + id)
                    .withPath(parent.getPath() + "::" + dec.getNameAsString())
                    .withPackageName(parent.getPackageName() + "." + dec.getNameAsString())
                    .withSubComponents(subComponents)
                    .withLineCount(count)
                    .withLineBegin(begin)
                    .withLineEnd(end)
                    .build();
            constructorInfoDeclarations.put(dec, output);
        }
        return output;
    }

    private List<MethodParamComponent> generateMethodParamComponents(List<Parameter> parameters) {
        List<MethodParamComponent> output = new ArrayList<>();
        for (Parameter p : parameters) {
            MethodParamComponent curr = new MethodParamComponent();
            //curr.setParameterType(p.getTypeAsString());
            curr.setParameterName(p.getName().getIdentifier());
            curr.setType(p.getClass());
            if (p.toString().startsWith("@")) {
                AnnotationComponent comp = new AnnotationComponent();
                curr.setAnnotation(AnnotationFactory.createAnnotationFromString(curr, p.toString().substring(p.toString().indexOf(' ') + 1)));
            }
            output.add(curr);
        }
        return output;
    }

    private List<Component> generateAnnotationsConstructor(Component parent, ConstructorDeclaration dec) {
        return AnnotationFactory.createAnnotationComponents(parent, dec.getAnnotations());
    }

    private List<Component> generateAnnotations(Component parent, MethodDeclaration dec) {
        return AnnotationFactory.createAnnotationComponents(parent, dec.getAnnotations());
    }

    private List<MethodInfoComponent> generateSubmethodsConstructor(ConstructorDeclaration dec) {
        List<MethodInfoComponent> subCalls = new ArrayList<>();
        dec.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodCallExpr n, Object arg) {
                super.visit(n, arg);
                subCalls.add(createMethodInfoWrapperFromExpr(n));
            }
        }, null);
        return subCalls;
    }

    private List<MethodInfoComponent> generateSubmethods(MethodDeclaration dec) {
        List<MethodInfoComponent> subCalls = new ArrayList<>();
        dec.accept(new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodCallExpr n, Object arg) {
                super.visit(n, arg);
                subCalls.add(createMethodInfoWrapperFromExpr(n));
            }
        }, null);
        return subCalls;
    }

    /**
     * Subroutine creation is handled here.
     * @param call
     * @return
     */
    private MethodInfoComponent createMethodInfoWrapperFromExpr(MethodCallExpr call) {
        if (call == null)
            return null;
        MethodInfoComponent out = new MethodInfoComponent();
        if (methodInfoExpressions.containsKey(call.getNameAsString())) {
            out = methodInfoExpressions.get(call.getNameAsString());
        } else {
            List<MethodParamComponent> arguments = new ArrayList<>();
            call.getArguments().forEach(x -> {
                MethodParamComponent param = new MethodParamComponent(); //TODO: Doesn't work for expr
                //param.setType(x.calculateResolvedType().getClass()); //TODO: Always parameter object
                param.setParameterType(x.toString());
                arguments.add(param);
            });
            out.setMethodName(call.getNameAsExpression().getName().toString());
            out.setMethodParams(arguments);
            out.setId(iterateId());
            methodInfoExpressions.put(call.getNameAsString(), out);
        }
        return out;
    }
}
