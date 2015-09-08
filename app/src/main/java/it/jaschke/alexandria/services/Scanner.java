package it.jaschke.alexandria.services;


import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by FM on 9/6/2015.
 */
public class Scanner extends Fragment implements ZXingScannerView.ResultHandler{

    public static final String LOG_TAG = Scanner.class.getSimpleName();
    private static final String FLASH_STATE = "FLASH";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS";
    private static final String CAMERA_ID = "CAMERA";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private int mCameraId = -1;

    /*// Toggle flash:
    void setFlash(boolean);

    // Toogle autofocus:
    void setAutoFocus(boolean);

    // Specify interested barcode formats:
    void setFormats(List<BarcodeFormat> formats);

    // Specify the cameraId to start with:
    void startCamera(int cameraId);*/

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(false);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state){
        mScannerView = new ZXingScannerView(getActivity());
        if(state != null){
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mCameraId = state.getInt(CAMERA_ID, -1);
        }
        else{
            mFlash = false;
            mAutoFocus = true;
            mCameraId = -1;
        }
        setFormats();
        return mScannerView;
    }



    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera(mCameraId);          // Start camera on resume
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
        //closeDialog("Scanner");
    }
/*
    public void closeDialog(String tag){
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment)fragmentManager.findFragmentByTag(tag);
        if (fragment!=null){
            fragment.dismiss();
        }

    }*/

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(LOG_TAG, rawResult.getText()); // Prints scan results
        Log.v(LOG_TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        try{
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
        }
        catch(Exception e){ Log.d(LOG_TAG, e.getMessage());}

        String result =rawResult.getText();
        if (result!=null){
            ((scannerCallBack)getActivity()).getResult(result);
        }
    }

    public interface  scannerCallBack{
        void getResult(String result);
    }




    //setup barcode format
    public void setFormats(){
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();

        //import all the format into mScannerView from Library
        for(int i=0; i<ZXingScannerView.ALL_FORMATS.size();i++){
            formats.add(ZXingScannerView.ALL_FORMATS.get(i));
        }
        if(mScannerView != null){
            mScannerView.setFormats(formats);
        }

    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putInt(CAMERA_ID, mCameraId);
    }
}
