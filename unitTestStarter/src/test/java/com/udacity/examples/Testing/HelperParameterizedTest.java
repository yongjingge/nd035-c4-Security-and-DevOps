package com.udacity.examples.Testing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class HelperParameterizedTest {

    private String input;
    private String output;

    public HelperParameterizedTest(String input, String output) {
        super();
        this.input = input;
        this.output = output;
    }

    @Parameterized.Parameters
    public static Collection initData () {
        String[][] init = {{ "input1", "input1"}, {"input1", "input2"}};
        return Arrays.asList(init);
    }

    @Test
    public void testInputAndOutput () {
        assertEquals(input, output);
    }
}
