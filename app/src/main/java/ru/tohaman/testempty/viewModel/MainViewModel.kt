package ru.tohaman.testempty.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tohaman.testempty.dbase.ItemsRepository


class MainViewModel(app: Application) : AndroidViewModel (app) {
    private val repository = ItemsRepository(app)
    var curPhase = "BEGIN"
    var curItem = MutableLiveData<String>()

    init {
        curItem.value = "000000"
    }

    fun getCurItem() : LiveData<String> {return curItem}

    val mainMenuItems = repository.getPhaseItems(curPhase)

    internal val allItems = repository.getAllItems()

    fun onMainMenuItemClick(index: Int) {
        //curItem.value = index.toString()
        Log.d("DEB", "Index - $index")
    }

}