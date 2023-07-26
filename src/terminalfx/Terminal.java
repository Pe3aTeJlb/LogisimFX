package terminalfx;

import pty4j.PtyProcess;
import pty4j.PtyProcessBuilder;
import pty4j.WinSize;
import terminalfx.annotation.WebkitCall;
import terminalfx.config.TerminalConfig;
import terminalfx.helper.ThreadHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public class Terminal extends TerminalView {

	private static int terminalsCount = 0;

	private PtyProcess process;
	private final ObjectProperty<Writer> outputWriterProperty;
	private final Path terminalPath;
	private String[] termCommand;
	private final LinkedBlockingQueue<String> commandQueue;

	public Terminal() {
		this(null, null);
	}

	public Terminal(TerminalConfig terminalConfig, Path terminalPath) {
		super();
		terminalsCount += 1;
		setTerminalConfig(terminalConfig);
		this.terminalPath = terminalPath;
		outputWriterProperty = new SimpleObjectProperty<>();
		commandQueue = new LinkedBlockingQueue<>();
	}

	@WebkitCall
	public void command(String command) {
		try {
			commandQueue.put(command);
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
		ThreadHelper.start(() -> {
			try {
				final String commandToExecute = commandQueue.poll();
				getOutputWriter().write(commandToExecute);
				getOutputWriter().flush();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void onTerminalReady() {
		ThreadHelper.start(() -> {
			try {
				initializeProcess();
			} catch (final Exception e) {
			}
		});
	}

	private void initializeProcess() throws Exception {

		if (SystemUtils.IS_OS_WINDOWS) {
			this.termCommand = getTerminalConfig().getWindowsTerminalStarter().split("\\s+");
		} else {
			this.termCommand = getTerminalConfig().getUnixTerminalStarter().split("\\s+");
		}

		final Map<String, String> envs = new HashMap<>(System.getenv());
		envs.put("TERM", "xterm");

		if (Objects.nonNull(terminalPath) && Files.exists(terminalPath)) {
			this.process = new PtyProcessBuilder().setCommand(termCommand).setRedirectErrorStream(true).setEnvironment(envs).setDirectory(terminalPath.toString()).start();
		} else {
			this.process = new PtyProcessBuilder().setCommand(termCommand).setRedirectErrorStream(true).setEnvironment(envs).start();
		}

		columnsProperty().addListener(evt -> updateWinSize());
		rowsProperty().addListener(evt -> updateWinSize());
		updateWinSize();
		Charset defaultCharEncoding = StandardCharsets.UTF_8;
		setInputReader(new BufferedReader(new InputStreamReader(process.getInputStream(), defaultCharEncoding)));
		setErrorReader(new BufferedReader(new InputStreamReader(process.getErrorStream(), defaultCharEncoding)));
		setOutputWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), defaultCharEncoding)));
		focusCursor();

		countDownLatch.countDown();

		process.waitFor();
	}

	public Path getTerminalPath() {
		return terminalPath;
	}

	private void updateWinSize() {
		try {
			process.setWinSize(new WinSize(getColumns(), getRows()));
		} catch (Exception e) {
			//
		}
	}

	public ObjectProperty<Writer> outputWriterProperty() {
		return outputWriterProperty;
	}

	public Writer getOutputWriter() {
		return outputWriterProperty.get();
	}

	public void setOutputWriter(Writer writer) {
		outputWriterProperty.set(writer);
	}

	public PtyProcess getProcess() {
		return process;
	}


	public void terminate(){

		if (process != null) {
			process.destroy();
			if (terminalsCount == 1){
				process.unloadJNALibraries();
			}
			process = null;
			terminalsCount -= 1;
		}

	}

}
