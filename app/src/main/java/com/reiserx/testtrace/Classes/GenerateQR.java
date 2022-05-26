package com.reiserx.testtrace.Classes;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

public class GenerateQR {

    public Bitmap imageBitmap;

    public Bitmap createQR(String data) {
        try {
            Hashtable<EncodeHintType, Object> hintMap = new Hashtable<> ();
            hintMap.put (EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hintMap.put(EncodeHintType.MARGIN, 1);

            Writer codeWriter;
                codeWriter = new QRCodeWriter();

            BitMatrix byteMatrix = codeWriter.encode (
                    data,
                    BarcodeFormat.QR_CODE,
                    500,
                    500,
                    hintMap
            );

            int width   = byteMatrix.getWidth ();
            int height  = byteMatrix.getHeight ();

            imageBitmap = Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < width; i ++) {
                for (int j = 0; j < height; j ++) {
                    imageBitmap.setPixel (i, j, byteMatrix.get (i, j) ? Color.BLACK: Color.WHITE);
                }
            }
            return imageBitmap;

        } catch (WriterException e) {
            e.printStackTrace ();
            return null;
        }
    }
    public void releaseBitmap() {
        if (imageBitmap != null) {
            imageBitmap.recycle();
            imageBitmap = null;
        }
    }
}
