package com.example.flashcardproto1

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class TestConfigDialogFragment : DialogFragment() {

    interface TestConfigListener {
        fun onTestConfigured(maxAttempts: Int, timeLimitMinutes: Int)
    }

    private var listener: TestConfigListener? = null

    private var attempts = 3
    private var timeLimit = 10
    private var noTimeLimit = false

    fun setListener(listener: TestConfigListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_test_config, null)

        val AttemptsText = view.findViewById<TextView>(R.id.attemptsText)
        val TimeText = view.findViewById<TextView>(R.id.timeText)
        val cbNoTimeLimit = view.findViewById<CheckBox>(R.id.cbNoTimeLimit)


        AttemptsText.text = attempts.toString()
        TimeText.text = timeLimit.toString()
        cbNoTimeLimit.isChecked = noTimeLimit


        cbNoTimeLimit.setOnCheckedChangeListener { _, isChecked ->
            noTimeLimit = isChecked
            view.findViewById<Button>(R.id.btnDecreaseTime).isEnabled = !isChecked
            view.findViewById<Button>(R.id.btnIncreaseTime).isEnabled = !isChecked
            TimeText.isEnabled = !isChecked
        }


        view.findViewById<Button>(R.id.btnDecreaseAttempts).setOnClickListener {
            if (attempts > 1) {
                attempts--
                AttemptsText.text = attempts.toString()
            }
        }

        view.findViewById<Button>(R.id.btnIncreaseAttempts).setOnClickListener {
            if (attempts < 10) {
                attempts++
                AttemptsText.text = attempts.toString()
            }
        }


        view.findViewById<Button>(R.id.btnDecreaseTime).setOnClickListener {
            if (timeLimit > 1) {
                timeLimit--
                TimeText.text = timeLimit.toString()
            }
        }

        view.findViewById<Button>(R.id.btnIncreaseTime).setOnClickListener {
            if (timeLimit < 60) {
                timeLimit++
                TimeText.text = timeLimit.toString()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Configure Test")
            .setPositiveButton("Start Test") { _, _ ->
                val finalTimeLimit = if (noTimeLimit) 0 else timeLimit
                listener?.onTestConfigured(attempts, finalTimeLimit)
            }
            .setNegativeButton("Cancel",){ _, _ ->
                dismiss()
                parentFragmentManager.popBackStack()
            }
            .create()
    }
}