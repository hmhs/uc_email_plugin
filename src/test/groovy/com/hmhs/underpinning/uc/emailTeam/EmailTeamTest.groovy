package com.hmhs.underpinning.uc.emailTeam

import com.urbancode.air.AirPluginTool
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Test Class for Team Emailer
 */

@Unroll
class EmailTeamTest extends Specification {

    /**
     * Test method for getting emails for a specific team
     * @param team - Team Name
     * @param role - Team Roles
     * @param expectedResult - One expected email address to be returned
     * @param expectedSize - Expected number of returned email addresses
     * @return
     */
    def "get emails for team"(String team, List<String> role, String expectedResult, Integer expectedSize) {
        given:
        // Setting up properties for UC Air Plugin, most items are ignored during processing
        String args1 = "PLUGIN_INPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\input.props\n" +
            "PLUGIN_OUTPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\output.props\n" +
            "directoryOffset=default_test\n" +
            "outputFile=\n" +
            "runAsDaemon=false\n" +
            "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "shellInterpreter="
        String args2 = "AGENT_HOME=C:\\ProgramData\\IBM\\ucd\\agent\n" +
            "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "AH_WEB_URL=https://ucdtest.example.com:8443\n" +
            "AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "DS_SYSTEM_ENCODING=Cp1252\n" +
            "JAVA_OPTS=-Dfile.encoding=Cp1252 -Dconsole.encoding=Cp1252\n" +
            "PLUGIN_HOME=C:\\ProgramData\\IBM\\ucd\\agent\\var\\plugins\\com.urbancode.air.plugin.Shell_9_56774f414df3906989c0bf22a65b086b539536a8ee20d3ba567756e7fff0d4ed\n" +
            "UCD_USE_ENCRYPTED_PROPERTIES=true\n" +
            "UD_DIALOGUE_ID=880dd5c8-324d-411a-98db-c1f32bc165ef\n" +
            "WE_ACTIVITY_ID=161b94b9-8caf-857d-1638-812eff57bcf2"

        // Create the air object
        final airTool = new AirPluginTool(args1, args2)

        // Construct the team emailer
        // You will need to set the UC Deploy URL, user and password for testing
        EmailTeam emailTeam = new EmailTeam(airTool, 'https://ucdeploytest.highmark.com', 'admin',
            '<enter pass>')

        when:
        println "Getting emails for ${team} and role ${role}"
        // Get the team results
        def result = emailTeam.getEmailListForTeam(team, role)
        println result

        then:
        // Make sure things match
        assert result.size() == expectedSize
        assert expectedResult == result.find { it == expectedResult }

        where:
        // Test data and results.  Update to meet your installation
        team        | role                            | expectedResult           | expectedSize
        "Test Team" | ["all"]                         | "james.neumyer@hmhs.com" | 15
        "Test Team" | ["Team Admin"]                  | "james.neumyer@hmhs.com" | 7
        "Test Team" | ["Team User"]                   | "alan.parker@highmark.com"   | 1
        "Test Team" | ["Administrator"]               | "james.neumyer@hmhs.com" | 13
        "Test Team" | ["Team Admin", "Administrator"] | "james.neumyer@hmhs.com" | 15
        "EDI" | ["Team Manager"]                | null                     | 0
    }

