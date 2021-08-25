So I've restarted with gradle instead because the instructions from the original link seemed to have some heavy dependencies on things that aren't available in the versions Debian has packaged.
I think I've gotten further, but not quite there yet.

Mostly based on https://developer.okta.com/blog/2018/08/10/basic-android-without-an-ide
with some manual changes to account for version mismatches, again.

Dependencies
============
Debian bullseye::

    sudo apt purge openjdk-17-jdk-headless
    sudo apt install android-sdk android-sdk-platform-23 gradle openjdk-11-jdk-headless

Build process
=============

Build the unsigned/debug/dev pacakge::

    gradle build

Install it on a device::

    adb install ./app/build/outputs/apk/debug/app-debug.apk

FIXME: Sign the package for uploading to Google

FIXME: Upload it to Google


Handy one-liners during development
===================================
Build package, turn device screen on, install on device, unlock device screen, run app on device::

    gradle build && adb shell input keyevent 82 && adb install ./app/build/outputs/apk/debug/app-debug.apk && adb shell input keyevent 82 && sleep 1 && adb shell monkey -p com.prisonpc.cacertinstaller 1

Watch device log for useful things::

    adb logcat -T 0 | grep --line-buffered -i -e admin -e prisonpc

Enable TestDPC as the device policy controller so it can enable the CERT_INSTALL delegation.
I couldn't find a way to do just the CERT_INSTALL delegation automatically::

    adb shell dpm set-device-owner com.afwsamples.testdpc/.DeviceAdminReceiver


Future Improvements
===================
* General clean up of the ugly code

* Make it run automatically once installed, or on boot.
  Android doesn't seem to allow apps to respond to an intent until it's been manually run once by the user.
  I can see good security reasons for this in general, but can we bypass that with policy stuff?
