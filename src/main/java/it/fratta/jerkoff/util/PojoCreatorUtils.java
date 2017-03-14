package it.fratta.jerkoff.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import it.fratta.jerkoff.generator.Main;

public class PojoCreatorUtils {
	public static final Logger LOG = Logger.getLogger(PojoCreatorUtils.class);
	
	/**
     * genera i metodi di test per clazz
     * 
     * @param classTestBuilder
     * @param clazz
     */
	public static void addClassMethodsToBuilder(Builder classTestBuilder, Class<?> clazz) {
	    
		int count = 0;
		LOG.info("Creating tests for public methods of " + clazz.getSimpleName());
		for (Method method : clazz.getDeclaredMethods()) {
			if (java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
				Main.LOG.info("method: " + method);
				count++;
				MethodSpec methodSpec = PojoCreatorUtils.getMethodSpec(count, method, clazz);
				classTestBuilder.addMethod(methodSpec);
			}
		}
	}

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
     * @return
     */
	public static MethodSpec getMethodSpec(int count, Method method, Class<?> clazz) {
		String result = "";
		String expected = "true";
		if (!void.class.equals(method.getReturnType())) {
			result = method.getReturnType().getName() + " result = ";
			// TODO assert expected
			expected = "result.equals(0)";
		}
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getName() + count + Main.TEST);
		/*
         * for non spring test
         */
        String invokerName = null;
        if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
            invokerName = clazz.getSimpleName();
        } else {
            newInstance(clazz, methodBuilder);
            invokerName = getInstanceVariableName(clazz);
        }
		String params = PojoCreatorUtils.getParams(method, methodBuilder);
		AnnotationSpec.Builder annSpecBuilder = AnnotationSpec.builder(Test.class);
		// TODO exception expected
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
     */
    private static void newInstance(Class<?> clazz, MethodSpec.Builder methodBuilder) {
        // TODO init object
        methodBuilder.addStatement(clazz.getSimpleName() + " " + getInstanceVariableName(clazz) + " = new "
                + clazz.getSimpleName() + "(0)");
    }

	private static String getNewInstanceOfNoParameters(Class<?> clazz) {
		return getNewInstanceOfWithParameter(clazz, "");
	}

	public static String getNewInstanceOfWithParameter(Class<?> clazz, String parameter) {
		return PojoCreatorUtils.getNewInstanceOfWithParameters(clazz, new String[]{parameter});
	}

	private static String getNewInstanceOfWithParameters(Class<?> clazz, List<String> parameters) {
		return getNewInstanceOfWithParameters(clazz, parameters.toArray(new String[parameters.size()]));
	}

	public static String getNewInstanceOfWithParameters(Class<?> clazz, String[] parameters) {
		return " = new " + clazz.getSimpleName() + "(" + Arrays.toString(parameters).replaceAll("\\[", "").replaceAll("\\]", "") + ")";
	}

	public static String getParams(Method method, MethodSpec.Builder methodBuilder) {
		String params = "";
		for (Parameter parameter : method.getParameters()) {
			params += "," + parameter.getName();
			// TODO param
			methodBuilder.addStatement(parameter.getType().getName() + " " + parameter.getName() + " = 1");
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
     * @return
     */
	public static TypeSpec getTypeSpec(Class<?> clazz) {
		Builder classTestBuilder = TypeSpec.classBuilder(clazz.getSimpleName() + Main.TEST);
		ClassName superClass = ClassName.get(Main.BASE_PACKAGE, Main.BASE_CLASS);
		classTestBuilder.superclass(superClass);
		classTestBuilder.addJavadoc("@author \n");
		classTestBuilder.addModifiers(Modifier.PUBLIC);
		/*
         * for spring test
         */
        // FieldSpec.Builder spec = FieldSpec.builder(clazz,
        // getNewInstanceOfNoParameters(clazz), Modifier.PRIVATE);
        // spec.addAnnotation(Autowired.class);
        // classTestBuilder.addField(spec.build());
		PojoCreatorUtils.addClassMethodsToBuilder(classTestBuilder, clazz);
		return classTestBuilder.build();
	}

	public static void writeJavaFile(File sourcePath, Class<?> clazz, TypeSpec classTest) {
		JavaFile classTestFile = JavaFile.builder(clazz.getPackage().getName(), classTest).build();
		try {
			classTestFile.writeTo(sourcePath);
		} catch (IOException e) {
			LOG.error(e);
		}
	}
}
