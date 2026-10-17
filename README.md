# LLM + RAG Connection — Android Application

An Android application that compares responses generated directly by a Large Language Model (LLM) with responses generated using Retrieval-Augmented Generation (RAG).

**Project:** 3-1 Module 3 — Hands-On 3  
**Mentor:** Kanoj Kumar Chavallam  
**Platform:** Android  
**Backend:** Python Flask

## Project Overview

Large Language Models can answer questions using their learned knowledge, but they may not always provide answers grounded in a specific knowledge base.

This project demonstrates how RAG can retrieve relevant documents and provide that context to an LLM before generating an answer.

The application displays direct LLM answers and RAG-based answers side by side, along with response times and retrieved source documents.

## Objectives

- Understand the difference between direct LLM generation and RAG.
- Connect an Android application to a Python Flask backend.
- Integrate an LLM API for answer generation.
- Retrieve relevant documents using semantic similarity.
- Compare response times and generated answers.
- Display retrieved sources to help users understand the context behind RAG responses.

## Technology Stack

| Component | Technology |
|---|---|
| Android application | Kotlin |
| UI | Android XML layouts |
| API communication | Retrofit and OkHttp |
| Backend | Python and Flask |
| Language model | Google Gemini API |
| Semantic retrieval | Sentence Transformers |
| Embedding model | `all-MiniLM-L6-v2` |
| Document storage | JSON |
| Version control | Git and GitHub |

## Application Features

### 1. Direct LLM Answer
Sends the user's question to the language model without retrieving documents from the project knowledge base.

### 2. RAG Answer
Retrieves relevant documents from the knowledge base and uses the retrieved context to generate an answer.

### 3. Answer Comparison
Displays both answers so users can compare their content.

### 4. Response-Time Display
Shows the response time reported by the backend for each answer.

### 5. Retrieved Sources
Displays relevant source documents returned by the retrieval system.

## System Architecture

1. The user enters a question in the Android application.
2. The Android application sends the request to the Flask backend using Retrofit.
3. The backend generates a direct LLM answer.
4. The retrieval service searches the knowledge base for relevant documents.
5. The RAG service generates an answer using the retrieved context.
6. The backend returns the results to the Android application.
7. The application displays both answers, timings, and retrieved sources.

## Project Structure

```text
31MODULE3HANDSON33/
├── app/
│   └── src/main/
│       ├── java/com/example/a3_1module3handson3/
│       │   ├── api/
│       │   ├── data/
│       │   ├── repository/
│       │   ├── viewmodel/
│       │   └── MainActivity.kt
│       └── res/
│           ├── layout/
│           ├── drawable/
│           └── values/
├── python backend/
│   ├── app.py
│   ├── config.py
│   ├── services/
│   ├── data/
│   └── .env.example
└── README.md
```

*Adjust the backend folder name and files in this diagram to match the actual repository structure.*

## Setup and Execution

### Prerequisites

- Android Studio
- Python 3.10 or a compatible version
- A Google Gemini API key
- Android emulator or compatible Android device

### 1. Configure the backend

Open the Python backend directory and create a virtual environment:

```bash
python -m venv venv
```

Activate it on Windows:

```powershell
.\venv\Scripts\Activate.ps1
```

Install the packages listed in your project's `requirements.txt` file:

```bash
pip install -r requirements.txt
```

Create a `.env` file using the project's environment-variable template and add your Gemini API key. Never commit the real `.env` file.

### 2. Start the Flask server

Run the backend using the entry point configured in your project. For example:

```bash
python app.py
```

Confirm that the backend health endpoint works at:

```text
http://127.0.0.1:5000/health
```

### 3. Connect the Android application

For the standard Android Studio emulator, configure the Retrofit base URL as:

```text
http://10.0.2.2:5000/
```

The Android application should call the backend comparison endpoint:

```text
POST /api/compare
```

Make sure the Flask server is running before testing the application.

### 4. Run the application

1. Open the project in Android Studio.
2. Sync the Gradle project.
3. Start the Flask backend.
4. Launch the app on an emulator.
5. Enter a question and tap **Compare Answers**.
6. Review both answers, response times, and retrieved sources.

## Example Test Question

> What is Retrieval-Augmented Generation (RAG), and how does it improve the accuracy of an LLM?

The actual answers and retrieved sources depend on the configured Gemini model and the contents of the knowledge base.

## Learning Outcomes

- Android-to-backend API integration.
- REST API development using Flask.
- LLM API integration.
- Semantic search and document retrieval.
- RAG pipeline fundamentals.
- Asynchronous network requests and response handling.
- Comparing direct generation with context-grounded generation.

## Security Notes

- Keep API keys and secrets in environment variables.
- Do not commit `.env`, virtual environments, cache files, or API credentials.
- Include a `.env.example` file containing placeholder values only.
- Never put the Gemini API key inside the Android application.

## Acknowledgment

This project was developed as part of the Module 3 hands-on learning activity under the guidance of **Kanoj Kumar Chavallam**.

## License

This project is intended for educational and learning purposes. Add a license file if you wish to specify reuse and distribution permissions.
