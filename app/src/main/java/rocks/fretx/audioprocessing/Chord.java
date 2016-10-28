package rocks.fretx.audioprocessing;

/**
 * Created by Onur Babacan on 10/20/16.
 */

public class Chord {
    public final String root;
    public final String type;

    public Chord(String root, String type){
        //TODO: input handling

        this.root = root;
        this.type = type;
    }

    public String getChordString(){
        return root + type;
    }

    private int[] getChordFormula(){
        //int[] majorIntervals = {2,2,1,2,2,2,1,2,2,1,2,2,2,1};
        //in MATLAB: semitoneLookup = cumsum([1 majorIntervals]);
        int[] semitoneLookup = {1,3,5,6,8,10,12,13,15,17,18,20,22,24,25};
        int[] template,modification;

        switch (type){
            case "maj" :
                template = new int[] {1,3,5};
                modification = new int[] {0,0,0};
                break;
            case "m" :
                template = new int[] {1,3,5};
                modification = new int[] {0,-1,0};
                break;
            case "maj7" :
                template = new int[] {1,3,5,7};
                modification = new int[] {0,0,0,0};
                break;
            case "m7" :
                template = new int[] {1,3,5,7};
                modification = new int[] {0,-1,0,-1};
                break;
            case "5" :
                template = new int[] {1,5};
                modification = new int[] {0,0};
                break;
            case "7" :
                template = new int[] {1,3,5,7};
                modification = new int[] {0,0,0,-1};
                break;
            case "9" :
                template = new int[] {1,3,5,7,9};
                modification = new int[] {0,0,0,-1,0};
                break;
            case "sus2" :
                template = new int[] {1,2,5};
                modification = new int[] {0,0,0};
                break;
            case "sus4" :
                template = new int[] {1,4,5};
                modification = new int[] {0,0,0};
                break;
            case "7sus4" :
                template = new int[] {1,4,5,7};
                modification = new int[] {0,0,0,-1};
                break;
            case "7#9" :
                template = new int[] {1,3,5,7,9};
                modification = new int[] {0,0,0,-1,+1};
                break;
            case "add9" :
                template = new int[] {1,3,5,9};
                modification = new int[] {0,0,0,0};
                break;
            case "aug" :
                template = new int[] {1,3,5};
                modification = new int[] {0,0,+1};
                break;
            case "dim" :
                template = new int[] {1,3,5};
                modification = new int[] {0,-1,-1};
                break;
            case "dim7" :
                template = new int[] {1,3,5,7};
                modification = new int[] {0,-1,-1,-2};
                break;
            default:
                //This shouldn't happen
                template = new int[] {0,0,0};
                modification = new int[] {0,0,0};
        }

        int[] formula = new int[template.length];

        for (int i = 0; i < formula.length; i++) {
            formula[i] = semitoneLookup[template[i]-1] + modification[i];
        }
        return formula;
    }

    public int[] getNotes(){
        int rootNumber = MusicUtils.noteNameToSemitoneNumber(root);
        int[] formula = getChordFormula();
        int[] notes = new int[formula.length];
        for (int i = 0; i < notes.length ; i++) {
            notes[i] =  (formula[i] + rootNumber - 1) % 12;
            if(notes[i] == 0) notes[i] = 12;
        }
        return notes;
    }

    public String[] getNoteNames(){
        int[] notes = getNotes();
        String[] noteNames = new String[notes.length];
        for (int i = 0; i < noteNames.length; i++) {
            noteNames[i] = MusicUtils.semitoneNumberToNoteName(notes[i]);
        }
        return noteNames;
    }
}
