package com.codenjoy.dojo.web.rest;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2020 Codenjoy
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

import com.codenjoy.dojo.client.CodenjoyContext;
import com.codenjoy.dojo.services.ConfigProperties;
import com.codenjoy.dojo.services.GameService;
import com.codenjoy.dojo.services.GameType;
import com.codenjoy.dojo.services.GuiPlotColorDecoder;
import com.codenjoy.dojo.services.PlayerService;
import com.codenjoy.dojo.services.dao.Registration;
import com.codenjoy.dojo.services.grpc.handler.UpdateHandler;
import com.codenjoy.dojo.web.rest.pojo.PGameTypeInfo;
import com.codenjoy.dojo.web.rest.pojo.PSprites;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/game")
@AllArgsConstructor
public class RestGameController {

    private UpdateHandler updateHandler;

    private GameService gameService;

    private PlayerService playerService;

    private ConfigProperties properties;

    @GetMapping("/{name}/exists")
    public Boolean exists(@PathVariable("name") String name) {
        return gameService.getGames().contains(name);
    }

    // TODO узнать кто использует и предупредить, что добавилось room
    @GetMapping("/{name}/{room}/info")
    public PGameTypeInfo type(@PathVariable("name") String name,
                              @PathVariable("room") String room)
    {
        if (!exists(name)) {
            return null; // TODO а если room несуществует, может тоже возвращать null
        }

        GameType game = gameService.getGameType(name, room);

        PSprites sprites = new PSprites(spritesAlphabet(), spritesUrl(name),
                spritesNames(name), spritesValues(name));

        return new PGameTypeInfo(game, room, help(name), client(name), ws(), sprites);
    }

    @GetMapping("/{name}/help/url")
    public String help(@PathVariable("name") String name) {
        if (!exists(name)) {
            return null;
        }

        // TODO тут было бы неплохо получить так же http[s]://domain:port/ 
        return String.format("/%s/resources/help/%s.html",
                CodenjoyContext.getContext(), name);
    }

    @GetMapping("/{name}/client/url")
    public String client(@PathVariable("name") String name) {
        if (!exists(name)) {
            return null;
        }

        // TODO тут было бы неплохо получить так же http[s]://domain:port/
        return String.format("/%s/resources/user/%s-servers.zip",
                CodenjoyContext.getContext(), name);
    }

    @GetMapping("/ws/url")
    public String ws() {
        // TODO тут было бы неплохо получить так же SERVER:PORT 
        // TODO а еще надо подумать если юзер авторизирован, то можно выдать его PLAYER_ID & CODE 
        return String.format("ws[s]://SERVER:PORT/%s/ws?user=PLAYER_ID&code=CODE",
                CodenjoyContext.getContext());
    }

    @GetMapping("/sprites")
    public Map<String, List<String>> allSprites() {
        return gameService.getSprites();
    }

    @GetMapping("/{name}/sprites/exists")
    public Boolean isGraphic(@PathVariable("name") String name) {
        if (!exists(name)) {
            return null;
        }

        return !spritesNames(name).isEmpty();
    }

    @GetMapping("/{name}/sprites/names")
    public List<String> spritesNames(@PathVariable("name") String name) {
        if (!exists(name)) {
            return null;
        }

        return gameService.getSpritesNames().get(name);
    }

    @GetMapping("/{name}/sprites/values")
    public List<String> spritesValues(@PathVariable("name") String name) {
        if (!exists(name)) {
            return null;
        }

        return gameService.getSpritesValues().get(name);
    }

    @GetMapping("/{name}/sprites/url")
    public String spritesUrl(@PathVariable("name") String name) {
        if (!exists(name) || !isGraphic(name)) {
            return null;
        }

        // TODO тут было бы неплохо получить так же http[s]://domain:port/
        return String.format("/%s/resources/sprite/%s/%s.png",
                CodenjoyContext.getContext(), name, "*");
    }

    @GetMapping("/sprites/alphabet")
    public String spritesAlphabet() {
        return String.valueOf(GuiPlotColorDecoder.GUI.toCharArray());
    }

    @DeleteMapping("/scores")
    public void cleanScores(@AuthenticationPrincipal Registration.User user) {
        if (!properties.isPlayerScoreCleanupEnabled()) {
            return;
        }
        playerService.cleanScores(user.getId());
    }

}

