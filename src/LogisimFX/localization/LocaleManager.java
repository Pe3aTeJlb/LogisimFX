package LogisimFX.localization;

import LogisimFX.util.LocaleListener;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.*;

public class LocaleManager {

    private static final ObjectProperty<Locale> locale;
    private static ArrayList<LocaleListener> listeners = new ArrayList<LocaleListener>();
    private static boolean replaceAccents = false;
    private static HashMap<Character,String> repl = null;

    static {
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> setLocale(newValue));
    }


    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale locale) {
        Locale.setDefault(locale);
        localeProperty().set(locale);
        fireLocaleChanged();
    }

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    private static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
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



    public static void setReplaceAccents(boolean value) {
        HashMap<Character,String> newRepl = value ? fetchReplaceAccents() : null;
        replaceAccents = value;
        repl = newRepl;
        fireLocaleChanged();
    }

    private static HashMap<Character,String> fetchReplaceAccents() {
        HashMap<Character,String> ret = null;
        String val;
        try {
            val = LC_util.getInstance().get("accentReplacements");
        } catch (MissingResourceException e) {
            return null;
        }
        StringTokenizer toks = new StringTokenizer(val, "/");
        while (toks.hasMoreTokens()) {
            String tok = toks.nextToken().trim();
            char c = '\0';
            String s = null;
            if (tok.length() == 1) {
                c = tok.charAt(0);
                s = "";
            } else if (tok.length() >= 2 && tok.charAt(1) == ' ') {
                c = tok.charAt(0);
                s = tok.substring(2).trim();
            }
            if (s != null) {
                if (ret == null) ret = new HashMap<>();
                ret.put(c, s);
            }
        }
        return ret;
    }



    public static void addLocaleListener(LocaleListener l) {
        listeners.add(l);
    }

    public static void removeLocaleListener(LocaleListener l) {
        listeners.remove(l);
    }

    private static void fireLocaleChanged() {
        for (LocaleListener l : listeners) {
            l.localeChanged();
        }
    }



    public static StringBinding getLocaleTitle(){
        return Bindings.createStringBinding(() -> locale.getValue().getDisplayName(locale.getValue()), locale);
    }

    public static StringBinding getComplexTitleForLocale(Locale l){
        return Bindings.createStringBinding(() -> l.getDisplayName(l)
                +"/"+l.getDisplayName(Locale.getDefault()), locale);
    }

}
