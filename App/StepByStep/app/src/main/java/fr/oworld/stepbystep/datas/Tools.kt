package fr.oworld.mpp.datas

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.res.ResourcesCompat

import java.util.Random
import androidx.fragment.app.FragmentActivity
import java.text.Normalizer


/**
 * Created by olivierbernal on 14/03/18 for KARTABLE
 */

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}


object Tools {

    fun getLocalizedString(mContext: FragmentActivity, ressource: String, params: String? = null): String {
        val idRessources = mContext.resources.getIdentifier(ressource, "string", mContext.packageName)

        return if (idRessources != 0)
            mContext.getString(idRessources,params)
        else
            ressource
    }

    fun getImageRessource(mContext: FragmentActivity, imageName: String): Drawable? {
        val resId = mContext.resources.getIdentifier(imageName, "drawable", mContext.packageName)

        return if (android.os.Build.VERSION.SDK_INT >= 21 && resId != 0) {
            mContext.getDrawable(resId)
        } else if (resId != 0) {
            ResourcesCompat.getDrawable(mContext.resources, resId, null)
        } else {
            null
        }
    }

    fun randomStringWithLength(length: Int): String {
        val valters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val len = valters.length

        var randomString = ""

        for (i in 0 until length) {
            val ran = Random()
            val randomNum = ran.nextInt(len)

            val nextChar = valters[randomNum]
            randomString = randomString + nextChar
        }

        return randomString
    }
}

private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun String.unaccent(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return REGEX_UNACCENT.replace(temp, "")
}


