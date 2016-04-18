package com.ctrip.apollo.biz.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Entity
@Table(name = "Release")
@SQLDelete(sql = "Update Release set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class Release extends BaseEntity {

  @Column(name = "Name", nullable = false)
  private String name;

  @Column(name = "AppId", nullable = false)
  private String appId;

  @Column(name = "ClusterName", nullable = false)
  private String clusterName;

  @Column(name = "NamespaceName", nullable = false)
  private String namespaceName;

  @Column(name = "Configurations", nullable = false)
  @Lob
  private String configurations;

  @Column(name = "Comment", nullable = false)
  private String comment;

  public String getAppId() {
    return appId;
  }

  public String getClusterName() {
    return clusterName;
  }

  public String getComment() {
    return comment;
  }

  public String getConfigurations() {
    return configurations;
  }

  public String getNamespaceName() {
    return namespaceName;
  }

  public String getName() {
    return name;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setConfigurations(String configurations) {
    this.configurations = configurations;
  }

  public void setNamespaceName(String namespaceName) {
    this.namespaceName = namespaceName;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String toString() {
    return toStringHelper().add("name", name).add("appId", appId).add("clusterName", clusterName)
        .add("namespaceName", namespaceName).add("configurations", configurations)
        .add("comment", comment).toString();
  }
}
