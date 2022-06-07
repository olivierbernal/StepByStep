package fr.oworld.stepbystep

import android.app.Application
import com.orhanobut.hawk.Hawk

/**
 * Created by olivierbernal on 8/05/18 for oWorld Software
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
    }
}