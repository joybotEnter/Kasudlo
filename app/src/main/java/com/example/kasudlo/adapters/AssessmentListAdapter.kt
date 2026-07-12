package com.example.kasudlo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kasudlo.databinding.ItemAssessmentBinding

class AssessmentListAdapter(
    private val list: ArrayList<HashMap<String, Any>>,
    private val onEdit: (String) -> Unit,
    private val onDelete: (String, Int) -> Unit = { _, _ -> },
    private val showEdit: Boolean = true,
    private val showDelete: Boolean = false
) : RecyclerView.Adapter<AssessmentListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAssessmentBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAssessmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item  = list[position]
        val docId = item["docId"] as? String ?: ""

        val headName  = (item["member1Name"] as? String)?.ifBlank { null }
            ?: (item["informant"] as? String)?.ifBlank { null }
            ?: "Unnamed Household"
        val address   = (item["address"]        as? String) ?: "—"
        val controlNo = (item["controlNo"]      as? String) ?: "—"
        val visitDate = (item["dateFirstVisit"] as? String) ?: "—"

        holder.binding.tvAssessmentName.text    = headName
        holder.binding.tvAssessmentAddress.text = address
        holder.binding.tvControlNo.text         = "Control #$controlNo"
        holder.binding.tvVisitDate.text         = "1st visit: $visitDate"

        holder.binding.btnEdit.visibility   = if (showEdit)   View.VISIBLE else View.GONE
        holder.binding.btnDelete.visibility = if (showDelete) View.VISIBLE else View.GONE

        holder.binding.root.setOnClickListener    { onEdit(docId) }
        holder.binding.btnEdit.setOnClickListener { onEdit(docId) }
        holder.binding.btnDelete.setOnClickListener {
            onDelete(docId, holder.bindingAdapterPosition)
        }
    }
}