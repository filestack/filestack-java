package org.filestack.transforms.tasks;

import org.junit.Assert;
import org.junit.Test;

public class TestAvTransformOptions {

  @Test
  public void testToStringAll() {
    String correct = "video_convert="

        // For audio and video
        + "force:true,"
        + "audio_bitrate:256,"
        + "audio_channels:2,"
        + "audio_sample_rate:44100,"
        + "clip_length:00:01:00,"
        + "clip_offset:00:00:30,"
        + "extname:.mp4,"
        + "filename:some_filename,"
        + "preset:h264,"
        + "title:Some Title,"

        // For video only
        + "two_pass:true,"
        + "upscale:false,"
        + "fps:24,"
        + "height:720,"
        + "keyframe_interval:250,"
        + "video_bitrate:4096,"
        + "watermark_bottom:50,"
        + "watermark_height:300,"
        + "watermark_left:50,"
        + "watermark_right:50,"
        + "watermark_top:50,"
        + "watermark_width:300,"
        + "width:1080,"
        + "aspect_mode:pad,"
        + "watermark_url:https://example.com/image.png";

    AvTransformOptions options = new AvTransformOptions.Builder()
        .preset("h264")
        .force(true)
        .width(1080)
        .height(720)
        .title("Some Title")
        .ext("mp4")
        .filename("some_filename")
        .upscale(false)
        .aspectMode("pad")
        .twoPass(true)
        .videoBitrate(4096)
        .fps(24)
        .keyframeInt(250)
        .audioBitrate(256)
        .audioSampleRate(44100)
        .audioChannels(2)
        .clipLength(0, 1, 0)
        .clipOffset(0, 0, 30)
        .watermarkUrl("https://example.com/image.png")
        .watermarkTop(50)
        .watermarkBottom(50)
        .watermarkLeft(50)
        .watermarkRight(50)
        .watermarkWidth(300)
        .watermarkHeight(300)
        .build();

    Assert.assertEquals(correct, options.toString());
  }

  @Test
  public void testToStringNone() {
    String correct = "video_convert";
    AvTransformOptions options = new AvTransformOptions.Builder().build();
    Assert.assertEquals(correct, options.toString());
  }

  @Test
  public void testToStringExt() {
    String correct = "video_convert=extname:.mp4";
    AvTransformOptions options = new AvTransformOptions.Builder().ext(".mp4").build();
    Assert.assertEquals(correct, options.toString());
    options = new AvTransformOptions.Builder().ext("mp4").build();
    Assert.assertEquals(correct, options.toString());
  }
}
