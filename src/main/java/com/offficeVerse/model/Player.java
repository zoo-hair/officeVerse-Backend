package com.offficeVerse.model;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    private String avatar;

    @Column(name = "is_online")
    private boolean isOnline = true;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "last_active")
    private Long lastActive;

    // Constructors
    public Player() {
        this.createdAt = System.currentTimeMillis();
        this.lastActive = System.currentTimeMillis();
    }

    public Player(String name) {
        this.name = name;
        this.createdAt = System.currentTimeMillis();
        this.lastActive = System.currentTimeMillis();
    }

    public Player(String name, Room room) {
        this.name = name;
        this.room = room;
        this.createdAt = System.currentTimeMillis();
        this.lastActive = System.currentTimeMillis();
    }

    public Player(String name, String avatar, Room room) {
        this.name = name;
        this.avatar = avatar;
        this.room = room;
        this.createdAt = System.currentTimeMillis();
        this.lastActive = System.currentTimeMillis();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Helper method to get room ID directly
     */
    public Long getRoomId() {
        return room != null ? room.getId() : null;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getLastActive() {
        return lastActive;
    }

    public void setLastActive(Long lastActive) {
        this.lastActive = lastActive;
    }

    public void updateLastActive() {
        this.lastActive = System.currentTimeMillis();
    }
}