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

import android.graphics.Color
import android.view.View
import android.widget.Button
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.InvocationTargetException


class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName.equals("com.android.deskclock")) {
            try {
                hookClock(lpparam)
            } catch (e: Exception) {
                e.printStackTrace();
            }
        }
    }
    private fun getOnClickListener(view: View): View.OnClickListener? {
        val hasOnClick = view.hasOnClickListeners()
        if (hasOnClick) {
            try {
                val viewClazz = Class.forName("android.view.View")
                val listenerInfoMethod = viewClazz.getDeclaredMethod("getListenerInfo")
                if (!listenerInfoMethod.isAccessible) {
                    listenerInfoMethod.isAccessible = true
                }
                val listenerInfoObj = listenerInfoMethod.invoke(view)
                val listenerInfoClazz = Class.forName("android.view.View\$ListenerInfo")
                val onClickListenerField = listenerInfoClazz.getDeclaredField("mOnClickListener")
                if (!onClickListenerField.isAccessible) {
                    onClickListenerField.isAccessible = true
                }
                return onClickListenerField[listenerInfoObj] as View.OnClickListener
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }
        }
        return null
    }
    private fun hookClock(lpparam: XC_LoadPackage.LoadPackageParam) {
        val alarmClass = XposedHelpers.findClass(
            "com.android.deskclock.alarm.AlarmEditDialogView",
            lpparam.classLoader
        )
        XposedHelpers.findAndHookMethod(alarmClass, "initView", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                val me = param.thisObject
                val mBackgroundView : View = XposedHelpers.getObjectField(me, "mBackgroundView") as View
                val mSaveBtn : Button = XposedHelpers.getObjectField(me, "mSaveBtn") as Button
                val mBackgroundListener = getOnClickListener(mBackgroundView)
                val mSaveListener = getOnClickListener(mSaveBtn)
                mBackgroundView.setOnClickListener(mSaveListener)
                mSaveBtn.setOnClickListener(mBackgroundListener)
                mSaveBtn.setTextColor(Color.argb(150, 255, 0, 0))
            }
        })
    }
}