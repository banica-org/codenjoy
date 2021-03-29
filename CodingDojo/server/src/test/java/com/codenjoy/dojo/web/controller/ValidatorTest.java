package com.codenjoy.dojo.web.controller;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.dao.Registration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;

import static com.codenjoy.dojo.web.controller.Validator.CANT_BE_NULL;
import static com.codenjoy.dojo.web.controller.Validator.CAN_BE_NULL;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ValidatorTest {

    private ConfigProperties properties;
    private Registration registration;
    private Validator validator;
    private GameService gameService;
    private PlayerService playerService;

    @Before
    public void setUp() {
        validator = new Validator() {{
            ValidatorTest.this.registration = this.registration = mock(Registration.class);
            ValidatorTest.this.properties = this.properties = mock(ConfigProperties.class);
            ValidatorTest.this.gameService = this.gameService = mock(GameService.class);
            ValidatorTest.this.playerService = this.playerService = mock(PlayerService.class);
        }};
    }

    @Test
    public void validateCheckPlayerId_only() {
        shouldError("Player id is invalid: 'null'",
                () -> validator.checkPlayerId(null));

        shouldError("Player id is invalid: ''",
                () -> validator.checkPlayerId(""));

        shouldError("Player id is invalid: 'NuLL'",
                () -> validator.checkPlayerId("NuLL"));

        shouldError("Player id is invalid: 'null'",
                () -> validator.checkPlayerId("null"));

        shouldError("Player id is invalid: 'NULL'",
                () -> validator.checkPlayerId("NULL"));

        shouldError("Player id is invalid: '*F(@DF^@(&@DF(@^'",
                () -> validator.checkPlayerId("*F(@DF^@(&@DF(@^"));

        shouldError("Player id is invalid: 'too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'",
                () -> validator.checkPlayerId("too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));

        shouldOk(() -> validator.checkPlayerId("1"));

        shouldOk(() -> validator.checkPlayerId("someId"));
    }

    @Test
    public void validateCheckEmail() {
        shouldError("Player email is invalid: 'null'",
                () -> validator.checkEmail(null, CANT_BE_NULL));

        shouldOk(() -> validator.checkEmail(null, CAN_BE_NULL));

        shouldError("Player email is invalid: 'null'",
                () -> validator.checkEmail("null", CANT_BE_NULL));

        shouldOk(() -> validator.checkEmail("null", CAN_BE_NULL));

        shouldError("Player email is invalid: ''",
                () -> validator.checkEmail("", CANT_BE_NULL));

        shouldOk(() -> validator.checkEmail("", CAN_BE_NULL));

        shouldError("Player email is invalid: 'NuLL'",
                () -> validator.checkEmail("NuLL", CANT_BE_NULL));

        shouldOk(() -> validator.checkEmail("NuLL", CAN_BE_NULL));

        shouldError("Player email is invalid: 'NULL'",
                () -> validator.checkEmail("NULL", CANT_BE_NULL));

        shouldOk(() -> validator.checkEmail("NULL", CAN_BE_NULL));

        shouldError("Player email is invalid: '*F(@DF^@(&@DF(@^'",
                () -> validator.checkEmail("*F(@DF^@(&@DF(@^", CANT_BE_NULL));

        shouldError("Player email is invalid: 'too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'",
                () -> validator.checkEmail("too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", CANT_BE_NULL));

        shouldOk(() -> validator.checkEmail("qwe@asd.com", CANT_BE_NULL));

        shouldError("Player email is invalid: 'someId'",
                () -> validator.checkEmail("someId", CANT_BE_NULL));
    }

    @Test
    public void validateIsEmail() {
        assertFalse(validator.isEmail(null, CANT_BE_NULL));

        assertTrue(validator.isEmail(null, CAN_BE_NULL));

        assertFalse(validator.isEmail("null", CANT_BE_NULL));

        assertTrue(validator.isEmail("null", CAN_BE_NULL));

        assertFalse(validator.isEmail("", CANT_BE_NULL));

        assertTrue(validator.isEmail("", CAN_BE_NULL));

        assertFalse(validator.isEmail("NuLL", CANT_BE_NULL));

        assertTrue(validator.isEmail("NuLL", CAN_BE_NULL));

        assertFalse(validator.isEmail("NULL", CANT_BE_NULL));

        assertTrue(validator.isEmail("NULL", CAN_BE_NULL));

        assertFalse(validator.isEmail("*F(@DF^@(&@DF(@^", CANT_BE_NULL));

        assertFalse(validator.isEmail("too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", CANT_BE_NULL));

        assertTrue(validator.isEmail("qwe@asd.com", CANT_BE_NULL));

        assertFalse(validator.isEmail("someId", CANT_BE_NULL));
    }

    @Test
    public void validateCheckCode() {
        shouldError("Player code is invalid: 'null'",
                () -> validator.checkCode(null, CANT_BE_NULL));

        shouldOk(() -> validator.checkCode(null, CAN_BE_NULL));

        shouldError("Player code is invalid: ''",
                () -> validator.checkCode("", CANT_BE_NULL));

        shouldOk(() -> validator.checkCode("", CAN_BE_NULL));

        shouldError("Player code is invalid: 'NuLL'",
                () -> validator.checkCode("NuLL", CANT_BE_NULL));

        shouldOk(() -> validator.checkCode("NuLL", CAN_BE_NULL));

        shouldError("Player code is invalid: 'null'",
                () -> validator.checkCode("null", CANT_BE_NULL));

        shouldOk(() -> validator.checkCode("null", CAN_BE_NULL));

        shouldError("Player code is invalid: 'NULL'",
                () -> validator.checkCode("NULL", CANT_BE_NULL));

        shouldOk(() -> validator.checkCode("NULL", CAN_BE_NULL));

        shouldError("Player code is invalid: '*F(@DF^@(&@DF(@^'",
                () -> validator.checkCode("*F(@DF^@(&@DF(@^", CANT_BE_NULL));

        shouldError("Player code is invalid: 'too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'",
                () -> validator.checkCode("too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", CANT_BE_NULL));

        shouldOk(() -> validator.checkCode("1", CANT_BE_NULL));

        shouldOk(() -> validator.checkCode("0", CAN_BE_NULL));

        shouldOk(() -> validator.checkCode("434589345613405760956134056340596345903465", CANT_BE_NULL));

        shouldError("Player code is invalid: 'someId'",
                () -> validator.checkCode("someId", CANT_BE_NULL));

        shouldError("Player code is invalid: 'some@email.com'",
                () -> validator.checkCode("some@email.com", CANT_BE_NULL));
    }

    @Test
    public void validateIsGameName() {
        assertFalse("Game name is invalid: 'null'",
                validator.isGameName(null, CANT_BE_NULL));

        assertTrue(validator.isGameName(null, CAN_BE_NULL));

        assertFalse("Game name is invalid: ''",
                validator.isGameName("", CANT_BE_NULL));

        assertTrue(validator.isGameName("", CAN_BE_NULL));

        assertFalse("Game name is invalid: 'NuLL'",
                validator.isGameName("NuLL", CANT_BE_NULL));

        assertTrue(validator.isGameName("NuLL", CAN_BE_NULL));

        assertFalse("Game name is invalid: 'null'",
                validator.isGameName("null", CANT_BE_NULL));

        assertTrue(validator.isGameName("null", CAN_BE_NULL));

        assertFalse("Game name is invalid: 'NULL'",
                validator.isGameName("NULL", CANT_BE_NULL));

        assertTrue(validator.isGameName("NULL", CAN_BE_NULL));

        assertFalse("Game name is invalid: '*F(@DF^@(&@DF(@^'",
                validator.isGameName("*F(@DF^@(&@DF(@^", CANT_BE_NULL));

        assertFalse("Game name is invalid: 'too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'",
                validator.isGameName("too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", CANT_BE_NULL));

        assertFalse("Game name is invalid: '-game'",
                validator.isGameName("-game", CANT_BE_NULL));

        assertFalse("Game name is invalid: 'game-'",
                validator.isGameName("game-", CANT_BE_NULL));

        assertTrue(validator.isGameName("a-game", CANT_BE_NULL));

        assertFalse("Game name is invalid: '_game'",
                validator.isGameName("_game", CANT_BE_NULL));

        assertFalse("Game name is invalid: 'game_'",
                validator.isGameName("game_", CANT_BE_NULL));

        assertTrue(validator.isGameName("a_game", CANT_BE_NULL));

        assertFalse("Game name is invalid: '.game'",
                validator.isGameName(".game", CANT_BE_NULL));

        assertFalse("Game name is invalid: 'game.'",
                validator.isGameName("game.", CANT_BE_NULL));

        assertTrue(validator.isGameName("a.game", CANT_BE_NULL));

        assertFalse("Game name is invalid: '1'",
                validator.isGameName("1", CANT_BE_NULL));

        assertTrue(validator.isGameName("a1", CANT_BE_NULL));

        assertTrue(validator.isGameName("a1", CANT_BE_NULL));

        assertFalse("Game name is invalid: '0'",
                validator.isGameName("0", CAN_BE_NULL));

        assertFalse("Game name is invalid: '434589345613405760956134056340596345903465'",
                validator.isGameName("434589345613405760956134056340596345903465", CANT_BE_NULL));

        assertTrue(validator.isGameName("someGame", CANT_BE_NULL));

        assertFalse("Game name is invalid: 'some@email.com'",
                validator.isGameName("some@email.com", CANT_BE_NULL));
    }

    @Test
    public void validateCheckGameName() {
        shouldError("Game name is invalid: 'null'",
                () -> validator.checkGame(null, CANT_BE_NULL));

        shouldOk(() -> validator.checkGame(null, CAN_BE_NULL));

        shouldError("Game name is invalid: ''",
                () -> validator.checkGame("", CANT_BE_NULL));

        shouldOk(() -> validator.checkGame("", CAN_BE_NULL));

        shouldError("Game name is invalid: 'NuLL'",
                () -> validator.checkGame("NuLL", CANT_BE_NULL));

        shouldOk(() -> validator.checkGame("NuLL", CAN_BE_NULL));

        shouldError("Game name is invalid: 'null'",
                () -> validator.checkGame("null", CANT_BE_NULL));

        shouldOk(() -> validator.checkGame("null", CAN_BE_NULL));

        shouldError("Game name is invalid: 'NULL'",
                () -> validator.checkGame("NULL", CANT_BE_NULL));

        shouldOk(() -> validator.checkGame("NULL", CAN_BE_NULL));

        shouldError("Game name is invalid: '*F(@DF^@(&@DF(@^'",
                () -> validator.checkGame("*F(@DF^@(&@DF(@^", CANT_BE_NULL));

        shouldError("Game name is invalid: 'too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'",
                () -> validator.checkGame("too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", CANT_BE_NULL));

        shouldError("Game name is invalid: '-game'",
                () -> validator.checkGame("-game", CANT_BE_NULL));

        shouldError("Game name is invalid: 'game-'",
                () -> validator.checkGame("game-", CANT_BE_NULL));

        shouldOk(() -> validator.checkGame("a-game", CANT_BE_NULL));

        shouldError("Game name is invalid: '_game'",
                () -> validator.checkGame("_game", CANT_BE_NULL));

        shouldError("Game name is invalid: 'game_'",
                () -> validator.checkGame("game_", CANT_BE_NULL));

        shouldOk(() -> validator.checkGame("a_game", CANT_BE_NULL));

        shouldError("Game name is invalid: '.game'",
                () -> validator.checkGame(".game", CANT_BE_NULL));

        shouldError("Game name is invalid: 'game.'",
                () -> validator.checkGame("game.", CANT_BE_NULL));

        shouldOk(() -> validator.checkGame("a.game", CANT_BE_NULL));

        shouldError("Game name is invalid: '1'",
                () -> validator.checkGame("1", CANT_BE_NULL));

        shouldOk(() -> validator.checkGame("a1", CANT_BE_NULL));

        shouldOk(() -> validator.checkGame("a1", CANT_BE_NULL));

        shouldError("Game name is invalid: '0'",
                () -> validator.checkGame("0", CAN_BE_NULL));

        shouldError("Game name is invalid: '434589345613405760956134056340596345903465'",
                () -> validator.checkGame("434589345613405760956134056340596345903465", CANT_BE_NULL));

        shouldOk(() -> validator.checkGame("someGame", CANT_BE_NULL));

        shouldError("Game name is invalid: 'some@email.com'",
                () -> validator.checkGame("some@email.com", CANT_BE_NULL));
    }

    @Test
    public void validateCheckRoomName() {
        shouldError("Room name is invalid: 'null'",
                () -> validator.checkRoom(null, CANT_BE_NULL));

        shouldOk(() -> validator.checkRoom(null, CAN_BE_NULL));

        shouldError("Room name is invalid: ''",
                () -> validator.checkRoom("", CANT_BE_NULL));

        shouldOk(() -> validator.checkRoom("", CAN_BE_NULL));

        shouldError("Room name is invalid: 'NuLL'",
                () -> validator.checkRoom("NuLL", CANT_BE_NULL));

        shouldOk(() -> validator.checkRoom("NuLL", CAN_BE_NULL));

        shouldError("Room name is invalid: 'null'",
                () -> validator.checkRoom("null", CANT_BE_NULL));

        shouldOk(() -> validator.checkRoom("null", CAN_BE_NULL));

        shouldError("Room name is invalid: 'NULL'",
                () -> validator.checkRoom("NULL", CANT_BE_NULL));

        shouldOk(() -> validator.checkRoom("NULL", CAN_BE_NULL));

        shouldError("Room name is invalid: '*F(@DF^@(&@DF(@^'",
                () -> validator.checkRoom("*F(@DF^@(&@DF(@^", CANT_BE_NULL));

        shouldError("Room name is invalid: 'too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'",
                () -> validator.checkRoom("too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", CANT_BE_NULL));

        shouldError("Room name is invalid: '-game'",
                () -> validator.checkRoom("-game", CANT_BE_NULL));

        shouldError("Room name is invalid: 'game-'",
                () -> validator.checkRoom("game-", CANT_BE_NULL));

        shouldOk(() -> validator.checkRoom("a-game", CANT_BE_NULL));

        shouldError("Room name is invalid: '_game'",
                () -> validator.checkRoom("_game", CANT_BE_NULL));

        shouldError("Room name is invalid: 'game_'",
                () -> validator.checkRoom("game_", CANT_BE_NULL));

        shouldOk(() -> validator.checkRoom("a_game", CANT_BE_NULL));

        shouldError("Room name is invalid: '.game'",
                () -> validator.checkRoom(".game", CANT_BE_NULL));

        shouldError("Room name is invalid: 'game.'",
                () -> validator.checkRoom("game.", CANT_BE_NULL));

        shouldOk(() -> validator.checkRoom("a.game", CANT_BE_NULL));

        shouldError("Room name is invalid: '1'",
                () -> validator.checkRoom("1", CANT_BE_NULL));

        shouldOk(() -> validator.checkRoom("a1", CANT_BE_NULL));

        shouldOk(() -> validator.checkRoom("a1", CANT_BE_NULL));

        shouldError("Room name is invalid: '0'",
                () -> validator.checkRoom("0", CAN_BE_NULL));

        shouldError("Room name is invalid: '434589345613405760956134056340596345903465'",
                () -> validator.checkRoom("434589345613405760956134056340596345903465", CANT_BE_NULL));

        shouldOk(() -> validator.checkRoom("someGame", CANT_BE_NULL));

        shouldError("Room name is invalid: 'some@email.com'",
                () -> validator.checkRoom("some@email.com", CANT_BE_NULL));
    }

    @Test
    public void validateCheckMd5() {
        shouldError("Hash is invalid: 'null'",
                () -> validator.checkMD5(null));

        shouldError("Hash is invalid: ''",
                () -> validator.checkMD5(""));

        shouldError("Hash is invalid: 'NuLL'",
                () -> validator.checkMD5("NuLL"));

        shouldError("Hash is invalid: 'null'",
                () -> validator.checkMD5("null"));

        shouldError("Hash is invalid: 'NULL'",
                () -> validator.checkMD5("NULL"));

        shouldError("Hash is invalid: '*F(@DF^@(&@DF(@^'",
                () -> validator.checkMD5("*F(@DF^@(&@DF(@^"));

        shouldError("Hash is invalid: 'too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'",
                () -> validator.checkMD5("too large aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));

        shouldError("Hash is invalid: '3-13dc7cb57f02b9c7c066b9e34b6fe72'",
                () -> validator.checkMD5("3-13dc7cb57f02b9c7c066b9e34b6fe72"));

        shouldError("Hash is invalid: '3_13dc7cb57f02b9c7c066b9e34b6fe72'",
                () -> validator.checkMD5("3_13dc7cb57f02b9c7c066b9e34b6fe72"));

        shouldError("Hash is invalid: '3.13dc7cb57f02b9c7c066b9e34b6fe72'",
                () -> validator.checkMD5("3.13dc7cb57f02b9c7c066b9e34b6fe72"));

        shouldError("Hash is invalid: '1'",
                () -> validator.checkMD5("1"));

        shouldError("Hash is invalid: '0'",
                () -> validator.checkMD5("0"));

        shouldError("Hash is invalid: '434589345613405760956134056340596345903465'",
                () -> validator.checkMD5("434589345613405760956134056340596345903465"));

        shouldOk(() -> validator.checkMD5("313dc7cb57f02b9c7c066b9e34b6fe72"));

        shouldError("Hash is invalid: 'some@email.com'",
                () -> validator.checkMD5("some@email.com"));
    }

    @Test
    public void validateCheckCommand() {
        shouldError("Command is invalid: 'null'",
                () -> validator.checkCommand(null));

        shouldError("Command is invalid: ''",
                () -> validator.checkCommand(""));

        shouldError("Command is invalid: 'NuLL'",
                () -> validator.checkCommand("NuLL"));

        shouldError("Command is invalid: 'null'",
                () -> validator.checkCommand("null"));

        shouldError("Command is invalid: 'NULL'",
                () -> validator.checkCommand("NULL"));

        shouldError("Command is invalid: '*F(@DF^@(&@DF(@^'",
                () -> validator.checkCommand("*F(@DF^@(&@DF(@^"));

        shouldError("Command is invalid: '3-13dc7cb57f02b9c7c066b9e34b6fe72'",
                () -> validator.checkCommand("3-13dc7cb57f02b9c7c066b9e34b6fe72"));

        shouldError("Command is invalid: '3_13dc7cb57f02b9c7c066b9e34b6fe72'",
                () -> validator.checkCommand("3_13dc7cb57f02b9c7c066b9e34b6fe72"));

        shouldError("Command is invalid: '3.13dc7cb57f02b9c7c066b9e34b6fe72'",
                () -> validator.checkCommand("3.13dc7cb57f02b9c7c066b9e34b6fe72"));

        shouldError("Command is invalid: '1'",
                () -> validator.checkCommand("1"));

        shouldError("Command is invalid: '0'",
                () -> validator.checkCommand("0"));

        shouldError("Command is invalid: '434589345613405760956134056340596345903465'",
                () -> validator.checkCommand("434589345613405760956134056340596345903465"));

        shouldOk(() -> validator.checkCommand("act(1,2,3),Left,messAGE('SOME TEXT'),RIGHT,ACT(),down,uP,act(1, 2 , 3)  ,DOWN"));

        shouldOk(() -> validator.checkCommand("act()"));

        shouldOk(() -> validator.checkCommand("DOWN"));

        shouldOk(() -> validator.checkCommand("leFT"));

        shouldOk(() -> validator.checkCommand("right,up"));

        shouldOk(() -> validator.checkCommand("left()"));

        shouldOk(() -> validator.checkCommand("act()()"));

        shouldOk(() -> validator.checkCommand("act(1,       2)"));

        shouldOk(() -> validator.checkCommand("act"));

        shouldOk(() -> validator.checkCommand("act(1, 34"));

        // TODO вот тут как-то не совсем верно
        shouldOk(() -> validator.checkCommand("right?up"));
        shouldOk(() -> validator.checkCommand("-right,up"));
        shouldOk(() -> validator.checkCommand("&^@#%&^right@#$&*up@$"));

        shouldError("Command is invalid: 'qwe'",
                () -> validator.checkCommand("qwe"));

        shouldError("Command is invalid: 'qwe,asd'",
                () -> validator.checkCommand("qwe,asd"));

        shouldError("Command is invalid: 'qwe(1,3)'",
                () -> validator.checkCommand("qwe(1,3)"));

        shouldOk(() -> validator.checkCommand("message('кириллица')"));

        shouldOk(() -> validator.checkCommand("message('latin')"));

        shouldOk(() -> validator.checkCommand("message(''')"));

        shouldOk(() -> validator.checkCommand("message(''''''''')"));

        shouldError("Command is invalid: 'messAGE('TOO LARGE'),message('aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa')'",
                () -> validator.checkCommand("messAGE('TOO LARGE'),message('aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa')"));

        shouldError("Command is invalid: 'some@email.com'",
                () -> validator.checkCommand("some@email.com"));

        shouldOk(() -> validator.checkCommand("message('some@email.com')"));
    }

    @Test
    public void validateCheckPlayerCode() {
        when(registration.checkUser(anyString(), anyString())).thenAnswer(inv -> inv.getArgument(0));

        shouldError("Player id is invalid: 'email@gmail.com'",
                () -> validator.checkPlayerCode("email@gmail.com", "12345678901234567890"));

        shouldOk(() -> validator.checkPlayerCode("validPlayerId", "12345678901234567890"));

        shouldError("Player id is invalid: 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@aaa.aaa'",
                () -> validator.checkPlayerCode("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@aaa.aaa", "12345678901234567890"));

        shouldError("Player id is invalid: 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'",
                () -> validator.checkPlayerCode("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "12345678901234567890"));

        shouldError("Player code is invalid: '000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000'",
                () -> validator.checkPlayerCode("codePlayerId", "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"));

        shouldError("Player id is invalid: 'email#&*^#gmail%#&^*com'",
                () -> validator.checkPlayerCode("email#&*^#gmail%#&^*com", "12345678901234567890"));

        shouldError("Player code is invalid: '12dehgfsgfsdlfidfj90'",
                () -> validator.checkPlayerCode("validPlayerId", "12dehgfsgfsdlfidfj90"));

        shouldError("Player id is invalid: 'null'",
                () -> validator.checkPlayerCode(null, "12345678901234567890"));

        shouldError("Player code is invalid: 'null'",
                () -> validator.checkPlayerCode("validPlayerId", null));
    }

    @Test
    public void validateCheckPlayerId() {
        shouldOk(() -> validator.checkPlayerId("validPlayerId", CANT_BE_NULL));

        shouldOk(() -> validator.checkPlayerId("valid-player-id", CANT_BE_NULL));

        shouldOk(() -> validator.checkPlayerId("codePlayerId", CANT_BE_NULL));

        shouldError("Player id is invalid: 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@aaa.aaa'",
                () -> validator.checkPlayerId("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@aaa.aaa", CANT_BE_NULL));

        shouldError("Player id is invalid: 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa'",
                () -> validator.checkPlayerId("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", CANT_BE_NULL));

        shouldError("Player id is invalid: 'email#&*^#gmail%#&^*com'",
                () -> validator.checkPlayerId("email#&*^#gmail%#&^*com", CANT_BE_NULL));

        shouldError("Player id is invalid: 'null'",
                () -> validator.checkPlayerId(null, CANT_BE_NULL));

        shouldError("Player id is invalid: 'NuLL'",
                () -> validator.checkPlayerId("NuLL", CANT_BE_NULL));

        shouldError("Player id is invalid: ''",
                () -> validator.checkPlayerId("", CANT_BE_NULL));

        shouldError("Player id is invalid: 'null'",
                () -> validator.checkPlayerId(null, CANT_BE_NULL));

        shouldOk(() -> validator.checkPlayerId(null, CAN_BE_NULL));

        shouldOk(() -> validator.checkPlayerId("null", CAN_BE_NULL));

        shouldOk(() -> validator.checkPlayerId("", CAN_BE_NULL));

        shouldOk(() -> validator.checkPlayerId("nULL", CAN_BE_NULL));
    }

    @Test
    public void validateIsReadablePlayerName() {
        assertTrue(validator.isGitHubUsername("Стивен Пупкин"));

        assertTrue(validator.isGitHubUsername("Oleksandr Baglay"));

        assertTrue(validator.isGitHubUsername("Stiven Pupkin"));

        assertTrue(validator.isGitHubUsername("стивен пупкин"));

        assertTrue(validator.isGitHubUsername("stiven pupkin"));

        assertTrue(validator.isGitHubUsername("ABCDEFGHIJKLMNOPQRSTUVQXYZ abcdefghijklmnopqrstuvqxyz"));

        assertTrue(validator.isGitHubUsername("abcdefghijklmnopqrstuvqxyz ABCDEFGHIJKLMNOPQRSTUVQXYZ"));

        assertTrue(validator.isGitHubUsername("абвгдеёжзийклмо НПРСТУФХЧЦЬЫЪЭЮЯ"));

        assertTrue(validator.isGitHubUsername("нпрстуфхчцьыъэюя АБВГДЕЁЖЗИЙКЛМО"));

        assertTrue(validator.isGitHubUsername("АБВГДЕЁЖЗИЙКЛМО нпрстуфхчцьыъэюя"));

        assertTrue(validator.isGitHubUsername("НПРСТУФХЧЦЬЫЪЭЮЯ абвгдеёжзийклмо"));

        assertTrue(validator.isGitHubUsername("ҐґІіІіЄє ҐґІіІіЄє"));

        assertFalse("Player name is invalid (should be 'Name Surname'): 'Стивен'",
                validator.isGitHubUsername("Стивен"));

        assertFalse("Player name is invalid (should be 'Name Surname'): 'Я Д'Артаньян'",
                validator.isGitHubUsername("Я Д'Артаньян"));

        assertFalse("Player name is invalid (should be 'Name Surname'): 'Дефис-нельзя'",
                validator.isGitHubUsername("Дефис-нельзя"));

        assertFalse("Player name is invalid (should be 'Name Surname'): 'Двапробела  нельзя'",
                validator.isGitHubUsername("Двапробела  нельзя"));

        assertFalse("Player name is invalid (should be 'Name Surname'): 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaa'",
                validator.isGitHubUsername("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaa"));

        assertFalse("Player name is invalid (should be 'Name Surname'): 'email#&*^#gmail%#&^*com'",
                validator.isGitHubUsername("email#&*^#gmail%#&^*com"));

        assertFalse("Player name is invalid (should be 'Name Surname'): 'null'",
                validator.isGitHubUsername(null));

        assertFalse("Player name is invalid (should be 'Name Surname'): 'NuLL'",
                validator.isGitHubUsername("NuLL"));

        assertFalse("Player name is invalid (should be 'Name Surname'): ''",
                validator.isGitHubUsername(""));

        assertFalse("Player name is invalid (should be 'Name Surname'): 'null'",
                validator.isGitHubUsername(null));
    }

    @Test
    public void validateIsNickName() {
        assertTrue(validator.isNickName("Стивен Пупкин"));

        assertTrue(validator.isNickName("Oleksandr Baglay"));

        assertTrue(validator.isNickName("Stiven Pupkin"));

        assertTrue(validator.isNickName("FuTuRamA"));

        assertTrue(validator.isNickName("BLIvl evLVe cyuw7 82fx"));

        assertTrue(validator.isNickName("123 444 56 7890 2231"));

        assertTrue(validator.isNickName("стивен пупкин"));

        assertTrue(validator.isNickName("stiven pupkin"));

        assertTrue(validator.isNickName("ABCDEFGHIJKLMNOP abcdefghijklmnop"));

        assertTrue(validator.isNickName("QRSTUVQXYZ qrstuvqxyz"));

        assertTrue(validator.isNickName("qrstuvqxyz QRSTUVQXYZ"));

        assertTrue(validator.isNickName("abcdefghijklmnop ABCDEFGHIJKLMNOP"));

        assertTrue(validator.isNickName("абвгдеёжзийклмо НПРСТУФХЧЦЬЫЪЭЮЯ"));

        assertTrue(validator.isNickName("нпрстуфхчцьыъэюя АБВГДЕЁЖЗИЙКЛМО"));

        assertTrue(validator.isNickName("АБВГДЕЁЖЗИЙКЛМО нпрстуфхчцьыъэюя"));

        assertTrue(validator.isNickName("НПРСТУФХЧЦЬЫЪЭЮЯ абвгдеёжзийклмо"));

        assertTrue(validator.isNickName("ҐґІіІіЄє ҐґІіІіЄє"));

        assertTrue(validator.isNickName("Стивен"));

        assertFalse("Player name is invalid (should be 'Name Surname' or 'niCKnAMe'): 'Я Д'Артаньян'",
                validator.isNickName("Я Д'Артаньян"));

        assertFalse("Player name is invalid (should be 'Name Surname' or 'niCKnAMe'): 'Дефис-нельзя'",
                validator.isNickName("Дефис-нельзя"));

        assertTrue(validator.isNickName("Двапробела  нельзя"));

        assertFalse("Player name is invalid (should be 'Name Surname' or 'niCKnAMe'): 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaa'",
                validator.isNickName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaa"));

        assertFalse("Player name is invalid (should be 'Name Surname' or 'niCKnAMe'): 'email#&*^#gmail%#&^*com'",
                validator.isNickName("email#&*^#gmail%#&^*com"));

        assertFalse("Player name is invalid (should be 'Name Surname' or 'niCKnAMe'): 'null'",
                validator.isNickName(null));

        assertFalse("Player name is invalid (should be 'Name Surname' or 'niCKnAMe'): 'NuLL'",
                validator.isNickName("NuLL"));

        assertFalse("Player name is invalid (should be 'Name Surname' or 'niCKnAMe'): ''",
                validator.isNickName(""));

        assertFalse("Player name is invalid (should be 'Name Surname' or 'niCKnAMe'): 'null'",
                validator.isNickName(null));
    }

    private void shouldOk(Runnable toRun) {
        shouldError("", toRun);
    }

    private void shouldError(String expected, Runnable toRun) {
        try {
            if (toRun != null) {
                toRun.run();
            }
            if (StringUtils.isNotEmpty(expected)) {
                fail();
            }
        } catch (Exception e) {
            assertEquals(expected, e.getMessage());
        }
    }

    private void shouldReturn(String expected, Callable toRun) {
        try {
            Object result = null;
            if (toRun != null) {
                result = toRun.call();
            }
            assertEquals(expected, result);
        } catch (Exception e) {
            assertEquals(expected, e.getMessage());
        }
    }

    @Test
    public void validateCheckNotEmpty() {
        shouldError("Parameter name is empty: 'Null'",
                () -> validator.checkNotEmpty("name", "Null"));

        shouldError("Parameter name is empty: 'null'",
                () -> validator.checkNotEmpty("name", null));

        shouldError("Parameter name is empty: 'NULL'",
                () -> validator.checkNotEmpty("name", "NULL"));

        shouldError("Parameter name is empty: 'null'",
                () -> validator.checkNotEmpty("name", "null"));

        shouldError("Parameter name is empty: ''",
                () -> validator.checkNotEmpty("name", ""));

        shouldOk(() -> validator.checkNotEmpty("name", "not-empty"));
    }

    @Test
    public void validateCheckGameType() {
        when(gameService.exists(anyString())).thenReturn(false);

        // empty string
        shouldError("Game name is invalid: 'Null'",
                () -> validator.checkGameType("Null"));

        shouldError("Game name is invalid: 'null'",
                () -> validator.checkGameType(null));

        shouldError("Game name is invalid: 'NULL'",
                () -> validator.checkGameType("NULL"));

        shouldError("Game name is invalid: 'null'",
                () -> validator.checkGameType("null"));

        shouldError("Game name is invalid: ''",
                () -> validator.checkGameType(""));

        shouldError("Game not found: not-empty",
                () -> validator.checkGameType("not-empty"));

        // other cases
        when(gameService.exists("valid-game")).thenReturn(true);
        when(gameService.exists("bad-game")).thenReturn(false);

        shouldOk(() -> validator.checkGameType("valid-game"));

        shouldError("Game not found: bad-game",
                () -> validator.checkGameType("bad-game"));
    }

    @Test
    public void validateCheckPlayerInRoom() {
        givenPlayer("validPlayerId", "validRoomName");
        givenPlayer("otherPlayer", "otherRoomName");

        shouldOk(() -> validator.checkPlayerInRoom("validPlayerId", "validRoomName"));

        shouldError("Player id is invalid: '$bad$'",
                () -> validator.checkPlayerInRoom("$bad$", "validRoomName"));

        shouldError("Room name is invalid: '$bad$'",
                () -> validator.checkPlayerInRoom("validPlayerId", "$bad$"));

        shouldError("Player 'otherPlayer' is not in room 'validRoomName'",
                () -> validator.checkPlayerInRoom("otherPlayer", "validRoomName"));
    }

    private void givenPlayer(String id, String room) {
        when(playerService.get(id))
                .thenReturn(new Player(id){{
                    setRoom(room);
                }});
    }
}