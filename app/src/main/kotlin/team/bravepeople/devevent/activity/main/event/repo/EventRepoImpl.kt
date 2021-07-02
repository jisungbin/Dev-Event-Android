/*
 * DevEventAndroid © 2021 용감한 친구들. all rights reserved.
 * DevEventAndroid license is under the MIT.
 *
 * [EventRepositoryImpl.kt] created by Ji Sungbin on 21. 7. 2. 오전 3:54.
 *
 * Please see: https://github.com/brave-people/Dev-Event-Android/blob/master/LICENSE.
 */

package team.bravepeople.devevent.activity.main.event.repo

import android.content.Context
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import retrofit2.await
import team.bravepeople.devevent.activity.main.event.database.EventDatabase
import team.bravepeople.devevent.activity.main.event.database.EventEntity
import team.bravepeople.devevent.repo.GithubService
import team.bravepeople.devevent.util.Data
import team.bravepeople.devevent.util.config.PathConfig
import team.bravepeople.devevent.util.extension.parseOrNull
import team.bravepeople.devevent.util.network.Network
import team.bravepeople.devevent.util.network.NetworkNotConnected

class EventRepoImpl @Inject constructor(
    private val context: Context,
    private val client: GithubService,
    private val database: EventDatabase
) : EventRepo {

    private val databaseDao = database.dao()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun load() = callbackFlow {
        val databaseEvents = databaseDao.getEvents()
        if (databaseEvents.isEmpty()) {
            if (Network.isNetworkAvailable(context)) {
                client.getEvents().await().use { _response ->
                    val response = runCatching { parseAndSave(_response.string()) }
                    val result =
                        (response.getOrNull() ?: response.exceptionOrNull()).toEventRepoResult()
                    trySend(result)
                }
            } else {
                trySend(EventRepoResult.Error(NetworkNotConnected()))
            }
        } else {
            trySend(EventRepoResult.Success(databaseEvents))
        }

        awaitClose { close() }
    }

    override fun refresh(): Flow<EventRepoResult> {
        if (Network.isNetworkAvailable(context)) database.clearAllTables()
        return load()
    }

    override fun save(eventEntities: List<EventEntity>, endAction: suspend () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val databaseEvents = databaseDao.getEvents()
            eventEntities.filterNot { event -> databaseEvents.contains(event) }
            if (eventEntities.isNotEmpty()) {
                databaseDao.updateAll(eventEntities)
                Data.save(context, PathConfig.DatabaseSaveTime, Date().time.toString())
            }
            endAction()
        }
    }

    private fun parseAndSave(value: String): List<EventEntity> {
        val eventList = mutableListOf<EventEntity>()

        fun String?.polish(removeWhiteSpace: Boolean = true): String? {
            return if (this != null) {
                var content = replaceFirst("-", "").replaceFirst(":", "")
                    .replace("`", "").split("\n")[0].trim()
                if (removeWhiteSpace) content = content.replace(" ", "")
                content
            } else null
        }

        val data = value.split("## Dev Event만의 특별함")[1].split("---------------")[0]
        data.split("##").forEachIndexed { eventsIndex, events ->
            if (eventsIndex > 0) {
                val headerDate = events.split("\n")[0].trim()
                events.split("- __").forEachIndexed { eventIndex, event ->
                    if (eventIndex > 0) {
                        event.run {
                            var site: String? = null
                            val name: String

                            if (contains("http")) {
                                name = "__$this".split("__[")[1].split("](http")[0]
                                site = "http" + split("(http")[1].split(")")[0]
                            } else {
                                name = split("__")[0]
                            }

                            val category = parseOrNull("- 분류").polish()
                            val joinDate = (parseOrNull("- 신청") ?: parseOrNull("- 모집")).polish()
                            val startDate = parseOrNull("- 일시").polish()
                            val owner = parseOrNull("- 주최").polish(false)

                            val eventEntity = EventEntity(
                                site = site,
                                name = name,
                                category = category,
                                headerDate = headerDate,
                                joinDate = joinDate,
                                startDate = startDate,
                                owner = owner
                            )

                            eventList.add(eventEntity)
                        }
                    }
                }
            }
        }
        return eventList
    }
}