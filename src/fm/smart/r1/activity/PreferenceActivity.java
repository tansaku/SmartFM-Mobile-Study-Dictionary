/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fm.smart.r1.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceScreen;
import fm.smart.r1.Main;
import fm.smart.r1.R;

public class PreferenceActivity extends android.preference.PreferenceActivity
    implements OnSharedPreferenceChangeListener {

  static final String SEARCH_LANG = "preferences_search_lang";
  static final String RESULT_LANG = "preferences_result_lang";

  EditTextPreference search_lang;
  EditTextPreference result_lang;

  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    addPreferencesFromResource(R.xml.preferences);

    PreferenceScreen preferences = getPreferenceScreen();
    preferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    search_lang = (EditTextPreference) preferences.findPreference(SEARCH_LANG);
    result_lang = (EditTextPreference) preferences.findPreference(RESULT_LANG);
   }

  // Prevent the user from turning off both decode options
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(SEARCH_LANG)) {
      Main.search_lang = search_lang.getEditText().getText().toString();
    }else if (key.equals(RESULT_LANG)) {
      Main.result_lang = result_lang.getEditText().getText().toString();
    }
  }

}
