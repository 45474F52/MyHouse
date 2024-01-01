package com.aes.myhome.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aes.myhome.IItemClickListener
import com.aes.myhome.R
import com.aes.myhome.storage.database.entities.ScheduleEvent

class ScheduleEventAdapter(
    private val list: List<ScheduleEvent>,
    private val listener: IItemClickListener,
    private val getColor: (String) -> Int
) : RecyclerView.Adapter<ScheduleEventAdapter.ViewHolder>() {

    inner class ViewHolder(
        eventView: View
    ) : RecyclerView.ViewHolder(eventView), View.OnClickListener {

        val dayOfWeekText: TextView = eventView.findViewById(R.id.event_date_dayOfWeek)
        val numberOfDateText: TextView = eventView.findViewById(R.id.event_date_numberOfDate)
        val eventDataText: TextView = eventView.findViewById(R.id.event_data_text)
        val card: CardView = eventView.findViewById(R.id.event_card)

        init {
            eventView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val index = adapterPosition

            if (index != RecyclerView.NO_POSITION) {
                listener.onItemClick(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_shedule_event, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.dayOfWeekText.text = item.dayOfWeek
        holder.numberOfDateText.text = item.dayNumber.toString()
        holder.eventDataText.text = item.description
        holder.card.setCardBackgroundColor(getColor(item.category))
    }
}