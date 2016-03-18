package com.ctrip.apollo.biz.service.impl;

import com.ctrip.apollo.biz.entity.ReleaseSnapShot;
import com.ctrip.apollo.biz.entity.Version;
import com.ctrip.apollo.biz.repository.ReleaseSnapShotRepository;
import com.ctrip.apollo.biz.repository.VersionRepository;
import com.ctrip.apollo.biz.service.ConfigService;
import com.ctrip.apollo.core.model.ApolloConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service("configService")
public class ConfigServiceImpl implements ConfigService {
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private ReleaseSnapShotRepository releaseSnapShotRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private TypeReference<Map<String, Object>> configurationTypeReference =
        new TypeReference<Map<String, Object>>() {
        };

    @Override
    public ApolloConfig loadConfig(long appId, String clusterName, String versionName) {
        Version version = versionRepository.findByAppIdAndName(appId, versionName);
        if (version == null) {
            return null;
        }
        ReleaseSnapShot releaseSnapShot =
            releaseSnapShotRepository.findByReleaseIdAndClusterName(version.getReleaseId(), clusterName);
        if (releaseSnapShot == null) {
            return null;
        }

        return assembleConfig(version, releaseSnapShot);
    }

    private ApolloConfig assembleConfig(Version version, ReleaseSnapShot releaseSnapShot) {
        ApolloConfig config =
            new ApolloConfig(version.getAppId(), releaseSnapShot.getClusterName(), version.getName(), version.getReleaseId());
        config.setConfigurations(transformConfigurationToMap(releaseSnapShot.getConfigurations()));

        return config;
    }

    Map<String, Object> transformConfigurationToMap(String configurations) {
        try {
            return objectMapper.readValue(configurations, configurationTypeReference);
        } catch (IOException e) {
            e.printStackTrace();
            return Maps.newHashMap();
        }
    }
}
