package com.evansabohi.com.top.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class InputValidation {
    private Context context;

    /**
     * constructor
     *
     * @param context
     */
    public InputValidation(Context context) {
        this.context = context;
    }


    public boolean isInputEditTextFilled(TextInputEditText textInputEditText, AppCompatTextView textInputLayout, String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty()) {
            textInputLayout.setVisibility(View.VISIBLE);
            textInputLayout.setText(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }

        return true;
    }
    public boolean isInputEditTextDevice(TextInputEditText textInputEditText,TextInputEditText textInputNumber,TextView textInputLayout, String message) {
        String value1 = textInputEditText.getText().toString().trim();
        String value2 = textInputNumber.getText().toString().trim();
        if (value1.isEmpty()||value2.isEmpty()) {
            textInputLayout.setVisibility(View.VISIBLE);
            textInputLayout.setText(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }

        return true;
    }
    public boolean isInputEditTextDevice2(TextInputEditText textInputEditText,TextView textInputLayout, String message) {
        String value1 = textInputEditText.getText().toString().trim();
        if (value1.isEmpty()) {
            textInputLayout.setVisibility(View.VISIBLE);
            textInputLayout.setText(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }

        return true;
    }


    /**
     * method to check InputEditText has valid email .
     *
     * @param textInputEditText
     * @param textInputLayout
     * @param message
     * @return
     */
    public boolean isInputEditTextEmail(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {

            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextMatches(TextInputEditText textInputEditText1, TextInputEditText textInputEditText2, AppCompatTextView textInputLayout, String message) {
        String value1 = textInputEditText1.getText().toString().trim();
        String value2 = textInputEditText2.getText().toString().trim();
        if (!value1.contentEquals(value2)) {
            textInputLayout.setVisibility(View.VISIBLE);
            textInputLayout.setText(message);
            hideKeyboardFrom(textInputEditText2);
            return false;
        }
        return true;
    }

    /**
     * method to Hide keyboard
     *
     * @param view
     */
    private void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
