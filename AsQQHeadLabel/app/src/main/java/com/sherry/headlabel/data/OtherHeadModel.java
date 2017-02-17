package com.sherry.headlabel.data;

/**
 * Created by xueli on 2017/2/17.
 */

public class OtherHeadModel {

    private String id;
    private String nickName; // 昵称
    private String headPhoto; // 头像图片

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

}
