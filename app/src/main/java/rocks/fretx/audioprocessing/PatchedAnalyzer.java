package rocks.fretx.audioprocessing;

/**
 * Created by Kickdrum on 27-Oct-16.
 */

abstract public interface PatchedAnalyzer {
	public abstract void process();

	public abstract void processingFinished();
}
