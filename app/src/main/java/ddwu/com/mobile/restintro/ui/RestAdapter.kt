package ddwu.com.mobile.restintro.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.restintro.data.RestDto
import ddwu.com.mobile.restintro.databinding.ListItemBinding

class RestAdapter: RecyclerView.Adapter<RestAdapter.RestHolder>(){

    var restList: List<RestDto>? = null
    var itemClickListener: OnRestItemClickListener? = null
    var itemLongClickListener: OnRestItemLongClickListener? = null

    override fun getItemCount(): Int {
        return restList?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestHolder {
        val itemBinding = ListItemBinding.inflate( LayoutInflater.from(parent.context), parent, false)
        return RestHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RestHolder, position: Int) {
        val dto = restList?.get(position)
        if (dto != null) {
            holder.bind(dto)
        }
        holder.itemBinding.clItem.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }

        holder.itemBinding.clItem.setOnLongClickListener {
            itemLongClickListener?.onItemLongClick(position)
            true
        }
    }

    class RestHolder(val itemBinding: ListItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(dto: RestDto) {
            itemBinding.tvData.text = dto.toString()
        }
    }

    interface OnRestItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnRestItemClickListener) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnRestItemLongClickListener) {
        itemLongClickListener = listener
    }

    interface OnRestItemLongClickListener {
        fun onItemLongClick(position: Int)
    }
}