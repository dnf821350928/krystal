package com.xlh.krystal.littlejoker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 这是显示详情的页面
 */
public class WebActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView detail_web;//这是webview
    private ImageView detail_back;//返回键
    private ImageView detail_share;//分享键
    private String url;//这是用来存放那个分享地址的
    private String words;//这是那个分享内容的文字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        init();
        setViewContent();
        setListener();
    }

    /**
     * 设置点击事件的监听
     */
    private void setListener() {
        detail_back.setOnClickListener(this);
        detail_share.setOnClickListener(this);
    }

    /**
     * 设置那个webview的内容
     */
    private void setViewContent() {
        url = getUrl();
        words = getWords();
        detail_web.getSettings().setJavaScriptEnabled(true);
        detail_web.loadUrl(url);
    }

    /**
     * 获取那个分享内容的文字
     */
    private String getWords() {
        Intent intent = getIntent();
        String word = intent.getStringExtra("text");
        return word;
    }

    /**
     * 获得webview的地址
     * @return
     */
    private String getUrl() {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        return url;
    }

    /**
     * 初始化 view
     */
    private void init() {
        detail_web = (WebView) findViewById(R.id.detail_web);
        detail_back = (ImageView) findViewById(R.id.detail_back);
        detail_share = (ImageView) findViewById(R.id.detail_share);
    }

    /**
     * 各个点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detail_back://这是返回键的点击事件
                this.finish();
                break;
            case R.id.detail_share://这是分享键的点击事件
                showShare();
                break;
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
//关闭sso授权
        oks.disableSSOWhenAuthorize();

// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("来自LittleJoker的分享");
// titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(url);
// text是分享文本，所有平台都需要这个字段
        oks.setText(words);
// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
// url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
// comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
// site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
// siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

// 启动分享GUI
        oks.show(this);
    }
}
