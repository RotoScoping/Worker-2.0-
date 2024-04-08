package core.validation.annotations.basic.validator;

import core.logger.AsyncLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;


/**
 * Класс предназначенный для сканирования пакетов файловой системы / jar файла и добавления классов модели в коллецию
 */
public class PackageScan {
    private static final AsyncLogger logger = AsyncLogger.get();

    /**
     * Gets classes.
     *
     * @param packageName the package name
     * @return the classes
     */
    public static List<Class<?>> getClasses(String packageName)  {
        logger.log(Level.INFO, "Сканирование классов в пакете " + packageName);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Такого пути не существует: " + path);
        }
        List<Class<?>> classes = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equalsIgnoreCase("file")) {
                try {
                    classes.addAll(findClasses(new File(resource.getFile()), packageName));
                } catch (ClassNotFoundException e) {
                    logger.log(Level.WARNING, "Ошибка опредления class файла по пути");
                }
            } else if (resource.getProtocol().equalsIgnoreCase("jar")) {
                String jarPath = resource.getFile().substring(5, resource.getFile().indexOf("!")); // Remove "file:" and "!..."
                try {
                    classes.addAll(findClassesFromJar(jarPath, path));
                } catch (IOException | ClassNotFoundException e) {
                    logger.log(Level.WARNING, "Ошибка опредления class файла по пути");
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
