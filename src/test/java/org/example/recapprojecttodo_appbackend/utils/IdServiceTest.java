package org.example.recapprojecttodo_appbackend.utils;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IdServiceTest {

    @Test
    void generateId() {
        IdService idService = mock(IdService.class);

        String expectedId = UUID.randomUUID().toString();
        when(idService.generateId()).thenReturn(expectedId);

        String actualId = idService.generateId();
        
        assertEquals(expectedId, actualId);
    }
}