### PolyChat
Yes, it's another AI chat project, but this one is a polyglot, connecting to OpenAI, Gemini, and beyond. Additionally, it is multi-platform!

---

This is a Kotlin Multiplatform project targeting Android, iOS, Desktop.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.  
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.  
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,  
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,   
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

---
### Build dmg for macOS

```  
./gradlew packageDmg  
```  
located in: .../ChatClient/composeApp/build/compose/binaries/main/app

  
---
### Used additional libraries:
#### Icon Pack
https://github.com/DevSrSouza/compose-icons?tab=readme-ov-file  
https://github.com/DevSrSouza/compose-icons/blob/master/line-awesome/DOCUMENTATION.md  
https://icons8.com/line-awesome

#### Custom load indicators
https://github.com/EhsanMsz/MszProgressIndicator/tree/master

#### Markdown Renderer
https://github.com/mikepenz/multiplatform-markdown-renderer

#### File Picker
https://github.com/vinceglb/FileKit

