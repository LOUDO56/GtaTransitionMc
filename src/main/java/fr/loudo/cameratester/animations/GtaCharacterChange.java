package fr.loudo.cameratester.animations;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.loudo.cameratester.Cameratester;
import fr.loudo.cameratester.screens.ColorScreen;
import fr.loudo.cameratester.screens.RandomColorScreen;
import fr.loudo.cameratester.sound.ModSounds;
import fr.loudo.cameratester.utils.ColorLibrary;
import fr.loudo.cameratester.utils.CustomUnicode;
import fr.loudo.cameratester.utils.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.List;
import java.util.UUID;

public class GtaCharacterChange {

    private final Property STEVE_SKIN = new Property(
            "textures",
            "ewogICJ0aW1lc3RhbXAiIDogMTc0MTQ0MzE4NTQ1NCwKICAicHJvZmlsZUlkIiA6ICJjYjhlYWY1MDc0ZmI0MjU3YjFlNWUxNTA1YzRmYzY2MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDYW1wZXI3NzYiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE5MWM0OTRiZjA2ZTYzMjg0YWI5MDdmMDRiNmQ5YTFiNWU2MzlmYTUxNmMzOTQzMTA2ZWEzNmUwMWYwMWJkMCIKICAgIH0KICB9Cn0=",
            "pWCNuKL7a0PxRg8wc3nBzR3tQ7RwotIbxqGO2A2APVqTeVuGDSPlP8VH+Cx6IPnFuJxn9MnJAitR/ZplRN3MaIpTM3o/o0fMD05Cpt5LEWt8dF6JBBG+6mN6LNdBwr48JazIAPjgVp8E70Qi437Qij+SGDBvvw7lG5lEPq8B0Z3CMPMLm7ot0nuYwMtpcgcNjXQEtPtiIAOcWd08Grzrm/kbMSdFhdw7j8ZKAxnTxsVtWhl2zaZWmtQjXmaW5VcngKOXs8SfZzQkQ7CZer5UKrFr9+xEPjFEEtdriQk/6IFtNym8IrS3h8/fp20Uldw1F1Ozb1KKmRJc4Ca5uPckNmqIqZ7YI+GYelPECyfZ1n/3AgPDMeC9uUQ917qWf9orDx31T7wzt2exVj/+cxGOuY4TdTj7cWsnZJJT/qYiUQZN2txfGF1Zc8I4bXANFTG7TVhcOt6CYNhn5ysQ0Mx1TJBcVgPlR21vnOSkCKjqfiNTmVHiu+7kFL4tShX9hoKZScR53j50y8ml3Fm/wKie/UFN5UhMLEVD5Y0MT5Or6l5h1acYxTUMZEx/mQEfibMwckBS5irz4erjY4FwCbrZW4YIxedwwII6SPZQKD4C5mG66iJ5AIGiFh0E58w/gCx1bGM/BiMkhDaXyDUpkWBJd03TVVEISk1M9HTWAvmNtyE="
    );
    private final Property ALEX_SKIN = new Property(
            "textures",
            "ewogICJ0aW1lc3RhbXAiIDogMTc0MTQ0MzQwNTkzNywKICAicHJvZmlsZUlkIiA6ICJmZmE1Y2FiM2ZlMzE0ZDgyODk3NTg3MmYxYmY2NDUyYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJjaGFybG90dGViYWtlciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mYWM4YTRkYTY1ZmQzMWU4ZjhjZWQ4NzFmOTllOWE5MTJlZjMxMWFhZDY2NzBhZDQ5M2FiMGNkMTdkMDc5NjU1IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            "pz+fllKrJdSxzZsX2fnEBuCKyoC3+qf0MN4OA8jwaf5guAYol1oDW36I7YuWdj4TDWqKTqDth2Q1oNSQ1tabij8Lrogxv3fEY2x/caOl50ighFALhN8WQeZtZ6VXLBOPWuSIEEtViRIhEdkermJz/ybBUCl9LN6t7FU5+VaAE4HQHD9ACH+XZoSu+eoObmtpSAHpUql6P7ORlquH8NBUs7CO6+7286eD+0v1mi8gQKV5/fXcyAVkO0D1dwlPmWVMqmPAO8FQEIy2atbP73FUfvezgtlDQqL/g8Y34F4tdCJnw6sgrfrnXiz912SLWzHdnPtni7zBsq+uP3e2TaU4aoZjfnfZAw2hDfNDxmbqlnU0DA8zXISnI9l7xeTvD66Ac+I+QJ383kq8g1y/3/mhVBhJChDuA+PqgWd7MPn1/1IuToGnd/CCS14zeRWuXGkJcR8X1a3H7T8dhYJyCLFkifKdiggYyEIg/RyCwfORX/8BbITfu458bPdWUlGQfZ6hdmhk62izw0N+3mNQjetZ3lCYmXnl/AkrEgAdeOpwAD08CMSaO+UaJgdN7w0ZH2YNrmPXT5kTYVmkoA3eR7KSQ/0fpKLJozEBog0fd7iuQWmU8qH4A+Fos25nnM5RupvHTAWdrem+68XZQpKWwLsdls71hUGT4ZANo8iKueJuhE0="
    );
    private final Property HEROBRINE_SKIN = new Property(
            "textures",
            "ewogICJ0aW1lc3RhbXAiIDogMTc0MTQ0MzQ3NjUyMywKICAicHJvZmlsZUlkIiA6ICJkNjNmY2Y2Yjc0Njg0MGFkODRkNTc1ZDU0YzIzZTZiYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJXdXplUmlicyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MmUwZThlMzZmNjhkOWEwM2ViM2EyNTdkMDJmZjViNzk0OTExYmU5NjdmYWRkNTljZTUxMjA4NTY4NWExMjA4IgogICAgfSwKICAgICJDQVBFIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jZDlkODJhYjE3ZmQ5MjAyMmRiZDRhODZjZGU0YzM4MmE3NTQwZTExN2ZhZTdiOWEyODUzNjU4NTA1YTgwNjI1IgogICAgfQogIH0KfQ==",
            "bEjdLC8RPNH/jRjjbSfBc23nRgeNj5OP7NESS6QyJ7SJf6xrCuaqJHXYzd3QZTcQjl1av7nTtbCPg/ZbWAwEubBsWmjwb6EB0EaJEhnYSNJD9eE06VPOK0XBUVghzW6e/VaSDeY1CschP9ijuXAlwOe5oI06CKMCPsZohMJVzeJN2iWLOwfLieWRRxAuO5yyzs+cFwVXdlnYpyhV33LSq3SDqD1Ot5urC3T3rTjq2OBm8W+ViQTb2AQFMp8FYT6EKry8+7rbMpBicEfIILtIdyQeFWnZhed5a63fMbHcBB4c75xK/7svSWGegaoj1WGhin+LN9iwYArk69ngAC3D0KaOZJnL8z4mF5v3eT6jebmLEZBZDgHoRBe3quDHAXmmcwXWhkXBMG5d+iR6tnxg2NIf5Bag7vFe+nrfbXXs8RrefxqpN4dWuWiGExF1uruWhmbVFN27yB4AYPKGUnaPSevwWb+KwixI4fKl+M1sX49kz+Sri6dOkSpio2dcgUKkHDED6gJF8B5Tn8Zt7ypvZ2hjGFLst8jty+0HuWAz1QXq1KzFiCcyiLcdxzq1V2q+VkF5RFm7OGl3Uinhu3dlpx3dJCOmEym3kotSd00gC/zPbj7TXGReGq6/bCUMP6fA7GvpC3jo1ss0socE8aUobmB//7mH3xjZlqLQNHi4fkI="
    );


