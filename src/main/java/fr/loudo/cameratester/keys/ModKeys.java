package fr.loudo.cameratester.keys;

import fr.loudo.cameratester.Cameratester;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ModKeys {

    public static KeyBinding OPEN_SWITCH_CHARACTER;

    private static KeyBinding registerKey(String translationKey, int code)
    {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                translationKey,
                InputUtil.Type.KEYSYM,
                code,
                "fr.loudo" + Cameratester.MOD_ID
        ));
    }

    public static void registerKeys() {
        OPEN_SWITCH_CHARACTER = registerKey("key.gta.switch_character", InputUtil.GLFW_KEY_U);
    }

}
