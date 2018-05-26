package dingdong.com.mql.www.compusdingdong.activity.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.bean.DbMealBean;
import dingdong.com.mql.www.compusdingdong.activity.bean.MealBean;
import dingdong.com.mql.www.compusdingdong.activity.fragment.AssociationFragment;
import dingdong.com.mql.www.compusdingdong.activity.fragment.RestaurantFragment;
import dingdong.com.mql.www.compusdingdong.activity.tools.BitmapCbyte;
import dingdong.com.mql.www.compusdingdong.activity.tools.ImgUtil;

/**
 * Created by MQL on 2018/5/23.
 */

public class MealListAdapter extends RecyclerView.Adapter<MealListAdapter.mealListHelpHodler> {
    private static final String TAG = "MealListAdapter";

    private final int TYPE_FOOTER = 1;  //说明是带有Footer的
    private final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

    private List<DbMealBean> mBeanList;
    private Context mContext;
    private DbMealBean bean;
    private View mFooterView;

    public MealListAdapter(List<DbMealBean> beanList, Context context) {
        mBeanList = beanList;
        mContext = context;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getItemCount()-1);
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView == null){
            return TYPE_NORMAL;
        }
        if (position == getItemCount()-1){
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }
    //此方法设置Item的跨度
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager){
            final GridLayoutManager layout = ((GridLayoutManager) manager);
            GridLayoutManager.SpanSizeLookup mGridSpanSizeLookup = new GridLayoutManager.SpanSizeLookup(){
                @Override
                public int getSpanSize(int position) {
                    if (getItemViewType(position) == TYPE_FOOTER){//如果是Footer，返回列数量
                        return layout.getSpanCount();
                    }else {
                        return 1;
                    }
                }
            };
            layout.setSpanSizeLookup(mGridSpanSizeLookup);
        }
    }

    @Override
    public mealListHelpHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mFooterView != null && viewType == TYPE_FOOTER){
            return new MealListAdapter.mealListHelpHodler(mFooterView);
        }
        View layout= LayoutInflater.from(mContext).inflate(R.layout.item_meals,parent,false);
        return new mealListHelpHodler(layout);
    }

    @Override
    public void onBindViewHolder(mealListHelpHodler holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL){
            bean = mBeanList.get(position);
            //为了防止图片错位，给每一个picture添加一个TAG作为标记
            final String flag = bean.getDbmealId();//把mealID作为标识
            holder.mMealPicture.setTag(flag);//给picture设置TAG
            if (flag.equals(holder.mMealPicture.getTag())){//判断图片TAG与当前flag是否匹配
                if (bean.getDbmealpicture() != null){
                    holder.mMealPicture.setBackground(ImgUtil.bitmapToDrawable(mContext, BitmapCbyte.getBitmap(bean.getDbmealpicture())));
                }else {
                    holder.mMealPicture.setBackground(mContext.getResources().getDrawable(R.drawable.dingdong_dack));
                }
            }
            holder.mMealName.setText(bean.getDbmealname());
            holder.mMealIntroduction.setText(bean.getDbmealintroduction());
            holder.mMealSite.setText(bean.getDbmealsite());
        }else if (getItemViewType(position) == TYPE_FOOTER){//如果是footer
            if (!RestaurantFragment.isLoadMealMore){
                holder.mLoadmore.setText(R.string.nomoreactivity);
            }
            return;
        }


    }

    @Override
    public int getItemCount() {
        if(mFooterView == null){
            return mBeanList.size();
        }else{
            return mBeanList.size() + 1;
        }
    }

    class mealListHelpHodler extends RecyclerView.ViewHolder{
        private ImageView mMealPicture;
        private TextView mMealName;
        private TextView mMealIntroduction;
        private TextView mMealSite;
        private TextView mLoadmore;
        public mealListHelpHodler(View itemView) {
            super(itemView);
            if (itemView == mFooterView){
                mLoadmore = (TextView)itemView.findViewById(R.id.AssociationItem_TV_footerloadmore);
                return;
            }
            mMealPicture = (ImageView)itemView.findViewById(R.id.MealItem_IV_mealpicture);
            mMealName = (TextView)itemView.findViewById(R.id.MealItem_TV_title);
            mMealIntroduction = (TextView)itemView.findViewById(R.id.MealItem_TV_introduction);
            mMealSite = (TextView)itemView.findViewById(R.id.MealItem_TV_site);
        }
    }
}
