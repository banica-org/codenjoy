package com.codenjoy.dojo.config.oauth2;

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

import java.util.Map;

public class UserData {

    private String id;
    private String email;
    private String fullName;
    private String readableName;
    private String gitHubUsername;
    private String slackId;

    public UserData(Map<String, ?> map) {
        id = (String) map.get("player_id");
        email = (String) map.get("email");
        fullName = (String) map.get("fullName");
        readableName = (String) map.get("name");
        gitHubUsername = (String) map.get("github_username");
        slackId = (String) map.get("slackEmail");
    }

    public String id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String fullName() {
        return fullName;
    }

    public String readableName() {
        return readableName;
    }

    public String gitHubUsername() {
        return gitHubUsername;
    }

    public String slackId() {
        return slackId;
    }
}
