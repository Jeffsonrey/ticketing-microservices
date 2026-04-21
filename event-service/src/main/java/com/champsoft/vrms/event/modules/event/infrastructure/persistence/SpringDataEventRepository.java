package com.champsoft.vrms.event.modules.event.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SpringDataEventRepository extends JpaRepository<EventJpaEntity, String> {


    Optional<EventJpaEntity> findByTitleAndVenue_NameAndSchedule_StartDateTimeAndSchedule_EndDateTime(
            String title,
            String venueName,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
}