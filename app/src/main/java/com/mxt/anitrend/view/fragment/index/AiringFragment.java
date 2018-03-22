package com.mxt.anitrend.view.fragment.index;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.annimon.stream.IntPair;
import com.annimon.stream.Optional;
import com.mxt.anitrend.R;
import com.mxt.anitrend.adapter.recycler.index.SeriesAiringAdapter;
import com.mxt.anitrend.base.custom.consumer.BaseConsumer;
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList;
import com.mxt.anitrend.model.entity.anilist.MediaList;
import com.mxt.anitrend.model.entity.container.body.PageContainer;
import com.mxt.anitrend.model.entity.container.request.GraphQueryContainer;
import com.mxt.anitrend.presenter.base.BasePresenter;
import com.mxt.anitrend.util.CompatUtil;
import com.mxt.anitrend.util.GraphParameterUtil;
import com.mxt.anitrend.util.KeyUtils;
import com.mxt.anitrend.util.NotifyUtil;
import com.mxt.anitrend.util.SeriesActionUtil;
import com.mxt.anitrend.view.activity.detail.SeriesActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by max on 2017/11/03.
 */

public class AiringFragment extends FragmentBaseList<MediaList, PageContainer<MediaList>, BasePresenter> implements BaseConsumer.onRequestModelChange<MediaList> {

    public static AiringFragment newInstance() {
        return new AiringFragment();
    }

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPresenter(new BasePresenter(getContext()));
        isPager = true; mColumnSize = R.integer.grid_list_x2;
        setViewModel(true);
    }

    /**
     * Is automatically called in the @onStart Method if overriden in list implementation
     */
    @Override
    protected void updateUI() {
        if(mAdapter == null)
            mAdapter = new SeriesAiringAdapter(model, getContext());
        injectAdapter();
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    @Override
    public void makeRequest() {
        GraphQueryContainer params = GraphParameterUtil.getDefaultQueryContainer(true)
                .setVariable(KeyUtils.arg_page, getPresenter().getCurrentPage());
        getViewModel().getParams().putParcelable(KeyUtils.arg_graph_params, params);
        getViewModel().requestData(KeyUtils.MEDIA_LIST_BROWSE_REQ, getContext());
    }

    /**
     * When the target view from {@link View.OnClickListener}
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the click index
     */
    @Override
    public void onItemClick(View target, MediaList data) {
        switch (target.getId()) {
            case R.id.series_image:
                Intent intent = new Intent(getActivity(), SeriesActivity.class);
                intent.putExtra(KeyUtils.arg_id, data.getMediaId());
                intent.putExtra(KeyUtils.arg_media_type, data.getMedia().getType());
                CompatUtil.startRevealAnim(getActivity(), target, intent);
                break;
        }
    }

    /**
     * When the target view from {@link View.OnLongClickListener}
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been long clicked
     * @param data   the model that at the long click index
     */
    @Override
    public void onItemLongClick(View target, MediaList data) {
        switch (target.getId()) {
            case R.id.series_image:
                if(getPresenter().getApplicationPref().isAuthenticated()) {
                    seriesActionUtil = new SeriesActionUtil.Builder()
                            .setModel(data).build(getActivity());
                    seriesActionUtil.startSeriesAction();
                } else
                    NotifyUtil.makeText(getContext(), R.string.info_login_req, R.drawable.ic_group_add_grey_600_18dp, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onModelChanged(BaseConsumer<MediaList> consumer) {
        Optional<IntPair<MediaList>> pairOptional;
        switch (consumer.getRequestMode()) {
            case KeyUtils.MUT_SAVE_MEDIA_LIST:
                pairOptional = CompatUtil.findIndexOf(model, consumer.getChangeModel());
                if(pairOptional.isPresent()) {
                    model.set(pairOptional.get().getFirst(), consumer.getChangeModel());
                    mAdapter.onItemChanged(consumer.getChangeModel(), pairOptional.get().getFirst());
                }
                break;
            case KeyUtils.MUT_DELETE_MEDIA_LIST:
                pairOptional = CompatUtil.findIndexOf(model, consumer.getChangeModel());
                if(pairOptional.isPresent()) {
                    int index = pairOptional.get().getFirst();
                    model.remove(index);
                    mAdapter.onItemRemoved(index);
                }
                break;
        }
    }

    @Override
    public void onChanged(@Nullable PageContainer<MediaList> content) {
        if(content != null) {
            if(content.hasPageInfo())
                pageInfo = content.getPageInfo();
            if(!content.isEmpty())
                onPostProcessed(content.getPageData());
        }
    }
}