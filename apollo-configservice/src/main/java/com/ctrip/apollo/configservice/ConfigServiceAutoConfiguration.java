package com.ctrip.apollo.configservice;

import com.ctrip.apollo.biz.message.ReleaseMessageScanner;
import com.ctrip.apollo.configservice.controller.NotificationController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Configuration
public class ConfigServiceAutoConfiguration {
  @Autowired
  private NotificationController notificationController;

  @Bean
  public ReleaseMessageScanner releaseMessageScanner() {
    ReleaseMessageScanner releaseMessageScanner = new ReleaseMessageScanner();
    releaseMessageScanner.addMessageListener(notificationController);
    return releaseMessageScanner;
  }

}
