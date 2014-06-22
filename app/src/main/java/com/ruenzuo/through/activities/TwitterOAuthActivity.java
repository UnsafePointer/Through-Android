package com.ruenzuo.through.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.ruenzuo.through.R;
import com.ruenzuo.through.application.ThroughApplication;

import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by renzocrisostomo on 20/06/14.
 */
public class TwitterOAuthActivity extends Activity {

    private Twitter twitter;
    private RequestToken requestToken;
    private WebView webView;
    public static final int RESULT_TWITTER_OAUTH_ERROR = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_oauth_activity_layout);
        ((ThroughApplication) getApplication()).getTracker(ThroughApplication.TrackerName.APP_TRACKER);
        webView = (WebView) findViewById(R.id.wbViewOAuth);
        WebViewClient webViewClient = new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Uri uri = Uri.parse(url);
                if (uri.getScheme().equalsIgnoreCase("oauth")) {
                    webView.stopLoading();
                    final String verifier = uri.getQueryParameter("oauth_verifier");
                    final ProgressDialog progressDialog = new ProgressDialog(TwitterOAuthActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    progressDialog.setMessage("Finishing authorization");
                    Task.callInBackground(new Callable<AccessToken>() {
                        @Override
                        public AccessToken call() throws Exception {
                            return twitter.getOAuthAccessToken(requestToken, verifier);
                        }
                    }).continueWith(new Continuation<AccessToken, Void>() {
                        @Override
                        public Void then(Task<AccessToken> task) throws Exception {
                            progressDialog.dismiss();
                            if (task.isFaulted()) {
                                setResult(RESULT_TWITTER_OAUTH_ERROR);
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra("AccessToken", task.getResult());
                                setResult(RESULT_OK, intent);
                            }
                            finish();
                            return null;
                        }
                    });
                }
            }

        };
        webView.setWebViewClient(webViewClient);
        connectTwitter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    private void connectTwitter() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setMessage("Getting authorization");
        Task.callInBackground(new Callable<RequestToken>() {
            @Override
            public RequestToken call() throws Exception {
                ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
                configurationBuilder.setOAuthConsumerKey("K7G3qi30RXciGattuJ1cBdoNG");
                configurationBuilder.setOAuthConsumerSecret("kNyT6OtLzNYB9uspygHnhWx11nXXs0oDDf9rD5E2RkzlW6EFPo");
                Configuration configuration = configurationBuilder.build();
                twitter = new TwitterFactory(configuration).getInstance();
                requestToken = twitter.getOAuthRequestToken("oauth://through");
                return requestToken;
            }
        }).continueWith(new Continuation<RequestToken, Void>() {
            @Override
            public Void then(Task<RequestToken> task) throws Exception {
                progressDialog.dismiss();
                if (task.isFaulted()) {
                    setResult(RESULT_TWITTER_OAUTH_ERROR);
                    finish();
                } else {
                    webView.loadUrl(task.getResult().getAuthenticationURL());
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

}
