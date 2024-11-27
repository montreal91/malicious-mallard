package org.example;

import java.util.List;

public record InitialState(
        List<Team> teamList,
        List<Player> playerList,
        List<BaseUnit> unitList
) {
    public InitialState {
        // Null safety
        if (teamList == null || playerList == null || unitList == null) {
            throw new NullPointerException("Fields in InitialState cannot be null");
        }

        // Defensive copy
        teamList = List.copyOf(teamList);
        playerList = List.copyOf(playerList);
        unitList = List.copyOf(unitList);
    }
}
