package com.example.pritertestappchritopher;

import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class BaseActivity extends AppCompatActivity {



    public Bitmap getBitMapFromHex(String hex) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = multiFormatWriter.encode(hex, BarcodeFormat.QR_CODE, 600, 600);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
        return  bitmap;

    }
    public String excatFigure(double value) {
        BigDecimal d = new BigDecimal(String.valueOf(value));

        return  d.toPlainString();
    }
    private String getCentralStandardTime(long paid_at) {
        return String.valueOf(paid_at);
    }
    public String getDateFromUTCTimestamp(long mTimestamp, String mDateFormate) {
        String date = null;
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("CST"));
            cal.setTimeInMillis(mTimestamp * 1000L);
            date = DateFormat.format(mDateFormate, cal.getTimeInMillis()).toString();

            SimpleDateFormat formatter = new SimpleDateFormat(mDateFormate);
            formatter.setTimeZone(TimeZone.getTimeZone("CST"));
            Date value = formatter.parse(date);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(mDateFormate);
            dateFormatter.setTimeZone(TimeZone.getDefault());
            date = dateFormatter.format(value);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    public  String getUnixTimeStamp() {
        Long tsLong = System.currentTimeMillis()/1000;
        String uNixtimeStamp=tsLong.toString();
        return  uNixtimeStamp;
    }
    public double mSatoshoToBtc(double msatoshhi) {
        double msatoshiToSatoshi=msatoshhi/AppConstants.satoshiToMSathosi;
        double satoshiToBtc=msatoshiToSatoshi/AppConstants.btcToSathosi;
        return satoshiToBtc;
    }
    public  double getUsdFromBtc(double btc) {
        double ret=btc*16700;
        return  ret;
    }
}
