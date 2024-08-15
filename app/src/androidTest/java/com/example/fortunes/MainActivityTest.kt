package com.example.fortunes

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fortunes.viewmodel.SharedViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule() //initializes the Mockito framework before the tests run

    @Mock
    private lateinit var sharedViewModel: SharedViewModel //mock instance

    @Test
    fun testActivityLaunchesAndFragmentIsDisplayed() {
        //ActivityScenario.launch(MainActivity::class.java)

        // Check if the fragment is displayed
        onView(withId(R.id.fragment_container))
            .check(matches(isDisplayed()))

        // Verify that the ViewModel's methods are called during onStart
        verify(sharedViewModel, times(1)).fetchDrawList()
        verify(sharedViewModel, times(1)).setLoadingVisibilityState(true)
    }
}