package com.test.allobank.Group.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.allobank.Group.entity.Participant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByPublicId(UUID publicId);

    List<Participant> findAllByPublicIdIn(List<UUID> publicIds);
}
