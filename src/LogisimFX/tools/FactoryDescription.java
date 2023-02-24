/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.tools;

import java.util.Arrays;
import java.util.List;

import LogisimFX.IconsManager;
import LogisimFX.comp.ComponentFactory;
import LogisimFX.std.LC;
import javafx.beans.binding.StringBinding;
import javafx.scene.image.ImageView;

/** This class allows an object to be created holding all the information
 * essential to showing a ComponentFactory in the explorer window, but without
 * actually loading the ComponentFactory unless a program genuinely gets around
 * to needing to use it. Note that for this to work, the relevant
 * ComponentFactory class must be in the same package as its Library class,
 * the ComponentFactory class must be public, and it must include a public
 * no-arguments constructor.
 */
public class FactoryDescription {

	public static List<Tool> getTools(Class<? extends Library> base,
                                      FactoryDescription[] descriptions) {
		Tool[] tools = new Tool[descriptions.length];
		for (int i = 0; i < tools.length; i++) {
			tools[i] = new AddTool(base, descriptions[i]);
			//tools[i].checkForFPGASupport();
		}
		return Arrays.asList(tools);
	}
	
	private String name;
	private StringBinding displayName;
	private String iconName;
	private boolean iconLoadAttempted;
	private ImageView icon;
	private String factoryClassName;
	private boolean factoryLoadAttempted;
	private ComponentFactory factory;
	private StringBinding toolTip;
	
	public FactoryDescription(String name, StringBinding displayName,
			String iconName, String factoryClassName) {
		this(name, displayName, factoryClassName);
		this.iconName = iconName;
		this.iconLoadAttempted = false;
		this.icon = IconsManager.getIcon(iconName);
	}
	
	public FactoryDescription(String name, StringBinding displayName,
							  ImageView icon, String factoryClassName) {
		this(name, displayName, factoryClassName);
		this.iconName = "???";
		this.iconLoadAttempted = true;
		this.icon = icon;
	}
	
	public FactoryDescription(String name, StringBinding displayName,
			String factoryClassName) {
		this.name = name;
		this.displayName = displayName;
		this.iconName = "???";
		this.iconLoadAttempted = true;
		this.icon = null;
		this.factoryClassName = factoryClassName;
		this.factoryLoadAttempted = false;
		this.factory = null;
		this.toolTip = null;
	}
	
	public String getName() {
		return name;
	}
	
	public StringBinding getDisplayName() {
		return displayName == null ? LC.castToBind(getName()) : displayName;
	}
	
	public boolean isFactoryLoaded() {
		return factoryLoadAttempted;
	}
	
	public ImageView getIcon() {

		ImageView ret = icon;

		if (ret == null && !iconLoadAttempted) {
			ret = IconsManager.getIcon(iconName);
			icon = ret;
			iconLoadAttempted = true;
		}
		return ret;

	}
	
	public ComponentFactory getFactory(Class<? extends Library> libraryClass) {
		ComponentFactory ret = factory;
		if (factory != null || factoryLoadAttempted) {
			return ret;
		} else {
			String msg = "";
			try {
				msg = "getting class loader";
				ClassLoader loader = libraryClass.getClassLoader();
				msg = "getting package name";
				String name;
				Package pack = libraryClass.getPackage();
				if (pack == null) {
					name = factoryClassName;
				} else {
					name = pack.getName() + "." + factoryClassName;
				}
				msg = "loading class";
				Class<?> factoryClass = loader.loadClass(name);
				msg = "creating instance";
				Object factoryValue = factoryClass.newInstance();
				msg = "converting to factory";
				if (factoryValue instanceof ComponentFactory) {
					ret = (ComponentFactory) factoryValue;
					factory = ret;
					factoryLoadAttempted = true;
					return ret;
				}
			} catch (Throwable t) {
				String name = t.getClass().getName();
				String m = t.getMessage();
				if (m != null) msg = msg + ": " + name + ": " + m;
				else msg = msg + ": " + name;
			}
			System.err.println("error while " + msg); //OK
			factory = null;
			factoryLoadAttempted = true;
			return null;
		}
	}
	
	public FactoryDescription setToolTip(StringBinding getter) {
		toolTip = getter;
		return this;
	}
	
	public StringBinding getToolTip() {
		StringBinding getter = toolTip;
		return getter == null ? null : getter;
	}

}
