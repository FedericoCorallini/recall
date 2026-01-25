# 📚 Recall - Quiz Generator App (MVP)

An Android app that creates and runs quizzes/flashcards from PDFs using Kotlin and Jetpack Compose.

## Overview

Recall is an educational app that transforms PDF documents into interactive quizzes and flashcards. This MVP version includes a mocked question generation system, designed to be easily extended with real AI-powered PDF parsing in future iterations.

## Features (Iteration 1)

### Home Screen
- Clean, modern UI with Material 3 design
- PDF file picker integration (restricted to application/pdf)
- Automatic quiz generation from selected PDFs
- Loading states with progress indicators

### Quiz Screen
- **Multiple Choice Questions**: Radio button selection with 4 options
- **Flashcard Questions**: Free-text input for open-ended answers
- Question progress indicator
- Immediate answer submission and validation
- Per-question statistics tracking (success rate, timing, etc.)
- Summary screen showing final score and accuracy

### Data Persistence
- **Room Database** with two tables:
  - `pdf_sources`: Stores PDF metadata
  - `questions`: Stores questions with embedded statistics
- Real-time question stats updates after each answer
- Persistent storage across app sessions

## Architecture

The app follows **Feature-layered Clean Architecture** with strict separation of concerns:

```
app/
├── core/
│   ├── common/          # Result wrapper, DispatchersProvider, TimeProvider
│   ├── model/           # Shared domain models (PdfSource, Question, etc.)
│   ├── db/              # Room Database, DAOs, TypeConverters
│   └── navigation/      # Navigation routes and NavGraph
├── home/
│   ├── domain/          # GenerateFromPdfUseCase + repository interface
│   ├── data/            # Repository implementation + mock generator
│   └── presentation/    # HomeScreen + HomeViewModel
├── quiz/
│   ├── domain/          # ObserveQuestionsUseCase + SubmitAnswerUseCase
│   ├── data/            # Repository implementation
│   └── presentation/    # QuizScreen + QuizViewModel
└── di/                  # Hilt dependency injection modules
```

### Key Principles
- **Domain layer**: Pure business logic, no Android dependencies
- **Data layer**: Maps Room entities ↔ domain models
- **Presentation layer**: ViewModels with StateFlow, dumb Composables
- **Dependency Injection**: Hilt for all features and core modules
- **Navigation**: Centralized in `core/navigation` (not per-feature)

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **DI**: Hilt
- **Database**: Room
- **Navigation**: Jetpack Navigation Compose
- **Async**: Kotlin Coroutines + Flow
- **Architecture**: MVVM + Clean Architecture
- **Build**: Gradle with Kotlin DSL

## Data Models

### Core Domain Models

```kotlin
enum class QuestionType { MULTIPLE_CHOICE, FLASHCARD }

data class PdfSource(
    val id: String,
    val displayName: String,
    val uriString: String,
    val createdAtEpochMs: Long
)

data class Question(
    val id: String,
    val sourceId: String,
    val type: QuestionType,
    val prompt: String,
    val options: List<String>,    // Empty for flashcards
    val answer: String,            // Correct answer text
    val stats: QuestionStats
)

data class QuestionStats(
    val totalTimesAsked: Int,
    val totalSuccess: Int,
    val lastTimeAskedEpochMs: Long?,
    val wasLastTimeSuccess: Boolean?,
    val rating: Float              // totalSuccess / totalTimesAsked
)
```

## Mock Question Generation

Currently, the app generates **6 hardcoded questions** about software engineering topics:
- 4 multiple-choice questions (Kotlin, HTTP, Clean Architecture)
- 2 flashcard questions (SOLID principles, ViewModels)

**Location**: `home/data/generator/MockQuestionGenerator.kt`

## How to Extend for Real AI Integration

### Step 1: Implement PDF Parsing
Replace mock generation in `GenerationRepositoryImpl`:
```kotlin
// Current: MockQuestionGenerator.generateQuestions(sourceId)
// Future: Parse PDF content from URI
val pdfContent = pdfParser.extractText(uriString)
```

### Step 2: Integrate AI API
Add AI question generation service:
```kotlin
interface AiService {
    suspend fun generateQuestions(content: String): List<Question>
}

// Use Retrofit with OpenAI/Gemini/etc.
```

### Step 3: Update Use Case
Modify `GenerateFromPdfUseCase` to call real API:
```kotlin
val content = pdfParser.extractText(uriString)
val questions = aiService.generateQuestions(content)
```

### Files to Modify
- `home/data/repository/GenerationRepositoryImpl.kt` - Main generation logic
- `home/data/generator/MockQuestionGenerator.kt` - Replace with real parser
- Add PDF parsing library (e.g., `Apache PDFBox` or `iText`)
- Add AI API client (Retrofit already included)

## Building and Running

### Requirements
- Android Studio Ladybug or later
- Minimum SDK: 26 (Android 8.0)
- Target SDK: 36

### Build Steps
1. Clone the repository
2. Open project in Android Studio
3. Sync Gradle dependencies
4. Run on emulator or physical device

### Testing the Flow
1. Launch app → See Home screen
2. Tap "Upload PDF" → Select any PDF file
3. Wait for generation (mocked, instant)
4. Answer quiz questions one by one
5. View final score summary
6. Return to Home screen

## Database Schema

### pdf_sources Table
```sql
CREATE TABLE pdf_sources (
    id TEXT PRIMARY KEY,
    displayName TEXT NOT NULL,
    uriString TEXT NOT NULL,
    createdAtEpochMs INTEGER NOT NULL
)
```

### questions Table
```sql
CREATE TABLE questions (
    id TEXT PRIMARY KEY,
    sourceId TEXT NOT NULL,
    type TEXT NOT NULL,        -- MULTIPLE_CHOICE or FLASHCARD
    prompt TEXT NOT NULL,
    options TEXT NOT NULL,      -- JSON array
    answer TEXT NOT NULL,
    totalTimesAsked INTEGER NOT NULL DEFAULT 0,
    totalSuccess INTEGER NOT NULL DEFAULT 0,
    lastTimeAskedEpochMs INTEGER,
    wasLastTimeSuccess INTEGER, -- SQLite boolean (0/1)
    rating REAL NOT NULL DEFAULT 0.0
)
```

## Future Enhancements

### Iteration 2+
- [ ] Real PDF text extraction
- [ ] AI-powered question generation (OpenAI/Gemini)
- [ ] Question difficulty levels
- [ ] Spaced repetition algorithm
- [ ] History screen showing past quizzes
- [ ] Export quiz results
- [ ] Dark mode support
- [ ] Multi-language support

## License

This is an MVP project for demonstration purposes.

---

**Built with ❤️ using Kotlin + Jetpack Compose**
