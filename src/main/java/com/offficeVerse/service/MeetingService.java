package com.offficeVerse.service;

import com.offficeVerse.model.Meeting;
import com.offficeVerse.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    /**
     * Get or create meeting for a specific room
     * If meeting exists and is active, return it
     * Otherwise, create a new one
     */
    @Transactional
    public Map<String, Object> getOrCreateMeeting(String roomId, String playerName) {
        Map<String, Object> response = new HashMap<>();

        // Check if active meeting exists for this room
        Optional<Meeting> existingMeeting = meetingRepository.findByRoomIdAndIsActiveTrue(roomId);

        if (existingMeeting.isPresent()) {
            Meeting meeting = existingMeeting.get();

            // Check if meeting has expired
            if (meeting.getExpiresAt() != null && Instant.now().isAfter(meeting.getExpiresAt())) {
                // Meeting expired, create new one
                meeting.setActive(false);
                meetingRepository.save(meeting);
                return createNewMeeting(roomId, playerName);
            }

            // Return existing active meeting
            response.put("success", true);
            response.put("meetingLink", meeting.getMeetingLink());
            response.put("meetingCode", meeting.getMeetingCode());
            response.put("conferenceId", meeting.getConferenceId());
            response.put("roomId", meeting.getRoomId());
            response.put("initiatedBy", meeting.getInitiatedBy());
            response.put("createdAt", meeting.getCreatedAt().toString());
            response.put("participantCount", meeting.getParticipantCount());
            response.put("isNew", false);

            return response;
        }

        // No active meeting, create new one
        return createNewMeeting(roomId, playerName);
    }

    /**
     * Create a new meeting for a room
     */
    private Map<String, Object> createNewMeeting(String roomId, String playerName) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Generate unique meeting link
            String meetingCode = generateMeetingCode(roomId);
            String meetingLink = "https://meet.google.com/" + meetingCode;

            // Create meeting entity
            Meeting meeting = new Meeting(roomId, meetingLink, playerName);
            meeting.setMeetingCode(meetingCode);
            meeting.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS)); // Expires in 24 hours

            // Save to database
            Meeting savedMeeting = meetingRepository.save(meeting);

            response.put("success", true);
            response.put("meetingLink", savedMeeting.getMeetingLink());
            response.put("meetingCode", savedMeeting.getMeetingCode());
            response.put("roomId", savedMeeting.getRoomId());
            response.put("initiatedBy", savedMeeting.getInitiatedBy());
            response.put("createdAt", savedMeeting.getCreatedAt().toString());
            response.put("participantCount", 0);
            response.put("isNew", true);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to create meeting: " + e.getMessage());
        }

        return response;
    }

    /**
     * Generate unique meeting code for a room
     */
    private String generateMeetingCode(String roomId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String code = "ov-" + roomId + "-" + timestamp.substring(timestamp.length() - 6);
        return code.toLowerCase().replaceAll("[^a-z0-9-]", "");
    }

    /**
     * Player joins meeting zone - increment participant count
     */
    @Transactional
    public Map<String, Object> joinMeeting(String roomId, String playerName) {
        Map<String, Object> response = getOrCreateMeeting(roomId, playerName);

        if ((Boolean) response.get("success")) {
            Optional<Meeting> meetingOpt = meetingRepository.findByRoomIdAndIsActiveTrue(roomId);
            meetingOpt.ifPresent(meeting -> {
                meeting.incrementParticipants();
                meetingRepository.save(meeting);
                response.put("participantCount", meeting.getParticipantCount());
            });
        }

        return response;
    }

    /**
     * Player leaves meeting zone - decrement participant count
     */
    @Transactional
    public void leaveMeeting(String roomId, String playerName) {
        Optional<Meeting> meetingOpt = meetingRepository.findByRoomIdAndIsActiveTrue(roomId);
        meetingOpt.ifPresent(meeting -> {
            meeting.decrementParticipants();
            meetingRepository.save(meeting);

            // Optional: Auto-deactivate if no participants
            if (meeting.getParticipantCount() == 0) {
                // You can choose to keep it active or deactivate
                // meeting.setActive(false);
                // meetingRepository.save(meeting);
            }
        });
    }

    /**
     * Get meeting info for a room
     */
    public Map<String, Object> getMeetingInfo(String roomId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Meeting> meetingOpt = meetingRepository.findByRoomIdAndIsActiveTrue(roomId);

        if (meetingOpt.isPresent()) {
            Meeting meeting = meetingOpt.get();
            response.put("exists", true);
            response.put("meetingLink", meeting.getMeetingLink());
            response.put("meetingCode", meeting.getMeetingCode());
            response.put("initiatedBy", meeting.getInitiatedBy());
            response.put("createdAt", meeting.getCreatedAt().toString());
            response.put("participantCount", meeting.getParticipantCount());
            response.put("isActive", meeting.isActive());
        } else {
            response.put("exists", false);
            response.put("message", "No active meeting for this room");
        }

        return response;
    }

    /**
     * Deactivate meeting for a room
     */
    @Transactional
    public Map<String, Object> endMeeting(String roomId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Meeting> meetingOpt = meetingRepository.findByRoomIdAndIsActiveTrue(roomId);

        if (meetingOpt.isPresent()) {
            Meeting meeting = meetingOpt.get();
            meeting.setActive(false);
            meetingRepository.save(meeting);

            response.put("success", true);
            response.put("message", "Meeting ended for room " + roomId);
        } else {
            response.put("success", false);
            response.put("message", "No active meeting found");
        }

        return response;
    }
}