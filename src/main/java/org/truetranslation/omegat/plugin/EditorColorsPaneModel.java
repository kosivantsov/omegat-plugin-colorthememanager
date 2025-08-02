package org.truetranslation.omegat.plugin;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.omegat.util.Preferences;
import org.omegat.util.gui.Styles;

public class EditorColorsPaneModel extends AbstractTableModel {
    protected static final ResourceBundle res = ResourceBundle.getBundle("ColorThemeManagerPlugin", Locale.getDefault());

    // See your detailed row mapping for row designations
    static final Object[][] ROWS = {
        { res.getString("ctm.dialog.editorpane.row0"), Styles.EditorColor.COLOR_FOREGROUND, Styles.EditorColor.COLOR_BACKGROUND, false, false },
        { res.getString("ctm.dialog.editorpane.row1"), Styles.EditorColor.COLOR_SOURCE_FG, Styles.EditorColor.COLOR_SOURCE, false, false },
        { res.getString("ctm.dialog.editorpane.row2"), Styles.EditorColor.COLOR_TRANSLATED_FG, Styles.EditorColor.COLOR_TRANSLATED, false, false },
        { res.getString("ctm.dialog.editorpane.row3"), Styles.EditorColor.COLOR_MOD_INFO_FG, Styles.EditorColor.COLOR_MOD_INFO, false, false },
        { res.getString("ctm.dialog.editorpane.row4"), Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG, Styles.EditorColor.COLOR_ACTIVE_SOURCE, false, false },
        { res.getString("ctm.dialog.editorpane.row5"), Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG, Styles.EditorColor.COLOR_ACTIVE_SOURCE, true, false },
        { res.getString("ctm.dialog.editorpane.row6"), Styles.EditorColor.COLOR_ACTIVE_TARGET_FG, Styles.EditorColor.COLOR_ACTIVE_TARGET, false, false },
        { res.getString("ctm.dialog.editorpane.row7"), Styles.EditorColor.COLOR_ACTIVE_TARGET_FG, Styles.EditorColor.COLOR_ACTIVE_TARGET, true, false },
        { res.getString("ctm.dialog.editorpane.row8"), Styles.EditorColor.COLOR_PLACEHOLDER, Styles.EditorColor.COLOR_BACKGROUND, true, false },
        { res.getString("ctm.dialog.editorpane.row9"), Styles.EditorColor.COLOR_ACTIVE_TARGET_FG, Styles.EditorColor.COLOR_ACTIVE_TARGET, true, false },
        { res.getString("ctm.dialog.editorpane.row10"), Styles.EditorColor.COLOR_WHITESPACE, Styles.EditorColor.COLOR_BACKGROUND, true, false },
        { res.getString("ctm.dialog.editorpane.row11"), Styles.EditorColor.COLOR_NBSP, Styles.EditorColor.COLOR_BACKGROUND, true, false },
        { res.getString("ctm.dialog.editorpane.row12"), Styles.EditorColor.COLOR_BIDIMARKERS, Styles.EditorColor.COLOR_BACKGROUND, true, false },
        { res.getString("ctm.dialog.editorpane.row13"), Styles.EditorColor.COLOR_SEGMENT_MARKER_FG, Styles.EditorColor.COLOR_SEGMENT_MARKER_BG, false, false },
        { res.getString("ctm.dialog.editorpane.row14"), Styles.EditorColor.COLOR_PARAGRAPH_START, Styles.EditorColor.COLOR_BACKGROUND, true, false },
        { res.getString("ctm.dialog.editorpane.row15"), Styles.EditorColor.COLOR_UNTRANSLATED_FG, Styles.EditorColor.COLOR_UNTRANSLATED, false, false },
        { res.getString("ctm.dialog.editorpane.row16"), Styles.EditorColor.COLOR_REMOVETEXT_TARGET, Styles.EditorColor.COLOR_ACTIVE_TARGET, true, false },
        { res.getString("ctm.dialog.editorpane.row17"), Styles.EditorColor.COLOR_NON_UNIQUE, Styles.EditorColor.COLOR_NON_UNIQUE_BG, false, false },
        { res.getString("ctm.dialog.editorpane.row18"), Styles.EditorColor.COLOR_NOTED_FG, Styles.EditorColor.COLOR_NOTED, false, false },
        { res.getString("ctm.dialog.editorpane.row19"), Styles.EditorColor.COLOR_HYPERLINK, Styles.EditorColor.COLOR_BACKGROUND, true, false },
        { res.getString("ctm.dialog.editorpane.row20"), Styles.EditorColor.COLOR_TRANSLATED_FG, Styles.EditorColor.COLOR_MARK_COMES_FROM_TM, false, true },
        { res.getString("ctm.dialog.editorpane.row21"), Styles.EditorColor.COLOR_TRANSLATED_FG, Styles.EditorColor.COLOR_MARK_COMES_FROM_TM_XICE, false, true },
        { res.getString("ctm.dialog.editorpane.row22"), Styles.EditorColor.COLOR_TRANSLATED_FG, Styles.EditorColor.COLOR_MARK_COMES_FROM_TM_X100PC, false, true },
        { res.getString("ctm.dialog.editorpane.row23"), Styles.EditorColor.COLOR_TRANSLATED_FG, Styles.EditorColor.COLOR_MARK_COMES_FROM_TM_XAUTO, false, true },
        { res.getString("ctm.dialog.editorpane.row24"), Styles.EditorColor.COLOR_TRANSLATED_FG, Styles.EditorColor.COLOR_MARK_COMES_FROM_TM_XENFORCED, false, true },
    };

    private String paragraphDelimiter = null; // null means use Preferences

    public void setParagraphDelimiter(String delimiter) {
        this.paragraphDelimiter = delimiter;
        fireTableCellUpdated(14, 0); // row 14, col 0 is the paragraph delimiter row
    }
    
    @Override
    public int getRowCount() { return ROWS.length; }
    @Override
    public int getColumnCount() { return 1; }
    @Override
    public Object getValueAt(int row, int col) {
        if (row == 14) {
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
        return row == 5 || row == 7 || row == 9;
    }
    public boolean isWhitespaceRow(int row) { return row == 10; }
    public boolean isNbspRow(int row) { return row == 11; }
    public boolean isBidiRow(int row) { return row == 12; }
    public boolean isGlossaryRow(int row) { return row == 5; }
    public boolean isLanguageToolRow(int row) { return row == 9; }
    public boolean isVisible(int row, int col) { return col == 0; }
    public boolean isDynamicSampleRow(int row, int col) {
        return col == 0 && (row == 1 || row == 2 || row == 4 || row == 6 || row == 15);
    }


}

