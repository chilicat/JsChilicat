package net.chilicat.testenv.ui;

import net.chilicat.testenv.model.Element;
import net.chilicat.testenv.model.TestCase;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 */
public class Node extends DefaultMutableTreeNode {
    Node(Object userObject) {
        super(userObject);
    }

    @Override
    public boolean isLeaf() {
        return getUserObject() instanceof TestCase;
    }

    public boolean done() {
        return !(getUserObject() instanceof Element) || ((Element) getUserObject()).done();
    }

    public boolean passed() {
        return !(getUserObject() instanceof Element) || ((Element) getUserObject()).passed();
    }

    public boolean running() {
        return !(getUserObject() instanceof Element) || ((Element) getUserObject()).running();
    }
}
