package com.xlh.krystal.littlejoker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.squareup.picasso.Picasso;
import com.xlh.krystal.littlejoker.ContentActivity;
import com.xlh.krystal.littlejoker.R;
import com.xlh.krystal.littlejoker.Util.CircleTransform;
import com.xlh.krystal.littlejoker.WebActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 这是用来显示gif动图的fra
 */
public class Fragment_gif extends Fragment {

    private ImageView gif_portrait;//这是头像view
    private View v;//父容器
    private List<String> portrait;//存放头像地址的集合
    private List<String> user_name;//存放用户名的集合
    private List<String> love;//存放点赞数的集合
    private List<String> content_word;//存放gif文字内容的集合
    private List<String> content_gif;//存放gif动图的集合
    private ListView gif_lv;//gif的list
    private int page_index = 1;//初始化加载的页数
    private List<String> static_ima;//存放静图的集合
    private boolean click = false;//判断是否点击加载
    private MyAdapter myAdapter;//那个listview的适配器
    private int OKposition = -1;//这是应该加载动图的位置
    private List<String> url;//这是用来放置那个web地址的集合
    private SwipeRefreshLayout fragment_gif_fresh;//这是那个实现下拉刷新的布局


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_gif,container,false);
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
     * 初始化存放页面数据的集合
     */
    private void initList() {
        static_ima = new ArrayList<>();
        portrait = new ArrayList<>();
        user_name = new ArrayList<>();
        love = new ArrayList<>();
        content_word = new ArrayList<>();
        content_gif = new ArrayList<>();
        url = new ArrayList<>();
    }

    /**
     * 设置事件的监听
     */
    private void setListener() {
        gif_lv.setOnItemClickListener(new MyItemClickListener());
        gif_lv.setOnScrollListener(new MyScrollListener());
        fragment_gif_fresh.setOnRefreshListener(new MyFreshListener());
    }

    private class MyFreshListener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            page_index =1;
            initList();
            getData();
        }
    }

    /**
     * 这是那个用来实现上拉加载的监听
     */
    private class MyScrollListener implements AbsListView.OnScrollListener{

        boolean isBottom = false;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (isBottom && scrollState == SCROLL_STATE_IDLE){
                getData();
                page_index++;
                myAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem + visibleItemCount == totalItemCount){
                isBottom = true;
            }
        }
    }

    /**
     * 这是那个listview的点击事件
     */
    private class MyItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("url",url.get(position));
            intent.putExtra("text",content_word.get(position));
            startActivity(intent);
        }
    }


    /**
     * 这是list的适配器
     */
    private void setAdapter() {
        myAdapter = new MyAdapter();
        gif_lv.setAdapter(myAdapter);
    }

    /**
     * 这是哪个listview的适配器
     */
    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return content_word.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder h = new ViewHolder();
            if (convertView==null){
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_gif_list,parent,false);
                h.gif_content = (TextView) convertView.findViewById(R.id.gif_content);
                h.gif_iv = (ImageView) convertView.findViewById(R.id.gif_iv);
                h.gif_love = (TextView) convertView.findViewById(R.id.gif_love);
                h.gif_name = (TextView) convertView.findViewById(R.id.gif_name);
                h.gif_portrait = (ImageView) convertView.findViewById(R.id.gif_portrait);
                convertView.setTag(h);
            }else{
                h = (ViewHolder) convertView.getTag();
            }
            h.gif_name.setText(user_name.get(position));
            h.gif_love.setText(love.get(position));
            h.gif_content.setText(content_word.get(position));
            Picasso.with(getActivity()).load(portrait.get(position)).transform(new CircleTransform()).into(h.gif_portrait);
            h.gif_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"正在加载动图，请稍候。。。",Toast.LENGTH_SHORT).show();
                    click =true;
                    OKposition = position;
                   // Log.e("点击的位置",position+"");
                    myAdapter.notifyDataSetChanged();
                }
            });
            if (click && OKposition == position) {
                Glide.with(getActivity()).load(content_gif.get(position)).into(h.gif_iv);
            }else{
                Glide.with(getActivity()).load(static_ima.get(position)).into(h.gif_iv);
            }

            return convertView;
        }

        public class ViewHolder{
             TextView gif_name;//用户名view
             TextView gif_love;//点赞数view
             TextView gif_content;//内容view
             ImageView gif_iv;//用来存放gif动图的view
             ImageView gif_portrait;//这是用户头像
        }
    }

    /**
     * 向服务器请求数据
     */
    private void getData() {
        OkHttpUtils.get("http://lockscreen.mobile7.cn/duanzi/index.php?catid=2&pagesize=10")
                .params("pageindex",page_index+"")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("----","动图请求成功");
                        getResult(s);
                    }

                    /**
                     * 请求失败的提示
                     * @param call
                     * @param response
                     * @param e
                     */
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(getActivity(),"gif动图加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * json解析数据
     * @param s
     */
    private void getResult(String s) {
        try {
            JSONArray arr = new JSONArray(s.trim());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONArray arr_item = obj.getJSONArray("item");
                for (int j = 0; j < arr_item.length(); j++) {
                    JSONObject obj_item = arr_item.getJSONObject(j);
                    String portrait0 = obj_item.getString("userlogo");
                    portrait.add(portrait0);
                    String user_name0 = obj_item.getString("username");
                    String article = obj_item.getString("article");
                    url.add(article);
                    user_name.add(user_name0);
                    String love0 = obj_item.getString("updateTime");
                    love.add(love0);
                    String content_word0 = obj_item.getString("title");
                    content_word.add(content_word0);
                    String static_ima0 = obj_item.getString("thumb");
                    static_ima.add(static_ima0);
                    String content_gif0 = obj_item.getString("thumbnail");
                    content_gif.add(content_gif0);
                }
            }
            myAdapter.notifyDataSetChanged();
            fragment_gif_fresh.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化数据
     */
    private void init() {

        gif_lv = (ListView) v.findViewById(R.id.gif_lv);
        fragment_gif_fresh = (SwipeRefreshLayout) v.findViewById(R.id.fragment_gif_fresh);
    }
}
