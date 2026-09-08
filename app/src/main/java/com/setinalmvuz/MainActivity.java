package com.setinalmvuz;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.setinalmvuz.databinding.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends AppCompatActivity {
	
	private MainBinding binding;
	
	private RequestNetwork net;
	private RequestNetwork.RequestListener _net_request_listener;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		binding = MainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		initialize(_savedInstanceState);
		
		MobileAds.initialize(this);
		
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		net = new RequestNetwork(this);
		
		binding.webview1.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView _param1, String _param2, Bitmap _param3) {
				final String _url = _param2;
				
				super.onPageStarted(_param1, _param2, _param3);
			}
			
			@Override
			public void onPageFinished(WebView _param1, String _param2) {
				final String _url = _param2;
				
				super.onPageFinished(_param1, _param2);
			}
		});
		
		_net_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
	}
	
	private void initializeLogic() {
		// 1. WebView-ni aniqlaymiz
		final WebView webview1 = (WebView) findViewById(R.id.webview1);
		
		// 2. WebView sozlamalari
		webview1.getSettings().setJavaScriptEnabled(true);
		webview1.getSettings().setDomStorageEnabled(true);
		webview1.getSettings().setDatabaseEnabled(true);
		webview1.getSettings().setAllowFileAccess(true);
		webview1.getSettings().setAllowContentAccess(true);
		webview1.getSettings().setAllowFileAccessFromFileURLs(true);
		webview1.getSettings().setAllowUniversalAccessFromFileURLs(true);
		webview1.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			webview1.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		
		// --- REKLAMA VA VIDEO UCHUN MUHIM QISMLAR ---
		webview1.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("http") || url.startsWith("https")) {
					return false; 
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		webview1.setWebChromeClient(new WebChromeClient());
		// --------------------------------------------
		
		// 3. Sardor Bridge (HTML bilan aloqa)
		webview1.addJavascriptInterface(new Object() {
			@android.webkit.JavascriptInterface
			public void open(final String v, final String n, final String r) {
				runOnUiThread(new Runnable() {
					@Override public void run() {
						try {
							Intent i = new Intent();
							i.setClass(getApplicationContext(), VideoActivity.class);
							i.putExtra("v_url", v);
							i.putExtra("v_nomi", n);
							i.putExtra("v_rasm", r);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
						} catch (Exception e) {
							toast("Xatolik: " + e.getMessage());
						}
					}
				});
			}
			
			@android.webkit.JavascriptInterface
			public void openUrl(final String url) {
				runOnUiThread(new Runnable() {
					@Override public void run() {
						if (url != null && !url.isEmpty()) {
							try {
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(url));
								i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(i);
							} catch (Exception e) {
								toast("Linkni ochib bo'lmadi");
							}
						}
					}
				});
			}
			
			@android.webkit.JavascriptInterface
			public void checkTG(final String link) {
				runOnUiThread(new Runnable() {
					@Override public void run() {
						if (link != null && link.startsWith("http")) {
							try {
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(link));
								i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(i);
							} catch (Exception e) {}
						}
					}
				});
			}
			
			@android.webkit.JavascriptInterface
			public void toast(final String msg) {
				runOnUiThread(new Runnable() {
					@Override public void run() {
						android.widget.Toast.makeText(getApplicationContext(), msg, android.widget.Toast.LENGTH_SHORT).show();
					}
				});
			}
		}, "Sardor");
		
		// 4. Asosiy sahifani yuklash
		webview1.loadUrl("file:///android_asset/index.html");
		
		// 5. AdView Banner yuklash
		com.google.android.gms.ads.AdView adview1 = (com.google.android.gms.ads.AdView) findViewById(R.id.adview1);
		com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
		adview1.loadAd(adRequest);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (binding.adview1 != null) {
			binding.adview1.destroy();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (binding.adview1 != null) {
			binding.adview1.pause();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (binding.adview1 != null) {
			binding.adview1.resume();
		}
	}
}