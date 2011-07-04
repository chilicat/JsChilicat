package net.chilicat.testenv.idea.ui;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import net.chilicat.testenv.idea.TestEnvConfiguration;
import net.chilicat.testenv.model.DefaultTestReportModel;
import net.chilicat.testenv.model.TestReportModel;
import net.chilicat.testenv.utils.RemoteMessageParser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TestConsoleView implements ConsoleView {
    private final ConsoleView console;
    private TestTreeView view;
    private JComponent container;
    private RemoteMessageParser parser;

    public TestConsoleView(final @NotNull TestEnvConfiguration config) {
        TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(config.getProject());
        console = consoleBuilder.getConsole();

        TestReportModel model = new DefaultTestReportModel();
        parser = new RemoteMessageParser(model.getMessageBus());

        view = new TestTreeView(model);

        EventHandler eventHandler = new EventHandler(config);
        view.addTreeWillExpandListener(eventHandler);
        view.addMouseListener(eventHandler);

        //view.getMouseListeners()
        JScrollPane scroller = new JScrollPane(view);
        final JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroller, console.getComponent());
        splitter.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitter.setDividerLocation(0.5);
                splitter.removeComponentListener(this);
            }
        });

        this.container = splitter;

        view.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                if (view.getSelectionCount() == 1) {
                    console.clear();
                    for (String str : view.logs(view.getSelectionPath())) {
                        console.print(str, ConsoleViewContentType.NORMAL_OUTPUT);
                        console.print("\n", ConsoleViewContentType.NORMAL_OUTPUT);
                    }
                }
            }
        });
    }

    public void print(String s, ConsoleViewContentType consoleViewContentType) {

    }

    public void clear() {
    }

    public void scrollTo(int i) {

    }

    public void attachToProcess(ProcessHandler processHandler) {
        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(ProcessEvent event) {
                view.stop();
            }

            @Override
            public void onTextAvailable(ProcessEvent event, Key outputType) {
                try {
                    console.print(event.getText(), ConsoleViewContentType.NORMAL_OUTPUT);
                    parser.parse(event.getText());
                } catch(RuntimeException e) {
                    throw new RuntimeException("Cannot parse: " + event.getText(), e);
                }
            }
        });
    }

    public void setOutputPaused(boolean b) {

    }

    public boolean isOutputPaused() {
        return false;
    }

    public boolean hasDeferredOutput() {
        return false;
    }

    public void performWhenNoDeferredOutput(Runnable runnable) {

    }

    public void setHelpId(String s) {

    }

    public void addMessageFilter(Filter filter) {

    }

    public void printHyperlink(String s, HyperlinkInfo hyperlinkInfo) {

    }

    public int getContentSize() {
        return console.getContentSize();
    }

    public boolean canPause() {
        return false;
    }

    @NotNull
    public AnAction[] createConsoleActions() {
        return new AnAction[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public JComponent getComponent() {
        return container;
    }

    public JComponent getPreferredFocusableComponent() {
        return view;
    }

    public void dispose() {
        console.dispose();
        view.dispose();
    }

    private class EventHandler extends MouseAdapter implements TreeWillExpandListener {
        private final TestEnvConfiguration config;

        public EventHandler(TestEnvConfiguration config) {
            this.config = config;
        }

        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        }

        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && view.isScriptNodeSelected()) {
                String script = view.getSelectedScript();
                final FileEditorManager editorMgr = FileEditorManager.getInstance(config.getProject());

                final String url = VirtualFileManager.constructUrl("file", script);
                final VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);

                if (file != null) {
                    e.consume();
                    editorMgr.openTextEditor(new OpenFileDescriptor(config.getProject(), file), true);
                }
            }
        }
    }
}
