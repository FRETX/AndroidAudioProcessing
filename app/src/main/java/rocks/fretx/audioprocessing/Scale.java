package rocks.fretx.audioprocessing;

import android.util.Log;

import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by onurb_000 on 12/12/16.
 */

public class Scale {
	public static final String[] ALL_ROOT_NOTES = {"A", "Bb", "B", "C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#"};
	public static final String[] ALL_SCALE_TYPES = {"Major","Minor","Major Pentatonic","Minor Pentatonic","Blues","Harmonic Minor","Melodic Minor","Ionian","Dorian","Phrygian","Lydian","Mixolydian","Aeolian","Locrian","Whole Tone"};

	protected String root;
	protected String type;
	private int rootNoteMidi;
	protected int lowerBoundMidiNote = 40;
	protected int upperBoundMidiNote = 68;
	protected int[] notes;
	protected ArrayList<FretboardPosition> fretboardPositions = new ArrayList<FretboardPosition>();
	private int[] scaleFormula;

	public Scale(String r , String t){
//		r = MusicUtils.validateNoteName(r);
//		if(r == null){
//			Log.e("Scale","Input root name is invalid");
//			return;
//		}
		root = r;
		type = t;
		switch (type){
			case "Major":
				scaleFormula = new int[] {2,2,1,2,2,2,1};
				break;
			case "Minor":
				scaleFormula = new int[] {2,1,2,2,1,2,2};
				break;
			case "Major Pentatonic":
				scaleFormula = new int[] {2,2,3,2,3};
				break;
			case "Minor Pentatonic":
				scaleFormula = new int[] {3,2,2,3,2};
				break;
			case "Blues":
				scaleFormula = new int[] {3,2,1,1,3,2};
				break;
			case "Harmonic Minor":
				scaleFormula = new int[] {2,1,2,2,1,3,2};
				break;
			case "Melodic Minor":
				scaleFormula = new int[] {2,1,2,2,2,2,1};
				break;
			case "Ionian":
				scaleFormula = new int[] {2,2,1,2,2,2,1};
				break;
			case "Dorian":
				scaleFormula = new int[] {2,1,2,2,2,1,2};
				break;
			case "Phrygian":
				scaleFormula = new int[] {1,2,2,2,1,2,2};
				break;
			case "Lydian":
				scaleFormula = new int[] {2,2,2,1,2,2,1};
				break;
			case "Mixolydian":
				scaleFormula = new int[] {2,2,1,2,2,1,2};
				break;
			case "Aeolian":
				scaleFormula = new int[] {2,1,2,2,1,2,2};
				break;
			case "Locrian":
				scaleFormula = new int[] {1,2,2,1,2,2,2};
				break;
			case "Whole Tone":
				scaleFormula = new int[] {2,2,2,2,2,2};
				break;
			default:
				type = null;
				scaleFormula = null;
				break;
		}

//		if(type == null){
//			Log.e("Scale","Input chord type is invalid");
//			return;
//		}

		Log.d("scale root: ", this.root);
		Log.d("scale type: ", this.type);
		Log.d("scaleFormula: ", scaleFormula.toString());

		int[] rootNotesMidi = MusicUtils.noteNameToMidiNotes(root);

		for (int i = rootNotesMidi.length-1; i >= 0 ; i--) {
			if(rootNotesMidi[i] <= upperBoundMidiNote && rootNotesMidi[i] >= lowerBoundMidiNote){
				rootNoteMidi = rootNotesMidi[i];
			}
		}

		Log.d("scale lowest root note:" , Integer.toString(rootNoteMidi));

		int octavesToGenerate = (int) Math.ceil((float) (upperBoundMidiNote - rootNoteMidi) / 12);

		int semitonesInPartialOctave = upperBoundMidiNote - (rootNoteMidi + (octavesToGenerate-1)*12);
		int[] cumulativeIntervals = new int[scaleFormula.length];
		cumulativeIntervals[0] = scaleFormula[0];
		for (int i = 1; i < scaleFormula.length; i++) {
			cumulativeIntervals[i] = cumulativeIntervals[i-1] + scaleFormula[i];
		}
		int nNotesInPartialOctave = 0;
		for (int i = 0; i < cumulativeIntervals.length; i++) {
			if(cumulativeIntervals[i] <= semitonesInPartialOctave ){
				nNotesInPartialOctave = i+1;
			}
		}

		int tmpNotesSize = (octavesToGenerate-1)*12 + nNotesInPartialOctave;
		int[] tmpNotes = new int[tmpNotesSize];

		Log.d("tmpNotes size: ", Integer.toString(tmpNotesSize));

		tmpNotes[0] = rootNoteMidi;

		Log.d("scaleFormula: ", Arrays.toString(scaleFormula));
		int i = 1;
		int scaleFormulaIndex = 0;
		while (i < tmpNotes.length) {
			tmpNotes[i] = tmpNotes[i-1] + scaleFormula[scaleFormulaIndex];
			i++;
			scaleFormulaIndex++;
			if (scaleFormulaIndex == scaleFormula.length) scaleFormulaIndex = 0;
		}

		Log.d("tmpNotes: ", Arrays.toString(tmpNotes));

		notes = tmpNotes;

		Log.d("notes length: ",Integer.toString(notes.length));

		FretboardPosition fp;
		Log.d("Scale: ","initialized fp");
		for (int j = 0; j < notes.length; j++) {
			fp = MusicUtils.midiNoteToFretboardPosition(notes[j]);
			if(fp.string == 2 && fp.fret == 0){
				fp.setString(3);
				fp.setFret(4);
			}
			Log.d("fp: ", fp.toString());
			fretboardPositions.add(fp);
		}
	}

	public String toString(){
		return root + " " + type;
	}

	public void setLowerBound(int midiNote){
		lowerBoundMidiNote = midiNote;
	}
	public void setUpperBound(int midiNote){
		upperBoundMidiNote = midiNote;
	}
	public int getLowerBound(){
		return lowerBoundMidiNote;
	}
	public int getUpperBound(){
		return upperBoundMidiNote;
	}

	public String getRoot(){
		return root;
	}

	public String getType(){
		return type;
	}

	public ArrayList<FretboardPosition> getFretboardPositions(){
		return fretboardPositions;
	}
	public int[] getNotes(){
		return notes;
	}
}
