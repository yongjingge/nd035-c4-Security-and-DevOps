package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    // create a mock object of UserRepository
    private UserRepository userRepository = mock(UserRepository.class);

    // create a mock object of CartRepository
    private CartRepository cartRepository = mock(CartRepository.class);

    // create a mock object of BCryptPasswordEncoder
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup () {
        userController = new UserController();
        // fieldName will be exactly the same with the fields names from the UserController class
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void create_user_happy_path () {
        when(bCryptPasswordEncoder.encode("password1")).thenReturn("thisishashed");

        CreateUserRequest userRequest = getSampleUserRequest();
        ResponseEntity<User> res = userController.createUser(userRequest);
        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        User user = res.getBody();
        assertEquals(0, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("thisishashed", user.getPassword());
    }

    @Test
    public void testFindById () {
        when(bCryptPasswordEncoder.encode("password1")).thenReturn("thisishashed");

        CreateUserRequest userRequest = getSampleUserRequest();
        ResponseEntity<User> res = userController.createUser(userRequest);
        User user = res.getBody();
        assertNotNull(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        ResponseEntity<User> findByIdRes = userController.findById(user.getId());
        assertNotNull(findByIdRes);
        assertEquals(200, findByIdRes.getStatusCodeValue());
        User findByIdUserRes = findByIdRes.getBody();
        assertEquals(user.getUsername(), findByIdUserRes.getUsername());
    }

    @Test
    public void testFindByIdAndNotFound () {
        ResponseEntity<User> userNotFound = userController.findById(2L);
        assertEquals(404, userNotFound.getStatusCodeValue());
    }

    @Test
    public void testFindByUserName () {
        when(bCryptPasswordEncoder.encode("password1")).thenReturn("thisishashed");

        CreateUserRequest userRequest = getSampleUserRequest();
        ResponseEntity<User> res = userController.createUser(userRequest);
        User user = res.getBody();
        assertNotNull(user);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ResponseEntity<User> findByUsernameRes = userController.findByUserName(user.getUsername());
        assertNotNull(findByUsernameRes);
        assertEquals(200, findByUsernameRes.getStatusCodeValue());
        User findByUsernameUserRes = findByUsernameRes.getBody();
        assertEquals(user.getUsername(), findByUsernameUserRes.getUsername());
    }

    @Test
    public void testFindByUserNameAndNotFound () {
        ResponseEntity<User> userNotFound = userController.findByUserName("Tom");
        assertEquals(404, userNotFound.getStatusCodeValue());
    }

    /**
     * Helper Method to get a CreateUserRequest object
     * @return
     */
    private static CreateUserRequest getSampleUserRequest () {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("testuser");
        userRequest.setPassword("password1");
        userRequest.setConfirmPassword("password1");
        return userRequest;
    }
}
