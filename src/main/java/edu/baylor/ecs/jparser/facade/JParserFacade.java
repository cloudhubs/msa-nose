package edu.baylor.ecs.jparser.facade;

import edu.baylor.ecs.jparser.component.Component;
import edu.baylor.ecs.jparser.component.context.AnalysisContext;
import edu.baylor.ecs.jparser.component.impl.DirectoryComponent;
import edu.baylor.ecs.jparser.component.impl.ModuleComponent;
import edu.baylor.ecs.jparser.factory.container.impl.ModuleComponentFactory;
import edu.baylor.ecs.jparser.factory.context.AnalysisContextFactory;
import edu.baylor.ecs.jparser.factory.directory.DirectoryFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;

/**
 * Facade class to make running and using a little bit easier and intuitive.
 */
public class JParserFacade {

    // Non Abstract Types

    /**
     * Create an AnalysisContext object from the file provided. Must be a file, not directory.
     * @param path Path of file needing analysis
     * @return An AnalysisContext object containing the results
     */
    public static AnalysisContext createContextFromFile(String path) {
        try {
            validateNotDirectory(path);
            return new AnalysisContextFactory().createAnalysisContextFromFile(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create an AnalysisContext object from the file provided. Must be a file, not directory.
     * @param file Path of file needing analysis
     * @return An AnalysisContext object containing the results
     */
    public static AnalysisContext createContextFromFile(File file) {
        if (file == null)
            return null;
        return createContextFromFile(file.getPath());
    }

    /**
     * Create an AnalysisContext object from the path of the directory provided. Must be a directory
     * @param path Path of directory needing analysis
     * @return An AnalysisContext object containing the results
     */
    public static AnalysisContext createContextFromPath(String path) {
        DirectoryComponent dir = createDirectoryComponentFromPath(path);
        return new AnalysisContextFactory().createAnalysisContextFromDirectoryGraph(dir);
    }

    /**
     * Create a ModuleComponent object from the path of the directory provided. Must be a directory,
     * there can not be a module of just a file.
     * @param path Path of directory needing analysis
     * @return A ModuleComponent object acting as the root of the tree for Modules given from the path
     */
    public static ModuleComponent createModuleGraphFromPath(String path) {
        DirectoryComponent dir = createDirectoryComponentFromPath(path);
        return ModuleComponentFactory.getInstance().createComponent(null, dir);
    }

    /**
     * Creates a DirectoryComponent object from the path of a file. This is to wrap behavior in other factories,
     * since the AnalysisContextFactory and subsequent classes require a DirectoryComponent input instead of a path.
     * @param path Path of the file to create a directory out of
     * @return a DirectoryComponent object serving as the root of the tree, containing a single leaf.
     */
    public static DirectoryComponent createDirectoryComponentFromFile(String path) {
        try {
            validateNotDirectory(path);
            return new DirectoryFactory().createDirectoryGraphOfFile(new File(path)).asDirectoryComponent();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a DirectoryComponent object from the path of a file. This is to wrap behavior in other factories,
     * since the AnalysisContextFactory and subsequent classes require a DirectoryComponent input instead of a path.
     * @param file The file to create a directory out of
     * @return a DirectoryComponent object serving as the root of the tree, containing a single leaf.
     */
    public static DirectoryComponent createDirectoryComponentFromFile(File file) {
        if (file == null)
            return null;
        return createDirectoryComponentFromFile(file.getPath());
    }

    /**
     * Creates a DirectoryComponent object from the path of a directory. Input must be a path to a directory.
     * @param path Path of the directory.
     * @return a DirectoryComponent object serving as the root of the tree.
     */
    public static DirectoryComponent createDirectoryComponentFromPath(String path) {
        try {
            validateDirectory(path);
            return new DirectoryFactory().createDirectoryGraph(path).asDirectoryComponent();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Abstract Types

    /**
     * Creates a Component object from the path of a file. This is to wrap behavior in other factories,
     * since the AnalysisContextFactory and subsequent classes require a DirectoryComponent input instead of a path.
     * @param path Path of the file to create a directory out of
     * @return a DirectoryComponent object serving as the root of the tree, containing a single leaf.
     */
    public static Component createDirectoryComponentFromFileAsComponent(String path) {
        try {
            validateNotDirectory(path);
            return new DirectoryFactory().createDirectoryGraphOfFile(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a Component object from the path of a file. This is to wrap behavior in other factories,
     * since the AnalysisContextFactory and subsequent classes require a DirectoryComponent input instead of a path.
     * @param file The file to create a directory out of
     * @return a DirectoryComponent object serving as the root of the tree, containing a single leaf.
     */
    public static Component createDirectoryComponentFromFileAsComponent(File file) {
        if (file == null)
            return null;
        return createDirectoryComponentFromFileAsComponent(file.getPath());
    }

    /**
     * Creates a Component object from the path of a directory. Input must be a path to a directory.
     * @param path Path of the directory.
     * @return a DirectoryComponent object serving as the root of the tree.
     */
    public static Component createDirectoryComponentFromPathAsComponent(String path) {
        try {
            validateDirectory(path);
            return new DirectoryFactory().createDirectoryGraph(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Service Methods

    /**
     * Service method to validate that the paths taken in by methods are a directory as needed.
     * @param path Path to check
     * @throws FileNotFoundException Thrown if the path does not point to a directory
     */
    private static void validateDirectory(String path) throws FileNotFoundException {
        File file = new File(path);
        if (!file.isDirectory())
            throw new FileNotFoundException("Directory expected, file supplied");
    }

    /**
     * Service method to validate that the paths taken in by methods are not a directory as needed.
     * @param path Path of file to check
     * @throws FileNotFoundException Thrown if the path points to a directory or file could not be found.
     * @throws FileSystemException Thrown if the file in question is not a .java file.
     */
    private static void validateNotDirectory(String path) throws FileNotFoundException, FileSystemException {
        File file = new File(path);
        if (!file.getName().endsWith("java"))
            throw new FileSystemException("File supplied was not a Java sourcecode file");
        if (!file.exists())
            throw new FileNotFoundException("Could not find file from path");
        if (file.isDirectory())
            throw new FileNotFoundException("File expected, directory supplied");
    }

}
