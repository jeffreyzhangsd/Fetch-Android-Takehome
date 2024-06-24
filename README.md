# Fetch Android Takehome
## _Completed using Kotlin_

### Project details
Display list of items with requirements:
- Display items grouped by "listId"
- Sort results by "listId", then by "name"
- Filter out items with blank or null "name"

### Project setup
I used Android Studio for Windows.
1. Clone the repository using git clone, user can also make a fork of the repository and then clone their own url
2. Open project in Android Studio
3. Sync the project with Gradle files (get dependencies)
4. Set up emulator or connect an Android device to see app
5. Set up config for web access to get data (https://fetch-hiring.s3.amazonaws.com/) in `AndroidManifest.xml`
    ```html
    <uses-permission android:name="android.permission.INTERNET" />


    ```
6. Run the project

### Images
![image](https://github.com/jeffreyzhangsd/Fetch-Android-Takehome/assets/109628105/dff4e4b5-0143-4b06-889f-23f82c4344d3)
![image](https://github.com/jeffreyzhangsd/Fetch-Android-Takehome/assets/109628105/f2b8654c-3a3f-4725-8e02-4af42d92c26c)




## Sources used
Docs on how to use Android Studio and project structure 
- [Android Studio Documentation](https://developer.android.com/studio/intro)

Code docs
   - [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
   - [Retrofit Documentation](https://square.github.io/retrofit/)
   - [Gson Converter Documentation](https://github.com/square/retrofit/tree/master/retrofit-converters/gson)
  
