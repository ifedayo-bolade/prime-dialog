package ifedayo.bolade.sample_kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SampleAdapter(var mainActivity: MainActivity, private val list: ArrayList<String>) :
    RecyclerView.Adapter<SampleAdapter.SampleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sample_adapter_layout, null, false)
        return SampleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.textView.text = list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class SampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.textView)

        init {
            itemView.setOnClickListener { _: View? ->
                val position = getBindingAdapterPosition()
                when (position) {
                    0 -> mainActivity.demoSimpleDialog()
                    1 -> mainActivity.demoAccentColoredDialog()
                    2 -> mainActivity.demoMultiColoredDialog()
                    3 -> mainActivity.demoDimensionDialog()
                    4 -> mainActivity.demoHeaderDialog()
                    5 -> mainActivity.demoAnimatedDialog()
                    6 -> mainActivity.demoListenerDialog()
                    7 -> mainActivity.demoCustomDialog()
                    8 -> mainActivity.demoCustomDialog2()
                }
            }
        }
    }
}