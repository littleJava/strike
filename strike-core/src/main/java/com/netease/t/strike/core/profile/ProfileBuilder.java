package com.netease.t.strike.core.profile;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import com.netease.t.strike.core.common.ExceptionUtil;
import com.netease.t.strike.core.common.ProfileInitException;

/**
 * make profile instance according by the profile file
 * 
 * @author hbliu hbliu
 */
public class ProfileBuilder {
    public static Profile build(String profilePath) {
        Serializer profileSerializer = new Persister();
        try {
            File file = ResourceUtils.getFile(profilePath);
            Profile profile = profileSerializer.read(Profile.class, file);
//            profile.appendId(StringUtils.stripFilenameExtension(file.getName()));
            profile.setProfileName(StringUtils.stripFilenameExtension(file.getName()));
            
//            Profile profile = profileSerializer.read(Profile.class, new ClassPathResource(profilePath).getFile());
            profile.afterPropertiesSet();
            return profile;
        } catch (Exception e) {
            throw ExceptionUtil.build("init file error! " + profilePath, e, ProfileInitException.class);
        }
    }
    public static Profile build(String profilePath,String profileId) {
        Profile profile = build(profilePath);
//        profile.appendId(profileId);
        profile.setId(profileId);
        return profile;
    }
}
