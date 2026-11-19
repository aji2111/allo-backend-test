package com.test.allobank.Group.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ParticipantResponse {

    private UUID publicId;

    private String name;
}