package fr.loudo.cameratester.animations;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.loudo.cameratester.screens.ColorScreen;
import fr.loudo.cameratester.skins.SkinConstants;
import fr.loudo.cameratester.sound.ModSounds;
import fr.loudo.cameratester.utils.ColorLibrary;
import fr.loudo.cameratester.utils.CustomUnicode;
import fr.loudo.cameratester.utils.FakePlayer;
import fr.loudo.cameratester.utils.MathUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.List;
import java.util.UUID;

public class GtaCharacterChange {

    private static final MinecraftClient MINECRAFT_CLIENT = MinecraftClient.getInstance();

    private static final int FIRST_POINT_EACH_TICK = 15;
    private static final int SECOND_POINT_EACH_TICK = 35;
    private static final int FIRST_CORD_ADD = 20;
    private static final int CORD_ADD_EACH_TICK = 60;

    private int tick;
    private int currentPointTick;
    private int wentUpTimes;
    private ColorScreen colorScreen;
    private double t;
    private double moveEveryCord;
    private boolean animating;
    private boolean isInOutBody;
    private boolean isAtFirstPoint;
    private boolean isMovingBetween;

    private final ServerPlayerEntity player;
    private final double[] firstPos;
    private final double[] secondPos;
    private Entity camera;
    private FakePlayer clonePlayerFirstLoc;
    private FakePlayer clonePlayerSecondLoc;

    public GtaCharacterChange(Vec3d firstPos, Vec3d secondPos, ServerPlayerEntity player, double moveEveryCord) {
        this.firstPos = new double[]{firstPos.getX(), firstPos.getY(), firstPos.getZ()};
        this.secondPos = new double[]{secondPos.getX(), secondPos.getY(), secondPos.getZ()};
        this.player = player;
        this.moveEveryCord = moveEveryCord;
        this.camera = player.getCameraEntity();
    }

    private Entity initCamera() {
        ArmorStandEntity armorStand = new ArmorStandEntity(player.getWorld(), player.getX(), player.getY(), player.getZ());
        armorStand.setInvisible(true);
        armorStand.setNoGravity(true);
        armorStand.setInvulnerable(true);
        player.getServerWorld().spawnEntity(armorStand);
        return armorStand;
    }

    public void start(String playerName) {
        player.playSoundToPlayer(ModSounds.GOING_UP, SoundCategory.MASTER, 1, 1);

        spawnFakePlayers(playerName);
        addPlayersToScoreboard();


        this.camera = initCamera();
        player.setCameraEntity(camera);
        player.changeGameMode(GameMode.SPECTATOR);

        resetAnimationState();
        isAtFirstPoint = true;
        isInOutBody = true;
        colorScreen = new ColorScreen(getCharacterColor(playerName));
        MINECRAFT_CLIENT.execute(() -> MINECRAFT_CLIENT.setScreen(colorScreen));

    }

