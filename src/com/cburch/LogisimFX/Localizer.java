package com.cburch.LogisimFX;

import com.cburch.logisim.util.StringUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.util.*;

public class Localizer {

    private final String bundleName;

    public Localizer(String bundlename){
        bundleName = "com/cburch/"+bundlename;
    }

    private static final ObjectProperty<Locale> locale;

    static {
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> setLocale(newValue));
    }

    private static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale locale) {
        Locale.setDefault(locale);
        localeProperty().set(locale);
    }

    private static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        System.out.println(getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH);
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }

    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(
                Locale.ENGLISH,
                new Locale("ru","RU"),
                new Locale("de","DE"),
                new Locale("el","EL"),
                new Locale("es","ES"),
                new Locale("pt","PT")
        ));
    }

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public StringBinding createStringBinding(final String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), locale);
    }

    public String createComplexString(final String key, String... strings){
        return StringUtil.format(get(key),strings);
    }

    public StringBinding createComplexStringBinding(final String key, String... strings) {
        return Bindings.createStringBinding(() -> StringUtil.format(get(key),strings), locale);
    }

    public String get(final String key, final Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, getLocale());
        return MessageFormat.format(bundle.getString(key), args);
    }

}