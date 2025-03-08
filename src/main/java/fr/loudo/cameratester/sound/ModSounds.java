package fr.loudo.cameratester.sound;

import fr.loudo.cameratester.Cameratester;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static SoundEvent BOOM;
    public static SoundEvent WHOOSH;
    public static SoundEvent TAM;
    public static SoundEvent CHOOSING;
    public static SoundEvent GOING_UP;
    public static SoundEvent SELECT;


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(Cameratester.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        BOOM = registerSoundEvent("boom");
        WHOOSH = registerSoundEvent("whoosh");
        TAM = registerSoundEvent("tam");
        CHOOSING = registerSoundEvent("choosing");
        GOING_UP = registerSoundEvent("going_up");
        SELECT = registerSoundEvent("select");
    }

}
