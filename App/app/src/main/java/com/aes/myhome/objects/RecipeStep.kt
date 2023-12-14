package com.aes.myhome.objects

import java.io.Serializable

/**
 * @param text Описание шага
 * @param listener Реагирует на выделение пункта при выполнении функции toggle()
 */
class RecipeStep(var text: String, var listener: ((Boolean) -> Unit)? = null) : Serializable {
    private var _checked = false

    fun toggle() {
        _checked = !_checked
        listener?.invoke(isChecked())
    }

    fun isChecked(): Boolean = _checked
}