# Team Project
Presentation slides: https://docs.google.com/presentation/d/18canBrp3pQwd7SYneYI0OH1R9T0T_DX1/edit?slide=id.p1#slide=id.p1

# AidLearn  
### *AI-Powered Learning Assistant for Personalized Study Resources*

AidLearn is an AI-powered study platform that transforms uploaded course materials (PDFs such as lecture slides, readings, and textbooks) into structured learning resources. Using the Google Gemini API, the system generates notes, flashcards, and tests, which are saved locally and displayed within each course’s workspace.

---

## Features  

- Upload PDF files for AI processing  
- Automatic extraction and generation of study materials  
- Generate structured notes, flashcards, and timed tests  
- View history of previously generated materials  
- Manage courses (create, edit, delete)  
- Clean Architecture design with separate layers for UI, use cases, data access  

---

## User Stories  

### **User Story 1**  
As a user, I want to see a list of my courses on the Courses Homepage and open a course’s workspace so that I can access uploads, notes, flashcards, and tests.

### **User Story 2**  
As a user, I want to have lecture notes generated based on uploaded course material.

### **User Story 3**  
As a user, I want to have flashcards automatically generated based on my uploaded course materials.

### **User Story 4**  
As a user, I want to have timed tests (short-answer + MCQ) generated based on uploaded course material.

### **User Story 5**  
As a user, I want to upload and delete files that guide learning, and view existing uploads.

### **User Story 6**  
As a user, I want to view existing generated notes, flashcards, and tests.

---

## Use Cases  

### **Use Case 1: Courses Homepage & Workspace Management (CRUD)**  
**Lead:** Shirley Zhang  

**Main Flow:**  
- User sees a Courses Homepage with all enrolled/created courses  
- User may add, edit, or delete a course  
- User opens a Course Workspace  
- Workspace includes navigation to uploads, notes, flashcards, and tests  

**Alternate Flow:**  
- No courses → “Create New Course”  
- No uploads → disable generation features  

---

### **Use Case 2: Course Notes Generation**  
**Lead:** Peiyu Yu  

**Main Flow:**  
- User clicks *Generate Notes*  
- User enters a topic  
- Notes are generated and displayed in a structured format  
- User can edit, highlight, or save the notes  

**Alternate Flow:**  
- API failure → “Lecture notes generation failed. Please try again later.”  

---

### **Use Case 3: Flashcards Generation**  
**Lead:** Wenle Zeng  

**Main Flow:**  
- User clicks *Generate Flashcards*  
- User enters a topic  
- Flashcards are automatically generated  
- User flips cards between question/answer  
- User cycles through all cards  

**Alternate Flow:**  
- API failure → “Flashcard generation failed. Please try again later.”  

---

### **Use Case 4: Test Generation**  
**Lead:** Jace Mu  

**Main Flow:**  
- User clicks *Generate Tests*  
- User enters a topic and chooses a test duration  
- System generates MCQ, short-answer, and essay questions  
- User takes the test and submits answers  
- System displays score, corrections, and feedback  

**Alternate Flow:**  
- API failure → “Test generation failed. Please try again later.”  

---

### **Use Case 5: Uploads Management (CRUD)**  
**Lead:** Soumil Nag  

**Main Flow:**  
- User enters the Uploads page  
- User uploads PDF(s)  
- User deletes existing uploads  

**Alternate Flow:**  
- No files uploaded → “No files available”  

---

### **Use Case 6: View Existing Generated Material**  
**Lead:** Iain Campbell  

**Main Flow:**  
- User views historical notes, flashcards, or tests  
- Items include date, time, and course labels  
- Tests include scores  

**Alternate Flow:**  
- No history → “This page is empty”  

---

## Architecture Overview  

AidLearn follows **Clean Architecture**, consisting of:

- **Entities:**  
  Course, UploadedFile, GeneratedContent (notes, flashcards, tests)

- **Use Cases:**  
  Business logic for course management, content generation, uploads, and history

- **Interface Adapters:**  
  Controllers, Presenters, ViewModels, and local repositories

- **Framework/UI Layer:**  
  Java Swing UI and local file handling  
  Integration with Google Gemini API

This architecture ensures testability, modularity, and easy future extensions.

---

## API Integration: Google Gemini  

**API:** Google Gemini Generative Language API  
**Docs:** https://ai.google.dev  

**Features Used:**  
- Summaries  
- Structured lecture notes  
- Flashcards  
- Quizzes & tests  
- JSON formatted responses  

**Processing Flow:**  
1. User uploads PDF  
2. System extracts text  
3. System builds a prompt including topic + extracted content  
4. Gemini generates JSON study materials  
5. Output saved locally  
6. UI displays final results  

**API for the project:
**API Name: Google Gemini (Generative Language API) — https://ai.google.dev
Main Service Provided: Given a topic + timeframe, generates a structured JSON lesson plan (summaries, readings, practice prompts); can reformat/edit lessons.

Current functionality:
<img width="2559" height="1343" alt="image" src="https://github.com/user-attachments/assets/27bf03bd-a6d2-4f6a-a1a6-6d806e0af295" />
<img width="2559" height="1334" alt="image" src="https://github.com/user-attachments/assets/51d65ac9-872f-4f55-80d6-84ec8ff9df0d" />
<img width="2559" height="1327" alt="image" src="https://github.com/user-attachments/assets/7ad03afa-e912-4524-9819-05c702ee1981" />
