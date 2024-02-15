sealed class Instruction {
    /** r{d} = r{a} + r{b} */
    data class Add(val dest: RegAddr, val a: RegAddr, val b: RegAddr) : Instruction()

    /** r{d} = r{a} - r{b} */
    data class Sub(val dest: RegAddr, val a: RegAddr, val b: RegAddr) : Instruction()

    /** r{d} = r{a} * r{b} */
    data class Mul(val dest: RegAddr, val a: RegAddr, val b: RegAddr) : Instruction()

    /** r{d} = r{d} << r{a}, setting the lowest bit to 0. */
    data class Shl(val dest: RegAddr, val a: RegAddr) : Instruction()

    /** r{d} = r{d} >> r{a}, preserving sign. */
    data class Shr(val dest: RegAddr, val a: RegAddr) : Instruction()

    /** r{d} = r{a} | r{b}, bitwise. */
    data class Bor(val dest: RegAddr, val a: RegAddr, val b: RegAddr) : Instruction()

    /** r{d} = r{a} & r{b}, bitwise. */
    data class Band(val dest: RegAddr, val a: RegAddr, val b: RegAddr) : Instruction()

    /** r{d} = *i* */
    data class Li(val dest: RegAddr, val i: Short) : Instruction()

    /** Relative jump by *i* if r{a} == 0 */
    data class Jz(val a: RegAddr, val i: Int) : Instruction()

    /** Relative jump by *i* if r{a} > 0 */
    data class Jp(val a: RegAddr, val i: Int) : Instruction()
}
