package ru.tohaman.rg2.ui.learn

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import ru.tohaman.rg2.AppSettings
import ru.tohaman.rg2.Constants.galleryDrawables
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.dataSource.entitys.TipsItem
import ru.tohaman.rg2.utils.NonNullMutableLiveData
import timber.log.Timber

class MiniHelpViewModel: ViewModel(), KoinComponent {

    private var _tipsItem = galleryDrawables[0]
    val tipsItem = ObservableField<TipsItem>(_tipsItem)

    private var _helpCount = AppSettings.helpCount

    private fun showingMiniHelpNumber(): Int {
        val maxHelpCount = galleryDrawables.count()
        return _helpCount % maxHelpCount                           //возвращаем остаток от деления
    }

    fun nextHelp() {
        _helpCount += 1
        _tipsItem = galleryDrawables[showingMiniHelpNumber()]
        tipsItem.set(_tipsItem)
    }

    private var _onStartMiniHelpEnabled = NonNullMutableLiveData(true)
    val onStartMiniHelpEnabled : LiveData<Boolean> get() = _onStartMiniHelpEnabled

    //Проверяем нужно ли отображать миниХелп
    fun checkMiniHelpShow() {
        _onStartMiniHelpEnabled.postValue( AppSettings.onStartHelpEnabled
                and !AppSettings.godMode)
        _tipsItem = galleryDrawables[showingMiniHelpNumber()]
        tipsItem.set(_tipsItem)
    }

    fun closeHelpAndDoNotShowInSession() {
        _helpCount += 1
        Timber.d("$TAG .closeAndDoNotShowInSession $_helpCount")
        AppSettings.helpCount = _helpCount
        _onStartMiniHelpEnabled.postValue(false)
    }

    fun isDoNotShowCheckBoxEnabled(): Boolean {
        return _helpCount > 3
    }


    fun doNotShowChange(value: Boolean) {
        AppSettings.onStartHelpEnabled = !value
    }
}