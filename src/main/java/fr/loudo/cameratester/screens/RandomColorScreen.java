package fr.loudo.cameratester.screens;

import fr.loudo.cameratester.utils.ColorLibrary;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomColorScreen extends Screen {

    private List<Integer> colors = Arrays.asList(ColorLibrary.BLUE, ColorLibrary.GREEN, ColorLibrary.YELLOW, ColorLibrary.ORANGE);
    private int randomColorIndex;


    public static void changeScreen() {
        RandomColorScreen randomColorScreen = new RandomColorScreen();
        MinecraftClient minecraft = MinecraftClient.getInstance();
        minecraft.execute(() -> minecraft.setScreen(randomColorScreen));
    }

    private RandomColorScreen() {
        super(Text.of(""));
        Random random = new Random();
        randomColorIndex = random.nextInt(colors.size());
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, this.width, this.height, colors.get(randomColorIndex));
    }


    @Override
    protected void applyBlur() {}

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void blur() {}
}
