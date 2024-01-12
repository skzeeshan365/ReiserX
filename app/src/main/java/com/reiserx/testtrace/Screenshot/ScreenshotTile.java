package com.reiserx.testtrace.Screenshot;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.reiserx.testtrace.R;
import com.reiserx.testtrace.Utilities.isAccessibilityEnabled;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ScreenshotTile extends TileService {

    @Override
    public void onTileAdded() {
        if (ScreenshotTile.this.getQsTile() != null) {
            Tile tile = ScreenshotTile.this.getQsTile();
            tile.setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_screenshot_white_24dp));
            tile.setLabel(getString(R.string.local_screenshot_label));
            tile.setState(Tile.STATE_INACTIVE);
            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled();
            if (isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class, this) && accessibilityService.instance != null) {
                accessibilityService.instance.closeNotifications();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> accessibilityService.instance.takeScreenshotsLocal(), 1000);
            } else {
                Toast.makeText(this, "Accessibility service is not enabled", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else
            Toast.makeText(this, "Feature only available for android 11+ devices", Toast.LENGTH_SHORT).show();
    }
}