package net.chilicat.testenv.core.testcollection;

import net.chilicat.testenv.core.TestSuit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
class DefaultTestCollection implements TestCollection {
    private final List<TestSuit> list = new ArrayList<TestSuit>();

    DefaultTestCollection() {

    }

    public void add(TestSuit suit) {
        list.add(suit);
    }

    public List<TestSuit> getTestSuits() {
        return Collections.unmodifiableList(list);
    }
}
