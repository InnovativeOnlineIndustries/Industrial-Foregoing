package com.buuz135.industrial.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

public class IFFakePlayer extends FakePlayer {

    private static final UUID uuid = UUID.fromString("ec5b5875-ebb5-4b47-833b-0de37ac9e6d7");

    private static GameProfile PROFILE = new GameProfile(uuid, "[IFFarmer]");

    public IFFakePlayer(WorldServer worldIn) {
        super(worldIn, PROFILE);
    }
}
