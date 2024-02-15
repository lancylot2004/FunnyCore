import Instruction.*

val INST_REGEX: Regex = "([a-z]+)\\s+(-?\\d+)\\s+(-?\\d+)(?:\\s+(-?\\d+))?".toRegex()

class AsmCode(code: String) {
    private val insts: List<Instruction> = parseCode(code)
    private val regs: Array<Int> = Array(REG_SIZE) { 0 }

    private fun parseCode(code: String): List<Instruction> {
        return code
            .lines()
            .map {
                val match = INST_REGEX.matchEntire(it)?.groupValues ?: throw AssemblySyntaxException(it)
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

    fun execute(vararg returnReg: RegAddr) {
        regs.fill(0)
        var currInd: Int = 0

        while (true) {
            // Check undefined jump
            val currInst = insts.getOrNull(currInd) ?: throw AssemblyUndefinedJump(currInd)
            var jumped = false

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
}
