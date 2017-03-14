/**
 * 
 */
package it.fratta.jerkoff.generetor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.lang.model.element.Modifier;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import it.fratta.jerkoff.undertest.ClassUnderTest;

/**
 * @author luca
 *
 */
public class Main {

    /**
     * Devono diventare paramametro in input
     * package e nome della classe che tutti i test estenderanno
     */
    private static final String BASE_CLASS = "GenericTest";

    private static final String BASE_PACKAGE = "it.fratta.jerkoff.test";

    /**
     * cartella base dove verranno creati i test
     */
    private static final String PATH = "src/test/java";

    private static final Logger LOG = Logger.getLogger(Main.class);

    private static final String TEST = "Test";

    /**
     * @param args
     */
    public static void main(String[] args) {
        LOG.info("start main");
        File sourcePath = new File(PATH);
        Class<?> clazz = ClassUnderTest.class;
        TypeSpec classTest = getTypeSpec(clazz);
        writeJavaFile(sourcePath, clazz, classTest);
        LOG.info("end main");
    }

    private static void writeJavaFile(File sourcePath, Class<?> clazz, TypeSpec classTest) {
        JavaFile classTestFile = JavaFile.builder(clazz.getPackage().getName(), classTest).build();
        try {
            classTestFile.writeTo(sourcePath);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    /**
     * genera classe di test per clazz
     * 
     * @param clazz
     * @return
     */
    private static TypeSpec getTypeSpec(Class<?> clazz) {
        Builder classTestBuilder = TypeSpec.classBuilder(clazz.getSimpleName() + TEST);
        ClassName superClass = ClassName.get(BASE_PACKAGE, BASE_CLASS);
        classTestBuilder.superclass(superClass);
        classTestBuilder.addJavadoc("@author \n");
        classTestBuilder.addModifiers(Modifier.PUBLIC);
        /*
         * for spring test
         */
        // FieldSpec.Builder spec = FieldSpec.builder(clazz,
        // getVariableName(clazz), Modifier.PRIVATE);
        // spec.addAnnotation(Autowired.class);
        // classTestBuilder.addField(spec.build());
        getMethods(classTestBuilder, clazz);
        return classTestBuilder.build();
    }

    private static String getVariableName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return name.toLowerCase().charAt(0) + name.substring(1);
    }

    /**
     * genera i metodi di test per clazz
     * 
     * @param classTestBuilder
     * @param clazz
     */
    private static void getMethods(Builder classTestBuilder, Class<?> clazz) {

        int count = 0;
        for (Method method : clazz.getDeclaredMethods()) {
            if (java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
                LOG.info("method: " + method);
                count++;
                MethodSpec methodSpec = getMethodSpec(count, method, clazz);
                classTestBuilder.addMethod(methodSpec);
            }
        }
    }

    /**
     * genera il metodo di test per method di clazz
     * 
     * @param count
     * @param method
     * @param clazz
     * @return
     */
    private static MethodSpec getMethodSpec(int count, Method method, Class<?> clazz) {
        String result = "";
        String expected = "true";
        if (!void.class.equals(method.getReturnType())) {
            result = method.getReturnType().getName() + " result = ";
            // TODO assert expected
            expected = "result.equals(0)";
        }

        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder(method.getName() + count + TEST);
        /*
         * for non spring test
         */
        String invokerName = null;
        if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            invokerName = clazz.getSimpleName();
        } else {
            newInstance(clazz, methodBuilder);
            invokerName = getVariableName(clazz);
        }
        
        
        String params = getParams(method, methodBuilder);
        AnnotationSpec.Builder annSpecBuilder = AnnotationSpec.builder(Test.class);
        // TODO exception expected
        // annSpecBuilder.addMember("expected","Exception.class");
        AnnotationSpec annTestSpec = annSpecBuilder.build();

        methodBuilder.addAnnotation(annTestSpec)
                .addStatement(result + invokerName + ".$N(" + params + ")",
                        method.getName())
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
     */
    private static void newInstance(Class<?> clazz, MethodSpec.Builder methodBuilder) {
        // TODO init object
        methodBuilder.addStatement(clazz.getSimpleName() + " " + getVariableName(clazz) + " = new "
                + clazz.getSimpleName() + "(0)");
    }

    private static String getParams(Method method, MethodSpec.Builder methodBuilder) {
        String params = "";
        for (Parameter parameter : method.getParameters()) {
            params += "," + parameter.getName();
            // TODO param
            methodBuilder.addStatement(
                    parameter.getType().getName() + " " + parameter.getName() + " = 1");
        }
        if (params.startsWith(",")) {
            params = params.substring(1);
        }
        return params;
    }

}
