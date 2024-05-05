package org.example.validation.annotations.basic.validator;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Класс предназначенный для сканирования пакетов файловой системы / jar файла и добавления классов модели в коллецию
 */
public class PackageScan {

    /**
     * Gets classes.
     *
     * @param packageName the package name
     * @return the classes
     */
    public static List<Class<?>> getClasses(String packageName)  {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            System.out.println("такого пути нет");
        }
        List<Class<?>> classes = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equalsIgnoreCase("file")) {
                try {
                    classes.addAll(findClasses(new File(resource.getFile()), packageName));
                } catch (ClassNotFoundException e) {
                    System.out.println("Ошибка опредления class файла по пути");
                }
            } else if (resource.getProtocol().equalsIgnoreCase("jar")) {
                String jarPath = resource.getFile().substring(5, resource.getFile().indexOf("!"));
                try {
                    classes.addAll(findClassesFromJar(jarPath, path));
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Ошибка опредления class файла по пути");
                }
            }
        }

        return classes;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        }
        return classes;
    }

    private static List<Class<?>> findClassesFromJar(String jarPath, String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class") && name.startsWith(packageName) && name.length() > (packageName.length() + "/".length())) {
                    String className = name.replace('/', '.').replace('\\', '.').substring(0, name.length() - 6);
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }


}
