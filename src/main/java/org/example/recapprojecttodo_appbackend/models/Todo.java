package org.example.recapprojecttodo_appbackend.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;
import org.example.recapprojecttodo_appbackend.utils.Status;

@Builder
@With
public record Todo(String id,
                   @NotBlank(message = "description must not be empty") String description,
                   @NotNull(message = "status must not be empty ('OPEN', 'IN_PROGRESS' or 'DONE')") Status status) {


    public Todo(String description, Status status) {
        this("", description, status);
    }
}
