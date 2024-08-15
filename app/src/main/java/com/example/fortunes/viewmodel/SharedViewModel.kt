package com.example.fortunes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fortunes.helpers.Utility
import com.example.fortunes.model.DrawsResponse
import com.example.fortunes.model.LotteryDraw
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

open class SharedViewModel  : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _lotteryDrawList = MutableLiveData<List<LotteryDraw>>()
    val lotteryDrawList: LiveData<List<LotteryDraw>> = _lotteryDrawList

   private val _loadingVisibilityState = MutableLiveData<Boolean?>()
    val loadingVisibilityState: LiveData<Boolean?> = _loadingVisibilityState

    private val _selectedItem = MutableLiveData<LotteryDraw?>(null)
    val selectedItem: LiveData<LotteryDraw?> get() = _selectedItem

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val json = Json { ignoreUnknownKeys = true } // Configure as needed

    companion object {
        private const val FAKE_JSON_RESPONSE = """
            { "draws": [ 
                { "id": "draw-1", "drawDate": "2023-05-15", "number1": "2", "number2": "16", "number3": "23", "number4": "44", "number5": "47", "number6": "52", "bonus-ball": "14", "topPrize": 4000000000 },
                { "id": "draw-2", "drawDate": "2023-05-22", "number1": "5", "number2": "45", "number3": "51", "number4": "32", "number5": "24", "number6": "18", "bonus-ball": "28", "topPrize": 6000000000 },
                { "id": "draw-3", "drawDate": "2023-05-29", "number1": "34", "number2": "21", "number3": "4", "number4": "58", "number5": "1", "number6": "15", "bonus-ball": "51", "topPrize": 6000000000 }
            ] }
        """
    }

    fun updateErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun setLoadingVisibilityState(isLoading: Boolean) {
        _loadingVisibilityState.value = isLoading
    }

    fun setSelectedItem(item: LotteryDraw?) {
        if (item == null || _selectedItem.value?.id != item.id) {
            _selectedItem.value = item
        }
    }

    fun fetchDrawList() {
        //coroutineScope.launch {
        viewModelScope.launch {
            // Simulate network delay
            delay(3000)
            try {
                // Parse the JSON data
                val drawResponse = json.decodeFromString<DrawsResponse>(FAKE_JSON_RESPONSE)
                _lotteryDrawList.postValue(drawResponse.draws)
            } catch (e: Exception) {
                _errorMessage.postValue("Unexpected error during fetch")
                _lotteryDrawList.postValue(emptyList())
            }
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

    fun getFormattedDate(stringDate: String?): String {
        return try {
            Utility.formatDate(stringDate)
        } catch (e: Error) {
            _errorMessage.value = "Error formatting date"
            " "
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel() // Cancel all coroutines when ViewModel is cleared
    }
}