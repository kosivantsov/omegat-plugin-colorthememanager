package org.truetranslation.omegat.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.omegat.util.gui.Styles;


public class OtherColorsPaneModel extends AbstractTableModel {

    protected static final ResourceBundle res = ResourceBundle.getBundle("ColorThemeManagerPlugin", Locale.getDefault());

    static final Object[][] ROWS = buildRows();
    
    private static Object[][] buildRows() {
        List<Object[]> rowList = new ArrayList<>();
        
        // Rows with BG menu disabled; BG is COLOR_BACKGROUND
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row0", "COLOR_MATCHES_INS_ACTIVE", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row1", "COLOR_MATCHES_DEL_ACTIVE", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row2", "COLOR_MATCHES_INS_INACTIVE", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row3", "COLOR_MATCHES_DEL_INACTIVE", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row4", "COLOR_MATCHES_CHANGED", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row5", "COLOR_MATCHES_UNCHANGED", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row6", "COLOR_GLOSSARY_SOURCE", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row7", "COLOR_GLOSSARY_TARGET", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row8", "COLOR_GLOSSARY_NOTE", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row9", "COLOR_SEARCH_FOUND_MARK", "COLOR_BACKGROUND", true, false);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row10", "COLOR_SEARCH_REPLACE_MARK", "COLOR_BACKGROUND", true, false);
        
        // Rows with FG menu disabled, fg is COLOR_FOREGROUND
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row11", "COLOR_FOREGROUND", "COLOR_REPLACE", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row12", "COLOR_FOREGROUND", "COLOR_TERMINOLOGY", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row13", "COLOR_FOREGROUND", "COLOR_ALIGNER_HIGHLIGHT", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row14", "COLOR_FOREGROUND", "COLOR_ALIGNER_ACCEPTED", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row15", "COLOR_FOREGROUND", "COLOR_ALIGNER_NEEDSREVIEW", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row16", "COLOR_FOREGROUND", "COLOR_ALIGNER_TABLE_ROW_HIGHLIGHT", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row17", "COLOR_FOREGROUND", "COLOR_MACHINETRANSLATE_SELECTED_HIGHLIGHT", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row18", "COLOR_FOREGROUND", "COLOR_NOTIFICATION_MIN", false, true);
        addRowIfColorExists(rowList, "ctm.dialog.otherpane.row19", "COLOR_FOREGROUND", "COLOR_NOTIFICATION_MAX", false, true);
        
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
