package com.myatminsoe.mkeyboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class FragmentAbout extends Fragment implements View.OnClickListener {

    View root;
    TextView tvVersion, tvFeedBack, tvFacebook, tvPrivacy, tvLicense;
    RelativeLayout rlVersion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frag_about, null);
        init();
        return root;
    }

    private void init() {
        tvVersion = (TextView) root.findViewById(R.id.tvversion);
        tvFeedBack = (TextView) root.findViewById(R.id.tvfeedback);
        tvFacebook = (TextView) root.findViewById(R.id.tvfb);
        tvPrivacy = (TextView) root.findViewById(R.id.tvpp);
        tvLicense = (TextView) root.findViewById(R.id.tvlicense);
        rlVersion = (RelativeLayout) root.findViewById(R.id.rlVersion);
        try {
            tvVersion.setText(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (Exception e) {
            Log.e("Version name not found", e + "");
        }
        tvFeedBack.setOnClickListener(this);
        tvFacebook.setOnClickListener(this);
        tvPrivacy.setOnClickListener(this);
        tvLicense.setOnClickListener(this);
        rlVersion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        switch (v.getId()) {
            case R.id.tvfeedback:
                alert.setTitle("Feedback");
                final EditText et = new EditText(getActivity());
                et.setMinLines(5);
                et.setGravity(Gravity.START);
                alert.setView(et);
                alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (et.getText().toString().trim().length() > 0) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("internet");
                            query.getInBackground("m0qtPOKcZT", new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        String fbstring = et.getText().toString();
                                        ParseObject fbObject = new ParseObject("Feedback");
                                        fbObject.put("Text", fbstring);
                                        fbObject.put("Version", android.os.Build.VERSION.SDK_INT);
                                        fbObject.put("Device", Build.DEVICE);
                                        fbObject.saveInBackground();
                                    } else {
                                        Toast.makeText(getActivity(), "Can't connect to server.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Developers don't want to read blank messages :(", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                break;
            case R.id.tvfb:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/mkeyboardapp")));
                break;
            case R.id.tvpp:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.googledrive.com/host/0BzVqrJ2Yq_yLbTRxNVE4YVhrdVE")));
                break;
            case R.id.tvlicense:
                alert.setTitle("Open source licenses");
                WebView wv = new WebView(getActivity());
                wv.loadUrl("file:///android_asset/licenses.html");
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });

                alert.setView(wv);
                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                break;
            case R.id.rlVersion:
                int versionCode = 5;
                try {
                    versionCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final int vc = versionCode;
                ParseQuery<ParseObject> query = ParseQuery.getQuery("internet");
                query.getInBackground("m0qtPOKcZT", new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            if (vc >= (Integer) object.get("version")) {
                                alert.setTitle("Hola!")
                                        .setMessage("You are using the latest version")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                            } else {
                                alert.setTitle("Update available")
                                        .setMessage("M Keyboard (Version " + object.get("name") + ") is available. Please downlaod it from Dinga Store").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                            alert.show();
                        } else {
                            Toast.makeText(getActivity(), "Can't connect to server.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}