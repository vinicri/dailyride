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


    private final boolean mCurrentUserMode;
    private User mUser;
    private final Context mContext;
    private List<RideRating> mComments = new ArrayList<>();
    private boolean mCommentsFetched = false;
    private EditListener mEditListener;

    public ProfileAdapter(Context context, User user, boolean currentUserMode){
        mCurrentUserMode = currentUserMode;
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
                    entityViewHolder.itemView.setOnClickListener(null);
                    switch (position){
                        case SUBTYPE_ENTITY_FACEBOOK:
                            //todo allow user to link facebook to his account
                            if(mUser.getFbUserId() != null) {
                                entityViewHolder.mContent.setVisibility(View.VISIBLE);
                                entityViewHolder.mTitle.setText(mContext.getResources().getQuantityString(R.plurals.profile_fb_friends, mUser.getFbTotalFriends(), mUser.getFbTotalFriends()));
                                entityViewHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_dark));
                                entityViewHolder.mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.facebook_36px));
                                entityViewHolder.mSubtitle.setVisibility(View.GONE);
                            }
                            else{
                                entityViewHolder.mContent.setVisibility(View.GONE);
                            }
                            break;
                        case SUBTYPE_ENTITY_SCHOOL:
                            entityViewHolder.mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.university_36px));
                            if(mCurrentUserMode){
                                entityViewHolder.mContent.setVisibility(View.VISIBLE);
                                if(mUser.getSchoolStatus() == User.EntityStatus.C){
                                    if(mUser.getSchool() == null){
                                        entityViewHolder.mTitle.setText(mUser.getSchoolSpecifiedName());
                                    }
                                    else {
                                        entityViewHolder.mTitle.setText(mUser.getSchool().getShortName());
                                    }
                                    entityViewHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_dark));
                                    entityViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                                    entityViewHolder.mSubtitle.setText(mContext.getString(R.string.profile_entity_approved));
                                    entityViewHolder.mSubtitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mCheck.setVisibility(View.VISIBLE);
                                }
                                else if(mUser.getSchoolStatus() == User.EntityStatus.P){
                                    //todo check this flow
                                    if(mUser.getSchool() == null){
                                        entityViewHolder.mTitle.setText(mUser.getSchoolSpecifiedName());
                                    }
                                    else {
                                        entityViewHolder.mTitle.setText(mUser.getSchool().getShortName());
                                    }
                                    entityViewHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                                    entityViewHolder.mSubtitle.setText(mContext.getString(R.string.profile_entity_pending));
                                    entityViewHolder.mSubtitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_dark));
                                    entityViewHolder.mCheck.setVisibility(View.GONE);
                                }
                                else if(mUser.getSchoolStatus() == User.EntityStatus.D){
                                    entityViewHolder.mTitle.setText(mContext.getString(R.string.profile_school_add));
                                    entityViewHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                                    entityViewHolder.mSubtitle.setText(mContext.getString(R.string.profile_entity_denied));
                                    entityViewHolder.mSubtitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mCheck.setVisibility(View.GONE);
                                    //todo open add school activity
                                }
                                else{ //empty
                                    entityViewHolder.mTitle.setText(mContext.getString(R.string.profile_school_add));
                                    entityViewHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mSubtitle.setVisibility(View.GONE);
                                    entityViewHolder.mCheck.setVisibility(View.GONE);
                                    //todo open add school activity
                                }
                                if(mEditListener != null){
                                    entityViewHolder.itemView.setOnClickListener(
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    mEditListener.onSchoolAdd();
                                                }
                                            }
                                    );
                                }
                            }
                            else{
                                if(mUser.isSchoolConfirmed()) {
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
                            entityViewHolder.mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.work_36px));
                            if(mCurrentUserMode){
                                entityViewHolder.mContent.setVisibility(View.VISIBLE);
                                if(mUser.getWorkStatus() == User.EntityStatus.C){
                                    if(mUser.getCompany() == null){
                                        entityViewHolder.mTitle.setText(mUser.getWorkSpecifiedName());
                                    }
                                    else {
                                        entityViewHolder.mTitle.setText(mUser.getCompany().getShortName());
                                    }
                                    entityViewHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_dark));
                                    entityViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                                    entityViewHolder.mSubtitle.setText(mContext.getString(R.string.profile_entity_approved));
                                    entityViewHolder.mSubtitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mCheck.setVisibility(View.VISIBLE);
                                }
                                else if(mUser.getWorkStatus() == User.EntityStatus.P){
                                    //todo check this flow
                                    if(mUser.getCompany() == null){
                                        entityViewHolder.mTitle.setText(mUser.getWorkSpecifiedName());
                                    }
                                    else {
                                        entityViewHolder.mTitle.setText(mUser.getCompany().getShortName());
                                    }
                                    entityViewHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                                    entityViewHolder.mSubtitle.setText(mContext.getString(R.string.profile_entity_pending));
                                    entityViewHolder.mSubtitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_dark));
                                    entityViewHolder.mCheck.setVisibility(View.GONE);
                                }
                                else if(mUser.getWorkStatus() == User.EntityStatus.D){
                                    entityViewHolder.mTitle.setText(mContext.getString(R.string.profile_work_add));
                                    entityViewHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                                    entityViewHolder.mSubtitle.setText(mContext.getString(R.string.profile_entity_denied));
                                    entityViewHolder.mSubtitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_dark));
                                    entityViewHolder.mCheck.setVisibility(View.GONE);
                                    //todo open add work activity
                                }
                                else{ //empty
                                    entityViewHolder.mTitle.setText(mContext.getString(R.string.profile_work_add));
                                    entityViewHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mSubtitle.setVisibility(View.GONE);
                                    entityViewHolder.mCheck.setVisibility(View.GONE);
                                    //todo open add work activity
                                }
                                if(mEditListener != null){
                                    entityViewHolder.itemView.setOnClickListener(
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    mEditListener.onWorkAdd();
                                                }
                                            }
                                    );
                                }
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
                            entityViewHolder.mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.phone_36px));

                            if(mCurrentUserMode){
                                entityViewHolder.mContent.setVisibility(View.VISIBLE);
                                if(mUser.isPhoneConfirmed()){
                                    entityViewHolder.mTitle.setText(mUser.getPhone());
                                    entityViewHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_dark));
                                    entityViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                                    entityViewHolder.mSubtitle.setText(mContext.getString(R.string.profile_phone_confirmed_subtitle));
                                    entityViewHolder.mSubtitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mCheck.setVisibility(View.VISIBLE);
                                }
                                else{
                                    entityViewHolder.mTitle.setText(mContext.getString(R.string.profile_phone_add));
                                    entityViewHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mSubtitle.setVisibility(View.VISIBLE);
                                    entityViewHolder.mSubtitle.setText(mContext.getString(R.string.profile_phone_confirmed_subtitle));
                                    entityViewHolder.mSubtitle.setTextColor(ContextCompat.getColor(mContext, R.color.gray_medium));
                                    entityViewHolder.mCheck.setVisibility(View.GONE);
                                }
                                if(mEditListener != null){
                                    entityViewHolder.itemView.setOnClickListener(
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    mEditListener.onPhoneAdd();
                                                }
                                            }
                                    );
                                }
                            }
                            else{
                                if(mUser.isPhoneConfirmed()) {
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
                    commentViewHolder.mSectionTitle.setVisibility(View.VISIBLE);
                    if(!mCommentsFetched){
                        //nothing to do
                    }
                    else{
                        if(mComments.size() == 0){
                            commentViewHolder.mProgressBox.setVisibility(View.GONE);
                            commentViewHolder.mNoCommentsBox.setVisibility(View.VISIBLE);
                            commentViewHolder.mCommentBox.setVisibility(View.GONE);
                        }
                        else{
                            commentViewHolder.mProgressBox.setVisibility(View.GONE);
                            commentViewHolder.mNoCommentsBox.setVisibility(View.GONE);
                            commentViewHolder.mCommentBox.setVisibility(View.VISIBLE);
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
                    commentViewHolder.mSectionTitle.setVisibility(View.GONE);
                    commentViewHolder.mProgressBox.setVisibility(View.GONE);
                    commentViewHolder.mCommentBox.setVisibility(View.VISIBLE);
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
        return ITEMS_COUNT + (mComments.size() == 0 ? 1 : mComments.size());
    }

    /*private int itemsCount(){
        if(mCurrentUserMode){
            return ITEMS_COUNT;
        }
        return (mUser.isPhoneConfirmed() ? 1 : 0) +
                (mUser.isWorkConfirmed() ? 1 : 0) +
                (mUser.isSchoolConfirmed() ? 1 : 0) +
                (mUser.getFbUserId() != null ? 1 : 0);
    }*/


    public void setUser(User user){
        mUser = user;
        notifyDataSetChanged();
    }

    public void setRideRatings(List<RideRating> rideRatings){
        mCommentsFetched = true;
        mComments.addAll(rideRatings);
        notifyDataSetChanged();
    }

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

    public void setEditListener(EditListener listener) {
        mEditListener = listener;
    }

    public interface EditListener{
        void onSchoolAdd();
        void onWorkAdd();
        void onPhoneAdd();
    }
}
