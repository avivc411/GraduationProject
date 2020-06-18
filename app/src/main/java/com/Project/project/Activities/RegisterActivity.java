package com.Project.project.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.Handlers.RegistrationHandler;
import com.Project.project.R;
import com.Project.project.UserManagment.AWSCognitoUserManager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.Project.project.Utilities.PreventTwoClick.preventTwoClick;

public class RegisterActivity extends AppCompatActivity {
    TextView name, password, email, register;
    Button registerButton;
    EditText userPasswordRegister, userEmailRegister, userNameRegister, nameE, passwordE, emailE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        connectButtonToXml();
        // register button calls to register function
        registerButton.setOnClickListener(view -> {
            preventTwoClick(view);
            String userName = userNameRegister.getText().toString();
            String userPassword = userPasswordRegister.getText().toString();
            String email = userEmailRegister.getText().toString();
            checkValidity(userName, userPassword, email);
        });
        runAnimation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        connectButtonToXml();
        runAnimation();
    }

    /**
     * run animation of class
     */
    private void runAnimation() {
        Animation animationbot = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.bottotop);
        Animation animationleft = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.lefttoright);
        Animation animationright = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.righttoleft);
        Animation animationtop = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.toptodown);
        name.startAnimation(animationleft);
        nameE.startAnimation(animationright);
        password.startAnimation(animationleft);
        passwordE.startAnimation(animationright);
        email.startAnimation(animationleft);
        emailE.startAnimation(animationright);
        register.startAnimation(animationtop);
        registerButton.startAnimation(animationbot);

    }

    /**
     * connect class and xml objects
     */
    private void connectButtonToXml() {
        name = findViewById(R.id.name_register);
        nameE = findViewById(R.id.userNameRegister);
        password = findViewById(R.id.password_register);
        passwordE = findViewById(R.id.userPasswordRegister);
        email = findViewById(R.id.email_register);
        emailE = findViewById(R.id.userEmailRegister);
        register = findViewById(R.id.register_title);
        registerButton = findViewById(R.id.registerButton);
        userNameRegister = findViewById(R.id.userNameRegister);
        userPasswordRegister = findViewById(R.id.userPasswordRegister);
        userEmailRegister = findViewById(R.id.userEmailRegister);
        registerButton = findViewById(R.id.registerButton);
    }

    // validate userName is not taken, create the user, move to homescreen

    /**
     * Check given details validity, continue or stop the registration process accordingly.
     *
     * @param userName     Chosen username.
     * @param userPassword Chosen password.
     * @param email        User's email.
     */
    private void checkValidity(String userName, String userPassword, String email) {
        // check if there are empty fields
        boolean emptyField = ((userName.equals("")) || (userPassword.equals("")) || (email.equals("")));
        if (emptyField) {
            Toast.makeText(this, "Some of the required fields are missing", Toast.LENGTH_SHORT).show();
            return;
        }
        String regex = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            Toast.makeText(this, "Email address illegal", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setMessage("Logging in, Please wait a few seconds");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        registerInServer(userName, userPassword, email);
        dialog.dismiss();
    }

    private void registerInServer(String userName, String userPassword, String email) {
        Map<String, String> defaultAttributes = new HashMap<>();
        defaultAttributes.put("custom:Camera", "1");
        defaultAttributes.put("custom:GPS", "1");
        defaultAttributes.put("custom:Usage", "1");
        defaultAttributes.put("custom:firstLogin", "1");
        AWSCognitoUserManager.getInstance(this).register(userName, userPassword, email, defaultAttributes, new RegistrationHandler(this));
    }
}