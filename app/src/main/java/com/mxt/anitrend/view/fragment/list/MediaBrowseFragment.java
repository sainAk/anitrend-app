package com.mxt.anitrend.view.fragment.list;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.annimon.stream.IntPair;
import com.annimon.stream.Stream;
import com.mxt.anitrend.R;
import com.mxt.anitrend.adapter.recycler.index.MediaAdapter;
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList;
import com.mxt.anitrend.model.entity.anilist.Genre;
import com.mxt.anitrend.model.entity.anilist.MediaTag;
import com.mxt.anitrend.model.entity.base.MediaBase;
import com.mxt.anitrend.model.entity.container.body.PageContainer;
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder;
import com.mxt.anitrend.presenter.fragment.MediaPresenter;
import com.mxt.anitrend.util.Settings;
import com.mxt.anitrend.util.CompatUtil;
import com.mxt.anitrend.util.DateUtil;
import com.mxt.anitrend.util.DialogUtil;
import com.mxt.anitrend.util.GenreTagUtil;
import com.mxt.anitrend.util.KeyUtil;
import com.mxt.anitrend.util.MediaActionUtil;
import com.mxt.anitrend.util.MediaBrowseUtil;
import com.mxt.anitrend.util.NotifyUtil;
import com.mxt.anitrend.view.activity.detail.MediaActivity;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by max on 2018/02/03.
 * Multi purpose media browse fragment
 */

public class MediaBrowseFragment extends FragmentBaseList<MediaBase, PageContainer<MediaBase>, MediaPresenter> {

    protected QueryContainerBuilder queryContainer;
    private MediaBrowseUtil mediaBrowseUtil;

