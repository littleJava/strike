package com.netease.t.strike.core.event;

import com.netease.t.strike.core.profile.Profile;

/**
 * @author hbliu
 *
 * hbliu
 */
public class ProfileEndEvent extends StrikeEvent {
    public ProfileEndEvent(Profile profile){
        super(profile);
    }
}
