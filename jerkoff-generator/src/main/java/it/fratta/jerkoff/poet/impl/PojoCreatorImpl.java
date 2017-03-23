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
		List<Document> methodInfo;
		for (Method method : clazz.getDeclaredMethods()) {
			Document methodInputs = null, methodOutput = null;
			if (java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
				LOG.info("app: " + appName + " - method: " + method);
				count++;
				methodInfo = mongo.find(appName, method.toString());
				for (Document doc : methodInfo) {
					LOG.debug("document: " + doc);
					if (doc.containsKey("argsBefore")) {
						methodInputs = doc;
					} else {
						methodOutput = doc;
					}
				}
				MethodSpec methodSpec = getMethodSpec(count, method, clazz, methodInputs, methodOutput);
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
	 * @param methodOutput
	 * @return
	 */
	private MethodSpec getMethodSpec(int count, Method method, Class<?> clazz, Document methodInputs,
			Document methodOutput) {
		String result = getAssignmentOfMethodResult(method);
		String expected = getExpectedResultAsBooleanAssert(method, methodOutput);
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getName() + count + TEST);
		/*
		 * for non spring test
		 */
		String invokerName = getInvokerName(method, clazz, methodInputs, methodBuilder);
		String params = getParams(method, methodBuilder, methodInputs);
		AnnotationSpec.Builder annSpecBuilder = AnnotationSpec.builder(Test.class);
		addExpectedExceptionIfAny(methodInputs, annSpecBuilder);
		AnnotationSpec annTestSpec = annSpecBuilder.build();
		methodBuilder.addAnnotation(annTestSpec)
				.addStatement(result + invokerName + ".$N(" + params + ")", method.getName())
				.addModifiers(Modifier.PUBLIC);
		methodBuilder.addStatement("$L.assertTrue(" + expected + ")", Assert.class.getName());
		methodBuilder.addJavadoc("\n");
		return methodBuilder.build();
	}

	/**
	 * Add an expected exception if at runtime the method threw one
	 * 
	 * @param methodInputs
	 * @param annSpecBuilder
	 */
	private void addExpectedExceptionIfAny(Document methodInputs, AnnotationSpec.Builder annSpecBuilder) {
		String expectedException = methodInputs.getString("exception");
		if (expectedException != null) {
			annSpecBuilder.addMember("expected", expectedException + ".class");
		}
	}

	/**
	 * Returns a string representing the way to call the given method, either
	 * static or via instance way depending on the method signature
	 * 
	 * @param method
	 * @param clazz
	 * @param methodInputs
	 * @param methodBuilder
	 * @return
	 */
	private String getInvokerName(Method method, Class<?> clazz, Document methodInputs,
			MethodSpec.Builder methodBuilder) {
		if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
			return clazz.getSimpleName();
		}
		Class<?> methodReturnType = method.getReturnType();
		String returnValue = methodInputs.getString("returnValue");
		newInstance(clazz, methodBuilder, methodReturnType, returnValue);
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
	 * @param methodOutput
	 * @return
	 */
	private String getExpectedResultAsBooleanAssert(Method method, Document methodOutput) {
		StringBuilder toReturn = new StringBuilder();
		// If method's return type is void methodOutput.returnValue should be
		// null
		if (!void.class.equals(method.getReturnType()) && methodOutput != null) {
			String methodReturnValue = methodOutput.getString("returnValue");
			Class<?> methodReturnType = method.getReturnType();
			if (methodReturnType.isPrimitive()) {
				toReturn.append("result == ").append(methodReturnValue);
			} else {
				toReturn.append("result.equals(")
						.append(getValueWithApexIfRequired(methodReturnType, methodReturnValue)).append(")");
			}
		} else {
			toReturn.append("true");
		}
		return toReturn.toString();
	}

	private String getValueWithApexIfRequired(Class<?> methodReturnType, String methodReturnValue) {
		String apex = (String.class.equals(methodReturnType)) ? "\"" : "";
		return new StringBuilder(apex).append(methodReturnValue).append(apex).toString();
	}

	/**
	 * inizializza l'object under test
	 * 
	 * @param clazz
	 * @param methodBuilder
	 * @param methodInputs
	 */
	private void newInstance(Class<?> clazz, MethodSpec.Builder methodBuilder, Class<?> methodReturnType,
			String methodReturnValue) {
		// TODO here we need to evaluate combination between constructors and
		// setter to see if it is possible to instantiate the object properly,
		// otherwise direct deserialization must be done
		methodBuilder.addStatement(new StringBuilder(clazz.getSimpleName()).append(" ")
				.append(getInstanceVariableName(clazz)).append(" = new ").append(clazz.getSimpleName()).append("(")
				.append(getValueWithApexIfRequired(methodReturnType, methodReturnValue)).append(")").toString());
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
		return getNewInstanceOfWithParameters(clazz, parameters.toArray(new String[parameters.size()]));
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
	 * @param methodInputs
	 * @return
	 */
	private String getParams(Method method, MethodSpec.Builder methodBuilder, Document methodInputs) {
		String params = "";
		String[] methodArgsValues = methodInputs.getString("argsBefore").split(",");
		int count = 0;
		for (Class<?> parameterClass : method.getParameterTypes()) {
			String parameterName = getInstanceVariableName(parameterClass) + count;
			params += "," + parameterName;
			methodBuilder.addStatement(parameterClass.getName() + " " + parameterName + " = " + methodArgsValues[count]);
			count++;
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
