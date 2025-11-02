package dev.ruitenbeek.elytraboost.client;

import org.reflections.Reflections;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import static dev.ruitenbeek.elytraboost.client.ElytraboostClient.LOG;

public class LoadModules {

    public static void loadAll() {
        Reflections reflections = new Reflections("dev.ruitenbeek.elytraboost.client.modules");
        Set<Class<? extends modModule>> modules = reflections.getSubTypesOf(modModule.class);

        for (Class<? extends modModule> moduleClass : modules) {
            try {
                Method initMethod = moduleClass.getDeclaredMethod("init");

                if (Modifier.isStatic(initMethod.getModifiers())) {
                    initMethod.invoke(null);
                    LOG.warn("\u001B[38;2;90;247;47m[ElytraBoost] Initialized module: " + moduleClass.getSimpleName()+ "\u001B[0m");
                } else {
                    LOG.error("[ElytraBoost] init() in " + moduleClass.getName() + " is not static!");
                }
            } catch (NoSuchMethodException e) {
                System.err.println("[ElytraBoost] " + moduleClass.getName() + " missing static init() method!");
            } catch (Exception e) {
                LOG.error("[ElytraBoost] failed: " + e.getMessage());
            }
        }
    }
}
