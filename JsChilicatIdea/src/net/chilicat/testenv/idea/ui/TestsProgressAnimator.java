package net.chilicat.testenv.idea.ui;


import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.Alarm;
import net.chilicat.testenv.ui.Node;
import net.chilicat.testenv.ui.TestReportTreeModel;

import javax.swing.*;

public class TestsProgressAnimator implements Runnable, Disposable, TestReportTreeModel.TestCaseTracker {
    private static final int FRAMES_COUNT = 8;
    private static final int MOVIE_TIME = 800;
    private static final int FRAME_TIME = MOVIE_TIME / FRAMES_COUNT;

    public static final Icon[] FRAMES = new Icon[FRAMES_COUNT];

    private long myLastInvocationTime = -1;
    private Alarm myAlarm;
    private Node currentTestCase;
    private TestTreeView view;

    protected TestsProgressAnimator(TestTreeView view) {
        Disposer.register(view, this);
        this.view = view;
        myAlarm = new Alarm();
    }

    static {
        for (int i = 0; i < FRAMES_COUNT; i++)
            FRAMES[i] = IconLoader.getIcon("/runConfigurations/testInProgress" + (i + 1) + ".png");
    }

    public static int getCurrentFrameIndex() {
        return (int) ((System.currentTimeMillis() % MOVIE_TIME) / FRAME_TIME);
    }

    public static Icon getCurrentFrame() {
        return FRAMES[getCurrentFrameIndex()];
    }


    public void run() {
        if (getCurrentTestCase() != null) {
            final long time = System.currentTimeMillis();
            // optimization:
            // we shouldn't repaint if this frame was painted in current interval
            if (time - myLastInvocationTime >= FRAME_TIME) {
                repaintSubTree();
                myLastInvocationTime = time;
            }
        }
        scheduleRepaint();
    }

    public Node getCurrentTestCase() {
        return currentTestCase;
    }

    public void currentTest(Node node) {
        currentTestCase = node;
        if (node == null) {
            cancelAlarm();
            repaintSubTree();
        } else {
            scheduleRepaint();
        }
    }

    public void stopMovie() {
        if (currentTestCase != null)
            repaintSubTree();
        currentTest(null);
        cancelAlarm();
    }


    public void dispose() {
        currentTestCase = null;
        cancelAlarm();
    }

    private void cancelAlarm() {
        if (myAlarm != null) {
            myAlarm.cancelAllRequests();
            myAlarm = null;
        }
    }

    private void repaintSubTree() {
        view.repaintWithParents(getCurrentTestCase());
    }

    private void scheduleRepaint() {
        if (myAlarm == null) {
            return;
        }
        myAlarm.cancelAllRequests();
        if (getCurrentTestCase() != null) {
            myAlarm.addRequest(this, FRAME_TIME);
        }
    }

}
