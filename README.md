<p align="center"><img src="logo.svg" align="center" width="100"/></p>
<h1 align="center">Filestack Java SDK</h1>

<p align="center">
  <a href="https://bintray.com/filestack/maven/filestack-java">
    <img src="https://img.shields.io/badge/bintray-v0.8.0-blue.svg?longCache=true&style=flat-square">
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
implementation 'org.filestack:filestack-java:0.9.0'
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
There are 3 levels of state to note with cloud integrations: 1) the local state (stored by a session token) 2) the authorization state between the user's cloud account and Filestack (on the backend) and 3) (potentially) the login state within the browser where users complete the OAuth login.

A session token determines the auth state between an app and Filestack. Every response includes a refreshed token, and every request should send the last token received. Tokens do not just identify a session, they hold the session state. For example if a user logs out of a cloud, only the token returned by the logout response reflects that action; Using the old token would still allow listing the contents of the logged out cloud. To maintain state across client destruction, export and save the token.

The auth state in the local session does not connect to the authorization state between a cloud account and Filestack. For example a user can log out of an account in a local session, but still see Filestack as authorized in the cloud provider's settings. A user must revoke access to Filestack within a provider to truly disconnect Filestack.

Users complete the OAuth flow (aka login to their clouds) in a browser, and the browser state can cause confusion. For example if a user logs into a cloud they previously logged out of, the OAuth flow may work differently because they've already logged in within the browser, and have already authorized Filestack to that cloud.

## Running Tests and Linting
To run tests:
```shell
./gradlew test
```

The project also has Checkstyle setup for code linting. The config is at `config/checkstyle/checkstyle.xml`. To run:
```shell
./gradlew check # Runs linter and unit tests
```

## Proguard
If you are using Proguard, please include entries from [this file](src/main/resources/META-INF/proguard/filestack.pro) in your Proguard configuration file.

Please note, that those rules will probably decrease in size as we proceed with getting rid of most of external dependencies.

## Deployment
_This is for Filestack devs._ Deployments are made to Bintray. You must have an account that's been added to the Filestack organization to deploy. Also make sure to follow general Filestack release guidelines. "BINTRAY_USER" and "BINTRAY_API_KEY" environment variables are required. To run:

```shell
export BINTRAY_USER=''
export BINTRAY_API_KEY=''
./gradlew bintrayUpload
```

[rxjava-repo]: https://github.com/ReactiveX/RxJava
