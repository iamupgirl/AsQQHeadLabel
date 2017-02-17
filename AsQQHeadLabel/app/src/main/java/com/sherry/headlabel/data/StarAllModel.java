package com.sherry.headlabel.data;

import java.util.List;

/**
 * Created by xueli on 2017/2/17.
 */

public class StarAllModel {

    private CenterHeadModel centerHeadModel;
    private List<LabelModel> labelList;
    private List<OtherHeadModel> otherHeadList;

    public CenterHeadModel getCenterHeadModel() {
        return centerHeadModel;
    }

    public void setCenterHeadModel(CenterHeadModel centerHeadModel) {
        this.centerHeadModel = centerHeadModel;
    }

    public List<LabelModel> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<LabelModel> labelList) {
        this.labelList = labelList;
    }

    public List<OtherHeadModel> getOtherHeadList() {
        return otherHeadList;
    }

    public void setOtherHeadList(List<OtherHeadModel> otherHeadList) {
        this.otherHeadList = otherHeadList;
    }
}
