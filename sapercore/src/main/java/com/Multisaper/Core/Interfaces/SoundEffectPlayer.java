package com.Multisaper.Core.Interfaces;

/**
 * Created by Kalessin on 2017-01-07.
 */

public interface SoundEffectPlayer {

    enum SoundEffect {
        None,
        MineExplosion,
        GameWon,
    }

    void PlaySoundEffect(SoundEffect value);
}
