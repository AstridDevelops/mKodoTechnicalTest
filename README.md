Fortunes App (Kotlin)

Overview

The Fortunes App is an Android application developed in Kotlin that allows users to view and interact with lottery draws.
The app is designed using the Model-View-ViewModel (MVVM) architecture pattern which provides a robust structure for managing UI-related data in a lifecycle-conscious way.
The app utilizes LiveData for data binding and view binding to ensure a responsive and dynamic user interface.

Technology Choices

Architecture

   MVVM Pattern: Utilized to separate concerns and manage UI-related data more effectively. This architecture enhances maintainability and testability by clearly defining the responsibilities of each component:
   Model: Represents the data and business logic.
   View: Represents the UI elements and handles user interactions.
   ViewModel: Acts as a bridge between the Model and the View, holding and managing UI-related data.

Libraries and Tools
  LiveData: Used to handle and observe changes in data. The library helps to keep the UI updated when data changes occure in a lifecycle-aware manner.
  RecyclerView: Used to display a dynamic list of lottery draws. The RecyclerView is paired with an adapter and DiffUtil to efficiently handle changes and updates in the list.
  View Binding: Implemented to simplify the interaction between the UI and code, reducing the risk of null pointer exceptions and making the code more readable.

UI Design
  Fragments: The app features three fragments within the main activity:
  Draw List Fragment: Displays a list of available lottery draws.
  Draw Details Fragment: Shows detailed information for a selected lottery draw.
  Ticket Numbers Fragment: Generates a sequence of random numbers and compares them against a lottery draw to determine if the ticket is a winner.

Testing
  Espresso, JUnit4, Mockito
  Unit Testing: Focused on the YourTicketViewModel to ensure that the ViewModel's logic functions correctly in isolation. Unit tests verify that data manipulation and business logic are handled as expected.
  Integration Testing: Conducted for the MainActivity class to ensure that the activity's components work together correctly, providing a seamless user experience.

Improvements: While the app is functional, there are areas for potential enhancement:
    Jet Compose (As I am more efficient in xml layouts, I have chosen this solution that is faster for me and let me focus on the essential tasks) 
    Local data storage 
    Mock Server 
    Animations
    Additional Unit and Integration Tests: Expanding test coverage to include more components and scenarios.
    UI/UX Enhancements: Refinements to the user interface and experience based on user feedback.
    
