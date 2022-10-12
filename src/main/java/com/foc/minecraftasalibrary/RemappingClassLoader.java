package com.foc.minecraftasalibrary;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Enumeration;

public class RemappingClassLoader extends ClassLoader {
    private final ClassLoader root;

    public RemappingClassLoader(ClassLoader root) {
        super(root);
        this.root = root;
    }

    /*
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // It would be easier to call the loadClass() methods of the delegateClassLoaders
        // here, but we have to load the class from the byte code ourselves, because we
        // need it to be associated with our class loader.
        String path = name.replace('.', '/') + ".class";
        URL url = findResource(path);
        if (url == null) {
            throw new ClassNotFoundException(name);
        }
        ByteBuffer byteCode;
        try {
            byteCode = loadResource(url);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
        Class<?> clazz = defineClass(name, byteCode, null);
        // Set every access to public
        try {
            return makePublic(clazz);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> makePublic(Class<?> clazz) throws NoSuchMethodException {
        // Constructors
        clazz.getDeclaredConstructor().setAccessible(true);
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            constructor.setAccessible(true);
        }

        // Methods
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
        }

        // Fields
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
        }

        // Classes
        for (Class<?> innerClass : clazz.getDeclaredClasses()) {
            makePublic(innerClass);
        }

        // Interfaces
        for (Class<?> innerInterface : clazz.getDeclaredClasses()) {
            makePublic(innerInterface);
        }

        // Enums
        for (Class<?> innerEnum : clazz.getDeclaredClasses()) {
            makePublic(innerEnum);
        }

        // Annotations
        for (AnnotatedType type : clazz.getAnnotatedInterfaces()) {
            makePublic((Class<?>) type.getType());
        }
        for (Annotation annotation : clazz.getAnnotations()) {
            makePublic(annotation.annotationType());
        }
        return clazz;
    }

    private ByteBuffer loadResource(URL url) throws IOException {
        try (InputStream stream = url.openStream()) {
            int initialBufferCapacity = Math.min(0x40000, stream.available() + 1);
            if (initialBufferCapacity <= 2) {
                initialBufferCapacity = 0x10000;
            } else {
                initialBufferCapacity = Math.max(initialBufferCapacity, 0x200);
            }
            ByteBuffer buf = ByteBuffer.allocate(initialBufferCapacity);
            while (true) {
                if (!buf.hasRemaining()) {
                    ByteBuffer newBuf = ByteBuffer.allocate(2 * buf.capacity());
                    buf.flip();
                    newBuf.put(buf);
                    buf = newBuf;
                }
                int len = stream.read(buf.array(), buf.position(), buf.remaining());
                if (len <= 0) {
                    break;
                }
                buf.position(buf.position() + len);
            }
            buf.flip();
            return buf;
        }
    }

    protected URL findResource(String name) {
        return root.getResource(name);
    }

    protected Enumeration<URL> findResources(String name) throws IOException {
        return root.getResources(name);
    }

    */
}
