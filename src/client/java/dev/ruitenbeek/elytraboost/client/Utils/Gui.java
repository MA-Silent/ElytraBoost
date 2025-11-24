package dev.ruitenbeek.elytraboost.client.Utils;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ruitenbeek.elytraboost.client.widgets.ModuleButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

import static dev.ruitenbeek.elytraboost.client.modules.OpenGui.keyBind;

public class Gui extends Screen {

    public static Map<ModuleButton, BooleanHolder> buttonWidgetMap =  new HashMap<>();

    private int x;
    private int y;

    private final int BoxWidth = 200;
    private final int BoxHeight = 150;

    public Gui(Text title) {
        super(title);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        float idkX = 200;
        float idkY = 100;

        drawRoundedRect(context, idkX, idkY, idkX+200, idkY+100, 20, 0xFF40E0D0);

//        context.fill((int)x-BoxWidth, (int)y-BoxHeight, (int)x+BoxWidth, (int)y+BoxHeight, 0xFF40E0D0);
        for (Element element : this.children()){
            if(element instanceof Drawable){
                ((Drawable) element).render(context, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    protected void init() {
        x = width/2;
        y= height/2;

        int offset = 0;

        if(!buttonWidgetMap.isEmpty()) {
            for (Map.Entry<ModuleButton, BooleanHolder> entry : buttonWidgetMap.entrySet()) {
                int buttonX = x-BoxWidth+10;
                int buttonY = y-BoxHeight+offset+10;

                entry.getKey().setAlpha(entry.getValue().value ? 255f : 126f);

                entry.getKey().setPosition(buttonX, buttonY);

                offset+=entry.getKey().getHeight()+10;

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

    private void drawRoundedRect(DrawContext context, float x1, float y1, float x2, float y2, float radius, int color) {
        radius = Math.min(radius, Math.min(x2 - x1, y2 - y1) / 2.0f);
        Tessellator tessellator = Tessellator.getInstance();
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        float z = 0.0F;

        float cx1 = x1 + radius;
        float cy1 = y1 + radius;
        float cx2 = x2 - radius;
        float cy2 = y2 - radius;

        final int SEGMENTS = 8;

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        BufferBuilder bufferRect = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

//      Center
        bufferRect.vertex(matrix, cx1, y1, z).color(r,g,b,a);
        bufferRect.vertex(matrix, cx1, y2, z).color(r,g,b,a);
        bufferRect.vertex(matrix, cx2, y2, z).color(r,g,b,a);
        bufferRect.vertex(matrix, cx2, y1, z).color(r,g,b,a);


//      Left
        bufferRect.vertex(matrix, x1, cy1, z).color(r, g, b, a);
        bufferRect.vertex(matrix, x1, cy2, z).color(r, g, b, a);
        bufferRect.vertex(matrix, cx1, cy2, z).color(r, g, b, a);
        bufferRect.vertex(matrix, cx1, cy1, z).color(r, g, b, a);

//      Right
        bufferRect.vertex(matrix, cx2, cy1, z).color(r, g, b, a);
        bufferRect.vertex(matrix, cx2, cy2, z).color(r, g, b, a);
        bufferRect.vertex(matrix, x2, cy2, z).color(r, g, b, a);
        bufferRect.vertex(matrix, x2, cy1, z).color(r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(bufferRect.end());

        BufferBuilder bufferCorners = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        for (int i = 0; i <= SEGMENTS; i++) {
            double angle = Math.PI + (i * Math.PI / 2 / SEGMENTS);
            bufferCorners.vertex(matrix, cx1 + (float)(Math.cos(angle) * radius), cy1 + (float)(Math.sin(angle) * radius), z).color(r, g, b, a);
        }

        BufferRenderer.drawWithGlobalProgram(bufferCorners.end());

        RenderSystem.disableBlend();
    }

    public final static Gui INSTANCE = new Gui(Text.of("ElytraBoost Gui"));
}
