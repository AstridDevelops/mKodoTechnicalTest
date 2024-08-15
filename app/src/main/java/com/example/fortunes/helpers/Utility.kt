package com.example.fortunes.helpers

import android.util.Log
import androidx.core.net.ParseException
import com.example.fortunes.viewmodel.DrawDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utility {

    @JvmStatic
    fun formatDate(stringDate: String?): String {
        // Define the input date format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        // Define the output date format
        val outputFormat = SimpleDateFormat("E. dd MMM yyyy", Locale.ENGLISH)

        return try {
            if (stringDate.isNullOrEmpty()) {
                throw IllegalArgumentException("Date is null or empty")
            }
            // Parse the date string from JSON to a Date object
            val date: Date = inputFormat.parse(stringDate) ?: return "Invalid Date"

            // Format the Date object into the desired output string
            outputFormat.format(date)
        } catch (e: ParseException) {
            "Invalid Date"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    // Extracts the number from the draw ID
    @JvmStatic
    fun extractFirstDigits(drawId: String): String {
        return try {
            val match = "\\d+".toRegex().find(drawId)
            val extractedDigits = match?.value ?: "x" // Default to "x" if no digits are found
            "$extractedDigits"

        } catch (e: Exception) {
            "x"
        }
    }

}