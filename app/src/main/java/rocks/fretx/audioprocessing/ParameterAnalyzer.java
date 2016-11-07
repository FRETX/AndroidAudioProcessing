package rocks.fretx.audioprocessing;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kickdrum on 27-Oct-16.
 */

abstract public class ParameterAnalyzer {
	protected double output;
	protected boolean enabled = true;
	private List<ParameterAnalyzer> parameterAnalyzers;

	public ParameterAnalyzer() {
		parameterAnalyzers = new CopyOnWriteArrayList<ParameterAnalyzer>();
	}

	public void process(double input){
		if(enabled){
			internalProcess(input);
			processingFinished();
			sendOutput(output);
		}
	}

	protected abstract void processingFinished();

	protected abstract void internalProcess(double input);

	public void addParameterAnalyzer(final ParameterAnalyzer parameterAnalyzer) {
		parameterAnalyzers.add(parameterAnalyzer);
	}

	public void removeParameterAnalyzer(final ParameterAnalyzer parameterAnalyzer) {
		parameterAnalyzers.remove(parameterAnalyzer);
	}

	private void sendOutput(double output) {
		for (ParameterAnalyzer analyzer : parameterAnalyzers) {
			analyzer.process(output);
		}
	}

	public void enable() {
		enabled = true;
	}

	public void disable() {
		enabled = false;
	}

}
