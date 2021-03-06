package com.renren.ruolan.travelaround.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.renren.ruolan.travelaround.BaseActivity;
import com.renren.ruolan.travelaround.R;
import com.renren.ruolan.travelaround.constant.Contants;
import com.renren.ruolan.travelaround.entity.MyUser;
import com.renren.ruolan.travelaround.event.LoginEvent;
import com.renren.ruolan.travelaround.utils.PreferencesUtils;
import com.renren.ruolan.travelaround.widget.CheckBox;
import com.renren.ruolan.travelaround.widget.dialog.CustomPrograss;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends BaseActivity implements View.OnClickListener {


    EditText mEtPhone;  //手机号
    EditText mEtPassword;  //密码
    CheckBox mIsPasswordMemory;  //是否记住密码
    TextView mTvForgetPassword; //忘记密码

    TextView mRegisterAccount;  //注册账号
    TextView mApplicationConsultant;  //申请咨询师
    ImageView mQqLogin;  //qq登录
    ImageView mSinaLogin;  //新浪登录
    ImageView mWxLogin;  //微信登录
    private String mUserName;
    private String mPassWord;

    private RelativeLayout mReLogin;
    private String mName;
    private String mPwd;
   // private static Tencent mTencent;
   // private BaseUiListener mUiListener;

  //  private UserInfo mInfo = null;


    @Override
    protected int getResultId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initListener() {
        mQqLogin.setOnClickListener(this);
        mRegisterAccount.setOnClickListener(this);
        mTvForgetPassword.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mUserName = PreferencesUtils.getString(this, Contants.USER_NAME);
        mPassWord = PreferencesUtils.getString(this, Contants.USER_PASSWORD);
        if (mUserName != null) {
            mEtPhone.setText(mUserName);
        }
        if (mPassWord != null) {
            mEtPassword.setText(mPassWord);
        }

        mName = mEtPhone.getText().toString();
//        if (!RegularUtils.isMobileExact(name)){
//            Toast.makeText(this, getResources().getString(R.string.is_not_phone), Toast.LENGTH_SHORT).show();
//            return;
//        }

        mPwd = mEtPassword.getText().toString();

        // toLogin(name,pwd);

    }

    /**
     * 登录账户
     *
     * @param name 用户名
     * @param pwd  密码
     */
    private void toLogin(String name, String pwd) {
        final MyUser bmobUser = new MyUser();
        bmobUser.setUsername(name);
        bmobUser.setPassword(pwd);
       // bmobUser.setImgurl("http://q.qlogo.cn/qqapp/1105704769/8D6EA799DE83CA23ABD0D22C4CDAF304/100");

        CustomPrograss.show(this,getResources().getString(R.string.loading),true,null);
        bmobUser.login(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null){
                    CustomPrograss.disMiss();
                    Toast.makeText(LoginActivity.this,
                            getResources().getString(R.string.login_success),
                            Toast.LENGTH_SHORT).show();

                    if (!mIsPasswordMemory.isChecked()) {  //如果用户没有点击记住密码  那就清除密码
                        PreferencesUtils.putBoolean(LoginActivity.this,"is_select",false);
                        PreferencesUtils.putString(LoginActivity.this, Contants.USER_PASSWORD, "");
                    } else { //否则就保存密码
                        PreferencesUtils.putBoolean(LoginActivity.this,"is_select",true);
                        PreferencesUtils.putString(LoginActivity.this, Contants.USER_PASSWORD, pwd);
                    }

                    if (!mIsPasswordMemory.isChecked()){
                        PreferencesUtils.putString(LoginActivity.this, Contants.USER_NAME, "");
                    } else {
                        PreferencesUtils.putString(LoginActivity.this, Contants.USER_NAME, name);
                    }

//                  //  MyUser myUser = new MyUser();
//                    myUser.setUsername(name);
//                    myUser.update(new UpdateListener() {
//                        @Override
//                        public void done(BmobException e) {
//
//                        }
//                    });
                    myUser.setImgurl("http://q.qlogo.cn/qqapp/1105704769/8D6EA799DE83CA23ABD0D22C4CDAF304/100");
                    myUser.update(myUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e==null){
                                Toast.makeText(LoginActivity.this, "update userinfo success", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    EventBus.getDefault().post(new LoginEvent(myUser));

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(Contants.IS_COME_FROM_LOGIN, true);
                    startActivity(intent);
                    finish();
                }
            }
        });

//        bmobUser.login(BmobUser.class).subscribe(new Subscriber<BmobUser>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(BmobUser bmobUser) {
//
//
//
//            }
//
//        });


    }

    @Override
    public void initView() {

//        //QQ的初始化
//        mTencent = Tencent.createInstance("1105704769", this.getApplicationContext());
//        mInfo = new UserInfo(this, mTencent.getQQToken());

        mReLogin = (RelativeLayout) findViewById(R.id.re_login);
        mReLogin.setOnClickListener(this);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mIsPasswordMemory = (CheckBox) findViewById(R.id.is_password_memory);
        boolean isSelect = PreferencesUtils.getBoolean(this, "is_select");
        mIsPasswordMemory.setChecked(isSelect);
        mTvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        mRegisterAccount = (TextView) findViewById(R.id.register_account);
        mApplicationConsultant = (TextView) findViewById(R.id.application_consultant);
        mQqLogin = (ImageView) findViewById(R.id.qq_login);
        mSinaLogin = (ImageView) findViewById(R.id.sina_login);
        mWxLogin = (ImageView) findViewById(R.id.wx_login);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_account:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;

            case R.id.re_login:
                mName = mEtPhone.getText().toString();
                mPwd = mEtPassword.getText().toString();
                if (!TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mPwd))
                    toLogin(mName, mPwd);
                break;

            case R.id.tv_forget_password:
                changePassword();
                break;

            case R.id.qq_login:
                //onClickLogin();

            //    mTencent.login(this, "all", loginListener);

                break;

        }
    }

    /**
     * 忘记密码之后修改密码
     * <p>
     * 只对于注册用户，如果是第三方登录的用户，可以自己去修改第三方
     * 密码之后再进行登录
     */
    private void changePassword() {
//        new MaterialDialog.Builder(this)
//                .title(getResources().getString(R.string.change_password))
//                .items(R.array.password)
//                .backgroundColor(getResources().getColor(R.color.dialog_bg))
//                .itemsCallback((dialog, itemView, position, text) -> {
//                    if (position == 0) {
//                        Intent intent = new Intent(LoginActivity.this, CorrectPasswordFirst.class);
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(LoginActivity.this,
//                                getResources().getString(R.string.new_function),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }).show();
    }

//    private void updateUserInfo() {
//        if (mTencent != null && mTencent.isSessionValid()) {
//            IUiListener listener = new IUiListener() {
//
//                @Override
//                public void onError(UiError e) {
//
//                }
//
//                @Override
//                public void onComplete(final Object response) {
//                    Message msg = new Message();
//                    msg.obj = response;
//                    msg.what = 0;
//                    mHandler.sendMessage(msg);
//                    new Thread() {
//
//                        @Override
//                        public void run() {
//                            JSONObject json = (JSONObject) response;
//                            if (json.has("figureurl")) {
//                                Bitmap bitmap = null;
//                                try {
//                                    bitmap = Util.getbitmap(json.getString("figureurl_qq_2"));
//                                } catch (JSONException e) {
//
//                                }
//                                Message msg = new Message();
//                                msg.obj = bitmap;
//                                msg.what = 1;
//                                mHandler.sendMessage(msg);
//                            }
//                        }
//
//                    }.start();
//                    finish();
//                }
//
//                @Override
//                public void onCancel() {
//
//                }
//            };
//            mInfo = new UserInfo(this, mTencent.getQQToken());
//            mInfo.getUserInfo(listener);
//
//        } else {
////            mUserInfo.setText("");
////            mUserInfo.setVisibility(android.view.View.GONE);
////            mUserLogo.setVisibility(android.view.View.GONE);
//        }
//    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                //SharedPreferencesUtils.setParam(LoginActivity.this,"isLogin",true);
                JSONObject response = (JSONObject) msg.obj;
//                if (response.has("nickname")) {
//                    try {
//                        mUserInfo.setVisibility(android.view.View.VISIBLE);
//                        mUserInfo.setText(response.getString("nickname"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
                String nickname;
                boolean sex;
                String imgurl;
                String location;
                try {
                    Log.d("ruolanmingyue", "response:" + response);
                    nickname = response.getString("nickname");
                    imgurl = response.getString("figureurl_qq_2");
                    sex = response.getString("gender").equals("男");
                    location = response.getString("city");
                    MyUser user = new MyUser();
                    user.setUsername(nickname);
                    user.setImgurl(imgurl);
                    user.setInstance(location);
                    user.setSex(sex);


//                    ThirdPartUser thirdPartUser = new ThirdPartUser();
//                    thirdPartUser.setUsername(nickname);
//                    thirdPartUser.setCity(location);
//                    thirdPartUser.setSex(sex == false ? "1" : "0");
//                    thirdPartUser.setImgUrl(imgurl);
//
//                    BmobQuery<ThirdPartUser> query = new BmobQuery<>();
//                   // query.
//                    thirdPartUser.

                    user.setPassword("123456asd");  //如果是第三方进入的，默认密码123456asd
                    user.signUp(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser o, BmobException e) {
                            if (e == null) {

                            } else {

                                //Toast.makeText(LoginActivity.this, getResources().getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
                            }
                        }


                    });

                    PreferencesUtils.putString(LoginActivity.this,
                            Contants.USER_NAME, nickname);
                    PreferencesUtils.putString(LoginActivity.this,
                            Contants.USER_PASSWORD, "123456asd");


