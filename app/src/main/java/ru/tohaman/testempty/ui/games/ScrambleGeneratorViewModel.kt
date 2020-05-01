package ru.tohaman.testempty.ui.games

import android.content.SharedPreferences
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.tohaman.testempty.Constants
import ru.tohaman.testempty.Constants.BUFFER_CORNER
import ru.tohaman.testempty.Constants.BUFFER_EDGE
import ru.tohaman.testempty.Constants.CURRENT_SCRAMBLE
import ru.tohaman.testempty.Constants.SCRAMBLE_LENGTH
import ru.tohaman.testempty.Constants.SHOW_SOLVING
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.*
import ru.tohaman.testempty.dataSource.entitys.AzbukaSimpleItem
import ru.tohaman.testempty.utils.ObservableViewModel
import ru.tohaman.testempty.utils.toMutableLiveData
import timber.log.Timber

class ScrambleGeneratorViewModel: ObservableViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()

    private var _showPreloader = false
    val showPreloader = ObservableBoolean(_showPreloader)

    private var _cornerBuffer = MutableLiveData(get<SharedPreferences>().getBoolean(BUFFER_CORNER, true))
    val cornerBuffer: LiveData<Boolean> get() = _cornerBuffer

    private var _edgeBuffer = MutableLiveData(get<SharedPreferences>().getBoolean(BUFFER_EDGE, false))
    val edgeBuffer: LiveData<Boolean> get() = _edgeBuffer

    private var _scrambleLength = get<SharedPreferences>().getInt(SCRAMBLE_LENGTH, 14)
    var scrambleLength = ObservableField<String>(_scrambleLength.toString())

    private var _currentScramble = get<SharedPreferences>().getString(CURRENT_SCRAMBLE, "R F L B U2 L B' R F' D B R L F D R' D L") ?: ""
    var currentScramble = ObservableField<String>(_currentScramble)

    private var gridViewAzbukaList = listOf<AzbukaSimpleItem>()
    private var _currentAzbuka = gridViewAzbukaList.toMutableLiveData()
    val currentAzbuka: LiveData<List<AzbukaSimpleItem>> get() = _currentAzbuka

    private var _showSolving = get<SharedPreferences>().getBoolean(SHOW_SOLVING, true)
    val showSolving = ObservableBoolean(_showSolving)

    private var _solvingText = "Тут решение скрамбла для блайнда"
    val solvingText = ObservableField<String>(_solvingText)

    private var clearCube = IntArray(54) { 0 }
    private var currentCube = IntArray(54) { 0 }
    private var currentLetters = Array<String> (54) { "" }

    init {
        reloadAzbuka()
    }

    fun reloadAzbuka(){
        viewModelScope.launch (Dispatchers.IO) {
            val listDBAzbuka = repository.getAzbukaItems(Constants.CURRENT_AZBUKA)
            currentLetters = getLettersFromCurrentAzbuka(prepareAzbukaToShowInGridView(listDBAzbuka))
            clearCube = getCubeFromCurrentAzbuka(prepareAzbukaToShowInGridView(listDBAzbuka))
            gridViewAzbukaList = prepareCubeToShowInGridView(clearCube)
            _currentAzbuka.postValue(gridViewAzbukaList)
        }
    }

    fun azbukaSelect() {
        Timber.d("$TAG AzbukaSelect Pressed")
        showPreloader.set(false)
    }


    fun generateScramble() {
        Timber.d("$TAG generateScramble Pressed")
        showPreloader.set(true)
        val eBuf = edgeBuffer.value ?: true
        val cBuf = cornerBuffer.value ?: true
        val scramble = generateScrambleWithParam(eBuf, cBuf, _scrambleLength, currentLetters)
        currentScramble.set(scramble)
        currentCube = runScramble(clearCube, scramble)
        gridViewAzbukaList = prepareCubeToShowInGridView(currentCube)
        _currentAzbuka.postValue(gridViewAzbukaList)
    }

    fun cornerCheck(value: Boolean) {
        Timber.d("$TAG cornerCheck $value")
        _cornerBuffer.postValue(value)
    }

    fun edgeCheck(value: Boolean) {
        Timber.d("$TAG edgeCheck $value")
        _edgeBuffer.postValue(value)
    }

    fun lengthPlus() {
        Timber.d("$TAG lengthPlus $_scrambleLength")
        _scrambleLength += 1
        if (_scrambleLength > 40) {_scrambleLength = 40}
        else {
            scrambleLength.set(_scrambleLength.toString())
            get<SharedPreferences>().edit().putInt(SCRAMBLE_LENGTH, _scrambleLength).apply()
        }
    }

    fun lengthMinus() {
        Timber.d("$TAG lengthMinus $_scrambleLength")
        _scrambleLength -= 1
        if (_scrambleLength < 3) { _scrambleLength = 3}
        else {
            scrambleLength.set(_scrambleLength.toString())
            get<SharedPreferences>().edit().putInt(SCRAMBLE_LENGTH, _scrambleLength).apply()
        }
    }

}