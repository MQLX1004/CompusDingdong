package dingdong.com.mql.www.compusdingdong.activity.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dingdong.com.mql.www.compusdingdong.R;
import dingdong.com.mql.www.compusdingdong.activity.DetailsActivity;
import dingdong.com.mql.www.compusdingdong.activity.UserSetActivity;
import dingdong.com.mql.www.compusdingdong.activity.bean.DbActivityBean;
import dingdong.com.mql.www.compusdingdong.activity.fragment.AssociationFragment;
import dingdong.com.mql.www.compusdingdong.activity.tools.BitmapCbyte;
import dingdong.com.mql.www.compusdingdong.activity.tools.ImgUtil;

/**
 * Created by MQL on 2018/5/21.
 */

public class AssociationListAdapter extends RecyclerView.Adapter<AssociationListAdapter.ListHelpHolder> {

    private final int TYPE_FOOTER = 1;  //说明是带有Footer的
    private final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

    private Context mContext;
    private List<DbActivityBean> mDbActivityBeens;
    private DbActivityBean mDbActivityBean;
    private View mFooterView;

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getItemCount()-1);
    }

    public AssociationListAdapter(Context context, List<DbActivityBean> dbActivityBeens) {
        mContext = context;
        mDbActivityBeens = dbActivityBeens;
    }

    @Override
    public ListHelpHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ListHelpHolder(mFooterView);
        }
        View layout= LayoutInflater.from(mContext).inflate(R.layout.item_activitys,parent,false);
        return new ListHelpHolder(layout);
    }

    @Override
    public void onBindViewHolder(ListHelpHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_NORMAL){//如果是正常item
            mDbActivityBean = mDbActivityBeens.get(position);
            holder.mTitle.setText(mDbActivityBean.getActivityName());
            final String flag = mDbActivityBean.getActivityId();
            holder.mPicture.setTag(flag);
            if (flag.equals(holder.mPicture.getTag())){
                if (mDbActivityBean.getActivityPicture() != null){
                    holder.mPicture.setBackground(ImgUtil.bitmapToDrawable(mContext,BitmapCbyte.getBitmap(mDbActivityBean.getActivityPicture())));
                }else {
                    holder.mPicture.setBackground(mContext.getResources().getDrawable(R.drawable.dingdong_dack));
                }
            }
            holder.mPicture.setTag(mDbActivityBean.getActivityId());
            holder.mPicture.setTag(mDbActivityBean.getActivityId());
            holder.mInterduction.setText(mDbActivityBean.getActivityInterduction());
            holder.mTime.setText(mDbActivityBean.getActivityTime());
            holder.mSite.setText(mDbActivityBean.getActivitySite());
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= DetailsActivity.newIntent(mContext);
                    intent.putExtra("datad",mDbActivityBeens.get(position));
                    intent.putExtra("delete",String.valueOf(0));
                    mContext.startActivity(intent);
                }
            });
        }else if (getItemViewType(position) == TYPE_FOOTER){//如果是footer
            if (!AssociationFragment.isLoadMore){
                holder.mLoadmore.setText(R.string.nomoreactivity);
            }
            return;
        }
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

    @Override
    public int getItemCount() {
        if(mFooterView == null){
            return mDbActivityBeens.size();
        }else{
            return mDbActivityBeens.size() + 1;
        }
    }

    class ListHelpHolder extends RecyclerView.ViewHolder{
        private TextView mTitle;
        private ImageView mPicture;
        private TextView mInterduction;
        private TextView mTime;
        private TextView mSite;
        private TextView mLoadmore;
        private CardView mCardView;
        public ListHelpHolder(View itemView) {
            super(itemView);
            if (itemView == mFooterView){
                mLoadmore = (TextView)itemView.findViewById(R.id.AssociationItem_TV_footerloadmore);
                return;
            }
            mCardView = (CardView)itemView.findViewById(R.id.AssociationItem_CV_item);
            mTitle = (TextView)itemView.findViewById(R.id.AssociationItem_TV_title);
            mPicture = (ImageView)itemView.findViewById(R.id.AssociationItem_IV_picture);
            mInterduction = (TextView)itemView.findViewById(R.id.AssociationItem_TV_interduction);
            mTime = (TextView)itemView.findViewById(R.id.AssociationItem_TV_time);
            mSite = (TextView)itemView.findViewById(R.id.AssociationItem_TV_site);
        }
    }

}
