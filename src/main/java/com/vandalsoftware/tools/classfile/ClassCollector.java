package com.vandalsoftware.tools.classfile;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class collects class files. Once classes have been collected, the methods for analysis can
 * be used: {@link #subclasses(String)}, {@link #usages(String)}, etc.
 *
 * <ol>
 *     <li>collect</li>
 *     <li>usages, subclasses, etc.</li>
 * </ol>
 * @author Jonathan Le
 */
public class ClassCollector {
    private final ClassFileReader classFileReader = new ClassFileReader();
    private HashMap<File, ClassInfo> infoMap;

    public ClassCollector() {
        this.infoMap = new HashMap<>();
    }

    public static void main(String[] args) {
        final BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        final ArrayList<String> input = new ArrayList<>();
        try {
            input.add(buf.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(buf);
        }

        final ClassCollector reader = new ClassCollector();
        reader.collect(args[0]);
        // Check each file for usage of each input
        final File[] files = reader.usages(input);
        for (File f : files) {
            System.out.println(f);
        }
    }

    static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listFiles(File dir, Collection<File> c) {
        if (dir.exists()) {
            final File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    final File f = new File(file, s);
                    return f.isDirectory() || s.endsWith(".class");
                }
            });
            for (File f : files) {
                if (f.isDirectory()) {
                    listFiles(f, c);
                }
                c.add(f);
            }
        }
    }

    /**
     * Collect class file.
     */
    public ClassInfo collectFile(File f) {
        if (f.isFile()) {
            ClassInfo info = this.infoMap.get(f);
            if (info == null) {
                try {
                    info = classFileReader.readFile(f);
                    this.infoMap.put(f, info);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return info;
        }
        return null;
    }

    /**
     * Collect class files in a directory.
     */
    public void collect(File dir) {
        if (!dir.isDirectory()) {
            return;
        }
        final ArrayList<File> files = new ArrayList<>();
        listFiles(dir, files);
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
     * Check for usages of a single class name.
     *
     * @see #collect(java.io.File)
     * @see #collect(String)
     * @see #collectFile(java.io.File)
     */
    public File[] usages(String className) {
        final HashSet<File> usages = new HashSet<>();
        for (Map.Entry<File, ClassInfo> entry : this.infoMap.entrySet()) {
            if (entry.getValue().check(className)) {
                usages.add(entry.getKey());
            }
        }
        return usages.toArray(new File[usages.size()]);
    }

    /**
     * Check for usages of collection of class names.
     *
     * @see #collect(java.io.File)
     * @see #collect(String)
     * @see #collectFile(java.io.File)
     */
    public File[] usages(Collection<String> classNames) {
        final HashSet<File> usages = new HashSet<>();
        for (String className : classNames) {
            for (Map.Entry<File, ClassInfo> entry : this.infoMap.entrySet()) {
                final ClassInfo info = entry.getValue();
                final File file = entry.getKey();
                if (info.check(className)) {
                    usages.add(file);
                }
            }
        }
        return usages.toArray(new File[usages.size()]);
    }

    /**
     * @return collected subclasses for a class given its name
     * @see #collect(java.io.File)
     * @see #collect(String)
     * @see #collectFile(java.io.File)
     */
    public Collection<String> subclasses(String className) {
        final HashSet<String> subclasses = new HashSet<>();
        for (ClassInfo info : this.infoMap.values()) {
            if (info.superClassName.equals(className)) {
                subclasses.add(info.thisClassName);
            }
        }
        return subclasses;
    }
}
