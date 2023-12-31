/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.mediapipe.examples.languagedetector

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.languagedetector.databinding.LanguageDetectedLanguageDetectorBinding
import com.google.mediapipe.tasks.text.languagedetector.LanguagePrediction

class ResultsAdapter : RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {

    private var resultsList: List<LanguagePrediction> = emptyList()

    inner class ViewHolder(private val binding: LanguageDetectedLanguageDetectorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label: String, score: Float) {
            with(binding) {
                result.text = binding.root.context.getString(
                    R.string.result_display_text,
                    label,
                    score
                ).replaceFirstChar { it.titlecase() }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateResult(results: List<LanguagePrediction>) {
        resultsList = results
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            LanguageDetectedLanguageDetectorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = resultsList[position]
        holder.bind(category.languageCode(), category.probability())
    }

    override fun getItemCount() = resultsList.size
}