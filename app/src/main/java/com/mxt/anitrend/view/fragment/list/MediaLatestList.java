package com.mxt.anitrend.view.fragment.list;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder;
import com.mxt.anitrend.util.KeyUtil;

public class MediaLatestList extends MediaBrowseFragment {

    public static MediaLatestList newInstance(Bundle params, QueryContainerBuilder queryContainer) {
        Bundle args = new Bundle(params);
        args.putParcelable(KeyUtil.Companion.getArg_graph_params(), queryContainer);
        MediaLatestList fragment = new MediaLatestList();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIsFilterable(false);
    }

    @Override
    public void makeRequest() {
        Bundle bundle = getViewModel().getParams();
        queryContainer.putVariable(KeyUtil.Companion.getArg_page(), getPresenter().getCurrentPage());
        bundle.putParcelable(KeyUtil.Companion.getArg_graph_params(), queryContainer);
        getViewModel().requestData(KeyUtil.Companion.getMEDIA_BROWSE_REQ(), getContext());
    }
}
