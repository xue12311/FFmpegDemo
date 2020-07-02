package com.zjx.ffmpegdemo


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.zjx.ffmpegdemo.model.CommandBean


/**
 * @author: AnJoiner
 * @datetime: 19-12-30
 */
class FFmpegCommandAdapter(list: List<CommandBean>) :
    RecyclerView.Adapter<FFmpegCommandAdapter.FFmpegCommandViewHolder>() {
    private val mList: List<CommandBean>
    private var mItemClickListener: ItemClickListener? = null

    init {
        mList = list
    }

    fun setItemClickListener(itemClickListener: ItemClickListener?) {
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FFmpegCommandViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_ffmpeg_command, parent, false)
        return FFmpegCommandViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FFmpegCommandViewHolder,
        position: Int
    ) {
        holder.mButton.setText(mList[position].name)
        holder.mButton.setOnClickListener {
            if (mItemClickListener != null) {
                mItemClickListener!!.itemClick(
                    mList[holder.adapterPosition].id,
                    mList[holder.adapterPosition].name
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    interface ItemClickListener {
        fun itemClick(id: Int, name: String)
    }


    inner class FFmpegCommandViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mButton: Button

        init {
            mButton = itemView.findViewById(R.id.btn_transform_audio)
        }
    }

}