//                    addSubscription(user.signUp(new SaveListener<MyUser>() {
//                        @Override
//                        public void done(MyUser myUser, BmobException e) {
//                            if (e == null) {
//    //                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
//  //                              PreferencesUtils.putString(LoginActivity.this,Contants.USER_NAME,userName);
////                                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
//                            } else {
//                                //注册失败
//                            }
//                        }
//                    }));

                    Log.d("ruolanmingyue", nickname);
                    PreferencesUtils.putBoolean(LoginActivity.this, Contants.IS_LOGIN, true);
                    EventBus.getDefault().post(new LoginEvent(user));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (msg.what == 1) {
                Bitmap bitmap = (Bitmap) msg.obj;
                Log.d("ruolan", "msg.obj:" + msg.obj);
                // EventBus.getDefault().post(bitmap);
//                mUserLogo.setImageBitmap(bitmap);
//                mUserLogo.setVisibility(android.view.View.VISIBLE);
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ruolan", "-->onActivityResult " + requestCode + " resultCode=" + resultCode);
//        if (requestCode == Constants.REQUEST_LOGIN ||
//                requestCode == Constants.REQUEST_APPBAR) {
//            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }

//
//    IUiListener loginListener = new BaseUiListener() {
//        @Override
//        protected void doComplete(JSONObject values) {
//            Log.d(TAG, "ruolanmingyue:" + values);
//            Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
//            initOpenidAndToken(values);
//
//            //下面的这个必须放到这个地方，要不然就会出错   哎，，，，，调整了近一个小时，，，，我是服我自己了
//            updateUserInfo();
//        }
//    };
//
//
//    public static void initOpenidAndToken(JSONObject jsonObject) {
//        try {
//            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
//            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
//            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
//            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
//                    && !TextUtils.isEmpty(openId)) {
//                mTencent.setAccessToken(token, expires);
//                mTencent.setOpenId(openId);
//            }
//        } catch (Exception e) {
//        }
//    }
//
//    private class BaseUiListener implements IUiListener {
//
//        @Override
//        public void onComplete(Object response) {
//            if (null == response) {
//                // Util.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
//                return;
//            }
//            JSONObject jsonResponse = (JSONObject) response;
//            if (null != jsonResponse && jsonResponse.length() == 0) {
//                //  Util.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
//                return;
//            }
//            doComplete((JSONObject) response);
//        }
//
//        @Override
//        public void onError(UiError e) {
//            Util.toastMessage(LoginActivity.this, "onError: " + e.errorDetail);
//
//        }
//
//        @Override
//        public void onCancel() {
//            Util.toastMessage(LoginActivity.this, "onCancel: ");
//
//        }
//
//        protected void doComplete(JSONObject values) {
//
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //进行反注册
        EventBus.getDefault().unregister(this);
    }


}
