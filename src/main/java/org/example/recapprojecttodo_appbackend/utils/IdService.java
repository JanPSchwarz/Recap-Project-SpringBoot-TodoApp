package org.example.recapprojecttodo_appbackend.utils;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Data
@Service
public class IdService {

    public String generateId() {
        return UUID.randomUUID().toString();
    }

    public String generateErrorId() {
        return "error-" + UUID.randomUUID();
    }
}
