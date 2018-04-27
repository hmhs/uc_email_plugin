import com.urbancode.air.AirPluginTool
import com.hmhs.underpinning.uc.emailTeam.EmailTeam

final airTool = new AirPluginTool(args[0], args[1])

final def props = airTool.getStepProperties()

/**
 * Groovy script to obtain and send emails for UC Tools
 */

def exitCode = 0

def emailSubject = props['emailSubject']
def emailBody = props['emailBody']
def attachment = props['attachment']
def getTeamBy = props['getTeamBy']
def roles = props['roles']
def appCompName = props['appCompName']
def ucdUrl = props['ucdUrl']
def ucdUserNamee = props['ucdUserNamee']
def ucdPassword = props['ucdPassword']

EmailTeam teamEmailer = null

// Determine which constructor to use based on options given
if(ucdUrl == "" || ucdUrl == null ||
        ucdUserNamee == "" || ucdUserNamee == null ||
        ucdPassword == "" || ucdPassword == null) {
    teamEmailer = new EmailTeam(airTool)
} else {
    teamEmailer = new EmailTeam(airTool,ucdUrl.toString(),ucdUserNamee.toString(),ucdPassword.toString())
}

List<String> roleList = []

// Get the roles into a comma separated list
roles.tokenize(',').each() {role -> roleList.add(role)}
def emailList = []
switch(getTeamBy) {
    // How should we search in the tool
    case "byApp":
        emailList = teamEmailer.getEmailsByApp(appCompName, roleList)
        teamEmailer.sendUCDEmail(emailList,emailSubject,emailBody,attachment)
        break
    case "byComp":
        emailList = teamEmailer.getEmailsByComp(appCompName, roleList)
        teamEmailer.sendUCDEmail(emailList,emailSubject,emailBody,attachment)
        break
}
