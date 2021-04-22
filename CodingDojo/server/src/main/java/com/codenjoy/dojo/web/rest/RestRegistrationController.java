package com.codenjoy.dojo.web.rest;

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


import com.codenjoy.dojo.services.GameServerService;
import com.codenjoy.dojo.services.PlayerGames;
import com.codenjoy.dojo.services.PlayerSave;
import com.codenjoy.dojo.services.PlayerService;
import com.codenjoy.dojo.services.SaveService;
import com.codenjoy.dojo.services.dao.Registration;
import com.codenjoy.dojo.web.controller.Validator;
import com.codenjoy.dojo.web.rest.pojo.PlayerDetailInfo;
import com.codenjoy.dojo.web.rest.pojo.PlayerInfo;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.codenjoy.dojo.web.controller.Validator.CANT_BE_NULL;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/rest")
@AllArgsConstructor
public class RestRegistrationController {

    private Registration registration;
    private PlayerService playerService;
    private PlayerGames playerGames;
    private SaveService saveService;
    private Validator validator;
    private GameServerService gameServerService;

    @GetMapping("/player/{player}/check/{code}")
    public boolean checkUserLogin(@PathVariable("player") String id,
                                  @PathVariable("code") String code) {
        validator.checkPlayerId(id, CANT_BE_NULL);
        validator.checkCode(code, CANT_BE_NULL);

        return registration.checkUser(id, code) != null;
    }

    // TODO test me
    @GetMapping("/game/{game}/players")
    public List<PlayerInfo> getGamePlayers(@PathVariable("game") String game) {
        validator.checkGame(game, CANT_BE_NULL);

        return playerService.getAll(game).stream()
                .map(PlayerInfo::new)
                .collect(toList());
    }

    // TODO test me
    @PostMapping("/player/create")
    public synchronized String createPlayer(@RequestBody PlayerDetailInfo player) {
        Registration.User user = player.getRegistration().build();
        registration.replace(user);

        boolean fromSave = player.getScore() == null;
        if (fromSave) {
            // делаем попытку грузить по сейву
            if (!saveService.load(player.getId())) {
                // неудача - обнуляем все
                player.setSave("{}");
                player.setScore("0");
                fromSave = false;
            }
        }

        if (!fromSave) {
            // грузим как положено
            PlayerSave save = player.buildPlayerSave();
            playerService.register(save);

            playerGames.setLevel(player.getId(),
                    new JSONObject(player.getSave()));
        }

        return user.getCode();
    }

    // TODO test me
    @GetMapping("/player/{player}/exists")
    public boolean isPlayerExists(@PathVariable("player") String id) {
        validator.checkPlayerId(id, CANT_BE_NULL);

        return registration.checkUser(id) != null
                && playerService.contains(id);
    }

    @PostMapping("/change/{username}/to/{newUsername}")
    public int updateGitHubUsername(@PathVariable("username") String username,
                                    @PathVariable("newUsername") String newUsername) {

        String id = registration.getIdByGitHubUsername(username);
        if (id == null) {
            return 0;
        }
        String repository = gameServerService.createOrGetRepository(newUsername);

        playerService.updateUserRepository(id, repository);

        return registration.updateGitHubUsername(username, newUsername);
    }


}
