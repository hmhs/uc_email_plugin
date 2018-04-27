# IBM UrbanCode Email Plugin
---

This plugin enables a process step to email a team.  The email subject, body, and attachment may be entered and configured.
The plugin works with both UC Build and UC Deploy.  However, the querying of the teams and email must be done in UC Deploy.
This is because the REST service in UC Build are not built out to obtain the team and email addresses based on the process.

## Build Information
---

The plugin is built with Gradle.  All groovy scripts should be placed and called in src/main/groovy.  All files to be
placed into the plugin root should be placed into src/ucbuild/zip or src/ucdeploy/zip.  Dependent objects will
automatically be placed into the lib folder at the root of the plugin.

### To Build
---
- Update the unit test class with the UC Deploy user and password to use for testing and update the data in the test class
for the data stored in instance of UC Deploy to use for testing
- Run ./gradlew build distPlugin
- Build will compile the Groovy code and run the unit test cases
- The distPlugin task will zip up the required files and place the plugin zip file into build/distributions
