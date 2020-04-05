package com.appringer.common.widget

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.regex.Pattern

class KKEditText : AppCompatEditText {
    var currency = "INR"

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        addTextChangedListener(textWatcher)
    }


    inner class CurrencyFormatInputFilter : InputFilter {
        private var mPattern: Pattern = Pattern.compile("(0|[1-9]+[0-9]*)?(\\.[0-9]{0,2})?")

        override fun filter(source: CharSequence,
                            start: Int,
                            end: Int,
                            dest: Spanned,
                            dstart: Int,
                            dend: Int): CharSequence {
            val result = (dest.subSequence(0, dstart)
                    .toString() + source.toString()
                    + dest.subSequence(dend, dest.length))
            val matcher = mPattern.matcher(result)
            return if (!matcher.matches()) dest.subSequence(dstart, dend) else ""
        }
    }

    private fun format(s: String): String {
        val format = DecimalFormat("###,###,###.##")
        format.roundingMode = RoundingMode.FLOOR
        val num = getDoubleValue(s)
        return format.format(num)
    }

    private fun getDoubleValue(s: String): Number {
        return try {
            val format = DecimalFormat("###,###,###.##")
            format.roundingMode = RoundingMode.FLOOR
            format.parse(s)
        } catch (e: Exception) {
            0.0
        }
    }

    fun formattedValue(): String {
        return if (text == null) "" else text.toString()
    }

    fun rawValue(): Double {
        return if (text == null) 0.0 else text.toString().toDoubleOrNull() ?: 0.0
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (count > 0) {
                removeTextChangedListener(this)
                var pos = selectionEnd
                if (!s.endsWith(".")) {
                    val value = format(s.toString())
                    pos += (value.length - s.length)
                    setText(value)
                }
                placeCursorToEnd(pos)
                addTextChangedListener(this)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    fun EditText.placeCursorToEnd(pos: Int) {
        this.setSelection(pos)
    }
}