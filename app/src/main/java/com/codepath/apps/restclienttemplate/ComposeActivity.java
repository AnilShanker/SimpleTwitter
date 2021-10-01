package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;

    EditText etCompose;
    Button btnTweet;
    TextView tvCharacters;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCharacters = findViewById(R.id.tvCharacters);
        client = TwitterApp.getRestClient(this);
        tvCharacters.setText("0/" + MAX_TWEET_LENGTH);



        // Adds a listener on EditText
        etCompose.addTextChangedListener(new TextWatcher() {
            // Initializes variable to track length of user's tweet in editText
            int tweetLength;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tweetLength = s.length();
                Log.i(TAG, "The length of the tweet is: " + tweetLength);
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCharacters.setText(tweetLength + "/" + MAX_TWEET_LENGTH);
                if (tweetLength > MAX_TWEET_LENGTH) {
                    Log.i(TAG, "The length of the tweet exceeds the max number of characters");
                    tvCharacters.setTextColor(Color.RED);
                } else {
                    tvCharacters.setTextColor(Color.BLACK);
                }
            }
        });

        // Add a click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot exceed 140 characters", Toast.LENGTH_SHORT).show();
                }
                    // Valid tweet, make call
                    client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess to publish tweet");
                            try {
                                Tweet tweet = Tweet.fromJson(json.jsonObject);
                                Log.i(TAG, "Published tweet says: " + tweet.body);
                                Intent i = new Intent();
                                i.putExtra("tweet", Parcels.wrap(tweet));
                                setResult(RESULT_OK, i);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure to publish tweet", throwable);
                        }
                    });
                }

                // Make an API call to Twitter to publish tweet


        });

    }
}