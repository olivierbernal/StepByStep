package fr.oworld.stepbystep.ui.bindings

import android.view.View
import androidx.databinding.BindingAdapter

/** Affiche une View suivant la valeur de [value] */
@BindingAdapter("visible")
fun View.setVisible(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.GONE
}

/** Fait disparaître une View suivant la valeur de [value] */
@BindingAdapter("gone")
fun View.setGone(value: Boolean) {
    visibility = if (value) View.GONE else View.VISIBLE
}

/** Rend invisible une View suivant la valeur de [value] */
@BindingAdapter("invisible")
fun View.setInvisible(value: Boolean) {
    visibility = if (value) View.INVISIBLE else View.VISIBLE
}

