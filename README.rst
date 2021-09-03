So I've restarted with gradle instead because the instructions from the original link seemed to have some heavy dependencies on things that aren't available in the versions Debian has packaged.
I think I've gotten further, but not quite there yet.

Mostly based on https://developer.okta.com/blog/2018/08/10/basic-android-without-an-ide
with some manual changes to account for version mismatches, again.

Dependencies
============
Debian bullseye::

    sudo apt install android-sdk android-sdk-platform-23 gradle openjdk-11-jdk-headless

Build process
=============

Build the unsigned/debug/dev pacakge::

    gradle build
    # NOTE: It doesn't currently work with openjdk-17 (FIXME) so either uninstall it, or if you keep it installed, run add this argument to the build: -Dorg.gradle.java.home=/usr/lib/jvm/java-11-openjdk-amd64/

Install it on a device::

    adb install ./app/build/outputs/apk/debug/app-debug.apk

Create a signing keystore thing.
NOTE this from Google: "If you plan to publish your apps on Google Play, the key you use to sign your app must have a validity period ending after 22 October 2033. Google Play enforces this requirement to ensure that users can seamlessly upgrade apps when new versions are available." -- https://developer.android.com/studio/publish/app-signing#considerations
I think this keystore file is supposed to have multiple keys in the one database-like file, not sure::

    keytool -genkey -v -keystore ~/.android/release.keystore -alias mike-prisonpcemm@cyber.com.au -keyalg RSA -keysize 2048 -validity 7300

Sign the APK
If you typo the password it gives a stacktrace, not a nice error::

    apksigner sign --ks ~/.android/release.keystore --out ./com.prisonpc.cacertinstaller.apk ./app/build/outputs/apk/release/app-release-unsigned.apk

FIXME: Upload it to Google. I did this via the GUI and it worked fine. Can't find any info specific to private store
       https://stackoverflow.com/questions/21829495/how-to-upload-an-android-app-to-the-app-store-via-command-line
       https://andresand.medium.com/automate-publishing-app-bundle-or-apk-to-google-play-store-8641f0ba2f64
       https://stasheq.medium.com/upload-apk-to-google-play-via-command-line-script-d93b0d6a28c5
       https://developers.google.com/android-publisher/api-ref/rest

Setting the wallpaper
=====================
Just place a valid JPG or PNG file at app/src/main/res/drawable/wallpaper.(jpg|png) and rebuild the package.

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
