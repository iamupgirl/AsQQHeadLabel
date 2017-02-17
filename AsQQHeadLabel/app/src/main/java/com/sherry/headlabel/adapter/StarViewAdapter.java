package com.sherry.headlabel.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sherry.headlabel.R;
import com.sherry.headlabel.data.CenterHeadModel;
import com.sherry.headlabel.data.LabelModel;
import com.sherry.headlabel.data.OtherHeadModel;
import com.sherry.headlabel.data.StarAllModel;
import com.sherry.headlabel.widget.StarView;

import java.util.List;

/**
 * StarView的适配器
 *
 * Created by xueli on 2016/8/30.
 */
public class StarViewAdapter {

    private StarView mStarView;
    private Context mContext;
    private StarAllModel mModel;

    public StarViewAdapter(Context context, StarAllModel model) {
        this.mContext = context;
        this.mModel = model;
    }

    public void setStarView(StarView starView) {
        this.mStarView = starView;
        setView(false);
    }

    public void starView() {
        setView(false);
    }

    private void setView(boolean isReset) {
        mStarView.removeAllViews();
        setCenterView(mModel.getCenterHeadModel());
        setInsideView(mModel.getLabelList());
        setOutSideView(mModel.getOtherHeadList());
        if (!isReset) {
            mStarView.startAnim();
        }
    }

    private void setCenterView(CenterHeadModel model) {
        if (model != null) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_star_recommend_star_item, null);

            RelativeLayout mRlIcon = (RelativeLayout) view.findViewById(R.id.rl_fragment_star_recommend_star_item);
            LinearLayout.LayoutParams ps = (LinearLayout.LayoutParams) mRlIcon.getLayoutParams();
            ps.height = ps.width = (int) mContext.getResources().getDimension(R.dimen.height_80dp);
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_fragment_star_recommend_star_item_icon);
//            Glide.with(mContext).load(model.getHeadPhoto()).centerCrop().into(ivIcon);
            Glide.with(mContext).load(R.drawable.head_photo).into(ivIcon);
            ImageView vipIcon = (ImageView) view.findViewById(R.id.iv_fragment_star_recommend_star_item_tips);
            if (model.isVip()) {
                vipIcon.setVisibility(View.VISIBLE);
                ivIcon.setImageResource(R.drawable.star_vip);
            } else {
                vipIcon.setVisibility(View.GONE);
            }
            TextView tvName = (TextView) view.findViewById(R.id.tv_fragment_star_recommend_star_item_name);
            tvName.setText(model.getNickName());
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelOffset(R.dimen.text_size_14));
            view.setTag(R.id.star_tag, model);
            view.setTag(StarView.STYLE_CENTER);
            mStarView.addView(view);
        }
    }

    private void setInsideView(List<LabelModel> labelList) {

        for (int i = 0; labelList != null && i < labelList.size(); i++) {
            LabelModel model = labelList.get(i);
            View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_star_recommend_style_item, null);
            LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_fragment_star_recommend_style);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_fragment_star_recommend_style_item_title);
            TextView tvNum = (TextView) view.findViewById(R.id.tv_fragment_star_recommend_style_item_num);
            FrameLayout.LayoutParams ps = (FrameLayout.LayoutParams) ll.getLayoutParams();

            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.text_size_13));
            tvNum.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.text_size_13));
            ps.width = ps.height = (int) (mContext.getResources().getDimensionPixelSize(R.dimen.height_60dp));

            view.setLayoutParams(ps);
            tvTitle.setText(model.getName());
            tvNum.setText(model.getRate());
            view.setTag(R.id.star_tag, model);
            view.setTag(StarView.STYLE_INSIDE);
            mStarView.addView(view);
        }
    }

    private void setOutSideView(List<OtherHeadModel> otherHeadList) {
        for (int i = 0; otherHeadList != null && i < otherHeadList.size(); i++) {
            OtherHeadModel model = otherHeadList.get(i);
            View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_star_recommend_star_item, null);
            RelativeLayout mRlIcon = (RelativeLayout) view.findViewById(R.id.rl_fragment_star_recommend_star_item);
            LinearLayout.LayoutParams ps = (LinearLayout.LayoutParams) mRlIcon.getLayoutParams();
            ps.height = ps.width = (int) mContext.getResources().getDimension(R.dimen.height_60dp);
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_fragment_star_recommend_star_item_icon);
//            Glide.with(mContext).load(model.getHeadPhoto()).centerCrop().into(ivIcon);
            Glide.with(mContext).load(R.drawable.head_photo).into(ivIcon);
            TextView tvName = (TextView) view.findViewById(R.id.tv_fragment_star_recommend_star_item_name);
            tvName.setText(model.getNickName());
            view.setTag(R.id.star_tag, model);
            view.setTag(StarView.STYLE_OUTSIDE);
            mStarView.addView(view);
        }
    }

    public void notifyView() {
        mStarView.startChangeItemAnimOut();
    }

    public void reSetView() {
        setView(true);
        mStarView.startChangeItemAnimIn();
    }
}