    private GameProfile generateGameProfile(Property skin) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), player.getDisplayName().getString());
        profile.getProperties().put("textures", skin);
        return profile;
    }

    private void addPlayersToScoreboard() {
        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam("notag");
        if (team == null) {
            team = scoreboard.addTeam("notag");
            team.setNameTagVisibilityRule(AbstractTeam.VisibilityRule.NEVER);
        }
        team.getPlayerList().add(player.getDisplayName().getString());
        team.getPlayerList().add(clonePlayerFirstLoc.getDisplayName().getString());
        team.getPlayerList().add(clonePlayerSecondLoc.getDisplayName().getString());
    }

    private void spawnFakePlayers(String playerName) {

        clonePlayerFirstLoc = new FakePlayer(player.getServerWorld(), generateGameProfile(SkinConstants.STEVE_SKIN));
        clonePlayerSecondLoc = new FakePlayer(player.getServerWorld(), generateGameProfile(SkinConstants.getSkinByName(playerName)));

        clonePlayerFirstLoc.setPosition(player.getPos());
        clonePlayerFirstLoc.setPitch(player.getPitch());
        clonePlayerFirstLoc.setYaw(player.getYaw());
        clonePlayerFirstLoc.setHeadYaw(player.getHeadYaw());

        clonePlayerSecondLoc.setPosition(secondPos[0], secondPos[1], secondPos[2]);
        clonePlayerSecondLoc.setPitch(player.getPitch());
        clonePlayerSecondLoc.setYaw(player.getYaw());
        clonePlayerSecondLoc.setHeadYaw(player.getHeadYaw());

        player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, clonePlayerFirstLoc));
        player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, clonePlayerSecondLoc));

        player.getServerWorld().spawnEntity(clonePlayerFirstLoc);
        player.getServerWorld().spawnEntity(clonePlayerSecondLoc);
    }

    public void stop() {
        player.setPosition(secondPos[0], secondPos[1], secondPos[2]);
        player.setCameraEntity(player);
        camera.remove(Entity.RemovalReason.KILLED);
        player.networkHandler.sendPacket(new PlayerRemoveS2CPacket(List.of(clonePlayerFirstLoc.getUuid(), clonePlayerSecondLoc.getUuid())));
        clonePlayerFirstLoc.remove(Entity.RemovalReason.KILLED);
        clonePlayerSecondLoc.remove(Entity.RemovalReason.KILLED);
        animating = false;
        player.changeGameMode(GameMode.CREATIVE);
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(null));
    }

    private void resetAnimationState() {
        tick = 0;
        t = 0;
        currentPointTick = FIRST_POINT_EACH_TICK;
        wentUpTimes = 0;
        animating = true;
        ServerTickEvents.START_SERVER_TICK.register(this::tickEvents);
    }

    private void tickEvents(MinecraftServer server) {
        if (!animating) return;

        if (isMovingBetween) {
            handleMovementBetweenPoints();
        } else if (isInOutBody) {
            handleCameraAnimation();
        } else {
            handlePositionChanges();
        }
    }


    private void handleMovementBetweenPoints() {
        if (t <= 1) {
            double[] newCord = MathUtils.moveBetweenPoints(firstPos, secondPos, t);
            camera.setPos(newCord[0], newCord[1] + FIRST_CORD_ADD + CORD_ADD_EACH_TICK * 3, newCord[2]);
            t += moveEveryCord;
        }

        if (t >= 1) {
            t = 1;
            tick++;
            if (tick == 10) {
                isAtFirstPoint = !isAtFirstPoint;
                currentPointTick = isAtFirstPoint ? FIRST_POINT_EACH_TICK : SECOND_POINT_EACH_TICK;
                colorScreen = new ColorScreen(ColorLibrary.GREEN);
                isMovingBetween = false;
                tick = 0;
                t = 0;
                player.networkHandler.sendPacket(new StopSoundS2CPacket(null, SoundCategory.MASTER));
            }
        }
    }

    private void handleCameraAnimation() {
        double[] start = {isAtFirstPoint ? firstPos[1] : camera.getY(), isAtFirstPoint ? player.getPitch() : camera.getPitch()};
        double[] end = {isAtFirstPoint ? firstPos[1] + FIRST_CORD_ADD : clonePlayerSecondLoc.getY(), isAtFirstPoint ? 90 : clonePlayerSecondLoc.getPitch()};

        double[] newCord = MathUtils.inOrOutBodyCamera(start, end, t);
        camera.setPos(camera.getX(), newCord[0], camera.getZ());
        camera.setPitch((float) newCord[1]);
        if(!isAtFirstPoint) {
            camera.setHeadYaw(clonePlayerSecondLoc.getHeadYaw());
            camera.setYaw(clonePlayerSecondLoc.getYaw());
        }

        t += isAtFirstPoint ? moveEveryCord * 2 : moveEveryCord * 1.1;

        if (t >= 0.8 || (!isAtFirstPoint && (clonePlayerSecondLoc.getPitch() == camera.getPitch()))) {
            isInOutBody = false;
            tick = 0;
            t = 0;
            if(!isAtFirstPoint) {
                stop();
            }
        }
    }

    private void handlePositionChanges() {
        if (tick % currentPointTick == 0 && wentUpTimes < 3) {
            if(isAtFirstPoint) {
                player.playSoundToPlayer(ModSounds.BOOM, SoundCategory.MASTER, 1, 1);
            } else {
                player.playSoundToPlayer(ModSounds.TAM, SoundCategory.MASTER, 1, 1);

            }
            sendWhiteFlash();
            camera.setPos(camera.getX(), camera.getY() + (isAtFirstPoint ? CORD_ADD_EACH_TICK : -CORD_ADD_EACH_TICK), camera.getZ());
            MINECRAFT_CLIENT.execute(() -> MINECRAFT_CLIENT.setScreen(colorScreen));
            wentUpTimes++;
        }

        if (tick == (currentPointTick * 2 + (isAtFirstPoint ? 15 : 30))) {

            isMovingBetween = isAtFirstPoint;
            isInOutBody = !isAtFirstPoint;
            tick = 0;
            wentUpTimes = 0;
            player.playSoundToPlayer(ModSounds.WHOOSH, SoundCategory.MASTER, 1, 1);
        }

        tick++;
    }

    private void sendWhiteFlash()
    {
        ServerPlayNetworkHandler connection = player.networkHandler;
        connection.sendPacket(new TitleS2CPacket(CustomUnicode.WHITE_SCREEN));
        connection.sendPacket(new TitleFadeS2CPacket(0, 7, 7));
    }

    private int getCharacterColor(String playerName) {

        switch (playerName) {
            case "steve":
                return ColorLibrary.BLUE;
            case "alex":
                return ColorLibrary.GREEN;
            case "herobrine":
                return ColorLibrary.ORANGE;
        }

        return 0;

    }



}
