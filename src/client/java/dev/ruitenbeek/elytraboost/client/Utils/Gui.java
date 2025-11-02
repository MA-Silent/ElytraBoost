package dev.ruitenbeek.elytraboost.client.Utils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import static dev.ruitenbeek.elytraboost.client.modules.OpenGui.keyBind;
import static dev.ruitenbeek.elytraboost.client.modules.StorageEsp.enabled;

public class Gui extends Screen {

    public Gui(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        ButtonWidget checkBox = ButtonWidget.builder(Text.of("StorageEsp"), (btn) -> {
            assert client != null;
            enabled = !enabled;
        }).build();

        checkBox.setDimensions(100,20);
        checkBox.setAlpha(enabled ? 255f : 126f);

        this.addDrawableChild(checkBox);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyBind.matchesKey(keyCode, scanCode)) {
            assert client != null;

            client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        this.clearAndInit();
        return handled;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public final static Gui INSTANCE = new Gui(Text.of("ElytraBoost Gui"));
}
