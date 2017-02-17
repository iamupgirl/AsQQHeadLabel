package com.sherry.headlabel.data;

/**
 * 中间用户Model类
 *
 * Created by xueli on 2016/9/2.
 */
public class CenterHeadModel {

    private String id;
    private String nickName; // 昵称
    private String headPhoto; // 头像图片
    private int vip; // 是否会员：0 会员 1 非会员

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public boolean isVip () {
        if (getVip() == 0) {
            return true;
        } else {
            return false;
        }
    }
    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

}
