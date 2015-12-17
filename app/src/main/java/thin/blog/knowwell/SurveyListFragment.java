package thin.blog.knowwell;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import adapters.SurveyListAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import datasets.Survey;
import network.CustomRequest;
import network.VolleySingleton;

public class SurveyListFragment extends Fragment {
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.pull_to_refresh)
    TextView pullToRefreshMessage;
    String email;
    int serverSuccess;
    int requestCode;
    String serverMessage;
    LinkedList<Survey> data = new LinkedList<>();
    SurveyListAdapter surveyListAdapter;
    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;
    private OnFragmentInteractionListener mListener;

    public SurveyListFragment() {
    }

    public static SurveyListFragment newInstance(int requestCode) {
        SurveyListFragment fragment = new SurveyListFragment();
        Bundle args = new Bundle();
        args.putInt("REQUEST_CODE", requestCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.requestCode = bundle.getInt("REQUEST_CODE");
            Toast.makeText(getContext(), "Request Code: " + this.requestCode, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_survey_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFS_USER_DATA, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(Constants.USER_DATA_EMAIL, "email");
        super.onActivityCreated(savedInstanceState);
        surveyListAdapter = new SurveyListAdapter(data);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshSureys();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setAdapter(surveyListAdapter);
        refreshSureys();
    }

    private void refreshSureys() {
        swipeRefreshLayout.setRefreshing(true);
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Map<String, String> formData = new HashMap<>();
        
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.requestCode = bundle.getInt("REQUEST_CODE");
            Toast.makeText(getContext(), "Request Code: " + this.requestCode, Toast.LENGTH_LONG).show();
        }

        formData.put("email", email);
        formData.put("request_code", String.valueOf(requestCode));
        Toast.makeText(getContext(), "Request Code in Volley :" + String.valueOf(requestCode), Toast.LENGTH_LONG).show();

        final CustomRequest request = new CustomRequest(Request.Method.POST, Constants.SURVEY_LIST, formData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonParser(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(swipeRefreshLayout, "Network Error", Snackbar.LENGTH_LONG).show();
            }
        });
        request.setTag(Constants.SURVEY_LIST);
        requestQueue.add(request);
    }

    private void jsonParser(JSONObject response) {
        try {
            serverSuccess = response.getInt("success");
            if (serverSuccess == 1) {
                JSONArray surveyArray = response.getJSONArray("survey");
                for (int i = 0; i < surveyArray.length(); i++) {
                    JSONObject object = surveyArray.getJSONObject(i);
                    int surveyId = Integer.parseInt(object.getString("id"));
                    String title = object.getString("title");
                    String organization = object.getString("organization");
                    Survey survey = new Survey(surveyId, title, organization);
                    if (!data.contains(survey)) {
                        data.add(0, survey);
                    }
                }
                surveyListAdapter.notifyDataSetChanged();
                notifyChangesToView();
            } else {
                serverMessage = response.getString("message");
                Snackbar.make(recyclerView, serverMessage, Snackbar.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void notifyChangesToView() {
        swipeRefreshLayout.setRefreshing(false);
        if (data.size() == 0) {
            pullToRefreshMessage.setText("No Surveys To Display\nPull Down to Refresh");
        } else {
            pullToRefreshMessage.setText("Pull Down To Refresh");
        }

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
