package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	// instance of Logger created (org.slf4j.Logger)
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		if (! userRepository.findById(id).isPresent()) {
			log.error("Can not find user by this id: " + id);
			return ResponseEntity.notFound().build();
		}
		log.info("User found by this id: " + id);
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			log.error("Can not find user by this username: " + username);
			return ResponseEntity.notFound().build();
		}
		log.info("User found by this username: " + username);
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();

		if (createUserRequest.getUsername() == null || createUserRequest.getUsername() == "") {
			log.error("Username can not be null or empty");
			return ResponseEntity.badRequest().build();
		}
		log.info("Username set successfully: " + createUserRequest.getUsername());
		user.setUsername(createUserRequest.getUsername());

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		if (createUserRequest.getPassword().length() < 7 ||
				! createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			log.error("Password: " + createUserRequest.getPassword() + " was not set correctly, please follow the rules.");
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		try {
			userRepository.save(user);
			log.info("User " + user.getUsername() + " created successfully!");
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			log.error("Exception throws: " + e.toString());
			return ResponseEntity.badRequest().build();
		}
	}
}
