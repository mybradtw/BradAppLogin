package tw.brad.bradapplogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();


        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                Log.v("brad", "onTokenChanged");
                if (currentAccessToken != null) {
                    Set<String> permissions = currentAccessToken.getPermissions();
                    for (String permission : permissions) {
                        Log.v("brad", permission);
                    }
                }
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                if (currentProfile != null) {
                    String name = currentProfile.getName();
                    Log.v("brad", name);
                }
            }
        };



        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("user_status","user_birthday"));

        // Callback registration
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.v("brad", "onSuccess()");

                        accessToken =  loginResult.getAccessToken();
                        String id = accessToken.getUserId();
                        String token = accessToken.getToken();
                        Log.v("brad", id + ":" + token);


                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code
                                        try {
                                            String email = object.getString("email");
                                            String birthday = object.getString("birthday"); // 01/31/1980 format
                                            String id = object.getString("id"); // 01/31/1980 format
                                            Log.v("brad", id + ":"+email + ":" + birthday);
                                        }catch (Exception e){
                                            Log.v("brad", e.toString());
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();



                    }

                @Override
                public void onCancel() {
                    // App code
                    Log.v("brad", "onCancel()");
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Log.v("brad", "onError()");
                }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
