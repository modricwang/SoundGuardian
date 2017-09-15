package wang.yi_ru.prerelease;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends Activity implements View.OnClickListener {

    //private Button welcomeButton;


    private static final String TAG = WelcomeActivity.class.getSimpleName();

    private static final int PWD_TYPE_TEXT = 1;
    // 自由说由于效果问题，暂不开放
//	private static final int PWD_TYPE_FREE = 2;
    private static final int PWD_TYPE_NUM = 3;
    // 当前声纹密码类型，1、2、3分别为文本、自由说和数字密码
    private int mPwdType = PWD_TYPE_TEXT;
    // 声纹识别对象
    private SpeakerVerifier mVerifier;
    // 声纹AuthId，用户在云平台的身份标识，也是声纹模型的标识
    // 请使用英文字母或者字母和数字的组合，勿使用中文字符
    private String mAuthId = "";
    // 文本声纹密码
    private String mTextPwd = "";
    // 数字声纹密码
    private String mNumPwd = "";
    // 数字声纹密码段，默认有5段
    private String[] mNumPwdSegs;

    private EditText mResultEditText;
    private TextView mAuthIdTextView;
    private RadioGroup mPwdTypeGroup;
    private TextView mShowPwdTextView;
    private TextView mShowMsgTextView;
    private TextView mShowRegFbkTextView;
    private TextView mRecordTimeTextView;
    private AlertDialog mTextPwdSelectDialog;
    private Button mWelcomeButton;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        //TODO initUi();
        // 将上个页面输入的用户名作为AuthId
        //mAuthId = getIntent().getStringExtra("uname");
        //mAuthIdTextView.setText(mAuthId);
        mWelcomeButton = (Button) findViewById(R.id.welcomeButton);
        String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
        mAuthId = Imei;
        Log.e("IMEI",Imei);
        //((TextView) findViewById(R.id.introductionText)).setText(Imei);
        // 初始化SpeakerVerifier，InitListener为初始化完成后的回调接口
        mVerifier = SpeakerVerifier.createVerifier(WelcomeActivity.this, new InitListener() {

            @Override
            public void onInit(int errorCode) {
                if (ErrorCode.SUCCESS == errorCode) {
                    showTip("引擎初始化成功");
                } else {
                    showTip("引擎初始化失败，错误码：" + errorCode);
                }
            }
        });
        checkState();
    }

    private void checkState() {
        mToast = Toast.makeText(WelcomeActivity.this, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        mPwdType = PWD_TYPE_NUM;
        // 获取密码之前先终止之前的注册或验证过程
        mVerifier.cancel();
        //initTextView();
        //setRadioClickable(false);
        // 清空参数
        mVerifier.setParameter(SpeechConstant.PARAMS, null);
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
        mVerifier.getPasswordList(mPwdListenter);

        performModelOperation("que", mModelOperationListener);

    }

    /**
     * 初始化TextView和密码文本
     */
    private void initTextView() {
        mTextPwd = null;
        mNumPwd = null;
        mResultEditText.setText("");
        mShowPwdTextView.setText("");
        mShowMsgTextView.setText("");
        mShowRegFbkTextView.setText("");
        mRecordTimeTextView.setText("");
    }

    /**
     * 设置radio的状态
     */
    private void setRadioClickable(boolean clickable) {
        // 设置RaioGroup状态为非按下状态
        //mPwdTypeGroup.setPressed(false);
        //findViewById(R.id.radioText).setClickable(clickable);
        //findViewById(R.id.radioNumber).setClickable(clickable);
    }

    /**
     * 执行模型操作
     *
     * @param operation 操作命令
     * @param listener  操作结果回调对象
     */
    private void performModelOperation(String operation, SpeechListener listener) {
        // 清空参数
        mVerifier.setParameter(SpeechConstant.PARAMS, null);
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);


        // 设置auth_id，不能设置为空
        String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
        mVerifier.setParameter(SpeechConstant.AUTH_ID, Imei);
        mVerifier.sendRequest(operation, Imei, listener);


    }

    @Override
    public void onClick(View v) {
    }

    private String[] items;
    private SpeechListener mPwdListenter = new SpeechListener() {
        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            setRadioClickable(true);

            String result = new String(buffer);
            switch (mPwdType) {
                case PWD_TYPE_NUM:
                    StringBuffer numberString = new StringBuffer();
                    try {
                        JSONObject object = new JSONObject(result);
                        if (!object.has("num_pwd")) {
                            initTextView();
                            return;
                        }

                        JSONArray pwdArray = object.optJSONArray("num_pwd");
                        numberString.append(pwdArray.get(0));
                        for (int i = 1; i < pwdArray.length(); i++) {
                            numberString.append("-" + pwdArray.get(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mNumPwd = numberString.toString();
                    mNumPwdSegs = mNumPwd.split("-");
                    //mResultEditText.setText("您的密码：\n" + mNumPwd);
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onCompleted(SpeechError error) {
            setRadioClickable(true);

            if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
                showTip("获取失败：" + error.getErrorCode());
            }
        }
    };

    private SpeechListener mModelOperationListener = new SpeechListener() {

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {

            setRadioClickable(true);

            String result = new String(buffer);
            try {
                JSONObject object = new JSONObject(result);
                String cmd = object.getString("cmd");
                int ret = object.getInt("ret");

                if ("del".equals(cmd)) {
                    if (ret == ErrorCode.SUCCESS) {
                        showTip("删除成功");
                        mResultEditText.setText("");
                    } else if (ret == ErrorCode.MSP_ERROR_FAIL) {
                        showTip("删除失败，模型不存在");
                    }
                } else if ("que".equals(cmd)) {
                    if (ret == ErrorCode.SUCCESS) {
                        //TODO
                        //showTip("模型存在");
                        //((TextView) findViewById(R.id.introductionText)).setText("AC");
                        mWelcomeButton.setText("现在使用");
                        mWelcomeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(WelcomeActivity.this, ManageActivity.class);
                                intent.putExtra("Registered",false);
                                startActivity(intent);
                            }
                        });
                    } else if (ret == ErrorCode.MSP_ERROR_FAIL) {
                        showTip("模型不存在");
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted(SpeechError error) {
            setRadioClickable(true);
            Log.e("Stat", "find");
            if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
                //showTip("操作失败：" + error.getPlainDescription(true));
                //((TextView) findViewById(R.id.introductionText)).setText("WA");
                mWelcomeButton.setText("去注册");
                mWelcomeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO Jump to registerActivity
                        Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                        intent.putExtra("Registered",false);
                        startActivity(intent);
                    }
                });
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        checkState();
    }

    @Override
    public void finish() {
        if (null != mTextPwdSelectDialog) {
            mTextPwdSelectDialog.dismiss();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (null != mVerifier) {
            mVerifier.stopListening();
            mVerifier.destroy();
        }
        super.onDestroy();
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    @Override
    public void onBackPressed() {
        //finishActivity(0);
        //System.exit(0);
    }
}
