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
import org.junit.Before;
import org.junit.Test;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import it.fratta.jerkoff.ClassUnderTest;

/**
 * @author luca
 *
 */
public class Main {

	private static final String BASE_CLASS = "GenericTest";

	private static final String BASE_PACKAGE = "it.fratta.jerkoff";

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

	private static TypeSpec getTypeSpec(Class<?> clazz) {
		Builder classTestBuilder = TypeSpec.classBuilder(clazz.getSimpleName() + TEST);
		ClassName superClass = ClassName.get(BASE_PACKAGE, BASE_CLASS);
		classTestBuilder.superclass(superClass);
		classTestBuilder.addJavadoc("@author \n");
		classTestBuilder.addModifiers(Modifier.PUBLIC);
		FieldSpec.Builder spec = FieldSpec.builder(clazz, getVariableName(clazz), Modifier.PRIVATE);
		/*
		 * for spring test --> spec.addAnnotation(Autowired.class);
		 */
		classTestBuilder.addField(spec.build());
		getMethods(classTestBuilder, clazz);
		return classTestBuilder.build();
	}

	private static String getVariableName(Class<?> clazz) {
		String name = clazz.getSimpleName();
		return name.toLowerCase().charAt(0) + name.substring(1);
	}

	private static void getMethods(Builder classTestBuilder, Class<?> clazz) {
		/*
		 * for non spring test
		 */
		MethodSpec methodInitSpec = getInitMethod(clazz);
		classTestBuilder.addMethod(methodInitSpec);

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

	private static MethodSpec getInitMethod(Class<?> clazz) {
		MethodSpec.Builder methodInitBuilder = MethodSpec.methodBuilder("init");
		// TODO init object
		methodInitBuilder.addAnnotation(Before.class)
				.addStatement(getVariableName(clazz) + " = new " + clazz.getSimpleName() + "(0)")
				.addModifiers(Modifier.PUBLIC);
		methodInitBuilder.addJavadoc("\n");
		return methodInitBuilder.build();
	}

	private static MethodSpec getMethodSpec(int count, Method method, Class<?> clazz) {
		String result = "";
		String expected = "true";
		if (!void.class.equals(method.getReturnType())) {
			result = method.getReturnType().getName() + " result = ";
			// TODO assert expected
			expected = "result.equals(0)";
		}
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getName() + count + TEST);
		String params = getParams(method, methodBuilder);
		AnnotationSpec.Builder annSpecBuilder = AnnotationSpec.builder(Test.class);
		// TODO exception expected
		// annSpecBuilder.addMember("expected","Exception.class");
		AnnotationSpec annTestSpec = annSpecBuilder.build();
		methodBuilder.addAnnotation(annTestSpec)
				.addStatement(result + getVariableName(clazz) + ".$N(" + params + ")", method.getName())
				.addModifiers(Modifier.PUBLIC);
		methodBuilder.addStatement("$L.assertTrue(" + expected + ")", Assert.class.getName());
		methodBuilder.addJavadoc("\n");
		return methodBuilder.build();
	}

	private static String getParams(Method method, MethodSpec.Builder methodBuilder) {
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

}
