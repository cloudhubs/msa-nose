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
 * This class performs Spring Boot RestController annotation analysis.
 * It constructs a list of {@link edu.baylor.ecs.rad.model.RestEntity} of rest endpoints.
 *
 * @author Dipta Das
 */

@Component
public class SpringAnalyzer {
    private static final String springAnnotationPrefix = "org.springframework.web.bind.annotation";

    public List<RestEntity> getRestEntity(CtClass ctClass) {
        List<RestEntity> restEntities = new ArrayList<>();

        if (!isController(ctClass)) { // not a controller, don't do further analysis
            return restEntities;
        }

        // get annotation specified in class level
        String path = null, produces = null, consumes = null;
        HttpMethod method = null;

        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.getTypeName().equals(getSpringAnnotation("RequestMapping"))) {
                    if (Helper.getAnnotationValue(annotation, "value") != null) {
                        path = Helper.getAnnotationValue(annotation, "value");
                    }
                    if (Helper.getAnnotationValue(annotation, "path") != null) { // path is alias for value
                        path = Helper.getAnnotationValue(annotation, "path");
                    }
                    if (Helper.getAnnotationValue(annotation, "method") != null) {
                        method = annotationToHttpMethod(Helper.getAnnotationValue(annotation, "method"));
                    }
                    if (Helper.getAnnotationValue(annotation, "produces") != null) {
                        produces = Helper.getAnnotationValue(annotation, "produces");
                    }
                    if (Helper.getAnnotationValue(annotation, "consumes") != null) {
                        consumes = Helper.getAnnotationValue(annotation, "consumes");
                    }
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

                // use class level properties if not specified in method level
                if (restEntity.getHttpMethod() == null) {
                    restEntity.setHttpMethod(method);
                }
                if (restEntity.getProduceType() == null) {
                    restEntity.setProduceType(produces);
                }
                if (restEntity.getConsumeType() == null) {
                    restEntity.setConsumeType(consumes);
                }

                // add class and method signatures
                restEntity.setClassName(ctClass.getName());
                restEntity.setMethodName(ctMethod.getName());
                restEntity.setReturnType(Helper.getReturnType(ctMethod));

                restEntities.add(restEntity);
            }
        }

        return restEntities;
    }

    private boolean isController(CtClass ctClass) {
        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.getTypeName().equals("org.springframework.stereotype.Controller") ||
                        annotation.getTypeName().equals(getSpringAnnotation("RestController"))) {
                    return true;
                }
            }
        }
        return false;
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

                if (annotationType.equals(getSpringAnnotation("RequestMapping"))) {
                    if (Helper.getAnnotationValue(annotation, "value") != null) {
                        restEntity.setPath(Helper.getAnnotationValue(annotation, "value"));
                    }
                    if (Helper.getAnnotationValue(annotation, "path") != null) { // path is alias for value
                        restEntity.setPath(Helper.getAnnotationValue(annotation, "path"));
                    }
                    if (Helper.getAnnotationValue(annotation, "method") != null) {
                        restEntity.setHttpMethod(annotationToHttpMethod(Helper.getAnnotationValue(annotation, "method")));
                    }
                    if (Helper.getAnnotationValue(annotation, "produces") != null) {
                        restEntity.setProduceType(Helper.getAnnotationValue(annotation, "produces"));
                    }
                    if (Helper.getAnnotationValue(annotation, "consumes") != null) {
                        restEntity.setConsumeType(Helper.getAnnotationValue(annotation, "consumes"));
                    }
                } else if (annotationToHttpMethodMapping(annotationType) != null) {
                    if (Helper.getAnnotationValue(annotation, "value") != null) {
                        restEntity.setPath(Helper.getAnnotationValue(annotation, "value"));
                    } else {
                        restEntity.setPath(Helper.getAnnotationValue(annotation, "path")); // path is alias for value
                    }
                    restEntity.setHttpMethod(annotationToHttpMethodMapping(annotationType));
                } else {
                    isRestAnnotation = false;
                }

                // true if at least one spring server annotation found
                isRestHandlerMethod = isRestHandlerMethod || isRestAnnotation;
            }
        }

        // if it is not a rest handler method, don't do further parameter analysis
        if (!isRestHandlerMethod) {
            return null;
        }

        ParameterAnnotationsAttribute parameterAnnotationsAttribute = (ParameterAnnotationsAttribute) ctMethod.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.visibleTag);
        if (parameterAnnotationsAttribute != null) {
            Annotation[][] annotationsList = parameterAnnotationsAttribute.getAnnotations();
            for (Annotation[] annotations : annotationsList) {
                String defaultValue = "";
                Param pathParam = null, queryParam = null;

                for (Annotation annotation : annotations) {
                    String annotationType = annotation.getTypeName();

                    if (annotationType.equals(getSpringAnnotation("PathVariable"))) {
                        if (Helper.getAnnotationValue(annotation, "value") != null) {
                            pathParam = new Param(Helper.getAnnotationValue(annotation, "value"));
                        } else {
                            pathParam = new Param("VARIABLE_NAME"); // TODO: get variable name
                        }

                    } else if (annotationType.equals(getSpringAnnotation("RequestParam"))) {
                        if (Helper.getAnnotationValue(annotation, "value") != null) {
                            queryParam = new Param(Helper.getAnnotationValue(annotation, "value"));
                        } else {
                            queryParam = new Param("VARIABLE_NAME"); // TODO: get variable name
                        }
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
                // TODO: formParam, headerParam, cookieParam, matrixParam
            }
        }

        return restEntity;
    }

    private HttpMethod annotationToHttpMethod(String annotation) {
        // annotation = annotation.replaceAll("[{}]", "");
        if (annotation.equals(getSpringAnnotation("RequestMethod.GET"))) {
            return HttpMethod.GET;
        } else if (annotation.equals(getSpringAnnotation("RequestMethod.POST"))) {
            return HttpMethod.POST;
        } else if (annotation.equals(getSpringAnnotation("RequestMethod.PUT"))) {
            return HttpMethod.PUT;
        } else if (annotation.equals(getSpringAnnotation("RequestMethod.DELETE"))) {
            return HttpMethod.DELETE;
        } else if (annotation.equals(getSpringAnnotation("RequestMethod.OPTIONS"))) {
            return HttpMethod.OPTIONS;
        } else if (annotation.equals(getSpringAnnotation("RequestMethod.HEAD"))) {
            return HttpMethod.HEAD;
        } else if (annotation.equals(getSpringAnnotation("RequestMethod.PATCH"))) {
            return HttpMethod.PATCH;
        } else {
            return null;
        }
    }

    private HttpMethod annotationToHttpMethodMapping(String annotation) {
        if (annotation.equals(getSpringAnnotation("GetMapping"))) {
            return HttpMethod.GET;
        } else if (annotation.equals(getSpringAnnotation("PostMapping"))) {
            return HttpMethod.POST;
        } else if (annotation.equals(getSpringAnnotation("PutMapping"))) {
            return HttpMethod.PUT;
        } else if (annotation.equals(getSpringAnnotation("DeleteMapping"))) {
            return HttpMethod.DELETE;
        } else if (annotation.equals(getSpringAnnotation("OptionsMapping"))) {
            return HttpMethod.OPTIONS;
        } else if (annotation.equals(getSpringAnnotation("HeadMapping"))) {
            return HttpMethod.HEAD;
        } else if (annotation.equals(getSpringAnnotation("PatchMapping"))) {
            return HttpMethod.PATCH;
        } else {
            return null;
        }
    }

    private String getSpringAnnotation(String suffix) {
        return springAnnotationPrefix + "." + suffix;
    }
}
