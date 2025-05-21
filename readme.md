# file_template_app

### Goal

This project aim to have an IDE independent tool to help dev in day to day work.

### Compile with :

- `./gradlew file_template_app:clean file_template_app:packageUberJarForCurrentOS`

### Usage in intellij based IDE

- Go to `settings` -> `Tools` -> `External Tools`
- Add new external tool
- Name : `FileTemplateApp` or what ever you want
- In the `Tools settings` window
    - Program : `/usr/bin/java`
    - Arguments : `-jar "<path-to-jar>" $FileDir$ $FileFQPackage$`
    - Working directory : `$ProjectFileDir$`

- Then go to `settings` -> `Keymap` and start typing the name of the tool (for me `FileTemplateApp`)
- Once the tool is present on the keymap window right-click on the tool and add a shortcut (for me `control + option + command + F`)

Now you could call this tool with a simple hit of the keymap shortcut!!