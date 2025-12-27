package org.truetranslation.omegat.plugin;

import org.truetranslation.omegat.plugin.ColorThemeManagerPlugin.ColorChangeHandler;
import org.omegat.util.gui.Styles;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditorColorTableUtils {

    protected static final ResourceBundle res = ResourceBundle.getBundle("ColorThemeManagerPlugin", Locale.getDefault());

    public static class MultiColumnPaneRenderer extends DefaultTableCellRenderer {
        private final EditorColorsPaneModel model;
        private final ColorChangeHandler handler;
        private int currentRenderRow = -1;

        public MultiColumnPaneRenderer(EditorColorsPaneModel model, ColorChangeHandler handler) {
            this.model = model;
            this.handler = handler;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int col) {
            currentRenderRow = row;
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            if (!model.isVisible(row, col)) {
                setText("");
                setBackground(handler.getBackgroundColor());
                return this;
            }
            setFont(handler.getCurrentFont());

            Styles.EditorColor fgEnum = model.getFgColorAt(row);
            Styles.EditorColor bgEnum = model.getBgColorAt(row);

            Color fg = handler.getFgColor(fgEnum);
            Color bg = model.isNbspRow(row)
                    ? handler.getBgColor(Styles.EditorColor.COLOR_BACKGROUND)
                    : handler.getBgColor(bgEnum);

            setForeground(fg != null ? fg : Color.BLACK);
            setBackground(bg != null ? bg : handler.getBackgroundColor());

            String baseText = Objects.toString(value, "");

            setText(baseText);

            // Dynamic sample text logic
            if (model.isDynamicSampleRow(row, col) && handler.isIncludeSample()) {
                Styles.EditorColor bgColor = model.getBgColorAt(row);
                Styles.EditorColor fgColor = model.getFgColorAt(row);
                
                // Determine if this is a source or target row
                boolean isSourceRow = (fgColor == Styles.EditorColor.COLOR_SOURCE_FG || 
                                      (fgColor == Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG && !model.isBgMenuDisabledRow(row)) ||
                                      fgColor == Styles.EditorColor.COLOR_UNTRANSLATED_FG);
                
                if (isSourceRow) {
                    String srcSample = handler.getSourceSampleText();
                    if (!srcSample.isEmpty())
                        setText(getText() + " " + srcSample);
                } else {
                    // This is a target row (including alternative translation)
                    String tgtSample = handler.getTargetSampleText();
                    if (!tgtSample.isEmpty())
                        setText(getText() + " " + tgtSample);
                }
            }
            setHorizontalAlignment(SwingConstants.LEFT);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int row = currentRenderRow;
            if (row < 0) return;
            String text = getText();
            if (text == null || text.isEmpty()) return;
            FontMetrics fm = getFontMetrics(getFont());
            int baseline = fm.getAscent();
            int xStart = getInsets().left;
            int textWidth = fm.stringWidth(text);

            if (model.isWhitespaceRow(row)) {
                Color wsColor = handler.getFgColor(Styles.EditorColor.COLOR_WHITESPACE);
                if (wsColor == null) wsColor = Color.GRAY;
                g.setColor(wsColor);
                g.drawString("·»¶", xStart + textWidth, baseline);
            }
            if (model.isNbspRow(row)) {
                Color nbspColor = handler.getFgColor(Styles.EditorColor.COLOR_NBSP);
                if (nbspColor == null) nbspColor = Color.LIGHT_GRAY;
                g.setColor(nbspColor);
                g.drawString("█", xStart + textWidth, baseline);
            }
            if (model.isBidiRow(row)) {
                Color bidiColor = handler.getFgColor(Styles.EditorColor.COLOR_BIDIMARKERS);
                if (bidiColor == null) bidiColor = Color.GREEN.darker();
                g.setColor(bidiColor);
                g.drawString("◥ ◤", xStart + textWidth, baseline);
            }
            if (model.isGlossaryRow(row)) {
                Color underline = handler.getFgColor(Styles.EditorColor.COLOR_TRANSTIPS);
                if (underline == null) underline = Color.BLUE;
                g.setColor(underline);
                g.drawLine(xStart, baseline + 4, xStart + fm.stringWidth(text), baseline + 4);
            }
            if (model.isLanguageToolRow(row)) {
                Color underline = handler.getFgColor(Styles.EditorColor.COLOR_LANGUAGE_TOOLS);
                if (underline == null) underline = Color.MAGENTA;
                g.setColor(underline);
                int width = fm.stringWidth(text);
                int y = baseline + 4;
                int period = 7;
                int amplitude = 2;
                int prevX = xStart, prevY = y;
                for (int x = xStart; x < xStart + width; x++) {
                    int wave = (int) (amplitude * Math.sin(x * 2.0 * Math.PI / period));
                    if (x > xStart) g.drawLine(prevX, prevY, x, y + wave);
                    prevX = x;
                    prevY = y + wave;
                }
            }
            // Check if this is spelling errors row by checking the fg/bg color combination
            Styles.EditorColor fgColor = model.getFgColorAt(row);
            Styles.EditorColor bgColor = model.getBgColorAt(row);
            if (fgColor == Styles.EditorColor.COLOR_ACTIVE_TARGET_FG && 
                bgColor == Styles.EditorColor.COLOR_ACTIVE_TARGET && 
                model.isBgMenuDisabledRow(row) && 
                !model.isLanguageToolRow(row) && 
                !model.isGlossaryRow(row)) {
                // This is likely the spelling errors row - draw red wavy underline
                Color waveColor = handler.getFgColor(Styles.EditorColor.COLOR_SPELLCHECK);
                if (waveColor == null) waveColor = Color.RED;
                g.setColor(waveColor);
                int width = fm.stringWidth(text);
                int y = baseline + 4;
                int period = 7;
                int amplitude = 2;
                int prevX = xStart, prevY = y;
                for (int x = xStart; x < xStart + width; x++) {
                    int wave = (int) (amplitude * Math.sin(x * 2.0 * Math.PI / period));
                    if (x > xStart) g.drawLine(prevX, prevY, x, y + wave);
                    prevX = x;
                    prevY = y + wave;
                }
            }
        }
    }

    public static class OtherPaneRenderer extends DefaultTableCellRenderer {
        private final OtherColorsPaneModel model;
        private final ColorChangeHandler handler;

        public OtherPaneRenderer(OtherColorsPaneModel model, ColorChangeHandler handler) {
            this.model = model;
            this.handler = handler;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            Styles.EditorColor fgEC = model.getFgColorAt(row);
            Styles.EditorColor bgEC = model.getBgColorAt(row);
            setFont(handler.getCurrentFont());
            Color fg = handler.getFgColor(fgEC);
            Color bg = handler.getBgColor(bgEC);
            setForeground(fg != null ? fg : Color.BLACK);
            setBackground(bg != null ? bg : handler.getBackgroundColor());
            setHorizontalAlignment(SwingConstants.LEFT);
            setText(Objects.toString(value, ""));
            return this;
        }
    }

    public static void installColorPanePopup(JTable table, EditorColorsPaneModel model,
                                             ColorChangeHandler handler, Runnable globalUpdate) {
        installPopup(table, model.getRowCount(),
                (row) -> model.getFgColorAt(row),
                (row) -> model.getBgColorAt(row),
                model::isBgMenuDisabledRow,
                model::isFgMenuDisabledRow,
                model::isFgMenuUnderlineOnlyRow,
                handler, globalUpdate);
    }

    public static void installOtherColorPanePopup(JTable table, OtherColorsPaneModel model,
                                                  ColorChangeHandler handler, Runnable globalUpdate) {
        installPopup(table, model.getRowCount(),
                model::getFgColorAt,
                model::getBgColorAt,
                model::isBgMenuDisabledRow,
                model::isFgMenuDisabledRow,
                (row) -> false,
                handler, globalUpdate);
    }

    /**
     * Unified popup menu logic for both color tables.
     */
    private static void installPopup(
            final JTable table, final int rowCount,
            java.util.function.IntFunction<Styles.EditorColor> fgGetter,
            java.util.function.IntFunction<Styles.EditorColor> bgGetter,
            java.util.function.IntPredicate bgMenuDisabled,
            java.util.function.IntPredicate fgMenuDisabled,
            java.util.function.IntPredicate fgUnderlineOnly,
            final ColorChangeHandler handler,
            final Runnable globalUpdate) {

        final JPopupMenu popup = new JPopupMenu();
        final JMenuItem chooseFg = new JMenuItem(res.getString("ctm.contextmenu.chooseFG"));
        final JMenuItem resetFg = new JMenuItem(res.getString("ctm.contextmenu.resetFG"));
        final JMenuItem copyFg = new JMenuItem(res.getString("ctm.contextmenu.copyFG"));
        final JMenuItem pasteFg = new JMenuItem(res.getString("ctm.contextmenu.pasteFG"));
        final JMenuItem chooseBg = new JMenuItem(res.getString("ctm.contextmenu.chooseBG"));
        final JMenuItem resetBg = new JMenuItem(res.getString("ctm.contextmenu.resetBG"));
        final JMenuItem copyBg = new JMenuItem(res.getString("ctm.contextmenu.copyBG"));
        final JMenuItem pasteBg = new JMenuItem(res.getString("ctm.contextmenu.pasteBG"));

        popup.add(chooseFg); popup.add(resetFg); popup.add(copyFg); popup.add(pasteFg);
        popup.addSeparator();
        popup.add(chooseBg); popup.add(resetBg); popup.add(copyBg); popup.add(pasteBg);

        final int[] currentRow = {-1};

        Runnable updateMenuItemsState = () -> {
            int row = currentRow[0];
            boolean fgEnable = !fgMenuDisabled.test(row);
            boolean bgEnable = !bgMenuDisabled.test(row);
            chooseFg.setEnabled(fgEnable);
            resetFg.setEnabled(fgEnable);
            copyFg.setEnabled(fgEnable);
            pasteFg.setEnabled(fgEnable);
            chooseBg.setEnabled(bgEnable);
            resetBg.setEnabled(bgEnable);
            copyBg.setEnabled(bgEnable);
            pasteBg.setEnabled(bgEnable);
        };

        table.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { maybeShow(e);}
            @Override public void mouseReleased(MouseEvent e) { maybeShow(e);}
            private void maybeShow(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        currentRow[0] = row;
                        table.setRowSelectionInterval(row, row);
                        updateMenuItemsState.run();
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        chooseFg.addActionListener(ev -> {
            int row = currentRow[0];
            if (row < 0) return;
            Styles.EditorColor fgEnum = fgGetter.apply(row);
            boolean underlineOnly = fgUnderlineOnly.test(row);
            Styles.EditorColor editEnum = fgEnum;
            if (underlineOnly) {
                // Determine which underline color to edit based on the row's foreground color
                if (fgEnum == Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG) {
                    editEnum = Styles.EditorColor.COLOR_TRANSTIPS; // Glossary underline
                } else if (fgEnum == Styles.EditorColor.COLOR_ACTIVE_TARGET_FG) {
                    // Need to distinguish between LanguageTool and Spellcheck
                    // Check if COLOR_LANGUAGE_TOOLS exists
                    try {
                        Styles.EditorColor.valueOf("COLOR_LANGUAGE_TOOLS");
                        editEnum = Styles.EditorColor.COLOR_LANGUAGE_TOOLS; // Default to LangTool
                    } catch (IllegalArgumentException e) {
                        editEnum = Styles.EditorColor.COLOR_SPELLCHECK;
                    }
                } else {
                    return;
                }
            }
            Color curr = handler.getFgColor(editEnum);
            Color sel = JColorChooser.showDialog(
                table,
                underlineOnly
                  ? res.getString("ctm.colorchooser.underlineTitle")
                  : res.getString("ctm.colorchooser.foregroundTitle"),
                curr
            );
            if (sel != null) {
                handler.setFgColor(editEnum, sel);
                handler.forceUpdateTable();
                globalUpdate.run();
            }
        });
        resetFg.addActionListener(ev -> {
            int row = currentRow[0]; if (row < 0) return;
            Styles.EditorColor fgEnum = fgGetter.apply(row);
            boolean underlineOnly = fgUnderlineOnly.test(row);
            Styles.EditorColor editEnum = fgEnum;
            if (underlineOnly) {
                if (fgEnum == Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG) {
                    editEnum = Styles.EditorColor.COLOR_TRANSTIPS;
                } else if (fgEnum == Styles.EditorColor.COLOR_ACTIVE_TARGET_FG) {
                    try {
                        Styles.EditorColor.valueOf("COLOR_LANGUAGE_TOOLS");
                        editEnum = Styles.EditorColor.COLOR_LANGUAGE_TOOLS;
                    } catch (IllegalArgumentException e) {
                        editEnum = Styles.EditorColor.COLOR_SPELLCHECK;
                    }
                } else {
                    return;
                }
            }
            handler.setFgColor(editEnum, handler.getResetFg(editEnum));
            handler.forceUpdateTable();
            globalUpdate.run();
        });
        copyFg.addActionListener(ev -> {
            int row = currentRow[0]; if (row < 0) return;
            Styles.EditorColor fgEnum = fgGetter.apply(row);
            boolean underlineOnly = fgUnderlineOnly.test(row);
            Styles.EditorColor editEnum = fgEnum;
            if (underlineOnly) {
                if (fgEnum == Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG) {
                    editEnum = Styles.EditorColor.COLOR_TRANSTIPS;
                } else if (fgEnum == Styles.EditorColor.COLOR_ACTIVE_TARGET_FG) {
                    try {
                        Styles.EditorColor.valueOf("COLOR_LANGUAGE_TOOLS");
                        editEnum = Styles.EditorColor.COLOR_LANGUAGE_TOOLS;
                    } catch (IllegalArgumentException e) {
                        editEnum = Styles.EditorColor.COLOR_SPELLCHECK;
                    }
                } else {
                    return;
                }
            }
            copyHex(handler.getFgColor(editEnum));
        });
        pasteFg.addActionListener(ev -> {
            int row = currentRow[0]; if (row < 0) return;
            Styles.EditorColor fgEnum = fgGetter.apply(row);
            boolean underlineOnly = fgUnderlineOnly.test(row);
            Styles.EditorColor editEnum = fgEnum;
            if (underlineOnly) {
                if (fgEnum == Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG) {
                    editEnum = Styles.EditorColor.COLOR_TRANSTIPS;
                } else if (fgEnum == Styles.EditorColor.COLOR_ACTIVE_TARGET_FG) {
                    try {
                        Styles.EditorColor.valueOf("COLOR_LANGUAGE_TOOLS");
                        editEnum = Styles.EditorColor.COLOR_LANGUAGE_TOOLS;
                    } catch (IllegalArgumentException e) {
                        editEnum = Styles.EditorColor.COLOR_SPELLCHECK;
                    }
                } else {
                    return;
                }
            }
            Color c = getColorFromClipboard();
            if (c != null) {
                handler.setFgColor(editEnum, c);
                handler.forceUpdateTable();
                globalUpdate.run();
            } else {
                JOptionPane.showMessageDialog(table, "Clipboard does not contain a valid color code.", "Paste FG color", JOptionPane.WARNING_MESSAGE);
            }
        });

        chooseBg.addActionListener(ev -> {
            int row = currentRow[0]; if (row < 0) return;
            Styles.EditorColor bgEnum = bgGetter.apply(row);
            Color curr = handler.getBgColor(bgEnum);
            Color sel = JColorChooser.showDialog(
                table,
                res.getString("ctm.colorchooser.backgroundTitle"),
                curr
            );
            if (sel != null) {
                handler.setBgColor(bgEnum, sel);
                handler.forceUpdateTable();
                globalUpdate.run();
            }
        });
        resetBg.addActionListener(ev -> {
            int row = currentRow[0]; if (row < 0) return;
            Styles.EditorColor bgEnum = bgGetter.apply(row);
            handler.setBgColor(bgEnum, handler.getResetBg(bgEnum));
            handler.forceUpdateTable();
            globalUpdate.run();
        });
        copyBg.addActionListener(ev -> {
            int row = currentRow[0]; if (row < 0) return;
            Styles.EditorColor bgEnum = bgGetter.apply(row);
            copyHex(handler.getBgColor(bgEnum));
        });
        pasteBg.addActionListener(ev -> {
            int row = currentRow[0]; if (row < 0) return;
            Styles.EditorColor bgEnum = bgGetter.apply(row);
            Color c = getColorFromClipboard();
            if (c != null) {
                handler.setBgColor(bgEnum, c);
                handler.forceUpdateTable();
                globalUpdate.run();
            } else {
                JOptionPane.showMessageDialog(table, "Clipboard does not contain a valid color code.", "Paste BG color", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public static Color getDefaultFg(Styles.EditorColor ec) {
        Color c = ec.getColor();
        if (c == null) c = ec.getDefault();
        if (c == null) c = Color.BLACK;
        return c;
    }

    public static Color getDefaultBg(Styles.EditorColor ec) {
        Color c = ec.getColor();
        if (c == null) c = ec.getDefault();
        if (c == null) c = Color.WHITE;
        return c;
    }

    private static void copyHex(Color color) {
        if (color != null) {
            String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(hex), null);
        }
    }

    /**
     * Tries to extract a Color from the system clipboard as "#RRGGBB", "RRGGBB" or "RGB".
     * Returns null if not possible.
     */
    private static Color getColorFromClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                String str = ((String) clipboard.getData(DataFlavor.stringFlavor)).trim();
                if (str.startsWith("#"))
                    str = str.substring(1);
                if (str.matches("[0-9A-Fa-f]{6}")) {
                    int rgb = Integer.parseInt(str, 16);
                    return new Color(rgb);
                }
                if (str.matches("[0-9A-Fa-f]{3}")) {
                    // Expand "abc" to "aabbcc"
                    int r = Integer.parseInt(str.substring(0,1) + str.substring(0,1), 16);
                    int g = Integer.parseInt(str.substring(1,2) + str.substring(1,2), 16);
                    int b = Integer.parseInt(str.substring(2,3) + str.substring(2,3), 16);
                    return new Color(r, g, b);
                }
            }
        } catch (Exception ex) {
            // ignore
        }
        return null;
    }
}
