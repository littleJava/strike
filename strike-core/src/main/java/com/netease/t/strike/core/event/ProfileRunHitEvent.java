package com.netease.t.strike.core.event;

import com.netease.t.strike.core.profile.Profile;

/**
 * @author hbliu
 *
 * hbliu
 */
public class ProfileRunHitEvent extends StrikeEvent {

    public ProfileRunHitEvent(Profile profile) {
        super(profile);
    }

}
