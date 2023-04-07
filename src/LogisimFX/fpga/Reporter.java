/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package LogisimFX.fpga;

import LogisimFX.Startup;
import LogisimFX.fpga.designrulecheck.SimpleDrcContainer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reporter {

	public static final Reporter report = new Reporter();
	private static final Logger logger = LoggerFactory.getLogger(Reporter.class);
	private static final ObservableList<Object> msgs = FXCollections.observableArrayList();

	public ObservableList<Object> getMessages(){
		return msgs;
	}

	public void addErrorIncrement(String message) {
		if (Startup.isTty) {
			logger.error(message);
		} else {
			msgs.add(new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_NORMAL, true));
		}
	}

	public void addError(Object message) {
		System.out.println("Error ! " + message);
		if (Startup.isTty) {
			if (message instanceof String) logger.error((String) message);
		} else {
			msgs.add((message instanceof String)
					? new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_NORMAL)
					: message);
		}
	}

	public void addFatalErrorFmt(String fmt, Object... args) {
		addFatalError(String.format(fmt, args));
	}

	public void addFatalError(String message) {
		System.out.println("Fatal ! " + message);
		if (Startup.isTty) {
			logger.error(message);
		} else {
			msgs.add(new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_FATAL));
		}
	}

	public void addSevereError(String message) {
		if (Startup.isTty) {
			logger.error(message);
		} else {
			msgs.add(new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_SEVERE));
		}
	}

	public void addInfo(String message) {
		System.out.println("Info " + message);
		if (Startup.isTty) {
			logger.info(message);
		} else {
			msgs.add(message);
		}
	}

	public void addSevereWarning(String message) {
		System.out.println("Severe ! " + message);
		if (Startup.isTty) {
			logger.warn(message);
		} else {
			msgs.add(new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_SEVERE));
		}
	}

	public void addWarningIncrement(String message) {
		if (Startup.isTty) {
			logger.warn(message);
		} else {
			msgs.add(new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_NORMAL, true));
		}
	}

	public void addWarning(Object message) {
		System.out.println("Warn ! " + message);
		if (Startup.isTty) {
			if (message instanceof String) logger.warn((String) message);
		} else {
			msgs.add(message instanceof String
					? new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_NORMAL)
					: message);
		}
	}

	public void clearConsole() {
		msgs.clear();
	}

	public void print(String message) {
		if (Startup.isTty) {
			logger.info(message);
		} else {
			msgs.add(message);
		}
	}

}
