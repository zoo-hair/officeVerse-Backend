package com.offficeVerse.controller;

import com.offficeVerse.model.Player;
import com.offficeVerse.model.Position;
import com.offficeVerse.service.PlayerService;
import com.offficeVerse.service.PositionService;
import com.offficeVerse.service.RoomService;
import com.offficeVerse.service.genAIService;
import com.offficeVerse.service.DiscordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/game")
@CrossOrigin(origins = "*")
public class GameController {

    private final PlayerService playerService;
    private final RoomService roomService;
    private final PositionService positionService;

    @Autowired
    private DiscordService discordService;

    @Autowired
    private genAIService genAIService;

    public GameController(PlayerService playerService, RoomService roomService, PositionService positionService) {
        this.playerService = playerService;
        this.roomService = roomService;
        this.positionService = positionService;
    }

    @PostMapping("/update-position")
    public Position updatePosition(@RequestParam Long playerId, @RequestParam int x, @RequestParam int y) {
        Player player = playerService.getPlayer(playerId);
        if (player == null) return null;
        Position pos = new Position(x, y, player);
        return positionService.savePosition(pos);
    }

    @GetMapping("/player-position/{playerId}")
    public Position getPosition(@PathVariable Long playerId) {
        return positionService.getPosition(playerId);
    }

    /**
     * Handle room-specific actions based on room type
     */
    @PostMapping("/room-action")
    public ResponseEntity<Map<String, Object>> handleRoomAction(
            @RequestParam Long playerId,
            @RequestParam Long roomId,
            @RequestParam String message) {

        Player player = playerService.getPlayer(playerId);
        if (player == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Player not found"));
        }

        String roomType = roomService.getRoomType(roomId);
        String roomIdStr = String.valueOf(roomId);

        Map<String, Object> response;

        switch(roomType) {
            case "MEETING":
                // Get or create meeting link for this room
                response = discordService.onPlayerEnterMeetingZone(roomIdStr, player.getName());
                response.put("roomType", "MEETING");
                return ResponseEntity.ok(response);

            case "AI_LAB":
                // AI interaction
                String aiResponse = genAIService.getResponse(message);
                response = Map.of(
                        "roomType", "AI_LAB",
                        "response", aiResponse,
                        "success", true
                );
                return ResponseEntity.ok(response);

            default:
                // Normal room - just acknowledge
                response = Map.of(
                        "roomType", roomType,
                        "message", "Message received",
                        "success", true
                );
                return ResponseEntity.ok(response);
        }
    }

    /**
     * Player enters meeting zone - get meeting link
     */
    @PostMapping("/meeting/enter")
    public ResponseEntity<Map<String, Object>> enterMeetingZone(
            @RequestParam Long playerId,
            @RequestParam Long roomId) {

        Player player = playerService.getPlayer(playerId);
        if (player == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Player not found"));
        }

        String roomIdStr = String.valueOf(roomId);
        Map<String, Object> result = discordService.onPlayerEnterMeetingZone(roomIdStr, player.getName());

        return ResponseEntity.ok(result);
    }

    /**
     * Player leaves meeting zone
     */
    @PostMapping("/meeting/leave")
    public ResponseEntity<Map<String, Object>> leaveMeetingZone(
            @RequestParam Long playerId,
            @RequestParam Long roomId) {

        Player player = playerService.getPlayer(playerId);
        if (player == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Player not found"));
        }

        String roomIdStr = String.valueOf(roomId);
        discordService.onPlayerLeaveMeetingZone(roomIdStr, player.getName());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Left meeting zone"
        ));
    }

    /**
     * Get meeting info for a specific room
     */
    @GetMapping("/meeting/{roomId}")
    public ResponseEntity<Map<String, Object>> getMeetingInfo(@PathVariable Long roomId) {
        String roomIdStr = String.valueOf(roomId);
        Map<String, Object> result = discordService.getMeetingForRoom(roomIdStr);
        return ResponseEntity.ok(result);
    }

    /**
     * Get all players in a room
     */
    @GetMapping("/room/{roomId}/players")
    public ResponseEntity<?> getPlayersInRoom(@PathVariable Long roomId) {
        // You can implement this in PlayerService
        return ResponseEntity.ok(Map.of(
                "roomId", roomId,
                "players", playerService.getPlayersInRoom(roomId)
        ));
    }


}