    public static MediaBrowseFragment newInstance(Bundle params, QueryContainerBuilder queryContainer) {
        Bundle args = new Bundle(params);
        args.putParcelable(KeyUtil.Companion.getArg_graph_params(), queryContainer);
        MediaBrowseFragment fragment = new MediaBrowseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static MediaBrowseFragment newInstance(Bundle params) {
        Bundle args = new Bundle(params);
        MediaBrowseFragment fragment = new MediaBrowseFragment();
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
            queryContainer = getArguments().getParcelable(KeyUtil.Companion.getArg_graph_params());
            mediaBrowseUtil = getArguments().getParcelable(KeyUtil.Companion.getArg_media_util());
        }
        if(mediaBrowseUtil == null)
            mediaBrowseUtil = new MediaBrowseUtil(true);

        setIsPager(true); setIsFilterable(mediaBrowseUtil.isFilterEnabled());
        setMColumnSize(mediaBrowseUtil.isCompactType() ? R.integer.grid_giphy_x3 : R.integer.grid_list_x2);
        setMAdapter(new MediaAdapter(getContext(), mediaBrowseUtil.isCompactType()));
        setPresenter(new MediaPresenter(getContext()));
        setViewModel(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(mediaBrowseUtil.isBasicFilter()) {
            menu.findItem(R.id.action_type).setVisible(false);
            menu.findItem(R.id.action_year).setVisible(false);
            menu.findItem(R.id.action_status).setVisible(false);
            menu.findItem(R.id.action_genre).setVisible(false);
            menu.findItem(R.id.action_tag).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getContext() != null)
            switch (item.getItemId()) {
                case R.id.action_sort:
                    DialogUtil.Companion.createSelection(getContext(), R.string.app_filter_sort, CompatUtil.INSTANCE.getIndexOf(KeyUtil.Companion.getMediaSortType(),
                            getPresenter().getSettings().getMediaSort()), CompatUtil.INSTANCE.capitalizeWords(KeyUtil.Companion.getMediaSortType()),
                            (dialog, which) -> {
                                if(which == DialogAction.POSITIVE)
                                    getPresenter().getSettings().setMediaSort(KeyUtil.Companion.getMediaSortType()[dialog.getSelectedIndex()]);
                            });
                    return true;
                case R.id.action_order:
                    DialogUtil.Companion.createSelection(getContext(), R.string.app_filter_order, CompatUtil.INSTANCE.getIndexOf(KeyUtil.Companion.getSortOrderType(),
                            getPresenter().getSettings().getSortOrder()), CompatUtil.INSTANCE.getStringList(getContext(), R.array.order_by_types),
                            (dialog, which) -> {
                                if(which == DialogAction.POSITIVE)
                                    getPresenter().getSettings().setSortOrder(KeyUtil.Companion.getSortOrderType()[dialog.getSelectedIndex()]);
                            });
                    return true;
                case R.id.action_genre:
                    List<Genre> genres = getPresenter().getDatabase().getGenreCollection();
                    if(CompatUtil.INSTANCE.isEmpty(genres)) {
                        NotifyUtil.INSTANCE.makeText(getContext(), R.string.app_splash_loading, R.drawable.ic_warning_white_18dp, Toast.LENGTH_SHORT).show();
                        getPresenter().checkGenresAndTags(getActivity());
                    } else {
                        Map<Integer, String> genresIndexMap = getPresenter()
                                .getSettings().getSelectedGenres();

                        Integer[] selectedGenres = Stream.of(genresIndexMap)
                                .map(Map.Entry::getKey)
                                .toArray(Integer[]::new);

                        DialogUtil.Companion.createCheckList(getContext(), R.string.app_filter_genres, genres, selectedGenres,
                                (dialog, which, text) -> false, (dialog, which) -> {
                                    switch (which) {
                                        case POSITIVE:
                                            Map<Integer, String> selectedIndices = GenreTagUtil.Companion
                                                    .createGenreSelectionMap(genres, dialog.getSelectedIndices());

                                            getPresenter().getSettings()
                                                    .setSelectedGenres(selectedIndices);
                                            break;
                                        case NEGATIVE:
                                            getPresenter().getSettings()
                                                    .setSelectedGenres(new WeakHashMap<>());
                                            break;
                                    }
                                });
                    }
                    return true;
                case R.id.action_tag:
                    List<MediaTag> tagList = getPresenter().getDatabase().getMediaTags();
                    if(CompatUtil.INSTANCE.isEmpty(tagList)) {
                        NotifyUtil.INSTANCE.makeText(getContext(), R.string.app_splash_loading, R.drawable.ic_warning_white_18dp, Toast.LENGTH_SHORT).show();
                        getPresenter().checkGenresAndTags(getActivity());
                    } else {
                        Map<Integer, String> tagsIndexMap = getPresenter()
                                .getSettings().getSelectedTags();

                        Integer[] selectedTags = Stream.of(tagsIndexMap)
                                .map(Map.Entry::getKey)
                                .toArray(Integer[]::new);

                        DialogUtil.Companion.createCheckList(getContext(), R.string.app_filter_tags, tagList, selectedTags,
                                (dialog, which, text) -> false, (dialog, which) -> {
                                    switch (which) {
                                        case POSITIVE:
                                            Map<Integer, String> selectedIndices = GenreTagUtil.Companion
                                                    .createTagSelectionMap(tagList, dialog.getSelectedIndices());

                                            getPresenter().getSettings()
                                                    .setSelectedTags(selectedIndices);
                                            break;
                                        case NEGATIVE:
                                            getPresenter().getSettings()
                                                    .setSelectedTags(new WeakHashMap<>());
                                            break;
                                    }
                                });
                    }
                    return true;
                case R.id.action_type:
                    if (CompatUtil.INSTANCE.equals(queryContainer.getVariable(KeyUtil.Companion.getArg_mediaType()), KeyUtil.Companion.getANIME())) {
                        DialogUtil.Companion.createSelection(getContext(), R.string.app_filter_show_type, CompatUtil.INSTANCE.getIndexOf(KeyUtil.Companion.getAnimeFormat(),
                                getPresenter().getSettings().getAnimeFormat()), CompatUtil.INSTANCE.getStringList(getContext(), R.array.anime_formats),
                                (dialog, which) -> {
                                    if(which == DialogAction.POSITIVE)
                                        getPresenter().getSettings().setAnimeFormat(KeyUtil.Companion.getAnimeFormat()[dialog.getSelectedIndex()]);
                                });
                    } else {
                        DialogUtil.Companion.createSelection(getContext(), R.string.app_filter_show_type, CompatUtil.INSTANCE.getIndexOf(KeyUtil.Companion.getMangaFormat(),
                                getPresenter().getSettings().getMangaFormat()), CompatUtil.INSTANCE.getStringList(getContext(), R.array.manga_formats),
                                (dialog, which) -> {
                                    if(which == DialogAction.POSITIVE)
                                        getPresenter().getSettings().setMangaFormat(KeyUtil.Companion.getMangaFormat()[dialog.getSelectedIndex()]);
                                });
                    }
                    return true;
                case R.id.action_year:
                    final List<Integer> yearRanges = DateUtil.INSTANCE.getYearRanges(1950, 1);
                    DialogUtil.Companion.createSelection(getContext(), R.string.app_filter_year, CompatUtil.INSTANCE.getIndexOf(yearRanges, getPresenter().getSettings().getSeasonYear()),
                            yearRanges, (dialog, which) -> {
                                if(which == DialogAction.POSITIVE)
                                    getPresenter().getSettings().setSeasonYear(yearRanges.get(dialog.getSelectedIndex()));
                            });
                    return true;
                case R.id.action_status:
                    DialogUtil.Companion.createSelection(getContext(), R.string.anime, CompatUtil.INSTANCE.getIndexOf(KeyUtil.Companion.getMediaStatus(),
                            getPresenter().getSettings().getMediaStatus()), CompatUtil.INSTANCE.getStringList(getContext(), R.array.media_status),
                            (dialog, which) -> {
                                if(which == DialogAction.POSITIVE)
                                    getPresenter().getSettings().setMediaStatus(KeyUtil.Companion.getMediaStatus()[dialog.getSelectedIndex()]);
                            });
                    return true;
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void updateUI() {
        injectAdapter();
    }

    @Override
    public void makeRequest() {
        Bundle bundle = getViewModel().getParams();
        Settings pref = getPresenter().getSettings();
        queryContainer.putVariable(KeyUtil.Companion.getArg_page(), getPresenter().getCurrentPage());

        if(getIsFilterable()) {
            if(!mediaBrowseUtil.isBasicFilter()) {
                if (CompatUtil.INSTANCE.equals(queryContainer.getVariable(KeyUtil.Companion.getArg_mediaType()), KeyUtil.Companion.getMANGA())) {
                    queryContainer.putVariable(KeyUtil.Companion.getArg_startDateLike(), String.format(Locale.getDefault(),
                            "%d%%", getPresenter().getSettings().getSeasonYear()))
                            .putVariable(KeyUtil.Companion.getArg_format(), pref.getMangaFormat());
                } else {
                    queryContainer.putVariable(KeyUtil.Companion.getArg_seasonYear(), getPresenter().getSettings().getSeasonYear())
                            .putVariable(KeyUtil.Companion.getArg_format(), pref.getAnimeFormat());
                }

                queryContainer.putVariable(KeyUtil.Companion.getArg_status(), pref.getMediaStatus())
                        .putVariable(KeyUtil.Companion.getArg_genres(), GenreTagUtil.Companion.getMappedValues(pref.getSelectedGenres()))
                        .putVariable(KeyUtil.Companion.getArg_tags(), GenreTagUtil.Companion.getMappedValues(pref.getSelectedTags()));
            }
            queryContainer.putVariable(KeyUtil.Companion.getArg_sort(), pref.getMediaSort() + pref.getSortOrder());
        }
        bundle.putParcelable(KeyUtil.Companion.getArg_graph_params(), queryContainer);
        getViewModel().requestData(KeyUtil.Companion.getMEDIA_BROWSE_REQ(), getContext());
    }

    @Override
    public void onChanged(@Nullable PageContainer<MediaBase> content) {
        if(content != null) {
            if(content.hasPageInfo())
                getPresenter().setPageInfo(content.getPageInfo());
            if(!content.isEmpty())
                onPostProcessed(content.getPageData());
            else
                onPostProcessed(Collections.emptyList());
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
    public void onItemClick(View target, IntPair<MediaBase> data) {
        switch (target.getId()) {
            case R.id.container:
                Intent intent = new Intent(getActivity(), MediaActivity.class);
                intent.putExtra(KeyUtil.Companion.getArg_id(), data.getSecond().getId());
                intent.putExtra(KeyUtil.Companion.getArg_mediaType(), data.getSecond().getType());
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
    public void onItemLongClick(View target, IntPair<MediaBase> data) {
        switch (target.getId()) {
            case R.id.container:
                if(getPresenter().getSettings().isAuthenticated()) {
                    setMediaActionUtil(new MediaActionUtil.Builder()
                            .setId(data.getSecond().getId()).build(getActivity()));
                    getMediaActionUtil().startSeriesAction();
                } else
                    NotifyUtil.INSTANCE.makeText(getContext(), R.string.info_login_req, R.drawable.ic_group_add_grey_600_18dp, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
