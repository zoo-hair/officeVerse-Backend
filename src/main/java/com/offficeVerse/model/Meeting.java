package com.offficeVerse.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomId;

    @Column(nullable = false)
    private String meetingLink;

    private String meetingCode;

    private String conferenceId;

    @Column(nullable = false)
    private String initiatedBy;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant expiresAt;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private int participantCount = 0;

    // Constructors
    public Meeting() {
        this.createdAt = Instant.now();
    }

    public Meeting(String roomId, String meetingLink, String initiatedBy) {
        this.roomId = roomId;
        this.meetingLink = meetingLink;
        this.initiatedBy = initiatedBy;
        this.createdAt = Instant.now();
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getMeetingCode() {
        return meetingCode;
    }

    public void setMeetingCode(String meetingCode) {
        this.meetingCode = meetingCode;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public void incrementParticipants() {
        this.participantCount++;
    }

    public void decrementParticipants() {
        if (this.participantCount > 0) {
            this.participantCount--;
        }
    }
}