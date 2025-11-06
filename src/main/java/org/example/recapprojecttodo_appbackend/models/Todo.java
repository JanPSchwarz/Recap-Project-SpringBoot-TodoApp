package org.example.recapprojecttodo_appbackend.models;

import lombok.Builder;
import lombok.With;
import org.example.recapprojecttodo_appbackend.utils.Status;

@Builder
@With
public record Todo(String id, String description, Status status) {
}
