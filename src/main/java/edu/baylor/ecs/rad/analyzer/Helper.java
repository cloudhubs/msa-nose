package edu.baylor.ecs.rad.analyzer;

import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.annotation.Annotation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

/**
 * This class contains several static helper methods.
 *
 * @author Dipta Das
 */

@Slf4j
public class Helper {
    public static String mergePaths(String classPath, String methodPath) {
        if (classPath.startsWith("/")) classPath = classPath.substring(1);
        if (methodPath.startsWith("/")) methodPath = methodPath.substring(1);

        String path = FilenameUtils.normalizeNoEndSeparator(FilenameUtils.concat(classPath, methodPath), true);
        if (!path.startsWith("/")) path = "/" + path;

        return path;
    }

    public static String getAnnotationValue(Annotation annotation, String member) {
        if (annotation.getMemberValue(member) == null) return null;
        String value = annotation.getMemberValue(member).toString();
        // System.out.println("###" + annotation.getTypeName() + " " + member + " " + value);
        return removeEnclosedQuotations(removeEnclosedBraces(value));
    }

    public static String removeEnclosedQuotations(String s) {
        if (s != null && s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static String removeEnclosedBraces(String s) {
        if (s != null && s.length() >= 2 && s.startsWith("{") && s.endsWith("}")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static String removeEnclosedSingleQuotations(String s) {
        if (s != null && s.length() >= 2 && s.startsWith("'") && s.endsWith("'")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static boolean matchUrl(String clientUrl, String serverUrl) {
        if (clientUrl == null || serverUrl == null) return false;
        return removeAmbiguity(clientUrl).equals(removeAmbiguity(serverUrl));
    }

    public static String unifyPathVariable(String url) {
        return url.replaceAll("\\{[^{]*?}", "{var}");
    }

    public static String removeAmbiguity(String url) {
        return unifyPathVariable(url).replaceAll("[^a-zA-Z0-9]", "");
    }

    public static String mergeUrlPath(String url, String path) {
        url = Helper.removeEnclosedSingleQuotations(url);
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        if (path != null && path.length() > 1) url = url + path; // merge if path not empty
        return url;
    }

    public static String getReturnType(CtMethod method) {
        try {
            String genericSignature = method.getGenericSignature();
            String simpleName = method.getReturnType().getSimpleName();
            if (genericSignature != null && simpleName != null && simpleName.equals("List")) { // generic type
                String[] splits = genericSignature.split("java/util/List<L");
                String returnType = splits[splits.length - 1];

                returnType = returnType.replace(";>;", "");
                returnType = returnType.replaceAll("/", ".");
                returnType = "java.util.List<" + returnType + ">";

                return returnType;
            }
            return method.getReturnType().getName();
        } catch (NotFoundException e) {
            return null;
        }
    }

    public static String getFieldAnnotationValue(CtField field) {
        try {
            Value value = (Value) field.getAnnotation(Value.class);
            return value.value()
                    .replace("$", "")
                    .replace("{", "")
                    .replace("}", "");
        } catch (ClassNotFoundException | NullPointerException e) {
            log.error(field.getName() + " " + e.toString());
            return null;
        }
    }

    public static void dumpProperties(Properties properties, String resourcePath) {
        log.info("#Properties of " + resourcePath);
        if (properties != null) {
            for (Object key : properties.keySet()) {
                log.info(key + ":" + properties.get(key));
            }
        } else {
            log.error("null properties");
        }
    }
}
