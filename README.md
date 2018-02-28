[![Bintray][bintray_badge]][bintray]
[![Travis][travis_badge]][travis]
[![Coveralls][coveralls_badge]][coveralls]

# Filestack Java SDK
Official Java SDK for the Filestack service. API reference is available [here][javadoc].

## Installing
```
compile 'com.filestack:filestack-java:0.6.0'
```

## Uploading
```java
Config config = new Config("API_KEY");
Client client = new Client(config);

// Storage options are "optional" BUT we don't guess MIME types
StorageOptions options = new StorageOptions.Builder()
    .mimeType("text/plain")
    .filename("hello.txt")
    .build();

// Synchronous, blocking upload
FileLink file = client.upload("/path/to/file", false);

// Asynchronous, not blocking upload
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

## FileLink Operations
Any method that makes a network call has both a synchrnous (blocking) and asynchronous (non-blocking) version.

```
// A handle is a file's ID in Filestack
String handle = fileLink.getHandle();
fileLink.download("/path/to/save/file");
fileLink.delete();
fileLink.overwrite("/path/to/new/file");
```

## Transformations
We have several wrappers to our backend transformations. These are not performed locally.

```
ImageTransformTask sepia = new SepiaTask();
ImageTransformTask crop = new CropTask(0, 0, 300, 300);

// Transform operations can be chained
ImageTransform transform = fileLink
    .imageTransform()
    .addTask(crop)
    .addTask(sepia);

// You can directly get the resulting file or generate a URL to it
String url = transform.url();
ResponseBody content = transform.getContent();
```

[bintray]: https://bintray.com/filestack/maven/filestack-java/
[bintray_badge]: https://img.shields.io/bintray/v/filestack/maven/filestack-java.svg?style=flat-square
[coveralls]: https://coveralls.io/github/filestack/filestack-java
[coveralls_badge]: https://img.shields.io/coveralls/filestack/filestack-java.svg?style=flat-square
[javadoc]: https://filestack.github.io/filestack-java
[travis]: https://travis-ci.org/filestack/filestack-java
[travis_badge]: https://img.shields.io/travis/filestack/filestack-java.svg?style=flat-square
