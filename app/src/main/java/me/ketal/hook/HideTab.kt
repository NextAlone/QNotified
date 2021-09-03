/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */

package me.ketal.hook

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TabHost
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.plusAssign
import ltd.nextalone.util.*
import me.ketal.util.findViewByType
import me.singleneuron.qn_kernel.data.isTim
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook

@SuppressLint("StaticFieldLeak")
@FunctionEntry
object HideTab : CommonDelayableHook("ketal_HideTab") {
    private lateinit var tab: TabHost

    override fun isValid() = !isTim()

    override fun initOnce() = tryOrFalse {
        val clazz= "com.tencent.mobileqq.widget.QQTabHost".clazz
            ?: return@tryOrFalse
        for (m in clazz.declaredMethods) {
            if (m.name == "setOnTabSelectionListener") {
                m.hookBefore(this) {
                    tab = it.thisObject as TabHost
                    val blur = tab.findViewByType("com.tencent.mobileqq.widget.QQBlurView".clazz!!) as View
                    tab.tabWidget.isVisible = !isEnabled
                    blur.hide()
                }
            }
        }
    }

    private fun addSettingItem(linearLayout: LinearLayout, resName: String, label: String, clickListener: View.OnClickListener) {
        val ctx = linearLayout.context
        val view = View.inflate(ctx, ctx.hostLayout("b2g")!!, null) as LinearLayout
        val imgView = view[0] as ImageView
        val textView = view[1] as TextView
        imgView.setImageResource(ctx.hostDrawable(resName)!!)
        textView.text = label
        view.setOnClickListener(clickListener)
        linearLayout += view
    }
}

