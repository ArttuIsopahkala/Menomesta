package com.ardeapps.menomesta.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.utils.DateUtil;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.utils.StringUtils;

import java.util.Calendar;

/**
 * Created by Arttu on 23.2.2018.
 */

public class BirthdayPicker extends LinearLayout {
    DateEditText dayText;
    DateEditText monthText;
    DateEditText yearText;
    Calendar birthday;

    public BirthdayPicker(Context context) {
        super(context);
        createView(context);
    }

    public BirthdayPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.birthday_picker, this);
        dayText = (DateEditText) findViewById(R.id.dayText);
        monthText = (DateEditText) findViewById(R.id.monthText);
        yearText = (DateEditText) findViewById(R.id.yearText);
        clearFocus();
    }

    private void setTextListeners() {
        // ON FOCUS CHANGE
        dayText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    formatDayText();
                }
            }
        });
        monthText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    formatMonthText();
                }
            }
        });
        yearText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    formatYearText();
                }
            }
        });
        // ON TEXT CHANGE
        dayText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String day = s.toString();
                if (!StringUtils.isEmptyString(day)) {
                    int value = Integer.parseInt(day);
                    if (count == 1 && value > 3) {
                        setDayText(value);
                    }
                    if (count == 2) {
                        monthText.setFocusableInTouchMode(true);
                        monthText.requestFocus();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        monthText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String month = s.toString();
                if (!StringUtils.isEmptyString(month)) {
                    int value = Integer.parseInt(month);
                    if (count == 1 && value > 1) {
                        setMonthText(value);
                    }
                    if (count == 2) {
                        yearText.setFocusableInTouchMode(true);
                        yearText.requestFocus();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        // IME
        dayText.setKeyImeChangeListener(new DateEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    formatDayText();
                    dayText.clearFocus();
                    Helper.hideKeyBoard(dayText);
                }
            }
        });
        monthText.setKeyImeChangeListener(new DateEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    formatMonthText();
                    monthText.clearFocus();
                    Helper.hideKeyBoard(monthText);
                }
            }
        });
        yearText.setKeyImeChangeListener(new DateEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    formatYearText();
                    yearText.clearFocus();
                    Helper.hideKeyBoard(yearText);
                }
            }
        });
        // EDITOR
        dayText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    formatDayText();
                }
                return false;
            }
        });
        monthText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    formatMonthText();
                }
                return false;
            }
        });
        yearText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    formatYearText();
                    yearText.clearFocus();
                    Helper.hideKeyBoard(yearText);
                }
                return false;
            }
        });

    }

    private void formatDayText() {
        int day = birthday.get(Calendar.DAY_OF_MONTH);
        String dayString = dayText.getText().toString();
        if (!StringUtils.isEmptyString(dayString)) {
            day = Integer.parseInt(dayText.getText().toString());
            if (day < 1 || day > 31) {
                day = birthday.get(Calendar.DAY_OF_MONTH);
            }
        }
        setDayText(day);
    }

    private void formatMonthText() {
        int month = birthday.get(Calendar.MONTH) + 1;
        String monthString = monthText.getText().toString();
        if (!StringUtils.isEmptyString(monthString)) {
            month = Integer.parseInt(monthText.getText().toString());
            if (month < 1 || month > 12) {
                month = birthday.get(Calendar.MONTH) + 1;
            }
        }
        setMonthText(month);
    }

    public void formatYearText() {
        int year = birthday.get(Calendar.YEAR);
        String yearString = yearText.getText().toString();
        if (!StringUtils.isEmptyString(yearString)) {
            year = Integer.parseInt(yearText.getText().toString());
            if (year < DateUtil.getEarliestValidYear() || year > DateUtil.getLatestValidYear()) {
                year = birthday.get(Calendar.YEAR);
            }
        }
        setYearText(year);
    }

    private void setDayText(int day) {
        String dayString = (day < 10 ? "0" : "") + day;
        dayText.setText(dayString);
    }

    private void setMonthText(int month) {
        String monthString = (month < 10 ? "0" : "") + month;
        monthText.setText(monthString);
    }

    private void setYearText(int year) {
        yearText.setText(String.valueOf(year));
    }

    public Calendar getDate() {
        try {
            int day = Integer.parseInt(dayText.getText().toString());
            int month = Integer.parseInt(monthText.getText().toString());
            int year = Integer.parseInt(yearText.getText().toString());
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year, month - 1, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);

            cal.getTime();
            return cal;
        } catch (Exception e) {
            return null;
        }
    }

    public void setDate(Calendar birthday) {
        this.birthday = birthday;
        int day = birthday.get(Calendar.DAY_OF_MONTH);
        int month = birthday.get(Calendar.MONTH) + 1;
        int year = birthday.get(Calendar.YEAR);
        setDayText(day);
        setMonthText(month);
        setYearText(year);
        setTextListeners();
    }
}
