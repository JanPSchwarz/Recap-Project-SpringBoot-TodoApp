package org.example.recapprojecttodo_appbackend.dto;

import lombok.Builder;
import lombok.With;
import org.example.recapprojecttodo_appbackend.utils.Status;

@Builder
@With
public record TodoDTO(String description, Status status) {
}
