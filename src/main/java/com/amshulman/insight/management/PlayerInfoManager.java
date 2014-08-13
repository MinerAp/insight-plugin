package com.amshulman.insight.management;

import java.util.HashMap;
import java.util.Map;

import com.amshulman.insight.util.PlayerInfo;

public class PlayerInfoManager {

    private Map<String, PlayerInfo> playerInfo = new HashMap<>();

    public PlayerInfo getPlayerInfo(String playerName) {
        PlayerInfo info = playerInfo.get(playerName);

        if (info == null) {
            info = new PlayerInfo();
            playerInfo.put(playerName, info);
        }

        return info;
    }
}
