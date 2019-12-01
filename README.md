# ApplicationProvider

# Retrieve the android application from anywhere
Useful to develop a standalone library

```kotlin
//from anywhere
val application = ApplicationProvider.application
```

# Retrieve the current activity from anywhere
```kotlin
//from anywhere
val currentActivity = ActivityProvider.currentActivity
```

# Download

<a href='https://ko-fi.com/A160LCC' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi1.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

[ ![Download](https://api.bintray.com/packages/florent37/maven/applicationprovider/images/download.svg) ](https://bintray.com/florent37/maven/applicationprovider/)
```java
dependencies {
    implementation 'com.github.florent37:applicationprovider:(lastest version)'
}
```

# Initialize classes that needs a context

You do not need to override the Application now

# Before

```kotlin
class MyApplication : Application() {
    override fun onCreate(){
        Stetho.initializeWithDefaults(application)
    }
}
```

# After

## Using a provider (for libraries)

*Note that you can include it directly on your library's aar*

```kotlin
class StethoInitializer : Provider() {
    override fun provide() {
        val application = ApplicationProvider.application
        Stetho.initializeWithDefaults(application)
    }
}

//or with an ProviderInitializer :

class StethoInitializer : ProviderInitializer() {
    override fun initialize(): (Application) -> Unit = {
        Stetho.initializeWithDefaults(application)
    }
}
```

```xml
<provider
     android:name=".timber.TimberInitializer"
     android:authorities="${applicationId}.StethoInitializer" />
```

## Using an initializer

Another way using Initializer

```kotlin
val InitializeStetho by lazy {
    ApplicationProvider.listen { application ->
        Stetho.initializeWithDefaults(application)
    }
}

class MainActivity : AppCompatActivity() {

    init {
        InitializeStetho
    }

    override fun onCreate(savedInstanceState: Bundle?) {
    ...
    }
}
```

## Access current activity

Access current activity from anywhere using 

```
val currentActivity : Activity? = ActivityProvider.currentActivity()
```

Or safety from a kotlin coroutine context : 

```kotlin
launch {
    val currentActivity = ActivityProvider.activity() //cannot be null
    Log.d(TAG, "activity : $currentActivity")
}
```

## Listen for current activity

```kotlin
launch {
    ActivityProvider.listenCurrentActivity().collect {
        Log.d(TAG, "activity : $currentActivity")
    }
}
```