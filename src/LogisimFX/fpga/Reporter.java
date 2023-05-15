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
import LogisimFX.newgui.MainFrame.EditorTabs.TerminalTab.Terminal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reporter {

	public static final Reporter report = new Reporter();
	private static final Logger logger = LoggerFactory.getLogger(Reporter.class);
	private Terminal terminal;

	public void setTerminal(Terminal terminal){
		this.terminal = terminal;
	}



	public void addInfo(String message) {
		if (Startup.isTty) {
			logger.info(message);
		} else {
			terminal.printInfo(message);
		}
	}



	public void addSevereWarning(String message) {
		if (Startup.isTty) {
			logger.warn(message);
		} else {
			terminal.printWarning(new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_SEVERE));
		}
	}

	public void addWarningIncrement(String message) {
		if (Startup.isTty) {
			logger.warn(message);
		} else {
			terminal.printWarning(new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_NORMAL, true));
		}
	}

	public void addWarning(Object message) {
		if (Startup.isTty) {
			if (message instanceof String) logger.warn((String) message);
		} else {
			terminal.printWarning(message instanceof String
					? new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_NORMAL)
					: (SimpleDrcContainer) message);
		}
	}



	public void addErrorIncrement(String message) {
		if (Startup.isTty) {
			logger.error(message);
		} else {
			terminal.printError(new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_NORMAL, true));
		}
	}

	public void addError(Object message) {
		if (Startup.isTty) {
			if (message instanceof String) logger.error((String) message);
		} else {
			terminal.printError((message instanceof String)
					? new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_NORMAL)
					: (SimpleDrcContainer)message);
		}
	}

	public void addFatalErrorFmt(String fmt, Object... args) {
		addFatalError(String.format(fmt, args));
	}

	public void addFatalError(String message) {
		if (Startup.isTty) {
			logger.error(message);
		} else {
			terminal.printError(new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_FATAL));
		}
	}

	public void addSevereError(String message) {
		if (Startup.isTty) {
			logger.error(message);
		} else {
			terminal.printWarning(new SimpleDrcContainer(message, SimpleDrcContainer.LEVEL_SEVERE));
		}
	}



	public void clearConsole() {
		terminal.clearTerminal();
	}

}
