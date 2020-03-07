package ru.tohaman.testempty.dataSource

import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.toLiveData
import ru.tohaman.testempty.dbase.daos.CubeTypesDao

import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.dbase.daos.MainDao
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.dbase.entitys.PhaseItem

/**
 * The Repository ist a simple Java class that abstracts the data layer from the rest of the app
 * and mediates between different data sources, like a web service and a local cache. It hides the
 * different database operations (like SQLite queries) and provides a clean API to the ViewModel.

    Since Room doesn’t allow database queries on the main thread, then we use suspend fun
 */

class ItemsRepository (private val mainDao : MainDao, private val typeDao: CubeTypesDao) : ItemDataSource {

    fun observePhase(phase: String): LiveData<List<MainDBItem>> {
        return mainDao.observePhase(phase)
    }

    suspend fun getPhase(phase: String) : List<PhaseItem> = mainDao.getPhase(phase)

    suspend fun getPhaseFromMain(phase: String): List<MainDBItem> = mainDao.getPhaseFromMain(phase)

    fun getLivePhaseFromMain(phase: String): LiveData<List<MainDBItem>> = mainDao.getLivePhaseFromMain(phase)

    fun getAllLiveDataItems() = mainDao.getAllLiveItems()

    fun getCurrentPhase() = mainDao.getCurrentPhase().toLiveData(Config(30))

    suspend fun clearMainTable() = mainDao.deleteAllItems()

    suspend fun clearCurrentTable() = mainDao.deleteCurrentItems()

    suspend fun insertItems2CurrentTable(curPhaseList : List<PhaseItem>) = mainDao.insertCurrentItems(curPhaseList)

    suspend fun insert2Main(item: MainDBItem) = mainDao.insert(item)

    suspend fun insert2Main(items: List<MainDBItem>) = mainDao.insert(items)


    suspend fun getCubeTypes() = typeDao.getAllItems()

    suspend fun clearTypesTable() = typeDao.deleteAllItems()

    suspend fun insert2Type(item: CubeType) = typeDao.insert(item)

    suspend fun insert2Type(items: List<CubeType>) = typeDao.insert(items)
}