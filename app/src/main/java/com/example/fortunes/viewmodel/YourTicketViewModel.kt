package com.example.fortunes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fortunes.helpers.Utility
import com.example.fortunes.model.LotteryDraw
import com.example.fortunes.model.LotteryTicket
import kotlin.random.Random

class YourTicketViewModel(private val randomGenerator: Random = Random.Default) : ViewModel() {

    companion object {
        private const val MAX_LOTTERY_NUMBER = 59
        private const val TICKET_NUMBERS_REQUIRED = 6
    }

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _lotteryDrawRandomlyPicked = MutableLiveData<LotteryDraw>()
    val lotteryDrawRandomlyPicked: LiveData<LotteryDraw> get() = _lotteryDrawRandomlyPicked

    private val _lotteryTicket = MutableLiveData<LotteryTicket>()
    val lotteryTicket: LiveData<LotteryTicket> get() = _lotteryTicket

    private val _winnerStatus = MutableLiveData<String>()
    val winnerStatus: LiveData<String> get() = _winnerStatus

    private var _ticketNumbers: List<Int>? = null


    fun getTicketNumbers(): List<Int>? {
        return _ticketNumbers
    }

    fun setTicketNumbers(list : List<Int>? ) {
        _ticketNumbers = list
    }

    fun updateErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun getFormattedDate(stringDate: String?): String {
        return Utility.formatDate(stringDate)
    }

    fun generateRandomLotteryTicket() {
        try {
            if (_ticketNumbers != null && _ticketNumbers!!.isNotEmpty()) return

            // Generates a set of unique random numbers for the lottery ticket
            val uniqueNumbers = mutableSetOf<Int>()
            while (uniqueNumbers.size < TICKET_NUMBERS_REQUIRED) {
                uniqueNumbers.add(randomGenerator.nextInt(MAX_LOTTERY_NUMBER) + 1)
            }
            _ticketNumbers = uniqueNumbers.toList()

            if (_ticketNumbers!!.size < TICKET_NUMBERS_REQUIRED) {
                _errorMessage.value = "Error: Ticket does not have $TICKET_NUMBERS_REQUIRED numbers."
                return
            }

            val ticketFormatted = LotteryTicket(_ticketNumbers!![0], _ticketNumbers!![1], _ticketNumbers!![2], _ticketNumbers!![3], _ticketNumbers!![4], _ticketNumbers!![5])
            _lotteryTicket.value = ticketFormatted

        } catch (e: Exception) {
            _errorMessage.value = "Error generating random lottery ticket"
        }
    }

    fun setWinnerStatus(lotteryDrawList: List<LotteryDraw>?){
        try {

           if (lotteryDrawList == null || lotteryDrawList.isEmpty()) {
               _errorMessage.value = "Error: List of draws is empty."
                return
            }

            _ticketNumbers?.let { ticketNumbers ->
                // randomly pick a lottery draw from the list and cache it
                val randomIndex = randomGenerator.nextInt(lotteryDrawList.size)
                val draw = lotteryDrawList[randomIndex]
                _lotteryDrawRandomlyPicked.value = draw

                // compare the draw balls against ticket balls and get the number of matches
                val drawNumbers = setOf(
                    draw.number1.toInt(), draw.number2.toInt(), draw.number3.toInt(),
                    draw.number4.toInt(), draw.number5.toInt(), draw.number6.toInt(),
                    draw.bonusBall.toInt()
                )
                val matches = _ticketNumbers!!.toSet().intersect(drawNumbers)
                val matchSize = matches.size

                // check if winner ticket (all numbers matching)
                val isWinner = (matchSize == TICKET_NUMBERS_REQUIRED)

                // Set winning status
                val resultString = when {
                    isWinner -> "Congratulations! You've won the lottery!"
                    else -> {
                        when (matchSize) {
                            0 -> "No matches this time. Better luck next time!"
                            1 -> "Only 1 ball matched. Better luck next time!"
                            else -> "Only $matchSize balls matched. Better luck next time!"
                        }
                    }
                }
                _winnerStatus.value = resultString
                } ?: run {
                _errorMessage.value = "Error: Ticket numbers are null."
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error setting ticket status"
        }
    }

    fun getLotteryDrawNumber(drawId: String): String {
        return try {
            "Draw number ${Utility.extractFirstDigits(drawId)}"
        } catch (e: Exception) {
            _errorMessage.value = "Error extracting first digits from draw ID"
            "x"
        }
    }

}