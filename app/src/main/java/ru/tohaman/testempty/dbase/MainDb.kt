package ru.tohaman.testempty.dbase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.tohaman.testempty.R
import ru.tohaman.testempty.utils.ioThread

/**
Аннотацией Database помечаем основной класс по работе с базой данных. Этот класс должен быть
абстрактным и наследовать RoomDatabase.
В параметрах аннотации Database указываем, какие Entity будут использоваться, и версию базы.
Для каждого Entity класса из списка entities будет создана таблица.
В Database классе необходимо описать абстрактные методы для получения Dao объектов, которые нам понадобятся.
 */

@Database(entities = [ListPagerDBItem::class], version = 1)
abstract class MainDb : RoomDatabase() {

    abstract fun listPagerDao(): ListPagerDao

    //TODO настроить компаньон объект, чтобы при создании базы, она заполнялась какими-то значениями
    companion object {
        private var instance: MainDb? = null
        @Synchronized
        fun get(context: Context): MainDb {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDb::class.java, "base.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            fillLPinDB(context.applicationContext)
                        }
                    }).build()
            }
            return instance!!
        }

        /**
         * fill database with list of SOME_DATA
         */

        private fun fillLPinDB (context: Context) {
            // inserts in Room are executed on the current thread, so we insert in the background
            ioThread {
                //subMenu
                phaseInit("MAIN3X3", R.array.main3x3_title,R.array.main3x3_icon,R.array.main3x3_descr,R.array.main3x3_url,context)
                //Phases
                phaseInit("BEGIN",R.array.begin_title,R.array.begin_icon,R.array.begin_descr,R.array.begin_url,context)
                phaseInit("ROZOV",R.array.begin_rozov_title,R.array.begin_rozov_icon,R.array.begin_rozov_descr,R.array.begin_rozov_url,context)

            }
        }


        // Инициализация фазы, с заданными массивами Заголовков, Иконок, Описаний, ютуб-ссылок
        private fun phaseInit(phase: String, titleArray: Int, iconArray: Int, descrArray: Int, urlArray: Int, context: Context, comment : Int = 0) {
            val emptyComment = Array (100) {""}
            val titles =  context.resources.getStringArray(titleArray)
            val icon = context.resources.obtainTypedArray (iconArray)
            val description = context.resources.obtainTypedArray (descrArray)
            val url = context.resources.getStringArray(urlArray)
            val cmnt = if (comment != 0) { context.resources.getStringArray(comment) } else { emptyComment}
            for (i in titles.indices) {
                val listPager = ListPagerDBItem(phase, i, titles[i], icon.getResourceId(i, 0),
                    description.getResourceId(i, 0), url[i], cmnt[i])
                get(context).listPagerDao().insert(listPager)
            }
            icon.recycle()
            description.recycle()

        }


    }


}

//TODO Удалить эти данные
private val SOME_DATA = arrayListOf(
    "Abbaye de Belloc", "Abbaye", "Abertammmmm", "Abondance", "Ackawi",
    "Acorn", "Adelostmmmmmmmm", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
    "Baylough", "Beaufort", "Beauvoorde", "Beenleigh Blue", "Beer Cheese", "Bel Paese",
    "Bergader", "Bergere Bleue", "Berkswell", "Beyaz Peynir", "Bierkase", "Bishop Kennedy",
    "Blarney", "Bleu d'Auvergne", "Bleu de Gex", "Bleu de Laqueuille",
    "Bleu de Septmoncel", "Bleu Des Causses", "Blue", "Blue Castello", "Blue Rathgore",
    "Blue Vein (Australian)", "Blue Vein Cheeses", "Bocconcini", "Bocconcini (Australian)",
    "Boeren Leidenkaas", "Bonchester", "Bosworth", "Bougon", "Boule Du Roves",
    "Boulette d'Avesnes", "Boursault", "Boursin", "Bouyssou", "Bra", "Braudostur"
)

private val SOME_DATA2 = arrayListOf(
    "1 кнопка", "2 кнопка", "3 кнопка", "4 кнопка", "5 кнопка",
    "6 кнопка", "7 кнопка", "8 кнопка", "9 кнопка", "10 кнопка", "11 кнопка",
    "12 кнопка", "13 кнопка", "14 кнопка", "15 кнопка", "16 кнопка", "17 кнопка",
    "Bergader", "Bergere Bleue", "Berkswell", "Beyaz Peynir", "Bierkase", "Bishop Kennedy",
    "Blarney", "Bleu d'Auvergne", "Bleu de Gex", "Bleu de Laqueuille",
    "Bleu de Septmoncel", "Bleu Des Causses", "Blue", "Blue Castello", "Blue Rathgore",
    "Blue Vein (Australian)", "Blue Vein Cheeses", "Bocconcini", "Bocconcini (Australian)",
    "Boeren Leidenkaas", "Bonchester", "Bosworth", "Bougon", "Boule Du Roves",
    "Boulette d'Avesnes", "Boursault", "Boursin", "Bouyssou", "Bra", "Braudostur"
)