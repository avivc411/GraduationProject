package com.Project.project.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.Project.project.Handlers.ConfirmationHandler;
import com.Project.project.Handlers.ForgotPasswordRequestHandler;
import com.Project.project.Handlers.LoginHandler;
import com.Project.project.R;
import com.Project.project.UserManagment.AWSCognitoUserManager;

import static com.Project.project.Utilities.PreventTwoClick.preventTwoClick;

/**
 * // TODO: 07/04/2020 when doing restore pass, if none extisisting user, still sends email - aviv
 * TODO: usage permission request - special request in dialog
 * TODO: Parse.enableLocalDatastore(this) - creates fatal error when return to this activity - almost done, need to fix next TODO
 * TODO: return button on home page creates new activity of Main - remove button or fix behaviour
 * TODO: remove unnecessary Parse usages.
 * TODO: show login+registration animation screen - auto connect and login
 */
public class MainActivity extends AppCompatActivity {
    private ForgotPasswordRequestHandler forgotPassHandler;
    private AWSCognitoUserManager userManager;
    private ImageView smiles;
    private EditText usernameEditText, passwordEditText, code, code_login;
    private TextView username_label, code_label, password_label, title, forgot_password, code_label_login;
    private Button login_button, register_button, sendCode, confirmNewPass, login_button_firstlogin;
    private CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userManager = AWSCognitoUserManager.getInstance(this);
        autoConnect();

        connectButtonToXml();
        runAnimation();

