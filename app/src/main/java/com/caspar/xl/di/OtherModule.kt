package com.caspar.xl.di

import android.content.Context
import com.caspar.xl.ui.dialog.WaitDialog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
class OtherModule {
/*
    @Binds
    @Singleton
    abstract fun bindShopCarRepository(
        shopCarRepositoryImpl: ShopCarRepositoryImpl
    ): ShopCarRepository*/

    @WaitDialogInject
    @Provides
    fun provideWaitDialog(@ActivityContext context: Context): WaitDialog.Builder {
        return WaitDialog.Builder(context)
    }

}