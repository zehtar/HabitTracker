# HabitTracker

A personal Android app for building consistent routines and understanding how you spend your time.

HabitTracker lets you track habits in three different ways, plan your week by category, review your progress, and keep your routine visible with reminders and a home screen widget.

## Download

Download the latest APK from the [GitHub Releases](../../releases/latest) page.

## Features

### Habit tracking
- Create, edit, and delete habits.
- Track habits as **completion**, **time spent**, or **count**.
- Assign each habit a category and priority.
- Customize habit icons and colors.
- Add an optional personal guideline to a habit.
- Pause habits temporarily or indefinitely.
- Reset a habit streak without deleting its history.

### Weekly planning
- Set weekly targets for categories instead of scheduling individual habits day by day.
- Track time, completion counts, and numeric counts separately.
- Compare planned targets with actual results.
- Review completed weekly plans and their history.

### Statistics and history
- View daily and weekly progress.
- Track streaks and completion history in a calendar.
- Review activity statistics and time spent.
- Compare current and previous periods.
- Explore statistics for individual habits and categories.

### Reminders and widget
- Set reminders for habits.
- View your habits and daily progress from the Android home screen.
- Open a habit directly from the widget.
- Keep the widget updated as your progress changes.

## Screenshots

Screenshots will be added in a future update.

## Tech stack

- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **Room** and **SQLite**
- **Kotlin Coroutines** and **StateFlow**
- **AndroidX ViewModel**
- **Navigation Compose**
- **Jetpack Glance** for the home screen widget
- **WorkManager** for background work

## Architecture

The app follows a layered architecture:

```text
Compose UI
    |
 ViewModel
    |
Repository
    |
 Room Database
    |
  SQLite
```

## Requirements

- Android 9.0 (API 28) or higher.

## Installation

1. Open the [Releases](../../releases/latest) page.
2. Download the APK attached to the latest release.
3. Install the APK on your Android device.
4. If Android asks for permission, allow installation from your browser or file manager.

## Data

Habit data is stored locally on the device using Room and SQLite. The app does not require an account to track habits.

## Project status

The core feature set is implemented. The project is currently focused on final testing, polish, and release preparation.
