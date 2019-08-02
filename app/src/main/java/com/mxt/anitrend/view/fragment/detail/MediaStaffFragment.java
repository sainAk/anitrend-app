package com.mxt.anitrend.view.fragment.detail;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.annimon.stream.IntPair;
import com.mxt.anitrend.R;
import com.mxt.anitrend.adapter.recycler.group.GroupStaffRoleAdapter;
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList;
import com.mxt.anitrend.model.entity.anilist.edge.StaffEdge;
import com.mxt.anitrend.model.entity.base.StaffBase;
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer;
import com.mxt.anitrend.model.entity.container.body.EdgeContainer;
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder;
import com.mxt.anitrend.model.entity.group.RecyclerItem;
import com.mxt.anitrend.presenter.fragment.MediaPresenter;
import com.mxt.anitrend.util.CompatUtil;
import com.mxt.anitrend.util.GraphUtil;
import com.mxt.anitrend.util.GroupingUtil;
import com.mxt.anitrend.util.KeyUtil;
import com.mxt.anitrend.view.activity.detail.StaffActivity;

import java.util.Collections;

/**
 * Created by max on 2018/01/18.
 */

public class MediaStaffFragment extends FragmentBaseList<RecyclerItem, ConnectionContainer<EdgeContainer<StaffEdge>>, MediaPresenter> {

    private @KeyUtil.MediaType String mediaType;
    private long mediaId;

    public static MediaStaffFragment newInstance(Bundle args) {
        MediaStaffFragment fragment = new MediaStaffFragment();
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
        if(getArguments() != null) {
            mediaId = getArguments().getLong(KeyUtil.Companion.getArg_id());
            mediaType = getArguments().getString(KeyUtil.Companion.getArg_mediaType());
        } setMColumnSize(R.integer.grid_giphy_x3); setIsPager(true);
        setMAdapter(new GroupStaffRoleAdapter(getContext()));
        setPresenter(new MediaPresenter(getContext()));
        setViewModel(true);
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    @Override
    protected void updateUI() {
        setSwipeRefreshLayoutEnabled(false);
        injectAdapter();
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    @Override
    public void makeRequest() {
        QueryContainerBuilder queryContainer = GraphUtil.INSTANCE.getDefaultQuery(getIsPager())
                .putVariable(KeyUtil.Companion.getArg_id(), mediaId)
                .putVariable(KeyUtil.Companion.getArg_type(), mediaType)
                .putVariable(KeyUtil.Companion.getArg_page(), getPresenter().getCurrentPage());

        getViewModel().getParams().putParcelable(KeyUtil.Companion.getArg_graph_params(), queryContainer);
        getViewModel().requestData(KeyUtil.Companion.getMEDIA_STAFF_REQ(), getContext());
    }

    @Override
    public void onChanged(@Nullable ConnectionContainer<EdgeContainer<StaffEdge>> content) {
        EdgeContainer<StaffEdge> edgeContainer;
        if (content != null && (edgeContainer = content.getConnection()) != null) {
            if(!edgeContainer.isEmpty()) {
                if (edgeContainer.hasPageInfo())
                    getPresenter().setPageInfo(edgeContainer.getPageInfo());
                if (!edgeContainer.isEmpty())
                    onPostProcessed(GroupingUtil.INSTANCE.groupStaffByRole(edgeContainer.getEdges(), getMAdapter().getData()));
                else
                    onPostProcessed(Collections.emptyList());
            }
        } else
            onPostProcessed(Collections.emptyList());
        if(getMAdapter().getItemCount() < 1)
            onPostProcessed(null);
    }

    /**
     * When the target view from {@link View.OnClickListener}
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the click index
     */
    @Override
    public void onItemClick(View target, IntPair<RecyclerItem> data) {
        switch (target.getId()) {
            case R.id.container:
                Intent intent = new Intent(getActivity(), StaffActivity.class);
                intent.putExtra(KeyUtil.Companion.getArg_id(), ((StaffBase)data.getSecond()).getId());
                CompatUtil.INSTANCE.startRevealAnim(getActivity(), target, intent);
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
    public void onItemLongClick(View target, IntPair<RecyclerItem> data) {

    }
}
