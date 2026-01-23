package com.offficeVerse.service;

import com.offficeVerse.model.Player;
import com.offficeVerse.model.Room;
import com.offficeVerse.repository.PlayerRepository;
import com.offficeVerse.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RoomRepository roomRepository;

    /**
     * Get player by ID
     */
    public Player getPlayer(Long playerId) {
        return playerRepository.findById(playerId).orElse(null);
    }

    /**
     * Get player by name
     */
    public Player getPlayerByName(String name) {
        return playerRepository.findByName(name).orElse(null);
    }

    /**
     * Create new player
     */
    @Transactional
    public Player createPlayer(String name, String avatar) {
        Player player = new Player(name);
        player.setAvatar(avatar);
        return playerRepository.save(player);
    }

    /**
     * Create new player in a specific room
     */
    @Transactional
    public Player createPlayer(String name, String avatar, Long roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        Player player = new Player(name, avatar, room);
        return playerRepository.save(player);
    }

    /**
     * Update player's room
     */
    @Transactional
    public Player updatePlayerRoom(Long playerId, Long roomId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            Room room = roomRepository.findById(roomId).orElse(null);
            player.setRoom(room);
            player.updateLastActive();
            return playerRepository.save(player);
        }
        return null;
    }

    /**
     * Get all players in a specific room
     */
    public List<Player> getPlayersInRoom(Long roomId) {
        return playerRepository.findByRoomId(roomId);
    }

    /**
     * Get all ONLINE players in a specific room
     */
    public List<Player> getOnlinePlayersInRoom(Long roomId) {
        return playerRepository.findByRoomIdAndIsOnlineTrue(roomId);
    }

    /**
     * Get detailed player info for all players in a room
     */
    public List<Map<String, Object>> getPlayersInRoomDetailed(Long roomId) {
        List<Player> players = getOnlinePlayersInRoom(roomId);

        return players.stream().map(player -> {
            Map<String, Object> playerInfo = new HashMap<>();
            playerInfo.put("id", player.getId());
            playerInfo.put("name", player.getName());
            playerInfo.put("avatar", player.getAvatar());
            playerInfo.put("isOnline", player.isOnline());
            playerInfo.put("lastActive", player.getLastActive());
            playerInfo.put("roomId", player.getRoomId());
            playerInfo.put("roomName", player.getRoom() != null ? player.getRoom().getName() : null);
            return playerInfo;
        }).collect(Collectors.toList());
    }

    /**
     * Count players in a room
     */
    public Long countPlayersInRoom(Long roomId) {
        return playerRepository.countByRoomId(roomId);
    }

    /**
     * Count online players in a room
     */
    public Long countOnlinePlayersInRoom(Long roomId) {
        return playerRepository.countByRoomIdAndIsOnlineTrue(roomId);
    }

    /**
     * Set player online status
     */
    @Transactional
    public Player setPlayerOnline(Long playerId, boolean isOnline) {
        Player player = getPlayer(playerId);
        if (player != null) {
            player.setOnline(isOnline);
            player.updateLastActive();
            return playerRepository.save(player);
        }
        return null;
    }

    /**
     * Player joins a room
     */
    @Transactional
    public Player joinRoom(Long playerId, Long roomId) {
        Player player = getPlayer(playerId);
        Room room = roomRepository.findById(roomId).orElse(null);

        if (player != null && room != null) {
            player.setRoom(room);
            player.setOnline(true);
            player.updateLastActive();
            return playerRepository.save(player);
        }
        return null;
    }

    /**
     * Player leaves a room
     */
    @Transactional
    public Player leaveRoom(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            player.setRoom(null);
            player.updateLastActive();
            return playerRepository.save(player);
        }
        return null;
    }

    /**
     * Get all online players across all rooms
     */
    public List<Player> getAllOnlinePlayers() {
        return playerRepository.findByIsOnlineTrue();
    }

    /**
     * Update player's last active timestamp
     */
    @Transactional
    public void updatePlayerActivity(Long playerId) {
        Player player = getPlayer(playerId);
        if (player != null) {
            player.updateLastActive();
            playerRepository.save(player);
        }
    }

    /**
     * Delete player
     */
    @Transactional
    public void deletePlayer(Long playerId) {
        playerRepository.deleteById(playerId);
    }

    /**
     * Save player
     */
    @Transactional
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }
}