    /**
     * Testing method to get emails for a specific application
     * @param app - UC Deploy application name
     * @param role - UC Deploy roles
     * @param expectedResult - One expected email address in result set
     * @param expectedSize - Expected result size
     * @return
     */
    def "get emails for app"(String app, List<String> role, String expectedResult, Integer expectedSize) {
        given:
        String args1 = "PLUGIN_INPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\input.props\n" +
                "PLUGIN_OUTPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\output.props\n" +
                "directoryOffset=default_test\n" +
                "outputFile=\n" +
                "runAsDaemon=false\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "shellInterpreter="
        String args2 = "AGENT_HOME=C:\\ProgramData\\IBM\\ucd\\agent\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_WEB_URL=https://ucdtest.example.com:8443\n" +
                "AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_SYSTEM_ENCODING=Cp1252\n" +
                "JAVA_OPTS=-Dfile.encoding=Cp1252 -Dconsole.encoding=Cp1252\n" +
                "PLUGIN_HOME=C:\\ProgramData\\IBM\\ucd\\agent\\var\\plugins\\com.urbancode.air.plugin.Shell_9_56774f414df3906989c0bf22a65b086b539536a8ee20d3ba567756e7fff0d4ed\n" +
                "UCD_USE_ENCRYPTED_PROPERTIES=true\n" +
                "UD_DIALOGUE_ID=880dd5c8-324d-411a-98db-c1f32bc165ef\n" +
                "WE_ACTIVITY_ID=161b94b9-8caf-857d-1638-812eff57bcf2"
        final airTool = new AirPluginTool(args1, args2)
        EmailTeam emailTeam = new EmailTeam(airTool, 'https://ucdeploytest.highmark.com', 'admin',
            '<enter pass>')

        when:
        println "Getting emails by application ${app} with role ${role}"
        def result = emailTeam.getEmailsByApp(app, role)
        println result

        then:
        assert result.size() == expectedSize
        assert expectedResult == result.find { it == expectedResult }

        where:
        app             | role                            | expectedResult       | expectedSize
        "xyz_was_intra" | ["all"]                         | "james.neumyer@hmhs.com" | 15
        "xyz_was_intra" | ["Team Admin"]                  | "james.neumyer@hmhs.com" | 7
        "xyz_aem"       | ["Team User"]                   | "Jin.Song@hmhs.com" | 1
        "xyz_was_intra" | ["Administrator"]               | "james.neumyer@hmhs.com" | 13
        "xyz_was_intra" | ["Team Admin", "Administrator"] | "james.neumyer@hmhs.com" | 15
        "b2b_was_intra" | ["Team Manager"]                | null                 | 0
    }

    /**
     * Test method to obtain email addresses by component
     * @param comp - UC Deploy component name
     * @param role - UC Deploy Roles
     * @param expectedResult - One email address in the expected result list
     * @param expectedSize - Number of expected results
     * @return
     */
    def "get emails for comp"(String comp, List<String> role, String expectedResult, Integer expectedSize) {
        given:
        String args1 = "PLUGIN_INPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\input.props\n" +
            "PLUGIN_OUTPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\output.props\n" +
            "directoryOffset=default_test\n" +
            "outputFile=\n" +
            "runAsDaemon=false\n" +
            "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "shellInterpreter="
        String args2 = "AGENT_HOME=C:\\ProgramData\\IBM\\ucd\\agent\n" +
            "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "AH_WEB_URL=https://ucdtest.example.com:8443\n" +
            "AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "DS_SYSTEM_ENCODING=Cp1252\n" +
            "JAVA_OPTS=-Dfile.encoding=Cp1252 -Dconsole.encoding=Cp1252\n" +
            "PLUGIN_HOME=C:\\ProgramData\\IBM\\ucd\\agent\\var\\plugins\\com.urbancode.air.plugin.Shell_9_56774f414df3906989c0bf22a65b086b539536a8ee20d3ba567756e7fff0d4ed\n" +
            "UCD_USE_ENCRYPTED_PROPERTIES=true\n" +
            "UD_DIALOGUE_ID=880dd5c8-324d-411a-98db-c1f32bc165ef\n" +
            "WE_ACTIVITY_ID=161b94b9-8caf-857d-1638-812eff57bcf2"
        final airTool = new AirPluginTool(args1, args2)
        EmailTeam emailTeam = new EmailTeam(airTool, 'https://ucdeploytest.highmark.com', 'admin',
            '<enter pass>')

        when:
        println "Getting emails by component ${comp} with role ${role}"
        def result = emailTeam.getEmailsByComp(comp, role)
        println result

        then:
        assert result.size() == expectedSize
        assert expectedResult == result.find { it == expectedResult }

        where:
        comp                   | role                            | expectedResult           | expectedSize
        "xyztst"               | ["all"]                         | "james.neumyer@hmhs.com" | 15
        "xyztst"               | ["Team Admin"]                  | "james.neumyer@hmhs.com" | 7
        "xyzaem_content_aem"   | ["Team User"]                   | "Jin.Song@hmhs.com"      | 1
        "xyztst"               | ["Administrator"]               | "james.neumyer@hmhs.com" | 13
        "xyztst"               | ["Team Admin", "Administrator"] | "james.neumyer@hmhs.com" | 15
        "b2bdir_dir_was_intra" | ["Team Manager"]                | null                     | 0
    }

