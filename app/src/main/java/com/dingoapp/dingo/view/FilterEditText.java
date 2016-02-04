package com.dingoapp.dingo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;

import static com.dingoapp.dingo.util.LogUtil.makeLogTag;

/**
 * Created by guestguest on 02/02/16.
 */
public class FilterEditText extends EditText implements Filter.FilterListener{

    private static final String TAG = makeLogTag(FilterEditText.class);
    static final boolean DEBUG = false;
    private boolean mBlockCompletion;
    private int mThreshold;
    private Filterable mAdapter;
    private Filter mFilter;
    private boolean filterOn;

    public FilterEditText(Context context) {
        super(context);
        addTextChangedListener(new MyWatcher());
    }

    public FilterEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(new MyWatcher());
    }

    public FilterEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(new MyWatcher());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FilterEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        addTextChangedListener(new MyWatcher());
    }

    /**
            * <p>Changes the list of data used for auto completion. The provided list
    * must be a filterable list adapter.</p>
            *
            * <p>The caller is still responsible for managing any resources used by the adapter.
    * Notably, when the AutoCompleteTextView is closed or released, the adapter is not notified.
    * A common case is the use of {@link android.widget.CursorAdapter}, which
    * contains a {@link android.database.Cursor} that must be closed.  This can be done
    * automatically (see
                     * {@link android.app.Activity#startManagingCursor(android.database.Cursor)
            * startManagingCursor()}),
            * or by manually closing the cursor when the AutoCompleteTextView is dismissed.</p>
            *
            * @param adapter the adapter holding the auto completion data
    *
            * @see #getAdapter()
    * @see android.widget.Filterable
    * @see android.widget.ListAdapter
    */
    public <T extends Filterable> void setAdapter(T adapter) {
        /*if (mObserver == null) {
            mObserver = new PopupDataSetObserver(this);
        } else if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }*/
        mAdapter = adapter;
        if (mAdapter != null) {
            //noinspection unchecked
            mFilter = mAdapter.getFilter();
            //adapter.registerDataSetObserver(mObserver);
        } else {
            mFilter = null;
        }

        //mPopup.setAdapter(mAdapter);
    }

    /**
     * <p>Returns a filterable list adapter used for auto completion.</p>
     *
     * @return a data adapter used for auto completion
     */
    public Filterable getAdapter() {
        return mAdapter;
    }

    private class MyWatcher implements TextWatcher {
        public void afterTextChanged(Editable s) {
            doAfterTextChanged();
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            doBeforeTextChanged();
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    void doBeforeTextChanged() {
        if (mBlockCompletion) return;

        // when text is changed, inserted or deleted, we attempt to show
        // the drop down
        //mOpenBefore = isPopupShowing();
        //if (DEBUG) Log.v(TAG, "before text changed: open=" + mOpenBefore);
    }

    void doAfterTextChanged() {
        if (mBlockCompletion) return;

        // if the list was open before the keystroke, but closed afterwards,
        // then something in the keystroke processing (an input filter perhaps)
        // called performCompletion() and we shouldn't do any more processing.
        //if (DEBUG) Log.v(TAG, "after text changed: openBefore=" + mOpenBefore
        //        + " open=" + isPopupShowing());
        //if (mOpenBefore && !isPopupShowing()) {
        //    return;
        //}

        // the drop down is shown only when a minimum number of characters
        // was typed in the text view
        if (enoughToFilter()) {
            if (mFilter != null) {
                //mPopupCanBeUpdated = true;
                performFiltering(getText(), 0);
            }
        } else {
            // drop down is automatically dismissed when enough characters
            // are deleted from the text view
            //if (!mPopup.isDropDownAlwaysVisible()) {
            //    dismissDropDown();
            //}
            if (mFilter != null) {
                mFilter.filter(null);
            }
        }
    }

    /**
     * <p>Starts filtering the content of the drop down list. The filtering
     * pattern is the content of the edit box. Subclasses should override this
     * method to filter with a different pattern, for instance a substring of
     * <code>text</code>.</p>
     *
     * @param text the filtering pattern
     * @param keyCode the last character inserted in the edit box; beware that
     * this will be null when text is being added through a soft input method.
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    protected void performFiltering(CharSequence text, int keyCode) {
        mFilter.filter(text, this);
    }

    /**
     * Like {@link #setText(CharSequence)}, except that it can disable filtering.
     *
     * @param filter If <code>false</code>, no filtering will be performed
     *        as a result of this call.
     */
    public void setText(CharSequence text, boolean filter) {
        if (filter) {
            setText(text);
        } else {
            mBlockCompletion = true;
            setText(text);
            mBlockCompletion = false;
        }
    }

    /**
     * Returns <code>true</code> if the amount of text in the field meets
     * or exceeds the {@link #getThreshold} requirement.  You can override
     * this to impose a different standard for when filtering will be
     * triggered.
     */
    public boolean enoughToFilter() {
        if (DEBUG) Log.v(TAG, "Enough to filter: len=" + getText().length()
                + " threshold=" + mThreshold);
        return getText().length() >= mThreshold;
    }

    /**
     * <p>Returns the number of characters the user must type before the drop
     * down list is shown.</p>
     *
     * @return the minimum number of characters to type to show the drop down
     *
     * @see #setThreshold(int)
     *
     * @attr ref android.R.styleable#AutoCompleteTextView_completionThreshold
     */
    public int getThreshold() {
        return mThreshold;
    }

    /**
     * <p>Specifies the minimum number of characters the user has to type in the
     * edit box before the drop down list is shown.</p>
     *
     * <p>When <code>threshold</code> is less than or equals 0, a threshold of
     * 1 is applied.</p>
     *
     * @param threshold the number of characters to type before the drop down
     *                  is shown
     *
     * @see #getThreshold()
     *
     * @attr ref android.R.styleable#AutoCompleteTextView_completionThreshold
     */
    public void setThreshold(int threshold) {
        if (threshold <= 0) {
            threshold = 1;
        }

        mThreshold = threshold;
    }

    @Override
    public void onFilterComplete(int count) {

    }
}
