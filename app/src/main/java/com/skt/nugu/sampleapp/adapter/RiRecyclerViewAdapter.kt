package com.skt.nugu.sampleapp.adapter

import android.app.DownloadManager
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skt.nugu.sampleapp.R

class RiRecyclerViewAdapter(
    private val context: Context,
    private val resolveInfoList: ArrayList<ArrayList<ResolveInfo>>,
    private val index: Int
) :
    RecyclerView.Adapter<RiRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RiRecyclerViewAdapter.ViewHolder {
        return RiRecyclerViewAdapter.ViewHolder(
            context,
            LayoutInflater.from(parent.context).inflate(R.layout.item_resolveinfo, parent, false),
            index
        )
    }

    override fun onBindViewHolder(holder: RiRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bind(resolveInfoList[position], context, index)
    }

    override fun getItemCount(): Int {
        return resolveInfoList.size
    }

    class ViewHolder(context: Context, itemView: View, index: Int) :
        RecyclerView.ViewHolder(itemView) {
        private val queryEditText = itemView.findViewById<EditText>(R.id.search_query_edit_text)

        fun bind(item: ArrayList<ResolveInfo>, context: Context, index: Int) {
            itemView.findViewById<ImageView>(R.id.resolve_info_icon)
                .setImageDrawable(item[0].loadIcon(context.packageManager))
            itemView.findViewById<TextView>(R.id.resolve_info_label).text =
                item[0].loadLabel(context.packageManager).toString()

            when (index) {
                3 -> {
                    queryEditText.apply {
                        visibility = View.VISIBLE
                    }
                    itemView.findViewById<Button>(R.id.launch_btn).setOnClickListener {
                        val intent: Intent = Intent(Intent.ACTION_WEB_SEARCH)
                        intent.setClassName(
                            item[0].activityInfo.packageName,
                            item[0].activityInfo.name
                        )
                        intent.putExtra(SearchManager.QUERY, queryEditText.text.toString())
                        context.startActivity(intent)
                    }
                    itemView.findViewById<Button>(R.id.launch_btn).text = "SEARCH"
                }
                else -> {
                    queryEditText.visibility = View.GONE
                    itemView.findViewById<Button>(R.id.launch_btn).setOnClickListener {
                        val intent: Intent = Intent(Intent.ACTION_MAIN)
                        intent.setClassName(
                            item[0].activityInfo.packageName,
                            item[0].activityInfo.name
                        )
                        context.startActivity(intent)
                    }
                }
            }

        }
    }
}