package edu.baylor.ecs.rad.analyzer;

import edu.baylor.ecs.rad.model.HttpMethod;
import edu.baylor.ecs.rad.model.Param;
import edu.baylor.ecs.rad.model.RestEntity;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class performs JAX RS annotation analysis and micro-profile client analysis.
 * It constructs a list of {@link edu.baylor.ecs.rad.model.RestEntity}.
 *
 * @author Dipta Das
 */

@Component
public class JaxRsAnalyzer {
    static final String jaxRsAnnotationPrefix = "javax.ws.rs";
    static final String mpRegisterControllerAnnotation = "org.eclipse.microprofile.rest.client.inject.RegisterRestClient";

    public List<RestEntity> getRestEntity(CtClass ctClass) {
        List<RestEntity> restEntities = new ArrayList<>();

        // get annotation specified in class level
        String path = null;
        boolean isClient = false;

        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.getTypeName().equals(mpRegisterControllerAnnotation)) {
                    isClient = true;
                } else if (annotation.getTypeName().equals(getJaxRsAnnotation("Path"))) {
                    path = Helper.getAnnotationValue(annotation, "value");
                }
            }
        }

        for (CtMethod ctMethod : ctClass.getMethods()) {
            RestEntity restEntity = analyseMethod(ctMethod);
            if (restEntity != null) {
                // append class level path
                if (path != null) {
                    if (restEntity.getPath() == null) {
                        restEntity.setPath(path);
                    } else {
                        restEntity.setPath(Helper.mergePaths(path, restEntity.getPath()));
                    }
                }

                // add class and method signatures
                restEntity.setClient(isClient);
                restEntity.setClassName(ctClass.getName());
                restEntity.setMethodName(ctMethod.getName());
                restEntity.setReturnType(Helper.getReturnType(ctMethod));

                restEntities.add(restEntity);
            }
        }

        return restEntities;
    }

    private RestEntity analyseMethod(CtMethod ctMethod) {
        RestEntity restEntity = new RestEntity();

        boolean isRestHandlerMethod = false;

        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctMethod.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                String annotationType = annotation.getTypeName();
                boolean isRestAnnotation = true;

                if (annotationType.equals(getJaxRsAnnotation("Path"))) {
                    restEntity.setPath(Helper.getAnnotationValue(annotation, "value"));
                } else if (annotationType.equals(getJaxRsAnnotation("Produces"))) {
                    restEntity.setProduceType(Helper.getAnnotationValue(annotation, "value"));
                } else if (annotationType.equals(getJaxRsAnnotation("Consumes"))) {
                    restEntity.setConsumeType(Helper.getAnnotationValue(annotation, "value"));
                } else if (annotationToHttpMethod(annotationType) != null) {
                    restEntity.setHttpMethod(annotationToHttpMethod(annotationType));
                } else { // not JAX-RS annotation
                    isRestAnnotation = false;
                }

                // true if at least one JAX-RS annotation found
                isRestHandlerMethod = isRestHandlerMethod || isRestAnnotation;
            }
        }

        // if it is not a rest handler method, don't do further parameter analysis
        if (!isRestHandlerMethod) {
            return null;
        }

        // a class annotated with: @Path("widgets/{id}")
        // can have methods annotated whose arguments are annotated with @PathParam("id")

        ParameterAnnotationsAttribute parameterAnnotationsAttribute = (ParameterAnnotationsAttribute) ctMethod.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.visibleTag);
        if (parameterAnnotationsAttribute != null) {
            Annotation[][] annotationsList = parameterAnnotationsAttribute.getAnnotations();
            for (Annotation[] annotations : annotationsList) {
                String defaultValue = "";
                Param pathParam = null, queryParam = null;

                for (Annotation annotation : annotations) {
                    String annotationType = annotation.getTypeName();

                    if (annotationType.equals(getJaxRsAnnotation("PathParam"))) {
                        pathParam = new Param(Helper.getAnnotationValue(annotation, "value"));
                    } else if (annotationType.equals(getJaxRsAnnotation("QueryParam"))) {
                        queryParam = new Param(Helper.getAnnotationValue(annotation, "value"));
                    } else if (annotationType.equals(getJaxRsAnnotation("DefaultValue"))) {
                        defaultValue = Helper.getAnnotationValue(annotation, "value");
                    }
                }

                if (pathParam != null) {
                    pathParam.setDefaultValue(defaultValue);
                    restEntity.addPathParam(pathParam);
                }
                if (queryParam != null) {
                    queryParam.setDefaultValue(defaultValue);
                    restEntity.addQueryParam(queryParam);
                }
            }
        }

        return restEntity;
    }

    private HttpMethod annotationToHttpMethod(String annotation) {
        if (annotation.equals(getJaxRsAnnotation("GET"))) {
            return HttpMethod.GET;
        } else if (annotation.equals(getJaxRsAnnotation("POST"))) {
            return HttpMethod.POST;
        } else if (annotation.equals(getJaxRsAnnotation("PUT"))) {
            return HttpMethod.PUT;
        } else if (annotation.equals(getJaxRsAnnotation("DELETE"))) {
            return HttpMethod.DELETE;
        } else if (annotation.equals(getJaxRsAnnotation("OPTIONS"))) {
            return HttpMethod.OPTIONS;
        } else if (annotation.equals(getJaxRsAnnotation("HEAD"))) {
            return HttpMethod.HEAD;
        } else if (annotation.equals(getJaxRsAnnotation("PATCH"))) {
            return HttpMethod.PATCH;
        } else {
            return null;
        }
    }

    public String getJaxRsAnnotation(String suffix) {
        return jaxRsAnnotationPrefix + "." + suffix;
    }
}