        //condition - in case of second call by mistake
        if (savedInstanceState == null)
            checkVersion(); // if version api > 23 need to get permission during app run
        switchView("reg");
    }

    /**
     * Connect user automatically if possible.
     */
    private void autoConnect() {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Logging in, Please wait a few seconds");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        Intent homePageIntent = new Intent(this, HomePageActivity.class);
        homePageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homePageIntent.putExtra("Username", "");
        Intent settingsIntent = new Intent(this, SettingActivity.class);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        settingsIntent.putExtra("Username", "");

        if(!userManager.autoConnect(this, new LoginHandler(this, userManager, homePageIntent, settingsIntent,
                dialog, "", "", true)))
            dialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectButtonToXml();
        runAnimation();
        userManager.logout(this);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        username.setText("");
        password.setText("");
    }

    /**
     * check which android version is running and request permission if needed
     */
    private void checkVersion() {
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            if version is newer then 23 and permission missing
            if (!checkIfAlreadyHavePermission()) {
                requestForSpecificPermission();
            }
        }
    }

    /**
     * run animation on main page
     */
    private void runAnimation() {
        Animation animationLeft = AnimationUtils.loadAnimation(MainActivity.this, R.anim.lefttoright);
        Animation animationRight = AnimationUtils.loadAnimation(MainActivity.this, R.anim.righttoleft);
        Animation animationTop = AnimationUtils.loadAnimation(MainActivity.this, R.anim.toptodown);
        login_button.startAnimation(animationLeft);
        register_button.startAnimation(animationRight);
        usernameEditText.startAnimation(animationRight);
        passwordEditText.startAnimation(animationRight);
        smiles.startAnimation(animationTop);
        username_label.startAnimation(animationLeft);
        password_label.startAnimation(animationLeft);
        title.startAnimation(animationTop);
        rememberMeCheckBox.startAnimation(animationLeft);
    }

    /**
     * connect xml to class
     */
    private void connectButtonToXml() {
        login_button = findViewById(R.id.login_button);
        register_button = findViewById(R.id.registerBtn);
        username_label = findViewById(R.id.username_label);
        password_label = findViewById(R.id.password_label);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        smiles = findViewById(R.id.smiles);
        title = findViewById(R.id.title);
        forgot_password = findViewById(R.id.forgot_pass);
        sendCode = findViewById(R.id.sendCode);
        confirmNewPass = findViewById(R.id.confirmNewPass);
        code_label = findViewById(R.id.code_label);
        code = findViewById(R.id.code);
        code_label_login = findViewById(R.id.code_label_login);
        code_login = findViewById(R.id.code_login);
        login_button_firstlogin = findViewById(R.id.login_button_firstlogin);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
    }

    /**
     * which permissions needed, add to string array
     */
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, 101);
    }

    /**
     * check if permission exists
     *
     * @return true if permission exist
     */
    private boolean checkIfAlreadyHavePermission() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = info.requestedPermissions;
            if (requestedPermissions != null) {
                for (String requestedPermission : requestedPermissions) {
                    System.out.println("Permission: " + requestedPermission + " is " +
                            (packageManager.checkPermission(requestedPermission, getPackageName()) == PackageManager.PERMISSION_GRANTED ? "Granted" : "Not Granted"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Login user to the system.
     *
     * @param view Current view.
     */
    public void login(View view) {
        preventTwoClick(view);
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Logging in, Please wait a few seconds");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Intent homePageIntent = new Intent(this, HomePageActivity.class);
        homePageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homePageIntent.putExtra("Username", username);
        Intent settingsIntent = new Intent(this, SettingActivity.class);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        settingsIntent.putExtra("Username", username);
        userManager.login(username, password, new LoginHandler(this, userManager, homePageIntent, settingsIntent,
                dialog, username, password, rememberMeCheckBox.isChecked()));
    }

    /**
     * Show forgot password input user name.
     *
     * @param view Current view.
     */
    public void forgotPasswordPage(View view) {
        preventTwoClick(view);
        switchView("sendCode");
    }

    /**
     * Start new password setup process - send verification code to user's email.
     *
     * @param view Current view.
     */
    public void continueForgotPasswordProcess(View view) {
        preventTwoClick(view);
        String user = usernameEditText.getText().toString();
        if ("".equals(user)) {
            Toast.makeText(this, "Username Cannot Be Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        forgotPassHandler = new ForgotPasswordRequestHandler(this);
        userManager.forgotPassword(user, forgotPassHandler);
    }

    /**
     * Register user to the system.
     *
     * @param view Current view.
     */
    public void register(View view) {
        preventTwoClick(view);
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Continue new password setup process.
     * Get new password and verification code from email.
     */
    public void setNewPassword(View view) {
        preventTwoClick(view);
        //get new password and verification code from user
        String verificationCode = code.getText().toString();
        String newPassword = passwordEditText.getText().toString();
        System.out.println("continueProcess verificationCode: " + verificationCode);
        System.out.println("continueProcess newPassword: " + newPassword);

        if (newPassword == null || verificationCode == null || "".equals(verificationCode) || "".equals(newPassword))
            return;
        forgotPassHandler.confirmPassword(verificationCode, newPassword);
    }

    public void loginPage(View view) {
        preventTwoClick(view);
        switchView("login");
    }

    /**
     * Switch views for forgot password
     */
    public void switchView(String expression) {
        switch (expression) {
            case "sendCode":
                sendCode();
                break;
            case "enterCodeNewPass":
                newPass();
                break;
            case "firstLogin":
                showFirstLogin();
                break;
            default:
                showLogin();
        }
    }

    /**
     * send code view
     */
    private void sendCode() {
        sendCode.setVisibility(View.VISIBLE);
        usernameEditText.setVisibility(View.VISIBLE);
        username_label.setVisibility(View.VISIBLE);

        animationSendCode();

        rememberMeCheckBox.setVisibility(View.INVISIBLE);
        login_button.setVisibility(View.INVISIBLE);
        register_button.setVisibility(View.INVISIBLE);
        forgot_password.setVisibility(View.INVISIBLE);
        confirmNewPass.setVisibility(View.INVISIBLE);
        passwordEditText.setVisibility(View.INVISIBLE);
        password_label.setVisibility(View.INVISIBLE);
        code.setVisibility(View.INVISIBLE);
        code_label.setVisibility(View.INVISIBLE);
    }

    /**
     * animation of send code
     */
    private void animationSendCode() {
        Animation animationleft = AnimationUtils.loadAnimation(MainActivity.this, R.anim.lefttoright);
        Animation animationright = AnimationUtils.loadAnimation(MainActivity.this, R.anim.righttoleft);
        Animation animationbot = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottotop);
        sendCode.startAnimation(animationbot);
        usernameEditText.startAnimation(animationleft);
        username_label.startAnimation(animationright);
    }

    /**
     * new pass view
     */
    private void newPass() {
        passwordEditText.setVisibility(View.VISIBLE);
        password_label.setVisibility(View.VISIBLE);
        confirmNewPass.setVisibility(View.VISIBLE);
        code.setVisibility(View.VISIBLE);
        code_label.setVisibility(View.VISIBLE);

        animationNewPass();

        sendCode.setVisibility(View.INVISIBLE);
        usernameEditText.setVisibility(View.INVISIBLE);
        username_label.setVisibility(View.INVISIBLE);
        login_button.setVisibility(View.INVISIBLE);
        register_button.setVisibility(View.INVISIBLE);
        forgot_password.setVisibility(View.INVISIBLE);
    }

    /**
     * animation of new pass
     */
    private void animationNewPass() {
        Animation animationleft = AnimationUtils.loadAnimation(MainActivity.this, R.anim.lefttoright);
        Animation animationright = AnimationUtils.loadAnimation(MainActivity.this, R.anim.righttoleft);
        Animation animationbot = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottotop);
        confirmNewPass.startAnimation(animationbot);
        code.startAnimation(animationleft);
        passwordEditText.startAnimation(animationleft);
        password_label.startAnimation(animationright);
        code_label.startAnimation(animationright);
    }

    /**
     * login view
     */
    private void showLogin() {
        usernameEditText.setVisibility(View.VISIBLE);
        username_label.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        password_label.setVisibility(View.VISIBLE);
        login_button.setVisibility(View.VISIBLE);
        register_button.setVisibility(View.VISIBLE);
        forgot_password.setVisibility(View.VISIBLE);
        rememberMeCheckBox.setVisibility(View.VISIBLE);

        runAnimation();

        confirmNewPass.setVisibility(View.INVISIBLE);
        sendCode.setVisibility(View.INVISIBLE);
        code.setVisibility(View.INVISIBLE);
        code_label.setVisibility(View.INVISIBLE);

        login_button_firstlogin.setVisibility(View.INVISIBLE);
        code_label_login.setVisibility(View.INVISIBLE);
        code_login.setVisibility(View.INVISIBLE);
    }

    /**
     * show first login page view
     */
    public void showFirstLogin() {
        System.out.println("showFirstLogin");
        login_button.setVisibility(View.INVISIBLE);
        register_button.setVisibility(View.INVISIBLE);
        rememberMeCheckBox.setVisibility(View.INVISIBLE);

        login_button_firstlogin.setVisibility(View.VISIBLE);
        code_label_login.setVisibility(View.VISIBLE);
        code_login.setVisibility(View.VISIBLE);
    }

    // TODO: 11/04/2020 make username and password text field disabled

    /**
     * Confirm user's account.
     *
     * @param view current view.
     */
    public void confirm(View view) {
        preventTwoClick(view);
        String code = code_login.getText().toString();
        System.out.println("confirm code " + code);
        if ("".equals(code))
            Toast.makeText(this, "Empty Code Is Not Accepted", Toast.LENGTH_SHORT).show();
        else
            userManager.verify(usernameEditText.getText().toString(), code, new ConfirmationHandler(this));
    }

    /**
     * Confirmation succeeded.
     */
    public void confirmed() {
        Toast.makeText(this, "Confirmation succeeded!", Toast.LENGTH_SHORT).show();
        login(null);
    }
}
