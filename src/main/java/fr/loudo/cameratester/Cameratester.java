package fr.loudo.cameratester;

import fr.loudo.cameratester.keys.ModKeys;
import fr.loudo.cameratester.screens.GtaSwapScreen;
import fr.loudo.cameratester.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

public class Cameratester implements ModInitializer {

    public static final String MOD_ID = "cameratester";
    public static GtaSwapScreen gtaSwapScreen;

    @Override
    public void onInitialize() {

        ModKeys.registerKeys();
        ModSounds.registerSounds();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient != null && minecraftClient.player != null) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(minecraftClient.player.getUuid());

                if (ModKeys.OPEN_SWITCH_CHARACTER.isPressed()) {
                    gtaSwapScreen = new GtaSwapScreen(player);
                    minecraftClient.execute(() -> minecraftClient.setScreen(gtaSwapScreen));
                    player.playSoundToPlayer(ModSounds.SELECT, SoundCategory.MASTER, 1, 1);
                    player.playSoundToPlayer(ModSounds.CHOOSING, SoundCategory.MASTER, 1, 1);
                }
            }
        });


    }
}
