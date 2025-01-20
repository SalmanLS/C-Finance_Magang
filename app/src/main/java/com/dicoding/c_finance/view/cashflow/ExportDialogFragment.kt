package com.dicoding.c_finance.view.cashflow

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dicoding.c_finance.databinding.FragmentExportDialogBinding
import java.util.Calendar

class ExportDialogFragment : DialogFragment() {

    private var _binding: FragmentExportDialogBinding? = null
    private val binding get() = _binding!!

    interface ExportDialogListener {
        fun onExportClicked(startDate: String, endDate: String)
    }

    private var listener: ExportDialogListener? = null

    fun setExportDialogListener(listener: ExportDialogListener) {
        this.listener = listener
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edtDateStart.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.edtDateStart.setText(selectedDate)
            }
        }

        binding.edtDateEnd.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.edtDateEnd.setText(selectedDate)
            }
        }

        binding.exportButton.setOnClickListener {
            val startDate = binding.edtDateStart.text.toString()
            val endDate = binding.edtDateEnd.text.toString()
            if (startDate.isEmpty() || endDate.isEmpty()) {
                showToast("Please select both start and end dates")
            } else {
                listener?.onExportClicked(startDate, endDate)
                dismiss()
            }
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay
                )
                onDateSelected(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}