    /**
     * Test method to obtain email addresses from ucbuild
     */
    def "get emails for ucb"(String projectId, String processId, List<String> role, String
        expectedResult, Integer expectedSize) {
        given:
        String args1 = "PLUGIN_INPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\input.props\n" +
            "PLUGIN_OUTPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\output.props\n" +
            "directoryOffset=default_test\n" +
            "outputFile=\n" +
            "runAsDaemon=false\n" +
            "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "shellInterpreter="
        String args2 = "AGENT_HOME=C:\\ProgramData\\IBM\\ucd\\agent\n" +
            "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "AH_WEB_URL=https://ucdtest.example.com:8443\n" +
            "AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "DS_SYSTEM_ENCODING=Cp1252\n" +
            "JAVA_OPTS=-Dfile.encoding=Cp1252 -Dconsole.encoding=Cp1252\n" +
            "PLUGIN_HOME=C:\\ProgramData\\IBM\\ucd\\agent\\var\\plugins\\com.urbancode.air.plugin.Shell_9_56774f414df3906989c0bf22a65b086b539536a8ee20d3ba567756e7fff0d4ed\n" +
            "UCD_USE_ENCRYPTED_PROPERTIES=true\n" +
            "UD_DIALOGUE_ID=880dd5c8-324d-411a-98db-c1f32bc165ef\n" +
            "WE_ACTIVITY_ID=161b94b9-8caf-857d-1638-812eff57bcf2"
        final airTool = new AirPluginTool(args1, args2)
        EmailTeam emailTeam = new EmailTeam(airTool, 'https://ucbuildtest.highmark.com', 'admin',
            '<enter pass>')

        when:
        println "Getting emails from UC Build with role ${role}"
        def result = emailTeam.getEmailFromUcBuild(projectId, processId, role)
        println result

        then:
        assert result.size() == expectedSize
        assert expectedResult == result.find { it == expectedResult }

        where:
        projectId | processId | role                            | expectedResult           | expectedSize
        "411"     | "851"     | ["all"]                         | null                     | 0
        "413"     | "743"     | ["Team Admin"]                  | "Jin.Song@hmhs.com"      | 2
        "413"     | "743"     | ["Administrator"]               | "james.neumyer@hmhs.com" | 5
        "413"     | "743"     | ["Team Admin", "Administrator"] | "james.neumyer@hmhs.com" | 7
    }

