package com.netease.t.strike.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.netease.t.strike.core.common.ExceptionUtil;
import com.netease.t.strike.core.common.ProfileInitException;
import com.netease.t.strike.core.log.Log;
import com.netease.t.strike.core.profile.Profile;
import com.netease.t.strike.core.profile.ProfileBuilder;

//@StrikeEventListener
@Service
public class ProfileManager {
    @Log
    private Logger logger;

    private List<Profile> activeProfiles = new LinkedList<Profile>();

    public List<Profile> select(String dirPath) {
        if (activeProfiles.size() == 0) {
            synchronized (ProfileManager.class) {
                if (activeProfiles.size() == 0) {
                    loadProfiles(dirPath);
                }
            }
        }
        return activeProfiles;
    }

    /**
     * init the active profiles
     * 
     * @throws IOException
     */
    private void loadProfiles(String dirPath) {
        try {
            File dir = loadProfileDir(dirPath);
            File[] profiles = dir.listFiles();
            for (int i = 0; i < profiles.length; i++) {
                Profile profile = ProfileBuilder.build(profiles[i].getAbsolutePath());
                if (profile.isActive()) {
                    profile.setAbsolutePath(profiles[i].getAbsolutePath());
                    activeProfiles.add(profile);
                }
            }
        } catch (IOException e) {
            throw ExceptionUtil.build("find profiles error!", e, ProfileInitException.class);
        }
    }

    private File loadProfileDir(String dirPath) throws IOException {
        File dir = null;
        try {
            dir = ResourceUtils.getFile(dirPath);
            checkDir(dir);
        } catch (IOException e1) {
            try {
                Resource profileDir = new ClassPathResource(dirPath);
                dir = profileDir.getFile();
                checkDir(dir);
            } catch (IOException e2) {
                Resource profileDir = new FileSystemResource(dirPath);
                dir = profileDir.getFile();
                checkDir(dir);
            }
        }
        return dir;
    }

    private void checkDir(File dir) throws FileNotFoundException {
        if (!dir.exists()||!dir.isDirectory()) {
            throw new FileNotFoundException(dir.getAbsolutePath()+" directory not found ");                
        }
    }
    
}
