package edu.baylor.ecs.rad.analyzer;

import edu.baylor.ecs.rad.model.HttpMethod;
import edu.baylor.ecs.rad.model.RestEntity;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class detects Spring Boot RestTemplate client calls.
 * It can detect the return type of a rest call using return type of the wrapper method.
 * It can not detect the url of a rest call.
 * It constructs a list of {@link edu.baylor.ecs.rad.model.RestEntity} of rest clients.
 *
 * @author Dipta Das
 */

@Component
public class SpringClientWrapperAnalyzer {
    private static final RestTemplateMethod[] restTemplateMethods = {
            new RestTemplateMethod("getForObject", HttpMethod.GET),
            new RestTemplateMethod("postForObject", HttpMethod.POST),
            new RestTemplateMethod("deleteForObject", HttpMethod.DELETE),
    };
    private static final String restTemplateClass = "org.springframework.web.client.RestTemplate";
    private static final String ribbonClientAnnotation = "org.springframework.cloud.netflix.ribbon.RibbonClient";

    public List<RestEntity> getRestEntity(CtClass ctClass) {
        List<RestEntity> restEntities = new ArrayList<>();

        // get ribbon server name if specified
        String ribbonServerName = getRibbonServerName(ctClass);

        for (CtMethod ctMethod : ctClass.getMethods()) {
            RestTemplateMethod foundMethod = findCaller(ctMethod);
            if (foundMethod != null) {
                RestEntity restEntity = new RestEntity();
                restEntity.setClient(true);
                restEntity.setHttpMethod(foundMethod.httpMethod);

                // add class and method signatures
                restEntity.setClassName(ctClass.getName());
                restEntity.setMethodName(ctMethod.getName());
                restEntity.setReturnType(Helper.getReturnType(ctMethod));

                // add ribbon server name
                restEntity.setRibbonServerName(ribbonServerName);

                restEntities.add(restEntity);
            }
        }
        return restEntities;
    }

    private String getRibbonServerName(CtClass ctClass) {
        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.getTypeName().equals(ribbonClientAnnotation)) {
                    if (Helper.getAnnotationValue(annotation, "name") != null) {
                        return Helper.getAnnotationValue(annotation, "name");
                    }
                }
            }
        }
        return null;
    }

    private RestTemplateMethod findCaller(CtMethod method) {
        // Instrument the method to pull out the method calls
        // Then filter method calls by methodName and className
        final RestTemplateMethod[] foundMethod = {null};
        try {
            method.instrument(
                    new ExprEditor() {
                        public void edit(MethodCall m) {
                            for (RestTemplateMethod targetMethod : restTemplateMethods) {
                                if (m.getClassName().equals(restTemplateClass) && m.getMethodName().equals(targetMethod.restTemplateMethod)) {
                                    foundMethod[0] = targetMethod;
                                    break;
                                }
                            }
                        }
                    }
            );
        } catch (CannotCompileException e) {
            System.err.println(e.toString());
        }

        return foundMethod[0];
    }

    @AllArgsConstructor
    private static class RestTemplateMethod {
        String restTemplateMethod;
        HttpMethod httpMethod;
    }
}
