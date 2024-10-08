Change Log
==========
Version 1.0.1 *(2024-10-11)*
----------------------------
  * Added support JDK 21
  * Updated build.gradle dependencies as per the latest Java changes.
  * Added mavencentral and replaced with JCenter(Deprecated) in build.gradle.
  * Updated existing JUnit test cases to be compatible with the latest JDK.
  * Updated existing kotlin test cases to be compatible with the latest changes.
  * Added new plugins and dependencies in build.gradle file.

Version 0.9.0 *(2018-09-27)*
----------------------------
  * New: Use `Client#fileLink(String)` to obtain `FileLink` instances from handle.
  * Change: The SDK no longer depends on Retrofit library. Proguard rules have been updated to include this change.
  * Fix: Support for TLS 1.2 on old Android devices has been enabled. This should not interfere with newer devices. If you encounter any kind of network issues, please report it.

Version 0.8.2 *(2018-09-13)*
----------------------------
  * New: Filestack Java SDK now uses `@Nullable` to annotate possibly-null parameters. We use `@ParametersAreNonnullByDefault` annotation to treat all other parameters as non-null.
  * Change: The SDK no longer depends on Guava library. Proguard rules have been updated to include this change.
  * Fix: Improved Proguard rules for Policy class.
  * Fix: Removed Kotlin dependency from main jar archive

Version 0.8.1 *(2018-09-10)*
----------------------------

  * New: Proguard rules are now included inside of META-INF dir for R8 users.
  * New: More convenient `Config` constructors that require `Policy` object.
  * **Deprecation:** `Config` constructors requiring `returnUrl` field are now deprecated. If you are relying on `CloudServiceUtil` class, use new `buildBaseJson` method that accepts `returnUrl` as a param.
  * **Deprecation:** `FileLink` constructor is deprecated and scheduled to be removed in future releases. Rely on `Client` class to acquire `FileLink` instances.
  * Change: prevent access to some internal classes

Version 0.8.0 *(2018-06-07)*
----------------------------

  * fix: improve progress events and upload rate calculation

Version 0.7.0 *(2018-05-21)*
----------------------------

  * fix: storage options ignored for cloud API, malformed request body

Version 0.6.0 *(2017-01-18)*
----------------------------

  * fix: s3 signature match error, now sending all headers from uploads api
  * feat: client supports uploading InputStream objects
  * fix: uploads will use a default file name and mime type if none is provided

Version 0.5.0 *(2017-11-16)*
----------------------------

  * No major changes
  * Change: CloudItem implements Serializable

Version 0.4.0 *(2017-11-01)*
----------------------------

  * FS-2024 and FS-2128 Refactoring for Android
    * Change: Major changes to constructors for all public classes.
    * Change: All objects now use static networking objects, can no longer customize.
    * Change: Security is no longer a class, just policy and signature strings.
    * Change: Config object stores common values for Client, FileLink and transform classes.
  * FS-1808, FS-1809, and FS-1811 Add cloud integrations
    * New: Cloud integrations. Use the client to view and store items from cloud providers.
    * Change: Error handling. Dropped more specific classes for basic HttpException and IOException.

Version 0.3.0 *(2017-09-12)*
----------------------------

 * FS-1674 Add upload progress
    * New: `Progress` class to return stats on an in-progress upload/download.
    * Change: `FilestackClient.uploadAsync()` now returns a `Flowable` that emits a stream of
      `Progress` objects. Monitor and act on an upload's ongoing progress by subscribing to the
      `Flowable` with a `Consumer` or `FlowableSubscriber`.

Version 0.2.0 *(2017-09-05)*
----------------------------

 * FS-1554 Add integration tests
    * Change: Must pass content type in to `FilestackClient.upload()`, no longer guesses based on
      extension or file content.
 * FS-1086 Add audio/video conversions
    * Change: Replace `StoreOptions` and `UploadOptions` with generalized `StorageOptions`.
    * Fix: Don't remove all spaces from `Transform` task string.
    * New: `AvTransform` and `AvTransformOptions` for audio/video conversions.
    * New: Add `FileLink.avTransform()` to get an `AvTransform` for the file.
 * FS-1137 Add image tagging and SFW check
    * New: Add `FileLink.imageTags()` to return a `Map<String, Integer>` of possible content tags.
    * New: Add `FileLink.imageSfw()` to check if an image file is "safe for work".
    * New: Add `Transform.getContent()` and `Transform.getContentJson()` to get transform content
      without having to save the file. The former returns a raw `okhttp3.ResponseBody` and the
      latter returns a `gson.JsonObject` for convenience.
    * Change: `FileLink.getContent()` returns an `okhttp3.ResponseBody` instead of a `byte[]`.   
 * FS-1087 Add file type conversions
    * New: `FileTypeTask` for `ImageTransform` to convert between different image file types.
    * New: `NoMetadataOption` for `ImageTransform` to strip metadata from an image.
 * FS-1330 Cleanup units tests
 * FS-1099 Add Coveralls config

Version 0.1.0 *(2017-08-22)*
----------------------------

Initial release.
