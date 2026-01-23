package com.offficeVerse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DiscordService {

    @Autowired
    private MeetingService meetingService;

    /**
     * Handle player entering meeting zone
     */
    public Map<String, Object> onPlayerEnterMeetingZone(String roomId, String playerName) {
        return meetingService.joinMeeting(roomId, playerName);
    }

    /**
     * Handle player leaving meeting zone
     */
    public void onPlayerLeaveMeetingZone(String roomId, String playerName) {
        meetingService.leaveMeeting(roomId, playerName);
    }

    /**
     * Get current meeting info for a room
     */
    public Map<String, Object> getMeetingForRoom(String roomId) {
        return meetingService.getMeetingInfo(roomId);
    }

    // Legacy methods (can be removed if not needed)
    public String sendMessageToDiscord(String message) {
        return "Sent to Discord: " + message;
    }

    public String fetchMessages() {
        return "Discord messages";
    }
}