/*
 * DevEventAndroid © 2021 용감한 친구들. all rights reserved.
 * DevEventAndroid license is under the MIT.
 *
 * [EventViewModel.kt] created by Ji Sungbin on 21. 6. 22. 오후 9:45.
 *
 * Please see: https://github.com/brave-people/Dev-Event-Android/blob/master/LICENSE.
 */

package team.bravepeople.devevent.activity.main.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import team.bravepeople.devevent.activity.main.event.database.EventDatabase
import team.bravepeople.devevent.activity.main.event.database.EventEntity
import team.bravepeople.devevent.activity.main.event.repo.EventRepo

@HiltViewModel
class EventViewModel @Inject constructor(
    private val database: EventDatabase,
    private val eventRepo: EventRepo
) : ViewModel() {

    private val _eventEntityList: MutableList<EventEntity> = mutableListOf()
    private val eventEntityList
        get() = _eventEntityList
            .sortedByDescending { it.name }
            .sortedByDescending { it.headerDate }
            .asReversed()
            .distinct()
            .filter { event ->
                val nowDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                (event.joinDate ?: event.startDate)?.let { _eventDate ->
                    fun String.polish() = split(".")[1].split("(")[0].toInt()

                    return@filter if (_eventDate.contains("~") && _eventDate.count { it == '.' } == 2) {
                        val eventDate = _eventDate.split("~")[1].polish()
                        eventDate >= nowDate
                    } else {
                        val eventDate = _eventDate.polish()
                        eventDate >= nowDate
                    }
                }

                return@filter true
            }

    private val _eventEntityFlow = MutableStateFlow<List<EventEntity>>(emptyList())
    val eventEntityFlow = _eventEntityFlow.asStateFlow() // flow; unnecessary getter

    fun getAllTags() =
        eventEntityFlow.value
            .mapNotNull { it.category }
            .flatMap { it.split(",") } // .flatten()?
            .distinct()
            .sorted()

    private fun updateFlow() {
        _eventEntityFlow.value = eventEntityList
    }

    fun update(event: EventEntity) {
        _eventEntityList.removeIf { it.name == event.name }
        _eventEntityList.add(event)
        eventRepo.save(
            eventEntities = listOf(event),
            endAction = {}
        )
        updateFlow()
    }

    fun reload() = viewModelScope.launch(Dispatchers.IO) {
        _eventEntityList.clear()
        _eventEntityList.addAll(database.dao().getEvents())
        updateFlow()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _eventEntityList.addAll(database.dao().getEvents())
            updateFlow()
        }
    }
}
