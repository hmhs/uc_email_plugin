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
def ucdUserNamee = props['ucdUserName']
def ucdPassword = props['ucdPassword']
def ucbProjectId = props['projectId']
def ucbProcessId = props['processId']
def msHost = props['msHost']
def msPort = props['msPort']
def msSecure = props['msSecure']
def msFrom = props['msFrom']
def msUser = props['msUser']
def msPass = props['msPass']

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
    // Default assumes UC Build
    case "byApp":
        emailList = teamEmailer.getEmailsByApp(appCompName, roleList)
        teamEmailer.sendUCDEmail(emailList,emailSubject,emailBody,attachment)
        break
    case "byComp":
        emailList = teamEmailer.getEmailsByComp(appCompName, roleList)
        teamEmailer.sendUCDEmail(emailList,emailSubject,emailBody,attachment)
        break
    default:
        if(msUser == "") {
            msUser = null
        }
        if(msPass == "") {
            msPass = null
        }
        emailList = teamEmailer.getEmailFromUcBuild(ucbProjectId, ucbProcessId, roleList)
        teamEmailer.sendSMTPEmail(emailList,emailSubject,emailBody,attachment,msHost,msPort,
            msSecure,msFrom,msUser,msPass)
        break
}
