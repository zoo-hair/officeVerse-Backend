package com.offficeVerse.repository;

import com.offficeVerse.model.Player;
import com.offficeVerse.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByName(String name);

    // Find all players in a specific room
    List<Player> findByRoom(Room room);

    // Find all players in a room by room ID
    @Query("SELECT p FROM Player p WHERE p.room.id = :roomId")
    List<Player> findByRoomId(@Param("roomId") Long roomId);

    // Find all online players in a room
    @Query("SELECT p FROM Player p WHERE p.room.id = :roomId AND p.isOnline = true")
    List<Player> findByRoomIdAndIsOnlineTrue(@Param("roomId") Long roomId);

    // Find all online players
    List<Player> findByIsOnlineTrue();

    // Count players in a room
    @Query("SELECT COUNT(p) FROM Player p WHERE p.room.id = :roomId")
    Long countByRoomId(@Param("roomId") Long roomId);

    // Count online players in a room
    @Query("SELECT COUNT(p) FROM Player p WHERE p.room.id = :roomId AND p.isOnline = true")
    Long countByRoomIdAndIsOnlineTrue(@Param("roomId") Long roomId);
}