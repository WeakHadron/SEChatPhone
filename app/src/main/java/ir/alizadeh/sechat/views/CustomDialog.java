package ir.alizadeh.sechat.views;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import ir.alizadeh.sechat.R;

public class CustomDialog extends Dialog {

    Activity activity;
    Button btn;
    public CustomDialog(@NonNull Activity act) {
        super(act);
        this.activity = act;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
        setCancelable(false);
        btn = findViewById(R.id.dialog_cancel_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
