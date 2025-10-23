package dev.ruitenbeek.elytraboost.client.Utils;

import static dev.ruitenbeek.elytraboost.client.ElytraboostClient.mc;

public class canUpdate {
    public static boolean canUpdate() {
        return mc != null && mc.world != null && mc.player != null;
    }
}
