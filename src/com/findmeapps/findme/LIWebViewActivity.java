package com.findmeapps.findme;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.findmeapps.findme.service.LinkedInService;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 29/10/12
 * Time: 22:26
 * To change this template use File | Settings | File Templates.
 */
public class LIWebViewActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.linkedinwebview);
        final WebView webView = (WebView) findViewById(R.id.linkedinwebview);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                setProgress(newProgress * 100);
            }

        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);

                if (url.startsWith(LinkedInService.CALLBACK_URL)) {
                    webView.setVisibility(WebView.GONE);

                    final String url1 = url;
                    Thread t1 = new Thread() {
                        public void run() {
                            Uri uri = Uri.parse(url1);

                            String verifier = uri.getQueryParameter(LinkedInService.OAUTH_VERIFIER_PARAM);
                            Intent intent = new Intent();
                            intent.putExtra(LinkedInService.OAUTH_VERIFIER_PARAM, verifier);
                            setResult(RESULT_OK, intent);

                            finish();
                        }
                    };
                    t1.start();
                }

                return false;
            }
        });


        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setAppCacheEnabled(true); // the important change
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(this.getIntent().getExtras().getString(LinkedInService.KEY_AUTHORIZATION_URL));

    }
}
