package com.codenjoy.dojo.services.grpc;

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

import com.codenjoy.dojo.EventsRequest;
import com.codenjoy.dojo.EventsResponse;
import com.codenjoy.dojo.config.grpc.EventsConfig;
import com.codenjoy.dojo.services.dao.PlayerGameSaver;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class EventServiceTests {

    private EventsRequest request;
    private EventsResponse response;
    private Map<String, String> events;

    @Mock
    private StreamObserver<EventsResponse> streamObserver;

    @Mock
    private EventsConfig eventsConfig;

    @Mock
    private PlayerGameSaver playerGameSaver;

    private EventService eventService;

    @Before
    public void init() {
        this.request = EventsRequest.newBuilder().build();
        this.response = EventsResponse.newBuilder().build();
        this.events = new HashMap<>();
        this.eventService = new EventService(playerGameSaver, eventsConfig);
    }

    @Test
    public void getAllEventsTest() {
        when(playerGameSaver.getEventsList()).thenReturn(events);

        eventService.getAllEvents(request, streamObserver);

        verify(streamObserver, times(1)).onNext(response);
        verify(streamObserver, times(1)).onCompleted();
    }
}
