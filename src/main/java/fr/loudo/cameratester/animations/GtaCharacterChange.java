package fr.loudo.cameratester.animations;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.loudo.cameratester.utils.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.List;
import java.util.UUID;

public class GtaCharacterChange {

    private int tick;
    private double[] firstPos;
    private double[] secondPos;
    private Entity camera;
    private ServerPlayerEntity player;
    private boolean isAtFirstPoint;
    private boolean isAtSecondPoint;
    private boolean isMovingBetween;
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
        ArmorStandEntity armorStand = new ArmorStandEntity(player.getWorld(), player.getX(), player.getY() + FIRST_CORD_ADD, player.getZ());
        armorStand.setInvisible(true);
        armorStand.setNoGravity(true);
        armorStand.setInvulnerable(true);
        armorStand.setPitch(90);
        armorStand.setYaw(-90);
        armorStand.setHeadYaw(-90);
        player.getServerWorld().spawnEntity(armorStand);
        this.camera = armorStand;
        return armorStand;
    }

    public void start()
    {
        GameProfile gameProfile1 = new GameProfile(UUID.randomUUID(), player.getDisplayName().getString());
        GameProfile gameProfile2 = new GameProfile(UUID.randomUUID(), player.getDisplayName().getString());


        if(player.getGameProfile().getProperties().containsKey("textures")) {
            Property textures = player.getGameProfile().getProperties().get("textures").iterator().next();
            gameProfile1.getProperties().put("textures", new Property("textures", textures.value(), textures.signature()));
            gameProfile2.getProperties().put("textures", new Property("textures", textures.value(), textures.signature()));
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
        animating = true;
        ServerTickEvents.START_SERVER_TICK.register(this.tickEvents());
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
    }


    public void stop(boolean force)
    {
        stop();
        player.setPosition(firstPos[0], firstPos[1], firstPos[2]);
    }

    private ServerTickEvents.StartTick tickEvents()
    {
        return minecraftServer -> {
            if(!animating) return;

            if(!isMovingBetween) {
                if(DEBUG) player.sendMessage(Text.literal("Tick: " + tick));
                switch (tick) {
                    case 0:
                    case 20:
                    case 40:
                        if(DEBUG) player.sendMessage(Text.literal("§aCHECKED!"));
                        double newY = isAtFirstPoint ? camera.getY() + CORD_ADD_EACH_TICk : camera.getY() - CORD_ADD_EACH_TICk;
                        camera.setPos(camera.getX(), newY, camera.getZ());
                        break;
                }
                if(tick == 60) {
                    if(isAtFirstPoint) isMovingBetween = true;
                    if(isAtSecondPoint) {
                        stop();
                    };
                    tick = 0;
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
                        isAtFirstPoint = false;
                        isAtSecondPoint = true;
                        isMovingBetween = false;
                    }
                };
            }

        };
    }

    private double[] moveBetweenPoint(double[] start, double[] end, double t)
    {
        double easeOutT = easeOut(t);

        double x = lerp(start[0], end[0], easeOutT);
        double y = lerp(start[1], end[1], easeOutT);
        double z = lerp(start[2], end[2], easeOutT);

        return new double[]{x, y, z};
    }

    private double easeOut(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    private double lerp(double a, double b, double t)
    {
        return a + (b - a) * t;
    }


}
