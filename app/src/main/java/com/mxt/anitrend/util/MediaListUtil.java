package com.mxt.anitrend.util;

import android.os.Bundle;
import androidx.annotation.NonNull;

import com.annimon.stream.Stream;
import com.mxt.anitrend.base.custom.view.widget.AutoIncrementWidget;
import com.mxt.anitrend.base.custom.view.widget.CustomSeriesManageBase;
import com.mxt.anitrend.model.entity.anilist.MediaList;
import com.mxt.anitrend.model.entity.anilist.meta.CustomList;
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder;

import java.util.List;
import java.util.Locale;

public class MediaListUtil {

    /**
     * Creates query variables for updating the status of the current users lists, use cases
     * @see CustomSeriesManageBase#persistChanges()
     * @see AutoIncrementWidget#updateModelState()
     *
     * @param model the current media list item
     */
    public static Bundle getMediaListParams(@NonNull MediaList model, @KeyUtil.ScoreFormat String scoreFormat) {
        QueryContainerBuilder queryContainer = GraphUtil.INSTANCE.getDefaultQuery(false)
                .putVariable(KeyUtil.Companion.getArg_scoreFormat(), scoreFormat);

        if (model.getId() > 0)
            queryContainer.putVariable(KeyUtil.Companion.getArg_id(), model.getId());
        queryContainer.putVariable(KeyUtil.Companion.getArg_mediaId(), model.getMediaId());
        queryContainer.putVariable(KeyUtil.Companion.getArg_listStatus(), model.getStatus());
        queryContainer.putVariable(KeyUtil.Companion.getArg_listScore(), model.getScore());
        queryContainer.putVariable(KeyUtil.Companion.getArg_listNotes(), model.getNotes());
        queryContainer.putVariable(KeyUtil.Companion.getArg_listPrivate(), model.isHidden());
        queryContainer.putVariable(KeyUtil.Companion.getArg_listPriority(), model.getPriority());
        queryContainer.putVariable(KeyUtil.Companion.getArg_listHiddenFromStatusLists(), model.isHiddenFromStatusLists());
        queryContainer.putVariable(KeyUtil.Companion.getArg_startedAt(), model.getStartedAt());
        queryContainer.putVariable(KeyUtil.Companion.getArg_completedAt(), model.getCompletedAt());

        if (model.getAdvancedScores() != null)
            queryContainer.putVariable(KeyUtil.Companion.getArg_listAdvancedScore(), model.getAdvancedScores());

        if (!CompatUtil.INSTANCE.isEmpty(model.getCustomLists())) {
            List<String> enabledCustomLists = Stream.of(model.getCustomLists())
                    .filter(CustomList::isEnabled)
                    .map(CustomList::getName)
                    .toList();
            queryContainer.putVariable(KeyUtil.Companion.getArg_listCustom(), enabledCustomLists);
        }

        queryContainer.putVariable(KeyUtil.Companion.getArg_listRepeat(), model.getRepeat());
        queryContainer.putVariable(KeyUtil.Companion.getArg_listProgress(), model.getProgress());
        queryContainer.putVariable(KeyUtil.Companion.getArg_listProgressVolumes(), model.getProgressVolumes());

        Bundle bundle = new Bundle();
        bundle.putParcelable(KeyUtil.Companion.getArg_graph_params(), queryContainer);
        return bundle;
    }

    /**
     * Checks if the sorting should be done on titles
     */
    public static boolean isTitleSort(@KeyUtil.MediaListSort String mediaSort) {
        return CompatUtil.INSTANCE.equals(mediaSort, KeyUtil.Companion.getTITLE());
    }

    /**
     * Checks if the current list items progress can be incremented beyond what it is currently at
     */
    public static boolean isProgressUpdatable(MediaList mediaList) {
        return mediaList.getMedia().getNextAiringEpisode() != null &&
                mediaList.getMedia().getNextAiringEpisode().getEpisode()
                        - mediaList.getProgress() >= 1;
    }

    /**
     * Filters by the given search term
     */
    public static boolean isFilterMatch(MediaList model, String filter) {
        return model.getMedia().getTitle().getEnglish().toLowerCase(Locale.getDefault()).contains(filter) ||
                model.getMedia().getTitle().getRomaji().toLowerCase(Locale.getDefault()).contains(filter) ||
                model.getMedia().getTitle().getOriginal().toLowerCase(Locale.getDefault()).contains(filter);
    }
}
