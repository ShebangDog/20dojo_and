package jp.co.cyberagent.dojo2020.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import jp.co.cyberagent.dojo2020.data.model.UserData

class ProfileAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var userDataList: List<UserData> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class RecyclerViewHolder(
        private val binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private fun setUserData() {

        }
    }

    override fun getItemViewType(position: Int): Int {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}