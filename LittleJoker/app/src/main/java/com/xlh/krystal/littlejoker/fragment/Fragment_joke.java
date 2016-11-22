package com.xlh.krystal.littlejoker.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
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
 * 这是用来播放段子的fra
 */
public class Fragment_joke extends Fragment {

    private ImageView joke_portrait;//这是头像view
    private View v;//父容器
    private TextView joke_name;//用户名view
    private TextView joke_love;//点赞数view
    private TextView joke_content;//内容view
    private int page_index = 1;//获取的当前页数初始值
    private List<String> portrait;//存放头像地址的集合
    private List<String> user_name;//存放用户名的集合
    private List<String> love;//存放点赞数的集合
    private List<String> content;//存放joke内容的集合
    private ListView joke_lv;//list表
    private MyAdapter myAdapter;//listview的适配器
    private List<String> url;//这是用来保存web地址的集合
    private SwipeRefreshLayout fragment_joke_fresh;//这是那个用来下拉刷新的布局

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_joke,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initList();
        setView();
        getData();
        setAdapter();
        setListener();
    }

    private void setView() {
        fragment_joke_fresh.setColorSchemeColors(Color.BLACK,Color.BLUE,Color.CYAN,Color.GRAY,Color.GREEN);

    }

    /**
     * 这是在初始化所有页面数据集合
     */
    private void initList() {
        portrait = new ArrayList<>();
        user_name = new ArrayList<>();
        love = new ArrayList<>();
        content = new ArrayList<>();
        url = new ArrayList<>();
    }

    /**
     * 设置事件的监听
     */
    private void setListener() {
        joke_lv.setOnItemClickListener(new MyItemClickListener());
        joke_lv.setOnScrollListener(new MyScrollListener());
        fragment_joke_fresh.setOnRefreshListener(new MyRefrshListener());
    }

    /**
     * 这是那个下拉刷新的监听
     */
    private class MyRefrshListener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            initList();
            page_index =1;
            getData();
        }
    }

    private class MyScrollListener implements AbsListView.OnScrollListener{

        boolean isBottom = false;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isBottom&&scrollState == SCROLL_STATE_IDLE){
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

    private class MyItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("url",url.get(position));
            intent.putExtra("text",content.get(position));
            startActivity(intent);
        }
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        myAdapter = new MyAdapter();
        joke_lv.setAdapter(myAdapter);
    }

    /**
     * 这是哪个listview的适配器
     */
    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return content.size();
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_joke_list,parent,false);
                h.joke_content = (TextView) convertView.findViewById(R.id.joke_content);
                h.joke_love = (TextView) convertView.findViewById(R.id.joke_love);
                h.joke_name = (TextView) convertView.findViewById(R.id.joke_name);
                h.joke_portrait = (ImageView) convertView.findViewById(R.id.joke_portrait);
                convertView.setTag(h);
            }else{
                h = (ViewHolder) convertView.getTag();
            }
            h.joke_name.setText(user_name.get(position));
            h.joke_love.setText(love.get(position));
            h.joke_content.setText(content.get(position));
            Picasso.with(getActivity()).load(portrait.get(position)).transform(new CircleTransform()).into(h.joke_portrait);

            return convertView;
        }

        public class ViewHolder{
            ImageView joke_portrait;
            TextView joke_name;
            TextView joke_love;
            TextView joke_content;
        }
    }

    /**
     * 向服务器请求数据
     */
    private void getData() {
        OkHttpUtils.get("http://lockscreen.mobile7.cn/duanzi/index.php?catid=1&pagesize=20")
                .params("pageindex",page_index+"")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("-----","段子请求成功");
                        getResult(s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(getActivity(),"段子请求失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * json解析获取到的数据
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
                    String article = obj_item.getString("article");
                    url.add(article);
                    String user_name0 = obj_item.getString("username");
                    user_name.add(user_name0);
                    String love0 = obj_item.getString("updateTime");
                    love.add(love0);
                    String content0 = obj_item.getString("title");
                    content.add(content0);
                }
            }
            myAdapter.notifyDataSetChanged();
            fragment_joke_fresh.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化数据
     */
    private void init() {
        joke_portrait = (ImageView) v.findViewById(R.id.joke_portrait);
        joke_name = (TextView) v.findViewById(R.id.joke_name);
        joke_love = (TextView) v.findViewById(R.id.joke_love);
        joke_content = (TextView) v.findViewById(R.id.joke_content);
        joke_lv = (ListView) v.findViewById(R.id.joke_lv);
        fragment_joke_fresh = (SwipeRefreshLayout) v.findViewById(R.id.fragment_joke_fresh);

    }
}
