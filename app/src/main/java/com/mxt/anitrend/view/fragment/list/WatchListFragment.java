package com.mxt.anitrend.view.fragment.list;

import androidx.lifecycle.Lifecycle;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.mxt.anitrend.BuildConfig;
import com.mxt.anitrend.R;
import com.mxt.anitrend.adapter.recycler.index.EpisodeAdapter;
import com.mxt.anitrend.base.custom.fragment.FragmentChannelBase;
import com.mxt.anitrend.base.interfaces.event.RetroCallback;
import com.mxt.anitrend.model.entity.anilist.ExternalLink;
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer;
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder;
import com.mxt.anitrend.presenter.widget.WidgetPresenter;
import com.mxt.anitrend.util.EpisodeUtil;
import com.mxt.anitrend.util.ErrorUtil;
import com.mxt.anitrend.util.GraphUtil;
import com.mxt.anitrend.util.KeyUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by max on 2017/11/03.
 * WatchListFragment for anime types
 */

public class WatchListFragment extends FragmentChannelBase implements RetroCallback<ConnectionContainer<List<ExternalLink>>> {

    private long mediaId;
    private @KeyUtil.MediaType String mediaType;

    public static FragmentChannelBase newInstance(Bundle params, boolean popular) {
        Bundle args = new Bundle(params);
        FragmentChannelBase fragment = new WatchListFragment();
        args.putBoolean(KeyUtil.Companion.getArg_popular(), popular);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentChannelBase newInstance(List<ExternalLink> externalLinks, boolean popular) {
        FragmentChannelBase fragment = new WatchListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KeyUtil.Companion.getArg_list_model(), (ArrayList<? extends Parcelable>) externalLinks);
        args.putBoolean(KeyUtil.Companion.getArg_popular(), popular);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mediaId = getArguments().getLong(KeyUtil.Companion.getArg_id());
            mediaType = getArguments().getString(KeyUtil.Companion.getArg_mediaType());
        }
        mAdapter = new EpisodeAdapter(getContext());
        mAdapter.setClickListener(clickListener);
        setPresenter(new WidgetPresenter<>(getContext()));
        setViewModel(true);
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    @Override
    protected void updateUI() {
        injectAdapter();
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    @Override
    public void makeRequest() {
        if(externalLinks != null) {
            boolean feed = targetLink != null && targetLink.startsWith(BuildConfig.FEEDS_LINK);
            Bundle bundle = getViewModel().getParams();
            bundle.putString(KeyUtil.Companion.getArg_search(), targetLink);
            bundle.putBoolean(KeyUtil.Companion.getArg_feed(), feed);
            getViewModel().requestData(getRequestMode(feed), getContext());
        } else {
            QueryContainerBuilder queryContainer = GraphUtil.INSTANCE.getDefaultQuery(false)
                    .putVariable(KeyUtil.Companion.getArg_id(), mediaId)
                    .putVariable(KeyUtil.Companion.getArg_type(), mediaType);
            getPresenter().getParams().putParcelable(KeyUtil.Companion.getArg_graph_params(), queryContainer);
            getPresenter().requestData(KeyUtil.Companion.getMEDIA_EPISODES_REQ(), getContext(), this);
        }
    }

    private @KeyUtil.RequestType int getRequestMode(boolean feed) {
        if(feed)
            return isPopular? KeyUtil.Companion.getEPISODE_POPULAR_REQ() : KeyUtil.Companion.getEPISODE_LATEST_REQ();
        return KeyUtil.Companion.getEPISODE_FEED_REQ();
    }

    @Override
    public void onResponse(@NonNull Call<ConnectionContainer<List<ExternalLink>>> call, @NonNull Response<ConnectionContainer<List<ExternalLink>>> response) {
        if(isAlive() && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            ConnectionContainer<List<ExternalLink>> connectionContainer;
            if(response.isSuccessful() && (connectionContainer = response.body()) != null) {
                if(!connectionContainer.isEmpty()) {
                    externalLinks = connectionContainer.getConnection();
                    if(mAdapter.getItemCount() < 1 && externalLinks != null)
                        targetLink = EpisodeUtil.INSTANCE.episodeSupport(externalLinks);
                    if (targetLink == null)
                        showEmpty(getString(R.string.waring_missing_episode_links));
                    else
                        makeRequest();
                }
            } else
                Log.e(getTAG(), ErrorUtil.INSTANCE.getError(response));
        }
    }

    @Override
    public void onFailure(@NonNull Call<ConnectionContainer<List<ExternalLink>>> call, @NonNull Throwable throwable) {
        if(isAlive() && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            throwable.printStackTrace();
            Log.e(getTAG(), throwable.getMessage());
        }
    }
}
