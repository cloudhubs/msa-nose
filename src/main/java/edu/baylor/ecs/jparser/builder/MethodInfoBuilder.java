package edu.baylor.ecs.jparser.builder;

import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.impl.MethodInfoComponent;
import edu.baylor.ecs.jparser.component.impl.MethodParamComponent;
import edu.baylor.ecs.jparser.model.AccessorType;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class MethodInfoBuilder {

    private long id;
    private int lineCount;
    private int lineBegin;
    private int lineEnd;
    private String path;
    private String rawSource;
    private String methodName;
    private String returnType;
    private String packageName;
    private String instanceName;
    private boolean staticMethod;
    private AccessorType accessor;
    private boolean abstractMethod;
    private List<String> statements;
    private List<String> rawSourceStripped;
    private Component parentComponent;
    private List<Component> subComponents;
    private List<MethodParamComponent> methodParams;
    private List<MethodInfoComponent> subMethods;
    private List<Component> annotations; // Method annotations

    /**
     * Creates a MethodInfoComponent based on the parameters passed to the builder.
     * InstanceType is automatically handled in the constructor of MethodInfoComponent so it is not listed here.
     * @return A MethodInfoComponent
     */
    public MethodInfoComponent build() {
        MethodInfoComponent output = new MethodInfoComponent();
        output.setId(this.id);
        output.setPath(this.path);
        output.setAccessor(this.accessor);
        output.setRawSource(this.rawSource);
        output.setMethodName(this.methodName);
        output.setSubMethods(this.subMethods);
        output.setReturnType(this.returnType);
        output.setStatements(this.statements);
        output.setParent(this.parentComponent);
        output.setAnnotations(this.annotations);
        output.setPackageName(this.packageName);
        output.setMethodParams(this.methodParams);
        output.setStaticMethod(this.staticMethod);
        output.setInstanceName(this.instanceName);
        output.setSubComponents(this.subComponents);
        output.setAbstractMethod(this.abstractMethod);
        output.setLineBegin(this.lineBegin);
        output.setLineEnd(this.lineEnd);
        output.setLineCount(this.lineCount);
        output.setRawSourceStripped(this.rawSourceStripped);
        return output;
    }

    public MethodInfoBuilder withRawSourceStripped(List<String> rss) {
        this.rawSourceStripped = rss;
        return this;
    }

    public MethodInfoBuilder withLineCount(int count) {
        this.lineCount = count;
        return this;
    }

    public MethodInfoBuilder withLineBegin(int begin) {
        this.lineBegin = begin;
        return this;
    }

    public MethodInfoBuilder withLineEnd(int end) {
        this.lineEnd = end;
        return this;
    }

    public MethodInfoBuilder withInstanceName(String name) {
        this.instanceName = name;
        return this;
    }

    public MethodInfoBuilder withPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public MethodInfoBuilder withPath(String path) {
        this.path = path;
        return this;
    }

    public MethodInfoBuilder withSubComponents(List<Component> subComponents) {
        this.subComponents = subComponents;
        return this;
    }

    public MethodInfoBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public MethodInfoBuilder withStatements(List<String> statements) {
        this.statements = statements;
        return this;
    }

    public MethodInfoBuilder withMethodName(String name) {
        this.methodName = name;
        return this;
    }

    public MethodInfoBuilder withReturnType(String type) {
        this.returnType = type;
        return this;
    }

    public MethodInfoBuilder withAccessor(AccessorType acc) {
        this.accessor = acc;
        return this;
    }

    public MethodInfoBuilder asStaticMethod(boolean b) {
        this.staticMethod = b;
        return this;
    }

    public MethodInfoBuilder asAbstractMethod(boolean b) {
        this.abstractMethod = b;
        return this;
    }

    public MethodInfoBuilder withMethodParams(List<MethodParamComponent> methodParams) {
        this.methodParams = methodParams;
        return this;
    }

    public MethodInfoBuilder withAnnotations(List<Component> anno) {
        this.annotations = anno;
        return this;
    }

    public MethodInfoBuilder withSubMethods(List<MethodInfoComponent> sub) {
        this.subMethods = sub;
        return this;
    }

    public MethodInfoBuilder withParentComponent(Component wrap) {
        this.parentComponent = wrap;
        return this;
    }

    public MethodInfoBuilder withRawSource(String source) {
        this.rawSource = source;
        return this;
    }

}
