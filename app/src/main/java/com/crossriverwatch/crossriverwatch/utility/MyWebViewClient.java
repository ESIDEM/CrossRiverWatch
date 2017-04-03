package com.crossriverwatch.crossriverwatch.utility;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by ESIDEM jnr on 4/3/2017.
 */

public class MyWebViewClient extends WebViewClient {

    private String myUrl;

    public MyWebViewClient(String myUrl) {
        this.myUrl = myUrl;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if(url.contains("logout") || url.contains("login-success")){
            Log.d("--- URL login-success: ", url);
            Log.d("--- URL indexof: ", String.valueOf(url.indexOf("login-success")));

            view.loadUrl(myUrl);
        }
        if(url.contains("disqus.com/_ax/twitter/complete") ||
                url.contains("disqus.com/_ax/facebook/complete") ||
                url.contains("disqus.com/_ax/google/complete")){
            view.loadUrl(myUrl);
        }
    }
}
