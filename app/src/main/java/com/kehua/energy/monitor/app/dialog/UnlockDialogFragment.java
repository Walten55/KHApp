package com.kehua.energy.monitor.app.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.walten.fastgo.common.Fastgo;

public class UnlockDialogFragment extends DialogFragment
{

    @BindView(R.id.et_password)
    EditText mPwdEt;

    @BindView(R.id.tv_message)
    TextView mMessageTv;

    @BindView(R.id.tv_submit)
    RoundTextView mSubmitTv;

    private String hint;

    private boolean canSubmit;

    private UnlockDialogFragment.OnEditPwdDialogFragmentListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View inflate = View.inflate(Fastgo.getContext(), R.layout.dialog_unlock, null);
        ButterKnife.bind(this,inflate);
        init();
        return inflate;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

    public void init() {
        mPwdEt.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mMessageTv.setText(getString(R.string.串号)+":"+ LocalUserManager.getSn());
        mSubmitTv.setTextColor(ContextCompat.getColor(Fastgo.getContext(),R.color.text_gray));
        mPwdEt.setHint(hint);
        mPwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()==6){
                    mSubmitTv.setTextColor(ContextCompat.getColor(Fastgo.getContext(),R.color.white));
                    canSubmit = true;
                }else {
                    mSubmitTv.setTextColor(ContextCompat.getColor(Fastgo.getContext(),R.color.text_gray));
                    canSubmit = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @OnClick(R.id.tv_submit)
    public void submit(View view){
        if(canSubmit){
            if(listener!=null)
                listener.onSubmit(mPwdEt.getText().toString());
        }
    }

    public void show(FragmentManager manager, String tag, String hint,UnlockDialogFragment.OnEditPwdDialogFragmentListener listener) {
        super.show(manager, tag);
        this.hint = hint;
        this.listener = listener;
    }

    public interface OnEditPwdDialogFragmentListener{
        void onSubmit(String msg);
    }
    
}  