package dev.ruitenbeek.elytraboost.client.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ruitenbeek.elytraboost.client.modModule;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.chunk.WorldChunk;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.ruitenbeek.elytraboost.client.Utils.canUpdate.canUpdate;
import static dev.ruitenbeek.elytraboost.client.ElytraboostClient.mc;

public class StorageEsp implements modModule {

    private final ArrayList<BlockPos> chestPositions = new ArrayList<>();
    private final List<BlockEntityType<?>> allowedBlocks = List.of(BlockEntityType.CHEST, BlockEntityType.SHULKER_BOX);
    public static boolean enabled = true;

    private StorageEsp(){
        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            chestPositions.addAll(getChestPositionsInChunk(chunk.getPos()));
        });

        WorldRenderEvents.END.register(context -> {
            if (!canUpdate()) return;

            MatrixStack pose = context.matrixStack();
            float tickDelta = context.tickCounter().getTickDelta(true);
            Camera camera = context.camera();
            Tessellator tessellator = Tessellator.getInstance();

            assert pose != null;

            pose.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            pose.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() - 180f));


            BufferBuilder builder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            final int r = 255;
            final int g = 0;
            final int b = 0;
            final int a = 200;

            for (BlockPos pos : chestPositions) {
                pose.push();

                final double xOffset = pos.getX() + (0.5 - camera.getPos().x);
                final double yOffset = pos.getY() + (0.5 - camera.getPos().y);
                final double zOffset = pos.getZ() + (0.5 - camera.getPos().z);
                pose.translate(xOffset, yOffset, zOffset);
                pose.scale(0.5f, 0.5f, 0.5f);
                MatrixStack.Entry resultMatrix = pose.peek();

                // -Z
                builder.vertex(resultMatrix, -1, -1, -1).color(r, g, b, a);
                builder.vertex(resultMatrix, -1, 1, -1).color(r, g, b, a);
                builder.vertex(resultMatrix, 1, 1, -1).color(r, g, b, a);
                builder.vertex(resultMatrix, 1, -1, -1).color(r, g, b, a);

                // +Z
                builder.vertex(resultMatrix, -1, -1, 1).color(r, g, b, a);
                builder.vertex(resultMatrix, 1, -1, 1).color(r, g, b, a);
                builder.vertex(resultMatrix, 1, 1, 1).color(r, g, b, a);
                builder.vertex(resultMatrix, -1, 1, 1).color(r, g, b, a);

                // -Y
                builder.vertex(resultMatrix, -1, -1, -1).color(r, g, b, a);
                builder.vertex(resultMatrix, 1, -1, -1).color(r, g, b, a);
                builder.vertex(resultMatrix, 1, -1, 1).color(r, g, b, a);
                builder.vertex(resultMatrix, -1, -1, 1).color(r, g, b, a);

                // +Y
                builder.vertex(resultMatrix, -1, 1, -1).color(r, g, b, a);
                builder.vertex(resultMatrix, -1, 1, 1).color(r, g, b, a);
                builder.vertex(resultMatrix, 1, 1, 1).color(r, g, b, a);
                builder.vertex(resultMatrix, 1, 1, -1).color(r, g, b, a);

                // -X
                builder.vertex(resultMatrix, -1, -1, -1).color(r, g, b, a);
                builder.vertex(resultMatrix, -1, -1, 1).color(r, g, b, a);
                builder.vertex(resultMatrix, -1, 1, 1).color(r, g, b, a);
                builder.vertex(resultMatrix, -1, 1, -1).color(r, g, b, a);

                // +X
                builder.vertex(resultMatrix, 1, -1, -1).color(r, g, b, a);
                builder.vertex(resultMatrix, 1, 1, -1).color(r, g, b, a);
                builder.vertex(resultMatrix, 1, 1, 1).color(r, g, b, a);
                builder.vertex(resultMatrix, 1, -1, 1).color(r, g, b, a);

                pose.pop();
            }

            if (!chestPositions.isEmpty()) {
                var buffer = builder.end();

                if (enabled) {
                    RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                    RenderSystem.setShaderColor(1f, 1f, 1f, a / 255f);
                    RenderSystem.enableBlend();
                    RenderSystem.depthFunc(GL11.GL_ALWAYS);

                    BufferRenderer.drawWithGlobalProgram(buffer);

                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                    RenderSystem.depthFunc(GL11.GL_LEQUAL);
                    RenderSystem.disableBlend();
                }
                buffer.close();
            }

        });

    }

    private ArrayList<BlockPos> getChestPositionsInChunk(ChunkPos chunkPos) {
        ArrayList<BlockPos> found = new ArrayList<>();
        if (!canUpdate()) return found;

        if (mc.world.isChunkLoaded(chunkPos.x, chunkPos.z)) {
            WorldChunk currentChunk = mc.world.getChunk(chunkPos.x, chunkPos.z);
            for (Map.Entry<BlockPos, BlockEntity> entry : currentChunk.getBlockEntities().entrySet()) {
                if (allowedBlocks.contains(entry.getValue().getType())) {
                    found.add(entry.getKey());
                }
            }
        }
        return found;
    }

    private static StorageEsp INSTANCE;

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new StorageEsp();
        }
    }
}
