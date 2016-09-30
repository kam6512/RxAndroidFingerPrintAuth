package com.orca.kam.rxandroidfingerprintauth.auth;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;

import rx.Observable;

/**
 * Created by Kang Young Won on 2016-08-22.
 */
public class RxFingerPrintAuth {
    public static Observable<FingerprintManager.AuthenticationResult> authenticate(Context context, FingerprintManager.CryptoObject cryptoObject) {
        return Observable.create(new FingerPrintAuthOnSubscribe(context, cryptoObject));
    }


    private RxFingerPrintAuth() {
        throw new AssertionError("No instances");
    }
}
