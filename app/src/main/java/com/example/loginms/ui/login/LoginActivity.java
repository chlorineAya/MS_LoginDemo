package com.example.loginms.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginms.R;
import com.example.loginms.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;

import org.koishi.library.logintool.auth.abstracts.TextureVariable;
import org.koishi.library.logintool.auth.abstracts.exception.AuthenticationException;
import org.koishi.library.logintool.auth.model.mojang.MinecraftAuthenticator;
import org.koishi.library.logintool.auth.model.mojang.MinecraftToken;
import org.koishi.library.logintool.auth.model.mojang.profile.MinecraftProfile;

public class LoginActivity extends AppCompatActivity {
	
    /*
        注意事项：
        1. 此demo使用开源项目Github@Ratsiiel：minecraft-auth-library写成程序用于直观展示免网页登录微软账号的过程。
        2. 由于我正在开发Android上的minecraft java版启动器，所以此demo是Android项目，但是如果您在开发PC启动器需要使用，登录方法与Android widget无关。
        3. 此LoginActivity项目是Intellij IDEA自动生成的，花里胡哨的代码很多，不需要管，核心就在Activity里Button的点击监听事件。
    */

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    public MinecraftProfile pf;
    public MinecraftToken tk;
    public TextureVariable tv;

    public String skinFaceBase64;
    public TextView name, uuid, token;

    public EditText usernameEditText;
    public EditText passwordEditText;
    public Button loginButton;
    public ProgressBar loadingProgressBar;

    public RelativeLayout rl;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rl = findViewById(R.id.container);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        usernameEditText = binding.username;
        passwordEditText = binding.password;
        loginButton = binding.login;
        loadingProgressBar = binding.loading;

        name = findViewById(R.id.name);
        uuid = findViewById(R.id.uuid);
        token = findViewById(R.id.token);

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            finish();
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        //登录需要调用异步线程实现，否则会报错
        loginButton.setOnClickListener(v -> {
            loginButton.setText(R.string.logining);
            loginButton.setEnabled(false);
            new Thread(() -> {
                try {
                    MinecraftAuthenticator minecraftAuthenticator = new MinecraftAuthenticator();
                    //通过EditText输入的账号密码调用Microsoft验证工具类登录获取令牌的字符串并通过令牌获取需要的用户名、uuid等信息。
	    //EditText的内容为String，因此获取token的函数正常调用如下：
 	    //tk = minecraftAuthenticator.loginWithXbox(String email, String password);
                    tk = minecraftAuthenticator.loginWithXbox(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                    pf = minecraftAuthenticator.checkOwnership(tk);
	    //线程执行成功结束后发送消息，标记参数为1
                    han.sendEmptyMessage(1);
                } catch (AuthenticationException e) {
	    //登录失败之后使用snackbar展示错误原因。
                    Snackbar.make(rl, "登录失败,原因如下:\n"+e, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    han.sendEmptyMessage(0);
                }
            }).start();

        });
    }

    @SuppressLint("HandlerLeak")
    Handler han = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //判断what参数，如果是1就展示用户信息，否则不对错误信息进行处理。
            if (msg.what == 1) {
                if (pf != null && tk != null) {
	    //将保存到的需要使用的uuid数据转换成字符串，用户名和令牌在登录成功后已经获取了字符串。
	    //Minecraft账号的核心是验证令牌，证明你是Minecraft的账号，uuid和username则用于展示你的皮肤和用户名
                    name.setText("<Username>\n"+pf.getUsername());
                    uuid.setText("<UUID>\n"+pf.getUuid().toString());
                    token.setText("<Token>\n"+tk.getAccessToken());
                    loginButton.setText(R.string.relogin);
                    loginButton.setEnabled(true);
                    Snackbar.make(rl, "成功登录到"+pf.getUsername(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {

                }

            }
        }
    };

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }


    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}