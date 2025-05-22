[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE.txt)  
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](./license/LICENSE-APACHE-2.0.txt)

# file_template_app

## Table of contents

- [License](#license)
- [Goal](#goal)
- [Template creation](#template-creation)
- [Compile with](#compile-with)
- [Usage outside intellij based IDE](#usage-outside-intellij-based-ide)
- [Usage in intellij based IDE](#usage-in-intellij-based-ide)

### Goal

This project aims to provide an IDE-independent tool to help developers in their day-to-day work with file structure creation.  
It is based on the same principle as [File Template](https://www.jetbrains.com/help/idea/using-file-and-code-templates.html).
Hope this simplifies new template creation and prevents issues from IDE updates that could break previous templates.

### Template creation 

1. Go to `src/main/resources/file_templates`
2. Create a new folder
3. Copy-paste any files from a file structure you want to create (no inner folders; the path will be constructed at template creation time).
4. Remove all specific code from the copied files to keep them generic.
5. Replace every package name (e.g. `package com.marcpicone.file_template_app.file_template_view`) at the top with `${ANDROID_PACKAGE}`.
6. Replace class-specific names (e.g. `FileTemplate` for `FileTemplateView`) with one of the following placeholders:
  - `${FEATURE_NAME_TO_UPPER_CAMEL_CASE}` → `FeatureName`
  - `${FEATURE_NAME_TO_LOWER_CAMEL_CASE}` → `featureName`
  - `${FEATURE_NAME_TO_LOWER_SNAKE_CASE}` → `feature_name`
  - `${FEATURE_NAME_TO_UPPER_SNAKE_CASE}` → `FEATURE_NAME`
7. In the same folder, create a file named `template.xml` with this structure:

    ```xml
    <templates>
        <template
            parent-folder-name="The folder name where your templates are stored"
            file-extension="File extension for the generated parent template"
            file-name="Name of your parent template file"
            groups="List of groups (e.g. android, all, personal)"
            id="Unique template ID"
            velocity-file-path="Output path (can mix placeholders and hardcoded text)">
            <!-- You can add as many <child> entries as needed -->
            <child
                file-extension="Extension for this child template"
                file-name="Name of the child template file"
                velocity-file-path="Output path for this child"/>
        </template>
    </templates>
    ```

   For a complete example, see [android_xml_view_example](./file_template_app/src/main/resources/file_templates/android_xml_view_example/template.xml).

### Compile with :

- `./gradlew file_template_app:clean file_template_app:packageUberJarForCurrentOS`
This command will output the path to the generated JAR.

### Usage outside intellij based IDE

- Run the jar with the following command :
```bash
java -jar <path-to-jar>
```
The application will prompt you to choose the folder where your structure will be created.
From here you could use the `FileTemplateApp` tool to create your template.

### Usage in intellij based IDE

1.	Go to Settings → Tools → External Tools.
Add a new external tool:
	Name: FileTemplateApp (or your preferred name)
	Program: /usr/bin/java
	Arguments: -jar "<path-to-jar>" $FileDir$ $FileFQPackage$
	Working directory: $ProjectFileDir$
Go to Settings → Keymap, search for your tool, right-click and add a shortcut (e.g. Ctrl+Alt+Cmd+F).

Now you can invoke the tool with your shortcut to generate your template structure!