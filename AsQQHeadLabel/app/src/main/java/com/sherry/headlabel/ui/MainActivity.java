package com.sherry.headlabel.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.sherry.headlabel.R;
import com.sherry.headlabel.adapter.StarViewAdapter;
import com.sherry.headlabel.data.StarAllModel;
import com.sherry.headlabel.widget.StarView;

public class MainActivity extends Activity {

    private StarView vStarView;
    private StarViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vStarView = (StarView) findViewById(R.id.star_tag);
        String jsonData = "{" +
                "\"centerHeadModel\": {" +
                "   \"id\": \"1\"," +
                "   \"nickName\": \"1号\"," +
                "   \"headPhoto\": \"图片地址吧啦吧啦\"," +
                "   \"vip\": 0" +
                "}," +
                "\"labelList\": [{" +
                "       \"name\": \"女汉子\"," +
                "       \"rate\": \"30%\"" +
                "   },{" +
                "       \"name\": \"单身\"," +
                "       \"rate\": \"60%\"" +
                "   },{" +
                "       \"name\": \"萌萌哒\"," +
                "       \"rate\": \"100%\"" +
                "   },{" +
                "       \"name\": \"白羊座\"," +
                "       \"rate\": \"80%\"" +
                "   },{" +
                "       \"name\": \"演员\"," +
                "       \"rate\": \"85%\"" +
                "   }]," +
                "\"otherHeadList\": [{" +
                "       \"id\": \"2\"," +
                "       \"nickName\": \"2号\"," +
                "       \"headPhoto\": \"图片地址吧啦吧啦\"" +
                "   },{" +
                "       \"id\": \"3\"," +
                "       \"nickName\": \"3号\"," +
                "       \"headPhoto\": \"图片地址吧啦吧啦\"" +
                "   },{" +
                "       \"id\": \"4\"," +
                "       \"nickName\": \"4号\"," +
                "       \"headPhoto\": \"图片地址吧啦吧啦\"" +
                "   },{" +
                "       \"id\": \"5\"," +
                "       \"nickName\": \"5号\"," +
                "       \"headPhoto\": \"图片地址吧啦吧啦\"" +
                "   },{" +
                "       \"id\": \"6\"," +
                "       \"nickName\": \"6号\"," +
                "       \"headPhoto\": \"图片地址吧啦吧啦\"" +
                "   },{" +
                "       \"id\": \"7\"," +
                "       \"nickName\": \"7号\"," +
                "       \"headPhoto\": \"图片地址吧啦吧啦\"" +
                "}]" +
                "}";
        StarAllModel model = JSON.parseObject(jsonData, StarAllModel.class);
        mAdapter = new StarViewAdapter(this, model);
        vStarView.setAdapter(mAdapter);
        vStarView.setOnStarViewItemClickListener(new StarView.onStarViewItemClickListener() {
            @Override
            public void onItemClick(View v, String style, int index) {
                // 实现对各个头像标签的监听，style分为三种：中间、内圆、外圆，index 对应位置
            }
        });
    }

}
