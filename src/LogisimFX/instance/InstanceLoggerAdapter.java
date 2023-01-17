/*
* This file is part of LogisimFX. Copyright (c) 2022, Pplos Studio
* Original code by Carl Burch (http://www.cburch.com), 2011.
* License information is located in the Launch file
*/

package LogisimFX.instance;

import LogisimFX.data.Value;
import LogisimFX.circuit.CircuitState;
import LogisimFX.newgui.WaveformFrame.Loggable;

class InstanceLoggerAdapter implements Loggable {

	private InstanceComponent comp;
	private InstanceLogger logger;
	private InstanceStateImpl state;

	public InstanceLoggerAdapter(InstanceComponent comp, Class<? extends InstanceLogger> loggerClass) {

		try {
			this.comp = comp;
			this.logger = loggerClass.newInstance();
			this.state = new InstanceStateImpl(null, comp);
		} catch (Throwable t) {
			handleError(t, loggerClass);
			logger = null;
		}

	}
	
	private void handleError(Throwable t, Class<? extends InstanceLogger> loggerClass) {

		String className = loggerClass.getName();
		System.err.println("error while instantiating logger " + className //OK
				+ ": " + t.getClass().getName());
		String msg = t.getMessage();
		if (msg != null) System.err.println("  (" + msg + ")"); //OK

	}

	public Object[] getLogOptions(CircuitState circState) {

		if (logger != null) {
			updateState(circState);
			return logger.getLogOptions(state);
		} else {
			return null;
		}

	}

	public String getLogName(Object option) {

		if (logger != null) {
			return logger.getLogName(state, option);
		} else {
			return null;
		}

	}

	public Value getLogValue(CircuitState circuitState, Object option) {

		if (logger != null) {
			updateState(circuitState);
			return logger.getLogValue(state, option);
		} else {
			return Value.UNKNOWN;
		}

	}
	
	private void updateState(CircuitState circuitState) {
		if (state.getCircuitState() != circuitState) {
			state.repurpose(circuitState, comp);
		}
	}

}
