package com.netease.t.strike.core.event;

import com.netease.t.strike.core.profile.Profile;

/**
 * @author hbliu
 *
 * hbliu
 */
public class ProfileSetupEvent extends StrikeEvent {

    public ProfileSetupEvent(Profile profile) {
        super(profile);
    }

}
