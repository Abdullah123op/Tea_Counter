![alt text](https://github.com/Abdullah123op/Tea_Counter/blob/master/app/src/main/res/drawable/tea_counter.png?raw=true)

# Tea Counter

Tea Counter is a versatile Android application designed to manage and track sales for small businesses, such as tea shops and snack vendors. The app allows sellers to efficiently manage multiple items, handle customer interactions, and streamline the billing process. It offers a range of features that enhance user experience through intuitive design and smooth functionality.

## Features

- **User Authentication**: Secure login and registration using Firebase Authentication to ensure only authorized users have access.
- **Bill Generation**: Quick and easy bill generation with itemized listings to help sellers manage transactions efficiently.
- **User Management**: Sellers can manage their list of customers, including adding, editing, or removing customer details.
- **Firebase Integration**: Real-time data synchronization with Firebase Firestore to ensure all sales and inventory data are updated across all devices.
- **Custom Animations**: Enhanced user experience with engaging animations using Lottie to make navigation within the app more dynamic and appealing.
- **Multi-Item Support**: Sellers can add, edit, or delete multiple items from their inventory, providing flexibility for managing various products.
- **Customer Requests**: Customers can send requests for items directly through the app. However, they are not required to specify the quantity, simplifying the order process.
- **Notification System**: Push notifications to keep users updated about new orders, requests, and other important updates.

## Technologies Used

- **Java**: Core programming language for building the Android app.
- **Android Studio**: Integrated Development Environment (IDE) used for developing Android applications.
- **Firebase Firestore**: Cloud database to store and sync data across all users in real time.
- **Firebase Authentication**: Secure authentication service for managing user sign-in and registration.
- **Data Binding**: Efficiently bind UI components in the app’s layout to data sources, helping with the automatic update of UI.
- **Lottie Animations**: Library for adding high-quality animations to enhance user engagement.

## Getting Started

### Prerequisites

- **Android Studio**: Latest version recommended.
- **Firebase Account**: Required for integrating Firebase services like Firestore and Authentication.

### Installation

1. **Clone the repository**:
    ```sh
    git clone https://github.com/Abdullah123op/tea-counter.git
    ```
2. **Open the project in Android Studio**.
3. **Sync the project with Gradle files** to ensure all dependencies are resolved.
4. **Set up Firebase**:
    - Add your `google-services.json` file to the `app` directory.
    - Configure Firebase Firestore and Authentication in the Firebase console following the [Firebase setup guide](https://firebase.google.com/docs/android/setup).

### Running the App

1. **Connect an Android device** or start an emulator.
2. Click on the **"Run" button** in Android Studio to build and deploy the app.

## Project Structure

- **`app/src/main/java/com/tea/counter/`**: Contains all Java source files.
  - **`ui/`**: Contains all UI-related classes and activities, such as login screens, item lists, and bill generation interfaces.
  - **`model/`**: Contains data models, including item details, user profiles, and order information.
  - **`adapter/`**: Contains adapter classes for RecyclerViews to efficiently manage and display lists of items and customers.
  - **`utils/`**: Contains utility classes for common functions like network checks and date formatting.
  - **`dialog/`**: Contains custom dialog classes for user confirmations, item edits, and notifications.
- **`app/src/main/res/`**: Contains all resource files, including layouts, drawables, and values.
  - **`layout/`**: XML layout files for defining UI elements and screens.
  - **`values/`**: XML files for strings, colors, styles, dimensions, and themes.
- **`app/src/main/AndroidManifest.xml`**: The Android manifest file, which declares essential app components and permissions.

## Key Classes

### SplashActivity

Manages the splash screen animation and determines the navigation flow based on the user's login status, providing a smooth entry into the app.

### BillFragment

Handles the process of bill generation, allowing sellers to select items from the inventory and generate an itemized bill for customers.

### ItemModel

Data model representing individual items, including properties such as name, price, and availability.

### UserRequestFragment

Manages customer requests, allowing sellers to view incoming requests and handle them efficiently.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Open a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgements

- [Firebase](https://firebase.google.com/)
- [Lottie](https://airbnb.io/lottie/#/)
- [Android Studio](https://developer.android.com/studio)
