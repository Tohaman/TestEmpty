package ru.tohaman.testempty.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import timber.log.Timber

class SettingsFragment  : Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        Timber.d("$TAG bottomNavHide")
        uiUtilViewModel.hideBottomNav()
        return view
    }
}