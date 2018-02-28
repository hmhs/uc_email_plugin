package com.hmhs.underpinning.uc.emailTeam

import com.urbancode.air.AirPluginTool
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class EmailTeamTest extends Specification {

  def "get emails for team"(String team, List<String> role, String expectedResult, Integer expectedSize) {
        given:
        String args1 = "PLUGIN_INPUT_PROPS=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\input.props\n" +
                "PLUGIN_OUTPUT_PROPS=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\output.props\n" +
                "directoryOffset=default_test\n" +
                "outputFile=\n" +
                "runAsDaemon=false\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "shellInterpreter="
        String args2 = "AGENT_HOME=C:\\HighmarkApps\\IBM\\ucd\\agent\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_WEB_URL=https://ucdtest.highmark.com:8443\n" +
                "AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_SYSTEM_ENCODING=Cp1252\n" +
                "JAVA_OPTS=-Dfile.encoding=Cp1252 -Dconsole.encoding=Cp1252\n" +
                "PLUGIN_HOME=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\plugins\\com.urbancode.air.plugin.Shell_9_56774f414df3906989c0bf22a65b086b539536a8ee20d3ba567756e7fff0d4ed\n" +
                "UCD_USE_ENCRYPTED_PROPERTIES=true\n" +
                "UD_DIALOGUE_ID=880dd5c8-324d-411a-98db-c1f32bc165ef\n" +
                "WE_ACTIVITY_ID=161b94b9-8caf-857d-1638-812eff57bcf2"
        final airTool = new AirPluginTool(args1,args2)
        EmailTeam emailTeam = new EmailTeam(airTool,'https://ucdeploytest.highmark.com','admin','admin')

        when:
        println "Getting emails for ${team} and role ${role}"
        def result = emailTeam.getEmailListForTeam(team, role)
        println result

        then:
        assert result.size() == expectedSize
        assert expectedResult == result.find {it == expectedResult}

        where:
        team | role | expectedResult | expectedSize
        "Test Team" | ["all"] | "james.neumyer@highmark.com" | 15
        "Test Team" | ["Team Admin"] | "james.neumyer@highmark.com" | 7
        "AEM" | ["Team User"] | "Jin.Song@highmark.com" | 1
        "Test Team" | ["Administrator"] | "james.neumyer@highmark.com" | 13
        "Test Team" | ["Team Admin","Administrator"] | "james.neumyer@highmark.com" | 15
        "HDB" | ["Team Manager"] | null | 0
    }

    def "get emails for app"(String app,List<String> role, String expectedResult, Integer expectedSize) {
        given:
        String args1 = "PLUGIN_INPUT_PROPS=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\input.props\n" +
                "PLUGIN_OUTPUT_PROPS=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\output.props\n" +
                "directoryOffset=default_test\n" +
                "outputFile=\n" +
                "runAsDaemon=false\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "shellInterpreter="
        String args2 = "AGENT_HOME=C:\\HighmarkApps\\IBM\\ucd\\agent\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_WEB_URL=https://ucdtest.highmark.com:8443\n" +
                "AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_SYSTEM_ENCODING=Cp1252\n" +
                "JAVA_OPTS=-Dfile.encoding=Cp1252 -Dconsole.encoding=Cp1252\n" +
                "PLUGIN_HOME=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\plugins\\com.urbancode.air.plugin.Shell_9_56774f414df3906989c0bf22a65b086b539536a8ee20d3ba567756e7fff0d4ed\n" +
                "UCD_USE_ENCRYPTED_PROPERTIES=true\n" +
                "UD_DIALOGUE_ID=880dd5c8-324d-411a-98db-c1f32bc165ef\n" +
                "WE_ACTIVITY_ID=161b94b9-8caf-857d-1638-812eff57bcf2"
        final airTool = new AirPluginTool(args1,args2)
        EmailTeam emailTeam = new EmailTeam(airTool,'https://ucdeploytest.highmark.com','admin','admin')

        when:
        println "Getting emails by application ${app} with role ${role}"
        def result = emailTeam.getEmailsByApp(app, role)
        println result

        then:
        assert result.size() == expectedSize
        assert expectedResult == result.find {it == expectedResult}

        where:
        app | role | expectedResult | expectedSize
        "xyz_was_intra" | ["all"] | "james.neumyer@highmark.com" | 15
        "xyz_was_intra" | ["Team Admin"] | "james.neumyer@highmark.com" | 7
        "xyz_aem" | ["Team User"] | "Jin.Song@highmark.com" | 1
        "xyz_was_intra" | ["Administrator"] | "james.neumyer@highmark.com" | 13
        "com.hmhs.risksol.opus_app:hm5_was_intra" | ["Team Admin","Administrator"] | "urmila.varanasi@highmark.com" | 7
        "esr_was_intra" | ["Team Manager"] | null | 0
    }

    def "get emails for comp"(String comp,List<String> role, String expectedResult, Integer expectedSize) {
        given:
        String args1 = "PLUGIN_INPUT_PROPS=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\input.props\n" +
                "PLUGIN_OUTPUT_PROPS=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\output.props\n" +
                "directoryOffset=default_test\n" +
                "outputFile=\n" +
                "runAsDaemon=false\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "shellInterpreter="
        String args2 = "AGENT_HOME=C:\\HighmarkApps\\IBM\\ucd\\agent\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_WEB_URL=https://ucdtest.highmark.com:8443\n" +
                "AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_SYSTEM_ENCODING=Cp1252\n" +
                "JAVA_OPTS=-Dfile.encoding=Cp1252 -Dconsole.encoding=Cp1252\n" +
                "PLUGIN_HOME=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\plugins\\com.urbancode.air.plugin.Shell_9_56774f414df3906989c0bf22a65b086b539536a8ee20d3ba567756e7fff0d4ed\n" +
                "UCD_USE_ENCRYPTED_PROPERTIES=true\n" +
                "UD_DIALOGUE_ID=880dd5c8-324d-411a-98db-c1f32bc165ef\n" +
                "WE_ACTIVITY_ID=161b94b9-8caf-857d-1638-812eff57bcf2"
        final airTool = new AirPluginTool(args1,args2)
        EmailTeam emailTeam = new EmailTeam(airTool,'https://ucdeploytest.highmark.com','admin','admin')

        when:
        println "Getting emails by component ${comp} with role ${role}"
        def result = emailTeam.getEmailsByComp(comp, role)
        println result

        then:
        assert result.size() == expectedSize
        assert expectedResult == result.find {it == expectedResult}

        where:
        comp | role | expectedResult | expectedSize
        "xyztst" | ["all"] | "james.neumyer@highmark.com" | 15
        "xyztst" | ["Team Admin"] | "james.neumyer@highmark.com" | 7
        "xyzaem_content_aem" | ["Team User"] | "Jin.Song@highmark.com" | 1
        "xyztst" | ["Administrator"] | "james.neumyer@highmark.com" | 13
        "com.hmhs.risksol.opus_app.opus_middleware" | ["Team Admin","Administrator"] | "urmila.varanasi@highmark.com" | 7
        "b2brep_ear_was_intra" | ["Team Manager"] | null | 0
    }

    def "sending emails"(List<String> emails,String subject,String message,String attachment) {
        given:
        String args1 = "PLUGIN_INPUT_PROPS=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\input.props\n" +
                "PLUGIN_OUTPUT_PROPS=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\temp\\logs8125200289903819506\\output.props\n" +
                "directoryOffset=default_test\n" +
                "outputFile=\n" +
                "runAsDaemon=false\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "shellInterpreter="
        String args2 = "AGENT_HOME=C:\\HighmarkApps\\IBM\\ucd\\agent\n" +
                "AH_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "AH_WEB_URL=https://ucdtest.highmark.com:8443\n" +
                "AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_AUTH_TOKEN=f1a21e9c-738d-426d-804b-2bea20d598b7\n" +
                "DS_SYSTEM_ENCODING=Cp1252\n" +
                "JAVA_OPTS=-Dfile.encoding=Cp1252 -Dconsole.encoding=Cp1252\n" +
                "PLUGIN_HOME=C:\\HighmarkApps\\IBM\\ucd\\agent\\var\\plugins\\com.urbancode.air.plugin.Shell_9_56774f414df3906989c0bf22a65b086b539536a8ee20d3ba567756e7fff0d4ed\n" +
                "UCD_USE_ENCRYPTED_PROPERTIES=true\n" +
                "UD_DIALOGUE_ID=880dd5c8-324d-411a-98db-c1f32bc165ef\n" +
                "WE_ACTIVITY_ID=161b94b9-8caf-857d-1638-812eff57bcf2"
        final airTool = new AirPluginTool(args1,args2)
        EmailTeam emailTeam = new EmailTeam(airTool,'https://ucdeploytest.highmark.com','admin','admin')

        when:
        println "Sending email to ${emails} with ${subject} and attachment ${attachment}"
        emailTeam.sendUCDEmail(emails, subject, message, attachment)

        then:
        assert true

        where:
        emails | subject | message | attachment
        ["james.neumyer@highmark.com"] | "Unit test email" | "Testing email from unit test system" | null
        ["james.neumyer@highmark.com"] | "Unit test email attachment" | "Testing email from unit test system with an attachment" | "Z:\\quotes.txt"
        //["james.neumyer@highmark.com","alan.parker@highmark.com"] | "Unit test email" | "Testing email from unit test system with multiple people" | null
    }
}
