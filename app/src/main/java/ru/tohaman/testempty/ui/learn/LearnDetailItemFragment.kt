package ru.tohaman.testempty.ui.learn

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentLearnDetailItemBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.utils.dp
import ru.tohaman.testempty.utils.toEditable
import timber.log.Timber


class LearnDetailItemFragment : Fragment() {
    private val detailViewModel by sharedViewModel<LearnDetailViewModel>()
    //private val learnViewModel by sharedViewModel<LearnViewModel>()
    private lateinit var binding: FragmentLearnDetailItemBinding
    private var fragmentNum = 0
    private lateinit var item: MainDBItem

    //Поскольку для вызова этого фрагмента НЕ используется Navigation component, то
    //передача/прием данных осуществляются классически через Bundle putInt/getInt
    companion object {
        private const val ARG_CUBE1 = "itemId"

        fun newInstance(mainDBItem: MainDBItem) = LearnDetailItemFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_CUBE1, mainDBItem.id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fragmentNum = it.getInt(ARG_CUBE1)
            Timber.d("$TAG Фрагмент DetailItem = $fragmentNum")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentLearnDetailItemBinding.inflate(inflater, container, false)
            .apply {
                item = detailViewModel.getCurrentItems()[fragmentNum]
//                Timber.d("$TAG mainDBItem = $item")
//                mainDBItem = item

                detailViewModel.liveCurrentItems.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        item = it[fragmentNum]
                        mainDBItem = item
                    }
                })

                val youTubePlayerView = content.youtubeView.youtubePlayerView
                lifecycle.addObserver(youTubePlayerView)

                youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        val videoId = item.url
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                })

                content.youtubeView.enabled = item.url != ""

                //Регистрируем контекстное меню для "Избранного" (будем вызывать при долгом нажатии)
                registerForContextMenu(favourites)

                setClickListeners(this)
            }
        return binding.root
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        activity?.menuInflater?.inflate(R.menu.favourite_context_menu, menu)
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.show_favourite -> {
                Timber.d("$TAG Показать список избранного вызвано из контекстного меню $item")
                findNavController().navigate(R.id.dialog_favourites)
                true
            }
            R.id.change_favourite -> {
                Timber.d("$TAG Сменить статус вызвано из контекстного меню")
                changeCurrentFavouriteStatus()
                true
            }
            else -> super.onContextItemSelected(menuItem)
        }
    }

    private fun setClickListeners(binding: FragmentLearnDetailItemBinding) {

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(LearnDetailFragmentDirections.actionDestLearnDetailsToRecyclerViewDialog(item.phase))
        }

        binding.favourites.setOnClickListener {
            changeCurrentFavouriteStatus()
        }

        //вызываем созданный в коде AlertDialog https://android--code.blogspot.com/2020/03/android-kotlin-alertdialog-edittext.html
        binding.writeComment.setOnClickListener {
            context?.let {
                val comment = binding.mainDBItem?.comment ?: ""
                val builder = MaterialAlertDialogBuilder(it)

                // dialog message view
                val constraintLayout = getEditTextLayout(it)
                builder.setView(constraintLayout)

                val textInputLayout = constraintLayout.findViewWithTag<TextInputLayout>("textInputLayoutTag")
                val textInputEditText = constraintLayout.findViewWithTag<TextInputEditText>("textInputEditTextTag")

                textInputEditText.text = comment.toEditable()
                textInputEditText.hint = "или алгоритм"
                builder.setTitle("Напишите свой комментарий:")
                    .setPositiveButton("OK") { _, _ ->
                        item.comment = textInputEditText.text.toString()
                        binding.mainDBItem = item
                        detailViewModel.updateComment(item)
                    }
                    .setNegativeButton("Отмена", null)
                val dialog = builder.create()
                dialog.show()

/**             Можно еще добавить слушатель на изменения вводимого текста

                textInputEditText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
*/
            }
        }

    }

    private fun changeCurrentFavouriteStatus() {
        detailViewModel.changeItemFavouriteStatus(item)
    }

    // get edit text layout
    private fun getEditTextLayout(context: Context): ConstraintLayout {
        val constraintLayout = ConstraintLayout(context)
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        constraintLayout.layoutParams = layoutParams
        constraintLayout.id = View.generateViewId()

        //Когда пользователь начинает вводить текст в текстовом поле, то подсказка, заданная в этом компоненте, всплывает над ним в специальном TextView
        //http://developer.alexanderklimov.ru/android/layout/textinputlayout.php
        val textInputLayout = TextInputLayout(context)
        textInputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        layoutParams.setMargins(
            16.dp(context),
            8.dp(context),
            16.dp(context),
            8.dp(context)
        )
        textInputLayout.layoutParams = layoutParams
        textInputLayout.hint = ""
        textInputLayout.id = View.generateViewId()
        textInputLayout.tag = "textInputLayoutTag"


        val textInputEditText = TextInputEditText(context)
        textInputEditText.id = View.generateViewId()
        textInputEditText.tag = "textInputEditTextTag"

        textInputLayout.addView(textInputEditText)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintLayout.addView(textInputLayout)
        return constraintLayout
    }

}
