package com.codenjoy.dojo.web.controller;

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


import com.codenjoy.dojo.services.ConfigProperties;
import com.codenjoy.dojo.services.GameBoardData;
import com.codenjoy.dojo.services.GameServiceImpl;
import com.codenjoy.dojo.services.GameType;
import com.codenjoy.dojo.services.BoardService;
import com.codenjoy.dojo.services.Player;
import com.codenjoy.dojo.services.PlayerService;
import com.codenjoy.dojo.services.dao.BoardData;
import com.codenjoy.dojo.services.dao.FeedbackSaver;
import com.codenjoy.dojo.services.dao.PlayerGameSaver;
import com.codenjoy.dojo.services.dao.Registration;
import com.codenjoy.dojo.services.dao.SubscriptionSaver;
import com.codenjoy.dojo.services.grpc.QueryClient;
import com.codenjoy.dojo.services.multiplayer.MultiplayerType;
import com.codenjoy.dojo.services.nullobj.NullGameType;
import com.codenjoy.dojo.services.nullobj.NullPlayer;
import com.codenjoy.dojo.services.playerdata.QuerySubscription;
import com.codenjoy.dojo.services.security.RegistrationService;
import com.dojo.notifications.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static com.codenjoy.dojo.web.controller.Validator.CANT_BE_NULL;
import static com.codenjoy.dojo.web.controller.Validator.CAN_BE_NULL;

@Controller
@RequestMapping(BoardController.URI)
@RequiredArgsConstructor
public class BoardController {

    public static final String URI = "/board";

    public static final String CODE = "code";
    public static final String GAME = "game";
    public static final String ROOM = "room";
    public static final String ALL_PLAYERS_SCREEN = "allPlayersScreen";
    public static final String GAME_ONLY = "gameOnly";
    public static final String PLAYER = "player";
    public static final String PLAYER_ID = "playerId";
    public static final String READABLE_NAME = "readableName";
    public static final String GITHUB = "github";
    public static final String WELCOME_TEXT = "welcomeText";
    public static final String PLAYER_SCORE_CLEANUP_ENABLED = "playerScoreCleanupEnabled";
    public static final String SUBSCRIBED = "subscribed";
    public static final String IS_SLACK_SUBSCRIBED = "isSlackSubscribed";
    public static final String REPOSITORY_URL = "repositoryURL";
    public static final String FEEDBACK = "feedback";
    public static final String LEADERBOARD = "leaderboard";

    private final PlayerService playerService;
    private final Registration registration;
    private final Validator validator;
    private final ConfigProperties properties;
    private final RegistrationService registrationService;
    private final PlayerGameSaver playerGameSaver;
    private final FeedbackSaver feedbackSaver;
    private final SubscriptionSaver subscriptionSaver;
    private final QueryClient queryClient;
    private final BoardService leaderboardService;
    private final BoardData boardData;

    @GetMapping("/player/{player}")
    public String boardPlayer(ModelMap model,
                              @PathVariable(PLAYER) String id,
                              @RequestParam(name = "only", required = false) Boolean justBoard) {
        validator.checkPlayerId(id, CANT_BE_NULL);

        return boardPlayer(model, id, null, justBoard, (String) model.get(GAME));
    }

    @GetMapping(value = "/player/{player}", params = {"code", "remove"})
    public String removePlayer(@PathVariable(PLAYER) String id, @RequestParam(CODE) String code) {
        validator.checkPlayerCode(id, code);

        Player player = playerService.get(id);
        if (player == NullPlayer.INSTANCE) {
            return "redirect:/register?id=" + id;
        }

        playerService.remove(player.getId());
        return "redirect:/";
    }

    @GetMapping(value = "/player/{player}", params = CODE)
    public String boardPlayer(ModelMap model,
                              @PathVariable(PLAYER) String id,
                              @RequestParam(CODE) String code,
                              @RequestParam(name = "only", required = false) Boolean justBoard,
                              @RequestParam(name = GAME, required = false, defaultValue = "") String game) {
        validator.checkPlayerId(id, CANT_BE_NULL);
        validator.checkCode(code, CAN_BE_NULL);
        validator.checkGame(game, CAN_BE_NULL); // TODO а зачем тут вообще game?

        Player player = playerService.get(id);
        if (player == NullPlayer.INSTANCE) {
            return "redirect:/register?id=" + id;
        }
        GameBoardData gameBoardData = boardData.getInfoForGame(game);
        setUpQueries(player, game);

        populateBoardAttributes(model, code, gameBoardData, player, false);

        justBoard = justBoard != null && justBoard;
        model.addAttribute("justBoard", justBoard);
        return justBoard ? "board-only" : "board";
    }