    private final int FIRST_POINT_EACH_TICK = 15;
    private final int SECOND_POINT_EACH_TICK = 35;
    private int currentPointTick;
    private int wentUpTimes;

    private int tick;
    private double[] firstPos;
    private double[] secondPos;
    private Entity camera;
    private ServerPlayerEntity player;
    private boolean isAtFirstPoint;
    private boolean isAtSecondPoint;
    private boolean isMovingBetween;
    private boolean isInOutBody;
    private final boolean DEBUG = false;

    private double moveEveryCord;
    private boolean animating;
    private double t;

    private FakePlayer clonePlayerFirstLoc;
    private FakePlayer clonePlayerSecondLoc;

    private final int FIRST_CORD_ADD = 20;
    private final int CORD_ADD_EACH_TICk = 60;

    public GtaCharacterChange(Vec3d firstPos, Vec3d secondPos, ServerPlayerEntity player, double moveEveryCord) {
        this.firstPos = new double[]{firstPos.getX(), firstPos.getY(), firstPos.getZ()};
        this.secondPos = new double[]{secondPos.getX(), secondPos.getY(), secondPos.getZ()};
        this.camera = player.getCameraEntity();
        this.player = player;
        isAtFirstPoint = false;
        isAtSecondPoint = false;
        isMovingBetween = false;
        animating = false;
        this.moveEveryCord = moveEveryCord;
    }

