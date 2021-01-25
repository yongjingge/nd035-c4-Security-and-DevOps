package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	// instance of Logger created
	private static final Logger log = LoggerFactory.getLogger(CartController.class);
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("Can not find user by this username: " + username);
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		if (order.getItems().isEmpty() || order.getItems() == null) {
			log.error("Cart is empty, can not submit this order");
			return ResponseEntity.badRequest().build();
		}
		orderRepository.save(order);
		log.info("Order of " + username + " is created successfully");
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("Can not find user by this username: " + username);
			return ResponseEntity.notFound().build();
		}

		if (orderRepository.findByUser(user) == null || ! orderRepository.findByUser(user).isEmpty()) {
			log.info("Orders found by this username: " + username);
			return ResponseEntity.ok(orderRepository.findByUser(user));
		}
		log.error("This user has no history order");
		return ResponseEntity.notFound().build();
	}
}
