package com.filestack.transforms.tasks;

import com.filestack.transforms.TransformTask;
import org.junit.Assert;
import org.junit.Test;

public class TestFileTypeConversionTask {

  @Test
  public void testToString() {
    String correct = "output="
        + "format:png,"
        + "background:white,"
        + "page:4,"
        + "density:50,"
        + "compress:true,"
        + "quality:80,"
        + "strip:true,"
        + "colorspace:RGB,"
        + "secure:true,"
        + "docinfo:true,"
        + "pageformat:legal,"
        + "pageorientation:landscape";

    TransformTask task = new FileTypeConversionTask.Builder()
        .format("png")
        .background("white")
        .page(4)
        .density(50)
        .compress(true)
        .quality(80)
        .strip(true)
        .colorSpace("RGB")
        .secure(true)
        .docInfo(true)
        .pageFormat("legal")
        .pageOrientation("landscape")
        .build();

    String output = task.toString();

    Assert.assertEquals(correct, output);
  }

  @Test
  public void testToStringQualityInput() {
    String correct = "output="
        + "quality:input";

    TransformTask task = new FileTypeConversionTask.Builder()
        .quality("input")
        .build();

    String output = task.toString();

    Assert.assertEquals(correct, output);
  }
}
