package mitya.pepemusic

val VK_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN0PQRSTUVWXYZO123456789+/="

fun decodeAudioUrl(url: String, userId: Int): String {
    val vals = url.split("?extra=", "#")
    var result = vkO(vals[1])
    val opData = vkO(vals[2]).split(0x0b.toChar())
    val cmd = opData[0]
    val arg = if (opData.size > 1) opData[1] else ""
    when (cmd) {
        "v" -> result = result.reversed()
        "r" -> result = vkR(result, arg)
        "x" -> result = vkXor(result, arg)
        "s" -> result = vkS(result, arg)
        "i" -> result = vkI(result, arg, userId)
    }
    return result
}

private fun vkO(string: String): String {
    var result = ""
    var index = 0
    var i = 0
    for (s in string) {
        val symIndex = VK_STR.indexOf(s)
        if (symIndex != -1) {
            i = if (index.rem(4) != 0) {
                (i.shl(6)) + symIndex
            } else symIndex
            if (index.rem(4) != 0) {
                index += 1
                val shift = -2 * index and 6
                result += (0xFF and (i shr shift)).toChar()
            } else index += 1
        }
    }
    return result
}

private fun vkR(string: String, i: String): String {
    val vkDoubleStr = VK_STR + VK_STR
    var result = ""
    var offset: Int
    for (s in string) {
        val index = vkDoubleStr.indexOf(s)
        if (index != -1) {
            offset = index - i.toInt()
            if (offset < 0) {
                offset += vkDoubleStr.length
            }
            result += vkDoubleStr[offset]
        } else result += s
    }

    return result
}

private fun vkXor(string: String, i: String): String {
    val xorVal = i.first().toInt()
    var result = ""
    for (s in string) {
        result += (s.toInt() xor xorVal).toChar()
    }
    return result
}

private fun vkS(string: String, e: String): String {
    if (string.isEmpty()) return string
    val o = vkChildS(string, e)
    val tmpString = string.toCharArray()
    for (a in 1 until string.length) {
        val tmpChar = tmpString[a]
        tmpString[a] = tmpString[o[string.length - 1 - a]]
        tmpString[o[string.length - 1 - a]] = tmpChar

    }
    return tmpString.joinToString("")
}

private fun vkChildS(string: String, i: String): ArrayList<Int> {
    if (string.isEmpty()) return arrayListOf()
    var e = i.toInt()
    val o = arrayListOf<Int>()
    for (a in (string.length - 1) downTo 0 step 1) {
        e = (string.length * (a + 1) xor e + a).rem(string.length)
        o.add(e)
    }
    o.reverse()
    return o
}

private fun vkI(string: String, i: String, userId: Int): String {
    return vkS(string, (i.toInt() xor userId).toString())
}
