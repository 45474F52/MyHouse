package com.aes.myhome.objects

import java.io.Serializable

/**
 * @param listener Реагирует на выделение пункта при выполнении функции toggle()
 */
class CheckableText(var text: String, var listener: ((Boolean) -> Unit)? = null) : Serializable {
    private var _checked = false

    fun toggle() {
        _checked = !_checked
        listener?.invoke(isChecked())
    }

    fun isChecked(): Boolean = _checked
}