package net.chilicat.testenv.idea.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.tree.TreeModelAdapter;
import net.chilicat.testenv.model.Element;
import net.chilicat.testenv.model.LogEntry;
import net.chilicat.testenv.model.TestReportModel;
import net.chilicat.testenv.ui.Node;
import net.chilicat.testenv.ui.ScriptNode;
import net.chilicat.testenv.ui.TestReportTreeModel;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 */
public class TestTreeView extends JTree implements Disposable {
    private final Icon ok = IconLoader.getIcon("/runConfigurations/testPassed.png");
    private final Icon failed = IconLoader.getIcon("/runConfigurations/testError.png");
    private final Icon running = IconLoader.getIcon("/runConfigurations/testInProgress1.png");
    private final TestsProgressAnimator animator;

    Icon wait = new Icon() {
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Composite comp = ((Graphics2D) g).getComposite();
            ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            running.paintIcon(c, g, x, y);
            ((Graphics2D) g).setComposite(comp);
        }

        public int getIconWidth() {
            return running.getIconWidth();  //To change body of implemented methods use File | Settings | File Templates.
        }

        public int getIconHeight() {
            return running.getIconHeight();  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    public TestTreeView(TestReportModel model) {
        super(new TestReportTreeModel(model));
        //setRootVisible(false);
        setCellRenderer(new Renderer());

        animator = new TestsProgressAnimator(this);
        getModel().setTracker(animator);

        getModel().addTreeModelListener(new TreeModelAdapter() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                for (Object obj : e.getChildren()) {
                    if (obj instanceof Node) {
                        final Node node = (Node) obj;
                        if (node.done() == !node.passed()) {
                            TreePath path = new TreePath(getModel().toPath(node));
                            expandPath(path);
                        }
                    }
                }

            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                /*for (int i = 0; i < getRowCount(); i++) {
                    expandRow(i);
                } */
            }
        });
    }

    @Override
    public TestReportTreeModel getModel() {
        return (TestReportTreeModel) super.getModel();
    }

    public void dispose() {
        animator.currentTest(null);
    }

    public Collection<String> logs(TreePath selectionPath) {
        Node node = ((Node) selectionPath.getLastPathComponent());
        if (node instanceof ScriptNode) {
            final Collection<String> logList = new ArrayList<String>();
            for (int i = 0; i < node.getChildCount(); i++) {
                Node child = (Node) node.getChildAt(i);
                logList.addAll(enumLogs(child));
            }
            return logList;
        } else if (node.getUserObject() instanceof Element) {
            return enumLogs(node);
        }
        return toStringList(getModel().getModel().getLogEntries());
    }

    private Collection<String> enumLogs(Node node) {
        if (node.getUserObject() instanceof Element) {
            Element el = (Element) node.getUserObject();
            java.util.List<LogEntry> entries = getModel().getModel().getLogEntries(el);
            return toStringList(entries);
        }
        return Collections.emptyList();
    }

    private Collection<String> toStringList(java.util.List<LogEntry> entries) {
        Collection<String> col = new ArrayList<String>();
        for (LogEntry l : entries) {
            col.add(l.toString());
        }
        return col;
    }

    public void repaintWithParents(Node currentTestCase) {
        /*if (currentTestCase != null) {
            Object path = ((TestTreeModel) getModel()).toPath(currentTestCase);
            TreePath tp = new TreePath(path);
            repaint(getPathBounds(tp));
        } */
        repaint();
    }

    public void stop() {
        animator.currentTest(null);
    }

    public boolean isScriptNodeSelected() {
        if (getSelectionCount() > 0) {
            return (getSelectionPath().getLastPathComponent() instanceof ScriptNode);
        }
        return false;
    }

    public String getSelectedScript() {
        if (isScriptNodeSelected()) {
            return ((ScriptNode) getSelectionPath().getLastPathComponent()).getScript();
        }
        return null;
    }

    private class Renderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);    //To change body of overridden methods use File | Settings | File Templates.

            if (value != null && value instanceof Node) {
                Node node = ((Node) value);
                if (node.done()) {
                    if (node.passed()) {
                        setIcon(ok);
                    } else {
                        setIcon(failed);
                    }
                } else if (node.running()) {
                    setIcon(TestsProgressAnimator.getCurrentFrame());
                } else {
                    setIcon(wait);
                }
            }

            return this;
        }
    }
}
