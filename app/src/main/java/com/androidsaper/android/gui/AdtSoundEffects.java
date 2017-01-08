package com.androidsaper.android.gui;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.androidsaper.R;

import com.Multisaper.Core.Interfaces.SoundEffectPlayer;

/**
 * Created by Kalessin on 2017-01-07.
 */

public class AdtSoundEffects implements SoundEffectPlayer {

    private MediaPlayer Explosion;
    private MediaPlayer Success;

    public AdtSoundEffects(Context ctx)
    {
         Explosion = MediaPlayer.create(ctx, R.raw.explosion001);
         Success = MediaPlayer.create(ctx, R.raw.success);
    }

    @Override
    public void PlaySoundEffect(SoundEffect value) {
        Log.d("AdtSoundEffects", "Playing: " + value.toString());
        switch(value) {
            case GameWon:
                Success.start();
                break;
            case MineExplosion:
                Explosion.start();
                break;
        }
    }
}
