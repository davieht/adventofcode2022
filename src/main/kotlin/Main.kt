import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.max

fun main(args: Array<String>) {
//    day1()
//    day2()
//    day3()
//    day4()
//    day5()
//    day6()
//    day7()
//    day8()
    day9()
}

fun day9() {

    data class Segment(var x: Int, var y: Int)

    val commands = File("src/main/resources/day9input").readLines()
        .map {
            it.split(" ") // [["R", "2"], ["U", "1"]]
                .let { item ->
                    List(item[1].toInt()) { item[0] } // [["R", "R"], ["U"]
                }
        }.flatten() // ["R", "R", "U"]

//    fun printRope(rope: List<Segment>) {
//        for (y in 0 until 26) {
//            for (x in 0 until 26) {
//                if (x == startX && y == startY)
//                    print('s')
//                else if (x == rope[0].x && y == rope[0].y)
//                    print('H')
//                else if (x == rope[1].x && y == rope[1].y)
//                    print('T')
//                else
//                    print('.')
//            }
//            println()
//        }
//        println()
//    }

    /**
     * Prints all visited fields by respecting boundaries
     */
    fun printVisited(visitedSet: Set<Pair<Int, Int>>) {
        val maxX = visitedSet.maxOf { it.first }
        val maxY = visitedSet.maxOf { it.second }
        val minX = visitedSet.minOf { it.first }
        val minY = visitedSet.minOf { it.second }
        val offset = Pair(-minX, -minY)
        val size = Pair(-minX + maxX, -minY + maxY)

        for (y in 0 until size.second) {
            for (x in 0 until size.first) {
                if (visitedSet.contains(Pair(x - offset.first, y - offset.second))) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
        println()
    }

    /**
     * Moves the given head according to input command
     */
    fun headCommand(segment: Segment, command: String) {
        when (command) {
            "R" -> segment.x++
            "U" -> segment.y--
            "L" -> segment.x--
            "D" -> segment.y++
        }
    }

    /**
     * Calculates the trailing segment
     */
    fun calcSegment(head: Segment, tail: Segment) {

        fun segmentCommandX(segment: Segment, dX: Int) {
            if (dX > 0) {
                segment.x++ // R
            } else {
                segment.x-- // L
            }
        }

        fun segmentCommandY(segment: Segment, dY: Int) {
            if (dY > 0) {
                segment.y++ // D
            } else {
                segment.y-- // U
            }
        }

        val dX = head.x - tail.x
        val dY = head.y - tail.y
        if (dX.absoluteValue + dY.absoluteValue > 2) { // dX = 1, dY = 2
            segmentCommandX(tail, dX)
            segmentCommandY(tail, dY)
        } else if (dX.absoluteValue > 1) { // dX = 2, dY = 0
            segmentCommandX(tail, dX)
        } else if (dY.absoluteValue > 1) { // dX = 0, dY = 2
            segmentCommandY(tail, dY)
        }
    }

    /**
     * Runs the given commands on a rope length. Stores all tail visits to a set, and eventually prints the entire result
     * @return sum of tail visits
     */
    fun run(commands: List<String>, ropeLength: Int, doPrint: Boolean = false): Int {
        val visitedSet = mutableSetOf<Pair<Int, Int>>()
        val rope = List(ropeLength) { Segment(0, 0) }
        for (command in commands) {
            headCommand(rope.first(), command)
            rope.windowed(2).forEach {
                calcSegment(it[0], it[1])
            }
            visitedSet.add(Pair(rope.last().x, rope.last().y))
        }

        if (doPrint)
            printVisited(visitedSet)

        return visitedSet.size
    }

    fun part1() {
        println(run(commands, 2))
    }

    fun part2() {
        println(run(commands, 10))
    }

    part1()
    part2()
}

fun day8() {

    data class Tree(
        val height: Int,
        var isVisible: Boolean = false,
        var distance: Int = 0,
        var score: Int = 0
    )

    val input = File("src/main/resources/day8input").readLines()
        .map {
            it.toCharArray()
                .map {
                    Tree(it.digitToInt())
                }
        }

    /**
     *
     */
    fun depthStencil(grid: List<List<Tree>>, direction: (x: Int, y: Int) -> Tree) {
        for (y in grid.indices) {
            var maxHeight = -1
            for (x in grid[y].indices) {
                val tree = direction(x, y)

                if (tree.height > maxHeight) {
                    tree.isVisible = true
                    maxHeight = tree.height
                }

                // max height reached. No further visibility possible
                if (maxHeight == 9) {
                    continue
                }
            }
        }
    }

    fun printGrid(grid: List<List<Tree>>) {
        for (y in grid.indices) {
            for (x in grid[y].indices) {
                print(if (grid[y][x].isVisible) "░${grid[y][x].height}|${grid[y][x].score} " else "▓${grid[y][x].height}|${grid[y][x].score} ")
//                print(if (grid[y][x].isVisible) "▓▓ " else "░░ ")
            }
            println()
        }
    }

    fun sumGrid(grid: List<List<Tree>>): Int {
        var sum = 0
        for (y in input.indices) {
            for (x in input[y].indices) {
                if (input[y][x].isVisible)
                    sum++
            }
        }
        return sum
    }

//    depthStencil(input) { x, y -> input[y][x] } // left
//    depthStencil(input) { x, y -> input[y][input[y].size - 1 - x] } // right
//    depthStencil(input) { x, y -> input[x][y] } // top
//    depthStencil(input) { x, y -> input[input[x].size - 1 - x][y] } // bottom
//    println(sumGrid(input))

    fun List<Tree>.findOffset(height: Int): Int {
        var index = 0
        for (i in indices) {
            if (this[i].height >= height)
                return ++index
            index++
        }
        return this.size
    }

    fun scenicScore(input: List<List<Tree>>, x: Int, y: Int): Int {
        val right = input[y].subList(x + 1, input[y].size).findOffset(input[y][x].height)
        val left = input[y].subList(0, x).reversed().findOffset(input[y][x].height)
        val bottom = input.subList(y + 1, input[y].size).map { it[x] }.findOffset(input[y][x].height)
        val top = input.subList(0, y).map { it[x] }.reversed().findOffset(input[y][x].height)
        return right * left * bottom * top
    }

    var max = 0

    for (y in input.indices) {
        for (x in input[y].indices) {
            val score = scenicScore(input, x, y)
            input[y][x].score = score
            max = max(max, score)
        }
    }

    printGrid(input)
    println(max)
}

fun day7() {
    val input = File("src/main/resources/day7input").readLines().map {
        it.split(" ")
    }

    // A directory node. Can be a directory or a file (a leave)
    data class Node(
        val name: String,
        val parent: Node?,
        var size: Int = 0,
        val childs: MutableMap<String, Node> = mutableMapOf()
    )

    // Print the tree, just for debug purpose
    fun printTree(node: Node, depth: Int = 0) {
        println("${"  ".repeat(depth)}${node.name} ${node.size}")
        if (node.childs.isNotEmpty()) {
            for (child in node.childs) {
                printTree(child.value, depth + 1)
            }
        }
    }

    // Sums up the size of all descendants of a directory
    fun calcDirSize(node: Node): Int {
        if (node.childs.isNotEmpty()) {
            for (child in node.childs) {
                node.size += calcDirSize(child.value)
            }
        }
        return node.size
    }

    // part 1
    // Sums all directories which have a size of 100000 the most
    fun sumDirsAboveThreshold(dirs: List<Node>): Int {
        var sum = 0
        for (dir in dirs) {
            if (dir.size <= 100000) {
                sum += dir.size
            }
        }
        return sum
    }

    // part 2
    // Finds the smallest directory required to free up space and returns its size
    fun findSmallestDir(dirs: List<Node>): Int {
        var minDir: Node = Node("init", null, Int.MAX_VALUE)
        val freeSpace = 70000000 - dirs[0].size
        val requiredSpace = 30000000 - freeSpace
        for (dir in dirs) {
            if (dir.size >= requiredSpace && dir.size < minDir.size) {
                minDir = dir
            }
        }
        return minDir.size
    }

    // Builds the directory
    fun buildTree(input: List<List<String>>): List<Node> {

        // The root node
        var node = Node("/", null)
        val dirIdx = mutableListOf<Node>(node)
        var next: List<String>?

        for (i in 1 until input.size) {
            next = input[i]
            if (next[0] == "$") {
                if (next[1] == "cd") {
                    // select current dir
                    node = if (next[2] == "..") {
                        node.parent!!
                    } else {
                        node.childs[next[2]]!!
                    }
                }
            } else {
                if (next[0] == "dir") {
                    val dir = Node(next[1], node)
                    dirIdx.add(dir)
                    node.childs[next[1]] = dir
                } else {
                    node.childs[next[1]] = Node(next[1], node, next[0].toInt())
                }
            }
        }
        return dirIdx
    }

    // A directory index to fast access a directory
    val dirIdx = buildTree(input)
    calcDirSize(dirIdx[0])
    println("Day7/1: ${sumDirsAboveThreshold(dirIdx)}")
    println("Day7/2: ${findSmallestDir(dirIdx)}")
}

fun day6() {
    val input = File("src/main/resources/day6input").readText()

    fun findPrimerPos(primerLength: Int): Int {
        var pivot = 0
        var buffer: CharArray

        while (pivot < input.length - primerLength) {
            val checkBox = mutableMapOf<Char, Boolean>()
            buffer = input.substring(pivot, pivot + primerLength).toCharArray()

            for (c in buffer) {
                checkBox.putIfAbsent(c, true)
            }
//            println("${String(buffer)} - $pivot")
            if (checkBox.keys.size == primerLength) {
                return pivot + primerLength
            }

            pivot++
        }

        return input.length
    }

    println("Day6/1: ${findPrimerPos(4)}")
    println("Day6/1: ${findPrimerPos(14)}")
}

fun day5() {

    fun initStack(): Array<ArrayDeque<Char>> {
        return arrayOf(
            ArrayDeque("STHFWR".toCharArray().toList()),
            ArrayDeque("SGDQW".toCharArray().toList()),
            ArrayDeque("BTW".toCharArray().toList()),
            ArrayDeque("DRWTNQZJ".toCharArray().toList()),
            ArrayDeque("FBHGLVTZ".toCharArray().toList()),
            ArrayDeque("LPTCVBSG".toCharArray().toList()),
            ArrayDeque("ZBRTWGP".toCharArray().toList()),
            ArrayDeque("NGMTCJR".toCharArray().toList()),
            ArrayDeque("LGBW".toCharArray().toList())
        )
    }

    var stack = initStack()

    fun command(from: Int, to: Int, quantity: Int) {
        for (i in 0 until quantity) {
            stack[to].add(stack[from].removeLast())
        }
    }

    val input = File("src/main/resources/day5input").readLines()
    input.forEach {
        val command = it.split(" ")
        val move = command[1].toInt()
        val from = command[3].toInt() - 1
        val to = command[5].toInt() - 1

        command(from, to, move)
    }

    var result = String(stack.map { it.last() }.toCharArray())
    println("Day5/1: $result")


    fun command2(from: Int, to: Int, quantity: Int) {
        val cache = ArrayDeque<Char>()
        for (i in 0 until quantity) {
            cache.add(stack[from].removeLast())
        }
        for (i in 0 until quantity) {
            stack[to].add(cache.removeLast())
        }
    }

    stack = initStack()

    input.forEach {
        val command = it.split(" ")
        val move = command[1].toInt()
        val from = command[3].toInt() - 1
        val to = command[5].toInt() - 1

        command2(from, to, move)
    }

    result = String(stack.map { it.last() }.toCharArray())
    println("Day5/2: $result")
}

fun day4() {

    // including
    fun first(input: Array<IntArray>): Int =
        if (input[0][0] <= input[1][0] && input[0][1] >= input[1][1]
            || input[1][0] <= input[0][0] && input[1][1] >= input[0][1]
        ) {
            1
        } else
            0

    // excluding
    fun second(input: Array<IntArray>): Int =
        if (input[0][1] < input[1][0] || input[1][1] < input[0][0]) {
            1
        } else {
            0
        }

    var sum = 0
    val input = File("src/main/resources/day4input").readLines().map {
        it.split(',')
            .map {
                it.split('-')
                    .map {
                        it.toInt()
                    }.toIntArray()
            }.toTypedArray()
    }

    input.forEach {
        sum += first(it)
    }

    println("Day4/1: $sum")

    sum = 0

    input.forEach {
        sum += second(it)
    }

    println("Day4/2: ${input.size - sum}")
}

fun day3() {
    val itemTypes =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .mapIndexed { idx, c -> c to (idx + 1) }
            .toMap()

    fun first(input: String): Int {
        val (left, right) = input.chunked(input.length / 2).map { it.toSet() }

        // not optimal. finds all occurrences, not only first
        val common = left.intersect(right).first()
        return itemTypes[common]!!
    }

    fun second(input: List<Set<Char>>): Int {
        val common: Char = input[0].intersect(input[1].intersect(input[2])).first()
        return itemTypes[common]!!
    }

    var sum = 0
    File("src/main/resources/day3input").forEachLine {
        sum += first(it)
    }

    println("Day3/1: $sum")

    sum = 0

    File("src/main/resources/day3input").readLines()
        .chunked(3).forEach { chunk ->
            sum += second(chunk.map { it.toSet() })
        }

    println("Day3/2: $sum")
}

// Rock, Paper, Scissors
fun day2() {
    val abcMap = mapOf<Char, Int>('A' to 0, 'B' to 1, 'C' to 2)
    val xyzMap2 = mapOf<Char, Int>(
        'X' to -1,
        'Y' to 0,
        'Z' to 1
    )

    val xyzMap1 = mapOf<Char, Int>(
        'X' to 0,
        'Y' to 1,
        'Z' to 2
    )

    val scoreMap = mapOf<Pair<Int, Int>, Int>(
        Pair(0, 0) to 1 + 3,
        Pair(0, 1) to 2 + 6,
        Pair(0, 2) to 3 + 0,
        Pair(1, 0) to 1 + 0,
        Pair(1, 1) to 2 + 3,
        Pair(1, 2) to 3 + 6,
        Pair(2, 0) to 1 + 6,
        Pair(2, 1) to 2 + 0,
        Pair(2, 2) to 3 + 3
    )

    fun first(line: String): Int = scoreMap[Pair(abcMap[line[0]], xyzMap1[line[2]])]!!

    fun second(line: String): Int {
        val their: Int = abcMap[line[0]]!!
        val me: Int = (their + xyzMap2[line[2]]!!).mod(3) // do not use % since it is rem (remainder)
        return scoreMap[Pair(their, me)]!!
    }

    var sum = 0
    File("src/main/resources/day2input").forEachLine {
        sum += first(it)
    }

    println("Day2/1: $sum")

    sum = 0
    File("src/main/resources/day2input").forEachLine {
        sum += second(it)
    }

    println("Day2/2: $sum")
}

// Calorie Counting
fun day1() {
    var idx = 0
    val elves = mutableListOf<Int>(0)
    File("src/main/resources/day1input").forEachLine {
        if (it.isEmpty()) {
            elves.add(0)
            idx++
        } else {
            elves[idx] += it.toIntOrNull() ?: 0
        }
    }

    // Answer 1/1
    println("Day1/1: ${elves.max()}")

    // Answer 1/2
    elves.sortDescending()
    println("Day1/2: ${elves.subList(0, 3).sum()}")
}