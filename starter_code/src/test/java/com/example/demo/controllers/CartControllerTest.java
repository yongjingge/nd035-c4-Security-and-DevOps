package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private CartRepository cartRepository = mock(CartRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup () {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void addToCartHappyPath () {

        User user = getUserForTest();
        Item item = getItemForTest();
        Cart cart = getCartForTest();
        user.setCart(cart);
        cart.setUser(user);
        ModifyCartRequest req = getCartRequest();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.findByUser(user)).thenReturn(cart);
        when(cartRepository.save(cart)).thenReturn(user.getCart());

        ResponseEntity<Cart> addToCartRes = cartController.addTocart(req);
        assertNotNull(addToCartRes);
        assertEquals(200, addToCartRes.getStatusCodeValue());
        Cart cartRes = addToCartRes.getBody();
        assertNotNull(cartRes.getUser());
        assertEquals(cartRes.getUser(), user);
        assertEquals(cartRes.getItems().get(0), item);
        assertEquals(BigDecimal.valueOf(11 * 10.99), cartRes.getTotal());
    }

    @Test
    public void removeFromCartHappyPath () {

        User user = getUserForTest();
        Item item = getItemForTest();
        Cart cart = getCartForTest();
        user.setCart(cart);
        cart.setUser(user);
        ModifyCartRequest req = removeCartRequest();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.findByUser(user)).thenReturn(cart);
        when(cartRepository.save(cart)).thenReturn(user.getCart());

        ResponseEntity<Cart> removeFromCartRes = cartController.removeFromcart(req);
        assertNotNull(removeFromCartRes);
        assertEquals(200, removeFromCartRes.getStatusCodeValue());
        Cart cartRes = removeFromCartRes.getBody();
        assertNotNull(cartRes.getUser());
        assertTrue(cartRes.getItems().isEmpty());
    }

    @Test
    public void addToCartNotHappy () {
        ResponseEntity<Cart> addToCartRes = cartController.addTocart(new ModifyCartRequest("usernotfound", 1L, 10));
        assertEquals(404, addToCartRes.getStatusCodeValue());
    }

    private static ModifyCartRequest getCartRequest () {
        ModifyCartRequest req = new ModifyCartRequest();
        req.setItemId(1L);
        req.setUsername(getUserForTest().getUsername());
        req.setQuantity(10);
        return req;
    }

    private static ModifyCartRequest removeCartRequest () {
        ModifyCartRequest req = new ModifyCartRequest();
        req.setItemId(1L);
        req.setUsername(getUserForTest().getUsername());
        req.setQuantity(1);
        return req;
    }

    private static User getUserForTest () {
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password1");
        return testUser;
    }

    private static Cart getCartForTest () {
        Cart testCart = new Cart();
        testCart.setItems(Lists.newArrayList(getItemForTest()));
        testCart.setTotal(getItemForTest().getPrice());
        return testCart;
    }

    private static Item getItemForTest () {
        Item testItem = new Item();
        testItem.setId(1L);
        testItem.setName("testitem");
        testItem.setDescription("this is a test item");
        testItem.setPrice(BigDecimal.valueOf(10.99));
        return testItem;
    }

}
