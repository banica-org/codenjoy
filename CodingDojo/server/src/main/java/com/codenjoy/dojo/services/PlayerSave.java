package com.codenjoy.dojo.services;

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


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class PlayerSave {

    public static final PlayerSave NULL = new PlayerSave(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, 0, StringUtils.EMPTY, StringUtils.EMPTY);

    private String id;
    private String callbackUrl;
    private String room;
    private String game;
    private Object score;
    private String save;
    private String repositoryUrl;

    public PlayerSave(String save) {
        this.save = save;
    }

    public PlayerSave(String id, String callbackUrl, String game, String room, Object score, String save, String repositoryUrl) {
        this.id = id;
        this.game = game;
        this.room = room;
        this.callbackUrl = callbackUrl;
        this.score = score;
        this.save = save;
        this.repositoryUrl = repositoryUrl;
    }

    public PlayerSave(Player save) {
        this.id = save.getId();
        this.game = save.getGame();
        this.room = save.getRoom();
        this.callbackUrl = save.getCallbackUrl();
        this.score = save.getScore();
        this.save = save.getData();
        this.repositoryUrl = save.getRepositoryUrl();
    }

    public static boolean isSaveNull(String str) {
        return StringUtils.isEmpty(str) || str.equalsIgnoreCase("null");
    }

}
