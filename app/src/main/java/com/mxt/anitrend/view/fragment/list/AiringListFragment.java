package com.mxt.anitrend.view.fragment.list;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.mxt.anitrend.adapter.recycler.index.MediaListAdapter;
import com.mxt.anitrend.model.entity.anilist.MediaList;
import com.mxt.anitrend.model.entity.anilist.MediaListCollection;
import com.mxt.anitrend.model.entity.base.UserBase;
import com.mxt.anitrend.model.entity.container.body.PageContainer;
import com.mxt.anitrend.util.CompatUtil;
import com.mxt.anitrend.util.GraphUtil;
import com.mxt.anitrend.util.KeyUtil;
import com.mxt.anitrend.util.MediaListUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by max on 2017/11/03.
 */

public class AiringListFragment extends MediaListFragment {

    public static AiringListFragment newInstance() {
        return new AiringListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserBase userBase = getPresenter().getDatabase().getCurrentUser();
        userId = userBase.getId(); userName = userBase.getName(); mediaType = KeyUtil.Companion.getANIME();
        ((MediaListAdapter) getMAdapter()).setCurrentUser(userName);
        queryContainer = GraphUtil.INSTANCE.getDefaultQuery(false)
                .putVariable(KeyUtil.Companion.getArg_statusIn(), KeyUtil.Companion.getCURRENT());
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    @Override
    protected void updateUI() {
        injectAdapter();
    }

    @Override
    public void onChanged(@Nullable PageContainer<MediaListCollection> content) {
        if(content != null) {
            if(content.hasPageInfo())
                getPresenter().setPageInfo(content.getPageInfo());
            if(!content.isEmpty()) {
                Optional<MediaListCollection> mediaOptional = Stream.of(content.getPageData()).findFirst();
                if(mediaOptional.isPresent()) {
                    MediaListCollection mediaListCollection = mediaOptional.get();

                    List<MediaList> mediaList = Stream.of(mediaListCollection.getEntries())
                            .filter(media -> CompatUtil.INSTANCE.equals(media.getMedia().getStatus(), KeyUtil.Companion.getRELEASING()))
                            .toList();

                    if(MediaListUtil.isTitleSort(getPresenter().getSettings().getMediaListSort()))
                        sortMediaListByTitle(mediaList);
                    else
                        onPostProcessed(mediaList);
                    mediaListCollectionBase = mediaListCollection;
                } else
                    onPostProcessed(Collections.emptyList());
            }
            else
                onPostProcessed(Collections.emptyList());
        } else
            onPostProcessed(Collections.emptyList());
        if(getMAdapter().getItemCount() < 1)
            onPostProcessed(null);
    }
}
