package rocks.fretx.audioprocessing;

/**
 * Created by Onur Babacan on 10/3/16.
 */

public class FretboardPosition {
    protected int string;
    protected int fret;

    public FretboardPosition(int str, int frt){
        if(str < 1 || str > 6){
            throw new IllegalArgumentException("String number needs to be between 1-6");
        }
        if(frt < 0 || frt > 4){
            throw new IllegalArgumentException("Fret number needs to between 0-4");
        }
        string = str;
        fret = frt;
    }

    public void setFret(int frt){
        if(frt < 0 || frt > 4){
            throw new IllegalArgumentException("Fret number needs to between 0-4");
        }
        fret = frt;
    }

    public void setString(int str){
        if(str < 1 || str > 6){
            throw new IllegalArgumentException("String number needs to be between 1-6");
        }
        string = str;
    }

    public int getFret(){
        return fret;
    }

    public int getString(){
        return string;
    }


}
