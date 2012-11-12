package com.netease.t.strike.core.event;

import com.netease.t.strike.core.profile.Profile;

/**
 * @author hbliu
 *
 * hbliu
 */
public class ProfileWarmupEvent extends StrikeEvent {

    public ProfileWarmupEvent(Profile profile) {
        super(profile);
    }

}
