package it.fratta.jerkoff.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import it.fratta.jerkoff.generator.Main;

/**
 * 
 * @author sap-nocops
 *
 */
public class PojoCreatorUtils {

    public static final Logger LOG = Logger.getLogger(PojoCreatorUtils.class);

    /**
     * genera i metodi di test per clazz
     * 
     * @param classTestBuilder
     * @param clazz
     * @param prop
     */
    public static void addClassMethodsToBuilder(Builder classTestBuilder, Class<?> clazz,
            Properties prop) {

        int count = 0;
        LOG.info("Creating tests for public methods of " + clazz.getSimpleName());
        for (Method method : clazz.getDeclaredMethods()) {
            if (java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
                Main.LOG.info("app: " + prop.getProperty("appName") + " - method: " + method);
                count++;
                /*
                 * TODO:
                 * find on MongoDB collection appName with method.toString()
                 * foreach result call getMethodSpec
                 */
                Object infoFromMongoDb = null;
                MethodSpec methodSpec = PojoCreatorUtils.getMethodSpec(count, method, clazz,
                        infoFromMongoDb);
                classTestBuilder.addMethod(methodSpec);
            }
        }
    }

    /**
     * ritorna nome classe minuscolo da usare come nome variabile
     * 
     * @param clazz
     * @return
     */
    public static String getInstanceVariableName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return name.toLowerCase().charAt(0) + name.substring(1);
    }

    /**
     * genera il metodo di test per method di clazz
     * 
     * @param count
     * @param method
     * @param clazz
     * @param infoFromMongoDb
     * @return
     */
    public static MethodSpec getMethodSpec(int count, Method method, Class<?> clazz,
            Object infoFromMongoDb) {
        String result = "";
        String expected = "true";
        if (!void.class.equals(method.getReturnType())) {
            result = method.getReturnType().getName() + " result = ";
            /*
             * TODO assert expected from infoFromMongoDb
             */
            if (method.getReturnType().isPrimitive()) {
                expected = "result == 0";
            } else {
                expected = "result.equals(0)";
            }
        }
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder(method.getName() + count + Main.TEST);
        /*
         * for non spring test
         */
        String invokerName = null;
        if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            invokerName = clazz.getSimpleName();
        } else {
            newInstance(clazz, methodBuilder, infoFromMongoDb);
            invokerName = getInstanceVariableName(clazz);
        }
        String params = PojoCreatorUtils.getParams(method, methodBuilder, infoFromMongoDb);
        AnnotationSpec.Builder annSpecBuilder = AnnotationSpec.builder(Test.class);
        /*
         * TODO if infoFromMongoDb exception expected
         */
        // annSpecBuilder.addMember("expected","Exception.class");
        AnnotationSpec annTestSpec = annSpecBuilder.build();
        methodBuilder.addAnnotation(annTestSpec)
                .addStatement(result + invokerName + ".$N(" + params + ")", method.getName())
                .addModifiers(Modifier.PUBLIC);
        methodBuilder.addStatement("$L.assertTrue(" + expected + ")", Assert.class.getName());
        methodBuilder.addJavadoc("\n");
        return methodBuilder.build();
    }

    /**
     * inizializza l'object under test
     * 
     * @param clazz
     * @param methodBuilder
     * @param infoFromMongoDb
     */
    private static void newInstance(Class<?> clazz, MethodSpec.Builder methodBuilder,
            Object infoFromMongoDb) {
        /*
         * TODO init object with infoFromMongoDb
         */
        methodBuilder.addStatement(clazz.getSimpleName() + " " + getInstanceVariableName(clazz)
                + " = new " + clazz.getSimpleName() + "(0)");
    }

    private static String getNewInstanceOfNoParameters(Class<?> clazz) {
        return getNewInstanceOfWithParameter(clazz, "");
    }

    /**
     * 
     * @param clazz
     * @param parameter
     * @return
     */
    public static String getNewInstanceOfWithParameter(Class<?> clazz, String parameter) {
        return PojoCreatorUtils.getNewInstanceOfWithParameters(clazz, new String[] { parameter });
    }

    private static String getNewInstanceOfWithParameters(Class<?> clazz, List<String> parameters) {
        return getNewInstanceOfWithParameters(clazz,
                parameters.toArray(new String[parameters.size()]));
    }

    /**
     * 
     * @param clazz
     * @param parameters
     * @return
     */
    public static String getNewInstanceOfWithParameters(Class<?> clazz, String[] parameters) {
        return " = new " + clazz.getSimpleName() + "("
                + Arrays.toString(parameters).replaceAll("\\[", "").replaceAll("\\]", "") + ")";
    }

    /**
     * genera l'inizializzazione dei parametri e ritorna la lista dei nomi
     * 
     * @param method
     * @param methodBuilder
     * @param infoFromMongoDb
     * @return
     */
    public static String getParams(Method method, MethodSpec.Builder methodBuilder,
            Object infoFromMongoDb) {
        String params = "";
        for (Parameter parameter : method.getParameters()) {
            params += "," + parameter.getName();
            /*
             * TODO set param from infoFromMongoDb
             */
            methodBuilder.addStatement(
                    parameter.getType().getName() + " " + parameter.getName() + " = 1");
        }
        if (params.startsWith(",")) {
            params = params.substring(1);
        }
        return params;
    }

    /**
     * genera classe di test per clazz
     * 
     * @param clazz
     * @param prop
     * @return
     */
    public static TypeSpec getTypeSpec(Class<?> clazz, Properties prop) {
        Builder classTestBuilder = TypeSpec.classBuilder(clazz.getSimpleName() + Main.TEST);
        ClassName superClass = ClassName.get(prop.getProperty("test.basePackage"),
                prop.getProperty("test.baseClass"));
        classTestBuilder.superclass(superClass);
        classTestBuilder.addJavadoc("@author \n");
        classTestBuilder.addModifiers(Modifier.PUBLIC);
        AnnotationSpec.Builder annSpecBuilder = AnnotationSpec.builder(Generated.class);
        annSpecBuilder.addMember("value", "\"it.fratta.jerkoff.Generator\"");
        annSpecBuilder.addMember("date", "\"" + Calendar.getInstance().getTime().toString() + "\"");
        AnnotationSpec annGenSpec = annSpecBuilder.build();
        classTestBuilder.addAnnotation(annGenSpec);
        /*
         * for spring test
         */
        // FieldSpec.Builder spec = FieldSpec.builder(clazz,
        // getNewInstanceOfNoParameters(clazz), Modifier.PRIVATE);
        // spec.addAnnotation(Autowired.class);
        // classTestBuilder.addField(spec.build());
        PojoCreatorUtils.addClassMethodsToBuilder(classTestBuilder, clazz, prop);
        return classTestBuilder.build();
    }

    /**
     * scrive il file.java
     * 
     * @param sourcePath
     * @param clazz
     * @param classTest
     */
    public static void writeJavaFile(File sourcePath, Class<?> clazz, TypeSpec classTest) {
        JavaFile classTestFile = JavaFile.builder(clazz.getPackage().getName(), classTest).build();
        try {
            classTestFile.writeTo(sourcePath);
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}
