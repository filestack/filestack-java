<p align="center"><img src="logo.svg" align="center" width="100"/></p>
<h1 align="center">Filestack Java SDK</h1>

<p align="center">
  <a href="https://bintray.com/filestack/maven/filestack-java">
    <img src="https://img.shields.io/badge/bintray-v0.6.0-blue.svg?longCache=true&style=flat-square">
  </a>
  <a href="https://filestack.github.io/filestack-java/">
    <img src="https://img.shields.io/badge/ref-javadoc-795548.svg?longCache=true&style=flat-square">
  </a>
  <img src="https://img.shields.io/badge/java_version-7-green.svg?longCache=true&style=flat-square">
  <a href="https://travis-ci.org/filestack/filestack-java">
    <img src="https://img.shields.io/travis/filestack/filestack-java.svg?style=flat-square">
  </a>
  <a href="https://coveralls.io/github/filestack/filestack-java">
    <img src="https://img.shields.io/coveralls/filestack/filestack-java.svg?style=flat-square">
  </a>
</p>

<p align="center">
  Java SDK for Filestack. Includes wrappers for Core, Upload, Transformation, and Cloud APIs. Supports Amazon Drive, Box, Dropbox, Facebook, GitHub, Gmail, Google Drive, Google Photos, Instagram, and OneDrive.
</p>

## Install
```
compile 'com.filestack:filestack-java:0.6.0'
```

## Upload
```java
// Create a client
Config config = new Config("API_KEY");
Client client = new Client(config);

// Set options and metadata for upload
StorageOptions options = new StorageOptions.Builder()
    .mimeType("text/plain")
    .filename("hello.txt")
    .build();

// Perform a synchronous, blocking upload
FileLink file = client.upload("/path/to/file", false);

// Perform an asynchronous, non-blocking upload
Flowable<Progress<FileLink>> upload = client.uploadAsync("/path/to/file", false);
upload.doOnNext(new Consumer<Progress<FileLink>>() {
  @Override
  public void accept(Progress<FileLink> progress) throws Exception {
    System.out.printf("%f%% uploaded\n", progress.getPercent());
    if (progress.getData() != null) {
      FileLink file = progress.getData();
    }
  }
});
```

## Asynchronous Functions
Every function that makes a network call has both a synchronous (blocking) and asynchronous (non-blocking) version. The asynchronous versions return [RxJava][rxjava-repo] classes (Observable, Single, Completable).

For example to delete a file both synchronously and asynchronously:
```java
// Synchronous, blocking
fileLink.delete();

// Asynchronous, not blocking
fileLink.deleteAsync().doOnComplete(new Action() {
  @Override
  public void run() throws Exception {
    System.out.println("File deleted.");
  }
});
```

## FileLink Operations
```
String handle = fileLink.getHandle(); // A handle is a file ID
fileLink.download("/path/to/save/file");
fileLink.delete();
fileLink.overwrite("/path/to/new/file");
```

## Transformations
The transform functions generate URLs for backend transformations; No local processing occurs. With the exception of video transformations, front-end clients can directly use the generated URLS.

For example, to reduce image bandwidth usage, generate resize transform URLs based on display size:
```java
ResizeTask task = new ResizeTask.Builder()
    .width(100)
    .fit("center")
    .build();
ImageTransform transform = fileLink.imageTransform().addTask(task);
String url = transform.url();
```

## Cloud Transfers

```java
// Check a user's auth status by first trying to list contents of drive
CloudResponse response = client.getCloudItems(Sources.GOOGLE_DRIVE, "/");
String authUrl = response.getAuthUrl();
// If auth URL isn't null, user needs to authenticate, open URL in browser
if (authUrl != null) {
    openBrowser(authUrl);
    return;
}

// Transfer the first file from the cloud provider to Filestack
CloudItem first = response.getItems()[0];
client.storeCloudItem(Sources.GOOGLE_DRIVE, first.getPath());

// Check for another page of results
String nextToken = response.getNextToken();
// If next token isn't null, there's more items to fetch
if (nextToken != null) {
  response = client.getCloudItems(Sources.GOOGLE_DRIVE, "/", nextToken);
}

// Save the session token
String sessionToken = client.getSessionToken();
saveSessionToken(sessionToken);
```

## Cloud Auth States
There are 3 levels of state to be mindful of with cloud transfers: 1) the local state (stored by a session token) 2) the authorization state between the user's cloud account and Filestack (on the backend) and 3) (potentially) the user's login state within the browser where the OAuth flow is performed.

The local authentication state is maintained by a session token. A new token is returned by the Cloud API on every request. Each request should use the latest session token. You can confuse state by using old session tokens, because the token determines the state. For example if you log out a user, then later use an old session token from before the logout request was performed, the user would still be logged in. The Client class manages the token automatically but you should manually save the session token before a client is destroyed.

The authorization state in the local session is not connected to the authorization state between a cloud account and Filestack. For example a user can be logged out of an account in a local session, but still see Filestack as authorized against their account in the account's settings. A user must revoke access to Filestack within a cloud provider's settings to truly disconnect Filestack.

You should also be mindful of the user's login state in the browser. For example if a user completes the OAuth flow in a browser, then logs out of the account within an app using the SDK, then goes through the OAuth flow in the same browser, there may be confusing behavior because they were still logged into the account in the browser.

[rxjava-repo]: https://github.com/ReactiveX/RxJava
