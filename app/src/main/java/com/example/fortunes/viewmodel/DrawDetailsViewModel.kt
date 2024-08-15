package com.example.fortunes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fortunes.helpers.Utility

class DrawDetailsViewModel : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun updateErrorMessage(message: String) {
        _errorMessage.value = message
    }

    // Formats a date string using the Utility class
    fun getFormattedDate(jsonDate: String?): String {
        return try {
            Utility.formatDate(jsonDate)
        } catch (e: Error) {
            _errorMessage.value = "Error formatting date"
            " "
        }
    }

    // Formats the prize amount into a string
    fun getFormattedTopPrize(prize: Long): String {
        return try {
            when {
                prize == 1_000_000L -> "Jackpot : 1 Million £"
                prize in 1_000_001L until 1_000_000_000L -> "Jackpot : ${prize / 1_000_000} Millions £"
                prize == 1_000_000_000L -> "Jackpot : 1 Billion £"
                prize > 1_000_000_000L -> "Jackpot : ${prize / 1_000_000_000} Billions £"
                else -> "Jackpot : ${"%,d £".format(prize)}"
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error formatting top prize"
            "Unknown prize"
        }
    }

    // Extracts the number from the draw ID and display text Draw number x
    fun extractFirstDigits(drawId: String): String {
        return try {
            "Draw number ${Utility.extractFirstDigits(drawId)}"
        } catch (e: Exception) {
            _errorMessage.value = "Error extracting first digits from draw ID"
            " "
        }
    }
}