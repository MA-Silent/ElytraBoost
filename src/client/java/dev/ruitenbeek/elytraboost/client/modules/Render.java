package dev.ruitenbeek.elytraboost.client.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import static dev.ruitenbeek.elytraboost.client.Utils.canUpdate.canUpdate;
import static dev.ruitenbeek.elytraboost.client.ElytraboostClient.mc;

public class Render implements modModule {

    private static KeyBinding keyBind;
    private final ArrayList<BlockPos> chestPositions = new ArrayList<>();

    private Render(){
        keyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "getAmountBlockEntities",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                "ElytraBoost"
        ));

        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            chestPositions.addAll(getChestPositionsInChunk(chunk.getPos()));
        });

        WorldRenderEvents.LAST.register(context -> {
            if (mc.world == null) return;

            MatrixStack matrices = context.matrixStack();
            VertexConsumerProvider.Immediate vertexConsumers = mc.getBufferBuilders().getEntityVertexConsumers();
            float tickDelta = context.tickCounter().getTickDelta(true);

            vertexConsumers.draw();

            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();

            matrices.push();

            double camX = mc.gameRenderer.getCamera().getPos().x;
            double camY = mc.gameRenderer.getCamera().getPos().y;
            double camZ = mc.gameRenderer.getCamera().getPos().z;

            matrices.translate(-camX, -camY, -camZ);

            for (BlockPos pos : chestPositions) {
                BlockEntity be = mc.world.getBlockEntity(pos);
                if (be != null && be.getType() == BlockEntityType.CHEST) {
                    matrices.push();
                    matrices.translate(pos.getX(), pos.getY(), pos.getZ());
                    mc.getBlockEntityRenderDispatcher().render(be, tickDelta, matrices, vertexConsumers);
                    matrices.pop();
                }
            }

            matrices.pop();


            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
        });

    }

    private ArrayList<BlockPos> getChestPositionsInChunk(ChunkPos chunkPos) {
        ArrayList<BlockPos> found = new ArrayList<>();
        if (!canUpdate()) return found;

        if (mc.world.isChunkLoaded(chunkPos.x, chunkPos.z)) {
            WorldChunk currentChunk = mc.world.getChunk(chunkPos.x, chunkPos.z);
            for (Map.Entry<BlockPos, BlockEntity> entry : currentChunk.getBlockEntities().entrySet()) {
                if (entry.getValue().getType() == BlockEntityType.CHEST) {
                    found.add(entry.getKey());
                }
            }
        }
        return found;
    }

    private static Render INSTANCE;

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new Render();
        }
    }
}
