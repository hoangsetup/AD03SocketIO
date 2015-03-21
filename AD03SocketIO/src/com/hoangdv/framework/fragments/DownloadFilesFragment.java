package com.hoangdv.framework.fragments;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.hoangdv.framework.R;
import com.hoangdv.framework.adapters.NavDrawerListAdapter;
import com.hoangdv.framework.models.NavDrawerItem;

/**
 * @author Hoangdv
 * 
 */
@SuppressLint("NewApi") public class DownloadFilesFragment extends Fragment {
	ListView listView;
	NavDrawerListAdapter adapter = null;
	Vector<NavDrawerItem> items = new Vector<NavDrawerItem>();
	// Download file
	private static String file_url = "http://10.0.3.2/files/Mobile-PC-Laptop.pdf";
	ProgressDialog dialog;
	DownloadFileFromUrl downloadHelper = null;

	public DownloadFilesFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_downloadfile,
				container, false);
		listView = (ListView) rootView.findViewById(R.id.listView_files);
		adapter = new NavDrawerListAdapter(getActivity(), items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				downloadHelper = new DownloadFileFromUrl();
				downloadHelper.execute(file_url);
			}
		});
		new getListFiles().execute();
		return rootView;
	}

	public class getListFiles extends AsyncTask<Void, Integer, Void> {
		ProgressDialog dl;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dl = ProgressDialog.show(getActivity(), "Thông báo", "Loading...",
					true, false);
			dl.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			for (int i = 0; i < 5; i++) {
				publishProgress(i);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			Random random = new Random();
			int ic = (random.nextInt(100) % 2 == 0) ? R.drawable.ic_pdf
					: R.drawable.ic_zip;
			items.add(new NavDrawerItem("File " + (values[0] + 1), ic, true,
					random.nextInt(100) + "MB"));
			adapter.notifyDataSetChanged();
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dl != null) {
				dl.dismiss();
			}
		}
	}

	/**
	 * 
	 * @param show
	 */
	@SuppressWarnings("deprecation")
	public void toggleShowProgressDialog(boolean show) {
		if (show) {
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Downloading, please wait...");
			dialog.setIndeterminate(false);
			dialog.setMax(100);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setCancelable(true);
			dialog.setButton("Cancel", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					if (downloadHelper != null) {
						downloadHelper.cancel(true);
						Log.d("task download", "---------Cancel----");
					}
				}
			});
			dialog.show();
			return;
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	/**
	 * DownloadFile thread
	 */
	@SuppressLint("NewApi") class DownloadFileFromUrl extends AsyncTask<String, String, String> {
		private int lengthOfFile;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			toggleShowProgressDialog(true);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			int count;
			try {
				String link_file = params[0];
				URL url = new URL(link_file);
				URLConnection connection = url.openConnection();
				connection.connect();
				lengthOfFile = connection.getContentLength();
				InputStream inputStream = new BufferedInputStream(
						url.openStream(), 8192);
				File dir = new File(Environment.getExternalStorageDirectory()
						.getPath(), "AD03SocketIO");
				if (!dir.exists()) {
					dir.mkdir();
				}
				String file_name = link_file.split("/")[link_file.split("/").length - 1];
				File file = new File(dir, file_name);
				OutputStream outputStream = new FileOutputStream(file);
				byte[] data = new byte[1024];
				long total = 0;

				while ((count = inputStream.read(data)) != -1) {
					if(isCancelled()) 
						break;
					total += count;
					outputStream.write(data, 0, count);
					// publishProgress("" + (int) ((total * 100) /
					// lengthOfFile));
					publishProgress(total + "");
				}
				
				outputStream.flush();
				outputStream.close();
				inputStream.close();
				return "success";
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			long total = Long.parseLong(values[0]);
			dialog.setProgress((int) ((total * 100) / lengthOfFile));
			dialog.setMessage((total / (1024 * 1014)) + "MB/"
					+ (lengthOfFile / (1024 * 1024)) + "MB");
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				Toast.makeText(getActivity(), "Tai thanh cong!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), "That bai", Toast.LENGTH_SHORT)
						.show();
			}
			toggleShowProgressDialog(false);
		}
	}
}
