package com.netease.t.strike.core.event;

import com.netease.t.strike.core.profile.Profile;

/**
 * @author hbliu
 *
 * hbliu
 */
public class ProfileRunStressEvent extends StrikeEvent {

    public ProfileRunStressEvent(Profile profile) {
        super(profile);
    }

}
