package wang.yi_ru.prerelease;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;

public class DialogFloatingService extends Service {

    Dialog mDialog;
    WindowManager.LayoutParams wmParams;
    LayoutInflater inflater;
    WindowManager mWindowManager;
    TextView textShow;
    Spinner spinner;
    ArrayAdapter<String> adapter;

    private SpeakerVerifier mVerifier;
    private static final String TAG = DialogFloatingService.class.getSimpleName();

    private int which = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * 初始化
     */
    private void initWindow() {
        mDialog = new Dialog(DialogFloatingService.this);
        mDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));

        //得到容器，通过这个inflater来获得悬浮窗控件
        inflater = LayoutInflater.from(getApplication());
        // 获取浮动窗口视图所在布局
        View view = inflater.inflate(R.layout.dialog_layout, null);

        final ImageView iv = (ImageView) view.findViewById(R.id.dialog_image);
        final TextView textPwd = (TextView) view.findViewById(R.id.dialog_text_pwd);
        textShow = (TextView) view.findViewById(R.id.dialog_text_show);

        final String[] passowrds = {
                getString(R.string.pref_title_password0),
                getString(R.string.pref_title_password1),
                getString(R.string.pref_title_password2)};

        spinner = (Spinner) view.findViewById(R.id.dialog_spinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, passowrds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将适配器添加到spinner中去
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);//设置默认显示
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                which = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                which = -1;
            }
        });

        mVerifier = SpeakerVerifier.createVerifier(DialogFloatingService.this, new InitListener() {

            @Override
            public void onInit(int errorCode) {
                if (ErrorCode.SUCCESS == errorCode) {
//                    showText("引擎初始化成功");
                    Log.d(TAG, "引擎初始化成功");
                } else {
                    showText("引擎初始化失败，错误码：" + errorCode);
                }
            }
        });

        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showText("ImageView Clicked.");
                // 清空参数
                mVerifier.setParameter(SpeechConstant.PARAMS, null);
                mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
                mVerifier = SpeakerVerifier.getVerifier();
                // 设置业务类型为验证
                mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");
                // 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
//

                mVerifier.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);

                // 钦定为数字类型密码
                // 数字密码注册需要传入密码
                String verifyPwd = mVerifier.generatePassword(8);
                mVerifier.setParameter(SpeechConstant.ISV_PWD, verifyPwd);
                textPwd.setText("请读出："
                        + verifyPwd);

                // 设置auth_id，不能设置为空
                String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                        .getDeviceId();
                mVerifier.setParameter(SpeechConstant.AUTH_ID, Imei);
                mVerifier.setParameter(SpeechConstant.ISV_PWDT, "3"); // 3为数字密码
                // 开始验证
                mVerifier.startListening(mVerifyListener);
            }
        });


        Log.e("Context Dialog", getApplicationContext().toString());
        // 添加悬浮窗的视图
        mDialog.setContentView(view);
        mDialog.setTitle("声纹验证");

        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    private VerifierListener mVerifyListener = new VerifierListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showText("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onResult(VerifierResult result) {
            showText(result.source);
            Log.e("result",result.toString());
            Log.e("score",Double.toString(result.score));
            Log.e("score raw",Double.toString(result.score_raw));
            Log.e("result err",Integer.toString(result.err));
            Log.e("result ret",Integer.toString(result.ret));
            if (result.ret == 0 && result.score>Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("rate","null")))// 验证通过
            {
                showText("验证通过，匹配率为" + result.score);
                if (which == -1) {
                    showText("请选择你需要的密码");
                    return;
                }
                ClipData thisClip;
                thisClip = ClipData.newPlainText("password",
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("password" + which, "null"));
                ClipboardManager SysClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                SysClipboard.setPrimaryClip(thisClip);

            } else {
                // 验证不通过
                switch (result.err) {
                    case VerifierResult.MSS_ERROR_IVP_GENERAL:
                        showText("内核异常");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
                        showText("出现截幅");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
                        showText("太多噪音");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
                        showText("录音太短");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
                        showText("验证不通过，您所读的文本不一致");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
                        showText("音量太低");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
                        showText("音频长达不到自由说的要求");
                        break;
                    default:
                        showText("验证不通过，匹配率为" + result.score);
                        break;
                }
            }
        }

        // 保留方法，暂不用
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }

        @Override
        public void onError(SpeechError error) {
            switch (error.getErrorCode()) {
                case ErrorCode.MSP_ERROR_NOT_FOUND:
                    showText("模型不存在，请先注册");
                    break;

                default:
                    showText("onError Code：" + error.getPlainDescription(true));
                    break;
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showText("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showText("开始说话");
        }
    };


    private void showText(String s) {
//        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        textShow.setText(s);
    }


}
