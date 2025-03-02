package fr.loudo.cameratester.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.loudo.cameratester.animations.GtaCharacterChange;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class CreateCameraCommand {

    public static Vec3d positionSwap;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("gtaswap")
                .then(CommandManager.literal("play")
                        .then(CommandManager.argument("moveEveryCord", DoubleArgumentType.doubleArg())
                                .executes(context -> execute(context, DoubleArgumentType.getDouble(context, "moveEveryCord"))))
                        .executes(context -> execute(context, 0.010))
                )
                .then(CommandManager.literal("pose").executes(CreateCameraCommand::setPose))
        );
    }


    public static int execute(CommandContext<ServerCommandSource> context, double moveEveryCord)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if(positionSwap == null) {
            player.sendMessage(Text.literal("§cSwap pose not set."));
            return Command.SINGLE_SUCCESS;
        }


        GtaCharacterChange animation = new GtaCharacterChange(
                player.getPos(),
                positionSwap,
                player,
                0.010
        );
        animation.start();

        return Command.SINGLE_SUCCESS;
    }

    public static int setPose(CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = context.getSource().getPlayer();

        positionSwap = player.getPos();

        player.sendMessage(Text.literal("§aSwap pose sucessfully set!"));

        return Command.SINGLE_SUCCESS;
    }

}
