package com.mxt.anitrend.view.fragment.list

import android.os.Bundle

import com.annimon.stream.Optional
import com.annimon.stream.Stream
import com.mxt.anitrend.adapter.recycler.index.MediaListAdapter
import com.mxt.anitrend.model.entity.anilist.MediaList
import com.mxt.anitrend.model.entity.anilist.MediaListCollection
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaListUtil

import java.util.Collections

/**
 * Created by max on 2017/11/03.
 */

class AiringListFragment : MediaListFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userBase = presenter.database.currentUser
        userId = userBase!!.id
        userName = userBase.name
        mediaType = KeyUtil.getANIME()
        (mAdapter as MediaListAdapter).setCurrentUser(userName!!)
        queryContainer = GraphUtil.getDefaultQuery(false)
            .putVariable(KeyUtil.getArg_statusIn(), KeyUtil.getCURRENT())
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    override fun updateUI() {
        injectAdapter()
    }

    override fun onChanged(content: PageContainer<MediaListCollection>?) {
        if (content != null) {
            if (content.hasPageInfo())
                presenter.pageInfo = content.pageInfo
            if (!content.isEmpty) {
                val mediaOptional = Stream.of(content.pageData).findFirst()
                if (mediaOptional.isPresent) {
                    val mediaListCollection = mediaOptional.get()

                    val mediaList = Stream.of(mediaListCollection.entries)
                        .filter { media -> CompatUtil.equals(media.media.status, KeyUtil.getRELEASING()) }
                        .toList()

                    if (MediaListUtil.isTitleSort(presenter.settings.mediaListSort))
                        sortMediaListByTitle(mediaList)
                    else
                        onPostProcessed(mediaList)
                    mediaListCollectionBase = mediaListCollection
                } else
                    onPostProcessed(emptyList())
            } else
                onPostProcessed(emptyList())
        } else
            onPostProcessed(emptyList())
        if (mAdapter!!.itemCount < 1)
            onPostProcessed(null)
    }

    companion object {

        fun newInstance(): AiringListFragment {
            return AiringListFragment()
        }
    }
}
