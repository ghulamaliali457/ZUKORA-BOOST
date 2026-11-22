package com.zukora.boost;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(5, 9, 20);
    private final int PANEL = Color.rgb(13, 22, 39);
    private final int BLUE = Color.rgb(56, 189, 248);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView ramValue, batteryValue, tempValue, statusValue, gamesText;
    private Runnable updater;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(26), dp(20), dp(24));
        scroll.addView(root);

        TextView brand = text("ZUKORA", 30, Color.WHITE, true);
        root.addView(brand);
        TextView sub = text("BOOST  •  GAMING CONTROL CENTER", 12, BLUE, true);
        root.addView(sub);
        root.addView(space(20));

        LinearLayout hero = panel();
        hero.addView(text("GAMING STATUS", 13, BLUE, true));
        statusValue = text("READY", 27, Color.WHITE, true);
        hero.addView(statusValue);
        TextView note = text("Your phone's performance depends on its hardware and the game settings.", 13, 0xFF9FB2C9, false);
        hero.addView(note);
        root.addView(hero);
        root.addView(space(14));

        LinearLayout metrics = new LinearLayout(this);
        metrics.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout left = metricCard("AVAILABLE RAM", "Reading…");
        LinearLayout right = metricCard("BATTERY", "Reading…");
        ramValue = (TextView) left.getChildAt(1);
        batteryValue = (TextView) right.getChildAt(1);
        LinearLayout.LayoutParams half = new LinearLayout.LayoutParams(0, -2, 1);
        half.setMargins(0, 0, dp(7), 0);
        metrics.addView(left, half);
        LinearLayout.LayoutParams half2 = new LinearLayout.LayoutParams(0, -2, 1);
        half2.setMargins(dp(7), 0, 0, 0);
        metrics.addView(right, half2);
        root.addView(metrics);
        root.addView(space(12));

        LinearLayout temp = panel();
        temp.addView(text("BATTERY TEMPERATURE", 12, BLUE, true));
        tempValue = text("Reading…", 22, Color.WHITE, true);
        temp.addView(tempValue);
        root.addView(temp);
        root.addView(space(18));

        Button boost = button("PREPARE GAMING");
        root.addView(boost);
        boost.setOnClickListener(v -> {
            updateStats();
            statusValue.setText("CHECK COMPLETE");
            Toast.makeText(this, "Stats refreshed. Close apps you are not using and use your game's supported graphics settings.", Toast.LENGTH_LONG).show();
        });
        root.addView(space(12));

        Button games = button("FIND INSTALLED GAMES");
        root.addView(games);
        gamesText = text("Tap above to view apps that Android exposes as launchable.", 13, 0xFF9FB2C9, false);
        root.addView(gamesText);
        games.setOnClickListener(v -> showGames());

        root.addView(space(18));
        TextView disclaimer = text("FPS NOTE\nThis app does not force higher FPS, change hidden system settings, or kill other apps. Android does not provide ordinary apps with reliable access to another game's live FPS. Use the game's own FPS/graphics options and keep the phone cool.", 12, 0xFF8295AD, false);
        root.addView(disclaimer);
        setContentView(scroll);

        updater = new Runnable() { @Override public void run() { updateStats(); handler.postDelayed(this, 5000); } };
        handler.post(updater);
    }

    private void updateStats() {
        try {
            ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            ramValue.setText(String.format(Locale.getDefault(), "%.1f GB", mi.availMem / 1073741824.0));
        } catch (Exception e) { ramValue.setText("Unavailable"); }

        try {
            Intent battery = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (battery != null) {
                int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                batteryValue.setText(level >= 0 ? (level * 100 / Math.max(scale, 1)) + "%" : "Unavailable");
                int tenths = battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                tempValue.setText(tenths >= 0 ? String.format(Locale.getDefault(), "%.1f °C", tenths / 10.0) : "Unavailable");
            }
        } catch (Exception e) { batteryValue.setText("Unavailable"); tempValue.setText("Unavailable"); }
    }

    private void showGames() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        StringBuilder b = new StringBuilder("LAUNCHABLE APPS\n");
        int count = 0;
        for (ResolveInfo r : list) {
            String label = r.loadLabel(getPackageManager()).toString();
            if (label.toLowerCase().matches(".*(game|pubg|free fire|minecraft|roblox|call of duty|cod).*")) {
                b.append("• ").append(label).append("\n");
                count++;
            }
        }
        if (count == 0) b.append("No obvious game names found. Your games may still be available in the app drawer.");
        gamesText.setText(b.toString());
    }

    private LinearLayout metricCard(String title, String value) {
        LinearLayout box = panel();
        box.addView(text(title, 11, BLUE, true));
        box.addView(text(value, 23, Color.WHITE, true));
        return box;
    }
    private LinearLayout panel() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(16), dp(16), dp(16), dp(16));
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(PANEL); d.setCornerRadius(dp(18)); d.setStroke(dp(1), 0xFF173B58);
        l.setBackground(d);
        return l;
    }
    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(null, android.graphics.Typeface.BOLD);
        t.setPadding(0, dp(3), 0, dp(3)); return t;
    }
    private Button button(String s) {
        Button b = new Button(this); b.setText(s); b.setTextColor(Color.BLACK); b.setTextSize(13);
        b.setAllCaps(false); b.setTypeface(null, android.graphics.Typeface.BOLD);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(BLUE));
        return b;
    }
    private View space(int h) { View v = new View(this); v.setLayoutParams(new LinearLayout.LayoutParams(1, dp(h))); return v; }
    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    @Override protected void onDestroy() { super.onDestroy(); if (updater != null) handler.removeCallbacks(updater); }
}
