package ru.tohaman.rg2.koin

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import ru.tohaman.rg2.dataSource.ItemsRepository
import ru.tohaman.rg2.dbase.MainDb
import ru.tohaman.rg2.ui.games.*
import ru.tohaman.rg2.ui.info.InfoViewModel
import ru.tohaman.rg2.ui.learn.LearnDetailViewModel
import ru.tohaman.rg2.ui.learn.LearnViewModel
import ru.tohaman.rg2.ui.settings.SettingsViewModel

private const val DATABASE_NAME = "base.db"

val appModule = module{
    single<SharedPreferences> {
        androidContext().getSharedPreferences("${androidContext().applicationInfo.packageName}_preferences", Context.MODE_PRIVATE) }
    single {
        Room.databaseBuilder(androidContext(), MainDb::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration() //На время разработки программы, каждый раз пересоздаем базу, вместо миграции
            //.addMigrations(MIGRATION_1_2)
            .build()
    }
    //В ItemRepository нужно передать сылку на dao, берем его предыдущего пункта, т.е. get<MainDb>
    single { ItemsRepository(get<MainDb>().mainDao,
                            get<MainDb>().cubeTypesDao,
                            get<MainDb>().movesDao,
                            get<MainDb>().azbukaDao,
                            get<MainDb>().timeNoteDao,
                            get<MainDb>().pllGameDao)}

}

val viewModelsModule = module {
    viewModel { UiUtilViewModel() }
    viewModel { GamesViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { PllTrainerViewModel(androidApplication()) }
    viewModel { InfoViewModel(androidApplication()) }
    viewModel { AzbukaTrainerViewModel(androidApplication()) }
    viewModel { ScrambleGeneratorViewModel() }
    viewModel { TimerViewModel(androidApplication()) }
    viewModel { TimerResultViewModel() }
    viewModel { LearnViewModel(androidContext()) }
    viewModel { LearnDetailViewModel(androidContext()) }
}