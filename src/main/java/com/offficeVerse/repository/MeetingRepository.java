package com.offficeVerse.repository;

import com.offficeVerse.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Optional<Meeting> findByRoomIdAndIsActiveTrue(String roomId);

    Optional<Meeting> findByRoomId(String roomId);

    void deleteByRoomId(String roomId);
}