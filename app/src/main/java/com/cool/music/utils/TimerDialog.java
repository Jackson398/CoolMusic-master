package com.cool.music.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cool.music.R;
import com.cool.music.widget.Item;
import com.cool.music.widget.PickerView;

public class TimerDialog extends Dialog implements View.OnClickListener {

    public TimerDialog(Context context) {
        super(context);
    }

    public TimerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private String mValue;
        private String positiveButtonText;
        private String negativeButtonText;
        private View.OnClickListener positiveButtonClickListener;
        private View.OnClickListener negativeButtonClickListener;
        private View view;
        public TimerDialog dialog;
        public PickerView mPicker;

        public Builder(Context context) {
            dialog = new TimerDialog(context, R.style.TimerDialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.timer_dialog, null);
            dialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public Builder setPositiveButton(String positiveButtonText, View.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, View.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setTimeValue(String value) {
            this.mValue = value;
            return this;
        }

        private void initPickerView(PickerView view) {
          //todo
        }

        public TimerDialog create() {
            TextView positiveButton = (TextView) view.findViewById(R.id.positive_button);
            TextView negativeButton = (TextView) view.findViewById(R.id.negative_button);
            positiveButton.setText(positiveButtonText);
            positiveButton.setOnClickListener(positiveButtonClickListener);
            negativeButton.setText(negativeButtonText);
            negativeButton.setOnClickListener(negativeButtonClickListener);
            view.findViewById(R.id.timer_close_dialog).setOnClickListener(dialog);
            ((TextView)view.findViewById(R.id.dialog_title)).setText(R.string.timer_stop_time);
            mPicker = (PickerView) view.findViewById(R.id.timer_time_set);
            initPickerView(mPicker);
            mPicker.setItems(Item.sampleItems(), item -> mValue = item.getText());
            int index = PickerUtils.indexOf(Item.sampleItems(), new Item(mValue));
            mPicker.setSelectedItemPosition(index >= 0 ? index : 0);
            dialog.setContentView(view);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

        public String getTimeValue() {
            return mValue;
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
