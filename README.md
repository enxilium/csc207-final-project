# Team Project
Presentation slides: https://docs.google.com/presentation/d/18canBrp3pQwd7SYneYI0OH1R9T0T_DX1/edit?slide=id.p1#slide=id.p1

Team Name: AidLearn
**Domain: AI-powered learning assistant that transforms uploaded course materials into personalized study resources.
Description: The system allows users to upload course-related PDF files such as lecture slides, readings, or textbooks. Once uploaded, the app automatically extracts and sends the content to Google Gemini, and receives structured outputs in JSON format. These include summarized notes, flashcards, quizzes, and practice tests. The generated materials are stored locally and displayed on the user’s course dashboard. 

**User Stories:
**User story 1: As a user, I want to see a list of my courses on the Courses Homepage and open a course’s workspace so that I can access uploads, notes, flashcards, and tests for that course.
User story 2: As a user, I want to have lecture notes generated based on uploaded course material.
User story 3: As a user, I want to have flash cards automatically generated based on my uploaded course materials.
User story 4: As a user, I want to have timed tests, with short answers and multiple choice options, generated based on uploaded course material.
User story 5: As a user, I want to be able to upload and delete files that can guide learning, as well as view existing uploads.
User story 6: As a user, I want to view existing generated material for notes, tests, and/or flashcards.


**Use Cases:
Use Case 1: Courses Homepage and Workspace Management (CRUD) 
_Shirley Zhang_
Main Flow:
• User has a Courses Homepage displaying a list of all enrolled or created courses.
• User can add, edit, or delete a course.
• User can click into each course to see the Course Workspace
• In each Course Workspace, there is the option to navigate to the upload page, generate or view existing Notes, Flashcards, or Tests
Alternative Flow:
• No courses → show “Create New Course.”
• No uploads → can’t generate new Notes/Flashcards/Tests


Use Case 2:  Course Notes Generation
_Peiyu Yu_
Main Flow:
 • User clicks on Generate Notes button in the Course Workspace to enter the Generate Notes page
 • In the Generate Notes page, the User can then input a topic  
 • Notes are generated and displayed in a structured format.
 • User can edit, highlight, or save the notes.
• The user can exit out back to the Course Workspace
 Alternative Flow:
 • If the API request fails → show error message “Lecture notes  generation failed. Please try again later.”


Use Case 3: Course Flashcards Generation
_Wenle Zeng
_Main Flow:
• User clicks on Generate Flashcards button in the Course Workspace to enter the Generate Flashcards page
• User can then input a topic 
• Flash cards are automatically generated and displayed.
• Learners can flip to see question/answer and proceed to the next flashcard
• Once done all the flashcards, the user can exit out back to the Course Workspace
Alternative Flow:
• If the API request fails → show error message “Flashcard generation failed. Please try again later.”


Use Case 4: Course Test Generation
_Jace Mu
_Main Flow:
• User clicks on Generate Tests button in the Course Workspace to enter the Generate Test page
• On the Generate Test page, the user can then input a topic and choose a certain time period (ex. 15 minutes, 30 minutes, 1 hour to finish all questions)
• System creates a test with short-answer, essay, and multiple-choice questions.
• User takes the test and submits answers.
• User displays score, feedback, and correct answers.
• The user can exit out back to the Course Workspace
Alternative Flow:
• If the API request fails → show error message “Test generation failed. Please try again later.”.


Use Case 5: Course Context Update (CRUD)
_Soumil Nag
_Main Flow:
• User clicks on Generate Tests button in the Course Workspace to enter the Uploads page
• User clicks on a button to upload a PDF file for content generation.
• User can click on a button next to each file uploaded to delete the upload
• Once done, the user can exit out back to the Course Workspace
Alternative Flow:
• No files uploaded -> Displays “no files available”.


Use Case 6: View Existing Material 
_Iain Campbell
_Main Flow:
• User clicks on View Existing Flashcards, View Existing Tests , or View Existing Notes button in the Course Workspace to enter the Flashcards History page, Tests History page, or Notes history page
• User is able to see history of past generated tests, flash cards, and lecture notes and it’s contents
• User has the date, time, and course each labelled clearly.
• User is able to see score on past tests
• Once done, the user can exit out back to the Course Workspace
Alternative Flow:
• No History → Display “This page is empty”


**API for the project:
**API Name: Google Gemini (Generative Language API) — https://ai.google.dev
Main Service Provided: Given a topic + timeframe, generates a structured JSON lesson plan (summaries, readings, practice prompts); can reformat/edit lessons.

Current functionality:
<img width="2559" height="1343" alt="image" src="https://github.com/user-attachments/assets/27bf03bd-a6d2-4f6a-a1a6-6d806e0af295" />
<img width="2559" height="1334" alt="image" src="https://github.com/user-attachments/assets/51d65ac9-872f-4f55-80d6-84ec8ff9df0d" />
<img width="2559" height="1327" alt="image" src="https://github.com/user-attachments/assets/7ad03afa-e912-4524-9819-05c702ee1981" />
