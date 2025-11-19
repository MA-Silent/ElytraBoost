package dev.ruitenbeek.elytraboost.client.modules;

import dev.ruitenbeek.elytraboost.client.modModule;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

import static dev.ruitenbeek.elytraboost.client.ElytraboostClient.mc;
import static dev.ruitenbeek.elytraboost.client.Utils.canUpdate.canUpdate;

public class AutoTotem implements modModule{

    private ClientPlayerEntity client = null;

    private AutoTotem() {
        ClientTickEvents.END_CLIENT_TICK.register((_wont_be_used) -> {
            if(client == null) client = mc.player;
            if(!canUpdate() || this.client == null) return;

            PlayerInventory inventory = client.getInventory();

            if(inventory.containsAny( Set.of(Items.TOTEM_OF_UNDYING) ) && inventory.getStack(40).isEmpty() ) {
                int totemSlot = findTotemSlot(inventory);
                client.sendMessage(Text.of(""+totemSlot));
                if(totemSlot == -1) return;

                if(totemSlot > 8){
                    client.networkHandler.sendPacket(
                            new net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket(
                                    0,
                                    client.playerScreenHandler.nextRevision(),
                                    totemSlot,
                                    40,
                                    net.minecraft.screen.slot.SlotActionType.SWAP,
                                    client.playerScreenHandler.getCursorStack().copy(),
                                    new Int2ObjectOpenHashMap<>()
                            )
                    );
                } else {
                    client.networkHandler.sendPacket(
                            new net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket(
                                    0,
                                    client.playerScreenHandler.nextRevision(),
                                    totemSlot+36,
                                    40,
                                    net.minecraft.screen.slot.SlotActionType.SWAP,
                                    client.playerScreenHandler.getCursorStack().copy(),
                                    new Int2ObjectOpenHashMap<>()
                            )
                    );
                }


            }
        });
    }

    private int findTotemSlot(PlayerInventory inventory) {

//        int main = inventory.getSlotWithStack(Items.TOTEM_OF_UNDYING.getDefaultStack());
        int hotBarSize = PlayerInventory.getHotbarSize();
        int mainInventoryEnd = hotBarSize + inventory.main.size() - 1; // 35

        for (int slot = hotBarSize; slot < mainInventoryEnd; slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
                return slot;
            }
        }

        for(int slot = 0; slot < 9; slot++) {
            ItemStack stack = inventory.getStack(slot);
            if(stack.isOf(Items.TOTEM_OF_UNDYING)) {
                return slot;
            }
        }
        return -1;
    }

    private static AutoTotem INSTANCE;

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new AutoTotem();
        }
    }
}
