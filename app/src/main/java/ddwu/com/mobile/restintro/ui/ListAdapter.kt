package ddwu.com.mobile.restintro.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.restintro.data.ListLayout
import ddwu.com.mobile.restintro.R

class ListAdapter(val itemList: ArrayList<ListLayout>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.info.text = itemList[position].road
        holder.address.text = itemList[position].address
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_list_name)
        val info: TextView = itemView.findViewById(R.id.tv_list_info)
        val address: TextView = itemView.findViewById(R.id.tv_list_address)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}