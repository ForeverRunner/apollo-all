application_module.controller("ConfigBaseInfoController",
                              ['$rootScope', '$scope', '$location', 'toastr', 'AppService', 'PermissionService',
                               'AppUtil',
                               function ($rootScope, $scope, $location, toastr, AppService, PermissionService,
                                         AppUtil) {

                                   var appId = AppUtil.parseParams($location.$$url).appid;

                                   //load session storage to recovery scene
                                   var env = sessionStorage.getItem(appId + "+env"),
                                       clusterName = sessionStorage.getItem(appId + "+cluster");

                                   var pageContext = {
                                       appId: appId,
                                       env: env ? env : '',
                                       clusterName: clusterName ? clusterName : 'default'
                                   };

                                   $rootScope.pageContext = pageContext;

                                   ////// load cluster nav tree //////

                                   AppService.load_nav_tree($rootScope.pageContext.appId).then(function (result) {
                                       var navTree = [];
                                       var nodes = AppUtil.collectData(result);

                                       if (!nodes || nodes.length == 0) {
                                           toastr.error("加载环境信息出错");
                                           return;
                                       }
                                       //default first env if session storage is empty
                                       if (!pageContext.env) {
                                           pageContext.env = nodes[0].env;
                                       }
                                       $rootScope.refreshNamespaces();

                                       nodes.forEach(function (env, envIdx) {
                                           if (!env.clusters || env.clusters.length == 0) {
                                               return;
                                           }
                                           var node = {};
                                           node.text = env.env;
                                           var clusterNodes = [];

                                           //如果env下面只有一个default集群则不显示集群列表
                                           if (env.clusters && env.clusters.length == 1 && env.clusters[0].name
                                                                                           == 'default') {
                                               if (envIdx == 0) {
                                                   node.state = {};
                                                   node.state.selected = true;
                                               }
                                               node.selectable = true;
                                           } else {
                                               node.selectable = false;
                                               //cluster list
                                               env.clusters.forEach(function (cluster, clusterIdx) {
                                                   var clusterNode = {},
                                                       parentNode = [];

                                                   //default selection from session storage or first env & first cluster
                                                   if ((pageContext.env && pageContext.env == env.env && pageContext.clusterName == cluster.name)
                                                       || (!pageContext.env && envIdx == 0 && clusterIdx == 0)) {
                                                       clusterNode.state = {};
                                                       clusterNode.state.selected = true;
                                                   }

                                                   clusterNode.text = cluster.name;
                                                   parentNode.push(node.text);
                                                   clusterNode.tags = ['集群'];
                                                   clusterNode.parentNode = parentNode;
                                                   clusterNodes.push(clusterNode);
                                               });
                                           }
                                           node.nodes = clusterNodes;
                                           navTree.push(node);
                                       });

                                       $('#treeview').treeview({
                                                                   color: "#797979",
                                                                   showBorder: true,
                                                                   data: navTree,
                                                                   levels: 99,
                                                                   expandIcon: '',
                                                                   collapseIcon: '',
                                                                   showTags: true,
                                                                   onNodeSelected: function (event, data) {
                                                                       if (!data.parentNode) {//first nav node
                                                                           $rootScope.pageContext.env = data.text;
                                                                           $rootScope.pageContext.clusterName =
                                                                               'default';
                                                                       } else {//second cluster node
                                                                           $rootScope.pageContext.env =
                                                                               data.parentNode[0];
                                                                           $rootScope.pageContext.clusterName =
                                                                               data.text;
                                                                       }
                                                                       //session storage
                                                                       //appId+env = env
                                                                       //appId+cluster = cluster
                                                                       sessionStorage.setItem(
                                                                           $rootScope.pageContext.appId + "+env",
                                                                           $rootScope.pageContext.env);
                                                                       sessionStorage.setItem(
                                                                           $rootScope.pageContext.appId + "+cluster",
                                                                           $rootScope.pageContext.clusterName);

                                                                       $rootScope.refreshNamespaces();
                                                                   }
                                                               });

                                   }, function (result) {
                                       toastr.error(AppUtil.errorMsg(result), "加载导航出错");
                                   });

                                   ////// app info //////

                                   AppService.load($rootScope.pageContext.appId).then(function (result) {
                                       $scope.appBaseInfo = result;
                                       $scope.appBaseInfo.orgInfo = result.orgName + '(' + result.orgId + ')';
                                   }, function (result) {
                                       toastr.error(AppUtil.errorMsg(result), "加载App信息出错");
                                   });

                                   ////// 补缺失的环境 //////
                                   $scope.missEnvs = [];
                                   AppService.find_miss_envs($rootScope.pageContext.appId).then(function (result) {
                                       $scope.missEnvs = AppUtil.collectData(result);
                                   }, function (result) {
                                       console.log(AppUtil.errorMsg(result));
                                   });

                                   $scope.createAppInMissEnv = function () {
                                       var count = 0;
                                       $scope.missEnvs.forEach(function (env) {
                                           AppService.create_remote(env, $scope.appBaseInfo).then(function (result) {
                                               toastr.success(env, '创建成功');
                                               count++;
                                               if (count == $scope.missEnvs.length) {
                                                   location.reload(true);
                                               }
                                           }, function (result) {
                                               toastr.error(AppUtil.errorMsg(result), '创建失败:' + env);
                                               count++;
                                               if (count == $scope.missEnvs.length) {
                                                   location.reload(true);
                                               }
                                           });
                                       });
                                   };

                                   //permission
                                   PermissionService.has_create_namespace_permission(appId).then(function (result) {
                                       $scope.hasCreateNamespacePermission = result.hasPermission;
                                   }, function (result) {

                                   });

                                   PermissionService.has_create_cluster_permission(appId).then(function (result) {
                                       $scope.hasCreateClusterPermission = result.hasPermission;
                                   }, function (result) {

                                   });

                                   PermissionService.has_assign_user_permission(appId).then(function (result) {
                                       $scope.hasAssignUserPermission = result.hasPermission;
                                   }, function (result) {

                                   });

                               }]);

