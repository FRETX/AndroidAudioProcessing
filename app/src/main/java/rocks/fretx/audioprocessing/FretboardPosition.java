package rocks.fretx.audioprocessing;

import android.util.Log;

/**
 * Created by Onur Babacan on 10/3/16.
 */

public class FretboardPosition {
    protected int string;
    protected int fret;

    public FretboardPosition(int str, int frt){
	    checkString(str);
		checkFret(frt);
        string = str;
        fret = frt;
    }

    public void setFret(int frt){
        checkFret(frt);
        fret = frt;
    }

    public void setString(int str){
        checkString(str);
        string = str;
    }

	public int getFret(){
		return fret;
	}

	public int getString(){
		return string;
	}

	private void checkFret(int frt){
		if(frt < -1 || frt > 18){
			//-1 means "don't play this string", 0 means "open string"
			throw new IllegalArgumentException("Fret number needs to be in [-1,18]");
		}
		if(frt > 4){
			Log.d("FretboardPosition","Fret number is outside FretX display range");
		}
	}

	private void checkString(int str){
		if(str < 1 || str > 6){
			throw new IllegalArgumentException("String number needs to be in [1,6]");
		}
	}

    public byte getByteCode() {
        return Byte.valueOf(Integer.toString(this.fret + this.string*10));
    }

}