    /**
     * Test method to send emails
     * @param emails - List of email addresses
     * @param subject - Subject of email
     * @param message - Message body
     * @param attachment - Attachement if any
     * @return
     */
    def "sending emails"(List<String> emails, String subject, String message, String attachment) {
        given:
        String args1 = "PLUGIN_INPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\input.props\n" +
                "PLUGIN_OUTPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\output.props\n" +
                "directoryOffset=default_test\n" +
                "outputFile=\n" +
                "runAsDaemon=false\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "shellInterpreter="
        String args2 = "AGENT_HOME=C:\\ProgramData\\IBM\\ucd\\agent\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_WEB_URL=https://ucdtest.example.com:8443\n" +
                "AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_SYSTEM_ENCODING=Cp1252\n" +
                "JAVA_OPTS=-Dfile.encoding=Cp1252 -Dconsole.encoding=Cp1252\n" +
                "PLUGIN_HOME=C:\\ProgramData\\IBM\\ucd\\agent\\var\\plugins\\com.urbancode.air.plugin.Shell_9_56774f414df3906989c0bf22a65b086b539536a8ee20d3ba567756e7fff0d4ed\n" +
                "UCD_USE_ENCRYPTED_PROPERTIES=true\n" +
                "UD_DIALOGUE_ID=880dd5c8-324d-411a-98db-c1f32bc165ef\n" +
                "WE_ACTIVITY_ID=161b94b9-8caf-857d-1638-812eff57bcf2"
        final airTool = new AirPluginTool(args1, args2)
        EmailTeam emailTeam = new EmailTeam(airTool, 'https://ucdeploytest.highmark.com', 'admin',
            '<enter pass>')

        when:
        println "Sending email to ${emails} with ${subject} and attachment ${attachment}"
        emailTeam.sendUCDEmail(emails, subject, message, attachment)

        then:
        assert true

        where:
        emails                 | subject                      | message                                                  | attachment
        ["james.neumyer@hmhs.com"] | "Unit test email"            | "Testing email from unit test system" | null
        ["james.neumyer@hmhs.com"] | "Unit test email attachment" | "Testing email from unit test system with an attachment" | "/Volumes/home2dir1/quotes.txt"
    }

    /**
     * Test method to send SMTP emails
     */
    def "sending SMTP emails"(List<String> emails, String subject, String message, String
        attachment) {
        given:
        String args1 = "PLUGIN_INPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\input.props\n" +
            "PLUGIN_OUTPUT_PROPS=C:\\ProgramData\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\output.props\n" +
            "directoryOffset=default_test\n" +
            "outputFile=\n" +
            "runAsDaemon=false\n" +
            "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "shellInterpreter="
        String args2 = "AGENT_HOME=C:\\ProgramData\\IBM\\ucd\\agent\n" +
            "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "AH_WEB_URL=https://ucdtest.example.com:8443\n" +
            "AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
            "DS_SYSTEM_ENCODING=Cp1252\n" +
            "JAVA_OPTS=-Dfile.encoding=Cp1252 -Dconsole.encoding=Cp1252\n" +
            "PLUGIN_HOME=C:\\ProgramData\\IBM\\ucd\\agent\\var\\plugins\\com.urbancode.air.plugin.Shell_9_56774f414df3906989c0bf22a65b086b539536a8ee20d3ba567756e7fff0d4ed\n" +
            "UCD_USE_ENCRYPTED_PROPERTIES=true\n" +
            "UD_DIALOGUE_ID=880dd5c8-324d-411a-98db-c1f32bc165ef\n" +
            "WE_ACTIVITY_ID=161b94b9-8caf-857d-1638-812eff57bcf2"
        final airTool = new AirPluginTool(args1, args2)
        EmailTeam emailTeam = new EmailTeam(airTool, 'https://ucdeploytest.highmark.com', 'admin',
            '<enter pass>')

        when:
        println "Sending email to ${emails} with ${subject} and attachment ${attachment}"
        emailTeam.sendSMTPEmail(emails, subject, message, attachment, "mailhub.highmark.com", "25",
            false, "ucbuildtest@highmark.com")

        then:
        assert true

        where:
        emails                     | subject                      | message                                                  | attachment
        ["james.neumyer@hmhs.com"] | "Unit test email"            | "Testing email from unit test system"                    | null
        ["james.neumyer@hmhs.com"] | "Unit test email attachment" | "Testing email from unit test system with an attachment" | "/Volumes/home2dir1/quotes.txt"
    }
}
