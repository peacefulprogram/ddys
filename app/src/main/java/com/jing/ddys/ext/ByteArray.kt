package com.jing.ddys.ext

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

fun ByteArray.inflate(): ByteArray {
    val out = ByteArrayOutputStream()
    val input = InflaterInputStream(this.inputStream())
    input.copyTo(out)
    out.close()
    input.close()
    return out.toByteArray()
}

fun ByteArray.unGzip(): ByteArray {
    val out = ByteArrayOutputStream()
    val input = GZIPInputStream(this.inputStream())
    input.copyTo(out)
    out.close()
    input.close()
    return out.toByteArray()

}