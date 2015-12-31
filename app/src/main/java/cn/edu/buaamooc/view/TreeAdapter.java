package cn.edu.buaamooc.view;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.nmbb.oplayer.ui.player.VideoActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.buaamooc.R;
import cn.edu.buaamooc.db.CommonDBOpenHelper;

public class TreeAdapter extends BaseAdapter {

	private Context context;
	/** 当前需要显示的数据 */
	private List<Node> mDataList;
	/** 节点点击事件 */
	NodeOnClickListener mNodeOnClickListener;
	private SQLiteDatabase db;

	public static ViewHolder current_holder;

	public static HashMap<String,ViewHolder> holders=new HashMap<>();

	public TreeAdapter(Context context, List<Node> objects) {
		this.context = context;
		this.mDataList = objects;
		holders.clear();
		db =new CommonDBOpenHelper(context,"cdb",null,1).getWritableDatabase();
		mNodeOnClickListener = new NodeOnClickListener();
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Node getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.listview_item_course_detail, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.label = (TextView) convertView.findViewById(R.id.label);
			holder.word=(TextView) convertView.findViewById(R.id.word);
			holder.progress=(TextView) convertView.findViewById(R.id.progress);
			holder.download=(ImageView) convertView.findViewById(R.id.download);
			holder.condition=(TextView) convertView.findViewById(R.id.condition);
			convertView.setTag(holder);
			convertView.setOnClickListener(mNodeOnClickListener);
			final ViewHolder holder1=holder;
			holder.download.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String url=((Node)v.getTag()).getUrl();
					Uri uri = Uri.parse(url);
					DownloadManager manager=(DownloadManager)(context.getSystemService(Context.DOWNLOAD_SERVICE));
					DownloadManager.Request request = new DownloadManager.Request(uri);
					request.setDestinationInExternalPublicDir("/download/", url.substring(25));
					request.setVisibleInDownloadsUi(true);
					manager.enqueue(request);
					holder1.condition.setText("正在下载");
					holder1.condition.setVisibility(View.VISIBLE);
					holder1.download.setVisibility(View.GONE);
					holders.put(uri.toString(),holder1);
				}
			});
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Node node = getItem(position);
		// 设置深度,层级越深距离左边越远
		convertView.setPadding(30 * node.getDeepLevel() + 10, 5, 5, 5);
		if (node.getChildCount() == 0) {// 如果沒有子节点说明为叶子节点,去掉icon
			holder.icon.setVisibility(View.INVISIBLE);
			holder.word.setVisibility(View.VISIBLE);
			holder.progress.setVisibility(View.VISIBLE);
			if(node.getUrl()!=null&&!node.getUrl().equals("")) {
				Cursor c = db.rawQuery("select percent,path from course_structure where url=? or path=?", new String[]{node.getUrl(),node.getUrl()});
				if (c.getCount() > 0) {
					c.moveToFirst();
					holder.progress.setText(c.getString(0) + "%");
					if(c.getString(1)!=null&&!c.getString(1).equals(""))
					{
						holder.download.setVisibility(View.GONE);
						holder.condition.setText("已下载");
						holder.condition.setVisibility(View.VISIBLE);
					}
					else if(holder.condition.getText().equals("正在下载"))
					{
						holder.download.setVisibility(View.GONE);
						holder.condition.setVisibility(View.VISIBLE);
					}
					else
					{
						holder.download.setVisibility(View.VISIBLE);
						holder.condition.setVisibility(View.GONE);
					}
				}
				c.close();
			}
			else {
				holder.progress.setText("100%");
				holder.condition.setVisibility(View.GONE);
				holder.download.setVisibility(View.GONE);
			}
		} else {
			holder.icon.setVisibility(View.VISIBLE);
			holder.word.setVisibility(View.GONE);
			holder.progress.setVisibility(View.GONE);
			holder.download.setVisibility(View.GONE);
			holder.condition.setVisibility(View.GONE);
			if (node.isExpand()) {//如果需要显示判断一下是否是需要显示展开的样式
				holder.icon.setImageResource(R.drawable.expand);
			} else {
				holder.icon.setImageResource(R.drawable.unexpand);
			}
		}
		holder.label.setTag(node);//label的tag里面存放Node,方便点击事件处理
		holder.download.setTag(node);
		holder.label.setText(node.getLabel());

		return convertView;
	}

	/** 目录点击事件 */
	class NodeOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			ViewHolder holder = (ViewHolder) v.getTag();
			current_holder=holder;
			Node node = (Node) holder.label.getTag();
			// 如果存在孩子节点就需要做展开或者隐藏操作
			if (node.getChildCount() > 0) {
				TreeUtils.filterNodeList(mDataList, node);
			} else {
				Intent intent = new Intent(context, VideoActivity.class);
				String url_t=node.getUrl();
				if(url_t!=null&&!url_t.equals("")) {
					intent.setData(Uri.parse(node.getUrl()));
					intent.putExtra("displayName", node.getLabel());
					context.startActivity(intent);
				}
				else {
					Toast.makeText(context,"对不起，此视频不存在。",Toast.LENGTH_SHORT).show();
				}
				return;
			}
			notifyDataSetChanged();
		}
	}
}
