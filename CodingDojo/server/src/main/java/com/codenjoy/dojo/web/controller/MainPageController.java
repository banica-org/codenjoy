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
import com.codenjoy.dojo.services.GameService;
import com.codenjoy.dojo.services.Player;
import com.codenjoy.dojo.services.PlayerService;
import com.codenjoy.dojo.services.dao.BoardData;
import com.codenjoy.dojo.services.dao.Registration;
import com.codenjoy.dojo.services.nullobj.NullPlayer;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.web.controller.Validator.CANT_BE_NULL;
import static com.codenjoy.dojo.web.controller.Validator.CAN_BE_NULL;

@Controller
@AllArgsConstructor
public class MainPageController {

    public static final String HELP_URI = "/help";

    private PlayerService playerService;
    private Registration registration;
    private GameService gameService;
    private Validator validator;
    private ConfigProperties properties;
    private RoomsAliaser rooms;
    private BoardData boardData;

    @GetMapping(HELP_URI)
    public String help(Model model) {
        model.addAttribute("games", gameService.getOnlyGames());
        return "help";
    }

    @GetMapping(value = HELP_URI, params = "game")
    public String helpForGame(@RequestParam("game") String game) {
        validator.checkGame(game, CANT_BE_NULL);

        String language = properties.getHelpLanguage();
        String suffix = (StringUtils.isEmpty(language)) ? "" : ("-" + language);
        return "redirect:resources/help/" + game + suffix + ".html";
    }

    @GetMapping("/")
    public String getMainPage(HttpServletRequest request, Model model,
                              @AuthenticationPrincipal Registration.User user) {
        // если указана кастомная домашняя страничка - редиректим туда
        String mainPage = properties.getMainPage();
        if (StringUtils.isNotEmpty(mainPage)) {
            model.addAttribute("url", mainPage);
            return "redirect";
        }

        if (user == null && !properties.isAllowUnauthorizedMainPage()) {
            return "redirect:login";
        }

//        List<String> games = gameService.getGames();
//        System.out.println(games.size());
//        // игра одна
//        if (games.size() == 1) {
//            if (user == null) {
//                // юзер неавторизирован - показываем все борды в этой игре
//                return "redirect:board/game/" + games.get(0);
//            }
//            // юзер авторизирован - показываем борду юзера
//            return "redirect:" + registrationService.getBoardUrl(user.getCode(), user.getId(), null);
//        }

        // игр несколько - грузим страничку с возможносью подглядеть за любой игрой
        return getMainPage(request, null, model);
    }

    @GetMapping(value = "/", params = "code")
    public String getMainPage(HttpServletRequest request,
                              @RequestParam("code") String code,
                              Model model) {
        validator.checkCode(code, CAN_BE_NULL);

        String userIp = request.getRemoteAddr();
        model.addAttribute("ip", userIp);

        Player player = playerService.get(registration.getIdByCode(code));
        Set<Map.Entry<String, String>> activeGames = boardData.allGames().stream().filter(entry -> rooms.all().contains(entry)).collect(Collectors.toSet());
        boolean registered = player != NullPlayer.INSTANCE;
        request.setAttribute("registered", registered);
        request.setAttribute("code", code);
        model.addAttribute("game", registered ? player.getGame() : StringUtils.EMPTY);
        model.addAttribute("games", activeGames);
        return "main";
    }

    @RequestMapping("/denied")
    public ModelAndView displayAccessDeniedPage() {
        return new ModelAndView() {{
            addObject("message", "Invalid Username or Password");
            setViewName("errorPage");
        }};
    }

    @GetMapping("/donate")
    public String donate(ModelMap model) {
        model.addAttribute("today", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        model.addAttribute("donateCode", properties.getDonateCode());
        return "donate-form";
    }

    @RequestMapping("/help")
    public String help() {
        return "help";
    }

}
