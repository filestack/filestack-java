package org.filestack.transforms.tasks;

import org.filestack.transforms.AvTransform;
import org.filestack.transforms.TransformTask;

/**
 * Configures conversion settings for {@link AvTransform}.
 */
public class AvTransformOptions extends TransformTask {

  AvTransformOptions() {
    super("video_convert");
  }

  public static class Builder {
    // For audio and video
    private Boolean force;
    private Integer audioBitrate;
    private Integer audioChannels;
    private Integer audioSampleRate;
    private Integer[] clipLength;
    private Integer[] clipOffset;
    private String ext;
    private String filename;
    private String preset;
    private String title;

    // For video only
    private Boolean twoPass;
    private Boolean upscale;
    private Integer fps;
    private Integer height;
    private Integer keyframeInt;
    private Integer videoBitrate;
    private Integer watermarkBottom;
    private Integer watermarkHeight;
    private Integer watermarkLeft;
    private Integer watermarkRight;
    private Integer watermarkTop;
    private Integer watermarkWidth;
    private Integer width;
    private String aspectMode;
    private String watermarkUrl;

    // For audio and video

    public Builder force(boolean force) {
      this.force = force;
      return this;
    }

    public Builder audioBitrate(int audioBitrate) {
      this.audioBitrate = audioBitrate;
      return this;
    }

    public Builder audioChannels(int audioChannels) {
      this.audioChannels = audioChannels;
      return this;
    }

    public Builder audioSampleRate(int audioSampleRate) {
      this.audioSampleRate = audioSampleRate;
      return this;
    }

    public Builder clipLength(int hour, int min, int sec) {
      this.clipLength = new Integer[]{hour, min, sec};
      return this;
    }

    public Builder clipOffset(int hour, int min, int sec) {
      this.clipOffset = new Integer[]{hour, min, sec};
      return this;
    }

    public Builder ext(String ext) {
      this.ext = ext;
      return this;
    }

    public Builder filename(String filename) {
      this.filename = filename;
      return this;
    }

    public Builder preset(String preset) {
      this.preset = preset;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    // For video only

    public Builder twoPass(boolean twoPass) {
      this.twoPass = twoPass;
      return this;
    }

    public Builder upscale(boolean upscale) {
      this.upscale = upscale;
      return this;
    }

    public Builder fps(int fps) {
      this.fps = fps;
      return this;
    }

    public Builder height(int height) {
      this.height = height;
      return this;
    }

    public Builder keyframeInt(int keyframeInt) {
      this.keyframeInt = keyframeInt;
      return this;
    }

    public Builder videoBitrate(int videoBitrate) {
      this.videoBitrate = videoBitrate;
      return this;
    }

    public Builder watermarkBottom(int watermarkBottom) {
      this.watermarkBottom = watermarkBottom;
      return this;
    }

    public Builder watermarkHeight(int watermarkHeight) {
      this.watermarkHeight = watermarkHeight;
      return this;
    }

    public Builder watermarkLeft(int watermarkLeft) {
      this.watermarkLeft = watermarkLeft;
      return this;
    }

    public Builder watermarkRight(int watermarkRight) {
      this.watermarkRight = watermarkRight;
      return this;
    }

    public Builder watermarkTop(int watermarkTop) {
      this.watermarkTop = watermarkTop;
      return this;
    }

    public Builder watermarkWidth(int watermarkWidth) {
      this.watermarkWidth = watermarkWidth;
      return this;
    }

    public Builder width(int width) {
      this.width = width;
      return this;
    }

    public Builder aspectMode(String aspectMode) {
      this.aspectMode = aspectMode;
      return this;
    }

    public Builder watermarkUrl(String watermarkUrl) {
      this.watermarkUrl = watermarkUrl;
      return this;
    }

    /**
     * Create the {@link AvTransformOptions} using the configured values.
     */
    public AvTransformOptions build() {
      AvTransformOptions options = new AvTransformOptions();

      // For audio and video
      options.addOption("force", force);
      options.addOption("audio_bitrate", audioBitrate);
      options.addOption("audio_channels", audioChannels);
      options.addOption("audio_sample_rate", audioSampleRate);
      if (clipLength != null) {
        options.addOption("clip_length", String.format("%02d:%02d:%02d",
            clipLength[0], clipLength[1], clipLength[2]));
      }
      if (clipOffset != null) {
        options.addOption("clip_offset", String.format("%02d:%02d:%02d",
            clipOffset[0], clipOffset[1], clipOffset[2]));
      }
      if (ext != null) {
        options.addOption("extname", ext.startsWith(".") ? ext : "." + ext);
      }
      options.addOption("filename", filename);
      options.addOption("preset", preset);
      options.addOption("title", title);

      // For video only
      options.addOption("two_pass", twoPass);
      options.addOption("upscale", upscale);
      options.addOption("fps", fps);
      options.addOption("height", height);
      options.addOption("keyframe_interval", keyframeInt);
      options.addOption("video_bitrate", videoBitrate);
      options.addOption("watermark_bottom", watermarkBottom);
      options.addOption("watermark_height", watermarkHeight);
      options.addOption("watermark_left", watermarkLeft);
      options.addOption("watermark_right", watermarkRight);
      options.addOption("watermark_top", watermarkTop);
      options.addOption("watermark_width", watermarkWidth);
      options.addOption("width", width);
      options.addOption("aspect_mode", aspectMode);
      options.addOption("watermark_url", watermarkUrl);

      return options;
    }
  }
}
