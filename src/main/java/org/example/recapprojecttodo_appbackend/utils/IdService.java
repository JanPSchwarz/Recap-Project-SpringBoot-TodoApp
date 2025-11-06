package org.example.recapprojecttodo_appbackend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Data
@Service
public class IdService {

    public String generateId() {
        return UUID.randomUUID().toString();
    }
}
