package com.caspar.xl.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/***
 * 全局使用的协程，因为官方不推荐使用GlobalScope，因此在Application中创建一个全局的协程以便于非Activity，ViewModel的类使用协程
 * 使用方法，在有application的地方使用, application.scope.launch{}
 */
val Application.lifecycleScope: CoroutineScope
    get() {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }