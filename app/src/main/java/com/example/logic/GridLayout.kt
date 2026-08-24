package com.example.logic

sealed class GridLayout {
    data class Standard(val columns: Int) : GridLayout()
    data class ColumnChunk(val chunkSize: Int) : GridLayout()
    data object DecadeColumn : GridLayout()

    fun getDimensions(numberRange: Int): Pair<Int, Int> = when (this) {
        is Standard -> {
            val rows = (numberRange + columns - 1) / columns
            Pair(columns, rows)
        }
        is ColumnChunk -> {
            val cols = (numberRange + chunkSize - 1) / chunkSize
            Pair(cols, chunkSize)
        }
        is DecadeColumn -> {
            val maxDecade = (1 + numberRange - 1) / 10
            val cols = maxDecade + 1
            Pair(cols, 10)
        }
    }

    fun getNumberAtPosition(row: Int, col: Int, numberRange: Int, minNumber: Int = 1): Int? = when (this) {
        is Standard -> {
            val n = minNumber + row * columns + col
            if (n in minNumber..(minNumber + numberRange - 1)) n else null
        }
        is ColumnChunk -> {
            val n = minNumber + col * chunkSize + row
            if (n in minNumber..(minNumber + numberRange - 1)) n else null
        }
        is DecadeColumn -> {
            val n = if (col == 0) {
                if (row == 9) null else minNumber + row
            } else {
                col * 10 + row
            }
            if (n != null && n in minNumber..(minNumber + numberRange - 1)) n else null
        }
    }
}
