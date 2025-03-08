package fr.loudo.cameratester.screens;

import fr.loudo.cameratester.utils.ColorLibrary;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ColorScreen extends Screen {

    private int color;

    public ColorScreen(int color) {
        super(Text.of(""));
        this.color = color;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, this.width, this.height, color);
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
