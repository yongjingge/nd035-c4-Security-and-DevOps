package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup () {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testFindById () {
        Item item = getItemForTest();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> findByIdRes = itemController.getItemById(item.getId());
        assertNotNull(findByIdRes);
        assertEquals(200, findByIdRes.getStatusCodeValue());
        Item findByIdItemRes = findByIdRes.getBody();
        assertEquals(item.getName(), findByIdItemRes.getName());
    }

    @Test
    public void testFindByIdAndNotFound () {
        ResponseEntity<Item> itemNotFound = itemController.getItemById(2L);
        assertEquals(404, itemNotFound.getStatusCodeValue());
    }

    @Test
    public void testGetItems () {
        Item item = getItemForTest();
        when(itemRepository.findAll()).thenReturn(Lists.newArrayList(item));

        ResponseEntity<List<Item>> findAllRes = itemController.getItems();
        assertNotNull(findAllRes);
        assertEquals(200, findAllRes.getStatusCodeValue());
        List<Item> findAllResList = findAllRes.getBody();
        assertEquals(item, findAllResList.get(0));
    }

    @Test
    public void testGetItemsByName () {
        Item item = getItemForTest();
        when(itemRepository.findByName(item.getName())).thenReturn(Lists.newArrayList(item));

        ResponseEntity<List<Item>> findByNameRes = itemController.getItemsByName(item.getName());
        assertNotNull(findByNameRes);
        assertEquals(200, findByNameRes.getStatusCodeValue());
        List<Item> findByNameResList = findByNameRes.getBody();
        assertEquals(item, findByNameResList.get(0));
    }

    @Test
    public void testGetItemsByNameAndNotFound () {
        ResponseEntity<List<Item>> itemsNotFound = itemController.getItemsByName("namenotexist");
        assertEquals(404, itemsNotFound.getStatusCodeValue());
    }

    /**
     * Helper Method to get an Item object
     * @return
     */
    private Item getItemForTest () {
        Item item = new Item();
        item.setId(1L);
        item.setName("testitem");
        item.setPrice(BigDecimal.valueOf(10.99));
        item.setDescription("this is a test item");
        return item;
    }
}
