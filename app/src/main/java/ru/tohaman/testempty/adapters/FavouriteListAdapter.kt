package ru.tohaman.testempty.adapters

import android.annotation.SuppressLint
import android.view.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.testempty.databinding.FavouriteMenuItemBinding
import ru.tohaman.testempty.databinding.MainMenuItemBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem

/**
 * Если надо в избранном сделать плавное удаление, то надо использовать DiffUtils https://www.youtube.com/watch?v=zqTbV79WOSY
 */


class FavouriteListAdapter() : RecyclerView.Adapter<FavouriteListAdapter.MenuHolder>() {
    //тут храним список, который надо отобразить
    private var items: List<MainDBItem> = ArrayList()
    private var onClickCallBack: OnClickCallBack? = null
    private var touchHelper: ItemTouchHelper? = null

    fun attachCallBack(onClickCallBack: OnClickCallBack, touchHelper: ItemTouchHelper) {
        this.onClickCallBack = onClickCallBack
        this.touchHelper = touchHelper
    }

    //создает ViewHolder и инициализирует views для списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        return MenuHolder.from(parent)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        //тут обновляем данные ячейки (вызываем биндер холдера) передаем туда MainDBItem и onClickListener
        val item = items[position]
        holder.bind(item, onClickCallBack, touchHelper)
        holder.binding.swapButton.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper?.startDrag(holder)
            }
            false
        }
    }

    fun refreshItems(items: List<MainDBItem>) {
        this.items = items
        notifyDataSetChanged()
    }

//    class OnClickListener(val clickListener: (MainDBItem, View) -> Unit) {
//        fun onClick(menuItem: MainDBItem, view: View) = clickListener(menuItem, view)
//    }

    class MenuHolder private constructor(val binding: FavouriteMenuItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MainDBItem, onClickCallBack: OnClickCallBack?, touchHelper: ItemTouchHelper?) {
            binding.viewMenuItem = item
            binding.clickListener = onClickCallBack

            //Метод executePendingBindings используется, чтобы биндинг не откладывался, а выполнился как можно быстрее. Это критично в случае с RecyclerView.
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : MenuHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding =  FavouriteMenuItemBinding.inflate(inflater, parent, false)

                return MenuHolder(binding)
            }
        }

    }

    interface OnClickCallBack {
        fun clickItem(menuItem: MainDBItem)
        fun arrowUpClick(menuItem: MainDBItem)
        fun arrowDownClick(menuItem: MainDBItem)
    }

}