import Instruction.*
import kotlin.random.Random
import kotlin.random.nextInt

val INST_REGEX: Regex = "([a-z]+)\\s+(-?\\d+)\\s+(-?\\d+)(?:\\s+(-?\\d+))?".toRegex()

class AsmCode(code: String) {
    private val insts: List<Instruction> = parseCode(code)
    private val regs: Array<Int> = Array(REG_SIZE) { 0 }
    private val returns = mutableListOf<Pair<String, Int>>()

    private fun parseCode(code: String): List<Instruction> {
        return code
            .lines()
            .map {
                val match = INST_REGEX.find(it)?.groupValues ?: throw AssemblySyntaxException(it)
                if (match.size < 4) throw AssemblySyntaxException(it)
                when (match[1]) {
                    "add" -> Add(RegAddr(match[2]), RegAddr(match[3]), RegAddr(match[4]))
                    "sub" -> Sub(RegAddr(match[2]), RegAddr(match[3]), RegAddr(match[4]))
                    "mul" -> Mul(RegAddr(match[2]), RegAddr(match[3]), RegAddr(match[4]))
                    "shl" -> Shl(RegAddr(match[2]), RegAddr(match[3]))
                    "shr" -> Shr(RegAddr(match[2]), RegAddr(match[3]))
                    "bor" -> Bor(RegAddr(match[2]), RegAddr(match[3]), RegAddr(match[4]))
                    "band" -> Band(RegAddr(match[2]), RegAddr(match[3]), RegAddr(match[4]))
                    "li" -> Li(RegAddr(match[2]), match[3].toShort())
                    "jz" -> Jz(RegAddr(match[2]), match[3].toInt())
                    "jp" -> Jp(RegAddr(match[2]), match[3].toInt())
                    else -> throw AssemblySyntaxException(it)
                }
            }
    }

    fun setReg(
        index: Int,
        value: Int,
    ) {
        regs[index] = value
    }

    fun expectReg(
        name: String,
        index: Int,
    ) {
        returns += name to index
    }

    data class RegVal(val name: String, val index: Int, val value: Int)

    fun execute(setup: AsmCode.() -> Unit): Pair<List<RegVal>, Int> {
        regs.fill(0)
        setup()
        var currInd = 0
        var cycles = 0

        while (true) {
            // Check undefined jump
            val currInst = insts.getOrNull(currInd) ?: throw AssemblyUndefinedJump(currInd)
            var jumped = false

            when (currInst) {
                is Jp, is Jz, is Mul -> cycles += 3
                else -> cycles++
            }

            when (currInst) {
                is Add -> regs[currInst.dest] = regs[currInst.a] eplus regs[currInst.b]
                is Sub -> regs[currInst.dest] = regs[currInst.a] esub regs[currInst.b]
                is Mul -> regs[currInst.dest] = regs[currInst.a] emul regs[currInst.b]
                is Band -> regs[currInst.dest] = regs[currInst.a] and regs[currInst.b]
                is Bor -> regs[currInst.dest] = regs[currInst.a] or regs[currInst.b]
                is Jp ->
                    if (regs[currInst.a.addr] > 0) {
                        currInd += currInst.i
                        jumped = true
                    }

                is Jz ->
                    if (regs[currInst.a.addr] == 0) {
                        currInd += currInst.i
                        jumped = true
                    }

                is Li -> regs[currInst.dest] = currInst.i.toInt()
                is Shl -> regs[currInst.dest] shl regs[currInst.a]
                is Shr -> regs[currInst.dest] shr regs[currInst.a]
            }

            if (!jumped) {
                currInd += 1
                if (currInd == insts.size) break
            }
        }

        for (addr in returnReg) {
            println("${addr.addr}: ${regs[addr.addr]}")
        }
    }
        return returns.map { RegVal(it.first, it.second, regs[it.second]) } to cycles
    }
}

private val divCode =
    """
    """.trimIndent()

private val divAsm = AsmCode(divCode)

fun divUsingAsm(
    dividend: Int,
    divisor: Int,
): Int {
    val (regs, cycles) =
        divAsm.execute {
            setReg(0, dividend)
            setReg(1, divisor)

            expectReg("Quotient", 2)
            expectReg("Remainder", 3)
        }

    val quotient = regs.first { it.name == "Quotient" }.value
    val remainder = regs.first { it.name == "Remainder" }.value

    require(dividend / divisor == quotient) { "Incorrect Quotient: $dividend / $divisor, got $quotient R $remainder" }
    require(dividend.rem(divisor) == remainder) { "Incorrect Remainder: $dividend / $divisor, got $quotient R $remainder" }
    println("Correct: $dividend / $divisor = $quotient R $remainder")

    return cycles
}

fun main() {
    val cycles = mutableListOf<Int>()
    repeat(40) {
        cycles +=
            divUsingAsm(
                Random.nextInt(0..Int.MAX_VALUE),
                Random.nextInt(1..Int.MAX_VALUE),
            )
    }

    println("Total Cycles: ${cycles.sum()}")
    print("Average Cycles: ${cycles.average()}")
}
