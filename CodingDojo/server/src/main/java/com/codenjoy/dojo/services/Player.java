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


import com.codenjoy.dojo.client.Closeable;
import com.codenjoy.dojo.services.nullobj.NullPlayer;
import com.codenjoy.dojo.services.nullobj.NullPlayerGame;
import com.codenjoy.dojo.services.playerdata.QuerySubscription;
import com.codenjoy.dojo.transport.screen.ScreenRecipient;
import com.dojo.notifications.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.services.GameServiceImpl.removeNumbers;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player implements ScreenRecipient, Closeable {

    public static final Player ANONYMOUS = new Player("anonymous");

    private String id;
    private String email;
    private String fullName;
    private String readableName;
    private String code;
    private String data;
    private String callbackUrl;
    private String game;
    private String room;
    private String password;
    private String passwordConfirmation;
    private PlayerScores scores;
    private Object score;
    private Information info;
    private GameType gameType;
    private InformationCollector eventListener;
    private Closeable ai;
    private String gitHubUsername;
    private String slackEmail;
    private String repositoryUrl;
    private Map<String, List<QuerySubscription>> subscriptionsForGame;

    public Player(String id) {
        this.id = id;
    }

    public Player(String id, String callbackUrl, GameType gameType, PlayerScores scores, Information info) {
        this.id = id;
        this.callbackUrl = callbackUrl;
        this.gameType = gameType;
        this.scores = scores;
        this.info = info;
        subscriptionsForGame = new HashMap<>();
    }

    public Player(String id, String readableName, String callbackUrl, String room, String game, int score, String repositoryUrl) {
        this.id = id;
        this.readableName = readableName;
        this.callbackUrl = callbackUrl;
        this.room = room;
        this.game = game;
        this.score = score;
        this.repositoryUrl = repositoryUrl;
    }

    public List<QuerySubscription> getSubscriptionsForGame(String game) {
        subscriptionsForGame.computeIfAbsent(game, k -> new ArrayList<>());
        return subscriptionsForGame.get(game);
    }

    public List<Integer> getSubscriptionIdsForGame(String game) {
        subscriptionsForGame.computeIfAbsent(game, k -> new ArrayList<>());
        return subscriptionsForGame.get(game).stream()
                .map(qs -> qs.getQuery().getId())
                .collect(Collectors.toList());
    }

    public void removeSubscriptionForGame(String game, int queryId) {
        QuerySubscription qs = subscriptionsForGame.get(game).stream()
                .filter(q -> queryId == q.getQuery().getId()).findFirst().get();
        subscriptionsForGame.get(game).remove(qs);
    }

    public void putSubscriptionForGame(String game, QuerySubscription subscription) {
        if (!subscriptionsForGame.containsKey(game)) {
            subscriptionsForGame.put(game, new ArrayList<>());
        }
        subscriptionsForGame.get(game).add(subscription);
    }

    public void updateSubscription(String game, Query query, boolean email, boolean slack) {
        removeSubscriptionForGame(game, query.getId());
        putSubscriptionForGame(game, new QuerySubscription(query, email, slack));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == NullPlayer.INSTANCE && (o != NullPlayer.INSTANCE && o != NullPlayerGame.INSTANCE)) return false;

        if (o instanceof Player) {
            Player p = (Player) o;

            if (p.id == null) {
                return id == null;
            }

            return (p.id.equals(id));
        }

        if (o instanceof PlayerGame) {
            PlayerGame pg = (PlayerGame) o;

            return pg.getPlayer().equals(this);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (id + code).hashCode();
    }

    public String getNotNullReadableName() {
        return StringUtils.isEmpty(readableName) ? id : readableName;
    }

    public int clearScore() {
        return scores.clear();
    }

    public Object getScore() {
        return (scores != null) ? scores.getScore() : score;
    }

    public void setScore(Object score) {
        this.score = score;
        if (scores != null) {
            scores.update(score);
        }
    }

    public String getMessage() {
        return info.getMessage();
    }

    public String getGame() {
        return (gameType != null) ? gameType.name() : game;
    }

    // TODO test me
    public String getGameOnly() {
        return removeNumbers(getGame());
    }

    @Override
    public void close() {
        if (ai != null) {
            ai.close();
        }
    }

    public boolean hasAi() {
        return ai != null;
    }

    @Override
    public String toString() {
        return id;
    }

    public void dropPassword() {
        this.password = null;
        this.passwordConfirmation = null;
    }

}
