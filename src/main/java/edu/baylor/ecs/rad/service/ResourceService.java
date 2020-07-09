package edu.baylor.ecs.rad.service;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.ClassFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The ResourceService service exposes many helper methods for managing loading resources
 * from the files contained within a jar. The service exposes three methods for public use,
 * {@link ResourceService#getResourcePaths(String)}, {@link ResourceService#getProperties(String, String)} and
 * {@link ResourceService#getCtClasses(String, String)}.
 *
 * @author  Jan Svacina
 * @version 1.1
 * @since   0.3.0
 */
@Service
public class ResourceService {

    // Loader in charge of loading JAR files and ClassFiles
    private final ResourceLoader resourceLoader;

    /**
     * Constructor for {@link ResourceService} which injects a {@link ResourceLoader}
     *
     * @param resourceLoader a loader for managing resources in the project
     */
    public ResourceService(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    /**
     * This method returns a {@link List} of {@link String} objects, each of which represents
     * a JAR or WAR file in the prject directory to be analyzed. This method is an entry point
     * for the {@link ResourceService}.
     *
     * @param folderPath the root folder of the project which contains the microservices
     *
     * @return a {@link List} of {@link String} objects, each of which represents a JAR or WAR
     * file in the prject directory to be analyzed
     */
    public List<String> getResourcePaths(String folderPath){
        String directory = new File(folderPath).getAbsolutePath();
        Path start = Paths.get(directory);
        int maxDepth = 15;
        List<String> fileNames = new ArrayList<>();
        try {
            Stream<Path> stream = Files.find(start, maxDepth,
                    (path, attr) ->
                            String.valueOf(path).toLowerCase().endsWith(".jar") ||
                                    String.valueOf(path).toLowerCase().endsWith(".war"));
            fileNames = stream
                    .sorted()
                    .map(String::valueOf)
                    .filter((path) -> {
                        return (String.valueOf(path).toLowerCase().endsWith(".jar") ||
                                String.valueOf(path).toLowerCase().endsWith(".war")) &&
                                !String.valueOf(path).toLowerCase().contains("/.mvn/") &&
                                !String.valueOf(path).toLowerCase().startsWith("/usr/lib/jvm/") &&
                                !String.valueOf(path).toLowerCase().contains("/target/dependency/") &&
                                !String.valueOf(path).toLowerCase().contains("/gradle") &&
                                !String.valueOf(path).toLowerCase().contains("\\.mvn\\") &&
                                !String.valueOf(path).toLowerCase().contains("\\target\\dependency") &&
                                !String.valueOf(path).toLowerCase().contains("document") &&
                                !String.valueOf(path).toLowerCase().contains("\\gradle");
                    })
                    .collect(Collectors.toList());
        } catch(Exception e){
            e.printStackTrace();
        }
        return fileNames;
    }

    public List<String> getPackageJsons(String folderPath){
        String directory = new File(folderPath).getAbsolutePath();
        Path start = Paths.get(directory);
        int maxDepth = 15;
        List<String> fileNames = new ArrayList<>();
        try {
            Stream<Path> stream = Files.find(start, maxDepth,
                    (path, attr) ->
                            String.valueOf(path).toLowerCase().endsWith(".json"));
            fileNames = stream
                    .sorted()
                    .map(String::valueOf)
                    .filter((path) -> (
                            String.valueOf(path).toLowerCase().equals("package.json") &&
                            !String.valueOf(path).toLowerCase().contains("/node_modules/") &&
                            !String.valueOf(path).toLowerCase().contains("/.mvn/") &&
                            !String.valueOf(path).toLowerCase().startsWith("/usr/lib/jvm/") &&
                            !String.valueOf(path).toLowerCase().contains("/target/dependency/") &&
                            !String.valueOf(path).toLowerCase().contains("/gradle") &&
                            !String.valueOf(path).toLowerCase().contains("\\.mvn\\") &&
                            !String.valueOf(path).toLowerCase().contains("\\target\\dependency") &&
                            !String.valueOf(path).toLowerCase().contains("\\gradle")))
                    .collect(Collectors.toList());
        } catch(Exception e){
            e.printStackTrace();
        }
        return fileNames;
    }

    public List<String> getPomXML(String folderPath){
        String directory = new File(folderPath).getAbsolutePath();
        Path start = Paths.get(directory);
        int maxDepth = 15;
        List<String> fileNames = new ArrayList<>();
        try {
            Stream<Path> stream = Files.find(start, maxDepth,
                    (path, attr) ->
                            String.valueOf(path).contains("pom.xml"));
            fileNames = stream
                    .sorted()
                    .map(String::valueOf)
                    .filter((path) -> {
                        return (!String.valueOf(path).toLowerCase().contains("/.mvn/") &&
                                !String.valueOf(path).toLowerCase().startsWith("/usr/lib/jvm/") &&
                                !String.valueOf(path).toLowerCase().contains("/target/dependency/") &&
                                !String.valueOf(path).toLowerCase().contains("/gradle") &&
                                !String.valueOf(path).toLowerCase().contains("\\.mvn\\") &&
                                !String.valueOf(path).toLowerCase().contains("\\target\\dependency") &&
                                !String.valueOf(path).toLowerCase().contains("\\gradle"));
                    })
                    .collect(Collectors.toList());
        } catch(Exception e){
            e.printStackTrace();
        }

        return fileNames;
    }

    /**
     * This method returns a {@link Set} of {@link Properties} objects loaded from the properties
     * files from the microservice. This method is an entry point for the {@link ResourceService}.
     *
     * @param jarPath the path to the JAR file
     * @param organizationPath the package of the project to ensure we only get project config
     *                         files
     *
     * @return a {@link Set} of {@link Properties} objects loaded from the properties files
     * from the microservice
     */
    public Set<Properties> getProperties(String jarPath, String organizationPath) {
        Resource resource = getResource(jarPath);
        Set<Properties> properties = new HashSet<>();
        String uriString = getUriStringFromResource(resource);
        URI u = getUri(uriString);
        Path path = Paths.get(u);

        try (JarFile jar = new JarFile(path.toFile())) {
            List<JarEntry> entries = Collections.list(jar.entries());
            for (JarEntry je: entries) {
                if (isPropertiesFile(je)){
                    if (je.getName().contains("application")) {
                        Properties prop = getPropertiesFileFromJar(jar, je);
                        if (prop != null) {
                            properties.add(prop);
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public Map<String, Map<String, Object>> getYamls(String jarPath, String organizationPath) {
        Resource resource = getResource(jarPath);
        String uriString = getUriStringFromResource(resource);
        URI u = getUri(uriString);
        Path path = Paths.get(u);
        Map<String, Map<String, Object>> yamls = new HashMap<>();

        try (JarFile jar = new JarFile(path.toFile())) {
            List<JarEntry> entries = Collections.list(jar.entries());
            for (JarEntry je: entries) {
                if (isYamlFile(je)){
                    if (je.getName().contains("application")) {
                        try (InputStream in = jar.getInputStream(je)) {
                            try (DataInputStream data = new DataInputStream(in)) {
                                Yaml yaml = new Yaml();
                                yamls.put(jarPath, yaml.load(data));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return yamls;
    }

    /**
     * This method returns a {@link List} of {@link CtClass} objects loaded from a particular
     * JAR file. This method is an entry point for the {@link ResourceService}.
     *
     * @param file the path to the JAR file
     * @param organizationPath the package of the project to ensure we only get project classes
     *
     * @return a {@link List} of {@link CtClass} objects loaded from a particular JAR file
     */
    public List<CtClass> getCtClasses(String file, String organizationPath){
        ClassPool cp = ClassPool.getDefault();
        List<CtClass> ctClasses = new ArrayList<>();
        // 1. Get resource
        Resource resource = getResource(file);
        // 2. Get class files
        Set<ClassFile> classFiles = getClassFileSet(resource, organizationPath);

        // Class file to ct class
        for (ClassFile classFile : classFiles) {

            CtClass clazz = null;
            try {
                clazz = cp.makeClass(classFile);
                ctClasses.add(clazz);
            } catch (Exception e) {
                /* LOG */
                System.out.println("Failed to make class:" + e.toString());
                break;
            }
        }
        //return ct classes
        return ctClasses;
    }

    /**
     * This method returns a {@link Resource} from a file path loaded by the injected
     * {@link ResourceLoader}. This is a private helper method used by
     * {@link ResourceService#getProperties(String, String)} and by {@link ResourceService#getCtClasses(String, String)}.
     *
     * @param file the path to the file
     *
     * @return a {@link Resource} from a file path
     */
    private Resource getResource(String file){
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        if(isWindows) {
            return resourceLoader.getResource("file:/" + file);
        } else {
            return resourceLoader.getResource("file:" + file);
        }
    }

    /**
     * This method constructs a {@link Set} of {@link ClassFile} objects from the
     * loaded JAR file. This is a private helper method used by
     * {@link ResourceService#getCtClasses(String, String)}.
     *
     * @param resource the {@link Resource} representing a JAR file
     * @param organizationPath the package of the project to ensure we only get project classes
     *
     * @return a {@link Set} of {@link ClassFile} objects from the loaded JAR file
     */
    private Set<ClassFile> getClassFileSet(Resource resource, String organizationPath){

        /*
         * ToDo: Check organization path on modules layer
         */

        Set<ClassFile> classFiles = new HashSet<>();
        // 2.1
        String uriString = getUriStringFromResource(resource);
        // 2.2
        URI u = getUri(uriString);
        Path path = Paths.get(u);
        try (JarFile jar = new JarFile(path.toFile())) {
            List<JarEntry> entries = Collections.list(jar.entries());
            for (JarEntry je: entries
            ) {
                //2.3
                if (isClassFile(je)){
                    if (je.getName().contains(organizationPath)) {
                        //2.4
                        ClassFile classFile = getClassFileFromJar(jar, je);
                        if (classFile != null) {
                            classFiles.add(classFile);
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classFiles;
    }

    /**
     * This method constructs a URI {@link String} from a given {@link Resource}. This is a private
     * helper method used by {@link ResourceService#getProperties(String, String)} and by
     * {@link ResourceService#getClassFileSet(Resource, String)}.
     *
     * @param resource the {@link Resource} to extract the URI from
     *
     * @return a URI {@link String} from a given {@link Resource}
     */
    private String getUriStringFromResource(Resource resource){
        try {
            return resource.getURI().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * This method constructs a {@link URI} from a given {@link String}. This is a private
     * helper method used by {@link ResourceService#getProperties(String, String)} and by
     * {@link ResourceService#getClassFileSet(Resource, String)}.
     *
     * @param uri the {@link String} to create the URI from
     *
     * @return a {@link URI} from a given {@link String}
     */
    private URI getUri(String uri){
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method returns if a {@link JarEntry} is a class file. This is a private
     * helper method used by {@link ResourceService#getClassFileSet(Resource, String)}.
     *
     * @param entry the {@link JarEntry} to test
     *
     * @return if a {@link JarEntry} is a class file
     */
    private boolean isClassFile(JarEntry entry) {
        return entry.getName().endsWith(".class");
    }

    /**
     * This method returns if a {@link JarEntry} is a properties file. This is a private
     * helper method used by {@link ResourceService#getClassFileSet(Resource, String)}.
     *
     * @param entry the {@link JarEntry} to test
     *
     * @return if a {@link JarEntry} is a properties file
     */
    private boolean isPropertiesFile(JarEntry entry) {
        return entry.getName().endsWith(".properties");
    }

    private boolean isYamlFile(JarEntry entry) {
        return entry.getName().endsWith(".yml");
    }

    /**
     * This method returns a {@link ClassFile} from a given {@link JarFile} and a {@link JarEntry}.
     *
     * @param jar the {@link JarFile} to get the {@link InputStream} from
     * @param entry the {@link JarEntry} to extract
     *
     * @return a {@link ClassFile} from a given {@link JarFile} and a {@link JarEntry}
     */
    private ClassFile getClassFileFromJar(JarFile jar, JarEntry entry) {
        /*
         * ToDo: Do not process jars for libraries, just code!
         */
        try (InputStream in = jar.getInputStream(entry)) {
            try (DataInputStream data = new DataInputStream(in)) {
                return new ClassFile(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method returns a {@link Properties} from a given {@link JarFile} and a {@link JarEntry}.
     *
     * @param jar the {@link JarFile} to get the {@link InputStream} from
     * @param entry the {@link JarEntry} to extract
     *
     * @return a {@link Properties} from a given {@link JarFile} and a {@link JarEntry}
     */
    private Properties getPropertiesFileFromJar(JarFile jar, JarEntry entry) {
        Properties prop = null;
        try (InputStream in = jar.getInputStream(entry)) {
            try (DataInputStream data = new DataInputStream(in)) {
                prop = new Properties();
                prop.load(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            prop = null;
        }
        return prop;
    }

}
