Gradle restart
==============
So I've restarted with gradle instead because the instructions from the original link seemed to have some heavy dependencies on things that aren't available in the versions Debian has packaged.
I think I've gotten further, but not quite there yet.

Mostly based on https://developer.okta.com/blog/2018/08/10/basic-android-without-an-ide
with some manual changes to account for version mismatches, again.

Dependencies
------------
Debian bullseye::

    sudo apt purge openjdk-17-jdk-headless
    sudo apt install android-sdk android-sdk-platform-23 gradle openjdk-11-jdk-headless

Process
-------

::

    gradle build

Currently broken with::

    > Task :app:compileDebugJavaWithJavac FAILED
    /home/mike/vcs/cyber/com.prisonpc.cacert-installer/app/src/main/java/com/prisonpc/cacertinstaller/MainActivity.java:11: error: package R does not exist
            setContentView(R.layout.activity_main);
                            ^
    1 error

Still working on what's wrong







Old stuff
=========

Based on info/instructions found at: https://stackoverflow.com/questions/29178552/hello-world-using-the-android-sdk-alone-no-ide#29313378
Until that started failing miserably and I started basing it on this: https://gist.github.com/SilverShades02/861f3e55fddfba02ded147bbfcaba030

Skipped "Setting up the Android SDK", as the packages were seemingly available in Debian.

Dependencies
============
Debian bullseye::

    sudo apt install google-android-emulator-installer google-android-build-tools-24-installer google-android-platform-24-installer openjdk-17-jdk

Emulator package probably entirely unnecessary, I installed it for testing though.

24 is the latest version of the build tools available in Bullseye at time of writing.
I would be targetting 26 given the option, as the oldest Android version we're working with is 8.0.
24 is Android version 7.0

Note the stackoverflow page said it requires "Oracle JDK 1.7 or later", but I see no evidence (yet) that it can't work with openjdk

Building the code
=================
1. Generate the source for the resource declarations. Substitute here the correct path to your SDK, and the installed API to build against (e.g. "android-23")::

       /usr/lib/android-sdk/build-tools/24.0.2/aapt package -f -I /usr/lib/android-sdk/platforms/android-24/android.jar -J src -m -M AndroidManifest.xml -S res -v

2. Compile the source code to Java bytecode (.java → .class):

   ::

       javac \
         -bootclasspath /usr/lib/android-sdk/platforms/android-24/android.jar \
         -classpath src -source 1.7 -target 1.7 \
         src/com/prisonpc/SayingHello.java

   FIXME: warning: [options] source value 7 is obsolete and will be removed in a future release
   FIXME: warning: [options] target value 7 is obsolete and will be removed in a future release

3. Translate the bytecode from Java to Android (.class → .dex):

   First using Jill (.class → .jayce)::

       java -jar /usr/lib/android-sdk/build-tools/24.0.2/jill.jar \
         --output classes.jayce src

   Then Jack (.jayce → .dex)::

       java -jar /usr/lib/android-sdk/build-tools/24.0.2/jack.jar --import classes.jayce --output-dex . \
         -D jack.source.digest.algo=SHA-512 -D sched.vfs.case-insensitive.algo=SHA-512 -D jack.library.digest.algo=SHA-512

   NOTE: The -D options were added due to getting errors about each of those defaulting to 'SHA' which isn't a supported option
   Android bytecode used to be called "Dalvik executable code", and so "dex".

   You could replace steps 2 and 3 with a single call to Jack if you like; it can compile directly from Java source (.java → .dex). But there are advantages to compiling with javac. It's a better known, better documented and more widely applicable tool.

4. Package up the resource files, including the manifest:

   ::

       aapt package -f \
         -F app.apkPart \
         -I /usr/lib/android-sdk/platforms/android-24/android.jar \
         -M AndroidManifest.xml -S res -v

   That results in a partial APK file (Android application package).

   Make the full APK using the ApkBuilder tool::

       java -classpath /usr/lib/android-sdk/tools/lib/sdklib.jar \
         com.android.sdklib.build.ApkBuilderMain \
         app.apkUnalign \
         -d -f classes.dex -v -z app.apkPart

   It warns, "THIS TOOL IS DEPRECATED. See --help for more information." If --help fails with an ArrayIndexOutOfBoundsException, then instead pass no arguments::

       java -classpath /usr/lib/android-sdk/tools/lib/sdklib.jar \
         com.android.sdklib.build.ApkBuilderMain

   It explains that the CLI (ApkBuilderMain) is deprecated in favour of directly calling the Java API (ApkBuilder). (If you know how to do that from the command line, please update this example.)

   Optimize the data alignment of the APK (recommended practice)::

       zipalign -f -v 4 app.apkUnalign app.apk

