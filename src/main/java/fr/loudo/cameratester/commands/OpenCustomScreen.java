package fr.loudo.cameratester.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.loudo.cameratester.animations.GtaCharacterChange;
import fr.loudo.cameratester.screens.GtaSwapScreen;
import fr.loudo.cameratester.sound.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class OpenCustomScreen {

    public static Vec3d positionSwap;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("openscreen").executes(OpenCustomScreen::open));
    }


    public static int open(CommandContext<ServerCommandSource> context)
    {

        GtaSwapScreen gtaSwapScreen = new GtaSwapScreen(context.getSource().getPlayer());

        MinecraftClient minecraft = MinecraftClient.getInstance();
        minecraft.execute(() -> minecraft.setScreen(gtaSwapScreen));
        context.getSource().getPlayer().playSoundToPlayer(ModSounds.SELECT, SoundCategory.MASTER, 1, 1);
        context.getSource().getPlayer().playSoundToPlayer(ModSounds.CHOOSING, SoundCategory.MASTER, 1, 1);

        return Command.SINGLE_SUCCESS;
    }


}
