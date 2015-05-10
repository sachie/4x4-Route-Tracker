package team4x4.trackers.routetracker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import team4x4.trackers.routetracker.R;
import team4x4.trackers.routetracker.utilities.Constants;
import team4x4.trackers.routetracker.utilities.SharedPreferencesUtility;

/**
 * Activity for the login screen.
 */
@EActivity(R.layout.activity_login)
public class Login extends AppCompatActivity {

    /**
     * Login button.
     */
    @ViewById(R.id.login_button)
    protected Button mLoginButton;

    /**
     * Called on login button click.
     */
    @Click(R.id.login_button)
    void loginButtonClick() {
        new SharedPreferencesUtility(this).saveBoolean(Constants.ACTIVATED, true);
        startActivity(new Intent(this, Routes_.class));
        finish();
    }
}
