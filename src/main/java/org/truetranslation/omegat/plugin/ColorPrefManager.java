package org.truetranslation.omegat.plugin;

import org.omegat.util.Preferences;
import org.omegat.util.gui.Styles;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ColorPrefManager {
    private final Map<Styles.EditorColor, Color> actualFg = new EnumMap<>(Styles.EditorColor.class);
    private final Map<Styles.EditorColor, Color> defaultFg = new EnumMap<>(Styles.EditorColor.class);
    private final Map<Styles.EditorColor, Color> actualBg = new EnumMap<>(Styles.EditorColor.class);
    private final Map<Styles.EditorColor, Color> defaultBg = new EnumMap<>(Styles.EditorColor.class);

    private static final Set<Styles.EditorColor> fgColors = EnumSet.of(
            Styles.EditorColor.COLOR_ACTIVE_SOURCE_FG, Styles.EditorColor.COLOR_ACTIVE_TARGET_FG, Styles.EditorColor.COLOR_BIDIMARKERS,
            Styles.EditorColor.COLOR_FOREGROUND, Styles.EditorColor.COLOR_GLOSSARY_NOTE, Styles.EditorColor.COLOR_GLOSSARY_SOURCE,
            Styles.EditorColor.COLOR_GLOSSARY_TARGET, Styles.EditorColor.COLOR_HYPERLINK, Styles.EditorColor.COLOR_LANGUAGE_TOOLS,
            Styles.EditorColor.COLOR_MATCHES_CHANGED, Styles.EditorColor.COLOR_MATCHES_DEL_ACTIVE, Styles.EditorColor.COLOR_MATCHES_DEL_INACTIVE,
            Styles.EditorColor.COLOR_MATCHES_INS_ACTIVE, Styles.EditorColor.COLOR_MATCHES_INS_INACTIVE, Styles.EditorColor.COLOR_MATCHES_UNCHANGED,
            Styles.EditorColor.COLOR_MOD_INFO_FG, Styles.EditorColor.COLOR_NBSP, Styles.EditorColor.COLOR_NON_UNIQUE,
            Styles.EditorColor.COLOR_NOTED_FG, Styles.EditorColor.COLOR_PARAGRAPH_START, Styles.EditorColor.COLOR_PLACEHOLDER,
            Styles.EditorColor.COLOR_REMOVETEXT_TARGET, Styles.EditorColor.COLOR_SEARCH_FOUND_MARK, Styles.EditorColor.COLOR_SEARCH_REPLACE_MARK,
            Styles.EditorColor.COLOR_SEGMENT_MARKER_FG, Styles.EditorColor.COLOR_SOURCE_FG, Styles.EditorColor.COLOR_SPELLCHECK,
          Styles.EditorColor.COLOR_TRANSLATED_FG, Styles.EditorColor.COLOR_TRANSTIPS,
            Styles.EditorColor.COLOR_UNTRANSLATED_FG, Styles.EditorColor.COLOR_WHITESPACE
    );

    private static final Set<Styles.EditorColor> bgColors = EnumSet.of(
            Styles.EditorColor.COLOR_ACTIVE_SOURCE, Styles.EditorColor.COLOR_ACTIVE_TARGET, Styles.EditorColor.COLOR_ALIGNER_ACCEPTED,
            Styles.EditorColor.COLOR_ALIGNER_HIGHLIGHT, Styles.EditorColor.COLOR_ALIGNER_NEEDSREVIEW, Styles.EditorColor.COLOR_ALIGNER_TABLE_ROW_HIGHLIGHT,
            Styles.EditorColor.COLOR_BACKGROUND, Styles.EditorColor.COLOR_MACHINETRANSLATE_SELECTED_HIGHLIGHT, Styles.EditorColor.COLOR_TERMINOLOGY,
            Styles.EditorColor.COLOR_MARK_COMES_FROM_TM_X100PC, Styles.EditorColor.COLOR_MARK_COMES_FROM_TM_XAUTO,
            Styles.EditorColor.COLOR_MARK_COMES_FROM_TM_XENFORCED, Styles.EditorColor.COLOR_MARK_COMES_FROM_TM_XICE, Styles.EditorColor.COLOR_MOD_INFO,
            Styles.EditorColor.COLOR_NON_UNIQUE_BG, Styles.EditorColor.COLOR_NOTED, Styles.EditorColor.COLOR_NOTIFICATION_MAX,
            Styles.EditorColor.COLOR_NOTIFICATION_MIN, Styles.EditorColor.COLOR_REPLACE, Styles.EditorColor.COLOR_SEGMENT_MARKER_BG,
            Styles.EditorColor.COLOR_SOURCE, Styles.EditorColor.COLOR_TRANSLATED, Styles.EditorColor.COLOR_UNTRANSLATED
    ); // moved out Styles.EditorColor.COLOR_MARK_ALT_TRANSLATION, Styles.EditorColor.COLOR_MARK_COMES_FROM_TM_MT 

    public ColorPrefManager() {
        // Populate actual and default colors
        for (Styles.EditorColor ec : Styles.EditorColor.values()) {
            Color c = ec.getColor();
            Color d = ec.getDefault();
            if (fgColors.contains(ec)) {
                if (c != null) actualFg.put(ec, c);
                if (d != null) defaultFg.put(ec, d);
                if (!actualFg.containsKey(ec) && d != null) actualFg.put(ec, d);
            } else if (bgColors.contains(ec)) {
                if (c != null) actualBg.put(ec, c);
                if (d != null) defaultBg.put(ec, d);
                if (!actualBg.containsKey(ec) && d != null) actualBg.put(ec, d);
            }
        }
        // FG fallback: COLOR_FOREGROUND; BG fallback: COLOR_BACKGROUND
        Color fgDefault = defaultFg.getOrDefault(Styles.EditorColor.COLOR_FOREGROUND, Color.BLACK);
        Color bgDefault = defaultBg.getOrDefault(Styles.EditorColor.COLOR_BACKGROUND, Color.WHITE);
        for (Styles.EditorColor ec : fgColors) {
            if (!actualFg.containsKey(ec)) actualFg.put(ec, fgDefault);
            if (!defaultFg.containsKey(ec)) defaultFg.put(ec, fgDefault);
        }
        for (Styles.EditorColor ec : bgColors) {
            if (!actualBg.containsKey(ec)) actualBg.put(ec, bgDefault);
            if (!defaultBg.containsKey(ec)) defaultBg.put(ec, bgDefault);
        }
    }

    public void setActual(Styles.EditorColor ec, Color c, boolean isFg) {
        if (isFg) { actualFg.put(ec, c); } else { actualBg.put(ec, c); }
    }
    public Color getActual(Styles.EditorColor ec, boolean isFg) {
        return isFg ? actualFg.get(ec) : actualBg.get(ec);
    }
    public Color getDefault(Styles.EditorColor ec, boolean isFg) {
        return isFg ? defaultFg.get(ec) : defaultBg.get(ec);
    }

    public void saveAll() {
        // Save actual values back to Preferences
        for (Styles.EditorColor ec : fgColors) {
            Preferences.setPreference(ec.name(), colorToHex(actualFg.get(ec)));
        }
        for (Styles.EditorColor ec : bgColors) {
            Preferences.setPreference(ec.name(), colorToHex(actualBg.get(ec)));
        }
    }

    public void exportToFile(File f,
        String fontName,
        String fontSize,
        String sourceSample,
        String targetSample,
        String paraDelimiter
    ) throws IOException {
        Properties props = new Properties();
        for (Styles.EditorColor ec : fgColors) {
            props.setProperty(ec.name(), colorToHex(actualFg.get(ec)));
        }
        for (Styles.EditorColor ec : bgColors) {
            props.setProperty(ec.name(), colorToHex(actualBg.get(ec)));
        }
        // Add font and font size
        props.setProperty("source_font", fontName);
        props.setProperty("source_font_size", fontSize);
    
        // Add preview texts and delimiter
        props.setProperty("source_sample_input", sourceSample);
        props.setProperty("target_sample_input", targetSample);
        props.setProperty(Preferences.MARK_PARA_TEXT, paraDelimiter);
        
        try (Writer out = new OutputStreamWriter(new FileOutputStream(f), "UTF-8")) {
            props.store(out, "OmegaT Color Theme");
        }
    }

    public Map<String, String> importFromFile(File f) throws IOException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(f);
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                props.load(reader);
            }
        // ... import color keys as before, e.g.
        for (Styles.EditorColor ec : Styles.EditorColor.values()) {
            String fg = props.getProperty(ec.name());
            if (fg != null) actualFg.put(ec, hexToColor(fg));
            String bg = props.getProperty(ec.name());
            if (bg != null) actualBg.put(ec, hexToColor(bg));
        }

        // Read extra UI values
        Map<String, String> extras = new HashMap<>();
        if (props.containsKey("source_font"))
            extras.put("source_font", props.getProperty("source_font"));
        if (props.containsKey("source_font_size"))
            extras.put("source_font_size", props.getProperty("source_font_size"));
        if (props.containsKey("source_sample_input"))
            extras.put("source_sample_input", props.getProperty("source_sample_input"));
        if (props.containsKey("target_sample_input"))
            extras.put("target_sample_input", props.getProperty("target_sample_input"));
        if (props.containsKey(Preferences.MARK_PARA_TEXT))
            extras.put(Preferences.MARK_PARA_TEXT, props.getProperty(Preferences.MARK_PARA_TEXT));
    
        return extras;
    }

    private static String colorToHex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static Color hexToColor(String s) {
        if (s.startsWith("#")) s = s.substring(1);
        return new Color(Integer.parseInt(s,16));
    }
}
