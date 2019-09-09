# Live Data [work in progress]

Kotlin Multiplatform implementation of Android's [Live Data](https://developer.android.com/topic/libraries/architecture/livedata).

## Introduction

&nbsp;1. Create your view model in Multiplatform with Live Data objects:

```kotlin
import com.andrewemery.livedata.MutableLiveData

val currentName: MutableLiveData<String> = MutableLiveData()
```

&nbsp;2. Interact with your Live Data objects in your Android application:

```kotlin
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

val currentName: MutableLiveData = viewModel.currentName
currentName.observe(this, Observer { name ->
    nameTextView.text = name
})
```

&nbsp;3. Then interact with your Live Data objects in your iOS project as well:

```swift
import com.andrewemery.livedata.LifecycleOwner

class Owner : LifecycleOwner { ... }
let owner: Owner()
viewModel.currentName.observe(owner, observer)
```
