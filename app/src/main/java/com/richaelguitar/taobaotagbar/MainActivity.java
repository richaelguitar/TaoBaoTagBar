package com.richaelguitar.taobaotagbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.richaelguitar.taobaotagbar.view.TagsBar;

public class MainActivity extends AppCompatActivity {

    private TagsBar tagsBar;

    private String[] tagList = new String[]{"内衣配饰","女装","男装","鞋靴","生鲜","箱包","手机数码","母婴","百货","珠宝配饰","运动户外","家电","食品","全球购","美妆","家装","家居家纺"};

    private TextView showTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tagsBar = findViewById(R.id.tag_bar);
        showTextView = findViewById(R.id.tv_show);
        tagsBar.setTagBarItemClickLisenter(tagBarItemClickLisenter);
        tagsBar.setTagList(tagList);
        tagsBar.getChildAt(0).performClick();
    }

    private TagsBar.OnTagBarItemClickLisenter tagBarItemClickLisenter = new TagsBar.OnTagBarItemClickLisenter() {
        @Override
        public void onTagItemClick(int position) {
//            Toast.makeText(TagBarActivity.this,tagList[position],Toast.LENGTH_SHORT).show();
            showTextView.setText(tagList[position]);
        }
    };

}
