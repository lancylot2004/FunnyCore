const val REG_SIZE: Int = 16

data class RegAddr(
    val addr: Int,
) {
    constructor(addrStr: String) : this(addrStr.toInt())

    init {
        require(0 <= addr && addr <= (REG_SIZE - 1)) { "RegAddr $addr out of bounds!" }
    }
}
