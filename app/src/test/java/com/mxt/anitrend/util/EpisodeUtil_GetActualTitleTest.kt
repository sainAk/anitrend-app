package com.mxt.anitrend.util

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import java.util.Arrays

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

@RunWith(Parameterized::class)
class EpisodeUtil_GetActualTitleTest {

    @Parameterized.Parameter(0)
    var inputTitle: String? = null

    @Parameterized.Parameter(1)
    var actualTitle: String? = null


    @Test
    fun getActualTile() {
        assertThat(EpisodeUtil.getActualTile(inputTitle!!), equalTo(actualTitle))
    }

    companion object {

        @Parameterized.Parameters
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf("Boku no Hero Academia - Episode 23", "Boku no Hero Academia"),
                    arrayOf("Haikyuu Season 3", "Haikyuu"),
                    arrayOf("Boku no Hero Academia Season 2 - Episode 19", "Boku no Hero Academia")
                )
            )
        }
    }

}
