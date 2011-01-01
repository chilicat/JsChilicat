package net.chilicat.testenv.idea.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.vfs.VirtualFile;
import net.chilicat.testenv.ExecutorType;
import net.chilicat.testenv.idea.TestEnvConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;


public class RunProfileSettingsPane extends SettingsEditor<TestEnvConfiguration> {
    private JTextField sourceTxt;
    private JTextField testTxt;
    private JButton sourceButton;
    private JButton testsButton;
    private JPanel root;
    private JList libraries;
    private JButton moveDownButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton moveUpButton;
    private JPanel browserTypePanel;
    private JTextField workingDirTxt;
    private JButton workDirButton;
    private JCheckBox codeCoverageReportCheckBox;
    private JCheckBox serverCheckBox;
    private JTextField serverFileTxt;
    private JButton serverButton;

    private final DefaultListModel libraryModel = new DefaultListModel();
    private final TestEnvConfiguration config;

    private ExecutorType browserType;
    private final ButtonGroup browserTypeGroup;

    public RunProfileSettingsPane(final @NotNull TestEnvConfiguration config) {
        this.config = config;

        libraries.setModel(libraryModel);
        libraries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        libraries.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateButtons();
            }
        });

        serverButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectAndSetFile(serverFileTxt, serverButton, true, false);
            }
        });

        sourceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectAndSetFile(sourceTxt, sourceButton, true, true);
            }
        });
        testsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectAndSetFile(testTxt, testsButton, true, true);
            }
        });
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                FileChooserDescriptor desc = new FileChooserDescriptor(true, false, false, false, false, true);

                FileChooserDialog dialog = FileChooserFactory.getInstance().createFileChooser(desc, config.getProject());
                VirtualFile baseDir = config.getProject().getBaseDir();
                VirtualFile files[] = dialog.choose(baseDir, config.getProject());

                if (files.length > 0) {
                    for (VirtualFile file : files) {
                        String selected = file.getPath();

                        String base = baseDir.getPath();
                        if (selected.startsWith(base)) {
                            selected = "." + selected.substring(base.length());
                        }

                        libraryModel.addElement(selected);
                    }
                }
            }
        });
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] i = libraries.getSelectedIndices();
                if (i != null && i.length > 0) {
                    for (int ib = i.length - 1; ib >= 0; ib--) {
                        libraryModel.remove(i[ib]);
                    }
                }
            }
        });
        moveUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = libraries.getSelectionModel().getMinSelectionIndex();
                if (index > 0) {
                    Object obj = libraryModel.remove(index);
                    libraryModel.add(--index, obj);
                    libraries.setSelectedIndex(index);
                }
            }
        });

        moveDownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = libraries.getSelectionModel().getMinSelectionIndex();
                if (index < libraryModel.getSize() - 1) {
                    Object obj = libraryModel.remove(index);
                    libraryModel.add(++index, obj);
                    libraries.setSelectedIndex(index);
                }
            }
        });

        browserTypeGroup = new ButtonGroup();

        for (final ExecutorType fw : ExecutorType.values()) {
            if (!fw.isHidden()) {
                String buttonName = fw.getDisplayName();

                JRadioButton b = new JRadioButton(buttonName);
                b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        RunProfileSettingsPane.this.browserType = fw;
                    }
                });
                b.putClientProperty("ExecutorType", fw);
                browserTypePanel.add(b);
                browserTypeGroup.add(b);

                if (config.getExecutionType() == null && browserTypeGroup.getSelection() == null) {
                    b.setSelected(true);
                    RunProfileSettingsPane.this.browserType = fw;
                }
            }
        }
        //browserTypePanel
        workDirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectAndSetFile(workingDirTxt, workDirButton, false, true);
            }
        });

        browserType = config.getExecutionType();

        serverCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateServerSection();
            }
        });

        // updateServerSection();
    }

    private void updateServerSection() {
        serverFileTxt.setEnabled(serverCheckBox.isSelected());
        serverButton.setEnabled(serverCheckBox.isSelected());
    }

    private void updateButtons() {
        int[] i = libraries.getSelectedIndices();
        boolean selected = (i != null && i.length > 0);
        removeButton.setEnabled(selected);
        moveDownButton.setEnabled(selected && i.length == 1);
        moveUpButton.setEnabled(selected && i.length == 1);
    }


    private void selectAndSetFile(JTextField field, JButton button, boolean selectFiles, boolean selectFolders) {

        FileChooserDescriptor desc = new FileChooserDescriptor(selectFiles, selectFolders, false, false, false, false);

        FileChooserDialog dialog = FileChooserFactory.getInstance().createFileChooser(desc, config.getProject());

        VirtualFile baseDir = config.getProject().getBaseDir();

        /*
        if(field.getText().length() > 0) {
            VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(field.getText());
            if(file != null && file.exists()) {
                baseDir = file;
            }

        }
        */

        VirtualFile files[] = dialog.choose(baseDir, config.getProject());

        if (files.length == 1) {
            String selected = files[0].getPath();
            String base = config.getProject().getBaseDir().getPath();
            if (selected.startsWith(base)) {
                selected = "." + selected.substring(base.length());
            }
            field.setText(selected);
        }
    }

    @Override
    protected void resetEditorFrom(TestEnvConfiguration testEnvConfiguration) {
        sourceTxt.setText(testEnvConfiguration.getSrcDir(false));
        testTxt.setText(testEnvConfiguration.getTestDir(false));
        workingDirTxt.setText(testEnvConfiguration.getWorkingDirectory(false));

        libraryModel.removeAllElements();
        if (testEnvConfiguration.getLibraries() != null) {
            for (String a : testEnvConfiguration.getLibraries().split("\\|")) {
                if (a.length() > 0) {
                    libraryModel.addElement(a);
                }
            }
        }

        Enumeration<AbstractButton> e = browserTypeGroup.getElements();
        while (e.hasMoreElements()) {
            AbstractButton b = e.nextElement();
            if (b.getClientProperty("ExecutorType") == testEnvConfiguration.getExecutionType()) {
                browserTypeGroup.setSelected(b.getModel(), true);
            }
        }

        browserType = testEnvConfiguration.getExecutionType();
        codeCoverageReportCheckBox.setSelected(testEnvConfiguration.isCoverageSelected());

        serverFileTxt.setText(testEnvConfiguration.getServerFile(false));
        serverCheckBox.setSelected(testEnvConfiguration.isServerIsEnabled());

        updateServerSection();
    }

    @Override
    protected void applyEditorTo(TestEnvConfiguration testEnvConfiguration) throws ConfigurationException {
        testEnvConfiguration.setConfiguration(sourceTxt.getText(), testTxt.getText(), getLibraries(), testEnvConfiguration.getFramework(), browserType, codeCoverageReportCheckBox.isSelected(), workingDirTxt.getText());
        testEnvConfiguration.setServerConfig(serverCheckBox.isSelected(), serverFileTxt.getText());
        updateServerSection();
    }

    public String getLibraries() {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < libraryModel.size(); i++) {
            if (b.length() > 0) {
                b.append("|");
            }
            b.append(libraryModel.get(i));
        }
        return b.toString();
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return root;
    }

    @Override
    protected void disposeEditor() {

    }
}
