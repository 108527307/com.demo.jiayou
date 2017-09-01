package com.example.jiayou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.SearchAdapter;
import utils.ActivityManager;

/**
 * Created by Administrator on 2016/6/6 0006.
 */
public class Search_activity extends ActivityManager implements
        OnGetPoiSearchResultListener, OnGetSuggestionResultListener, View.OnClickListener, AdapterView.OnItemClickListener {
    private EditText Input;
    private ImageView Search;
    private ListView result;


    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private List<Map<String, Object>> PoiInfo = new ArrayList<>();
    private SearchAdapter sa;
    private List<PoiInfo> AddList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        init();
        sa = new SearchAdapter(Search_activity.this, PoiInfo);
        result.setAdapter(sa);
    }
    /**
     *
初始化view
     * */
    private void init() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        Input = (EditText) findViewById(R.id.input_edittext);
        Search = (ImageView) findViewById(R.id.btn_search);
        result = (ListView) findViewById(R.id.search);

        Search.setOnClickListener(this);

        result.setOnItemClickListener(this);
    }
    /**
     *获取返回信息

     * */
    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(Search_activity.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            PoiInfo.clear();

            AddList = result.getAllPoi();
            for (int i = 0; i < AddList.size(); i++) {
                Map<String, Object> Map = new HashMap<>();
                Map.put("Title", AddList.get(i).name);
                Map.put("addre", AddList.get(i).address);
                Map.put("location", AddList.get(i).location);
                PoiInfo.add(Map);
            }
            Toast.makeText(this, "成功找到", Toast.LENGTH_LONG).show();
            sa.notifyDataSetChanged();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
    }
    /**
     *处理点击事件

     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                String str = Input.getText().toString().trim();
                if ("".equals(str)) {
                    Toast.makeText(this, "请输入地点", Toast.LENGTH_LONG).show();
                } else {
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city(getIntent().getStringExtra("city"))
                            .keyword(str).pageCapacity(30)
                            .pageNum(1));
                }break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("re", (Parcelable) AddList.get(position));
        if (getIntent().getIntExtra("flag", -1) == 0)
            setResult(1010, intent);
        else if (getIntent().getIntExtra("flag", -1) == 1)
            setResult(1020, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        mPoiSearch.destroy();
        super.onDestroy();
    }
}