    private Entity initCamera()
    {
        ArmorStandEntity armorStand = new ArmorStandEntity(player.getWorld(), player.getX(), player.getY(), player.getZ());
        armorStand.setInvisible(true);
        armorStand.setNoGravity(true);
        armorStand.setInvulnerable(true);
        armorStand.setPitch(player.getPitch());
        armorStand.setYaw(player.getYaw());
        armorStand.setHeadYaw(player.getHeadYaw());
        player.getServerWorld().spawnEntity(armorStand);
        this.camera = armorStand;
        return armorStand;
    }

    public void start(String playerName)
    {
        player.playSoundToPlayer(ModSounds.GOING_UP, SoundCategory.MASTER, 1, 1);
        GameProfile gameProfile1 = new GameProfile(UUID.randomUUID(), player.getDisplayName().getString());
        GameProfile gameProfile2 = new GameProfile(UUID.randomUUID(), player.getDisplayName().getString());
            gameProfile1.getProperties().put("textures", STEVE_SKIN);

        if(playerName != null) {
            GameProfile pGameProfile = player.getGameProfile();
            pGameProfile.getProperties().removeAll("textures");
            switch (playerName) {
                case "steve":
                    gameProfile2.getProperties().put("textures", STEVE_SKIN);
                    pGameProfile.getProperties().put("textures", STEVE_SKIN);
                    break;
                case "alex":
                    gameProfile2.getProperties().put("textures", ALEX_SKIN);
                    pGameProfile.getProperties().put("textures", ALEX_SKIN);
                    break;
                case "herobrine":
                    gameProfile2.getProperties().put("textures", HEROBRINE_SKIN);
                    pGameProfile.getProperties().put("textures", HEROBRINE_SKIN);
                    break;

            }
        }

        clonePlayerFirstLoc = new FakePlayer(player.getServerWorld(), gameProfile1);
        clonePlayerSecondLoc = new FakePlayer(player.getServerWorld(), gameProfile2);

        clonePlayerFirstLoc.setPosition(player.getPos());
        clonePlayerFirstLoc.setPitch(player.getPitch());
        clonePlayerFirstLoc.setYaw(player.getYaw());
        clonePlayerFirstLoc.setHeadYaw(player.getHeadYaw());

        clonePlayerSecondLoc.setPosition(secondPos[0], secondPos[1], secondPos[2]);
        clonePlayerSecondLoc.setPitch(player.getPitch());
        clonePlayerSecondLoc.setYaw(player.getYaw());
        clonePlayerSecondLoc.setHeadYaw(player.getHeadYaw());

        Scoreboard scoreboard = player.getScoreboard();

        player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, clonePlayerFirstLoc));
        player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, clonePlayerSecondLoc));
        player.getServerWorld().spawnEntity(clonePlayerFirstLoc);
        player.getServerWorld().spawnEntity(clonePlayerSecondLoc);

        Team team = scoreboard.getTeam("notag");
        if (team == null) {
            team = scoreboard.addTeam("notag");
            team.setNameTagVisibilityRule(AbstractTeam.VisibilityRule.NEVER);
        }
        team.getPlayerList().add(player.getDisplayName().getString());
        team.getPlayerList().add(clonePlayerFirstLoc.getDisplayName().getString());
        team.getPlayerList().add(clonePlayerSecondLoc.getDisplayName().getString());

        Entity camera = initCamera();
        player.setCameraEntity(camera);
        player.changeGameMode(GameMode.SPECTATOR);
        //player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(camera.getId()));
        tick = 0;
        t = 0;
        isAtFirstPoint = true;
        isInOutBody = true;
        animating = true;
        currentPointTick = FIRST_POINT_EACH_TICK;
        wentUpTimes = 0;
        ServerTickEvents.START_SERVER_TICK.register(this.tickEvents());
        ColorScreen colorScreen = new ColorScreen(ColorLibrary.GREEN);
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(colorScreen));

    }

    public void stop() {
        player.setPosition(clonePlayerSecondLoc.getX(), clonePlayerSecondLoc.getY(), clonePlayerSecondLoc.getZ());
        player.setPitch(clonePlayerSecondLoc.getPitch());
        player.setYaw(clonePlayerSecondLoc.getYaw());
        player.setHeadYaw(clonePlayerSecondLoc.getHeadYaw());

        camera.setPosition(clonePlayerSecondLoc.getX(), clonePlayerSecondLoc.getY(), clonePlayerSecondLoc.getZ());
        camera.setPitch(clonePlayerSecondLoc.getPitch());
        camera.setYaw(clonePlayerSecondLoc.getYaw());
        camera.setHeadYaw(clonePlayerSecondLoc.getHeadYaw());

        player.setCameraEntity(player);

        camera.remove(Entity.RemovalReason.KILLED);
        player.networkHandler.sendPacket(new PlayerRemoveS2CPacket(List.of(clonePlayerFirstLoc.getUuid(), clonePlayerSecondLoc.getUuid())));
        player.changeGameMode(GameMode.SURVIVAL);

        clonePlayerFirstLoc.remove(Entity.RemovalReason.KILLED);
        clonePlayerSecondLoc.remove(Entity.RemovalReason.KILLED);

        animating = false;

        MinecraftClient minecraft = MinecraftClient.getInstance();
        minecraft.execute(() -> minecraft.setScreen(null));
    }


    public void stop(boolean force)
    {
        stop();
        player.setPosition(firstPos[0], firstPos[1], firstPos[2]);
    }

    private void sendWhiteFlash()
    {
        ServerPlayNetworkHandler connection = player.networkHandler;
        connection.sendPacket(new TitleS2CPacket(CustomUnicode.WHITE_SCREEN));
        connection.sendPacket(new TitleFadeS2CPacket(0, 5, 5));
    }

    private ServerTickEvents.StartTick tickEvents()
    {
        return minecraftServer -> {
            if(!animating) return;

            if(!isMovingBetween && !isInOutBody) {
                if(DEBUG) player.sendMessage(Text.literal("Tick: " + tick));
                if(tick % currentPointTick == 0 && wentUpTimes < 3) {
                    if(DEBUG) player.sendMessage(Text.literal("§aCHECKED!"));
                    if(isAtFirstPoint) {
                        player.playSoundToPlayer(ModSounds.BOOM, SoundCategory.MASTER, 1, 1);
                    } else {
                        player.playSoundToPlayer(ModSounds.TAM, SoundCategory.MASTER, 1, 1);
                    }
                    sendWhiteFlash();
                    double newY = isAtFirstPoint ? camera.getY() + CORD_ADD_EACH_TICk : camera.getY() - CORD_ADD_EACH_TICk;
                    camera.setPos(camera.getX(), newY, camera.getZ());
                    RandomColorScreen.changeScreen();
                    wentUpTimes++;
                }
                if(tick == (currentPointTick * 2 + (isAtFirstPoint ? 15 : 30))) {
                    if(isAtFirstPoint) {
                        isMovingBetween = true;
                        currentPointTick = SECOND_POINT_EACH_TICK;
                        player.playSoundToPlayer(ModSounds.WHOOSH, SoundCategory.MASTER, 1, 1);
                    };
                    if(isAtSecondPoint) {
                        player.playSoundToPlayer(ModSounds.WHOOSH, SoundCategory.MASTER, 1, 1);
                        isInOutBody = true;
                    };
                    tick = 0;
                    wentUpTimes = 0;
                }
                tick++;
            }

            if(isMovingBetween && t <= 1){
                double[] newCord = moveBetweenPoint(firstPos, secondPos, t);
                if(DEBUG) player.sendMessage(Text.literal(newCord[0] + " " + newCord[1] + " " + newCord[2]));
                camera.setPos(newCord[0], newCord[1] +  FIRST_CORD_ADD + CORD_ADD_EACH_TICk * 3, newCord[2]);
                t += moveEveryCord;
                if(t >= 1) {
                    t = 1;
                    tick++;
                    if(tick == 10) {
                        tick = 0;
                        t = 0;
                        isAtFirstPoint = false;
                        isAtSecondPoint = true;
                        isMovingBetween = false;
                        player.networkHandler.sendPacket(new StopSoundS2CPacket(null, SoundCategory.MASTER));
                    }
                };
            }


            if(isInOutBody && t <= 1) {
                double[] firstBodyCord = new double[]{isAtFirstPoint ? firstPos[1] : camera.getY(), isAtFirstPoint ? player.getPitch() : camera.getPitch()};
                double[] endBodyCord = new double[]{isAtFirstPoint ? firstPos[1] + FIRST_CORD_ADD : clonePlayerSecondLoc.getY(), isAtFirstPoint ? 90 : clonePlayerSecondLoc.getPitch()};
                if(DEBUG) player.sendMessage(Text.literal("Head yaw first: " + firstBodyCord[1] + "Head yaw end: " + endBodyCord[1]));
                double[] newCord = inOrOutBodyCamera(firstBodyCord, endBodyCord, t);
                camera.setPos(camera.getX(), newCord[0], camera.getZ());
                camera.setPitch((float) newCord[1]);
                t += isAtFirstPoint ? moveEveryCord * 2 : moveEveryCord * 1.1;
                if(t >= 1 || (isAtSecondPoint && camera.getPitch() == clonePlayerSecondLoc.getPitch())) {
                    tick = 0;
                    t = 0;
                    if(isAtSecondPoint) {
                        stop();
                    }
                    isInOutBody = false;
                }
            }

        };
    }

    private double[] inOrOutBodyCamera(double[] start, double[] end, double t)
    {
        double easeOutT = easeOutCubic(t);

        double y = lerp(start[0], end[0], easeOutT);
        double headYaw = lerp(start[1], end[1], easeOutT);

        return new double[]{y, headYaw};
    }

    private double[] moveBetweenPoint(double[] start, double[] end, double t)
    {
        double easeOutT = easeOutCubic(t);

        double x = lerp(start[0], end[0], easeOutT);
        double y = lerp(start[1], end[1], easeOutT);
        double z = lerp(start[2], end[2], easeOutT);

        return new double[]{x, y, z};
    }

    private double easeOutCubic(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    private double lerp(double a, double b, double t)
    {
        return a + (b - a) * t;
    }


}
