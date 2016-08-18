package com.justplay1.shoppist.view.fragments.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justplay1.shoppist.R;
import com.justplay1.shoppist.models.UnitViewModel;
import com.justplay1.shoppist.presenter.SelectUnitPresenter;
import com.justplay1.shoppist.view.SelectUnitView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Mkhytar on 06.02.2016.
 */
public class SelectUnitDialogFragment extends BaseSelectItemDialogFragment<UnitViewModel>
        implements SelectUnitView {

    @Inject
    SelectUnitPresenter mPresenter;

    public SelectUnitDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static SelectUnitDialogFragment newInstance() {
        return newInstance(null);
    }

    public static SelectUnitDialogFragment newInstance(UnitViewModel unit) {
        Bundle args = new Bundle();
        args.putParcelable(UnitViewModel.class.getName(), unit);
        SelectUnitDialogFragment fragment = new SelectUnitDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.onCreate(getArguments(), savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
        mPresenter.init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
        mPresenter.onDestroy();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_unit_dialog, container);
    }

    @Override
    public void init(View view) {
        super.init(view);
        mPositiveButton.setText(R.string.ok);
        getDialog().setTitle(R.string.change_unit);
    }

    @Override
    public void showUnitDialog(final UnitViewModel editUnit) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AddUnitsDialogFragment dialog = AddUnitsDialogFragment.newInstance(editUnit);
        dialog.setCompleteListener(isUpdate -> mPresenter.loadUnits());
        dialog.show(fm, AddUnitsDialogFragment.class.getName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_button:
                mPresenter.onAddUnitClick();
                break;
            case R.id.edit_button:
                mPresenter.onEditUnitClick(mSelectView.getSelectedItem());
                break;
            case R.id.positive_button:
                mPresenter.onPositiveButtonClick(mSelectView.getSelectedItem());
                break;
            case R.id.negative_button:
                mPresenter.onNegativeButtonClick();
                break;
        }
    }

    @Override
    public void setUnits(List<UnitViewModel> unit) {
        mSelectView.setData(unit);
    }

    @Override
    public void selectUnit(String id) {
        mSelectView.selectItem(id);
    }

    @Override
    public void onComplete(UnitViewModel unit, boolean isUpdate) {
        if (mCompleteListener != null) {
            mCompleteListener.onComplete(unit, isUpdate);
        }
    }

    @Override
    public void closeDialog() {
        dismiss();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String message) {

    }
}
