package ru.tohaman.rg2.ui.settings

import android.content.SharedPreferences
import android.os.Handler
import android.widget.SeekBar
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.rg2.Constants.ALL_INTERNET
import ru.tohaman.rg2.Constants.GOD_MODE
import ru.tohaman.rg2.Constants.IS_SCREEN_ALWAYS_ON
import ru.tohaman.rg2.Constants.IS_TEXT_SELECTABLE
import ru.tohaman.rg2.Constants.IS_VIDEO_SCREEN_ON
import ru.tohaman.rg2.Constants.NOT_USE_INTERNET
import ru.tohaman.rg2.Constants.ONLY_WIFI
import ru.tohaman.rg2.Constants.ON_START_MINI_HELP
import ru.tohaman.rg2.Constants.SHOW_FAB
import ru.tohaman.rg2.Constants.TEXT_SIZE
import ru.tohaman.rg2.Constants.THEME
import ru.tohaman.rg2.DebugTag.TAG
import timber.log.Timber

class SettingsViewModel: ViewModel(), KoinComponent {
    private val sp = get<SharedPreferences>()

    enum class Themes (val themeName: String){ Dark("AppTheme"), Light("AppThemeLight")}

    private val theme = sp.getString(THEME, "AppTheme")
    var isThemeDark = ObservableBoolean()

    private val _textSize = sp.getInt(TEXT_SIZE, 2)
    var textSize = ObservableInt(_textSize)

    init {
        if (theme == Themes.Dark.themeName) isThemeDark.set(true) else isThemeDark.set(false)
    }

    fun darkThemeSelect() {
        val theme = Themes.Dark.themeName
        sp.edit().putString(THEME, theme).apply()
    }

    fun lightThemeSelect() {
        val theme = Themes.Light.themeName
        sp.edit().putString(THEME, theme).apply()
    }

    fun needShowFabChange(value: Boolean) {
        sp.edit().putBoolean(SHOW_FAB, value).apply()
    }

    fun onTextSeek(): SeekBar.OnSeekBarChangeListener? {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSize.set(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                sp.edit().putInt(TEXT_SIZE, textSize.get()).apply()
            }
        }
    }

    /**Задаем параметры отлючения экрана*/
    //Общие
    private val _isScreenAlwaysOn = sp.getBoolean(IS_SCREEN_ALWAYS_ON, false)
    var isScreenAlwaysOn = ObservableBoolean(_isScreenAlwaysOn)

    fun isScreenAlwaysOnChange(value: Boolean) {
        sp.edit().putBoolean(IS_SCREEN_ALWAYS_ON, value).apply()
    }
    //Для просмотра YouTube
    private val _isYoutubeScreenAlwaysOn = sp.getBoolean(IS_VIDEO_SCREEN_ON, false)
    var isYoutubeScreenAlwaysOn = ObservableBoolean(_isYoutubeScreenAlwaysOn)

    fun isYoutubeScreenAlwaysOnChange(value: Boolean) {
        sp.edit().putBoolean(IS_VIDEO_SCREEN_ON, value).apply()
    }

    //Параметр выделения текста
    private val _isTextSelectable = sp.getBoolean(IS_TEXT_SELECTABLE, false)
    var isTextSelectable = ObservableBoolean(_isTextSelectable)

    fun isTextSelectableChange(value: Boolean) {
        sp.edit().putBoolean(IS_TEXT_SELECTABLE, value).apply()
    }

    //МиниХелп при старте программы
    private val _onStartMiniHelp = sp.getBoolean(ON_START_MINI_HELP, true)
    var onStartMiniHelp = ObservableBoolean(_onStartMiniHelp)

    private var _godMode = sp.getBoolean(GOD_MODE, false)
    var godMode = ObservableBoolean(_godMode)
    var godCount = 0

    fun miniHelpTextClick() {
        if (godCount == 6) {
            godCount += 1
            _godMode = !_godMode
            godMode.set(_godMode)
            sp.edit().putBoolean(GOD_MODE, _godMode).apply()
        } else {
            godCount += 1
            Handler().postDelayed({ godCount = 0}, 2500)
        }

    }

    fun isOnStartMiniHelpEnabled(value: Boolean) {
        sp.edit().putBoolean(ON_START_MINI_HELP, value).apply()
    }

    //Использование интернета
    private var _allInternet = sp.getBoolean(ALL_INTERNET, true)
    var allInternet = ObservableBoolean(_allInternet)

    private var _onlyWiFi = sp.getBoolean(ONLY_WIFI, false)
    var onlyWiFi = ObservableBoolean(_onlyWiFi)

    private var _doNotUseInternet = sp.getBoolean(NOT_USE_INTERNET, false)
    var doNotUseInternet = ObservableBoolean(_doNotUseInternet)

    fun allInternetSelect() {
        _allInternet = true
        _onlyWiFi = false
        _doNotUseInternet = false
        saveInternetSettings()
    }

    fun onlyWiFiSelect() {
        _allInternet = false
        _onlyWiFi = true
        _doNotUseInternet = false
        saveInternetSettings()
    }

    fun doNotUseInternetSelect() {
        _allInternet = false
        _onlyWiFi = false
        _doNotUseInternet = true
        saveInternetSettings()
    }

    private fun saveInternetSettings() {
        allInternet.set(_allInternet)
        onlyWiFi.set(_onlyWiFi)
        doNotUseInternet.set(_doNotUseInternet)

        sp.edit().putBoolean(ALL_INTERNET, _allInternet).apply()
        sp.edit().putBoolean(ONLY_WIFI, _onlyWiFi).apply()
        sp.edit().putBoolean(NOT_USE_INTERNET, _doNotUseInternet).apply()
    }
}