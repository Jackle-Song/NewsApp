package com.mrsworkshop.newsapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mrsworkshop.newsapp.databinding.FragmentSearchNewsBinding

class SearchHistoryFragment : BaseFragment() {
    private lateinit var binding : FragmentSearchNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)

        initUI()
        setupComponentListener()

        return binding.root
    }

    /**
     * private function
     */

    private fun initUI() {

    }

    private fun setupComponentListener() {
        binding.layoutSearchFragment.setOnClickListener {
            binding.etSearchEditText.clearFocus()
            dismissKeyBoard()
        }

        binding.etSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.txtSearchText.visibility = View.VISIBLE
                }
                else {
                    binding.txtSearchText.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed
            }
        })

        binding.txtSearchText.setOnClickListener {

        }
    }
}