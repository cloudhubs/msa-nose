package edu.baylor.ecs.msanose;

import edu.baylor.ecs.ciljssa.component.Component;
import edu.baylor.ecs.ciljssa.component.context.AnalysisContext;
import edu.baylor.ecs.ciljssa.component.impl.AnnotationComponent;
import edu.baylor.ecs.ciljssa.component.impl.ClassComponent;
import edu.baylor.ecs.ciljssa.component.impl.MethodInfoComponent;
import edu.baylor.ecs.ciljssa.facade.JParserFacade;
import edu.baylor.ecs.ciljssa.model.AnnotationValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Runner {

    public static void main(String[] args) {

        List<String> apis = new ArrayList<>();
        String path = "C:\\Programs\\corpus-cc";
        AnalysisContext analysisContext = JParserFacade.createContextFromPath(path);
        List<ClassComponent> classes = analysisContext.getClasses();
        classes.forEach(clazz -> {
            List<AnnotationComponent> annotationComponents = clazz.getAnnotations().stream().map(Component::asAnnotationComponent).collect(Collectors.toList());
            for(AnnotationComponent annotationComponent : annotationComponents) {
                String annotation = annotationComponent.getAsString();
                if (annotation.matches("@RequestMapping|@RestController")) {
                    String classLevelPath =  annotationComponent.getAnnotationValue() == null ? "" : annotationComponent.getAnnotationValue();
                    List<MethodInfoComponent> methods = clazz.getMethods().stream().map(Component::asMethodInfoComponent).collect(Collectors.toList());
                    for(MethodInfoComponent method : methods){
                        List<AnnotationComponent> methodAnnotationComponents = method.getAnnotations().stream().map(Component::asAnnotationComponent).collect(Collectors.toList());
                        for(AnnotationComponent methodAnnotationComponent : methodAnnotationComponents) {
                            if (methodAnnotationComponent.getAsString().contains("Mapping")) {
                                String methodLevelPath =  methodAnnotationComponent.getAnnotationValue();
                                if(methodLevelPath == null) {
                                    List<AnnotationValuePair> annotationValuePairList = methodAnnotationComponent.getAnnotationValuePairList();
                                    for (AnnotationValuePair valuePair : annotationValuePairList) {
                                        if (valuePair.getKey().equals("path")) {
                                            apis.add((classLevelPath + valuePair.getValue()).replace("\"", ""));
                                        }
                                    }
                                } else {
                                    apis.add((classLevelPath + methodLevelPath).replace("\"", ""));
                                }
                            }
                        }
                    }
                }
            }
        });

        apis.forEach(api -> {
            if(!api.matches(".v[0-9]+.")){
                System.out.println("Unversioned API - " + api);
            }
        });
    }
}
