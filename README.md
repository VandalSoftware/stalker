Stalker
=======

Stalker detects affected Java classes given a set of changed Java source files.
It does this by examining bytecode and completing the closure on dependencies.

## Usage

**build.gradle**

    buildscript {
      repositories {
          mavenCentral()
      }
      dependencies {
          classpath "com.vandalsoftware.tools.gradle:stalker:0.3.+"
      }
    }

## Configuration

**build.gradle**

    apply plugin: 'stalker'

    stalker {
      srcRoot "src/main/java"
      srcClassPath "$buildDir/classes/main"
      targetClassPath "$buildDir/classes/test"
    }

Currently, stalker obtains a list of changed files by relying on Git.
To use it, you must be tracking a git repository in your working directory:

    $ git init
    
By default, it will use `HEAD` as its reference. You can configure this by setting
the `revision` property.

For example:

    stalker {
      ...
      revison = project.has("rev") ? rev : "HEAD"
    }

You might then run stalker like this...

    $ ./gradlew stalk -Prev=a34dfef0 --info

...which outputs something along these lines.

    com.vandalsoftware.android.common.IoUtils used by build/classes/test/com/vandalsoftware/android/common/IoUtilsTests.class
    com.vandalsoftware.android.common.IoUtilsTests

That's pretty interesting but not particularly useful since the output is stuck on the command line.
However, Stalker allows you to direct output anywhere you want.

**build.gradle**

    stalker {
      srcRoot "src/main/java"
      srcClassPath "$buildDir/classes/main"
      targetClassPath "$buildDir/classes/test"
      standardOutput = new FileOutputStream(new File("out.txt"))
    }

**out.txt**

    com.vandalsoftware.android.common.IoUtilsTests

We can now take the output of this file and input it into any script we write to execute the tests.
For example, in this imaginary Python script I wrote.

    $ cat out.txt | test_runner.py -s mycooldevice
    
Taking it one step further, we can use the afterStalk closure to do whatever we want within the build script itself:

**build.gradle**

    stalker {
      srcRoot "src/main/java"
      srcClassPath "$buildDir/classes/main"
      targetClassPath "$buildDir/classes/test"
      afterStalk { names ->
        names.each() {
            println "adb shell am instrument -w -e class " + it + " com.vandalsoftware.android.tests/android.test.InstrumentationTestRunner"
        }
      }
    }

Which produces some output:

    adb shell am instrument -w -e class com.vandalsoftware.android.common.IoUtilsTests com.vandalsoftware.android.tests/android.test.InstrumentationTestRunner

## License

Apache License, Version 2.0
