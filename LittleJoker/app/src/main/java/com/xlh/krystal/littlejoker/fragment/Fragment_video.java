package com.xlh.krystal.littlejoker.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.xlh.krystal.littlejoker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 这是用来播放视频的fra
 */
public class Fragment_video extends Fragment {

    private ListView fragment_video_lv;//播放video的listview
    private View v;//父容器
    private int page_index=1;//初始化请求参数
    private int page = 1;//初始化请求参数
    private List<String> video_url;//这个是用来存放视频地址的集合
    private List<String> video_intro;//这个是用来存放视频简介的集合
    private List<String> video_scan;//这个是用来存放视频预览图片地址的集合
    private MyAdapter myAdapter;//这是哪个listview的适配器
    private SwipeRefreshLayout fragment_video_fresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_video,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initList();
        getData();
        setAdapter();
        setListener();
    }

    /**
     * 初始化存放数据的集合
     */
    private void initList() {
        video_url = new ArrayList<>();
        video_intro = new ArrayList<>();
        video_scan = new ArrayList<>();
    }

    /**
     * 设置事件的监听
     */
    private void setListener() {
        fragment_video_lv.setOnScrollListener(new MyScrollListener());
        fragment_video_fresh.setOnRefreshListener(new MyFreshListener());
    }

    /**
     * 这是那个下拉刷新的监听
     */
    private class MyFreshListener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            page =1;
            page_index =1;
            initList();
            getData();
        }
    }

    /**
     * 这是listview滑动监听
     */
    private class MyScrollListener implements AbsListView.OnScrollListener{

        boolean isBottom = false;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (isBottom && scrollState == SCROLL_STATE_IDLE){
                getData();
                page++;
                page_index++;
                myAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem+visibleItemCount == totalItemCount){
                    isBottom = true;
                }
        }
    }

    /**
     * 这是用来fragment滑初之后停止视频播放
     */
    @Override
    public void onPause() {
        super.onPause();
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }


    /**
     * 给listview设置适配器
     */
    private void setAdapter() {
        myAdapter = new MyAdapter();
        fragment_video_lv.setAdapter(myAdapter);
    }

    /**
     * listview的适配器
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return video_url.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder h = new ViewHolder();
            if (convertView==null){
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_video_list,parent,false);
                h.video_player = (JCVideoPlayerStandard) convertView.findViewById(R.id.video_player);
                convertView.setTag(h);
            }else{
                h = (ViewHolder) convertView.getTag();
            }
            if (video_intro.get(position)!=null) {
                h.video_player.setUp(video_url.get(position), video_intro.get(position));
            }else{
                h.video_player.setUp(video_url.get(position));
            }
           // h.video_player.setUp(video_url.get(position));
            Glide.with(getActivity()).load(video_scan.get(position)).into(h.video_player.thumbImageView);
            return convertView;
        }

        public class ViewHolder{
            JCVideoPlayerStandard video_player;
        }
    }

    /**
     * 从服务器获得数据
     */
    private void getData() {
        OkHttpUtils.get("http://lockscreen.mobile7.cn/duanzi/video.php?keyword=18&pagesize=20&count=20")
                .params("pageindex",page_index+"")
                .params("page",page+"")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("-----","获取视频成功");
                        getResult(s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(getActivity(),"获取视频失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getResult(String s) {
        try {
            JSONObject obj = new JSONObject(s.trim());
            JSONArray arr_videos = obj.getJSONArray("videos");
            for (int i = 0; i < arr_videos.length(); i++) {
                JSONObject obj_videos = arr_videos.getJSONObject(i);
                String link = obj_videos.getString("link");
                video_url.add(link);
                //Log.e("获得的视频数据",link);
                String title = obj_videos.getString("title");
                //Log.e("获得的介绍数据",title);
                video_intro.add(title);
                String thumbnail = obj_videos.getString("thumbnail");
               // Log.e("获得的预览图片",thumbnail);
                video_scan.add(thumbnail);
            }
            myAdapter.notifyDataSetChanged();
            fragment_video_fresh.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        fragment_video_fresh = (SwipeRefreshLayout) v.findViewById(R.id.fragment_video_fresh);
        fragment_video_lv = (ListView) v.findViewById(R.id.fragment_video_lv);

    }
}
