package com.dingoapp.dingo.profile;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.model.RideRating;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.util.CircleTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guestguest on 14/04/16.
 */
public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEMS_COUNT = 4; //facebook, phone, school, work

    private static final int TYPE_ENTITY = 1;
    private static final int TYPE_COMMENT = 2;

    private static final int SUBTYPE_ENTITY_FACEBOOK = 0;
    private static final int SUBTYPE_ENTITY_SCHOOL = 1;
    private static final int SUBTYPE_ENTITY_WORK = 2;
    private static final int SUBTYPE_ENTITY_PHONE = 3;


    private final boolean mShowAllItems;
    private final User mUser;
    private final Context mContext;
    private List<RideRating> mComments = new ArrayList<>();
    private boolean mCommentsFetched = false;

    public ProfileAdapter(Context context, User user, boolean showAllItems){
        mShowAllItems = showAllItems;
        mUser = user;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case TYPE_ENTITY:
                View v1 = inflater.inflate(R.layout.item_profile_entity, parent, false);
                viewHolder = new EntityViewHolder(v1);
                break;
            default:
                View v2 = inflater.inflate(R.layout.item_profile_comment, parent, false);
                viewHolder = new CommentViewHolder(v2);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case TYPE_ENTITY:
                    EntityViewHolder entityViewHolder = (EntityViewHolder)holder;
                    switch (position){
                        case SUBTYPE_ENTITY_FACEBOOK:
                            if(mShowAllItems){
                                //todo
                            }
                            else{
                                if(mUser.getFbUserId() != null) {
                                    entityViewHolder.mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.facebook_36px));
                                    entityViewHolder.mTitle.setText(mContext.getResources().getQuantityString(R.plurals.profile_fb_friends, mUser.getFbTotalFriends(), mUser.getFbTotalFriends()));
                                }
                                else{
                                    entityViewHolder.mContent.setVisibility(View.GONE);
                                }
                            }
                            break;
                        case SUBTYPE_ENTITY_SCHOOL:
                            if(mShowAllItems){
                                //todo
                            }
                            else{
                                if(mUser.isSchoolConfirmed()) {
                                    entityViewHolder.mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.university_36px));
                                    if(mUser.getSchool() == null){
                                        entityViewHolder.mTitle.setText(mUser.getSchoolSpecifiedName());
                                    }
                                    else {
                                        entityViewHolder.mTitle.setText(mUser.getSchool().getShortName());
                                    }
                                }
                                else{
                                    entityViewHolder.mContent.setVisibility(View.GONE);
                                }
                            }
                            break;
                        case SUBTYPE_ENTITY_WORK:
                            if(mShowAllItems){
                                //todo
                            }
                            else{
                                if(mUser.isWorkConfirmed()) {
                                    entityViewHolder.mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.work_36px));
                                    entityViewHolder.mTitle.setText(mUser.getCompany().getShortName());
                                }
                                else{
                                    entityViewHolder.mContent.setVisibility(View.GONE);
                                }
                            }
                            break;
                        case SUBTYPE_ENTITY_PHONE:
                            if(mShowAllItems){
                                //todo
                            }
                            else{
                                if(mUser.isPhoneConfirmed()) {
                                    entityViewHolder.mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.phone_36px));
                                    entityViewHolder.mTitle.setText(mContext.getString(R.string.profile_phone_confirmed));
                                }
                                else{
                                    entityViewHolder.mContent.setVisibility(View.GONE);
                                }
                            }
                            break;
                    }
                break;
            default:
                CommentViewHolder commentViewHolder = (CommentViewHolder)holder;
                if(position == ITEMS_COUNT){
                    if(!mCommentsFetched){
                        //nothing to do
                    }
                    else{
                        if(mComments.size() == 0){
                            commentViewHolder.mProgressBox.setVisibility(View.GONE);
                            commentViewHolder.mNoCommentsBox.setVisibility(View.VISIBLE);
                        }
                        else{
                            commentViewHolder.mProgressBox.setVisibility(View.GONE);
                            commentViewHolder.mNoCommentsBox.setVisibility(View.VISIBLE);
                            RideRating rideRating = mComments.get(position - ITEMS_COUNT);
                            Glide.with(mContext).load(DingoApiService.getPhotoUrl(rideRating.getUser()))
                                    .transform(new CircleTransform(mContext))
                                    .into(commentViewHolder.mPicture);
                            commentViewHolder.mName.setText(rideRating.getUser().getFullName());
                            commentViewHolder.mComment.setText(rideRating.getComments());
                        }
                    }
                }
                else{
                    commentViewHolder.mProgressBox.setVisibility(View.GONE);
                    commentViewHolder.mComment.setVisibility(View.VISIBLE);
                    RideRating rideRating = mComments.get(position - ITEMS_COUNT);
                    Glide.with(mContext).load(DingoApiService.getPhotoUrl(rideRating.getUser()))
                            .transform(new CircleTransform(mContext))
                            .into(commentViewHolder.mPicture);
                    commentViewHolder.mName.setText(rideRating.getUser().getFullName());
                    commentViewHolder.mComment.setText(rideRating.getComments());
                }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < ITEMS_COUNT){
            return TYPE_ENTITY;
        }
        else{
            return TYPE_COMMENT;
        }
    }

    @Override
    public int getItemCount() {
        return ITEMS_COUNT + mComments.size() == 0 ? 1 : mComments.size();
    }

    /*private int itemsCount(){
        if(mShowAllItems){
            return ITEMS_COUNT;
        }
        return (mUser.isPhoneConfirmed() ? 1 : 0) +
                (mUser.isWorkConfirmed() ? 1 : 0) +
                (mUser.isSchoolConfirmed() ? 1 : 0) +
                (mUser.getFbUserId() != null ? 1 : 0);
    }*/

    public static class EntityViewHolder extends RecyclerView.ViewHolder{

        View mContent;

        ImageView mIcon;

        TextView mTitle;
        TextView mSubtitle;

        ImageView mCheck;

        public EntityViewHolder(View itemView) {
            super(itemView);

            mContent = itemView.findViewById(R.id.content);
            mIcon = (ImageView)itemView.findViewById(R.id.icon);
            mTitle = (TextView)itemView.findViewById(R.id.title);
            mSubtitle = (TextView)itemView.findViewById(R.id.subtitle);
            mCheck = (ImageView)itemView.findViewById(R.id.check);

        }
    }


    public static class CommentViewHolder extends RecyclerView.ViewHolder{


        TextView mSectionTitle;

        View mCommentBox;
        View mNoCommentsBox;
        View mProgressBox;

        ImageView mPicture;

        TextView mName;
        TextView mComment;

        public CommentViewHolder(View itemView) {
            super(itemView);

            mSectionTitle = (TextView)itemView.findViewById(R.id.section_title);
            mPicture = (ImageView)itemView.findViewById(R.id.picture);

            mCommentBox = itemView.findViewById(R.id.comment_box);
            mNoCommentsBox = itemView.findViewById(R.id.no_comments_box);
            mProgressBox = itemView.findViewById(R.id.progress_box);

            mName = (TextView) itemView.findViewById(R.id.name);
            mComment = (TextView) itemView.findViewById(R.id.comment);

        }
    }

}
