package info.deepakom.deepakom.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

public class ProgressDialog {

    private MaterialDialog mDialog;
    private Context mContext;

    public ProgressDialog(Context context) {
        mContext = context;
        mDialog = new MaterialDialog.Builder(context).widgetColor(Color.RED).theme(Theme.LIGHT).
                dividerColor(Color.BLACK)
                .contentColor(Color.BLACK).cancelable(false)
                .progress(true, 0).build();
    }



    public void show(String message) {
        mDialog.setTitle("Title");
        mDialog.setMessage(message);
        mDialog.getProgressBar().setVisibility(View.VISIBLE);
        show();
    }

    public void changeToError(String message) {
        mDialog.getProgressBar().setVisibility(View.GONE);
        mDialog.setContent(message);
        handler.postDelayed(runnable, 3000);
    }

    public void changeMessage(String message) {
        mDialog.setContent(message);
    }


    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };

    public void dismiss() {
        if (!((Activity) mContext).isFinishing() && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void show() {
        if (!((Activity) mContext).isFinishing() && !mDialog.isShowing()) {

            mDialog.show();
        }
    }
}
