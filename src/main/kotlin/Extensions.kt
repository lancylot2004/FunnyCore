

/** Exact addition on UInt. */
inline infix fun Int.eplus(other: Int): Int {
    val result: Long = this.toLong() + other.toLong()
    if (result !in Int.MIN_VALUE..Int.MAX_VALUE) throw ArithmeticException("Numeric overflow when performing addition!")
    return result.toInt()
}

/** Exact multiplicationon UInt. */
inline infix fun Int.emul(other: Int): Int {
    val result: Long = this.toLong() * other.toLong()
    if (result !in Int.MIN_VALUE..Int.MAX_VALUE) throw ArithmeticException("Numeric overflow when performing multiplication!")
    return result.toInt()
}

/** Exact subtraction on UInt. */
inline infix fun Int.esub(other: Int): Int {
    val result: Long = this.toLong() - other.toLong()
    if (result !in Int.MIN_VALUE..Int.MAX_VALUE) throw ArithmeticException("Numeric overflow when performing addition!")
    return result.toInt()
}

operator fun <T> Array<T>.get(dest: RegAddr): T {
    return this[dest.addr]
}

operator fun <T> Array<T>.set(
    dest: RegAddr,
    value: T,
) {
    this[dest.addr] = value
}
