package com.nextar.sumup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpLogin;
import com.sumup.merchant.api.SumUpPayment;
import com.sumup.merchant.api.SumUpState;
import com.sumup.merchant.ui.Activities.MainActivity;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by Robert Suman on 22/02/2018.
 */

public class RNSumUpModule extends ReactContextBaseJavaModule {

  private static final int REQUEST_CODE_LOGIN = 1;
  private static final int REQUEST_CODE_PAYMENT = 2;
  private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;

  private static final int TRANSACTION_SUCCESSFUL = 1;

  private Promise mSumUpPromise;

  public RNSumUpModule(ReactApplicationContext reactContext) {
    super(reactContext);
    reactContext.addActivityEventListener(mActivityEventListener);
  }

  @Override
  public String getName() {
    return "RNSumUpModule";
  }

  @ReactMethod
  public void toastString(String text) {
    Toast.makeText(getReactApplicationContext(), text, Toast.LENGTH_SHORT).show();
  }

  @ReactMethod
  public void login(String affiliateKey) {
    SumUpLogin sumupLogin = SumUpLogin.builder(affiliateKey).build();
    SumUpAPI.openLoginActivity(getCurrentActivity(), sumupLogin, REQUEST_CODE_LOGIN);
  }

  @ReactMethod
  public void prepareForCheckout() {
    SumUpAPI.prepareForCheckout();
  }

  @ReactMethod
  public void logout() {
    SumUpAPI.logout();
  }

  @ReactMethod
  public void checkout(String affiliateKey, Double value, String name, Promise promise)
  {
    mSumUpPromise = promise;
    try {

      SumUpPayment payment = SumUpPayment.builder()
              .affiliateKey(affiliateKey)
              .total(new BigDecimal(value)) // minimum 1.00
              .currency(SumUpPayment.Currency.BRL)
              .title(name)
              // optional: foreign transaction ID, must be unique!
              .foreignTransactionId(UUID.randomUUID().toString()) // can not exceed 128 chars
              .skipSuccessScreen()
              .build();

      SumUpAPI.checkout(getCurrentActivity(), payment, REQUEST_CODE_PAYMENT);
    }
    catch (Exception ex) {
      mSumUpPromise.reject(ex);
      mSumUpPromise = null;
    }
  }



  private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

    @Override
    public void onActivityResult(Activity activity,int requestCode, int resultCode, Intent data) {

      switch (requestCode) {
        case REQUEST_CODE_LOGIN:

          if (data != null) {

            Bundle extra = data.getExtras();
            String text = "Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE) + "Message: " + extra.getString(SumUpAPI.Response.MESSAGE);
            Toast.makeText(getReactApplicationContext(), text, Toast.LENGTH_SHORT).show();
            SumUpAPI.openPaymentSettingsActivity(getCurrentActivity(), REQUEST_CODE_PAYMENT_SETTINGS);
          }
          break;

        case REQUEST_CODE_PAYMENT:

          if (data != null) {

            if (mSumUpPromise != null) {

              Bundle extra = data.getExtras();

              if (extra.getInt(SumUpAPI.Response.RESULT_CODE) == TRANSACTION_SUCCESSFUL)
                mSumUpPromise.resolve(extra.getString(SumUpAPI.Response.MESSAGE));
              else
                mSumUpPromise.reject(extra.getString(SumUpAPI.Response.RESULT_CODE),extra.getString(SumUpAPI.Response.MESSAGE));
            }

          }
          break;

        default:
          break;
      }
    }

  };
}


