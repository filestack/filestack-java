package com.filestack.util;

import com.filestack.responses.CompleteResponse;
import com.filestack.responses.StartResponse;
import com.filestack.responses.StoreResponse;
import com.filestack.responses.UploadResponse;
import com.google.gson.JsonObject;
import java.util.Map;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/** Combines all REST services into a single service. */
public class FsService implements FsApiService, FsCdnService, FsUploadService {
  private FsApiService customApiService;
  private FsCdnService customCdnService;
  private FsUploadService customUploadService;

  /** Constructs instance using singleton REST services. */
  public FsService() { }

  /** Constructs instance using custom REST services. */
  public FsService(FsApiService api, FsCdnService cdn, FsUploadService upload) {
    this.customApiService = api;
    this.customCdnService = cdn;
    this.customUploadService = upload;
  }
  
  private FsApiService getApiService() {
    if (customApiService != null) {
      return customApiService;
    } else {
      return Networking.getFsApiService();
    }
  }

  private FsCdnService getCdnService() {
    if (customCdnService != null) {
      return customCdnService;
    } else {
      return Networking.getFsCdnService();
    }
  }

  private FsUploadService getUploadService() {
    if (customUploadService != null) {
      return customUploadService;
    } else {
      return Networking.getFsUploadService();
    }
  }

  @Override
  public Call<ResponseBody> overwrite(String handle, String policy, String signature,
                                      RequestBody body) {
    return getApiService().overwrite(handle, policy, signature, body);
  }

  @Override
  public Call<ResponseBody> delete(String handle, String key, String policy, String signature) {
    return getApiService().delete(handle, key, policy, signature);
  }

  @Override
  public Call<ResponseBody> get(String handle, String policy, String signature) {
    return getCdnService().get(handle, policy, signature);
  }

  @Override
  public Call<ResponseBody> transform(String tasks, String handle) {
    return getCdnService().transform(tasks, handle);
  }

  @Override
  public Call<JsonObject> transformDebug(String tasks, String handle) {
    return getCdnService().transformDebug(tasks, handle);
  }

  @Override
  public Call<StoreResponse> transformStore(String tasks, String handle) {
    return getCdnService().transformStore(tasks, handle);
  }

  @Override
  public Call<ResponseBody> transformExt(String key, String tasks, String url) {
    return getCdnService().transformExt(key, tasks, url);
  }

  @Override
  public Call<JsonObject> transformDebugExt(String key, String tasks, String url) {
    return getCdnService().transformDebugExt(key, tasks, url);
  }

  @Override
  public Call<StoreResponse> transformStoreExt(String key, String tasks, String url) {
    return getCdnService().transformStoreExt(key, tasks, url);
  }

  @Override
  public Call<StartResponse> start(Map<String, RequestBody> parameters) {
    return getUploadService().start(parameters);
  }

  @Override
  public Call<UploadResponse> upload(Map<String, RequestBody> parameters) {
    return getUploadService().upload(parameters);
  }

  @Override
  public Call<ResponseBody> uploadS3(Map<String, String> headers, String url, RequestBody body) {
    return getUploadService().uploadS3(headers, url, body);
  }

  @Override
  public Call<ResponseBody> commit(Map<String, RequestBody> parameters) {
    return getUploadService().commit(parameters);
  }

  @Override
  public Call<CompleteResponse> complete(Map<String, RequestBody> parameters) {
    return getUploadService().complete(parameters);
  }
}
