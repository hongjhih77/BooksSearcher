package jpa.domain.embeddable;

import javax.persistence.Embeddable;

@Embeddable
public class OperationInfo {

  private Long createTime;

  private Long updateTime;

  private Long deleteTime;

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public Long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Long updateTime) {
    this.updateTime = updateTime;
  }

  public Long getDeleteTime() {
    return deleteTime;
  }

  public void setDeleteTime(Long deleteTime) {
    this.deleteTime = deleteTime;
  }
}
