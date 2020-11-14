package com.henrique.geoquiz

import android.view.View

fun View.onClick(clickListener: (View) -> Unit) {
    setOnClickListener(clickListener)
}
