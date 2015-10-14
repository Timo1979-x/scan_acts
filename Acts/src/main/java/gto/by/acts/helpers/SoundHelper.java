package gto.by.acts.helpers;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by ltv on 14.10.2015.
 */
public class SoundHelper {
    private static MediaPlayer mediaPlayer = null;
    //private static SoundHelper helper = null;
    public static void playSound(Context c, int resid) {
        closeMP();
        Log.d("SoundHelper", "start Raw");
        mediaPlayer = MediaPlayer.create(c, resid);
        mediaPlayer.start();
    }

    private static void closeMP(){
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
