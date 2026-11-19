package com.test.allobank.Group.service;

import com.test.allobank.Group.dto.request.CreateGroupRequest;
import com.test.allobank.Group.dto.request.CreateParticipantRequest;
import com.test.allobank.Group.dto.response.GroupResponse;
import com.test.allobank.Group.dto.response.ParticipantResponse;
import com.test.allobank.Group.entity.BillGroup;
import com.test.allobank.Group.entity.Participant;
import com.test.allobank.Group.repository.BillGroupRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

    private final BillGroupRepository billGroupRepository;

    /**
     * Create new bill group with participants.
     */
    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) {

        log.info(
                "Creating group with name={}",
                request.getName()
        );

        BillGroup group = buildGroup(request);

        BillGroup savedGroup =
                billGroupRepository.save(group);

        log.info(
                "Group created successfully. groupId={}",
                savedGroup.getPublicId()
        );

        return toResponse(savedGroup);
    }

    private BillGroup buildGroup(
            CreateGroupRequest request
    ) {

        BillGroup group = new BillGroup();

        group.setName(request.getName());

        addParticipants(group, request.getParticipants());

        return group;
    }

    private void addParticipants(
            BillGroup group,
            List<CreateParticipantRequest> participants
    ) {

        for (CreateParticipantRequest request : participants) {

            Participant participant = new Participant();

            participant.setName(request.getName());
            participant.setGroup(group);

            group.getParticipants().add(participant);
        }
    }

    private GroupResponse toResponse(
            BillGroup group
    ) {

        return GroupResponse.builder()
                .publicId(group.getPublicId())
                .name(group.getName())
                .participants(
                        group.getParticipants()
                                .stream()
                                .map(this::toParticipantResponse)
                                .collect(Collectors.toList())
                )
                .createdAt(group.getCreatedAt())
                .build();
    }

    private ParticipantResponse toParticipantResponse(
            Participant participant
    ) {

        return ParticipantResponse.builder()
                .publicId(participant.getPublicId())
                .name(participant.getName())
                .build();
    }
}