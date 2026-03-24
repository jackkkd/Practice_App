# 📚 Android Library Management App

A robust Android practice application built to demonstrate local database management, secure user authentication, and Role-Based Access Control (RBAC). 

Users can browse a catalog of books, search for specific titles, and view detailed information. Administrators have elevated privileges to add new books to the database and delete existing ones.

## ✨ Features

* **Role-Based Access Control (RBAC):** Distinct UI and permission levels for 'Admin' and 'User' roles.
* **Secure Authentication:** Passwords are encrypted using SHA-256 hashing before being stored in the database.
* **Local Database:** Fully offline data persistence using SQLite (`SQLiteOpenHelper`).
* **Dynamic UI:** Interface adapts instantly based on the logged-in user's role (e.g., hiding action buttons for regular users).
* **Live Search:** Filter books in the library in real-time.
* **Dark/Light Mode:** Includes theme persistence using `SharedPreferences`.

---

## 🔐 Default Admin Credentials

The database is seeded with a default Administrator account upon the first installation.

* **Username:** `hello`
* **Password:** `tanginamo`

*Note: Any new account registered through the app's sign-up screen will automatically be assigned the standard `USER` role.*

---

## 🖼️ How to Add Custom Book Covers

If you want to add new cover images to the app, follow these steps:

**1. Prepare your Image:**
* Ensure your image is a `.png` or `.jpg`.
* **Important:** Rename the file to use **only lowercase letters, numbers, and underscores** (no spaces or capital letters). 
* *Example:* `the_anarchist_cookbook.jpg` or `java_guide_2024.png`

**2. Add to Android Studio:**
* Copy the image file from your computer.
* In Android Studio, navigate to `app/src/main/res/drawable`.
* Right-click the `drawable` folder and click **Paste**.

**3. Update the Code:**
To make the new image selectable when adding a book, update the `showAddBookMenu()` method in `LibraryFragment.java`:

Add the new option to the Spinner list:
```java
String[] imageOptions = {"Cookbook Cover", "Cryptography Cover", "My New Book", "Default Placeholder"};
