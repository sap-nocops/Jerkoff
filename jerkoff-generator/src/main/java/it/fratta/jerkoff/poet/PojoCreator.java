/**
 * 
 */
package it.fratta.jerkoff.poet;

import java.io.File;

import com.squareup.javapoet.TypeSpec;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public interface PojoCreator {

    /**
     * @param clazz
     * @return
     */
    TypeSpec getTypeSpec(Class<?> clazz);

    /**
     * @param sourcePath
     * @param clazz
     * @param classTest
     */
    void writeJavaFile(File sourcePath, Class<?> clazz, TypeSpec classTest);

}
