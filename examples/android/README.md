# Simple Android Example

This example demonstrates how to use the async, observable functions to make calls in Android. Take note of the `.observeOn(AndroidSchedulers.mainThread())` calls, this ensures we receive results on the UI thread. The app has a simple UI with a button to upload, a button to delete, and a TextView for logging. You can select a file to upload, getting a link to view the file in the browser. You can also delete the last uploaded file.

Add the following dependencies to your build.gradle:

```Groovy
compile 'io.reactivex.rxjava2:rxjava:2.y.z'
compile 'io.reactivex.rxjava2:rxandroid:2.y.z'
compile 'org.filestack:filestack-java:x.y.z'
```

And add the following permissions to your AndroidManifest.xml:

```XML
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
