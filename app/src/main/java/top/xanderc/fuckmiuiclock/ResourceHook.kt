// Copyright (C) 2024, XanderC
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
package top.xanderc.fuckmiuiclock

import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import java.util.Locale

class ResourceHook : IXposedHookZygoteInit, IXposedHookInitPackageResources {

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {

    }

    @Throws(Throwable::class)
    override fun handleInitPackageResources(resparam: InitPackageResourcesParam) {
        // replacements only for SystemUI
        if (resparam.packageName != "com.android.deskclock") return
        val locale = Locale.getDefault()
        val languageTag = locale.toLanguageTag()
        XposedBridge.log("languageTag: $languageTag")
        var cancelString = "Cancel"
        if (languageTag.startsWith("zh")) {
            cancelString = "取消"
        }
        resparam.res.setReplacement("com.android.deskclock", "string", "set_alarm_done", cancelString)

    }
}