package com.paymybuddy.backend.service;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.paymybuddy.backend.model.entity.Connection;
import com.paymybuddy.backend.model.entity.User;
import com.paymybuddy.backend.model.response.ConnectionResponse;
import com.paymybuddy.backend.repository.interfaces.ConnectionRepository;
import com.paymybuddy.backend.repository.interfaces.UserRepository;

@Service
public class ConnectionService {
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    public ConnectionService(ConnectionRepository connectionRepository, UserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    public void addConnection(int userId, String connectionEmail) {
        User user = userRepository.findById(userId).orElseThrow();
        User connectionUser = userRepository.findByEmail(connectionEmail).orElseThrow();
        Example<Connection> example = Example.of(Connection.builder().user(user).connection(connectionUser).build());
        if (connectionRepository.exists(example)) {
            throw new IllegalArgumentException("Connection already exists");
        }
        Connection connection = new Connection();
        connection.setUser(user);
        connection.setConnection(connectionUser);
        connectionRepository.save(connection);
    }

    public Set<ConnectionResponse> getConnectionsForUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Example<Connection> example = Example.of(Connection.builder().user(user).build());
        Example<Connection> exampleConn = Example.of(Connection.builder().connection(user).build());
        List<Connection> connections = connectionRepository.findAll(example);
        List<Connection> connectionsConn = connectionRepository.findAll(exampleConn);
        Set<ConnectionResponse> responseSet = connections.stream()
                .map(conn -> ConnectionResponse.builder()
                        .id(conn.getConnection().getId())
                        .username(conn.getConnection().getUsername())
                        .email(conn.getConnection().getEmail())
                        .build())
                .collect(Collectors.toSet());
        responseSet.addAll(connectionsConn.stream()
                .map(conn -> ConnectionResponse.builder()
                        .id(conn.getUser().getId())
                        .username(conn.getUser().getUsername())
                        .email(conn.getUser().getEmail())
                        .build())
                .collect(Collectors.toSet()));
        return responseSet;
    }
}
