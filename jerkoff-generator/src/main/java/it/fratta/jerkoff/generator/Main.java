/**
 * 
 */
package it.fratta.jerkoff.generator;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.common.reflect.ClassPath;
import com.squareup.javapoet.TypeSpec;

import it.fratta.jerkoff.mongo.MongoDBDao;
import it.fratta.jerkoff.mongo.impl.MongoDBDaoImpl;
import it.fratta.jerkoff.poet.PojoCreator;
import it.fratta.jerkoff.poet.impl.PojoCreatorImpl;
import it.fratta.jerkoff.util.PropertiesUtils;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
@Mojo(name = "generate-test")
public class Main extends AbstractMojo {
    
    @Parameter( property = "generate-test.propertyfile", defaultValue = "${basedir}/src/main/resources/META-INF/generator.properties" )
    private String propertyfile;
    
    @org.apache.maven.plugins.annotations.Component
    private MavenProject project;

    /**
     * 
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        getLog().info("start main");
        try {
            getLog().info("load " + propertyfile);
            Properties prop = PropertiesUtils.loadPropertiesFromAbs(propertyfile);
            String packageToScan = PropertiesUtils.getRequiredProperty(prop,
                    PropertiesUtils.PACKAGE_TO_SCAN);
            getLog().info("Scan package: " + packageToScan);
            ClassPath classpath = ClassPath.from(getClassLoader());
            File sourcePath = new File(
                    PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.TARGET_FOLDER));
            MongoDBDao mongo = new MongoDBDaoImpl(prop);
            PojoCreator creator = new PojoCreatorImpl(prop, mongo);
            for (ClassPath.ClassInfo classInfo : classpath
                    .getTopLevelClassesRecursive(packageToScan)) {
                Class<?> clazz = classInfo.load();
                getLog().info("Creating tests for class " + clazz.getSimpleName());
                TypeSpec classTest = creator.getTypeSpec(clazz);
                creator.writeJavaFile(sourcePath, clazz, classTest);
            }
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException(e.getMessage());
        }
        getLog().info("end main");
    }

    /**
     * @return
     */
    private ClassLoader getClassLoader() {
        //TODO
//        DependencyResolutionRequiredException e;
//        List<String> classpathElements = project.getCompileClasspathElements();
//        List<URL> projectClasspathList = new ArrayList<URL>();
//        for (String element : classpathElements) {
//            try {
//                projectClasspathList.add(new File(element).toURI().toURL());
//            } catch (MalformedURLException e) {
//                throw new MojoExecutionException(element + " is an invalid classpath element", e);
//            }
//        }
//
//        URLClassLoader loader = new URLClassLoader(projectClasspathList.toArray(new URL[0]));
        return Main.class.getClassLoader();
    }
}
