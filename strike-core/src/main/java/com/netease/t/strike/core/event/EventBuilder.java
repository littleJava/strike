package com.netease.t.strike.core.event;

import org.springframework.stereotype.Component;

import com.netease.t.strike.core.profile.Phase;
import com.netease.t.strike.core.profile.Profile;

@Component
public class EventBuilder {
    public StrikeEvent build(Profile profile){
        Phase phase = profile.getProfileContext().moveToPhase();
        switch (phase) {
        case START:
            return new ProfileNoEvent(profile);
        case SETUP:
            return new ProfileSetupEvent(profile);
        case WARMUP:
            return new ProfileWarmupEvent(profile);
        case RUN:
            if ("hit".equalsIgnoreCase(profile.getType())) {
                return new ProfileRunHitEvent(profile);
            }else {
                return new ProfileRunStressEvent(profile);
            }
        case COOLDOWN:
            return new ProfileCooldownEvent(profile);
        case END:
            return new ProfileEndEvent(profile);
        case NO:
            return null;
        default:
            return new ProfileNoEvent(profile);
        }
        
    }
}
