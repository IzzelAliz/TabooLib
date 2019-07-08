package io.izzel.taboolib.util;

import io.izzel.taboolib.TabooLib;
import io.izzel.taboolib.common.plugin.InternalPlugin;
import io.izzel.taboolib.module.inject.TSchedule;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author sky
 */
public class Files {

    public static List<Class> getClasses(Plugin plugin) {
        return getClasses(plugin, new String[0]);
    }

    public static List<Class> getClasses(Plugin plugin, String[] ignore) {
        List<Class> classes = new CopyOnWriteArrayList<>();
        URL url = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
        try {
            File src;
            try {
                src = new File(url.toURI());
            } catch (URISyntaxException e) {
                src = new File(url.getPath());
            }
            new JarFile(src).stream().filter(entry -> entry.getName().endsWith(".class")).forEach(entry -> {
                String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
                try {
                    if (Arrays.stream(ignore).noneMatch(className::startsWith)) {
                        classes.add(Class.forName(className, false, plugin.getClass().getClassLoader()));
                    }
                } catch (Throwable ignored) {
                }
            });
        } catch (Throwable ignored) {
        }
        return classes;
    }

    @TSchedule(period = 100, async = true)
    public static void clearTempFiles() {
        deepDelete(new File("plugins/TabooLib/temp"));
    }

    public static InputStream getResource(String filename) {
        return getResource(TabooLib.getPlugin(), filename);
    }

    public static InputStream getResource(Plugin plugin, String filename) {
        return plugin instanceof InternalPlugin ? getTabooLibResource(filename) : plugin.getClass().getClassLoader().getResourceAsStream(filename);
    }

    public static InputStream getTabooLibResource(String filename) {
        return getCanonicalResource(TabooLib.getPlugin(), filename);
    }

    public static InputStream getCanonicalResource(Plugin plugin, String filename) {
        File file = Files.file(new File("plugins/TabooLib/temp/" + UUID.randomUUID()));
        try {
            ZipFile zipFile = new ZipFile(Files.toFile(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().openStream(), file));
            ZipEntry entry = zipFile.getEntry(filename);
            if (entry != null) {
                return zipFile.getInputStream(entry);
            }
        } catch (Exception t) {
            t.printStackTrace();
        }
        return null;
    }

    public static void releaseResource(Plugin plugin, String path, boolean replace) {
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists() || replace) {
            try (InputStream inputStream = getCanonicalResource(plugin, (plugin instanceof InternalPlugin ? "__resources__/" : "") + path)) {
                if (inputStream != null) {
                    toFile(inputStream, Files.file(file));
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static File toFile(String in, File file) {
        try (FileWriter fileWriter = new FileWriter(file); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(in);
            bufferedWriter.flush();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return file;
    }

    public static File toFile(InputStream inputStream, File file) {
        try (FileOutputStream fos = new FileOutputStream(file); BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.flush();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return file;
    }

    public static File file(File path, String filePath) {
        return file(new File(path, filePath));
    }

    public static File file(String filePath) {
        return file(new File(filePath));
    }

    public static File file(File file) {
        if (!file.exists()) {
            folder(file);
            try {
                file.createNewFile();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return file;
    }

    public static File folder(File path, String filePath) {
        return folder(new File(path, filePath));
    }

    public static File folder(String filePath) {
        return folder(new File(filePath));
    }

    public static File folder(File file) {
        if (!file.exists()) {
            String filePath = file.getPath();
            int index = filePath.lastIndexOf(File.separator);
            String folderPath;
            File folder;
            if ((index >= 0) && (!(folder = new File(filePath.substring(0, index))).exists())) {
                folder.mkdirs();
            }
        }
        return file;
    }

    public static void deepDelete(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        for (File file1 : Objects.requireNonNull(file.listFiles())) {
            deepDelete(file1);
        }
        file.delete();
    }

    public static void deepCopy(String originFileName, String targetFileName) {
        File originFile = new File(originFileName);
        File targetFile = new File(targetFileName);
        if (!targetFile.exists()) {
            if (!originFile.isDirectory()) {
                file(targetFile);
            } else {
                targetFile.mkdirs();
            }
        }
        if (originFile.isDirectory()) {
            for (File file : Objects.requireNonNull(originFile.listFiles())) {
                if (file.isDirectory()) {
                    deepCopy(file.getAbsolutePath(), targetFileName + "/" + file.getName());
                } else {
                    weekCopy(file, new File(targetFileName + "/" + file.getName()));
                }
            }
        } else {
            weekCopy(originFile, targetFile);
        }
    }

    public static void weekCopy(File file1, File file2) {
        try (FileInputStream fileIn = new FileInputStream(file1);
             FileOutputStream fileOut = new FileOutputStream(file2);
             FileChannel channelIn = fileIn.getChannel();
             FileChannel channelOut = fileOut.getChannel()) {
            channelIn.transferTo(0, channelIn.size(), channelOut);
        } catch (IOException t) {
            t.printStackTrace();
        }
    }

    public static String readFromURL(String url, String def) {
        return Optional.ofNullable(readFromURL(url)).orElse(def);
    }

    public static String readFromURL(String url) {
        try (InputStream inputStream = new URL(url).openStream(); BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            return new String(IO.readFully(bufferedInputStream));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static String readFromFile(File file, int size, String encode) {
        try (FileInputStream fin = new FileInputStream(file); BufferedInputStream bin = new BufferedInputStream(fin)) {
            return readFromStream(fin, size, encode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFromStream(InputStream in, int size, String encode) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] b = new byte[size];
            int i;
            while ((i = in.read(b)) > 0) {
                bos.write(b, 0, i);
            }
            return new String(bos.toByteArray(), encode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encodeYAML(FileConfiguration file) {
        return Base64Coder.encodeLines(file.saveToString().getBytes()).replaceAll("\\s+", "");
    }

    public static FileConfiguration decodeYAML(String args) {
        return YamlConfiguration.loadConfiguration(new StringReader(Base64Coder.decodeString(args)));
    }

    public static FileConfiguration load(File file) {
        return loadYaml(file);
    }

    public static YamlConfiguration loadYaml(File file) {
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            String yaml = com.google.common.io.Files.toString(file, Charset.forName("utf-8"));
            configuration.loadFromString(yaml);
            return configuration;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configuration;
    }

    private static Class getCaller(Class<?> obj) {
        try {
            return Class.forName(Thread.currentThread().getStackTrace()[3].getClassName(), false, obj.getClassLoader());
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }
}
