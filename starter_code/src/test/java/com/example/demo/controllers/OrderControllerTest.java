package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup () {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void testSubmitHappyPath () {
        User user = getUserForTest();
        Item item = getItemForTest();
        Cart cart = getCartForTest();
        user.setCart(cart);
        cart.setUser(user);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ResponseEntity<UserOrder> orderRes = orderController.submit(user.getUsername());
        assertNotNull(orderRes);
        assertEquals(200, orderRes.getStatusCodeValue());
        UserOrder orderFromRes = orderRes.getBody();
        assertNotNull(orderFromRes.getUser());
        assertEquals(item, orderFromRes.getItems().get(0));
        assertEquals(user, orderFromRes.getUser());
    }

    @Test
    public void testSubmitAndNotFound () {
        ResponseEntity<UserOrder> res = orderController.submit("namenotexist");
        assertEquals(404, res.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersForUserHappyPath () {
        User user = getUserForTest();
        Item item = getItemForTest();
        Cart cart = getCartForTest();
        user.setCart(cart);
        cart.setUser(user);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Lists.newArrayList(UserOrder.createFromCart(cart)));

        ResponseEntity<List<UserOrder>> orderListFindByUserRes = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(orderListFindByUserRes);
        assertEquals(200, orderListFindByUserRes.getStatusCodeValue());
        assertEquals(item, orderListFindByUserRes.getBody().get(0).getItems().get(0));
    }

    @Test
    public void testGetOrdersForUserNotFound () {
        ResponseEntity<List<UserOrder>> res = orderController.getOrdersForUser("namenotexist");
        assertEquals(404, res.getStatusCodeValue());
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
