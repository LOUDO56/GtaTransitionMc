package fr.loudo.cameratester.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class FakePlayer extends ServerPlayerEntity {

    private static final SyncedClientOptions CLIENT_OPTIONS = SyncedClientOptions.createDefault();

    public FakePlayer(ServerWorld world, GameProfile profile) {
        super(world.getServer(), world, profile, CLIENT_OPTIONS);
        this.networkHandler = new ServerPlayNetworkHandler(
                world.getServer(),
                new ClientConnection(NetworkSide.CLIENTBOUND),
                this,
                new ConnectedClientData(profile, 0, CLIENT_OPTIONS, true)
        );
    }
}
