package com.xlh.krystal.littlejoker.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.squareup.picasso.Picasso;
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
 * 这是用来推荐的fra
 */
public class Fragment_recommend extends Fragment {

    private View v;//父容器
    private ListView recommend_lv;//这是推荐页的listView
    private int pageIndex = 1;//这是那个listView选择的页数
    private List<String> portrait;//头像集合
    private List<String> user_name;//用户名
    private List<String> love;//点赞数
    private List<String> content_word;//文字文本集合
    private List<String> content_gif;//gif地址集合
    private MyAdapter myAdapter;//listView的适配器
    private List<String> static_img;//存放静图的集合
    private int OKposition = -1;//判断是否点击播放gif
    private boolean click = false;//判断gif是否被点击
    private List<String> url;//这是哪个详情网址
    private SwipeRefreshLayout fragment_recommend_fresh;//这是那个下拉刷新的控件


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         v = LayoutInflater.from(getActivity()).inflate(R.layout.fragmeng_recommend,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initList();
        getData();
        setView();
        setAdapter();
        setListener();
        //Log.e("大小",portrait.size()+"---"+user_name.size()+"---"+love.size()+"---"+content_word.size()+"---"+content_gif.size()+"");
    }

    /**
     * 初始化页面数据集合
     */
    private void initList() {
        content_word = new ArrayList<>();
        content_gif = new ArrayList<>();
        static_img = new ArrayList<>();
        url = new ArrayList<>();
        love = new ArrayList<>();
        user_name = new ArrayList<>();
        portrait = new ArrayList<>();
    }

    /**
     * 设置页面参数
     */
    private void setView() {
        fragment_recommend_fresh.setColorSchemeColors(Color.BLACK,Color.BLUE,Color.CYAN,Color.GRAY,Color.GREEN);
    }

    /**
     * 设置监听
     */
    private void setListener() {
        recommend_lv.setOnItemClickListener(new MyItemClickListener());
        recommend_lv.setOnScrollListener(new MyScrollListener());
        fragment_recommend_fresh.setOnRefreshListener(new MyFreshListener());
    }

    /**
     * 这是那个下拉刷新控件的监听
     */
    private class MyFreshListener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            initList();
            pageIndex = 1;
            getData();


        }


    }

    /**
     * 设置listview的下拉刷新监听
     */
    private class MyScrollListener implements AbsListView.OnScrollListener{

        boolean isBottom = false;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (isBottom&&scrollState==SCROLL_STATE_IDLE){
                pageIndex++;
                getData();
                myAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem+visibleItemCount==totalItemCount){
                isBottom = true;
            }
        }
    }

    /**
     * 设置listview的点击监听
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
     * 请求数据
     */
    private void getData() {
        OkHttpUtils.get("http://lockscreen.mobile7.cn/duanzi/index.php")
                .params("catid","0")
                .params("pagesize","20")
                .params("pageindex",pageIndex+"")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //Toast.makeText(getActivity(),"首页数据加载成功",Toast.LENGTH_SHORT).show();
                        getResult(s);
                       // Log.e("请求的数据",s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e("-----","首页数据请求失败");
                    }
                });
    }

    /**
     * josn解析获取数据结果
     * @param s
     */
    private void getResult(String s) {
        try {
            JSONArray arr = new JSONArray(s.trim());
            for (int i = 0; i < arr.length() ; i++) {
                JSONObject obj = arr.getJSONObject(i);
                JSONArray arr_item = obj.getJSONArray("item");
                for (int j = 0; j <arr_item.length() ; j++) {
                    JSONObject obj_item = arr_item.getJSONObject(j);
                    String portrait0 = obj_item.getString("userlogo");
                    portrait.add(portrait0);
                    String article = obj_item.getString("article");
                    url.add(article);
                    String username = obj_item.getString("username");
                    user_name.add(username);
                    String updateTime = obj_item.getString("updateTime");
                    love.add(updateTime);
                    String title = obj_item.getString("title");
                    content_word.add(title);
                    String thumb = obj_item.getString("thumb");
                    static_img.add(thumb);
                    JSONObject obj_style = obj_item.getJSONObject("style");
                    JSONArray arr_image = obj_style.getJSONArray("images");
                    String image = arr_image.getString(0);
                    content_gif.add(image);
                }
            }
            myAdapter.notifyDataSetChanged();
            //Toast.makeText(getActivity(),"刷新完成",Toast.LENGTH_SHORT).show();
            fragment_recommend_fresh.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置设配器
     */
    private void setAdapter() {
        recommend_lv.setAdapter(myAdapter);
    }

    /**
     * 这是那个listView的适配器
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recommend_list,parent,false);
                h.recommend_portrait = (ImageView) convertView.findViewById(R.id.recommend_portrait);
                h.recommend_gif = (ImageView) convertView.findViewById(R.id.recommend_gif);
                h.recommend_love = (TextView) convertView.findViewById(R.id.recommend_love);
                h.recommend_name = (TextView) convertView.findViewById(R.id.recommend_name);
                h.recommend_text = (TextView) convertView.findViewById(R.id.recommend_text);
                convertView.setTag(h);
            }else{
                h = (ViewHolder) convertView.getTag();
            }
            Picasso.with(getActivity()).load(portrait.get(position))
                    .transform(new CircleTransform())
                    .into(h.recommend_portrait);
            h.recommend_name.setText(user_name.get(position));
            //Log.e("-----",user_name.get(position));
            h.recommend_love.setText(love.get(position));
           // Log.e("-----",love.get(position));
            h.recommend_text.setText(content_word.get(position));
           // Log.e("-----",content_word.get(position));
            h.recommend_gif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"gif动图加载中，请稍候。。。",Toast.LENGTH_SHORT).show();
                    click = true ;
                    OKposition = position;
                    myAdapter.notifyDataSetChanged();
                }
            });
            if (!content_gif.get(position).equals("")) {
                if (click&&OKposition ==position){
                    Glide.with(getActivity()).load(content_gif.get(position)).into(h.recommend_gif);
                }else{
                    Glide.with(getActivity()).load(static_img.get(position)).into(h.recommend_gif);
                }

            }
            return convertView;
        }

        public class ViewHolder{
            ImageView recommend_portrait;
            TextView recommend_name;
            TextView recommend_love;
            TextView recommend_text;
            ImageView recommend_gif;
        }
    }

    private void init() {
        recommend_lv = (ListView)v.findViewById(R.id.recommend_lv);
        fragment_recommend_fresh = (SwipeRefreshLayout) v.findViewById(R.id.fragment_recommend_fresh);
        myAdapter = new MyAdapter();

    }


}
