package com.orca.kam.rxandroidfingerprintauth.auth;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

/**
 * Created by Kang Young Won on 2016-08-22.
 */
public class FingerPrintAuthOnSubscribe implements Observable.OnSubscribe<FingerprintManager.AuthenticationResult> {

    private Context context;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private CancellationSignal cancellationSignal;


    public FingerPrintAuthOnSubscribe(Context context, FingerprintManager.CryptoObject cryptoObject) {
        this.context = context;
        this.cryptoObject = cryptoObject;
        fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        cancellationSignal = new CancellationSignal();
    }


    @Override public void call(Subscriber<? super FingerprintManager.AuthenticationResult> subscriber) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            subscriber.onError(new Resources.NotFoundException("SDK is installed does not support the fingerprint authentication"));
        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                subscriber.onError(new IllegalAccessException("This has not been granted permission for fingerprint recognition"));
                return;
            }
            if (!fingerprintManager.isHardwareDetected()) {
                subscriber.onError(new Resources.NotFoundException("The fingerprint device is not installed"));
                return;
            }
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                subscriber.onError(new Resources.NotFoundException("Fingerprint is not registered"));
                return;
            }
            FingerprintManager.AuthenticationCallback authCallback = new FingerprintManager.AuthenticationCallback() {
                @Override public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    subscriber.onError(new NullPointerException("onAuthenticationError [ errorCode : " + errorCode + " | errString : " + errString + "]"));
                }


                @Override public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    subscriber.onError(new NullPointerException("onAuthenticationHelp [ helpCode : " + helpCode + " | helpString : " + helpString + "]"));
                }


                @Override public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    subscriber.onNext(result);
                }


                @Override public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    subscriber.onError(new NullPointerException("onAuthenticationFailed"));
                }
            };
            fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, authCallback, null);
        }


        subscriber.add(new MainThreadSubscription() {
            @Override protected void onUnsubscribe() {
                if (cancellationSignal != null) {
                    cancellationSignal.cancel();
                    cancellationSignal = null;
                }
            }
        });
    }
}
