package com.ilummc.tlib.inject;

import com.google.common.io.Files;
import com.ilummc.tlib.TLib;
import com.ilummc.tlib.annotations.Config;
import me.skymc.taboolib.fileutils.ConfigUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Map;

public class TConfigInjector {

    public static void fixUnicode(YamlConfiguration configuration) {
        try {
            Field field = YamlConfiguration.class.getDeclaredField("yamlOptions");
            field.setAccessible(true);
            field.set(configuration, NoUnicodeDumperOption.INSTANCE);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static final class NoUnicodeDumperOption extends DumperOptions {

        private static final NoUnicodeDumperOption INSTANCE = new NoUnicodeDumperOption();

        @Override
        public void setAllowUnicode(boolean allowUnicode) {
            super.setAllowUnicode(false);
        }

        @Override
        public boolean isAllowUnicode() {
            return false;
        }

        @Override
        public void setLineBreak(LineBreak lineBreak) {
            super.setLineBreak(LineBreak.getPlatformLineBreak());
        }
    }

    public static Object loadConfig(Plugin plugin, Class<?> clazz) {
        try {
            Config config = clazz.getAnnotation(Config.class);
            Validate.notNull(config);
            File file = new File(plugin.getDataFolder(), config.name());
            if (!file.exists()) if (config.fromJar()) plugin.saveResource(config.name(), true);
            else saveConfig(plugin, clazz.newInstance());
            Object obj = unserialize(plugin, clazz);
            if (!config.readOnly()) saveConfig(plugin, obj);
            return obj;
        } catch (NullPointerException e) {
            TLib.getTLib().getLogger().warn("插件 " + plugin + " 的配置类 " + clazz.getSimpleName() + " 加载失败：没有 @Config 注解");
        } catch (Exception e) {
            TLib.getTLib().getLogger().warn("插件 " + plugin + " 的配置类 " + clazz.getSimpleName() + " 加载失败");
        }
        return null;
    }

    public static Object unserialize(Plugin plugin, Class<?> clazz) {
        try {
            Config config = clazz.getAnnotation(Config.class);
            Validate.notNull(config);
            return ConfigUtils.confToObj(
                    ConfigUtils.mapToConf(
                            ConfigUtils.yamlToMap(
                                    Files.toString(new File(plugin.getDataFolder(), config.name()), Charset.forName(config.charset())))), clazz);
        } catch (NullPointerException e) {
            TLib.getTLib().getLogger().warn("插件 " + plugin + " 的配置类 " + clazz.getSimpleName() + " 加载失败：没有 @Config 注解或文件不存在");
            return null;
        } catch (Exception e) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e1) {
                TLib.getTLib().getLogger().warn("插件 " + plugin + " 的配置类 " + clazz.getSimpleName() + " 加载失败");
                return null;
            }
        }
    }

    public static Map<String, Object> serialize(Plugin plugin, Object object) {
        try {
            Config config = object.getClass().getAnnotation(Config.class);
            Validate.notNull(config);
            return ConfigUtils.objToConf(object).getValues(false);
        } catch (NullPointerException e) {
            TLib.getTLib().getLogger().warn("插件 " + plugin + " 的配置类 " + object.getClass().getSimpleName() + " 序列化失败：没有 @Config 注解");
        } catch (Exception e) {
            TLib.getTLib().getLogger().warn("插件 " + plugin + " 的配置类 " + object.getClass().getSimpleName() + " 序列化失败");
        }
        return null;
    }

    public static void saveConfig(Plugin plugin, Object object) throws IOException, NullPointerException {
        Config config = object.getClass().getAnnotation(Config.class);
        Validate.notNull(config);
        Object obj = serialize(plugin, object);
        Validate.notNull(obj);
        File target = new File(plugin.getDataFolder(), config.name());
        if (!target.exists()) target.createNewFile();
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(false);
        Yaml yaml = new Yaml(options);
        String str = yaml.dump(obj);
        byte[] arr = str.getBytes(config.charset());
        Files.write(arr, target);
    }

}
