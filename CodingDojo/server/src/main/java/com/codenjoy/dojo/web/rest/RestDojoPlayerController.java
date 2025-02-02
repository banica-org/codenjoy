package com.codenjoy.dojo.web.rest;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
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

import com.codenjoy.dojo.services.DojoPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class RestDojoPlayerController {

    @Autowired
    private DojoPlayerService dojoPlayerService;

    @PostMapping("/change/{username}/to/{newUsername}")
    public int updateGitHubUsername(@PathVariable("username") String username,
                                    @PathVariable("newUsername") String newUsername) {

        return dojoPlayerService.updateGitHubUsername(username, newUsername);
    }

    @PostMapping("/update/{username}/{game}/score")
    public void updateUserScore(@PathVariable("username") String username,
                                @PathVariable("game") String game,
                                @RequestBody long score) {
        if(score != -1) {
            dojoPlayerService.updateUserScore(username, game, score);
        }
    }
}
