package cc.opentv.api21;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/** API 21 TV shell for the ordinary official Douyin web experience. */
public final class MainActivity extends AppCompatActivity {
    private static final String HOME_URL = "https://www.douyin.com/";
    private static final long CHROME_AUTO_HIDE_MS = 4500L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private WebView webView;
    private LinearLayout chromeBar;
    private View loadingPanel;
    private ProgressBar progressBar;
    private View customVideoView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private long lastBackAt;

    private final Runnable hideChrome = new Runnable() {
        @Override public void run() {
            if (customVideoView == null && chromeBar != null) chromeBar.setVisibility(View.GONE);
        }
    };

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        configureWindow();
        setContentView(R.layout.activity_main);
        bindViews();
        configureControls();
        configureWebView();
        webView.loadUrl(HOME_URL);
        showChromeTemporarily();
    }

    private void configureWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        enterImmersiveMode();
    }

    private void enterImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void bindViews() {
        webView = findViewById(R.id.douyin_web_view);
        chromeBar = findViewById(R.id.chrome_bar);
        loadingPanel = findViewById(R.id.loading_panel);
        progressBar = findViewById(R.id.web_progress);
    }

    private void configureControls() {
        ((Button) findViewById(R.id.action_home)).setOnClickListener(v -> webView.loadUrl(HOME_URL));
        ((Button) findViewById(R.id.action_refresh)).setOnClickListener(v -> webView.reload());
        ((Button) findViewById(R.id.action_privacy)).setOnClickListener(v -> showPrivacyNotice());
        ((Button) findViewById(R.id.action_clear_session)).setOnClickListener(v -> confirmClearSession());
        ((Button) findViewById(R.id.action_exit)).setOnClickListener(v -> finish());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        CookieManager cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);
        cookies.setAcceptThirdPartyCookies(webView, true);
        webView.setBackgroundColor(Color.BLACK);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setWebViewClient(new TvWebClient());
        webView.setWebChromeClient(new TvChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
    }

    private final class TvWebClient extends WebViewClient {
        @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return !WebUrlPolicy.isSupportedWebUrl(url);
        }

        @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return request == null || !WebUrlPolicy.isSupportedWebUrl(request.getUrl().toString());
        }

        @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
            loadingPanel.setVisibility(View.VISIBLE);
            showChromeTemporarily();
        }

        @Override public void onPageFinished(WebView view, String url) {
            loadingPanel.setVisibility(View.GONE);
            CookieManager.getInstance().flush();
            webView.requestFocus();
        }

        @Override @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            showLoadError();
        }
    }

    private final class TvChromeClient extends WebChromeClient {
        @Override public void onProgressChanged(WebView view, int progress) {
            progressBar.setProgress(progress);
            progressBar.setVisibility(progress >= 100 ? View.GONE : View.VISIBLE);
        }

        @Override public boolean onConsoleMessage(ConsoleMessage message) { return true; }

        @Override public void onShowCustomView(View view, CustomViewCallback callback) {
            if (customVideoView != null) { callback.onCustomViewHidden(); return; }
            customVideoView = view;
            customViewCallback = callback;
            ((android.view.ViewGroup) getWindow().getDecorView()).addView(view,
                    new android.view.ViewGroup.LayoutParams(-1, -1));
            webView.setVisibility(View.GONE);
            chromeBar.setVisibility(View.GONE);
            enterImmersiveMode();
        }

        @Override public void onHideCustomView() { exitCustomVideoView(); }
    }

    private void exitCustomVideoView() {
        if (customVideoView == null) return;
        ((android.view.ViewGroup) getWindow().getDecorView()).removeView(customVideoView);
        customVideoView = null;
        if (customViewCallback != null) customViewCallback.onCustomViewHidden();
        customViewCallback = null;
        webView.setVisibility(View.VISIBLE);
        webView.requestFocus();
        showChromeTemporarily();
    }

    @Override public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && isHandledRemoteKey(event.getKeyCode())) return true;
        if (event.getAction() != KeyEvent.ACTION_DOWN) return super.dispatchKeyEvent(event);
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP: scrollPage(0, -0.72f); return true;
            case KeyEvent.KEYCODE_DPAD_DOWN: scrollPage(0, 0.72f); return true;
            case KeyEvent.KEYCODE_DPAD_LEFT: scrollPage(-0.32f, 0); return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT: scrollPage(0.32f, 0); return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: toggleVisibleVideo(); return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY: setVisibleVideoPlaying(true); return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE: setVisibleVideoPlaying(false); return true;
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_SETTINGS: toggleChrome(); return true;
            case KeyEvent.KEYCODE_BACK: return handleBack();
            default: return super.dispatchKeyEvent(event);
        }
    }

    private boolean isHandledRemoteKey(int code) {
        return code == KeyEvent.KEYCODE_DPAD_UP || code == KeyEvent.KEYCODE_DPAD_DOWN
                || code == KeyEvent.KEYCODE_DPAD_LEFT || code == KeyEvent.KEYCODE_DPAD_RIGHT
                || code == KeyEvent.KEYCODE_DPAD_CENTER || code == KeyEvent.KEYCODE_ENTER
                || code == KeyEvent.KEYCODE_MENU || code == KeyEvent.KEYCODE_SETTINGS
                || code == KeyEvent.KEYCODE_BACK || code == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || code == KeyEvent.KEYCODE_MEDIA_PLAY || code == KeyEvent.KEYCODE_MEDIA_PAUSE;
    }

    private boolean handleBack() {
        if (customVideoView != null) { exitCustomVideoView(); return true; }
        if (chromeBar.getVisibility() != View.VISIBLE) { showChromeTemporarily(); return true; }
        long now = System.currentTimeMillis();
        if (now - lastBackAt < 1500L) finish();
        else { lastBackAt = now; Toast.makeText(this, R.string.exit_hint, Toast.LENGTH_SHORT).show(); }
        return true;
    }

    private void toggleChrome() {
        if (chromeBar.getVisibility() == View.VISIBLE) {
            chromeBar.setVisibility(View.GONE);
            handler.removeCallbacks(hideChrome);
        } else showChromeTemporarily();
    }

    private void showChromeTemporarily() {
        chromeBar.setVisibility(View.VISIBLE);
        handler.removeCallbacks(hideChrome);
        handler.postDelayed(hideChrome, CHROME_AUTO_HIDE_MS);
    }

    private void scrollPage(float horizontalFactor, float verticalFactor) {
        int dx = (int) (webView.getWidth() * horizontalFactor);
        int dy = (int) (webView.getHeight() * verticalFactor);
        webView.evaluateJavascript("window.scrollBy({left:" + dx + ",top:" + dy + ",behavior:'smooth'});", null);
        showChromeTemporarily();
    }

    private void toggleVisibleVideo() {
        webView.evaluateJavascript(primaryVideoScript("if(v.paused){v.play();}else{v.pause();}"), null);
    }

    private void setVisibleVideoPlaying(boolean play) {
        webView.evaluateJavascript(primaryVideoScript(play ? "v.play();" : "v.pause();"), null);
    }

    private String primaryVideoScript(String action) {
        return "(function(){var a=document.querySelectorAll('video'),v=null,b=0;for(var i=0;i<a.length;i++){var r=a[i].getBoundingClientRect(),w=Math.max(0,Math.min(r.right,innerWidth)-Math.max(r.left,0)),h=Math.max(0,Math.min(r.bottom,innerHeight)-Math.max(r.top,0));if(w*h>b){b=w*h;v=a[i];}}if(v){" + action + "}})();";
    }

    private void showPrivacyNotice() {
        new AlertDialog.Builder(this).setTitle(R.string.privacy_title).setMessage(R.string.privacy_body)
                .setPositiveButton(android.R.string.ok, null).show();
    }

    private void confirmClearSession() {
        new AlertDialog.Builder(this).setTitle(R.string.clear_session_title).setMessage(R.string.clear_session_body)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.clear_session_action, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
                            @Override public void onReceiveValue(Boolean value) {
                                webView.clearCache(true); webView.clearHistory(); CookieManager.getInstance().flush();
                                webView.loadUrl(HOME_URL);
                                Toast.makeText(MainActivity.this, R.string.session_cleared, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).show();
    }

    private void showLoadError() {
        loadingPanel.setVisibility(View.GONE);
        Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show();
        showChromeTemporarily();
    }

    @Override protected void onResume() { super.onResume(); enterImmersiveMode(); if (webView != null) webView.onResume(); }
    @Override protected void onPause() { if (webView != null) webView.onPause(); super.onPause(); }
    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (webView != null) { webView.stopLoading(); webView.loadUrl("about:blank"); webView.destroy(); }
        super.onDestroy();
    }
}
