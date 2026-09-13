# Inventour - Smart Warehouse Management System

Inventour is an Android application designed for modern warehouse management. It provides a robust solution for tracking inventory, managing product locations, and monitoring occupancy levels in real-time. The application follows an offline-first architecture to ensure reliability in environments with limited connectivity.

## Core Features

- **Authentication**: Secure user registration and login using Firebase Authentication.
- **Inventory Management**: Full CRUD operations (Create, Read, Update, Delete) for products.
- **Barcode & QR Scanning**: Integrated camera scanning using CameraX and ML Kit for rapid data entry.
- **Offline Support**: Data persistence using Room SQLite database, allowing the app to function without an internet connection.
- **Real-time Synchronization**: Automatic and manual (Pull-to-Refresh) synchronization between the local database and Google Firestore.
- **Global Notifications**: Push notifications via Firebase Cloud Messaging (FCM) and Cloud Functions that alert all users to inventory changes.
- **Occupancy Analytics**: Visual dashboards and alerts when warehouse capacity crosses critical thresholds (25%, 50%, 75%, 100%).
- **Advanced UI/UX**: Built with Jetpack Compose, featuring search filtering, swipe-to-delete gestures, and a responsive sidebar navigation.

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Dependency Injection**: Dagger Hilt
- **Local Database**: Room
- **Cloud Database**: Firebase Firestore
- **Backend Triggers**: Firebase Cloud Functions (TypeScript)
- **Messaging**: Firebase Cloud Messaging (FCM)
- **Image Processing**: ML Kit & CameraX
- **Architecture**: MVVM (Model-View-ViewModel)

## Getting Started

### Prerequisites

- Android Studio Koala or newer.
- A Firebase project with Firestore, Authentication, and Cloud Functions enabled.
- Node.js and npm installed for Cloud Functions deployment.

### Firebase Setup

1. Place your `google-services.json` file in the `app/` directory.
2. Ensure your Firebase project is on the Blaze (Pay-as-you-go) plan to use Cloud Functions.

### Cloud Functions Deployment

1. Navigate to the `functions/` directory.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Log in to Firebase:
   ```bash
   npx firebase login
   ```
4. Deploy the triggers:
   ```bash
   npx firebase deploy --only functions
   ```

### Building the App

1. Open the project in Android Studio.
2. Sync Project with Gradle Files.
3. Run the `app` module on an emulator or physical device.

## License

This project was developed as part of the "Razvoj mobilnih aplikacija" (RMA) course.
