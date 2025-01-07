package com.dicoding.c_finance.view.users

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.dicoding.c_finance.R
import com.dicoding.c_finance.databinding.FragmentUserDetailDialogBinding
import com.dicoding.c_finance.model.response.user.UsersItem

class UserDetailDialogFragment : DialogFragment() {
    private var _binding: FragmentUserDetailDialogBinding? = null
    private val binding get() = _binding!!
    private var onEditListener: ((UsersItem) -> Unit)? = null
    private var onDeleteListener: ((UsersItem) -> Unit)? = null

    companion object {
        private const val ARG_USER = "user"

        fun newInstance(user: UsersItem): UserDetailDialogFragment {
            val args = Bundle()
            args.putParcelable(ARG_USER, user)
            val fragment = UserDetailDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentUserDetailDialogBinding.inflate(layoutInflater)
        val user = arguments?.getParcelable<UsersItem>(ARG_USER)

        binding.tvUsername.text = getString(R.string.username_detail, user?.username)
        binding.tvPhone.text = getString(R.string.phone_detail, user?.noHp)
        binding.tvRole.text = getString(R.string.role_detail, getRoleName(user?.idRole))

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Edit") { _, _ ->
                user?.let { onEditListener?.invoke(it) }
            }
            .setNegativeButton("Delete") { _, _ ->
                user?.let { onDeleteListener?.invoke(it) }
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    fun setOnEditListener(listener: (UsersItem) -> Unit) {
        onEditListener = listener
    }

    fun setOnDeleteListener(listener: (UsersItem) -> Unit) {
        onDeleteListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getRoleName(roleId: String?): String {
        return when (roleId) {
            "1" -> "Admin"
            "2" -> "User"
            else -> "Unknown Role"
        }
    }
}
