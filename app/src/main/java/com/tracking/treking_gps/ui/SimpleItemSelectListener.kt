package com.tracking.treking_gps.ui

import android.view.View
import android.widget.AdapterView

class SimpleItemSelectListener(
        private val handle: (index: Int) -> Unit
): AdapterView.OnItemSelectedListener {

    override fun onNothingSelected(adapterView: AdapterView<*>?) {
//        handle(-1)
    }

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, index: Int, id: Long) {
        handle(index)
    }

}
