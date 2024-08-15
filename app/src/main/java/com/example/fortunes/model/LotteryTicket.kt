package com.example.fortunes.model

data class LotteryTicket(
    var number1: Int,
    var number2: Int,
    var number3: Int,
    var number4: Int,
    var number5: Int,
    var number6: Int
) {
    override fun hashCode(): Int {
        return listOf(number1, number2, number3, number4, number5, number6).hashCode()

    }
}