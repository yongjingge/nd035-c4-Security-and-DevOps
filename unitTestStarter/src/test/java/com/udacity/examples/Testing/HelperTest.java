package com.udacity.examples.Testing;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HelperTest {

    @Test
    public void testGetCount () {
        List<String> emp = new ArrayList<>();
        emp.add("test1");
        emp.add("test2");
        assertEquals(2, Helper.getCount(emp));
    }

    @Test
    public void testGetStatus () {
        List<Integer> emp = Arrays.asList(13,4,15,6,17,8,19,1,2,3);
        assertEquals(19, Helper.getStats(emp).getMax());
    }

    @Test
    public void testGetMergedList () {
        List<String> emp = Arrays.asList("test1", "", "test2", "");
        String expected = "test1, test2";
        assertEquals(expected, Helper.getMergedList(emp));
    }
}
