package fr.loudo.cameratester.screens;

import fr.loudo.cameratester.animations.GtaCharacterChange;
import fr.loudo.cameratester.sound.ModSounds;
import fr.loudo.cameratester.utils.ColorLibrary;
import fr.loudo.cameratester.utils.CustomUnicode;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public class GtaSwapScreen extends Screen {

    private ServerPlayerEntity player;
    private boolean isOpen;

    public GtaSwapScreen(ServerPlayerEntity player) {
        super(Text.of("GTA SWAP"));
        this.player = player;
        this.isOpen = true;
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private Vec3d getRandomLocation(Vec3d startPos) {
        int randomX = getRandomNumber(-800, 800);
        int randomZ = getRandomNumber(-800, 800);
        while(randomX > -20 && randomX < 20) {
            randomX = getRandomNumber(-800, 800);
        }
        while(randomZ > -20 && randomZ < 20) {
            randomZ = getRandomNumber(-800, 800);
        }
        int x = (int) startPos.getX() + randomX;
        int y = (int) startPos.getY();
        int z = (int) startPos.getZ() + randomZ;
        boolean safeToTeleport = false;

        while (!safeToTeleport) {
            Block below = player.getServerWorld().getBlockState(new BlockPos(x, y - 1, z)).getBlock();
            Block current = player.getServerWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
            Block above = player.getServerWorld().getBlockState(new BlockPos(x, y + 1, z)).getBlock();

            if (current != Blocks.AIR) {
                y++;
            } else if (below == Blocks.AIR) {
                y--;
            } else if (below == Blocks.WATER) {
                x++;
                y++;
            } else if (above == Blocks.AIR && below != Blocks.AIR) {
                safeToTeleport = true;
            }
        }

        return new Vec3d(x, y, z);

    }

    private void onButtonClick(String playerName) {
        this.close();
        player.networkHandler.sendPacket(new StopSoundS2CPacket(null, SoundCategory.MASTER));
        player.playSoundToPlayer(ModSounds.SELECT, SoundCategory.MASTER, 1, 1);
        GtaCharacterChange animation = new GtaCharacterChange(
                player.getPos(),
                getRandomLocation(player.getPos()),
                player,
                0.010
        );
        animation.start(playerName);
    }

    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(CustomUnicode.STEVE_FACE, (button) -> {
            onButtonClick("steve");
        }).dimensions(this.width - 75, this.height - 90, 30, 30).build());

        this.addDrawableChild(ButtonWidget.builder(CustomUnicode.ALEX_FACE, (button) -> {
            onButtonClick("alex");
        }).dimensions(this.width - 50, this.height - 50, 30, 30).build());

        this.addDrawableChild(ButtonWidget.builder(CustomUnicode.HEROBRINE_FACE, (button) -> {
            onButtonClick("herobrine");
        }).dimensions(this.width - 100, this.height - 50, 30, 30).build());
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, this.width, this.height, ColorLibrary.GREEN);

    }

    @Override
    protected void applyBlur() { }

    @Override
    public void blur() { }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
