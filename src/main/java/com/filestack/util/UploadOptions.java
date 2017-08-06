package com.filestack.util;

import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Map;

public class UploadOptions {
    private HashMap<String, RequestBody> options = new HashMap<>();

    public Map<String, RequestBody> getMap() {
        return options;
    }

    public static class Builder {
        UploadOptions uploadOptions = new UploadOptions();
        
        public Builder location(String location) {
            uploadOptions.options.put("store_location", Util.createStringPart(location));
            return this;
        }

        public Builder region(String region) {
            uploadOptions.options.put("store_region", Util.createStringPart(region));
            return this;
        }

        public Builder container(String container) {
            uploadOptions.options.put("store_container", Util.createStringPart(container));
            return this;
        }

        public Builder path(String path) {
            uploadOptions.options.put("store_path", Util.createStringPart(path));
            return this;
        }

        public Builder access(String access) {
            uploadOptions.options.put("store_access", Util.createStringPart(access));
            return this;
        }

        public UploadOptions build() {
            if (!uploadOptions.options.containsKey("store_location"))
                uploadOptions.options.put("store_location", Util.createStringPart("s3"));
            return uploadOptions;
        }
    }
}
