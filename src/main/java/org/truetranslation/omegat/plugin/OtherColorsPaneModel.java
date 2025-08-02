package org.truetranslation.omegat.plugin;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.omegat.util.gui.Styles;


public class OtherColorsPaneModel extends AbstractTableModel {

    protected static final ResourceBundle res = ResourceBundle.getBundle("ColorThemeManagerPlugin", Locale.getDefault());

    static final Object[][] ROWS = {
            // rows with BG menu disabled; BG is COLOR_BACKGROUND
            { res.getString("ctm.dialog.otherpane.row0"), Styles.EditorColor.COLOR_MATCHES_INS_ACTIVE, Styles.EditorColor.COLOR_BACKGROUND, true, false },
            { res.getString("ctm.dialog.otherpane.row1"), Styles.EditorColor.COLOR_MATCHES_DEL_ACTIVE, Styles.EditorColor.COLOR_BACKGROUND, true, false },
            { res.getString("ctm.dialog.otherpane.row2"), Styles.EditorColor.COLOR_MATCHES_INS_INACTIVE, Styles.EditorColor.COLOR_BACKGROUND, true, false },
            { res.getString("ctm.dialog.otherpane.row3"), Styles.EditorColor.COLOR_MATCHES_DEL_INACTIVE, Styles.EditorColor.COLOR_BACKGROUND, true, false },
            { res.getString("ctm.dialog.otherpane.row4"), Styles.EditorColor.COLOR_MATCHES_CHANGED, Styles.EditorColor.COLOR_BACKGROUND, true, false },
            { res.getString("ctm.dialog.otherpane.row5"), Styles.EditorColor.COLOR_MATCHES_UNCHANGED, Styles.EditorColor.COLOR_BACKGROUND, true, false },
            { res.getString("ctm.dialog.otherpane.row6"), Styles.EditorColor.COLOR_GLOSSARY_SOURCE, Styles.EditorColor.COLOR_BACKGROUND, true, false },
            { res.getString("ctm.dialog.otherpane.row7"), Styles.EditorColor.COLOR_GLOSSARY_TARGET, Styles.EditorColor.COLOR_BACKGROUND, true, false },
            { res.getString("ctm.dialog.otherpane.row8"), Styles.EditorColor.COLOR_GLOSSARY_NOTE, Styles.EditorColor.COLOR_BACKGROUND, true, false },
            { res.getString("ctm.dialog.otherpane.row9"), Styles.EditorColor.COLOR_SEARCH_FOUND_MARK, Styles.EditorColor.COLOR_BACKGROUND, true, false },
            { res.getString("ctm.dialog.otherpane.row10"), Styles.EditorColor.COLOR_SEARCH_REPLACE_MARK, Styles.EditorColor.COLOR_BACKGROUND, true, false },
            // rows with FG menu disabled, fg is COLOR_FOREGROUND:
            { res.getString("ctm.dialog.otherpane.row11"), Styles.EditorColor.COLOR_FOREGROUND, Styles.EditorColor.COLOR_REPLACE, false, true },
            { res.getString("ctm.dialog.otherpane.row12"), Styles.EditorColor.COLOR_FOREGROUND, Styles.EditorColor.COLOR_TERMINOLOGY, false, true },
            { res.getString("ctm.dialog.otherpane.row13"), Styles.EditorColor.COLOR_FOREGROUND, Styles.EditorColor.COLOR_ALIGNER_HIGHLIGHT, false, true },
            { res.getString("ctm.dialog.otherpane.row14"), Styles.EditorColor.COLOR_FOREGROUND, Styles.EditorColor.COLOR_ALIGNER_ACCEPTED, false, true },
            { res.getString("ctm.dialog.otherpane.row15"), Styles.EditorColor.COLOR_FOREGROUND, Styles.EditorColor.COLOR_ALIGNER_NEEDSREVIEW, false, true },
            { res.getString("ctm.dialog.otherpane.row16"), Styles.EditorColor.COLOR_FOREGROUND, Styles.EditorColor.COLOR_ALIGNER_TABLE_ROW_HIGHLIGHT, false, true },
            { res.getString("ctm.dialog.otherpane.row17"), Styles.EditorColor.COLOR_FOREGROUND, Styles.EditorColor.COLOR_MACHINETRANSLATE_SELECTED_HIGHLIGHT, false, true },
            { res.getString("ctm.dialog.otherpane.row18"), Styles.EditorColor.COLOR_FOREGROUND, Styles.EditorColor.COLOR_NOTIFICATION_MIN, false, true },
            { res.getString("ctm.dialog.otherpane.row19"), Styles.EditorColor.COLOR_FOREGROUND, Styles.EditorColor.COLOR_NOTIFICATION_MAX, false, true },
    };
    @Override
    public int getRowCount() { return ROWS.length; }
    @Override
    public int getColumnCount() { return 1; }
    @Override
    public Object getValueAt(int row, int col) { return ROWS[row][0]; }
    public Styles.EditorColor getFgColorAt(int row) { return (Styles.EditorColor) ROWS[row][1]; }
    public Styles.EditorColor getBgColorAt(int row) { return (Styles.EditorColor) ROWS[row][2]; }
    public boolean isBgMenuDisabledRow(int row) { return (Boolean) ROWS[row][3]; }
    public boolean isFgMenuDisabledRow(int row) { return (Boolean) ROWS[row][4]; }
    public boolean isVisible(int row, int col) { return col == 0; }
}
