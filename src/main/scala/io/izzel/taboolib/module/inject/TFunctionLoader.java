package io.izzel.taboolib.module.inject;

import io.izzel.taboolib.TabooLibAPI;
import io.izzel.taboolib.TabooLibLoader;
import io.izzel.taboolib.module.locale.logger.TLogger;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @Author sky
 * @Since 2018-09-08 14:00
 */
public class TFunctionLoader implements TabooLibLoader.Loader {

    @Override
    public void postLoad(Plugin plugin, Class<?> pluginClass) {
        if (pluginClass.isAnnotationPresent(TFunction.class)) {
            TFunction function = pluginClass.getAnnotation(TFunction.class);
            try {
                Method method = pluginClass.getDeclaredMethod(function.enable());
                if (!Modifier.isStatic(method.getModifiers())) {
                    TLogger.getGlobalLogger().error(method.getName() + " is not a static method.");
                    return;
                }
                method.setAccessible(true);
                method.invoke(null);
                TabooLibAPI.debug("Function " + pluginClass.getSimpleName() + " loaded. (" + plugin.getName() + ")");
            } catch (NoSuchMethodException ignore) {
            } catch (Exception e) {
                TLogger.getGlobalLogger().warn("TFunction load Failed: " + pluginClass.getName());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unload(Plugin plugin, Class<?> pluginClass) {
        if (pluginClass.isAnnotationPresent(TFunction.class)) {
            TFunction function = pluginClass.getAnnotation(TFunction.class);
            try {
                Method method = pluginClass.getDeclaredMethod(function.disable());
                if (!Modifier.isStatic(method.getModifiers())) {
                    TLogger.getGlobalLogger().error(method.getName() + " is not a static method.");
                    return;
                }
                method.setAccessible(true);
                method.invoke(null);
                TabooLibAPI.debug("Function " + pluginClass.getSimpleName() + " unloaded. (" + plugin.getName() + ")");
            } catch (NoSuchMethodException ignore) {
            } catch (Exception e) {
                TLogger.getGlobalLogger().warn("TFunction unload Failed: " + pluginClass.getName());
                e.printStackTrace();
            }
        }
    }
}
