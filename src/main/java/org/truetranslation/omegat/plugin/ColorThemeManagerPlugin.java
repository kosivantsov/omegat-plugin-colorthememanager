package org.truetranslation.omegat.plugin;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.util.Preferences;
import org.omegat.util.gui.Styles;

import org.openide.awt.Mnemonics;

public class ColorThemeManagerPlugin {
    protected static final ResourceBundle res = ResourceBundle.getBundle("ColorThemeManagerPlugin", Locale.getDefault());
    private static JMenuItem colorManagementMenuItem;
    private static JDialog colorManagementDialog = null;

    public static void loadPlugins() {
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            @Override public void onApplicationStartup() { addMenuItems(); }
            @Override public void onApplicationShutdown() {}
        });
    }

    public static void unloadPlugins() {}

    private static void addMenuItems() {
        try {
            JMenu toolsMenu = Core.getMainWindow().getMainMenu().getToolsMenu();
            int menuPosition = toolsMenu.getItemCount();
            colorManagementMenuItem = new JMenuItem();
            Mnemonics.setLocalizedText(colorManagementMenuItem, res.getString("ctm.mainmenu.name"));
            colorManagementMenuItem.addActionListener(e -> showColorManagementDialog());
            toolsMenu.add(new JPopupMenu.Separator(), menuPosition);
            toolsMenu.add(colorManagementMenuItem, menuPosition + 1);
        } catch (Exception e) {
            System.err.println("CTM plugin: Error adding menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Utility: Update JTable row height dynamically based on font size + margin.
     */
    private static void updateTableRowHeight(JTable table, Font font) {
        FontMetrics fm = table.getFontMetrics(font);
        int newRowHeight = fm.getHeight() + 8; // margin for underline/waves
        table.setRowHeight(newRowHeight);
    }

    public static void showColorManagementDialog() {
        if (colorManagementDialog != null && colorManagementDialog.isShowing()) {
            colorManagementDialog.toFront();
            colorManagementDialog.requestFocus();
            return;
        }
        SwingUtilities.invokeLater(() -> {
            ColorPrefManager colorPrefs = new ColorPrefManager();

            JFrame owner = Core.getMainWindow().getApplicationFrame();
            String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

            String initialFont = "Dialog";
            int initialFontSize = 12;
            try {
                String f = Preferences.getPreference("source_font");
                if (f != null && !f.isEmpty()) initialFont = f;
                String s = Preferences.getPreference("source_font_size");
                if (s != null && !s.isEmpty()) initialFontSize = Integer.parseInt(s);
            } catch (Exception ignore) {}

            Font[] currentFont = { new Font(initialFont, Font.PLAIN, initialFontSize) };

            // --- UI Components ---

            final JComboBox<String> fontCombo = new JComboBox<>(fontFamilies);
            fontCombo.setSelectedItem(initialFont);
            fontCombo.setToolTipText(res.getString("ctm.dialog.tooltip.fontcombo"));

            final JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(initialFontSize, 6, 72, 1));
            sizeSpinner.setToolTipText(res.getString("ctm.dialog.tooltip.sizespiner"));

            final JTextField sourceSampleInput = new JTextField(24);
            sourceSampleInput.setToolTipText(res.getString("ctm.dialog.tooltip.sampleSource"));
            String sourceSampleTextPref = Preferences.getPreference("source_sample_input");
            sourceSampleInput.setText(sourceSampleTextPref == null || sourceSampleTextPref.isEmpty()
                    ? res.getString("ctm.dialog.sampleSourceText") : sourceSampleTextPref);

            final JTextField targetSampleInput = new JTextField(24);
            targetSampleInput.setToolTipText(res.getString("ctm.dialog.tooltip.sampleTarget"));
            String targetSampleTextPref = Preferences.getPreference("target_sample_input");
            targetSampleInput.setText(targetSampleTextPref == null || targetSampleTextPref.isEmpty()
                    ? res.getString("ctm.dialog.sampleTargetText") : targetSampleTextPref);

            final JTextField paraStartInput = new JTextField(24);
            paraStartInput.setToolTipText(res.getString("ctm.dialog.tooltip.paraDelim"));
            String paraTextPref = Preferences.getPreference(Preferences.MARK_PARA_TEXT);
            paraStartInput.setText(paraTextPref == null || paraTextPref.isEmpty()
                    ? Preferences.MARK_PARA_TEXT_DEFAULT : paraTextPref);

            final JCheckBox includeSampleCheckbox = new JCheckBox(res.getString("ctm.dialog.sampleTextCheckbox"));
            includeSampleCheckbox.setSelected(true);
            includeSampleCheckbox.setToolTipText(res.getString("ctm.dialog.tooltip.sampleCheckbox"));

            // --- Controls Panel Layout ---

            JPanel controls = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2, 4, 2, 4);

            gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END;
            controls.add(new JLabel(res.getString("ctm.dialog.fontSelector")), gbc);
            gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_START;
            controls.add(fontCombo, gbc);
            gbc.gridx = 2; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END;
            controls.add(new JLabel(res.getString("ctm.dialog.fontSize")), gbc);
            gbc.gridx = 3; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_START;
            controls.add(sizeSpinner, gbc);

            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.LINE_START;
            controls.add(includeSampleCheckbox, gbc);
            gbc.gridwidth = 1;

            gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_END;
            controls.add(new JLabel(res.getString("ctm.dialog.sampleSourceLabel")), gbc);
            gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; 
            gbc.anchor = GridBagConstraints.LINE_START; 
            gbc.fill = GridBagConstraints.HORIZONTAL; 
            gbc.weightx = 1.0;
            controls.add(sourceSampleInput, gbc);
            gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

            gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.LINE_END;
            controls.add(new JLabel(res.getString("ctm.dialog.sampleTargetLabel")), gbc);
            gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 3;
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            controls.add(targetSampleInput, gbc);
            gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

            gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.LINE_END;
            controls.add(new JLabel(res.getString("ctm.dialog.paraDelimLabel")), gbc);
            gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 3;
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            controls.add(paraStartInput, gbc);
            gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

            // --- Editor Colors Table Setup ---

            EditorColorsPaneModel editorColorsModel = new EditorColorsPaneModel();
            JTable editorColorsTable = new JTable(editorColorsModel);

            // Custom color handler for editor colors table
            ColorChangeHandler handlerEditor = new ColorChangeHandler() {
                @Override public void setFgColor(Styles.EditorColor ec, Color c) { colorPrefs.setActual(ec, c, true); }
                @Override public void setBgColor(Styles.EditorColor ec, Color c) { colorPrefs.setActual(ec, c, false); }
                @Override public Color getFgColor(Styles.EditorColor ec) { return colorPrefs.getActual(ec, true); }
                @Override public Color getBgColor(Styles.EditorColor ec) { return colorPrefs.getActual(ec, false); }
                @Override public Color getResetFg(Styles.EditorColor ec) { return colorPrefs.getDefault(ec, true); }
                @Override public Color getResetBg(Styles.EditorColor ec) { return colorPrefs.getDefault(ec, false); }
                @Override public void forceUpdateTable() { ((AbstractTableModel) editorColorsTable.getModel()).fireTableDataChanged(); }
                @Override public Font getCurrentFont() { return currentFont[0]; }
                @Override public Color getBackgroundColor() { return colorPrefs.getActual(Styles.EditorColor.COLOR_BACKGROUND, false); }
                @Override public String getSourceSampleText() { return sourceSampleInput.getText(); }
                @Override public String getTargetSampleText() { return targetSampleInput.getText(); }
                @Override public boolean isIncludeSample() { return includeSampleCheckbox.isSelected(); }
                @Override public String getParagraphDelimiter() { return paraStartInput.getText(); }
            };

            editorColorsTable.setDefaultRenderer(Object.class,
                new EditorColorTableUtils.MultiColumnPaneRenderer(editorColorsModel, handlerEditor));
            editorColorsTable.setShowGrid(false);
            editorColorsTable.setTableHeader(null);
            editorColorsTable.setFocusable(false);
            editorColorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            TableColumnModel columnModel1 = editorColorsTable.getColumnModel();
            columnModel1.getColumn(0).setPreferredWidth(600);
            EditorColorTableUtils.installColorPanePopup(editorColorsTable, editorColorsModel, handlerEditor, editorColorsTable::repaint);

            // --- Other Colors Table Setup ---

            OtherColorsPaneModel otherColorsModel = new OtherColorsPaneModel();
            JTable otherColorsTable = new JTable(otherColorsModel);

            ColorChangeHandler handlerOther = new ColorChangeHandler() {
                @Override public void setFgColor(Styles.EditorColor ec, Color c) { colorPrefs.setActual(ec, c, true); }
                @Override public void setBgColor(Styles.EditorColor ec, Color c) { colorPrefs.setActual(ec, c, false); }
                @Override public Color getFgColor(Styles.EditorColor ec) { return colorPrefs.getActual(ec, true); }
                @Override public Color getBgColor(Styles.EditorColor ec) { return colorPrefs.getActual(ec, false); }
                @Override public Color getResetFg(Styles.EditorColor ec) { return colorPrefs.getDefault(ec, true); }
                @Override public Color getResetBg(Styles.EditorColor ec) { return colorPrefs.getDefault(ec, false); }
                @Override public void forceUpdateTable() { ((AbstractTableModel) otherColorsTable.getModel()).fireTableDataChanged(); }
                @Override public Font getCurrentFont() { return currentFont[0]; }
                @Override public Color getBackgroundColor() { return colorPrefs.getActual(Styles.EditorColor.COLOR_BACKGROUND, false); }
                @Override public String getSourceSampleText() { return sourceSampleInput.getText(); }
                @Override public String getTargetSampleText() { return targetSampleInput.getText(); }
                @Override public boolean isIncludeSample() { return includeSampleCheckbox.isSelected(); }
                @Override public String getParagraphDelimiter() { return paraStartInput.getText(); }
            };

            otherColorsTable.setDefaultRenderer(Object.class,
                new EditorColorTableUtils.OtherPaneRenderer(otherColorsModel, handlerOther));
            otherColorsTable.setShowGrid(false);
            otherColorsTable.setTableHeader(null);
            otherColorsTable.setFocusable(false);
            otherColorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            TableColumnModel columnModel2 = otherColorsTable.getColumnModel();
            columnModel2.getColumn(0).setPreferredWidth(600);
            EditorColorTableUtils.installOtherColorPanePopup(otherColorsTable, otherColorsModel, handlerOther, otherColorsTable::repaint);

            // --- Scroll panes for tables ---

            JScrollPane editorScroll = new JScrollPane(editorColorsTable);
            editorScroll.setPreferredSize(new Dimension(375, 28 * editorColorsModel.getRowCount() + 40));
            editorScroll.setBorder(BorderFactory.createTitledBorder(res.getString("ctm.dialog.editorpane.title")));
            editorScroll.getViewport().setBackground(colorPrefs.getActual(Styles.EditorColor.COLOR_BACKGROUND, false));

            JScrollPane otherScroll = new JScrollPane(otherColorsTable);
            otherScroll.setPreferredSize(new Dimension(375, 28 * otherColorsModel.getRowCount() + 40));
            otherScroll.setBorder(BorderFactory.createTitledBorder(res.getString("ctm.dialog.otherpane.title")));
            otherScroll.getViewport().setBackground(colorPrefs.getActual(Styles.EditorColor.COLOR_BACKGROUND, false));

            JPanel middlePanel = new JPanel(new GridLayout(1, 2, 10, 0));
            middlePanel.add(editorScroll);
            middlePanel.add(otherScroll);

            // --- Main panel ---

            JPanel mainPanel = new JPanel(new BorderLayout());
            JPanel northPanel = new JPanel(new BorderLayout());
            northPanel.setBackground(colorPrefs.getActual(Styles.EditorColor.COLOR_BACKGROUND, false));
            northPanel.add(controls, BorderLayout.NORTH);
            mainPanel.add(northPanel, BorderLayout.NORTH);
            mainPanel.add(middlePanel, BorderLayout.CENTER);

            // --- Buttons ---

            JPanel buttonPanel = new JPanel(new BorderLayout());

            JPanel ioPanel = new JPanel();
            JButton importBtn = new JButton(res.getString("ctm.dialog.button.import"));
            JButton exportBtn = new JButton(res.getString("ctm.dialog.button.export"));
            JButton applyBtn = new JButton(res.getString("ctm.dialog.button.apply"));
            ioPanel.add(importBtn);
            ioPanel.add(exportBtn);
            ioPanel.add(applyBtn);

            JPanel okPanel = new JPanel();
            JButton okBtn = new JButton(res.getString("ctm.dialog.button.ok"));
            JButton cancelBtn = new JButton(res.getString("ctm.dialog.button.cancel"));
            okPanel.add(okBtn);
            okPanel.add(cancelBtn);

            // Tooltips
            importBtn.setToolTipText(res.getString("ctm.dialog.tooltip.importBtn"));
            exportBtn.setToolTipText(res.getString("ctm.dialog.tooltip.exportBtn"));
            applyBtn.setToolTipText(res.getString("ctm.dialog.tooltip.applyBtn"));
            okBtn.setToolTipText(res.getString("ctm.dialog.tooltip.okBtn"));
            cancelBtn.setToolTipText(res.getString("ctm.dialog.tooltip.cancelBtn"));

            buttonPanel.add(ioPanel, BorderLayout.WEST);
            buttonPanel.add(okPanel, BorderLayout.EAST);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            // --- Button actions ---

            importBtn.addActionListener(e -> {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle(res.getString("ctm.filechooser.import"));
                fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(res.getString("ctm.filechooser.filetypeFilter"), "omttheme"));
                int result = fc.showOpenDialog(mainPanel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        Map<String, String> extras = colorPrefs.importFromFile(file);
                        handlerEditor.forceUpdateTable();
                        handlerOther.forceUpdateTable();
                        // Update UI with imported extras if they exist
                        if (extras.containsKey("source_font")) {
                            fontCombo.setSelectedItem(extras.get("source_font"));
                        }
                        if (extras.containsKey("source_font_size")) {
                            try {
                                sizeSpinner.setValue(Integer.parseInt(extras.get("source_font_size")));
                            } catch (NumberFormatException ignore) {}
                        }
                        if (extras.containsKey("source_sample_input")) {
                            sourceSampleInput.setText(extras.get("source_sample_input"));
                        }
                        if (extras.containsKey("target_sample_input")) {
                            targetSampleInput.setText(extras.get("target_sample_input"));
                        }
                        if (extras.containsKey(Preferences.MARK_PARA_TEXT)) {
                            paraStartInput.setText(extras.get(Preferences.MARK_PARA_TEXT));
                            editorColorsModel.setParagraphDelimiter(extras.get(Preferences.MARK_PARA_TEXT));
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainPanel,
                                res.getString("ctm.error.import") + ex.getMessage(),
                                res.getString("ctm.error.title"),
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            exportBtn.addActionListener(e -> {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle(res.getString("ctm.filechooser.export"));
                fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(res.getString("ctm.filechooser.filetypeFilter"), "omttheme"));
                int result = fc.showSaveDialog(mainPanel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if (!file.getName().endsWith(".omttheme")) {
                        file = new File(file.getAbsolutePath() + ".omttheme");
                    }
                    try {
                        colorPrefs.exportToFile(
                            file,
                            fontCombo.getSelectedItem().toString(),
                            sizeSpinner.getValue().toString(),
                            sourceSampleInput.getText(),
                            targetSampleInput.getText(),
                            paraStartInput.getText()
                        );
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainPanel,
                                res.getString("ctm.error.export") + ex.getMessage(),
                                res.getString("ctm.error.title"),
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            applyBtn.addActionListener(e -> {
                String src = sourceSampleInput.getText().trim();
                String tgt = targetSampleInput.getText().trim();
                if (!src.isEmpty()) Preferences.setPreference("source_sample_input", src);
                if (!tgt.isEmpty()) Preferences.setPreference("target_sample_input", tgt);
                Preferences.setPreference("source_font", fontCombo.getSelectedItem().toString());
                Preferences.setPreference("source_font_size", sizeSpinner.getValue().toString());
                Preferences.setPreference(Preferences.MARK_PARA_TEXT, paraStartInput.getText());
                colorPrefs.saveAll();
                JOptionPane.showMessageDialog(mainPanel,
                        res.getString("ctm.dialog.restartText"),
                        res.getString("ctm.dialog.restartTitle"),
                        JOptionPane.INFORMATION_MESSAGE);
            });

            okBtn.addActionListener(e -> {
                String src = sourceSampleInput.getText().trim();
                String tgt = targetSampleInput.getText().trim();
                if (!src.isEmpty()) Preferences.setPreference("source_sample_input", src);
                if (!tgt.isEmpty()) Preferences.setPreference("target_sample_input", tgt);
                if (colorManagementDialog != null) colorManagementDialog.dispose();
            });

            cancelBtn.addActionListener(e -> {
                if (colorManagementDialog != null) colorManagementDialog.dispose();
            });

            // --- Dynamic font and row height update ---

            Runnable updateFont = () -> {
                String font = (String) fontCombo.getSelectedItem();
                int size = (Integer) sizeSpinner.getValue();
                currentFont[0] = new Font(font, Font.PLAIN, size);
                // Dynamically adjust row height of both tables
                updateTableRowHeight(editorColorsTable, currentFont[0]);
                updateTableRowHeight(otherColorsTable, currentFont[0]);
                editorColorsTable.repaint();
                otherColorsTable.repaint();
            };
            fontCombo.addActionListener(e -> updateFont.run());
            sizeSpinner.addChangeListener(e -> updateFont.run());

            // --- Repaint tables on text changes ---
            DocumentListener sampleUpdateListener = new DocumentListener() {
                @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { editorColorsTable.repaint(); otherColorsTable.repaint(); }
                @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { editorColorsTable.repaint(); otherColorsTable.repaint(); }
                @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { editorColorsTable.repaint(); otherColorsTable.repaint(); }
            };
            sourceSampleInput.getDocument().addDocumentListener(sampleUpdateListener);
            targetSampleInput.getDocument().addDocumentListener(sampleUpdateListener);
            paraStartInput.getDocument().addDocumentListener(sampleUpdateListener);
            includeSampleCheckbox.addActionListener(e -> { editorColorsTable.repaint(); otherColorsTable.repaint(); });

            // Paragraph delimiter model update on input changes
            paraStartInput.getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateParagraphDelimiter(); }
                @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateParagraphDelimiter(); }
                @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateParagraphDelimiter(); }
                private void updateParagraphDelimiter() {
                    editorColorsModel.setParagraphDelimiter(paraStartInput.getText());
                }
            });

            updateFont.run();

            colorManagementDialog = new JDialog((Frame) null, res.getString("ctm.dialog.title"), false);
            colorManagementDialog.setAlwaysOnTop(false);
            colorManagementDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            colorManagementDialog.setResizable(true);
            colorManagementDialog.getContentPane().add(mainPanel);
            colorManagementDialog.pack();
            colorManagementDialog.setMinimumSize(new Dimension(820, 375));
            colorManagementDialog.setLocationRelativeTo(owner);
            colorManagementDialog.addWindowListener(new WindowAdapter() {
                @Override public void windowClosed(WindowEvent e) {
                    colorManagementDialog = null;
                }
            });
            colorManagementDialog.setVisible(true);
        });
    }

    public interface ColorChangeHandler {
        void setFgColor(Styles.EditorColor colorEnum, Color c);
        void setBgColor(Styles.EditorColor colorEnum, Color c);
        Color getFgColor(Styles.EditorColor colorEnum);
        Color getBgColor(Styles.EditorColor colorEnum);
        Color getResetFg(Styles.EditorColor colorEnum);
        Color getResetBg(Styles.EditorColor colorEnum);
        void forceUpdateTable();
        Font getCurrentFont();
        Color getBackgroundColor();
        String getSourceSampleText();
        String getTargetSampleText();
        boolean isIncludeSample();
        String getParagraphDelimiter();
    }
}
