package com.hoangdv.framework.fragments;

import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hoangdv.framework.R;
import com.hoangdv.framework.SlideActivity;
import com.hoangdv.framework.adapters.NavDrawerListAdapter;
import com.hoangdv.framework.models.NavDrawerItem;

public class VbookFragment extends Fragment {
	ListView listView;
	Vector<NavDrawerItem> items = new Vector<NavDrawerItem>();
	NavDrawerListAdapter adapter = null;

	@SuppressLint("NewApi") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_vbook, container,
				false);
		listView = (ListView) rootView.findViewById(R.id.listView_videos);
		for (int i = 0; i < 5; i++) {
			items.add(new NavDrawerItem("Video" + (i + 1), R.drawable.ic_tut,
					true, "10:30"));
		}
		adapter = new NavDrawerListAdapter(getActivity(), items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String url = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
				//HH
				//SlideActivity.playVideoMain(url, getActivity());
			}
		});
		return rootView;
	}
}
