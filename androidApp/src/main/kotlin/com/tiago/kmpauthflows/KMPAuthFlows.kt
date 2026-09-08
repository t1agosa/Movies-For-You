package com.tiago.kmpauthflows

import android.app.Application
import com.tiago.kmpauthflows.di.androidAuthModule
import com.tiago.kmpauthflows.di.androidMoviesModule
import com.tiago.kmpauthflows.di.initKoin
import org.koin.android.ext.koin.androidContext

class KmpAuthFlowsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@KmpAuthFlowsApp)
            modules(androidAuthModule, androidMoviesModule)
        }
    }
}