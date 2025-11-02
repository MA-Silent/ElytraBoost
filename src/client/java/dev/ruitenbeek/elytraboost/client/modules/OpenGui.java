package dev.ruitenbeek.elytraboost.client.modules;

import dev.ruitenbeek.elytraboost.client.Utils.Gui;
import dev.ruitenbeek.elytraboost.client.modModule;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class OpenGui implements modModule {

    public static boolean open = false;
    public static KeyBinding keyBind;

    private OpenGui() {

        keyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "ElytraBoost"
        ));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
           while (keyBind.wasPressed()) {
               if(!open){
                   client.setScreen(Gui.INSTANCE);
                   open = true;
               }
           }
        });

    }

    private static OpenGui INSTANCE;

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new OpenGui();
        }
    }
}