    @GetMapping("/rejoining/{game}")
    public String rejoinGame(ModelMap model, @PathVariable(GAME) String game,
                             HttpServletRequest request,
                             @AuthenticationPrincipal Registration.User user) {
        validator.checkGame(game, CANT_BE_NULL);

        if (user == null) {
            return "redirect:/login?" + "game" + "=" + game;
        }

        // TODO ROOM а надо ли тут этот метод вообще, ниже есть более универсальный?
        // TODO ROOM так как есть rest методы то может вообще убрать отсюда этих двоих?
        String room = game;

        registrationService.register(user.getId(), user.getCode(), game, room, request.getRemoteAddr(), user.getGitHubUsername(), user.getSlackEmail());

        return rejoinGame(model, game, room, request, user);
    }

    @GetMapping("/rejoining/{game}/room/{room}")
    public String rejoinGame(ModelMap model, @PathVariable(GAME) String game,
                             @PathVariable(ROOM) String room,
                             HttpServletRequest request,
                             @AuthenticationPrincipal Registration.User user) {
        validator.checkGame(game, CANT_BE_NULL);
        validator.checkRoom(room, CANT_BE_NULL);

        Player player = playerService.get(user.getCode());
        if (player == NullPlayer.INSTANCE) {
            return registrationService.connectRegisteredPlayer(user.getCode(), request, user.getId(), room, game, user.getGitHubUsername(), player.getSlackEmail());
        }
        GameBoardData gameBoardData = boardData.getInfoForGame(game);
        populateBoardAttributes(model, player.getCode(), gameBoardData, player, false);
        return "board";
    }

    private void populateBoardAttributes(ModelMap model, String code, GameBoardData gameBoardData, Player player, boolean allPlayersScreen) {
        populateBoardAttributes(model, code, gameBoardData, player.getRoom(), player.getId(),
                player.getReadableName(), player.getGitHubUsername(), player.getSubscriptionsForGame(player.getGame()), allPlayersScreen);
    }

    private void populateBoardAttributes(ModelMap model, String code, GameBoardData gameBoardData, String room,
                                         String playerId, String readableName, String github,
                                         List<QuerySubscription> subscriptionForGame, boolean allPlayersScreen) {
        String repoURL = playerGameSaver.getRepositoryByPlayerIdForGame(playerId, gameBoardData.getGameName());
        String game = gameBoardData.getGameName();
        model.addAttribute(CODE, code);
        model.addAttribute(GAME, game);
        model.addAttribute(ROOM, room);
        model.addAttribute(GAME_ONLY, game);
        model.addAttribute(PLAYER_ID, playerId);
        model.addAttribute(READABLE_NAME, readableName);
        model.addAttribute(GITHUB, github);
        model.addAttribute(WELCOME_TEXT, formatText(gameBoardData.getWelcomeText(), readableName, game, repoURL));
        model.addAttribute(ALL_PLAYERS_SCREEN, allPlayersScreen); // TODO так клиенту припрутся все доски и даже не из его игры, надо фиксить dojo transport
        model.addAttribute(PLAYER_SCORE_CLEANUP_ENABLED, properties.isPlayerScoreCleanupEnabled());
        model.addAttribute(SUBSCRIBED, subscriptionForGame);
        model.addAttribute(IS_SLACK_SUBSCRIBED, !registration.getSlackEmailById(playerId).equals(""));
        model.addAttribute(REPOSITORY_URL, repoURL);
        model.addAttribute(LEADERBOARD, leaderboardService.getPlayersForGame(gameBoardData.getGameName()));
    }

    @GetMapping(value = "/log/player/{player}", params = {"game", "room"})
    public String boardPlayerLog(ModelMap model, @PathVariable("player") String id,
                                 @RequestParam(GAME) String game,
                                 @RequestParam(ROOM) String room) {
        validator.checkPlayerId(id, CANT_BE_NULL);
        validator.checkGame(game, CANT_BE_NULL);
        validator.checkRoom(room, CANT_BE_NULL);

        Optional<Registration.User> user = registration.getUserById(id);
        if (!user.isPresent()) {
            return "redirect:/register?id=" + id;
        }

        model.addAttribute(GAME, game);
        model.addAttribute(ROOM, room);
        model.addAttribute(GAME_ONLY, GameServiceImpl.removeNumbers(game));
        model.addAttribute(PLAYER_ID, user.get().getId());
        model.addAttribute(READABLE_NAME, user.get().getReadableName());
        model.addAttribute(GITHUB, user.get().getGitHubUsername());

        return "board-log";
    }

    @GetMapping("/")
    public String boardAll() {
        GameType gameType = playerService.getAnyGameWithPlayers();
        if (gameType == NullGameType.INSTANCE) {
            return "redirect:/register";
        }
        return "redirect:/board/game/" + gameType.name();
    }

