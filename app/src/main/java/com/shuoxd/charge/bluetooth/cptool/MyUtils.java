package com.shuoxd.charge.bluetooth.cptool;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Toast;

import com.shuoxd.charge.application.MainApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyUtils {

	public static void showToast(String s) {
		Toast.makeText(MainApplication.Companion.instance(), s, Toast.LENGTH_SHORT).show();
	}

	public static final InputFilter asciiFilter = new InputFilter() {
		final Pattern pattern = Pattern.compile(
				"[^ -~]",
				Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
								   int dstart, int dend) {
			Matcher matcher = pattern.matcher(source);
			if (matcher.find()) {
				return "";
			}
			return null;
		}
	};

	public static void addAsciiFilter(EditText editText) {
		InputFilter[] oldFilters = editText.getFilters();
		List<InputFilter> list = new ArrayList<>();
		list.add(asciiFilter);
		if (oldFilters != null) {
			list.addAll(Arrays.asList(oldFilters));
		}
		InputFilter[] newFilters = new InputFilter[list.size()];
		list.toArray(newFilters);
		editText.setFilters(newFilters);
	}

	public static String getVersionName(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static int getVersionCode(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
}
