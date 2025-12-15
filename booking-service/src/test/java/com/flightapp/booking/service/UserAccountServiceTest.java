package com.flightapp.booking.service;

import com.flightapp.booking.dto.request.UserRegisterRequest;
import com.flightapp.booking.dto.response.UserResponse;
import com.flightapp.booking.entity.UserAccount;
import com.flightapp.booking.exception.ResourceNotFoundException;
import com.flightapp.booking.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountService userAccountService;

    @Captor
    private ArgumentCaptor<UserAccount> userCaptor;

    @BeforeEach
    void setUp() {
        // Mockito annotations initialise mocks
    }

    @Test
    void registerUser_success() {
        // given
        UserRegisterRequest req = new UserRegisterRequest();
        req.setName("Alice");
        req.setEmail("alice@example.com");
        req.setPassword("s3cr3t");

        when(userAccountRepository.existsByEmail("alice@example.com")).thenReturn(false);

        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> {
            UserAccount u = invocation.getArgument(0);
            u.setId(42L); // simulate DB generated id
            return u;
        });

        // when
        UserResponse resp = userAccountService.registerUser(req);

        // then
        verify(userAccountRepository, times(1)).existsByEmail("alice@example.com");
        verify(userAccountRepository, times(1)).save(userCaptor.capture());

        UserAccount saved = userCaptor.getValue();
        assertEquals("Alice", saved.getName());
        assertEquals("alice@example.com", saved.getEmail());
        assertNotNull(saved.getPasswordHash(), "password hash must be set");
        assertEquals(42L, resp.getId());
        assertEquals("Alice", resp.getName());
        assertEquals("alice@example.com", resp.getEmail());
    }

    @Test
    void registerUser_duplicateEmail_throwsIllegalArgument() {
        UserRegisterRequest req = new UserRegisterRequest();
        req.setName("Bob");
        req.setEmail("bob@example.com");
        req.setPassword("password");

        when(userAccountRepository.existsByEmail("bob@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userAccountService.registerUser(req));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(userAccountRepository, times(1)).existsByEmail("bob@example.com");
        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void getUserByEmail_found_returnsResponse() {
        UserAccount user = UserAccount.builder()
                .id(7L)
                .name("Carol")
                .email("carol@example.com")
                .build();

        when(userAccountRepository.findByEmail("carol@example.com")).thenReturn(Optional.of(user));

        UserResponse resp = userAccountService.getUserByEmail("carol@example.com");

        assertEquals(7L, resp.getId());
        assertEquals("Carol", resp.getName());
        assertEquals("carol@example.com", resp.getEmail());
        verify(userAccountRepository, times(1)).findByEmail("carol@example.com");
    }

    @Test
    void getUserByEmail_notFound_throws() {
        when(userAccountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.getUserByEmail("missing@example.com"));

        verify(userAccountRepository, times(1)).findByEmail("missing@example.com");
    }

    @Test
    void getUserById_found_returnsResponse() {
        UserAccount user = UserAccount.builder()
                .id(99L)
                .name("Dave")
                .email("dave@example.com")
                .build();

        when(userAccountRepository.findById(99L)).thenReturn(Optional.of(user));

        UserResponse resp = userAccountService.getUserById(99L);

        assertEquals(99L, resp.getId());
        assertEquals("Dave", resp.getName());
        assertEquals("dave@example.com", resp.getEmail());
        verify(userAccountRepository, times(1)).findById(99L);
    }

    @Test
    void getUserById_notFound_throws() {
        when(userAccountRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userAccountService.getUserById(123L));
        verify(userAccountRepository, times(1)).findById(123L);
    }

    @Test
    void validatePassword_trueAndFalse() throws Exception {
        // same hashing algorithm as service: SHA-256 + Base64
        String raw = "myPassword!";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashed = md.digest(raw.getBytes());
        String base64Hash = Base64.getEncoder().encodeToString(hashed);

        // true case
        assertTrue(userAccountService.validatePassword(raw, base64Hash));

        // false case (different password)
        assertFalse(userAccountService.validatePassword("wrongPassword", base64Hash));
    }
}
