package com.mrsmart.standard.message

import android.graphics.pdf.PdfDocument.Page

data class Message(
    val type: Int,
    val content: Any
)
