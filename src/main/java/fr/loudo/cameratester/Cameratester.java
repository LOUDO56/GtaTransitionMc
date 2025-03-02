package fr.loudo.cameratester;

import fr.loudo.cameratester.commands.CreateCameraCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Cameratester implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CreateCameraCommand.register(dispatcher);
        });


    }
}
