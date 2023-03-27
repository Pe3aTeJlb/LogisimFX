/*
 * This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
 * License information is located in the Launch file
 */

package LogisimFX.localization;

import LogisimFX.newgui.MainFrame.LC;
import LogisimFX.util.LocaleListener;
import LogisimFX.util.StringUtil;

import docklib.draggabletabpane.DraggableTab;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;

import java.util.*;

import static java.util.Map.entry;

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

    @Override
    public void localeChanged(Locale locale) {
        bundle =  ResourceBundle.getBundle(bundleName, locale);
    }

    public void changeBundle(String bundlename){
        bundleName = "LogisimFX/resources/localization/"+bundlename;
        bundle =  ResourceBundle.getBundle(bundleName, LocaleManager.getLocale());
    }


    public StringBinding createStringBinding(final String key) {
        return Bindings.createStringBinding(() -> get(key), LocaleManager.localeProperty());
    }

    public StringBinding createComplexStringBinding(final String key, String... strings) {
        return Bindings.createStringBinding(() -> StringUtil.format(get(key),strings), LocaleManager.localeProperty());
    }

    public StringBinding castToBind(final String string){
        return Bindings.createStringBinding(() -> string);
    }

    public String get(final String key) {

        //System.out.println("from "+this.hashCode()+" find key " + key + " in " + bundle.getBaseBundleName() + " " + bundleName);

        return bundle.getString(key);

    }

    public String getFormatted(final String key, String... strings) {
        return StringUtil.format(get(key),strings);
    }

    public static void initExtrenalLibrariesLocalization(){
        DraggableTab.setLocalizationPack(
                Map.ofEntries(
                        entry("dockPinnedItem", LC.createStringBinding("dockPinnedItem")),
                        entry("floatItem", LC.createStringBinding("floatItem")),
                        entry("windowItem", LC.createStringBinding("windowItem")),
                        entry("closeItem", LC.createStringBinding("closeItem")),
                        entry("closeOthersItem", LC.createStringBinding("closeOthersItem")),
                        entry("closeAllItems", LC.createStringBinding("closeAllItems")),
                        entry("closeToTheLeftItem", LC.createStringBinding("closeToTheLeftItem")),
                        entry("closeToTheRightItem", LC.createStringBinding("closeToTheRightItem")),
                        entry("splitVerticallyItem", LC.createStringBinding("splitVerticallyItem")),
                        entry("splitHorizontallyItem", LC.createStringBinding("splitHorizontallyItem")),
                        entry("selectNextTabItem", LC.createStringBinding("selectNextTabItem")),
                        entry("selectPreviousTabItem", LC.createStringBinding("selectPreviousTabItem")
                        )
                ));
    }
    
}