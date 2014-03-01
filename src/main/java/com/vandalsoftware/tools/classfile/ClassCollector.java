package com.vandalsoftware.tools.classfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class collects class files. Once classes have been collected, the methods for analysis can
 * be used: {@link #findSubclasses(String)}, {@link #usages(String)}, etc.
 *
 * <ol>
 *     <li>collect</li>
 *     <li>findUsages, findSubclasses, etc.</li>
 * </ol>
 * @author Jonathan Le
 */
public class ClassCollector {
    private final ClassFileReader classFileReader = new ClassFileReader();
    private HashMap<File, ClassInfo> infoMap;
    private Logger logger;

    public ClassCollector() {
        this.infoMap = new HashMap<>();
        this.logger = LoggerFactory.getLogger(getClass());
    }

    static Collection<File> listFiles(File rootDir) {
        if (!rootDir.isDirectory()) {
            return Collections.emptyList();
        }
        final LinkedList<File> dirs = new LinkedList<>();
        dirs.add(rootDir);
        final HashSet<File> fileSet = new HashSet<>();
        final ClassFileFilter filter = new ClassFileFilter();
        do {
            final File dir = dirs.removeFirst();
            if (dir.exists() && dir.isDirectory()) {
                final File[] files = dir.listFiles(filter);
                for (File f : files) {
                    if (f.isDirectory()) {
                        dirs.add(f);
                    }
                    fileSet.add(f);
                }
            }
        } while (!dirs.isEmpty());
        return fileSet;
    }

    /**
     * Collects class file.
     */
    public ClassInfo collectFile(File f) {
        if (f.isFile()) {
            ClassInfo info = this.infoMap.get(f);
            if (info == null) {
                try {
                    info = classFileReader.readFile(f);
                    this.infoMap.put(f, info);
                } catch (IOException e) {
                    this.logger.error("Class read error", e);
                }
            }
            return info;
        }
        return null;
    }

    /**
     * Collects class files in a directory.
     */
    public void collect(File dir) {
        final Collection<File> files = listFiles(dir);
        for (File f : files) {
            collectFile(f);
        }
    }

    /**
     * Collect class files in a path.
     */
    public void collect(String path) {
        collect(new File(path));
    }

    /**
     * Checks for findUsages of a single class name.
     *
     * @see #collect(java.io.File)
     * @see #collect(String)
     * @see #collectFile(java.io.File)
     */
    public File[] usages(String className) {
        final HashSet<File> usages = new HashSet<>();
        addUsages(usages, className);
        return usages.toArray(new File[usages.size()]);
    }

    /**
     * Checks for usages of collection of class names.
     *
     * @see #collect(java.io.File)
     * @see #collect(String)
     * @see #collectFile(java.io.File)
     */
    public File[] findUsages(Collection<String> classNames) {
        final HashSet<File> usages = new HashSet<>();
        for (String className : classNames) {
            addUsages(usages, className);
        }
        return usages.toArray(new File[usages.size()]);
    }

    /**
     * Adds class names that use the given class name to the Collection.
     */
    private void addUsages(Collection<File> usages, String className) {
        for (Map.Entry<File, ClassInfo> entry : this.infoMap.entrySet()) {
            if (entry.getValue().usesClass(className)) {
                usages.add(entry.getKey());
            }
        }
    }

    /**
     * @return collected subclasses for a class given its name
     * @see #collect(java.io.File)
     * @see #collect(String)
     * @see #collectFile(java.io.File)
     */
    public Collection<String> findSubclasses(String className) {
        final HashSet<String> subclasses = new HashSet<>();
        for (ClassInfo info : this.infoMap.values()) {
            if (info.superClassName.equals(className)) {
                subclasses.add(info.thisClassName);
            }
        }
        return subclasses;
    }

    /**
     * Finds implementations of a class.
     */
    public Collection<String> findImplementations(String className) {
        final HashSet<String> impls = new HashSet<>();
        for (ClassInfo info : this.infoMap.values()) {
            if (info.getInterfaces().contains(className)) {
                impls.add(info.thisClassName);
            }
        }
        return impls;
    }

    private static class ClassFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File file, String s) {
            final File f = new File(file, s);
            return f.isDirectory() || s.endsWith(".class");
        }
    }
}
