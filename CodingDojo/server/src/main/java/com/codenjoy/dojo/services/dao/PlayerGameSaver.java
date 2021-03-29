package com.codenjoy.dojo.services.dao;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.services.GameSaver;
import com.codenjoy.dojo.services.Player;
import com.codenjoy.dojo.services.PlayerSave;
import com.codenjoy.dojo.services.jdbc.*;

import java.sql.Date;
import java.util.*;

public class PlayerGameSaver implements GameSaver {

    private CrudConnectionThreadPool pool;

    public PlayerGameSaver(ConnectionThreadPoolFactory factory) {
        pool = factory.create(
                "CREATE TABLE IF NOT EXISTS saves (" +
                        "time varchar(255), " +
                        "player_id varchar(255), " +
                        "repository_url varchar(255)," +
                        "room_name varchar(255)," +
                        "game_name varchar(255)," +
                        "score varchar(255)," +
                        "save varchar(255));");
    }

    void removeDatabase() {
        pool.removeDatabase();
    }

    //TODO to make it create on first call after that update it

    @Override
    public void saveGame(Player player, String save, long time) {
        pool.update("INSERT INTO saves " +
                        "(time, player_id, repository_url, room_name, game_name, score, save) " +
                        "VALUES (?,?,?,?,?,?,?);",
                new Object[]{JDBCTimeUtils.toString(new Date(time)),
                        player.getId(),
                        player.getRepositoryUrl(),
                        player.getRoom(),
                        player.getGame(),
                        player.getScore(),
                        save
                });
    }

//    @Override
//    public void saveGame(Player player, String save, long time) {
//        pool.update("UPDATE saves " +
//                        "SET time = ?, player_id = ?, repository_url = ?, room_name = ?, game_name = ?, score = ?, save = ?" +
//                        "WHERE player_id = 'icnlno2o2kbluwwxrkgw'",
//                new Object[]{JDBCTimeUtils.toString(new Date(time)),
//                        player.getId(),
//                        player.getRepositoryUrl(),
//                        player.getRoom(),
//                        player.getGame(),
//                        player.getScore(),
//                        save
//                });
//    }

    @Override
    public PlayerSave loadGame(String id) {
        return pool.select("SELECT * FROM saves WHERE player_id = ? ORDER BY time DESC LIMIT 1;",
                new Object[]{id},
                rs -> {
                    if (rs.next()) {
                        String repositoryUrl = rs.getString("repository_url");
                        String score = rs.getString("score");
                        String room = rs.getString("room_name");
                        String game = rs.getString("game_name");
                        String save = rs.getString("save");
                        return new PlayerSave(id, repositoryUrl, game, room, score, save);
                    } else {
                        return PlayerSave.NULL;
                    }
                }
        );
    }

    @Override
    public List<String> getSavedList() {
        return pool.select("SELECT DISTINCT player_id FROM saves;", // TODO убедиться, что загружены самые последние
                rs -> {
                    List<String> result = new LinkedList<>();
                    while (rs.next()) {
                        String id = rs.getString("player_id");
                        result.add(id);
                    }
                    return result;
                }
        );
    }

    @Override
    public void delete(String id) {
        pool.update("DELETE FROM saves WHERE player_id = ?;",
                new Object[]{id});
    }
}