    @GetMapping("/game/{game}")
    public String boardAllGames(ModelMap model,
                                @PathVariable(GAME) String game,
                                @RequestParam(value = CODE, required = false) String code,
                                @AuthenticationPrincipal Registration.User user) {
        // TODO возможно тут CAN_BE_NULL, иначе проверка (game == null) никогда не true
        validator.checkGame(game, CANT_BE_NULL);
        validator.checkCode(code, CAN_BE_NULL);

        if (game == null) {
            return "redirect:/board" + code(code);
        }

        String room = game; // TODO закончить с room

        Player player = playerService.getRandom(game);
        if (player == NullPlayer.INSTANCE) {
            // TODO а это тут вообще надо?
            return "redirect:/register?" + "game" + "=" + game;
        }
        GameType gameType = player.getGameType(); // TODO а тут точно сеттинги румы а не игры?
        if (gameType.getMultiplayerType(gameType.getSettings()) == MultiplayerType.MULTIPLE) {
            return "redirect:/board/player/" + player.getId() + code(code);
        }

        if (user != null && code == null) {
            code = user.getCode();
        }

        GameBoardData gameBoardData = boardData.getInfoForGame(game);
        populateBoardAttributes(model, code, gameBoardData, room, null, null, null, null, true);
        return "board";
    }

    @GetMapping(value = "/", params = CODE)
    public String boardAll(ModelMap model, @RequestParam(CODE) String code) {
        validator.checkCode(code, CAN_BE_NULL);

        String id = registration.getIdByCode(code);
        Player player = playerService.get(id);
        if (player == NullPlayer.INSTANCE) {
            player = playerService.getRandom(null);
        }
        if (player == NullPlayer.INSTANCE) {
            return "redirect:/register";
        }
        GameType gameType = player.getGameType(); // TODO а тут точно сеттинги румы а не игры?
        if (gameType.getMultiplayerType(gameType.getSettings()) != MultiplayerType.SINGLE) {
            return "redirect:/board/player/" + player.getId() + code(code);
        }

        model.addAttribute(CODE, code);
        model.addAttribute(GAME, player.getGame());
        model.addAttribute(ROOM, player.getRoom());
        model.addAttribute(GAME_ONLY, player.getGameOnly());
        model.addAttribute(PLAYER_ID, player.getId());
        model.addAttribute(READABLE_NAME, player.getReadableName());
        model.addAttribute(GITHUB, player.getGitHubUsername());
        model.addAttribute(ALL_PLAYERS_SCREEN, true);
        return "board";
    }

    @PostMapping("/feedback")
    public String subscribeOrUnsubscribe(HttpServletRequest request) {
        String playerId = request.getParameter(PLAYER_ID).replace("\"", "");
        String game = request.getParameter(GAME).replace("\"", "");
        String feedbackText = request.getParameter(FEEDBACK);
        Player player = playerService.get(playerId);
        if (!feedbackText.equals("")) {
            List<Query> queries = queryClient.getQueriesForContest(game);
            for (Query q : queries) {
                subscriptionSaver.updateEmailSubscription(playerId, String.valueOf(q.getId()), getCheckBoxValue(q.getId(), "email", request), game);
                subscriptionSaver.updateSlackSubscription(playerId, String.valueOf(q.getId()), getCheckBoxValue(q.getId(), "slackEmail", request), game);
                player.updateSubscription(game, q, getCheckBoxValue(q.getId(), "email", request), getCheckBoxValue(q.getId(), "slackEmail", request));
            }
            feedbackSaver.saveFeedback(playerId, game, feedbackText);
        }

        String code = request.getParameter(CODE).replace("\"", "");
        return "redirect:/board/player/" + playerId + code(code);
    }

    private String code(@RequestParam(CODE) String code) {
        return (code != null) ? "?code=" + code : "";
    }

    private boolean getCheckBoxValue(int queryId, String forWhichCheckBox, HttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(forWhichCheckBox + queryId));
    }

    private void setUpQueries(Player player, String game) {
        List<Query> allActiveQueries = queryClient.getQueriesForContest(game);
        List<Integer> userQueryIds = player.getSubscriptionIdsForGame(game);

        if (allActiveQueries != null) {
            queryClient.subscribeToNewQueries(player, allActiveQueries, userQueryIds, game);
            queryClient.removeOldQueries(player, allActiveQueries, userQueryIds, game);
        }
    }

    private String formatText(String welcomeText, String readableName, String game, String repoURL) {
        return welcomeText
                .replace("*player*", readableName)
                .replace("*game*", game)
                .replace("*repositoryURL*", repoURL);
    }
}
