package LogisimFX.localization;

import LogisimFX.util.LocaleListener;
import LogisimFX.util.StringUtil;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;

import java.util.*;

public class Localizer implements LocaleListener{

    private String bundleName;
    private ResourceBundle bundle;
    public static Boolean debug = false;

    public Localizer(String bundlename){
        if(debug)System.out.println("create from "+this.hashCode()+" "+bundlename);
        LocaleManager.addLocaleListener(this);
        bundleName = "LogisimFX/resources/localization/"+bundlename;
        bundle =  ResourceBundle.getBundle(bundleName, LocaleManager.getLocale());
    }

    @Override
    public void localeChanged() {
        bundle =  ResourceBundle.getBundle(bundleName, LocaleManager.getLocale());
    }

    public void changeBundle(String bundlename){
        bundleName = "LogisimFX/resources/localization/"+bundlename;
        bundle =  ResourceBundle.getBundle(bundleName, LocaleManager.getLocale());
    }


    public StringBinding createStringBinding(final String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), LocaleManager.localeProperty());
    }

    public StringBinding createComplexStringBinding(final String key, String... strings) {
        return Bindings.createStringBinding(() -> StringUtil.format(get(key),strings), LocaleManager.localeProperty());
    }

    public StringBinding castToBind(final String string){
        return Bindings.createStringBinding(() -> string);
    }

    public String get(final String key, final Object... args) {
        //System.out.println("from "+this.hashCode()+" find key " + key + " in " + bundle.getBaseBundleName() + " " + bundleName);
        return bundle.getString(key);
    }

    public String getFormatted(final String key, String... strings) {
        return StringUtil.format(get(key),strings);
    }

}