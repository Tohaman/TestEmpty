package ru.tohaman.testempty.ui.settings

import android.content.SharedPreferences
import android.widget.SeekBar
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.testempty.Constants.SHOW_FAB
import ru.tohaman.testempty.Constants.TEXT_SIZE
import ru.tohaman.testempty.Constants.THEME
import ru.tohaman.testempty.DebugTag.TAG
import timber.log.Timber

class SettingsViewModel: ViewModel(), KoinComponent {
    private val sp = get<SharedPreferences>()

    enum class Themes (val themeName: String){ Dark("AppTheme"), Light("AppThemeLight")}

    private val theme = sp.getString(THEME, "AppTheme")
    var isThemeDark = ObservableBoolean()

    private val showFab = sp.getBoolean(SHOW_FAB, true)
    var needShowFab = ObservableBoolean(showFab)

    private val _textSize = sp.getInt(TEXT_SIZE, 18)
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


}