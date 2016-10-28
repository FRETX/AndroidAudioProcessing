package rocks.fretx.audioprocessing;

/**
 * Created by Onur Babacan on 28-Oct-16.
 */

public class MusicUtils {
	public static final String[] ALL_ROOT_NOTES = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "G", "G#"};
	public static final String[] ALL_CHORD_TYPES = {"maj", "m", "maj7", "m7", "5", "7", "9", "sus2", "sus4", "7sus4", "7#9", "add9", "aug", "dim", "dim7"};
	//obviously not all possible chord types, just ones we're considering at the moment

	public static int noteNameToSemitoneNumber(String name) {
		String newName = name;
		if (name.length() == 2) {
			if (name.charAt(1) == 'b') {
				switch (name.charAt(0)) {
					case 'A':
						newName = "G#";
						break;
					case 'B':
						newName = "A#";
						break;
					case 'D':
						newName = "C#";
						break;
					case 'E':
						newName = "D#";
						break;
					case 'G':
						newName = "F#";
						break;
					//This shouldn't happen
					default:
						break;
				}
			}
		}
		int semitone = 0;
		switch (newName) {
			case "A":
				semitone = 1;
				break;
			case "A#":
				semitone = 2;
				break;
			case "B":
				semitone = 3;
				break;
			case "C":
				semitone = 4;
				break;
			case "C#":
				semitone = 5;
				break;
			case "D":
				semitone = 6;
				break;
			case "D#":
				semitone = 7;
				break;
			case "E":
				semitone = 8;
				break;
			case "F":
				semitone = 9;
				break;
			case "F#":
				semitone = 10;
				break;
			case "G":
				semitone = 11;
				break;
			case "G#":
				semitone = 12;
				break;
			default:
				semitone = 0;
		}
		return semitone;
	}

	public static String semitoneNumberToNoteName(int number) {
		switch (number) {
			case 1:
				return "A";
			case 2:
				return "A#";
			case 3:
				return "B";
			case 4:
				return "C";
			case 5:
				return "C#";
			case 6:
				return "D";
			case 7:
				return "D#";
			case 8:
				return "E";
			case 9:
				return "F";
			case 10:
				return "F#";
			case 11:
				return "G";
			case 12:
				return "G#";
			//This shouldn't happen
			default:
				return "NONE";
		}
	}

	public static double hzToMidiNote(double hertz) {
		return 69 + (12 * Math.log(hertz / 440) / Math.log(2));
	}

	public static double midiNoteToHz(int midiNote){
		return 440 * Math.pow(2, ((float)midiNote - 69) / 12 );
	}

	public static String midiNoteToName(int midiNote) {
		String[] noteString = new String[]{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
		int octave = (midiNote / 12) - 1;
		int noteIndex = (midiNote % 12);
		return (noteString[noteIndex] + Integer.toString(octave));
	}

	public static FretboardPosition midiNoteToFretboardPosition(int note) {
		if (note < 40 || note > 68) {
			throw new IllegalArgumentException("This note is outside the display range of FretX");
		}
		if (note > 59) {
			note++;
		}
		int fret = (note - 40) % 5;
		int string = 6 - ((note - 40) / 5);
		//This formula always prefers the open 2nd string to the 4th fret of the 3rd string
		return new FretboardPosition(string, fret);
	}

	public static double hzToCent(double hz){
		return (1200 * Math.log(hz) / Math.log(2));
	}

	public static double centToHz(double cent){
		return Math.pow(2,cent/1200);
	}

	public static double frequencyFromInterval(double baseNote, int intervalInSemitones) {
		return baseNote * Math.pow(2, (double) intervalInSemitones / 12);
	}

	public enum TuningName {STANDARD,DROP_D}; //to be expanded later on

	public static int[] getTuningMidiNotes(TuningName tuning){
		int[] tuningNotes = new int[6];
		switch (tuning){
			case STANDARD:
				tuningNotes = new int[] {40, 45, 50, 55, 59, 64};
				break;
			case DROP_D:
				tuningNotes = new int[]{38, 45, 50, 55, 59, 64};
				break;
		}
		return tuningNotes;
	}
}
