package org.truetranslation.omegat.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.omegat.util.Preferences;
import org.omegat.util.gui.Styles;

public class EditorColorsPaneModel extends AbstractTableModel {
    protected static final ResourceBundle res = ResourceBundle.getBundle("ColorThemeManagerPlugin", Locale.getDefault());

    static final Object[][] ROWS = buildRows();

    private static Object[][] buildRows() {
        List<Object[]> rowList = new ArrayList<>();
        
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row0", "COLOR_FOREGROUND", "COLOR_BACKGROUND", false, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row1", "COLOR_SOURCE_FG", "COLOR_SOURCE", false, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row2", "COLOR_TRANSLATED_FG", "COLOR_TRANSLATED", false, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row3", "COLOR_MOD_INFO_FG", "COLOR_MOD_INFO", false, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row4", "COLOR_ACTIVE_SOURCE_FG", "COLOR_ACTIVE_SOURCE", false, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row5", "COLOR_ACTIVE_SOURCE_FG", "COLOR_ACTIVE_SOURCE", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row6", "COLOR_ACTIVE_TARGET_FG", "COLOR_ACTIVE_TARGET", false, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row7", "COLOR_ACTIVE_TARGET_FG", "COLOR_ACTIVE_TARGET", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row8", "COLOR_PLACEHOLDER", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row9", "COLOR_ACTIVE_TARGET_FG", "COLOR_ACTIVE_TARGET", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row10", "COLOR_WHITESPACE", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row11", "COLOR_NBSP", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row12", "COLOR_BIDIMARKERS", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row13", "COLOR_SEGMENT_MARKER_FG", "COLOR_SEGMENT_MARKER_BG", false, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row14", "COLOR_PARAGRAPH_START", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row15", "COLOR_UNTRANSLATED_FG", "COLOR_UNTRANSLATED", false, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row15a", "COLOR_TRANSLATED_FG", "COLOR_MARK_ALT_TRANSLATION", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row16", "COLOR_REMOVETEXT_TARGET", "COLOR_ACTIVE_TARGET", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row17", "COLOR_NON_UNIQUE", "COLOR_NON_UNIQUE_BG", false, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row18", "COLOR_NOTED_FG", "COLOR_NOTED", false, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row19", "COLOR_HYPERLINK", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row20", "COLOR_TRANSLATED_FG", "COLOR_MARK_COMES_FROM_TM", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row21", "COLOR_TRANSLATED_FG", "COLOR_MARK_COMES_FROM_TM_XICE", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row22", "COLOR_TRANSLATED_FG", "COLOR_MARK_COMES_FROM_TM_X100PC", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row23", "COLOR_TRANSLATED_FG", "COLOR_MARK_COMES_FROM_TM_XAUTO", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row24", "COLOR_TRANSLATED_FG", "COLOR_MARK_COMES_FROM_TM_XENFORCED", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.editorpane.row24a", "COLOR_TRANSLATED_FG", "COLOR_MARK_COMES_FROM_TM_MT", false, true);
        
        return rowList.toArray(new Object[0][]);
    }
    
    private static void addRowIfColorExists(List<Object[]> rowList, String resourceKey, 
                                           String fgColorName, String bgColorName,
                                           boolean bgDisabled, boolean fgDisabled) {
        try {
            Styles.EditorColor fgColor = Styles.EditorColor.valueOf(fgColorName);
            Styles.EditorColor bgColor = Styles.EditorColor.valueOf(bgColorName);
            rowList.add(new Object[] { 
                res.getString(resourceKey), 
                fgColor, 
                bgColor, 
                bgDisabled, 
                fgDisabled 
            });
        } catch (IllegalArgumentException e) {
            // One of the colors doesn't exist in this OmegaT version, skip this row
        }
    }

    private String paragraphDelimiter = null; // null means use Preferences

    public void setParagraphDelimiter(String delimiter) {
        this.paragraphDelimiter = delimiter;
        // Find the row with COLOR_PARAGRAPH_START and fire update for it
        for (int i = 0; i < ROWS.length; i++) {
            if (ROWS[i][1] == Styles.EditorColor.COLOR_PARAGRAPH_START) {
                fireTableCellUpdated(i, 0);
                break;
            }
        }
    }
    
    @Override
    public int getRowCount() { return ROWS.length; }
    @Override
    public int getColumnCount() { return 1; }
    @Override
    public Object getValueAt(int row, int col) {
        // Check if this is the paragraph delimiter row
        if (ROWS[row][1] == Styles.EditorColor.COLOR_PARAGRAPH_START) {
            if (paragraphDelimiter != null && !paragraphDelimiter.isEmpty()) {
                return paragraphDelimiter;
            }
            String val = Preferences.getPreference(Preferences.MARK_PARA_TEXT);
            if (val == null || val.isEmpty()) {
                return Preferences.MARK_PARA_TEXT_DEFAULT;
            }
            return val;
        }
        return ROWS[row][0];
    }
    public Styles.EditorColor getFgColorAt(int row) { return (Styles.EditorColor) ROWS[row][1]; }
    public Styles.EditorColor getBgColorAt(int row) { return (Styles.EditorColor) ROWS[row][2]; }

    public boolean isBgMenuDisabledRow(int row) { return (Boolean) ROWS[row][3]; }
    public boolean isFgMenuDisabledRow(int row) { return (Boolean) ROWS[row][4]; }
    public boolean isFgMenuUnderlineOnlyRow(int row) {
        Styles.EditorColor fg = getFgColorAt(row);
        return fg == Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG && (Boolean) ROWS[row][3]; // Glossary, LanguageTool, Spellcheck rows
    }
    public boolean isWhitespaceRow(int row) { return getFgColorAt(row) == Styles.EditorColor.COLOR_WHITESPACE; }
    public boolean isNbspRow(int row) { return getFgColorAt(row) == Styles.EditorColor.COLOR_NBSP; }
    public boolean isBidiRow(int row) { return getFgColorAt(row) == Styles.EditorColor.COLOR_BIDIMARKERS; }
    public boolean isGlossaryRow(int row) { 
        // Check if this is row that has underline-only and is the glossary row (row 5 in original)
        return getFgColorAt(row) == Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG && 
               (Boolean) ROWS[row][3] && 
               row > 0 && 
               getFgColorAt(row-1) == Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG && 
               !(Boolean) ROWS[row-1][3];
    }
    public boolean isLanguageToolRow(int row) { 
        return getFgColorAt(row) == Styles.EditorColor.COLOR_ACTIVE_TARGET_FG && 
               (Boolean) ROWS[row][3] &&
               row > 1;
    }
    public boolean isVisible(int row, int col) { return col == 0; }
    public boolean isDynamicSampleRow(int row, int col) {
        if (col != 0) return false;
        Styles.EditorColor fg = getFgColorAt(row);
        Styles.EditorColor bg = getBgColorAt(row);
        // Check for source rows
        if (fg == Styles.EditorColor.COLOR_SOURCE_FG || 
            (fg == Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG && !isBgMenuDisabledRow(row)) ||
            fg == Styles.EditorColor.COLOR_UNTRANSLATED_FG) {
            return true;
        }
        // Check for target rows
        if ((fg == Styles.EditorColor.COLOR_TRANSLATED_FG && bg == Styles.EditorColor.COLOR_TRANSLATED) ||
            (fg == Styles.EditorColor.COLOR_ACTIVE_TARGET_FG && bg == Styles.EditorColor.COLOR_ACTIVE_TARGET && !isBgMenuDisabledRow(row))) {
            return true;
        }
        return false;
    }
}
