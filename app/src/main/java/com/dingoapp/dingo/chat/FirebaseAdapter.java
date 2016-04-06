package com.dingoapp.dingo.chat;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.dingoapp.dingo.chat.api.FirebaseApi;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guestguest on 05/04/16.
 */
public abstract class FirebaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

   // private final Firebase mRef;
    private ChildEventListener mListener;
    private final Class<T> mModelClass;
    private final Context mContext;
    private List<T> mModels;
    private List<T> mFilteredModels;
    private Map<String, T> mModelKeys;
    private Filter<T> mFilter;

    public FirebaseAdapter(Context context, Uri uri, Class<T> model, Filter<T> filter){
        mContext = context;
       // mRef = new Firebase(uri.toString());
        mModelClass = model;
        mModels = new ArrayList<T>();
        mModelKeys = new HashMap<String, T>();
        mFilter = filter;

        FirebaseApi.getAuthenticatedSession(mContext, uri,
                new FirebaseApi.AuthCallback() {
                    @Override
                    public void onAuthenticated(Firebase ref) {
                        addChildEventListener(ref);
                    }

                    @Override
                    public void onAuthenticationError() {

                    }
                }, true);
    }

    private void addChildEventListener(Firebase ref){
        mListener = ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                T model = dataSnapshot.getValue(FirebaseAdapter.this.mModelClass);
                mModelKeys.put(dataSnapshot.getKey(), model);

                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    mModels.add(0, model);
                } else {
                    T previousModel = mModelKeys.get(previousChildName);
                    int previousIndex = mModels.indexOf(previousModel);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(model);
                    } else {
                        mModels.add(nextIndex, model);
                    }
                }

                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // One of the mModels changed. Replace it in our list and name mapping
                String modelName = dataSnapshot.getKey();
                T oldModel = mModelKeys.get(modelName);
                T newModel = dataSnapshot.getValue(FirebaseAdapter.this.mModelClass);
                int index = mModels.indexOf(oldModel);

                mModels.set(index, newModel);
                mModelKeys.put(modelName, newModel);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                String modelName = dataSnapshot.getKey();
                T oldModel = mModelKeys.get(modelName);
                mModels.remove(oldModel);
                mModelKeys.remove(modelName);
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                // A model changed position in the list. Update our list accordingly
                String modelName = dataSnapshot.getKey();
                T oldModel = mModelKeys.get(modelName);
                T newModel = dataSnapshot.getValue(FirebaseAdapter.this.mModelClass);
                int index = mModels.indexOf(oldModel);
                mModels.remove(index);
                if (previousChildName == null) {
                    mModels.add(0, newModel);
                } else {
                    T previousModel = mModelKeys.get(previousChildName);
                    int previousIndex = mModels.indexOf(previousModel);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(newModel);
                    } else {
                        mModels.add(nextIndex, newModel);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });
    }

    //todo filter messages
    public List<T> getFilteredModels() {
        return mFilteredModels;
    }

    public List<T> getModels() {
        return mModels;
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public interface Filter<T>{
        public boolean filter(T model);
    }

}
