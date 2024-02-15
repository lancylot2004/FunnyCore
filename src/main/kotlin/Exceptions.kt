class AssemblySyntaxException(private val line: String) : Exception() {
    override fun toString(): String {
        return "Could not parse \"$line\"!"
    }
}

class AssemblyUndefinedJump(private val to: Int) : Exception() {
    override fun toString(): String {
        return "Could not jump to line $to!"
    }
}
