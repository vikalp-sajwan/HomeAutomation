package com.example.vikalpsajwan.homeautomation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.VideoView;

/**
 * Created by Vikalp on 25/05/2017.
 */

public class VideoActivity extends AppCompatActivity {

    WebView webView;
    VideoView videoView;
    SharedPreferences ipPref;
    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        setContentView(R.layout.activity_video);
                WebView webView = (WebView) findViewById(R.id.webView);



//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("http://192.168.43.9:9090");
//        webView.setHorizontalScrollBarEnabled(false);


        ipPref = getApplicationContext().getSharedPreferences("IPPref", MODE_PRIVATE);
        ip = ipPref.getString("ip", "");



//
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//
        String data = "<html><head>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, minimum-scale=0.1\">\n" +
                "  </head>\n" +
                "  <body style=\"margin-top: 45%; ;  background: #0e0e0e;\">\n" +
                "    <img style=\"-webkit-user-select: none; image-orientation: from-image; \" width=\"100%\" \" height=\"40%\"src=\"http://" +
                ip +
                ":9090/\" width=\"640\" height=\"480\">\n" +
                "  \n" +
                "</body></html>";
        String data2 = "<html><body><p>hello</p></body></html>";
        webView.loadData(data, "text/html", "UTF-16");
//



        super.onCreate(savedInstanceState);
    }


}
