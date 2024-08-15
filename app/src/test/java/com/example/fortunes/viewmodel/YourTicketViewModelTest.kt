package com.example.fortunes.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.fortunes.model.LotteryDraw
import com.example.fortunes.model.LotteryTicket
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlin.random.Random

class YourTicketViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule() //allows LiveData to work synchronously in the test environment

    private lateinit var viewModel: YourTicketViewModel
    private lateinit var randomGenerator: Random

    companion object {
        private const val MAX_LOTTERY_NUMBER = 59
        private const val TICKET_NUMBERS_REQUIRED = 6
    }

    @Before
    fun setUp() {
        //viewModel = YourTicketViewModel(randomGenerator)
        randomGenerator = mock(Random::class.java)
        viewModel = YourTicketViewModel(randomGenerator)
    }

    //region getFormattedDate
    @Test
    fun getFormattedDate_return_formatted_date_string_when_valid_date_string_passed() {
        // Arrange
        val inputDate = "2024-05-22"
        val expectedOutput = "Wed. 22 May 2024"  // This is the expected output format

        // Act
        val result = viewModel.getFormattedDate(inputDate)

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun getFormattedDate_return_Invalid_Date_when_date_string_is_invalid() {
        // Arrange
        val inputDate = "24/05/05"  // Invalid date format
        val expectedOutput = "Invalid Date"

        // Act
        val result = viewModel.getFormattedDate(inputDate)

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun getFormattedDate_return_Invalid_Date_when_date_string_is_null() {
        // Arrange
        val inputDate: String? = null
        val expectedOutput = "Invalid Date"

        // Act
        val result = viewModel.getFormattedDate(inputDate)

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun getFormattedDate_return_Invalid_Date_when_date_string_is_empty() {
        // Arrange
        val inputDate = ""
        val expectedOutput = "Invalid Date"

        // Act
        val result = viewModel.getFormattedDate(inputDate)

        // Assert
        assertEquals(expectedOutput, result)
    }
    //endregion

    //region getLotteryDrawNumber

    @Test
    fun getLotteryDrawNumber_return_correct_draw_number_when_drawId_contains_digits() {
        // Arrange
        val drawId = "1234-ABC"
        val expectedOutput = "Draw number 1234"

        // Act
        val result = viewModel.getLotteryDrawNumber(drawId)

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun getLotteryDrawNumber_return_default_draw_number_when_drawId_contains_no_digits() {
        // Arrange
        val drawId = "ABC-XYZ"
        val expectedOutput = "Draw number x"

        // Act
        val result = viewModel.getLotteryDrawNumber(drawId)

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun getLotteryDrawNumber_return_default_draw_number_when_drawId_is_empty() {
        // Arrange
        val drawId = ""
        val expectedOutput = "Draw number x"

        // Act
        val result = viewModel.getLotteryDrawNumber(drawId)

        // Assert
        assertEquals(expectedOutput, result)
    }
    //endregion

    //region generateRandomLotteryTicket
    @Test
    fun generateRandomLotteryTicket_generate_valid_ticket_with_unique_numbers() {
        // Arrange : random generator mocked to return a controlled sequence of numbers
        `when`(randomGenerator.nextInt(any(Int::class.java))).thenReturn(1, 2, 3, 4, 5, 6) // Mock Random.nextInt(MAX_LOTTERY_NUMBER) to return values in sequence
        val observer = mock(Observer::class.java) as Observer<LotteryTicket> // Set up an observer for the LiveData
        viewModel.lotteryTicket.observeForever(observer)

        // Act
        viewModel.generateRandomLotteryTicket()

        // Assert
        val lotteryTicket = viewModel.lotteryTicket.value
        assertNotNull(lotteryTicket)
        val ticketNumbers = listOf(
            lotteryTicket!!.number1,
            lotteryTicket.number2,
            lotteryTicket.number3,
            lotteryTicket.number4,
            lotteryTicket.number5,
            lotteryTicket.number6
        )

        assertEquals(TICKET_NUMBERS_REQUIRED, ticketNumbers.toSet().size)
        ticketNumbers.forEach { number ->
            assertTrue(number in 1..MAX_LOTTERY_NUMBER)
        }

        // Verify the LiveData observer was triggered with a non-null value
        verify(observer).onChanged(lotteryTicket)

    }

    @Test
    fun generateRandomLotteryTicket_should_not_regenerate_if_ticketNumbers_already_populated() {
        // Arrange
        viewModel.setTicketNumbers(listOf(7, 8, 9, 10, 11, 12))

        // Act
        viewModel.generateRandomLotteryTicket()

        // Assert: Verify the function nextInt() has not been called, because numbers were already populated
        Mockito.verify(randomGenerator, Mockito.times(0)).nextInt(MAX_LOTTERY_NUMBER)  // Ensure random number generation was not called
    }

    @Test
    fun generateRandomLotteryTicket_should_handle_exceptions_gracefully() {
        // Arrange : simulate an exception thrown by Random
        `when`(randomGenerator.nextInt(MAX_LOTTERY_NUMBER)).thenThrow(RuntimeException("Test Exception"))

        // Act
        viewModel.generateRandomLotteryTicket()

        // Assert
        assertNull(viewModel.getTicketNumbers())
        assertNull(viewModel.lotteryTicket.value)
        assertTrue(viewModel.errorMessage.value == "Error generating random lottery ticket")
    }

    @Test
    fun generateRandomLotteryTicket_should_handle_edge_values_correctly() {
        // Arrange
        `when`(randomGenerator.nextInt(any(Int::class.java))).thenReturn(0,1,2,3,4, MAX_LOTTERY_NUMBER - 1) // Returns 1 and MAX_LOTTERY_NUMBER
        val observer = mock(Observer::class.java) as Observer<LotteryTicket> // Set up an observer for the LiveData
        viewModel.lotteryTicket.observeForever(observer)

        // Act
        viewModel.generateRandomLotteryTicket()

        // Assert
        val lotteryTicket = viewModel.lotteryTicket.value
        assertNotNull(lotteryTicket)

        val ticketNumbers = listOf(
            lotteryTicket!!.number1,
            lotteryTicket.number2,
            lotteryTicket.number3,
            lotteryTicket.number4,
            lotteryTicket.number5,
            lotteryTicket.number6
        )
        assertTrue(ticketNumbers.contains(1))
        assertTrue(ticketNumbers.contains(MAX_LOTTERY_NUMBER))
    }
    //endregion

    //region setWinnerStatus
    @Test
    fun setWinnerStatus_empty_list() {
        // Arrange
        val lotteryDrawList: List<LotteryDraw>? = emptyList()

        // Act
        viewModel.setWinnerStatus(lotteryDrawList)

        // Assert
        assertEquals("Error: List of draws is empty.", viewModel.errorMessage.value)

        Mockito.verify(randomGenerator, Mockito.times(0)).nextInt(lotteryDrawList?.size ?: 0)  // Ensure random number generation was not called

    }

    @Test
    fun setWinnerStatus_null_list() {
        // Arrange
        val lotteryDrawList: List<LotteryDraw>? = null

        // Act
        viewModel.setWinnerStatus(lotteryDrawList)

        // Assert
        assertEquals("Error: List of draws is empty.", viewModel.errorMessage.value)

    }

    @Test
    fun setWinnerStatus_ticket_numbers_sequence_is_null() {
        // Arrange
        val lotteryDrawList = listOf(LotteryDraw("draw-1", "2023-05-15", "2", "16", "23", "44", "47", "52", "14", 4000000000))
        viewModel.setTicketNumbers(null)

        // Act
        viewModel.setWinnerStatus(lotteryDrawList)

        // Assert
        assertEquals("Error: Ticket numbers are null.", viewModel.errorMessage.value)
    }

    @Test
    fun setWinnerStatus_winning_ticket() {
        viewModel.setTicketNumbers(listOf(1, 2, 3, 4, 5, 6))
        val lotteryDrawList = listOf(LotteryDraw("draw-1", "2023-05-15", "1", "2", "3", "4", "5", "6", "7", 4000000000))

        // Act
        viewModel.setWinnerStatus(lotteryDrawList)

        // Assert
        assertEquals("Congratulations! You've won the lottery!", viewModel.winnerStatus.value)
    }

    @Test
    fun setWinnerStatus_winning_with_5_balls_and_bonus_ball() {
        viewModel.setTicketNumbers(listOf(1, 2, 3, 4, 5, 6))
        val lotteryDrawList = listOf(LotteryDraw("draw-1", "2023-05-15", "1", "2", "3", "4", "5", "7", "6", 4000000000))

        // Act
        viewModel.setWinnerStatus(lotteryDrawList)

        // Assert
        assertEquals("Congratulations! You've won the lottery!", viewModel.winnerStatus.value)
    }

    @Test
    fun setWinnerStatus_no_match_at_all() {
        viewModel.setTicketNumbers(listOf(11, 22, 33, 44, 55, 56))
        val lotteryDrawList = listOf(LotteryDraw("draw-1", "2023-05-15", "1", "2", "3", "4", "5", "7", "6", 4000000000))

        // Act
        viewModel.setWinnerStatus(lotteryDrawList)

        // Assert
        assertEquals("No matches this time. Better luck next time!", viewModel.winnerStatus.value)
    }

    @Test
    fun setWinnerStatus_partially_winning_ticket() {
        viewModel.setTicketNumbers(listOf(1, 2, 3, 44, 55, 56))
        val lotteryDrawList = listOf(LotteryDraw("draw-1", "2023-05-15", "1", "2", "3", "4", "5", "7", "6", 4000000000))

        // Act
        viewModel.setWinnerStatus(lotteryDrawList)

        // Assert
        assertEquals("Only 3 balls matched. Better luck next time!", viewModel.winnerStatus.value)
    }

    @Test
    fun setWinnerStatus_1_number_winning_ticket() {
        viewModel.setTicketNumbers(listOf(1, 22, 33, 44, 55, 56))
        val lotteryDrawList = listOf(LotteryDraw("draw-1", "2023-05-15", "1", "2", "3", "4", "5", "7", "6", 4000000000))

        // Act
        viewModel.setWinnerStatus(lotteryDrawList)

        // Assert
        assertEquals("Only 1 ball matched. Better luck next time!", viewModel.winnerStatus.value)
    }
    //endregion


}