package net.chilicat.testenv.ui;

import net.chilicat.testenv.model.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.util.*;

/**
 */
public class TestReportTreeModel extends DefaultTreeModel {
    private final TestReportModel model;
    private final Map<Object, Node> elementToNodeMap = new HashMap<Object, Node>();
    private TestCaseTracker tracker;

    public TestReportTreeModel(TestReportModel model) {
        super(new Node("TestCases"));
        if (model == null) {
            throw new NullPointerException("model");
        }

        this.model = model;
        model.addTestReportModelListener(new Handler());
    }

    public TestCaseTracker getTracker() {
        return tracker;
    }

    public Object[] toPath(Node n) {
        if (n == null) {
            throw new NullPointerException("n");
        }

        List<Node> nodes = new ArrayList<Node>();
        Node parent = n;
        do {
            nodes.add(parent);
            parent = (Node) parent.getParent();
        } while (parent != null);

        Collections.reverse(nodes);
        return nodes.toArray();
    }


    public void setTracker(TestCaseTracker tracker) {
        this.tracker = tracker;
    }

    public TestReportModel getModel() {
        return model;
    }

    public Node getParentNodeFor(Element element) {
        if (element instanceof Module) {
            String scriptFull = ((Module) element).getFullScriptName();
            return elementToNodeMap.get(scriptFull);
        }
        if (element.getParent() == null) {
            return (Node) getRoot();
        }
        return elementToNodeMap.get(element.getParent());
    }


    private class Handler implements TestReportModelListener {
        public void started(TestReportEvent event) {
            started(event.getElement());
        }

        public void ended(TestReportEvent event) {
            ended(event.getElement());
        }

        public void added(TestReportEvent event) {
            if (!(event.getElement() instanceof LogEntry)) {
                added(event.getElement());
            }
        }


        private Node getParent(Element element) {
            Node parent = getParentNodeFor(element);

            if (parent == null && element instanceof Module) {
                String scriptFull = ((Module) element).getFullScriptName();
                String scriptName = ((Module) element).getScriptName();
                parent = new ScriptNode(scriptName, scriptFull);
                Node root = (Node) getRoot();
                insert(root, scriptFull, parent);
            }

            return parent;
        }

        public void added(final Element child) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (child instanceof TestCase) {
                        System.out.printf("");
                    }
                    Node parent = getParent(child);

                    if (parent != null) {
                        Node childNode = new Node(child);
                        insert(parent, child, childNode);
                    }
                }
            });
        }

        private void insert(Node parent, Object key, Node childNode) {
            parent.add(childNode);
            Object[] path = toPath(parent);
            elementToNodeMap.put(key, childNode);
            fireTreeNodesInserted(TestReportTreeModel.this, path,
                    new int[]{parent.getChildCount() - 1}, new Object[]{childNode});
        }

        public void ended(Element test) {
            fireUpdateInUI(test, false);
        }

        public void started(final Element test) {
            fireUpdateInUI(test, true);
        }

        private void fireUpdateInUI(final Element test, final boolean started) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Node parentNode = getParentNodeFor(test);
                    Node child = elementToNodeMap.get(test);

                    assert parentNode != null : "test must exists: " + test.attributeAt(1);
                    Object[] path = toPath(parentNode);
                    int index = indexOf(child);
                    fireTreeNodesChanged(TestReportTreeModel.this, path, new int[]{index}, new Object[]{child});

                    tracker.currentTest(null);
                    if (started) {
                        tracker.currentTest(parentNode);
                    }
                }
            });
        }


        private int indexOf(Node n) {
            if (n == null) {
                throw new NullPointerException("n");
            }

            int index = -1;
            for (int i = 0; i < n.getParent().getChildCount(); i++) {
                if (n.getParent().getChildAt(i) == n) {
                    index = i;
                    break;
                }
            }
            return index;
        }


    }

    public static interface TestCaseTracker {
        public void currentTest(Node node);
    }

}
