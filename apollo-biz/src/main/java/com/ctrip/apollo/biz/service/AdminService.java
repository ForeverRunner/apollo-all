package com.ctrip.apollo.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ctrip.apollo.biz.entity.App;

@Service
public class AdminService {

  @Autowired
  private AppService appService;

  @Autowired
  private AppNamespaceService appNamespaceService;

  @Autowired
  private ClusterService clusterService;

  @Autowired
  private NamespaceService namespaceService;

  @Transactional
  public App createNewApp(App app) {
    String createBy = app.getDataChangeCreatedBy();
    App createdApp = appService.save(app);

    String appId = createdApp.getAppId();

    appNamespaceService.createDefaultAppNamespace(appId, createBy);

    clusterService.createDefaultCluster(appId, createBy);

    namespaceService.createDefaultNamespace(appId, createBy);

    return app;
  }

}
