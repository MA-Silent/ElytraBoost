package dev.ruitenbeek.elytraboost.client.widgets;

import dev.ruitenbeek.elytraboost.client.Utils.BooleanHolder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import static dev.ruitenbeek.elytraboost.client.ElytraboostClient.mc;

public class ModuleButton extends ClickableWidget {
    private final ModuleButtonPressAction onPress;
    private final Text message;
    private BooleanHolder enabled;

    public ModuleButton(int x, int y, int width, int height, Text message, ModuleButtonPressAction onPress, BooleanHolder enabled) {
        super(x, y, width, height, message);
        this.onPress = onPress;
        this.message = message;
        this.enabled = enabled;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress.onPress(this);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        return;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        assert enabled != null;
        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), enabled.value ? 0xB32196F3: 0x6193B5D1 );
        context.drawText(mc.textRenderer, message, this.getX()+5, this.getY()+(this.getHeight()/4), 0xffffff, false);
    }

}
