package vn.mran.bc3.instance;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import vn.mran.bc3.R;
import vn.mran.bc3.constant.PrefValue;
import vn.mran.bc3.helper.Log;
import vn.mran.bc3.util.Preferences;
import vn.mran.bc3.util.Task;

/**
 * Created by Mr An on 28/11/2017.
 */

public class Media {
    public MediaPlayer mediaPlayer;
    public MediaPlayer shakePlayer;
    private Context context;
    private Preferences preferences;

    private int[] musicArrays = new int[]{
            R.raw.wc_2002,
            R.raw.lalala,
            R.raw.og_africa,
            R.raw.waka,
            R.raw.wavin_flag,
            R.raw.we_are_one
    };
    private int playId;
    private int posMusic;

    public Media(Context context) {
        this.context = context;
        preferences = new Preferences(context);
        playId = preferences.getIntValue(PrefValue.MUSIC_ID, PrefValue.DEFAULT_MUSIC_ID);

        for (int i = 0; i < musicArrays.length; i++) {
            if (playId == musicArrays[i]) {
                posMusic = i;
                break;
            }
        }
    }

    private String TAG = "Media";

    public void changeBackgroundMusic() {
        Log.d(TAG, "changeBackgroundMusic");
        if (posMusic < musicArrays.length - 1) {
            posMusic = posMusic + 1;
        } else {
            posMusic = 0;
        }
        playId = musicArrays[posMusic];
        Task.startNewBackGroundThread(new Thread(new Runnable() {
            @Override
            public void run() {
                stopBackgroundMusic();
                Task.sleep(200);
                playBackgroundMusic();
            }
        }));
    }

    public void playBackgroundMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, playId);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.seekTo(2000);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    public void stopBackgroundMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    public void pausePlayBackgroundMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    public void stopShortSound() {
        if (shakePlayer != null) {
            shakePlayer.release();
            shakePlayer = null;
        }
    }

    public void playShortSound(int id) {
        stopShortSound();

        shakePlayer = MediaPlayer.create(context, id);
        shakePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopShortSound();
            }
        });

        shakePlayer.start();
    }
}
