package com.nextar.sumup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpLogin;
import com.sumup.merchant.api.SumUpPayment;
import com.sumup.merchant.CoreState;
import com.sumup.merchant.Models.UserModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Robert Suman on 22/02/2018.
 * Updated by Ítalo Menezes :)
 */

public class RNSumUpModule extends ReactContextBaseJavaModule {

  private static final int REQUEST_CODE_LOGIN = 1;
  private static final int REQUEST_CODE_PAYMENT = 2;
  private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;
  private static final int TRANSACTION_SUCCESSFUL = 1;

  private static final String SMPCurrencyCodeBGN = "SMPCurrencyCodeBGN";
  private static final String SMPCurrencyCodeBRL = "SMPCurrencyCodeBRL";
  private static final String SMPCurrencyCodeCHF = "SMPCurrencyCodeCHF";
  private static final String SMPCurrencyCodeCLP = "SMPCurrencyCodeCLP";
  private static final String SMPCurrencyCodeCZK = "SMPCurrencyCodeCZK";
  private static final String SMPCurrencyCodeDKK = "SMPCurrencyCodeDKK";
  private static final String SMPCurrencyCodeEUR = "SMPCurrencyCodeEUR";
  private static final String SMPCurrencyCodeGBP = "SMPCurrencyCodeGBP";
  private static final String SMPCurrencyCodeHUF = "SMPCurrencyCodeHUF";
  private static final String SMPCurrencyCodeNOK = "SMPCurrencyCodeNOK";
  private static final String SMPCurrencyCodePLN = "SMPCurrencyCodePLN";
  private static final String SMPCurrencyCodeRON = "SMPCurrencyCodeRON";
  private static final String SMPCurrencyCodeSEK = "SMPCurrencyCodeSEK";
  private static final String SMPCurrencyCodeUSD = "SMPCurrencyCodeUSD";

  private Promise mSumUpPromise;

  public RNSumUpModule(ReactApplicationContext reactContext) {
    super(reactContext);
    reactContext.addActivityEventListener(mActivityEventListener);
  }

  @Override
  public String getName() {
    return "RNSumUp";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("SMPCurrencyCodeBGN", SMPCurrencyCodeBGN);
    constants.put("SMPCurrencyCodeBRL", SMPCurrencyCodeBRL);
    constants.put("SMPCurrencyCodeCHF", SMPCurrencyCodeCHF);
    constants.put("SMPCurrencyCodeCLP", SMPCurrencyCodeCLP);
    constants.put("SMPCurrencyCodeCZK", SMPCurrencyCodeCZK);
    constants.put("SMPCurrencyCodeDKK", SMPCurrencyCodeDKK);
    constants.put("SMPCurrencyCodeEUR", SMPCurrencyCodeEUR);
    constants.put("SMPCurrencyCodeGBP", SMPCurrencyCodeGBP);
    constants.put("SMPCurrencyCodeHUF", SMPCurrencyCodeHUF);
    constants.put("SMPCurrencyCodeNOK", SMPCurrencyCodeNOK);
    constants.put("SMPCurrencyCodePLN", SMPCurrencyCodePLN);
    constants.put("SMPCurrencyCodeRON", SMPCurrencyCodeRON);
    constants.put("SMPCurrencyCodeSEK", SMPCurrencyCodeSEK);
    constants.put("SMPCurrencyCodeUSD", SMPCurrencyCodeUSD);
    return constants;
  }

  @ReactMethod
  public void authenticate(String affiliateKey, Promise promise) {
    mSumUpPromise = promise;
    SumUpLogin sumupLogin = SumUpLogin.builder(affiliateKey).build();
    SumUpAPI.openLoginActivity(getCurrentActivity(), sumupLogin, REQUEST_CODE_LOGIN);
  }

  @ReactMethod
  public void authenticateWithToken(String affiliateKey, String token, Promise promise) {
    mSumUpPromise = promise;
    SumUpLogin sumupLogin = SumUpLogin.builder(affiliateKey).accessToken(token).build();
    SumUpAPI.openLoginActivity(getCurrentActivity(), sumupLogin, REQUEST_CODE_LOGIN);
  }

  @ReactMethod
  public void prepareForCheckout(Promise promise) {
    mSumUpPromise = promise;
    SumUpAPI.prepareForCheckout();
  }

  @ReactMethod
  public void logout(Promise promise) {
    mSumUpPromise = promise;
    SumUpAPI.logout();
    mSumUpPromise.resolve(true);
  }

