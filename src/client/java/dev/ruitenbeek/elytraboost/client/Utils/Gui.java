package dev.ruitenbeek.elytraboost.client.Utils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

import static dev.ruitenbeek.elytraboost.client.modules.OpenGui.keyBind;

public class Gui extends Screen {

    public static Map<ButtonWidget, BooleanHolder> buttonWidgetMap =  new HashMap<>();

    public Gui(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        if(!buttonWidgetMap.isEmpty()) {
            for (Map.Entry<ButtonWidget, BooleanHolder> entry : buttonWidgetMap.entrySet()) {
               entry.getKey().setAlpha(entry.getValue().value ? 255f : 126f);
               this.addDrawableChild(entry.getKey());
            }
        }
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
