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
import LogisimFX.newgui.MainFrame.SystemTabs.TerminalTab.TerminalMessageContainer;
import LogisimFX.newgui.MainFrame.SystemTabs.TerminalTab.Terminal;
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

	public void addInfo(Object message) {
		if (Startup.isTty) {
			if (message instanceof String) logger.info((String) message);
		} else {
			terminal.printInfo(message instanceof String
					? new TerminalMessageContainer(message, TerminalMessageContainer.LEVEL_NORMAL)
					: (TerminalMessageContainer) message);
		}
	}



	public void addSevereWarning(String message) {
		if (Startup.isTty) {
			logger.warn(message);
		} else {
			terminal.printWarning(new TerminalMessageContainer(message, TerminalMessageContainer.LEVEL_SEVERE));
		}
	}

	public void addWarningIncrement(String message) {
		if (Startup.isTty) {
			logger.warn(message);
		} else {
			terminal.printWarning(new TerminalMessageContainer(message, TerminalMessageContainer.LEVEL_NORMAL, true));
		}
	}

	public void addWarning(Object message) {
		if (Startup.isTty) {
			if (message instanceof String) logger.warn((String) message);
		} else {
			terminal.printWarning(message instanceof String
					? new TerminalMessageContainer(message, TerminalMessageContainer.LEVEL_NORMAL)
					: (TerminalMessageContainer) message);
		}
	}



	public void addErrorIncrement(String message) {
		if (Startup.isTty) {
			logger.error(message);
		} else {
			terminal.printError(new TerminalMessageContainer(message, TerminalMessageContainer.LEVEL_NORMAL, true));
		}
	}

	public void addError(Object message) {
		if (Startup.isTty) {
			if (message instanceof String) logger.error((String) message);
		} else {
			terminal.printError((message instanceof String)
					? new TerminalMessageContainer(message, TerminalMessageContainer.LEVEL_NORMAL)
					: (TerminalMessageContainer)message);
		}
	}

	public void addFatalErrorFmt(String fmt, Object... args) {
		addFatalError(String.format(fmt, args));
	}

	public void addFatalError(String message) {
		if (Startup.isTty) {
			logger.error(message);
		} else {
			terminal.printError(new TerminalMessageContainer(message, TerminalMessageContainer.LEVEL_FATAL));
		}
	}

	public void addSevereError(String message) {
		if (Startup.isTty) {
			logger.error(message);
		} else {
			terminal.printWarning(new TerminalMessageContainer(message, TerminalMessageContainer.LEVEL_SEVERE));
		}
	}



	public void clearConsole() {
		terminal.clearTerminal();
	}

}
