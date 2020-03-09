package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.adapters.MenuAdapter
import ru.tohaman.testempty.databinding.FragmentLearnMenuBinding
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import timber.log.Timber

class LearnMenuFragment : Fragment() {
    private val learnViewModel by sharedViewModel<LearnViewModel>()
    private var ctId : Int = 0
    private lateinit var binding : FragmentLearnMenuBinding

    companion object {
        private const val ARG_CUBE = "cubeType"
        fun newInstance(cubeType: CubeType) = LearnMenuFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_CUBE, cubeType.id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val id = it.getInt(ARG_CUBE)
            ctId = id
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        binding = FragmentLearnMenuBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@LearnMenuFragment
                menuList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

                val menuAdapter = MenuAdapter(MenuAdapter.OnClickListener { onMenuItemClick(it) })
                learnViewModel.mainDBItemLiveArray[ctId].observe(viewLifecycleOwner, Observer {
                    menuAdapter.refreshItems(it)
                })
                menuList.adapter = menuAdapter

                //Настраиваем кнопку... потом ее уберем
                val button = nextButton
                learnViewModel.liveDataCubeTypes.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        button.text = it[ctId].curPhase
                    }
                })
                button.setOnClickListener {
                    learnViewModel.onSomeButtonClick()
                    //Navigation.findNavController(view).navigate(R.id.action_title_screen_to_register)
                }

            }
        return binding.root
    }

    private fun onMenuItemClick(item: MainDBItem) {
        Timber.d("$TAG onItemClick - $item")
        learnViewModel.onMainMenuItemClick(item)
    }
}
