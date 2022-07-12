package com.dnieln7.collection.utils

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.setMargins
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions

fun SupportMapFragment.setMyLocationButtonPosition(
    verticalPosition: Int = RelativeLayout.ALIGN_PARENT_BOTTOM,
    horizontalPosition: Int = RelativeLayout.ALIGN_PARENT_START,
    margin: Int = 20
) {
    val locationButton = (view?.findViewById<View>(Integer.parseInt("1"))?.parent as View)
        .findViewById<View>(Integer.parseInt("2"))

    val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams

    rlp.removeRule(RelativeLayout.ALIGN_PARENT_TOP)
    rlp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
    rlp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
    rlp.removeRule(RelativeLayout.ALIGN_PARENT_LEFT)
    rlp.removeRule(RelativeLayout.ALIGN_PARENT_END)
    rlp.removeRule(RelativeLayout.ALIGN_PARENT_START)

    rlp.addRule(verticalPosition, RelativeLayout.TRUE)
    rlp.addRule(horizontalPosition, RelativeLayout.TRUE)

    rlp.setMargins(margin)
}

fun GoogleMap.addStyle(context: Context, styleResource: Int) {
    try {
        setMapStyle(
            MapStyleOptions.loadRawResourceStyle(context, styleResource)
        )
    } catch (e: Resources.NotFoundException) {
        Log.e("MAP", "Can't find style. Error: ", e)
    }
}

fun GoogleMap.removeStyle() {
    setMapStyle(null)
}