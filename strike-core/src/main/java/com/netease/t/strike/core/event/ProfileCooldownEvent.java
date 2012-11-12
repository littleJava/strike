package com.netease.t.strike.core.event;

import com.netease.t.strike.core.profile.Profile;

/**
 * @author hbliu
 *
 * hbliu
 */
public class ProfileCooldownEvent extends StrikeEvent {

    public ProfileCooldownEvent(Profile profile) {
        super(profile);
    }

}
