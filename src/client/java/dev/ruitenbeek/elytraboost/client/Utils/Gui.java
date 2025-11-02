package dev.ruitenbeek.elytraboost.client.Utils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import static dev.ruitenbeek.elytraboost.client.modules.OpenGui.keyBind;
import static dev.ruitenbeek.elytraboost.client.modules.OpenGui.open;

public class Gui extends Screen {

    private Gui(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        ButtonWidget checkBox = ButtonWidget.builder(Text.of("Disconnect"), (btn) -> {
            assert client != null;

            client.disconnect();
        }).build();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyBind.matchesKey(keyCode, scanCode)) {
            assert client != null;

            client.setScreen(null);
            open = false;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public static Gui INSTANCE = new Gui(Text.of("Elytra Boost Client"));
}
