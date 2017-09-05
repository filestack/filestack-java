Change Log
==========

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
