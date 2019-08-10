/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.samplestickerapp;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import com.example.samplestickerapp.databinding.ActivityStickerPackInfoBinding;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class StickerPackInfoActivity extends BaseActivity {

    private static final String TAG = "StickerPackInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStickerPackInfoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sticker_pack_info);

        final String trayIconUriString = getIntent().getStringExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_TRAY_ICON);
        final String website = getIntent().getStringExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_WEBSITE);
        final String email = getIntent().getStringExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_EMAIL);
        final String privacyPolicy = getIntent().getStringExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_PRIVACY_POLICY);
        final String licenseAgreement = getIntent().getStringExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_LICENSE_AGREEMENT);

        try {
            final InputStream inputStream = getContentResolver().openInputStream(Uri.parse(trayIconUriString));
            final BitmapDrawable trayDrawable = new BitmapDrawable(getResources(), inputStream);
            final Drawable emailDrawable = getDrawableForAllAPIs(R.drawable.sticker_3rdparty_email);
            trayDrawable.setBounds(new Rect(0, 0, emailDrawable.getIntrinsicWidth(), emailDrawable.getIntrinsicHeight()));
			if (Build.VERSION.SDK_INT > 17) {
                binding.trayIcon.setCompoundDrawablesRelative(trayDrawable, null, null, null);
            } else {
                if (ViewCompat.getLayoutDirection(trayIcon) == ViewCompat.LAYOUT_DIRECTION_LTR) {
                    binding.trayIcon.setCompoundDrawables(null, null, trayDrawable, null);
                } else {
                    binding.trayIcon.setCompoundDrawables(trayDrawable, null, null, null);
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "could not find the uri for the tray image:" + trayIconUriString);
        }

        setupTextView(website, binding.viewWebpage);

        if (TextUtils.isEmpty(email)) {
            binding.sendEmail.setVisibility(View.GONE);
        } else {
            binding.sendEmail.setOnClickListener(v -> launchEmailClient(email));
        }

        setupTextView(privacyPolicy, binding.privacyPolicy);

        setupTextView(licenseAgreement, binding.licenseAgreement);
    }

    private void setupTextView(String website, TextView textView) {
        if (TextUtils.isEmpty(website)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setOnClickListener(v -> launchWebpage(website));
        }
    }

    private void launchEmailClient(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.info_send_email_to_prompt)));
    }

    private void launchWebpage(String website) {
        Uri uri = Uri.parse(website);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private Drawable getDrawableForAllAPIs(@DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= 21) {
            return getDrawable(id);
        } else {
            return getResources().getDrawable(id);
        }
    }
}
