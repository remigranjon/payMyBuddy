package com.paymybuddy.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.backend.model.entity.Connection;
import com.paymybuddy.backend.model.entity.User;
import com.paymybuddy.backend.model.response.ConnectionResponse;
import com.paymybuddy.backend.repository.interfaces.ConnectionRepository;
import com.paymybuddy.backend.repository.interfaces.UserRepository;

import org.springframework.data.domain.Example;

@ExtendWith(MockitoExtension.class)
public class ConnectionServiceTests {
    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private UserRepository userRepository;

    private ConnectionService connectionService;

    @BeforeEach
    void setup() {
        connectionService = new ConnectionService(connectionRepository, userRepository);
    }

    private User createUser1() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        user.setPassword("password");
        user.setCredit(100.0);
        return user;
    }

    private User createUser2() {
        User user = new User();
        user.setId(2L);
        user.setUsername("user2");
        user.setEmail("user2@example.com");
        user.setPassword("password");
        user.setCredit(100.0);
        return user;
    }

    private User createUser3() {
        User user = new User();
        user.setId(3L);
        user.setUsername("user3");
        user.setEmail("user3@example.com");
        user.setPassword("password");
        user.setCredit(100.0);
        return user;
    }

    @Nested
    class AddConnectionTests {
        @Test
        void testAddConnection() {
            when(userRepository.findById(1)).thenReturn(Optional.of(createUser1()));
            when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(createUser2()));
            when(connectionRepository.exists(any())).thenReturn(false);

            when(connectionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
            connectionService.addConnection(1, "user2@example.com");

            verify(connectionRepository).save(argThat(
                    conn -> conn.getUser().equals(createUser1()) && conn.getConnection().equals(createUser2())));
        }
        @Test
        void testAddConnectionAlreadyExists() {
            when(userRepository.findById(1)).thenReturn(Optional.of(createUser1()));
            when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(createUser2()));
            when(connectionRepository.exists(any())).thenReturn(true);
            assertThrows(IllegalArgumentException.class, () -> {
                connectionService.addConnection(1, "user2@example.com");
            });
        }
    }

    @Nested
    class GetConnectionsForUserTests {
        @Test
        void testGetConnectionsForUser() {
            when(userRepository.findById(1)).thenReturn(Optional.of(createUser1()));
            Example<Connection> example = Example.of(Connection.builder().user(createUser1()).build());
            Example<Connection> exampleConn = Example.of(Connection.builder().connection(createUser1()).build());
            when(connectionRepository.findAll(example)).thenReturn(List.of(Connection.builder().user(createUser1()).connection(createUser2()).build()));
            when(connectionRepository.findAll(exampleConn)).thenReturn(List.of(Connection.builder().user(createUser3()).connection(createUser1()).build()));
            Set<ConnectionResponse> expected = Set.of(ConnectionResponse.builder().id(2L).username("user2").email("user2@example.com").build(),
                    ConnectionResponse.builder().id(3L).username("user3").email("user3@example.com").build());
            assertEquals(expected, connectionService.getConnectionsForUser(1));
        }
    }

}
