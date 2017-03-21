package it.fratta.jerkoff.poet.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import it.fratta.jerkoff.mongo.MongoDBDao;
import it.fratta.jerkoff.poet.PojoCreator;
import it.fratta.jerkoff.util.LogUtils;
import it.fratta.jerkoff.util.PropertiesUtils;

/**
 * 
 * @author sap-nocops
 *
 */
public class PojoCreatorImpl implements PojoCreator {

    private static final String TEST = "Test";

    private static Logger LOG;

    private Properties prop;
    private MongoDBDao mongo;

    /**
     * 
     * @param prop
     */
    public PojoCreatorImpl(Properties prop, MongoDBDao mongo) {
        LOG = LogUtils.getLogger(prop, PojoCreatorImpl.class);
        this.prop = prop;
        this.mongo = mongo;
    }

    /**
     * genera i metodi di test per clazz
     * 
     * @param classTestBuilder
     * @param clazz
     * @param prop
     * @param mongo
     */
    private void addClassMethodsToBuilder(Builder classTestBuilder, Class<?> clazz) {
        int count = 0;
        String appName = PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.APP_NAME);
        for (Method method : clazz.getDeclaredMethods()) {
            if (java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
                LOG.info("app: " + appName + " - method: " + method);
                count++;
                for (Document doc : mongo.find(appName, method.toString())) {
                    LOG.debug("document: " + doc);
                    // TODO
                }
                Document infoFromMongoDb = null;
                MethodSpec methodSpec = getMethodSpec(count, method, clazz, infoFromMongoDb);
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
    private String getInstanceVariableName(Class<?> clazz) {
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
    private MethodSpec getMethodSpec(int count, Method method, Class<?> clazz,
            Document infoFromMongoDb) {
        String result = getAssignmentOfMethodResult(method);
        String expected = getExpectedResultAsBooleanAssert(method);
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder(method.getName() + count + TEST);
        /*
         * for non spring test
         */
        String invokerName = getInvokerName(method, clazz, infoFromMongoDb, methodBuilder);
        String params = getParams(method, methodBuilder, infoFromMongoDb);
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
     * Returns a string representing the way to call the given method, either
     * static or via instance way depending on the method signature
     * 
     * @param method
     * @param clazz
     * @param infoFromMongoDb
     * @param methodBuilder
     * @return
     */
    private String getInvokerName(Method method, Class<?> clazz, Document infoFromMongoDb,
            MethodSpec.Builder methodBuilder) {
        if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            return clazz.getSimpleName();
        }
        newInstance(clazz, methodBuilder, infoFromMongoDb);
        return getInstanceVariableName(clazz);
    }

    /**
     * Returns a string representing the declaration of 'result' variable with
     * the type of return type of the given method if the latter is not void,
     * returns an empty string otherwise
     * 
     * @param method
     * @return
     */
    private String getAssignmentOfMethodResult(Method method) {
        if (!void.class.equals(method.getReturnType())) {
            return method.getReturnType().getName() + " result = ";
        }
        return "";
    }

    /**
     * Returns a string representing the boolean condition to put as assertTrue
     * argument in junit if the method return type is not void, returns an empty
     * string otherwise
     * 
     * @param method
     * @return
     */
    private String getExpectedResultAsBooleanAssert(Method method) {
        if (!void.class.equals(method.getReturnType())) {
            /*
             * TODO assert expected from infoFromMongoDb
             */
            if (method.getReturnType().isPrimitive()) {
                return "result == 0";
            }
            return "result.equals(0)";
        }
        return "true";
    }

    /**
     * inizializza l'object under test
     * 
     * @param clazz
     * @param methodBuilder
     * @param infoFromMongoDb
     */
    private void newInstance(Class<?> clazz, MethodSpec.Builder methodBuilder,
            Document infoFromMongoDb) {
        /*
         * TODO init object with infoFromMongoDb
         */
        methodBuilder.addStatement(clazz.getSimpleName() + " " + getInstanceVariableName(clazz)
                + " = new " + clazz.getSimpleName() + "(0)");
    }

    /**
     * Generate the string to assign a new Instance of the given class without
     * constructor parameters Example: if given class is String, returns:" = new
     * String()"
     * 
     * @param clazz
     * @return
     */
    private String getNewInstanceOfNoParameters(Class<?> clazz) {
        return getNewInstanceOfWithParameter(clazz, "");
    }

    /**
     * Generate the string to assign a new Instance of the given class with the
     * parameter as argument Example: if given class is String with original
     * variable as parameter, returns:" = new String(original)" NB: if a
     * constant string is given, it needs to be escaped otherwise the method
     * will consider it as a variable Example: if parameters is "Hello" the
     * method needs to be called this way:
     * getNewInstanceOfWithParameters(myClazz, "\"Hello\"")
     * 
     * @param clazz
     * @param parameter
     * @return
     */
    private String getNewInstanceOfWithParameter(Class<?> clazz, String parameter) {
        return getNewInstanceOfWithParameters(clazz, new String[] { parameter });
    }

    /**
     * Generate the string to assign a new Instance of the given class with the
     * parameters as arguments Example: if given class is String with bytes and
     * charsetName as variable parameters, returns:" = new String(bytes,
     * charsetName); NB: if a constant string is given, it needs to be escaped
     * otherwise the method will consider it as a variable See
     * getNewInstanceOfWithParameter(Class<?>, String) for example
     * 
     * @param clazz
     * @param parameters
     * @return
     */
    private String getNewInstanceOfWithParameters(Class<?> clazz, List<String> parameters) {
        return getNewInstanceOfWithParameters(clazz,
                parameters.toArray(new String[parameters.size()]));
    }

    /**
     * Generate the string to assign a new Instance of the given class with the
     * parameters as arguments Example: if given class is String with bytes and
     * charsetName as variable parameters, returns:" = new String(bytes,
     * charsetName); NB: if a constant string is given, it needs to be escaped
     * otherwise the method will consider it as a variable See
     * getNewInstanceOfWithParameter(Class<?>, String) for example
     * 
     * @param clazz
     * @param parameters
     * @return
     */
    private String getNewInstanceOfWithParameters(Class<?> clazz, String[] parameters) {
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
    private String getParams(Method method, MethodSpec.Builder methodBuilder,
            Document infoFromMongoDb) {
        String params = "";
        int count = 0;
        for (Class<?> parameterClass : method.getParameterTypes()) {
            count++;
            String parameterName = getInstanceVariableName(parameterClass) + count;
            params += "," + parameterName;
            /*
             * TODO set param from infoFromMongoDb
             */
            methodBuilder.addStatement(parameterClass.getName() + " " + parameterName + " = 1");
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
     * @param mongo
     * @return
     */
    @Override
    public TypeSpec getTypeSpec(Class<?> clazz) {
        Builder classTestBuilder = TypeSpec.classBuilder(clazz.getSimpleName() + TEST);
        ClassName superClass = ClassName.get(
                PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.TEST_BASE_PACKAGE),
                PropertiesUtils.getRequiredProperty(prop, PropertiesUtils.TEST_BASE_CLASS));
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
        addClassMethodsToBuilder(classTestBuilder, clazz);
        return classTestBuilder.build();
    }

    /**
     * scrive il file.java
     * 
     * @param sourcePath
     * @param clazz
     * @param classTest
     */
    @Override
    public void writeJavaFile(File sourcePath, Class<?> clazz, TypeSpec classTest) {
        JavaFile classTestFile = JavaFile.builder(clazz.getPackage().getName(), classTest).build();
        try {
            classTestFile.writeTo(sourcePath);
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}