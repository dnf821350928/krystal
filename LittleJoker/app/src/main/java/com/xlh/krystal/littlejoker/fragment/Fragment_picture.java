package com.xlh.krystal.littlejoker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.squareup.picasso.Picasso;
import com.xlh.krystal.littlejoker.HDImageActivity;
import com.xlh.krystal.littlejoker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 这是用来显示美图的fra
 */
public class Fragment_picture extends Fragment {

    private List<String> smallPic;//小图集合
    private List<String> bigPic;//大图集合
    private RecyclerView recommend_recycs;//recyclerview
    private View v;//父容器
    private int page = 1;//显示内容的页数
    private List<Integer> heights;//图片的高度
    private MyAdapter myAdapter;//recyclerview的适配器
    private boolean idle = true;//这是哪个recyclerview判断是否在滑动的变量
    private SwipeRefreshLayout pic_fresh;//这是那个下拉刷新的布局
    private int rn =50;//控制那个请求图片的数量

    /**
     * 加载fragment的界面
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_picture, container, false);
        return v;
    }

    /**
     * 页面初始化
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initList();
        setAdapter();
        getData();
        setLine();
        setListener();
    }

    /**
     * 初始化那个存放页面数据的集合
     */
    private void initList() {
        smallPic = new ArrayList<>();
        bigPic = new ArrayList<>();
        heights = new ArrayList<>();
    }

    /**
     * 设置那个事件的监听
     */
    private void setListener() {
        recommend_recycs.setOnScrollListener(new MyScrollListener());
        pic_fresh.setOnRefreshListener(new MyFreshListener());
    }

    private class MyFreshListener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            page =1;
            initList();
            getData();
        }
    }

    /**
     * 这是那个recyclerview的滑动监听
     */
    private class MyScrollListener extends RecyclerView.OnScrollListener{
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (recyclerView.SCROLL_STATE_IDLE==newState&&recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent() == recyclerView.computeVerticalScrollRange()){
                    page++;
                    rn = rn + 20;
                    getData();
                //Toast.makeText(getActivity(),"上拉刷新了",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setAdapter() {
        myAdapter = new MyAdapter();
        recommend_recycs.setAdapter(myAdapter);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_picture_recyc_item,parent,false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            ImageView iv = holder.getView();
            holder.setIsRecyclable(false);
            iv.setLayoutParams(new LinearLayout.LayoutParams(iv.getLayoutParams().width,heights.get(position)));
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), HDImageActivity.class);
                    intent.putExtra("url",bigPic.get(position));
                    startActivity(intent);
                }
            });
            ////////////////////////////////////////////////////////////////////
            Picasso.with(getActivity()).load(smallPic.get(position)).resize(iv.getLayoutParams().width,heights.get(position)).into(iv);
        }


        @Override
        public int getItemCount() {
            return smallPic.size();
        }

        /**
         *  recyclerviewd的viewholder
         */
        public class MyViewHolder extends RecyclerView.ViewHolder{
            ImageView picture_recyc_iv;
            public MyViewHolder(View itemView) {
                super(itemView);
                picture_recyc_iv = (ImageView) itemView.findViewById(R.id.picture_recyc_iv);
            }
            public ImageView getView(){
                return picture_recyc_iv;
            }
        }
    }

    /**
     * 向服务器请求数据
     */
    private void getData() {
        OkHttpUtils.get("http://image.baidu.com/app/search?uid=868930029667044&query=%E5%B0%91%E5%A5%B3&appversion=1.5&devType=0&clientInfo=model:MI%205;+manufacturer:Xiaomi;+iMEI:868930029667044;+osVersion:6.0;+deviceInfo:MI%205_6.0_23_Xiaomi;+versionName:0.8;+versionCode:400121;+&version=400121")
                .params("pn", page+"")
                .params("rn",rn+"")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Toast.makeText(getActivity(),"美图请求成功",Toast.LENGTH_SHORT).show();
                        getResult(s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(getActivity(), "获取美图失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 解析获取的数据
     *
     * @param s
     */
    private void getResult(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            String msg = obj.getString("msg");
            //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            JSONObject obj_data = obj.getJSONObject("data");
            JSONArray arr = obj_data.getJSONArray("picList");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj_picList = arr.getJSONObject(i);
                String small = obj_picList.getString("thumbnailUrl");
                String big = obj_picList.getString("objUrl");
                int a = (int) (Math.random()*200+300);
                heights.add(a);
                smallPic.add(small);
                bigPic.add(big);
            }
            myAdapter.notifyDataSetChanged();
            pic_fresh.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置recycler的列数
     */
    private void setLine() {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recommend_recycs.setLayoutManager(manager);
    }

    /**
     * 初始化数据
     */
    private void init() {

        recommend_recycs = (RecyclerView) v.findViewById(R.id.recommend_recycs);
        pic_fresh = (SwipeRefreshLayout) v.findViewById(R.id.pic_fresh);
    }


}