  private SumUpPayment.Currency getCurrency(String currency) {
    switch (currency) {
      case SMPCurrencyCodeBGN: return SumUpPayment.Currency.BGN;
      case SMPCurrencyCodeBRL: return SumUpPayment.Currency.BRL;
      case SMPCurrencyCodeCHF: return SumUpPayment.Currency.CHF;
      case SMPCurrencyCodeCLP: return SumUpPayment.Currency.CLP;
      case SMPCurrencyCodeCZK: return SumUpPayment.Currency.CZK;
      case SMPCurrencyCodeDKK: return SumUpPayment.Currency.DKK;
      case SMPCurrencyCodeEUR: return SumUpPayment.Currency.EUR;
      case SMPCurrencyCodeGBP: return SumUpPayment.Currency.GBP;
      case SMPCurrencyCodeHUF: return SumUpPayment.Currency.HUF;
      case SMPCurrencyCodeNOK: return SumUpPayment.Currency.NOK;
      case SMPCurrencyCodePLN: return SumUpPayment.Currency.PLN;
      case SMPCurrencyCodeRON: return SumUpPayment.Currency.RON;
      case SMPCurrencyCodeSEK: return SumUpPayment.Currency.SEK;
      default: case SMPCurrencyCodeUSD: return SumUpPayment.Currency.USD;
    }
  }

  @ReactMethod
  public void checkout(ReadableMap request, Promise promise) {
    // TODO: replace foreignTransactionId for transaction UUID sent by user.
    mSumUpPromise = promise;
    try {
      String foreignTransactionId = UUID.randomUUID().toString();
      if (request.getString("foreignTransactionId") != null) {
        foreignTransactionId = request.getString("foreignTransactionId");
      }

      SumUpPayment.Currency currencyCode = this.getCurrency(request.getString("currencyCode"));
      SumUpPayment payment = SumUpPayment.builder()
              .total(new BigDecimal(request.getString("totalAmount")).setScale(2, RoundingMode.HALF_EVEN))
              .currency(currencyCode)
              .title(request.getString("title"))
              .foreignTransactionId(foreignTransactionId)
              .skipSuccessScreen()
              .build();
      SumUpAPI.checkout(getCurrentActivity(), payment, REQUEST_CODE_PAYMENT);
    } catch (Exception ex) {
      mSumUpPromise.reject(ex);
      mSumUpPromise = null;
    }
  }

  @ReactMethod
  public void preferences(Promise promise) {
    mSumUpPromise = promise;
    SumUpAPI.openPaymentSettingsActivity(getCurrentActivity(), REQUEST_CODE_PAYMENT_SETTINGS);
  }

  @ReactMethod
    public void isLoggedIn(Promise promise) {
      WritableMap map = Arguments.createMap();
      map.putBoolean("isLoggedIn", SumUpAPI.isLoggedIn());
      promise.resolve(map);
    }

  private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
        case REQUEST_CODE_LOGIN:
          if (data != null) {
            Bundle extra = data.getExtras();
            if (extra.getInt(SumUpAPI.Response.RESULT_CODE) == REQUEST_CODE_LOGIN) {
              WritableMap map = Arguments.createMap();
              map.putBoolean("success", true);

              UserModel userInfo = CoreState.Instance().get(UserModel.class);
              WritableMap userAdditionalInfo = Arguments.createMap();
              userAdditionalInfo.putString("merchantCode", userInfo.getBusiness().getMerchantCode());
              userAdditionalInfo.putString("currencyCode", userInfo.getBusiness().getCountry().getCurrency().getCode());
              map.putMap("userAdditionalInfo", userAdditionalInfo);

              mSumUpPromise.resolve(map);
            } else {
              mSumUpPromise.reject(extra.getString(SumUpAPI.Response.RESULT_CODE), extra.getString(SumUpAPI.Response.MESSAGE));
            }
          }
          break;

        case REQUEST_CODE_PAYMENT:
          if (data != null) {
            Bundle extra = data.getExtras();
            if (mSumUpPromise != null) {
              if (extra.getInt(SumUpAPI.Response.RESULT_CODE) == TRANSACTION_SUCCESSFUL) {
                WritableMap map = Arguments.createMap();
                map.putBoolean("success", true);
                map.putString("transactionCode", extra.getString(SumUpAPI.Response.TX_CODE));

                TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);
                WritableMap additionalInfo = Arguments.createMap();
                additionalInfo.putString("cardType", transactionInfo.getCard().getType());
                additionalInfo.putString("cardLast4Digits", transactionInfo.getCard().getLast4Digits());
                additionalInfo.putInt("installments", transactionInfo.getInstallments());
                map.putMap("additionalInfo", additionalInfo);

                mSumUpPromise.resolve(map);
              }else
                mSumUpPromise.reject(extra.getString(SumUpAPI.Response.RESULT_CODE), extra.getString(SumUpAPI.Response.MESSAGE));
            }
          }
          break;
        case REQUEST_CODE_PAYMENT_SETTINGS:
          WritableMap map = Arguments.createMap();
          map.putBoolean("success", true);
          mSumUpPromise.resolve(map);
          break;
        default:
          break;
      }
    }

  };
}
