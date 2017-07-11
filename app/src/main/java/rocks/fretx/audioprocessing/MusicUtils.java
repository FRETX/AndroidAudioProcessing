package rocks.fretx.audioprocessing;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Onur Babacan on 28-Oct-16.
 */

public class MusicUtils {
	//These two methods are used in chord recognition and there IS a reason why C!=1 and A==1 instead.
	//Don't touch these two, yo, things will break.
	public static int noteNameToSemitoneNumber(String name) {
//		String name = validateNoteName(name);
		int semitone = 0;
		switch (name) {
			case "A":
				semitone = 1;
				break;
			case "Bb":
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
			case "Eb":
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
				return "Bb";
			case 3:
				return "B";
			case 4:
				return "C";
			case 5:
				return "C#";
			case 6:
				return "D";
			case 7:
				return "Eb";
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
	//Can't touch this
	//Naa na na naa, naaa naa

	public static String validateNoteName(String name){
		Log.d("validating note name",name);
		String newName = name;
		if (name.length() == 2) {
			if (name.charAt(1) == 'b') {
				switch (name.charAt(0)) {
					case 'A':
						newName = "G#";
						break;
					case 'D':
						newName = "C#";
						break;
					case 'G':
						newName = "F#";
						break;
					//This shouldn't happen
					default:
						newName = null;
						break;
				}
			}
			if (name.charAt(1) == '#') {
				switch (name.charAt(0)) {
					case 'A':
						newName = "Bb";
						break;
					case 'D':
						newName = "Eb";
						break;
					//This shouldn't happen
					default:
						newName = null;
						break;
				}
			}
		}
		if(newName == null){
			Log.d("validation returns", "null");
		} else{
			Log.d("validated,returning",newName);
		}
		return newName;
	}

	public static double hzToMidiNote(double hertz) {
		return 69 + (12 * Math.log(hertz / 440) / Math.log(2));
	}

	public static double midiNoteToHz(int midiNote){
		return 440 * Math.pow(2, ((float)midiNote - 69) / 12 );
	}

	public static String midiNoteToName(int midiNote) {
		String[] noteString = new String[]{"C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B"};
		int octave = (midiNote / 12) - 1;
		int noteIndex = (midiNote % 12);
		return (noteString[noteIndex] + Integer.toString(octave));
	}

	public static int[] noteNameToMidiNotes(String name){
//		String name = validateNoteName(name);
		int lowestMidiNote = 0;
		int upperMidiBound = 108;
		switch (name) {
			case "A":
				lowestMidiNote = 21;
				break;
			case "Bb":
				lowestMidiNote = 22;
				break;
			case "B":
				lowestMidiNote = 23;
				break;
			case "C":
				lowestMidiNote = 24;
				break;
			case "C#":
				lowestMidiNote = 25;
				break;
			case "D":
				lowestMidiNote = 26;
				break;
			case "Eb":
				lowestMidiNote = 27;
				break;
			case "E":
				lowestMidiNote = 28;
				break;
			case "F":
				lowestMidiNote = 29;
				break;
			case "F#":
				lowestMidiNote = 30;
				break;
			case "G":
				lowestMidiNote = 31;
				break;
			case "G#":
				lowestMidiNote = 32;
				break;
			default:
				lowestMidiNote = 0;
		}
		int arraySize = (int) Math.floor( (double)(upperMidiBound - lowestMidiNote) / 12 );
		int[] notes = new int[arraySize];
		for (int i = 0; i < arraySize-1; i++) {
			notes[i] = lowestMidiNote + i*12;
		}
		return notes;
	}

	public static FretboardPosition midiNoteToFretboardPosition(int note) {
		if (note < 40) {
			Log.d("MusicUtils","This note is outside the display range of FretX");
			return new FretboardPosition(6,0);
//			throw new IllegalArgumentException("This note is outside the display range of FretX");
		}
		if(note > 68){
			Log.d("MusicUtils","This note is outside the display range of FretX");
			return new FretboardPosition(1,4);
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

	//How to get bytecodes from chord names in the app:
	//HashMap<String,FingerPositions> chordFingerings = MusicUtils.parseChordDb();
	//now keep this chordFingerings object stashed somewhere so you don't parse again everytime you need a fingering
	//byte[] bluetoothArray = MusicUtils.getBluetoothArrayFromChord(chordName,chordFingerings);
	//that's it, now send to BT

	public static HashMap<String,FingerPositions> parseChordDb(){
//		ArrayList<FingerPositions> chordFingerings = new ArrayList<>(0);
		HashMap<String,FingerPositions> chordFingerings = new HashMap<>(0);
		try {
			JSONArray jsonArray = new JSONArray(chordDbJsonString);
			for(int index = 0;index < jsonArray.length(); index++) {
				JSONObject jsonObject = jsonArray.getJSONObject(index);
				FingerPositions fp = new FingerPositions();
				fp.name = jsonObject.getString("name");
				fp.baseFret = jsonObject.getInt("baseFret");
				fp.string1 =  jsonObject.getInt("string1");
				fp.string2 = jsonObject.getInt("string2");
				fp.string3 = jsonObject.getInt("string3");
				fp.string4 = jsonObject.getInt("string4");
				fp.string5 = jsonObject.getInt("string5");
				fp.string6 = jsonObject.getInt("string6");
				chordFingerings.put(fp.name,fp);
			}
		} catch (JSONException e){
			Log.e("Chord DB", e.toString());
		}
		return chordFingerings;
	}

	public static FingerPositions getFingering(String chordName, HashMap<String,FingerPositions> chordFingerings){
		return chordFingerings.get(chordName);
	}

	public static byte[] getBluetoothArrayFromChord(String chordName, HashMap<String,FingerPositions> chordFingerings){
		byte[] bytecodes = new byte[1];
		if(chordName.equals("noise")){
			bytecodes[0] = Byte.valueOf("0");
		} else {
			FingerPositions fp = chordFingerings.get(chordName);
			int[] fingerArray = new int[6];
			fingerArray[0] = fp.string1;
			fingerArray[1] = fp.string2;
			fingerArray[2] = fp.string3;
			fingerArray[3] = fp.string4;
			fingerArray[4] = fp.string5;
			fingerArray[5] = fp.string6;

			int lightCount = 6;
			for (int i = 0; i < fingerArray.length; i++) {
				if(fingerArray[i] + fp.baseFret > 4){
					Log.e("MusicUtils","The chord " + fp.name + " is outside FretX's display range");
					byte[] b = new byte[1];
					b[0] = Byte.valueOf("0");
					return b;
				}
				if(fingerArray[i] == -1){
					lightCount--;
				}
			}
			bytecodes = new byte[lightCount+1];
			int byteIndex = 0;
			for (int i = 0; i < fingerArray.length; i++) {
				if(fingerArray[i] > -1){
					bytecodes[byteIndex] =  Byte.valueOf(Integer.toString((i+1) + 10*fingerArray[i]));
					byteIndex++;
				}
			}
			bytecodes[byteIndex] = Byte.valueOf("0");
		}

		return bytecodes;
	}

	public static final String chordDbJsonString = "[\n" +
			"{\"name\":\"Cmaj\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":2,\"string3\":0,\"string2\":1,\"string1\":0},\n" +
			"{\"name\":\"Cm\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":1,\"string3\":0,\"string2\":1,\"string1\":-1},\n" +
			"{\"name\":\"C6\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":2,\"string2\":1,\"string1\":3},\n" +
			"{\"name\":\"Cm6\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":1,\"string1\":3},\n" +
			"{\"name\":\"C69\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":2,\"string3\":2,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"C7\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":2,\"string3\":3,\"string2\":1,\"string1\":0},\n" +
			"{\"name\":\"Cm7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":3,\"string2\":1,\"string1\":3},\n" +
			"{\"name\":\"Cmaj7\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":2,\"string3\":0,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"C7b5\",\"baseFret\":2,\"string6\":-1,\"string5\":3,\"string4\":4,\"string3\":3,\"string2\":5,\"string1\":-1},\n" +
			"{\"name\":\"C7#5\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":2,\"string3\":3,\"string2\":1,\"string1\":4},\n" +
			"{\"name\":\"Cm7b5\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":4,\"string3\":3,\"string2\":4,\"string1\":-1},\n" +
			"{\"name\":\"C7b9\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":2,\"string3\":3,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"C9\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":2,\"string3\":3,\"string2\":3,\"string1\":-1},\n" +
			"{\"name\":\"Cm9\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":1,\"string3\":3,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"Cmaj9\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":0,\"string3\":0,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"Cadd9\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":2,\"string3\":0,\"string2\":3,\"string1\":0},\n" +
			"{\"name\":\"C13\",\"baseFret\":2,\"string6\":-1,\"string5\":3,\"string4\":5,\"string3\":3,\"string2\":5,\"string1\":5},\n" +
			"{\"name\":\"Csus2\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":0,\"string3\":0,\"string2\":1,\"string1\":-1},\n" +
			"{\"name\":\"Csus4\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":3,\"string3\":0,\"string2\":1,\"string1\":-1},\n" +
			"{\"name\":\"Cdim\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":4,\"string3\":2,\"string2\":1,\"string1\":-1},\n" +
			"{\"name\":\"Cdim7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"Caug\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":2,\"string3\":1,\"string2\":1,\"string1\":4},\n" +
			"{\"name\":\"C#maj\",\"baseFret\":0,\"string6\":-1,\"string5\":4,\"string4\":3,\"string3\":1,\"string2\":2,\"string1\":1},\n" +
			"{\"name\":\"C#m\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":2,\"string3\":1,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"C#6\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":3,\"string3\":3,\"string2\":2,\"string1\":4},\n" +
			"{\"name\":\"C#m6\",\"baseFret\":0,\"string6\":-1,\"string5\":4,\"string4\":2,\"string3\":3,\"string2\":2,\"string1\":4},\n" +
			"{\"name\":\"C#69\",\"baseFret\":2,\"string6\":-1,\"string5\":4,\"string4\":3,\"string3\":3,\"string2\":4,\"string1\":4},\n" +
			"{\"name\":\"C#7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":3,\"string3\":4,\"string2\":2,\"string1\":4},\n" +
			"{\"name\":\"C#m7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":2,\"string3\":4,\"string2\":2,\"string1\":4},\n" +
			"{\"name\":\"C#maj7\",\"baseFret\":0,\"string6\":-1,\"string5\":4,\"string4\":3,\"string3\":1,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"C#7b5\",\"baseFret\":0,\"string6\":3,\"string5\":0,\"string4\":3,\"string3\":4,\"string2\":2,\"string1\":-1},\n" +
			"{\"name\":\"C#7#5\",\"baseFret\":0,\"string6\":-1,\"string5\":4,\"string4\":3,\"string3\":2,\"string2\":0,\"string1\":-1},\n" +
			"{\"name\":\"C#m7b5\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":2,\"string3\":0,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"C#7b9\",\"baseFret\":0,\"string6\":-1,\"string5\":4,\"string4\":3,\"string3\":4,\"string2\":3,\"string1\":4},\n" +
			"{\"name\":\"C#9\",\"baseFret\":3,\"string6\":-1,\"string5\":4,\"string4\":6,\"string3\":6,\"string2\":4,\"string1\":4},\n" +
			"{\"name\":\"C#m9\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":1,\"string3\":1,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"C#maj9\",\"baseFret\":0,\"string6\":-1,\"string5\":4,\"string4\":1,\"string3\":1,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"C#add9\",\"baseFret\":0,\"string6\":1,\"string5\":-1,\"string4\":1,\"string3\":1,\"string2\":2,\"string1\":1},\n" +
			"{\"name\":\"C#13\",\"baseFret\":3,\"string6\":-1,\"string5\":4,\"string4\":6,\"string3\":4,\"string2\":6,\"string1\":6},\n" +
			"{\"name\":\"C#sus2\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":3,\"string3\":3,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"C#sus4\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":3,\"string3\":3,\"string2\":4,\"string1\":1},\n" +
			"{\"name\":\"C#dim\",\"baseFret\":2,\"string6\":-1,\"string5\":3,\"string4\":1,\"string3\":0,\"string2\":1,\"string1\":0},\n" +
			"{\"name\":\"C#dim7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":2,\"string3\":3,\"string2\":2,\"string1\":3},\n" +
			"{\"name\":\"C#aug\",\"baseFret\":0,\"string6\":-1,\"string5\":4,\"string4\":3,\"string3\":2,\"string2\":2,\"string1\":-1},\n" +
			"{\"name\":\"Dmaj\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":0,\"string3\":2,\"string2\":3,\"string1\":2},\n" +
			"{\"name\":\"Dm\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":0,\"string3\":2,\"string2\":3,\"string1\":1},\n" +
			"{\"name\":\"D6\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":0,\"string3\":2,\"string2\":0,\"string1\":2},\n" +
			"{\"name\":\"Dm6\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":0,\"string3\":2,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"D69\",\"baseFret\":3,\"string6\":-1,\"string5\":5,\"string4\":4,\"string3\":4,\"string2\":5,\"string1\":5},\n" +
			"{\"name\":\"D7\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":0,\"string3\":2,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"Dm7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":2,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Dmaj7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":2,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"D7b5\",\"baseFret\":4,\"string6\":-1,\"string5\":5,\"string4\":6,\"string3\":5,\"string2\":7,\"string1\":-1},\n" +
			"{\"name\":\"D7#5\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":4,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"Dm7b5\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":1,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"D7b9\",\"baseFret\":3,\"string6\":-1,\"string5\":5,\"string4\":4,\"string3\":5,\"string2\":4,\"string1\":5},\n" +
			"{\"name\":\"D9\",\"baseFret\":0,\"string6\":2,\"string5\":-1,\"string4\":0,\"string3\":2,\"string2\":1,\"string1\":0},\n" +
			"{\"name\":\"Dm9\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":3,\"string3\":2,\"string2\":1,\"string1\":0},\n" +
			"{\"name\":\"Dmaj9\",\"baseFret\":0,\"string6\":-1,\"string5\":5,\"string4\":2,\"string3\":2,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"Dadd9\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":2,\"string2\":3,\"string1\":0},\n" +
			"{\"name\":\"D13\",\"baseFret\":4,\"string6\":-1,\"string5\":5,\"string4\":7,\"string3\":5,\"string2\":7,\"string1\":7},\n" +
			"{\"name\":\"Dsus2\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":2,\"string2\":3,\"string1\":0},\n" +
			"{\"name\":\"Dsus4\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":2,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"Ddim\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"Ddim7\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"Daug\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":3,\"string2\":3,\"string1\":2},\n" +
			"{\"name\":\"Ebmaj\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":3,\"string2\":4,\"string1\":3},\n" +
			"{\"name\":\"Ebm\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":4,\"string3\":3,\"string2\":4,\"string1\":2},\n" +
			"{\"name\":\"Eb6\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":5,\"string3\":3,\"string2\":4,\"string1\":3},\n" +
			"{\"name\":\"Ebm6\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":3,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"Eb69\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":0,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Eb7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":3,\"string2\":2,\"string1\":3},\n" +
			"{\"name\":\"Ebm7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":3,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"Ebmaj7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":3,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"Eb7b5\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":2,\"string1\":4},\n" +
			"{\"name\":\"Eb7#5\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":4,\"string2\":2,\"string1\":3},\n" +
			"{\"name\":\"Ebm7b5\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"Eb7b9\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":0,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"Eb9\",\"baseFret\":0,\"string6\":3,\"string5\":1,\"string4\":1,\"string3\":0,\"string2\":2,\"string1\":1},\n" +
			"{\"name\":\"Ebm9\",\"baseFret\":0,\"string6\":2,\"string5\":1,\"string4\":1,\"string3\":3,\"string2\":2,\"string1\":1},\n" +
			"{\"name\":\"Ebmaj9\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":0,\"string2\":3,\"string1\":1},\n" +
			"{\"name\":\"Ebadd9\",\"baseFret\":0,\"string6\":3,\"string5\":2,\"string4\":2,\"string3\":0,\"string2\":-1,\"string1\":1},\n" +
			"{\"name\":\"Eb13\",\"baseFret\":4,\"string6\":-1,\"string5\":6,\"string4\":5,\"string3\":6,\"string2\":8,\"string1\":8},\n" +
			"{\"name\":\"Ebsus2\",\"baseFret\":0,\"string6\":1,\"string5\":1,\"string4\":1,\"string3\":3,\"string2\":-1,\"string1\":1},\n" +
			"{\"name\":\"Ebsus4\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":3,\"string2\":4,\"string1\":4},\n" +
			"{\"name\":\"Ebdim\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"Ebdim7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"Ebaug\",\"baseFret\":0,\"string6\":3,\"string5\":2,\"string4\":1,\"string3\":0,\"string2\":0,\"string1\":3},\n" +
			"{\"name\":\"Emaj\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":2,\"string3\":1,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"Em\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":2,\"string3\":0,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"E6\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":2,\"string3\":1,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"Em6\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":2,\"string3\":0,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"E69\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":2,\"string3\":1,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"E7\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"Em7\",\"baseFret\":0,\"string6\":0,\"string5\":1,\"string4\":0,\"string3\":0,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"Emaj7\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"E7b5\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":0,\"string3\":1,\"string2\":3,\"string1\":0},\n" +
			"{\"name\":\"E7#5\",\"baseFret\":0,\"string6\":0,\"string5\":3,\"string4\":0,\"string3\":1,\"string2\":1,\"string1\":-1},\n" +
			"{\"name\":\"Em7b5\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":2,\"string3\":3,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"E7b9\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"E9\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":2},\n" +
			"{\"name\":\"Em9\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":0,\"string3\":0,\"string2\":0,\"string1\":2},\n" +
			"{\"name\":\"Emaj9\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":1,\"string3\":1,\"string2\":0,\"string1\":2},\n" +
			"{\"name\":\"Eadd9\",\"baseFret\":0,\"string6\":2,\"string5\":2,\"string4\":2,\"string3\":1,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"E13\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":0,\"string3\":1,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"Esus2\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":4,\"string3\":4,\"string2\":-1,\"string1\":0},\n" +
			"{\"name\":\"Esus4\",\"baseFret\":0,\"string6\":0,\"string5\":2,\"string4\":2,\"string3\":2,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"Edim\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":2,\"string3\":3,\"string2\":2,\"string1\":-1},\n" +
			"{\"name\":\"Edim7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":2,\"string3\":3,\"string2\":2,\"string1\":3},\n" +
			"{\"name\":\"Eaug\",\"baseFret\":0,\"string6\":0,\"string5\":3,\"string4\":2,\"string3\":1,\"string2\":1,\"string1\":0},\n" +
			"{\"name\":\"Fmaj\",\"baseFret\":0,\"string6\":1,\"string5\":3,\"string4\":3,\"string3\":2,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Fm\",\"baseFret\":0,\"string6\":1,\"string5\":3,\"string4\":3,\"string3\":1,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"F6\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":3,\"string3\":2,\"string2\":3,\"string1\":-1},\n" +
			"{\"name\":\"Fm6\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":1,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"F69\",\"baseFret\":0,\"string6\":1,\"string5\":0,\"string4\":0,\"string3\":0,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"F7\",\"baseFret\":0,\"string6\":1,\"string5\":3,\"string4\":1,\"string3\":2,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Fm7\",\"baseFret\":0,\"string6\":1,\"string5\":3,\"string4\":3,\"string3\":1,\"string2\":4,\"string1\":1},\n" +
			"{\"name\":\"Fmaj7\",\"baseFret\":0,\"string6\":1,\"string5\":-1,\"string4\":2,\"string3\":2,\"string2\":1,\"string1\":-1},\n" +
			"{\"name\":\"F7b5\",\"baseFret\":0,\"string6\":1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":0,\"string1\":-1},\n" +
			"{\"name\":\"F7#5\",\"baseFret\":0,\"string6\":1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":2,\"string1\":-1},\n" +
			"{\"name\":\"Fm7b5\",\"baseFret\":2,\"string6\":-1,\"string5\":-1,\"string4\":3,\"string3\":4,\"string2\":4,\"string1\":4},\n" +
			"{\"name\":\"F7b9\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":3,\"string3\":2,\"string2\":4,\"string1\":2},\n" +
			"{\"name\":\"F9\",\"baseFret\":0,\"string6\":3,\"string5\":0,\"string4\":3,\"string3\":2,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Fm9\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":1,\"string2\":1,\"string1\":3},\n" +
			"{\"name\":\"Fmaj9\",\"baseFret\":0,\"string6\":0,\"string5\":0,\"string4\":3,\"string3\":0,\"string2\":1,\"string1\":3},\n" +
			"{\"name\":\"Fadd9\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":3,\"string3\":2,\"string2\":1,\"string1\":3},\n" +
			"{\"name\":\"F13\",\"baseFret\":0,\"string6\":1,\"string5\":0,\"string4\":1,\"string3\":3,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"Fsus2\",\"baseFret\":0,\"string6\":-1,\"string5\":3,\"string4\":3,\"string3\":0,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Fsus4\",\"baseFret\":0,\"string6\":1,\"string5\":3,\"string4\":3,\"string3\":3,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Fdim\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"Fdim7\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"Faug\",\"baseFret\":0,\"string6\":1,\"string5\":-1,\"string4\":3,\"string3\":2,\"string2\":2,\"string1\":1},\n" +
			"{\"name\":\"F#maj\",\"baseFret\":0,\"string6\":2,\"string5\":4,\"string4\":4,\"string3\":3,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"F#m\",\"baseFret\":0,\"string6\":2,\"string5\":4,\"string4\":4,\"string3\":2,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"F#6\",\"baseFret\":0,\"string6\":2,\"string5\":4,\"string4\":-1,\"string3\":3,\"string2\":4,\"string1\":-1},\n" +
			"{\"name\":\"F#m6\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"F#69\",\"baseFret\":0,\"string6\":2,\"string5\":1,\"string4\":1,\"string3\":1,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"F#7\",\"baseFret\":0,\"string6\":2,\"string5\":4,\"string4\":2,\"string3\":3,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"F#m7\",\"baseFret\":0,\"string6\":2,\"string5\":0,\"string4\":2,\"string3\":2,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"F#maj7\",\"baseFret\":0,\"string6\":2,\"string5\":-1,\"string4\":3,\"string3\":3,\"string2\":2,\"string1\":-1},\n" +
			"{\"name\":\"F#7b5\",\"baseFret\":0,\"string6\":2,\"string5\":-1,\"string4\":2,\"string3\":3,\"string2\":1,\"string1\":-1},\n" +
			"{\"name\":\"F#7#5\",\"baseFret\":0,\"string6\":2,\"string5\":-1,\"string4\":2,\"string3\":3,\"string2\":3,\"string1\":-1},\n" +
			"{\"name\":\"F#m7b5\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":2,\"string3\":2,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"F#7b9\",\"baseFret\":0,\"string6\":2,\"string5\":1,\"string4\":2,\"string3\":0,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"F#9\",\"baseFret\":0,\"string6\":2,\"string5\":4,\"string4\":2,\"string3\":3,\"string2\":2,\"string1\":4},\n" +
			"{\"name\":\"F#m9\",\"baseFret\":0,\"string6\":3,\"string5\":0,\"string4\":3,\"string3\":1,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"F#maj9\",\"baseFret\":0,\"string6\":2,\"string5\":-1,\"string4\":3,\"string3\":3,\"string2\":-1,\"string1\":4},\n" +
			"{\"name\":\"F#add9\",\"baseFret\":0,\"string6\":2,\"string5\":1,\"string4\":-1,\"string3\":1,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"F#13\",\"baseFret\":0,\"string6\":2,\"string5\":4,\"string4\":2,\"string3\":3,\"string2\":4,\"string1\":2},\n" +
			"{\"name\":\"F#sus2\",\"baseFret\":0,\"string6\":2,\"string5\":-1,\"string4\":-1,\"string3\":1,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"F#sus4\",\"baseFret\":0,\"string6\":2,\"string5\":4,\"string4\":4,\"string3\":4,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"F#dim\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"F#dim7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"F#aug\",\"baseFret\":0,\"string6\":2,\"string5\":-1,\"string4\":4,\"string3\":3,\"string2\":3,\"string1\":2},\n" +
			"{\"name\":\"Gmaj\",\"baseFret\":0,\"string6\":3,\"string5\":2,\"string4\":0,\"string3\":0,\"string2\":0,\"string1\":3},\n" +
			"{\"name\":\"Gm\",\"baseFret\":0,\"string6\":3,\"string5\":1,\"string4\":0,\"string3\":0,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"G6\",\"baseFret\":0,\"string6\":3,\"string5\":2,\"string4\":0,\"string3\":0,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"Gm6\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":2,\"string3\":3,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"G69\",\"baseFret\":0,\"string6\":3,\"string5\":2,\"string4\":0,\"string3\":2,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"G7\",\"baseFret\":0,\"string6\":3,\"string5\":2,\"string4\":0,\"string3\":0,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"Gm7\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":3,\"string3\":0,\"string2\":3,\"string1\":-1},\n" +
			"{\"name\":\"Gmaj7\",\"baseFret\":0,\"string6\":3,\"string5\":2,\"string4\":0,\"string3\":0,\"string2\":0,\"string1\":2},\n" +
			"{\"name\":\"G7b5\",\"baseFret\":0,\"string6\":3,\"string5\":-1,\"string4\":3,\"string3\":4,\"string2\":2,\"string1\":-1},\n" +
			"{\"name\":\"G7#5\",\"baseFret\":0,\"string6\":3,\"string5\":-1,\"string4\":3,\"string3\":4,\"string2\":4,\"string1\":-1},\n" +
			"{\"name\":\"Gm7b5\",\"baseFret\":2,\"string6\":3,\"string5\":4,\"string4\":3,\"string3\":3,\"string2\":6,\"string1\":3},\n" +
			"{\"name\":\"G7b9\",\"baseFret\":0,\"string6\":3,\"string5\":2,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"G9\",\"baseFret\":0,\"string6\":3,\"string5\":-1,\"string4\":0,\"string3\":2,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"Gm9\",\"baseFret\":2,\"string6\":3,\"string5\":5,\"string4\":3,\"string3\":3,\"string2\":3,\"string1\":5},\n" +
			"{\"name\":\"Gmaj9\",\"baseFret\":0,\"string6\":3,\"string5\":-1,\"string4\":0,\"string3\":2,\"string2\":0,\"string1\":2},\n" +
			"{\"name\":\"Gadd9\",\"baseFret\":0,\"string6\":3,\"string5\":0,\"string4\":0,\"string3\":0,\"string2\":0,\"string1\":3},\n" +
			"{\"name\":\"G13\",\"baseFret\":0,\"string6\":3,\"string5\":2,\"string4\":3,\"string3\":0,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"Gsus2\",\"baseFret\":0,\"string6\":3,\"string5\":0,\"string4\":0,\"string3\":0,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"Gsus4\",\"baseFret\":0,\"string6\":3,\"string5\":3,\"string4\":0,\"string3\":0,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"Gdim\",\"baseFret\":1,\"string6\":-1,\"string5\":-1,\"string4\":4,\"string3\":2,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"Gdim7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":2,\"string3\":3,\"string2\":2,\"string1\":3},\n" +
			"{\"name\":\"Gaug\",\"baseFret\":0,\"string6\":3,\"string5\":-1,\"string4\":5,\"string3\":4,\"string2\":4,\"string1\":3},\n" +
			"{\"name\":\"G#maj\",\"baseFret\":0,\"string6\":4,\"string5\":3,\"string4\":1,\"string3\":1,\"string2\":1,\"string1\":-1},\n" +
			"{\"name\":\"G#m\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":1,\"string3\":1,\"string2\":4,\"string1\":4},\n" +
			"{\"name\":\"G#6\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":1,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"G#m6\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":1,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"G#69\",\"baseFret\":0,\"string6\":4,\"string5\":3,\"string4\":3,\"string3\":3,\"string2\":4,\"string1\":4},\n" +
			"{\"name\":\"G#7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":1,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"G#m7\",\"baseFret\":3,\"string6\":4,\"string5\":6,\"string4\":4,\"string3\":4,\"string2\":7,\"string1\":4},\n" +
			"{\"name\":\"G#maj7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":1,\"string2\":1,\"string1\":3},\n" +
			"{\"name\":\"G#7b5\",\"baseFret\":2,\"string6\":4,\"string5\":-1,\"string4\":4,\"string3\":5,\"string2\":3,\"string1\":-1},\n" +
			"{\"name\":\"G#7#5\",\"baseFret\":3,\"string6\":4,\"string5\":-1,\"string4\":4,\"string3\":5,\"string2\":5,\"string1\":-1},\n" +
			"{\"name\":\"G#m7b5\",\"baseFret\":0,\"string6\":4,\"string5\":5,\"string4\":4,\"string3\":4,\"string2\":7,\"string1\":4},\n" +
			"{\"name\":\"G#7b9\",\"baseFret\":0,\"string6\":4,\"string5\":3,\"string4\":4,\"string3\":2,\"string2\":-1,\"string1\":-1},\n" +
			"{\"name\":\"G#9\",\"baseFret\":0,\"string6\":2,\"string5\":1,\"string4\":1,\"string3\":1,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"G#m9\",\"baseFret\":0,\"string6\":2,\"string5\":1,\"string4\":1,\"string3\":1,\"string2\":0,\"string1\":2},\n" +
			"{\"name\":\"G#maj9\",\"baseFret\":2,\"string6\":4,\"string5\":3,\"string4\":5,\"string3\":3,\"string2\":4,\"string1\":-1},\n" +
			"{\"name\":\"G#add9\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":1,\"string3\":1,\"string2\":1,\"string1\":-1},\n" +
			"{\"name\":\"G#13\",\"baseFret\":0,\"string6\":4,\"string5\":3,\"string4\":4,\"string3\":1,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"G#sus2\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":1,\"string3\":1,\"string2\":-1,\"string1\":-1},\n" +
			"{\"name\":\"G#sus4\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":1,\"string2\":2,\"string1\":4},\n" +
			"{\"name\":\"G#dim\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"G#dim7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"G#aug\",\"baseFret\":0,\"string6\":0,\"string5\":3,\"string4\":2,\"string3\":1,\"string2\":1,\"string1\":0},\n" +
			"{\"name\":\"Amaj\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":2,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"Am\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":2,\"string2\":1,\"string1\":0},\n" +
			"{\"name\":\"A6\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":2,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"Am6\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":2,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"A69\",\"baseFret\":3,\"string6\":5,\"string5\":4,\"string4\":4,\"string3\":4,\"string2\":5,\"string1\":5},\n" +
			"{\"name\":\"A7\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":0,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"Am7\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":0,\"string2\":1,\"string1\":0},\n" +
			"{\"name\":\"Amaj7\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":1,\"string2\":2,\"string1\":0},\n" +
			"{\"name\":\"A7b5\",\"baseFret\":3,\"string6\":5,\"string5\":-1,\"string4\":5,\"string3\":6,\"string2\":4,\"string1\":-1},\n" +
			"{\"name\":\"A7#5\",\"baseFret\":4,\"string6\":5,\"string5\":-1,\"string4\":5,\"string3\":6,\"string2\":6,\"string1\":-1},\n" +
			"{\"name\":\"Am7b5\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":1,\"string1\":3},\n" +
			"{\"name\":\"A7b9\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":3,\"string2\":2,\"string1\":3},\n" +
			"{\"name\":\"A9\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":4,\"string2\":2,\"string1\":3},\n" +
			"{\"name\":\"Am9\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":1,\"string3\":1,\"string2\":1,\"string1\":3},\n" +
			"{\"name\":\"Amaj9\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":4,\"string2\":2,\"string1\":4},\n" +
			"{\"name\":\"Aadd9\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":2,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"A13\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":0,\"string2\":2,\"string1\":3},\n" +
			"{\"name\":\"Asus2\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":2,\"string2\":0,\"string1\":0},\n" +
			"{\"name\":\"Asus4\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":2,\"string3\":2,\"string2\":3,\"string1\":0},\n" +
			"{\"name\":\"Adim\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":1,\"string3\":2,\"string2\":1,\"string1\":-1},\n" +
			"{\"name\":\"Adim7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":1,\"string3\":2,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"Aaug\",\"baseFret\":0,\"string6\":-1,\"string5\":0,\"string4\":3,\"string3\":2,\"string2\":2,\"string1\":1},\n" +
			"{\"name\":\"Bbmaj\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":3,\"string3\":3,\"string2\":3,\"string1\":1},\n" +
			"{\"name\":\"Bbm\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":3,\"string3\":3,\"string2\":2,\"string1\":1},\n" +
			"{\"name\":\"Bb6\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":3,\"string3\":3,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"Bbm6\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":3,\"string3\":0,\"string2\":2,\"string1\":-1},\n" +
			"{\"name\":\"Bb69\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":0,\"string3\":0,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Bb7\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":3,\"string3\":1,\"string2\":4,\"string1\":1},\n" +
			"{\"name\":\"Bbm7\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":3,\"string3\":1,\"string2\":2,\"string1\":1},\n" +
			"{\"name\":\"Bbmaj7\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":3,\"string3\":2,\"string2\":3,\"string1\":1},\n" +
			"{\"name\":\"Bb7b5\",\"baseFret\":4,\"string6\":6,\"string5\":-1,\"string4\":6,\"string3\":7,\"string2\":5,\"string1\":-1},\n" +
			"{\"name\":\"Bb7#5\",\"baseFret\":5,\"string6\":6,\"string5\":-1,\"string4\":6,\"string3\":7,\"string2\":7,\"string1\":-1},\n" +
			"{\"name\":\"Bbm7b5\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":-1,\"string3\":1,\"string2\":2,\"string1\":-1},\n" +
			"{\"name\":\"Bb7b9\",\"baseFret\":6,\"string6\":-1,\"string5\":-1,\"string4\":8,\"string3\":7,\"string2\":9,\"string1\":7},\n" +
			"{\"name\":\"Bb9\",\"baseFret\":0,\"string6\":1,\"string5\":1,\"string4\":0,\"string3\":1,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Bbm9\",\"baseFret\":5,\"string6\":-1,\"string5\":-1,\"string4\":-1,\"string3\":6,\"string2\":6,\"string1\":8},\n" +
			"{\"name\":\"Bbmaj9\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":0,\"string3\":2,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Bbadd9\",\"baseFret\":0,\"string6\":1,\"string5\":1,\"string4\":0,\"string3\":3,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Bb13\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":0,\"string3\":1,\"string2\":3,\"string1\":3},\n" +
			"{\"name\":\"Bbsus2\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":3,\"string3\":3,\"string2\":1,\"string1\":1},\n" +
			"{\"name\":\"Bbsus4\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":3,\"string3\":3,\"string2\":4,\"string1\":1},\n" +
			"{\"name\":\"Bbdim\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":2,\"string3\":3,\"string2\":2,\"string1\":-1},\n" +
			"{\"name\":\"Bbdim7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":2,\"string3\":3,\"string2\":2,\"string1\":3},\n" +
			"{\"name\":\"Bbaug\",\"baseFret\":0,\"string6\":-1,\"string5\":1,\"string4\":4,\"string3\":3,\"string2\":3,\"string1\":2},\n" +
			"{\"name\":\"Bmaj\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":4,\"string3\":4,\"string2\":4,\"string1\":2},\n" +
			"{\"name\":\"Bm\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":4,\"string3\":4,\"string2\":3,\"string1\":2},\n" +
			"{\"name\":\"B6\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":4,\"string3\":4,\"string2\":4,\"string1\":4},\n" +
			"{\"name\":\"Bm6\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":4,\"string3\":4,\"string2\":3,\"string1\":4},\n" +
			"{\"name\":\"B69\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":1,\"string3\":1,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"B7\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":1,\"string3\":2,\"string2\":0,\"string1\":2},\n" +
			"{\"name\":\"Bm7\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":0,\"string3\":2,\"string2\":0,\"string1\":2},\n" +
			"{\"name\":\"Bmaj7\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":4,\"string3\":3,\"string2\":4,\"string1\":2},\n" +
			"{\"name\":\"B7b5\",\"baseFret\":5,\"string6\":7,\"string5\":-1,\"string4\":7,\"string3\":8,\"string2\":6,\"string1\":-1},\n" +
			"{\"name\":\"B7#5\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":1,\"string3\":2,\"string2\":0,\"string1\":3},\n" +
			"{\"name\":\"Bm7b5\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":0,\"string3\":2,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"B7b9\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":1,\"string3\":2,\"string2\":1,\"string1\":2},\n" +
			"{\"name\":\"B9\",\"baseFret\":6,\"string6\":7,\"string5\":9,\"string4\":7,\"string3\":8,\"string2\":7,\"string1\":9},\n" +
			"{\"name\":\"Bm9\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":0,\"string3\":2,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"Bmaj9\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":1,\"string3\":3,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"Badd9\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":4,\"string3\":4,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"B13\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":1,\"string3\":2,\"string2\":0,\"string1\":4},\n" +
			"{\"name\":\"Bsus2\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":4,\"string3\":4,\"string2\":2,\"string1\":2},\n" +
			"{\"name\":\"Bsus4\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":4,\"string3\":4,\"string2\":5,\"string1\":2},\n" +
			"{\"name\":\"Bdim\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":3,\"string3\":4,\"string2\":3,\"string1\":-1},\n" +
			"{\"name\":\"Bdim7\",\"baseFret\":0,\"string6\":-1,\"string5\":-1,\"string4\":0,\"string3\":1,\"string2\":0,\"string1\":1},\n" +
			"{\"name\":\"Baug\",\"baseFret\":0,\"string6\":-1,\"string5\":2,\"string4\":5,\"string3\":4,\"string2\":4,\"string1\":3}\n" +
			"]";
}






//STOP!
//Hammer time