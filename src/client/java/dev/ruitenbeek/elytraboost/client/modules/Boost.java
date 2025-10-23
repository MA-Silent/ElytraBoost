package dev.ruitenbeek.elytraboost.client.modules;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static dev.ruitenbeek.elytraboost.client.ElytraboostClient.*;
import static dev.ruitenbeek.elytraboost.client.Utils.canUpdate.canUpdate;

public class Boost implements modModule {

    private static KeyBinding keyBind;
    private final List<FireworkRocketEntity> fireworks = new ArrayList<>();
    private final int durationInSeconds = 1;

    private Boost() {

        keyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Boost",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "ElytraBoost"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyBind.wasPressed()) {
                boost();
            }

            if(!canUpdate()) return;

            Iterator<FireworkRocketEntity> iterator = fireworks.iterator();

            int maxAge = durationInSeconds * 20;

            while (iterator.hasNext()) {
                FireworkRocketEntity firework = iterator.next();
                if (firework.isAlive() && firework.age >= maxAge) {
                    mc.world.removeEntity(firework.getId(), Entity.RemovalReason.DISCARDED);
                    iterator.remove();
                }
            }

        });
    }

    private void boost() {
        if (!canUpdate()) return;

        if (mc.player.isFallFlying() && mc.currentScreen == null) {
            ItemStack itemStack = Items.FIREWORK_ROCKET.getDefaultStack();
            itemStack.set(DataComponentTypes.FIREWORKS, new FireworksComponent(0, Objects.requireNonNull(itemStack.get(DataComponentTypes.FIREWORKS)).explosions()));

            FireworkRocketEntity entity = new FireworkRocketEntity(mc.world, itemStack, mc.player);
            fireworks.add(entity);
            mc.world.playSoundFromEntity(mc.player, entity, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
            mc.world.addEntity(entity);
        }
    }

    private static Boost INSTANCE;

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new Boost();
        }
    }
}
