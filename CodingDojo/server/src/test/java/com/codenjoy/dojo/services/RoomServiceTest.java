package com.codenjoy.dojo.services;

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

import com.codenjoy.dojo.services.mocks.FirstGameType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RoomServiceTest {

    private RoomService service;
    private GameType gameType;

    @Before
    public void setUp() {
        // given
        service = new RoomService();
        gameType = new FirstGameType();
    }

    @Test
    public void shouldRoomIsNotActive_ifNotPresent() {
        // when then
        assertEquals(false, service.isActive("non-exists-room"));
    }

    @Test
    public void shouldRoomIsActiveByDefault_ifPresent() {
        // given
        service.create("room", gameType);

        // when then
        assertEquals(true, service.isActive("room"));
    }

    @Test
    public void shouldCreateAgain_whenCreated() {
        // given
        service.create("room", gameType);

        // when
        service.create("room", gameType);

        // then
        assertEquals(true, service.isActive("room"));
    }

    @Test
    public void shouldException_whenSetActiveOfNonExistsRoom() {
        // when
        service.setActive("room", true);

        // then
        assertEquals(false, service.isActive("room"));
    }

    @Test
    public void shouldChangeRoomActiveness() {
        // given
        service.create("room", mock(GameType.class));

        // when
        service.setActive("room", false);

        // then
        assertEquals(false, service.isActive("room"));

        // when
        service.setActive("room", true);

        // then
        assertEquals(true, service.isActive("room"));
    }

    @Test
    public void shouldGetState_ifCreated() {
        // given
        service.create("room", gameType);

        // when then
        assertEquals("RoomService.RoomState(name=room, " +
                        "type=RoomGameType{type=GameType[first], " +
                        "settings=First-SettingsImpl(map={" +
                            "Parameter 1=[Parameter 1:Integer = multiline[false] def[12] val[15]], " +
                            "Parameter 2=[Parameter 2:Boolean = def[true] val[true]]})}, " +
                        "active=true)",
                service.state("room").toString());
    }

    @Test
    public void shouldGetState_ifNotCreated() {
        // when then
        assertEquals(null, service.state("room"));
    }

    @Test
    public void shouldGetSettings_ifCreated() {
        // given
        service.create("room", gameType);

        // when then
        assertEquals("First-SettingsImpl(map={" +
                        "Parameter 1=[Parameter 1:Integer = multiline[false] def[12] val[15]], " +
                        "Parameter 2=[Parameter 2:Boolean = def[true] val[true]]})",
                service.settings("room").toString());
    }

    @Test
    public void shouldGetSettings_ifNotCreated() {
        // when then
        assertEquals(null, service.settings("room"));
    }
}