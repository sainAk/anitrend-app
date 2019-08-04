package com.mxt.anitrend.util

import com.mxt.anitrend.model.entity.anilist.edge.CharacterEdge
import com.mxt.anitrend.model.entity.anilist.edge.MediaEdge
import com.mxt.anitrend.model.entity.anilist.edge.StaffEdge
import com.mxt.anitrend.model.entity.base.CharacterBase
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.base.StaffBase
import com.mxt.anitrend.model.entity.group.RecyclerHeaderItem
import com.mxt.anitrend.model.entity.group.RecyclerItem

import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Test

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.Comparator
import java.util.Objects
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasSize
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class GroupingUtilTests {

    // a sorted list of MediaBase objects of all media formats
    private val mediaOfAllFormats = Stream.of(*KeyUtil.MediaFormat)
        .filter { Objects.nonNull(it) }
        .map { format ->
            val media = mock(MediaBase::class.java)
            `when`(media.format).thenReturn(format)
            media
        }
        .sorted(Comparator.comparing { it.format })
        .collect<List<MediaBase>, Any>(Collectors.toList())

    private val mediaFormatMap = mediaOfAllFormats.stream()
        .collect<Map<String, List<MediaBase>>, Any>(Collectors.groupingBy(Function { it.format }))

    // a sorted list of StaffBase objects of different languages
    private val staffOfAllLanguages = Stream.of(*languages)
        .filter { Objects.nonNull(it) }
        .sorted()
        .map { language ->
            val staff = mock(StaffBase::class.java)
            `when`(staff.language).thenReturn(language)
            staff
        }.collect<List<StaffBase>, Any>(Collectors.toList())

    private val staffLanguageMap = staffOfAllLanguages.stream()
        .collect<Map<String, List<StaffBase>>, Any>(Collectors.groupingBy(Function { it.language }))

    // a sorted list of MediaEdge objects of all media relations
    private val mediaOfAllRelations = Stream.of(*relationTypes)
        .sorted()
        .flatMap { relation ->
            val edges = Arrays.asList(
                mock(MediaEdge::class.java),
                mock(MediaEdge::class.java)
            )
            edges.forEach { edge ->
                `when`(edge.relationType).thenReturn(relation)
                val media = mock(MediaBase::class.java)
                `when`(edge.node).thenReturn(media)
            }
            edges.stream()
        }.collect<List<MediaEdge>, Any>(Collectors.toList())

    private val mediaRelationMap = mediaOfAllRelations.stream()
        .collect<Map<String, List<MediaEdge>>, Any>(Collectors.groupingBy(Function { it.relationType }))
        .entries
        .stream()
        .collect<Map<String, List<MediaBase>>, Any>(
            Collectors.toMap<Entry<String, List<MediaEdge>>, String, List<MediaBase>>(
                Function { it.key },
                { entry ->
                    entry.value
                        .stream()
                        .map(Function<MediaEdge, MediaBase> { it.node })
                        .collect(Collectors.toList<MediaBase>())
                })
        )

    // a sorted list of CharacterEdge objects of all character roles
    private val charactersOfAllRoles = Stream.of(*characterRoles)
        .sorted()
        .map { role ->
            val edge = mock(CharacterEdge::class.java)
            `when`(edge.role).thenReturn(role)
            val character = mock(CharacterBase::class.java)
            `when`(edge.node).thenReturn(character)
            edge
        }
        .collect<List<CharacterEdge>, Any>(Collectors.toList())

    private val characterRoleMap = charactersOfAllRoles.stream()
        .collect<Map<String, List<CharacterEdge>>, Any>(Collectors.groupingBy(Function { it.role }))
        .entries
        .stream()
        .collect<Map<String, List<CharacterBase>>, Any>(
            Collectors.toMap<Entry<String, List<CharacterEdge>>, String, List<CharacterBase>>(
                Function { it.key },
                { entry ->
                    entry.value
                        .stream()
                        .map(Function<CharacterEdge, CharacterBase> { it.node })
                        .collect(Collectors.toList<CharacterBase>())
                })
        )

    // a sorted list of StaffEdge objects of all staff roles
    private val staffOfAllRoles = Stream.of(*staffRoles)
        .sorted()
        .map { role ->
            val edge = mock(StaffEdge::class.java)
            `when`(edge.role).thenReturn(role)
            val staff = mock(StaffBase::class.java)
            `when`(edge.node).thenReturn(staff)
            edge
        }
        .collect<List<StaffEdge>, Any>(Collectors.toList())

    private val staffRoleMap = staffOfAllRoles.stream()
        .collect<Map<String, List<StaffEdge>>, Any>(Collectors.groupingBy(Function { it.role }))
        .entries
        .stream()
        .collect<Map<String, List<StaffBase>>, Any>(
            Collectors.toMap<Entry<String, List<StaffEdge>>, String, List<StaffBase>>(
                Function { it.key },
                { entry ->
                    entry.value
                        .stream()
                        .map(Function<StaffEdge, StaffBase> { it.node })
                        .collect(Collectors.toList<StaffBase>())
                })
        )

    // a sorted list of MediaEdge objects of all staff roles
    private val mediaOfAllStaffRoles = Stream.of(*staffRoles)
        .sorted()
        .map { role ->
            val edge = mock(MediaEdge::class.java)
            `when`(edge.staffRole).thenReturn(role)
            val media = mock(MediaBase::class.java)
            `when`(edge.node).thenReturn(media)
            edge
        }
        .collect<List<MediaEdge>, Any>(Collectors.toList())

    private val mediaStaffRoleMap = mediaOfAllStaffRoles.stream()
        .collect<Map<String, List<MediaEdge>>, Any>(Collectors.groupingBy(Function { it.staffRole }))
        .entries
        .stream()
        .collect<Map<String, List<MediaBase>>, Any>(
            Collectors.toMap<Entry<String, List<MediaEdge>>, String, List<MediaBase>>(
                Function { it.key },
                { entry ->
                    entry.value
                        .stream()
                        .map(Function<MediaEdge, MediaBase> { it.node })
                        .collect(Collectors.toList<MediaBase>())
                })
        )

    //region groupMediaByFormat

    @Test
    fun groupMediaByFormat_ifTheMediaListIsEmpty_shouldReturnAnEmptyList() {
        assertThat(GroupingUtil.groupMediaByFormat(emptyList(), null), empty())
    }

    @Test
    fun groupMediaByFormat_ifTheExistingListIsNull_shouldReturnAllItems() {


        /*
        the required result, a list containing a RecyclerHeaderItem per media format,
        followed by all media of that format
         */
        val required = mediaFormatMap.keys.stream()
            .sorted()
            .flatMap(getRecyclerItemsMapperForMap(mediaFormatMap))
            .collect<List<RecyclerItem>, Any>(Collectors.toList())


        val results = GroupingUtil.groupMediaByFormat(mediaOfAllFormats, null)

        assertThat(results, hasSize(required.size))
        assertThat(results, containsItemsOf(required))
    }

    // TODO: 18/06/18 confirm whether exisiting media objects should be included in the results
    @Test
    fun groupMediaByFormat_ifTheExistingListIsNotEmpty_shouldNotReturnExistingHeaders() {
        val existingFormats = Arrays.asList(KeyUtil.getMANGA(), KeyUtil.getOVA(), KeyUtil.getONE_SHOT())

        val existingItems = existingFormats.stream()
            .flatMap({ format ->
                val items = ArrayList<RecyclerItem>()
                items.add(RecyclerHeaderItem(format, 1))
                val media = mock(MediaBase::class.java)
                `when`(media.format).thenReturn(format)
                items.add(media)
                items.stream()
            })
            .collect(Collectors.toList<RecyclerItem>())

        /*
        The required result, a list containing a RecyclerHeaderItem per non-existing media format,
        followed by all media of that format
         */
        val required = mediaFormatMap.keys.stream()
            .sorted()
            .flatMap(getRecyclerItemsMapperForMap(
                mediaFormatMap
            ) { format -> !existingFormats.contains(format) })
            .collect<List<RecyclerItem>, Any>(Collectors.toList())

        val results = GroupingUtil.groupMediaByFormat(mediaOfAllFormats, existingItems)

        assertThat(results, hasSize(required.size))
        assertThat(results, containsItemsOf(required))
    }
    //endregion

    //region groupStaffByLanguage
    @Test
    fun groupStaffByLanguage_ifTheMediaListIsEmpty_shouldReturnAnEmptyList() {
        assertThat(GroupingUtil.groupStaffByLanguage(emptyList(), null), empty())
    }

    @Test
    fun groupStaffByLanguage_ifTheExistingListIsNull_shouldReturnAllItems() {


        /*
        the required result, a list containing a RecyclerHeaderItem per language,
        followed by all staff of that language
         */
        val required = staffLanguageMap.keys.stream()
            .sorted()
            .flatMap(getRecyclerItemsMapperForMap(staffLanguageMap))
            .collect<List<RecyclerItem>, Any>(Collectors.toList())


        val results = GroupingUtil.groupStaffByLanguage(staffOfAllLanguages, null)

        assertThat(results, hasSize(required.size))
        assertThat(results, containsItemsOf(required))
    }

    // TODO: 18/06/18 confirm whether existing staff objects should be included in the results
    @Test
    fun groupStaffByLanguage_ifTheExistingListIsNotEmpty_shouldNotReturnExistingHeaders() {
        val existingLanguages = listOf("ENGLISH")

        val existingItems = existingLanguages.stream()
            .flatMap { language ->
                val items = ArrayList<RecyclerItem>()
                items.add(RecyclerHeaderItem(language, 1))
                val staff = mock(StaffBase::class.java)
                `when`(staff.language).thenReturn(language)
                items.add(staff)
                items.stream()
            }
            .collect<List<RecyclerItem>, Any>(Collectors.toList())

        /*
        The required result, a list containing a RecyclerHeaderItem per non-existing language,
        followed by all staff of that language
         */
        val required = staffLanguageMap.keys.stream()
            .sorted()
            .flatMap(getRecyclerItemsMapperForMap(
                staffLanguageMap
            ) { language -> !existingLanguages.contains(language) })
            .collect<List<RecyclerItem>, Any>(Collectors.toList())

        val results = GroupingUtil.groupStaffByLanguage(staffOfAllLanguages, existingItems)

        assertThat(results, hasSize(required.size))
        assertThat(results, containsItemsOf(required))
    }
    //endregion

    //region groupActorMediaEdge
    @Test
    fun groupActorMediaEdge_ifEdgeListIsEmpty_shouldReturnAnEmptyList() {
        assertThat(GroupingUtil.groupActorMediaEdge(emptyList()), empty())
    }

    @Test
    fun groupActorMediaEdge_shouldReturnTheMediaAsAHeaderFollowedByVoiceActors() {
        val mediaEdges = Stream.of(*characterRoles)
            .map { role ->
                val edge = mock(MediaEdge::class.java)
                `when`(edge.characterRole).thenReturn(role)

                val va1 = mock(StaffBase::class.java)
                val va2 = mock(StaffBase::class.java)
                `when`(edge.voiceActors).thenReturn(Arrays.asList(va1, va2))

                val media = mock(MediaBase::class.java)
                `when`(edge.node).thenReturn(media)

                edge
            }.collect<List<MediaEdge>, Any>(Collectors.toList())

        val required = mediaEdges.stream()
            .flatMap { edge ->
                val items = ArrayList<RecyclerItem>()
                items.add(edge.node)
                items.addAll(edge.voiceActors)
                items.stream()
            }.collect<List<RecyclerItem>, Any>(Collectors.toList())

        val result = GroupingUtil.groupActorMediaEdge(mediaEdges)

        assertThat(result, hasSize(required.size))
        assertThat(result, containsItemsOf(required))

        val media = result.stream()
            .filter { item -> item is MediaBase }
            .map { item -> item as MediaBase }
            .collect<List<MediaBase>, Any>(Collectors.toList())
        // verify that the content type is set as header for media items

        // and the subgroup title is set to the character role
        for (i in media.indices) {
            verify(media[i]).subGroupTitle = characterRoles[i]
            verify(media[i]).contentType = KeyUtil.getRECYCLER_TYPE_HEADER()
        }

    }
    //endregion

    //region groupMediaByRelationType
    @Test
    fun groupMediaByRelationType_ifEdgeListIsEmpty_shouldReturnAnEmptyList() {
        assertThat(GroupingUtil.groupMediaByRelationType(emptyList()), empty())
    }

    @Test
    fun groupMediaByRelationType_shouldReturnAHeaderForEachRelationFollowedByMedia() {
        val required = mediaRelationMap.keys.stream()
            .sorted()
            .flatMap(getRecyclerItemsMapperForMap(mediaRelationMap))
            .collect<List<RecyclerItem>, Any>(Collectors.toList())

        val results = GroupingUtil.groupMediaByRelationType(mediaOfAllRelations)

        assertThat(results, hasSize(required.size))
        assertThat(results, containsItemsOf(required))
    }
    //endregion

    //region groupCharactersByRole

    @Test
    fun groupCharactersByRole_ifTheCharacterEdgeListIsEmpty_shouldReturnAnEmptyList() {
        assertThat(GroupingUtil.groupCharactersByRole(emptyList(), null), empty())
    }

    @Test
    fun groupCharactersByRole_ifTheExistingListIsNull_shouldReturnAllItems() {


        /*
        the required result, a list containing a RecyclerHeaderItem per role,
        followed by all characters of that role
         */
        val required = characterRoleMap.keys.stream()
            .sorted()
            .flatMap(getRecyclerItemsMapperForMap(characterRoleMap))
            .collect<List<RecyclerItem>, Any>(Collectors.toList())


        val results = GroupingUtil.groupCharactersByRole(charactersOfAllRoles, null)

        assertThat(results, hasSize(required.size))
        assertThat(results, containsItemsOf(required))
    }

    // TODO: 18/06/18 confirm whether exisiting character objects should be included in the results
    @Test
    fun groupCharactersByRole_ifTheExistingListIsNotEmpty_shouldNotReturnExistingHeaders() {
        val existingRoles = Arrays.asList(KeyUtil.getMAIN(), KeyUtil.getBACKGROUND())

        val existingItems = existingRoles.stream()
            .flatMap({ role ->
                val items = ArrayList<RecyclerItem>()
                items.add(RecyclerHeaderItem(role, 1))
                items.add(mock(CharacterBase::class.java))
                items.stream()
            })
            .collect(Collectors.toList<RecyclerItem>())

        /*
        The required result, a list containing a RecyclerHeaderItem per non-existing character role,
        followed by all characters of that role
         */
        val required = characterRoleMap.keys.stream()
            .sorted()
            .flatMap(getRecyclerItemsMapperForMap(
                characterRoleMap
            ) { role -> !existingRoles.contains(role) })
            .collect<List<RecyclerItem>, Any>(Collectors.toList())

        val results = GroupingUtil.groupCharactersByRole(charactersOfAllRoles, existingItems)

        assertThat(results, hasSize(required.size))
        assertThat(results, containsItemsOf(required))
    }
    //endregion

    //region groupStaffByRole
    @Test
    fun groupStaffByRole_ifTheStaffEdgeListIsEmpty_shouldReturnAnEmptyList() {
        assertThat(GroupingUtil.groupStaffByRole(emptyList(), null), empty())
    }

    @Test
    fun groupStaffByRole_ifTheExistingListIsNull_shouldReturnAllItems() {


        /*
        the required result, a list containing a RecyclerHeaderItem per role,
        followed by all staff of that role
         */
        val required = staffRoleMap.keys.stream()
            .sorted()
            .flatMap(getRecyclerItemsMapperForMap(staffRoleMap))
            .collect<List<RecyclerItem>, Any>(Collectors.toList())


        val results = GroupingUtil.groupStaffByRole(staffOfAllRoles, null)

        assertThat(results, hasSize(required.size))
        assertThat(results, containsItemsOf(required))
    }

    // TODO: 18/06/18 confirm whether exisiting staff objects should be included in the results
    @Test
    fun groupStaffByRole_ifTheExistingListIsNotEmpty_shouldNotReturnExistingHeaders() {
        val existingRoles = Arrays.asList("Director", "Character Design")

        val existingItems = existingRoles.stream()
            .flatMap { role ->
                val items = ArrayList<RecyclerItem>()
                items.add(RecyclerHeaderItem(role, 1))
                items.add(mock(StaffBase::class.java))
                items.stream()
            }
            .collect<List<RecyclerItem>, Any>(Collectors.toList())

        /*
        The required result, a list containing a RecyclerHeaderItem per non-existing staff role,
        followed by all staff of that role
         */
        val required = staffRoleMap.keys.stream()
            .sorted()
            .flatMap(getRecyclerItemsMapperForMap(
                staffRoleMap
            ) { role -> !existingRoles.contains(role) })
            .collect<List<RecyclerItem>, Any>(Collectors.toList())

        val results = GroupingUtil.groupStaffByRole(staffOfAllRoles, existingItems)

        assertThat(results, hasSize(required.size))
        assertThat(results, containsItemsOf(required))
    }
    //endregion

    //region groupMediaByStaffRole
    @Test
    fun groupMediaByStaffRole_ifTheMediaEdgeListIsEmpty_shouldReturnAnEmptyList() {
        assertThat(GroupingUtil.groupMediaByStaffRole(emptyList(), null), empty())
    }

    @Test
    fun groupMediaByStaffRole_ifTheExistingListIsNull_shouldReturnAllItems() {


        /*
        the required result, a list containing a RecyclerHeaderItem per role,
        followed by all media of that role
         */
        val required = mediaStaffRoleMap.keys.stream()
            .sorted()
            .flatMap(getRecyclerItemsMapperForMap(mediaStaffRoleMap))
            .collect<List<RecyclerItem>, Any>(Collectors.toList())


        val results = GroupingUtil.groupMediaByStaffRole(mediaOfAllStaffRoles, null)

        assertThat(results, hasSize(required.size))
        assertThat(results, containsItemsOf(required))
    }

    // TODO: 18/06/18 confirm whether exisiting media objects should be included in the results
    @Test
    fun groupMediaByStaffRole_ifTheExistingListIsNotEmpty_shouldNotReturnExistingHeaders() {
        val existingRoles = Arrays.asList("Director", "Character Design")

        val existingItems = existingRoles.stream()
            .flatMap { role ->
                val items = ArrayList<RecyclerItem>()
                items.add(RecyclerHeaderItem(role, 1))
                items.add(mock(MediaBase::class.java))
                items.stream()
            }
            .collect<List<RecyclerItem>, Any>(Collectors.toList())

        /*
        The required result, a list containing a RecyclerHeaderItem per non-existing staff role,
        followed by all media of that role
         */
        val required = mediaStaffRoleMap.keys.stream()
            .sorted()
            .flatMap(getRecyclerItemsMapperForMap(
                mediaStaffRoleMap
            ) { role -> !existingRoles.contains(role) })
            .collect<List<RecyclerItem>, Any>(Collectors.toList())

        val results = GroupingUtil.groupMediaByStaffRole(mediaOfAllStaffRoles, existingItems)

        assertThat(results, hasSize(required.size))
        assertThat(results, containsItemsOf(required))
    }

    companion object {

        // all media relation types
        private val relationTypes = arrayOf<String>(
            KeyUtil.getADAPTATION(),
            KeyUtil.getPREQUEL(),
            KeyUtil.getSEQUEL(),
            KeyUtil.getPARENT(),
            KeyUtil.getSIDE_STORY(),
            KeyUtil.getCHARACTER(),
            KeyUtil.getSUMMARY(),
            KeyUtil.getALTERNATIVE(),
            KeyUtil.getSPIN_OFF()
        )
        private val characterRoles =
            arrayOf<String>(KeyUtil.getMAIN(), KeyUtil.getSUPPORTING(), KeyUtil.getBACKGROUND())
        private val staffRoles = arrayOf("Character Dseign", "Director", "Music", "Series Compostion")
        private val languages = arrayOf("ENGLISH", "JAPANESE")
        //endregion

        //region test utils
        private fun <T : RecyclerItem> getRecyclerItemsMapperForMap(map: Map<String, List<T>>): Function<String, Stream<RecyclerItem>> {
            return getRecyclerItemsMapperForMap(map) { format -> true }
        }

        /**
         * Make a mapper that transforms keys from a given [java.util.Map] to a stream of recycler items
         * this stream conditionally contains a [RecyclerHeaderItem] for each key followed by the associated list of items
         * from the map
         *
         * @param map           specifies the items associated with each key
         * @param includeHeader decides if a header is included in the stream for a given key
         */
        private fun <T : RecyclerItem> getRecyclerItemsMapperForMap(
            map: Map<String, List<T>>,
            includeHeader: Predicate<String>
        ): Function<String, Stream<RecyclerItem>> {
            return { key ->
                val items = ArrayList<RecyclerItem>()
                if (includeHeader.test(key)) {
                    items.add(RecyclerHeaderItem(key, map[key].size))
                }
                items.addAll(map[key])
                items.stream()
            }
        }

        private fun <T> containsItemsOf(collection: Collection<T>): Matcher<Iterable<T>> {
            return contains(collection.stream()
                .map { Matchers.equalTo(it) }
                .collect<List<Matcher<in T>>, Any>(Collectors.toList()))
        }
    }
    //endregion
}