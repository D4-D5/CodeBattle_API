package com.example.CodeWar.repositories;

import com.example.CodeWar.model.Lobby;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LobbyRepository extends MongoRepository<Lobby,String> {
    Lobby findByRoomId(String roomId);

    boolean exitsByRoomId(String roomId);
}
