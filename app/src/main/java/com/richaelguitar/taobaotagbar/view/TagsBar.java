package com.richaelguitar.taobaotagbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.richaelguitar.taobaotagbar.R;


/**
 * Created by richaelguitar on 2018/2/25.
 * 自定义标签栏
 */

public class TagsBar extends LinearLayout {

    //子控件的数量
    private int childCount;

    //自适应大小的时候tag的宽度
    private int  wrapWidth;

    //自适应宽度大小时候指定子控件数量临界点
    private int maxWrapContentCount = 3;

    //自适应大小时候tag的默认显示个数
    private int maxShowTagCount = 6;

    //默认布局参数为自适应
   private LayoutParams defaultLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);

   //平分屏幕宽度布局参数
   private LayoutParams weightLayoutParams = new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);

   //tag点击选中颜色
    private int tagSelectedColor = getResources().getColor(R.color.colorPrimary);

   private Context mContex;

   //设置可以滚动
    private Scroller scroller;

    //记录开始位置
    private int startX;

    //记录最后位置
    private int endX;

    //记录上次滑动的位置
    private int lastMoveX;

    //左右临界边界
    private float leftBorder,rightBorder;

    //设置点击监听
    private OnTagBarItemClickLisenter tagBarItemClickLisenter;

    //屏幕宽度
    private int screenWidth;

    //当前选择位置
    private int currentPosition = -1;

    //最小有效滑动距离
    private int minTouchSlop;

    private int pageCount;





    public TagsBar(Context context) {
        this(context,null);
    }

    public TagsBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public TagsBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContex = context;
        scroller = new Scroller(context);//, new AccelerateInterpolator()
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        minTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop()*2;
        setLongClickable(false);
        init(context,attrs);

    }

    private void init(Context context,AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TagsBar);
        maxWrapContentCount = array.getInt(R.styleable.TagsBar_max_wrap_content_count,maxWrapContentCount);
        maxShowTagCount = array.getInt(R.styleable.TagsBar_max_show_tag_count,maxShowTagCount);
        tagSelectedColor = array.getColor(R.styleable.TagsBar_tag_selected_color,tagSelectedColor);
        array.recycle();
        wrapWidth = (screenWidth-getPaddingLeft()-getPaddingRight())/(maxShowTagCount);
    }

    public void setTagBarItemClickLisenter(OnTagBarItemClickLisenter tagBarItemClickLisenter) {
        this.tagBarItemClickLisenter = tagBarItemClickLisenter;
    }

    public void setTagList(@NonNull String ... tagList){
        childCount = tagList.length;
        for(int i=0;i<childCount;i++){
            final TagView tagChildView = new TagView(mContex);
            tagChildView.setText(tagList[i]);
            tagChildView.setGravity(Gravity.CENTER);
            tagChildView.setTextColor(getResources().getColor(R.color.black));
            tagChildView.setTag(i);
            //设置监听
            tagChildView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    //防选中时多次点击
                    if(currentPosition!=(int)v.getTag()){
                        //清除选中状态
                        clearAllSelectedState();
                        if(tagBarItemClickLisenter!=null){
                            tagBarItemClickLisenter.onTagItemClick((int)tagChildView.getTag());
                        }

                        if(currentPosition!=-1&&childCount>maxWrapContentCount){
                            scrollToScreenCenter(v);
                        }
                        //记录当前点击位置
                        currentPosition = (int)v.getTag();
                        ((TagView)v).setTagSelected(true,tagSelectedColor);
                    }
                }
            });

            //按需设置布局参数
            if(childCount>maxWrapContentCount){
                defaultLayoutParams = new LayoutParams(wrapWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                tagChildView.setLayoutParams(defaultLayoutParams);
            }else{
                tagChildView.setLayoutParams(weightLayoutParams);
            }
            tagChildView.setPadding(10,10,10,10);
            addView(tagChildView);
        }
    }

    private void scrollToScreenCenter(View v) {

        //设置选中条目居中
        View currentView=getChildAt((int)v.getTag());
        int left=currentView.getLeft();     //获取点击控件与父控件左侧的距离
        int width=currentView.getMeasuredWidth();   //获得控件本身宽度
        //判断左边界
        if(left+width/2<screenWidth/2){
            return;
        }
        //判断右边界
        if((rightBorder-left-width/2)<screenWidth/2){
            return;
        }

        int dx=left+width/2-screenWidth/2;
        //使条目移动到居中显示
        scrollTo(dx, 0);
    }

    private void clearAllSelectedState() {
        for(int i=0;i<childCount;i++){
           TagView tagView = (TagView) getChildAt(i);
           tagView.setTagSelected(false,getResources().getColor(R.color.black));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);
        if(changed){
            //初始化边界值
            leftBorder= getChildAt(0).getLeft();
            rightBorder = getChildAt(getChildCount()-1).getRight();
            //计算总页数
            if(childCount%maxShowTagCount != 0){
                pageCount = childCount/maxShowTagCount+1;
            }else{
                pageCount = childCount/maxShowTagCount;
            }
        }
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int  x =(int) event.getX();
        Log.e(TagsBar.class.getSimpleName(),"currentx="+event.getRawX());
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();  //终止动画
                }
                int distance = lastMoveX-x;
                if(getScrollX()+distance<leftBorder){
                    //滚回左边界
                    scrollTo((int)leftBorder,0);
                    return true;
                }else if(getScrollX()+getWidth()+distance>rightBorder){
                    //滚到右边界
                    scrollTo((int)rightBorder-getWidth(),0);
                    return true;
                }

                scrollBy((int)(distance*0.08),0);
                break;
            case MotionEvent.ACTION_UP:
                //动画回弹
                endX = getScrollX();
                int targetIndex = (endX+(int)(screenWidth/1.1))/screenWidth;

                int dx = targetIndex * screenWidth - endX;

                if(childCount%maxShowTagCount !=0&&targetIndex== pageCount-1){
                    dx = 0;
                }
                scroller.startScroll(endX,0,dx,0,1000);

                break;
        }
        postInvalidate();
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int)ev.getX();
        boolean isMove = false;
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastMoveX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                int distance =lastMoveX-x;
                if(Math.abs(distance)>minTouchSlop){
                    isMove =  true;
                }
                break;
        }
        return isMove||super.onInterceptTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller!=null&&scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(),0);
            postInvalidate();
        }
    }

    public interface OnTagBarItemClickLisenter{
        void onTagItemClick(int position);
    }
}
