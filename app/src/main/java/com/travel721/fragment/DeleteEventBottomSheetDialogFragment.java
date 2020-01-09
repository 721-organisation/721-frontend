package com.travel721.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;
import com.travel721.R;
import com.travel721.analytics.AnalyticsHelper;
import com.travel721.analytics.ReleaseAnalyticsEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.travel721.Constants.API_ROOT_URL;

public class DeleteEventBottomSheetDialogFragment extends
        RoundedBottomSheetDialogFragment {
    private String eventProfileId;
    private String eventName;
    private String accessToken;

    public static DeleteEventBottomSheetDialogFragment newInstance(String eventProfileId, String eventName, String accessToken) {
        DeleteEventBottomSheetDialogFragment deleteEventBottomSheetDialogFragment = new DeleteEventBottomSheetDialogFragment();
        deleteEventBottomSheetDialogFragment.eventProfileId = eventProfileId;
        deleteEventBottomSheetDialogFragment.eventName = eventName;
        deleteEventBottomSheetDialogFragment.accessToken = accessToken;
        return deleteEventBottomSheetDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_delete_bottom_sheet, null);
        TextView eventTitle = v.findViewById(R.id.delEventTitle);
        eventTitle.setText(eventName);
        Button cancel = v.findViewById(R.id.cancel_delete);
        cancel.setOnClickListener(view -> dismiss());
        Button delete = v.findViewById(R.id.proceed_delete);
        delete.setOnClickListener(view -> {
            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, API_ROOT_URL + "eventProfiles/" + eventProfileId + "?access_token=" + accessToken, response -> {
                AnalyticsHelper.logEvent(getContext(), ReleaseAnalyticsEvent.EVENT_DELETED, null);
                if (getContext() == null) return;
                Toast.makeText(getContext(), "Successfully deleted event " + eventName + ".", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, My721Fragment.newInstance(accessToken)).commit();
                dismiss();
            }, error -> {
                Toast.makeText(getContext(), "There was an error while deleting that event. Please try again later.", Toast.LENGTH_SHORT).show();
                dismiss();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put("accessToken", accessToken);
                    return map;
                }
            };
            queue.add(stringRequest);
            queue.start();

        });
        return v;
    }
}
