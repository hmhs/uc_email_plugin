package com.hmhs.underpinning.uc.emailTeam

import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import com.urbancode.commons.httpcomponentsutil.HttpClientBuilder

import groovy.json.JsonSlurper
import javax.mail.internet.*
import javax.mail.*

import com.urbancode.air.AirPluginTool
import com.urbancode.ud.client.SystemClient

/**
 * EmailTeam Class - Methods and constructors for obtaining a list of users based on Team/Role from UC Deploy.
 * An email will then be sent with specific information to the group of users.  It may also include an attachment
 * from the server requesting the email be sent.
 *
 * NOTE: This plugin can be used with UC Build, but UC Build does not have REST API's to obtain team information from a
 * build life.  Thus, it will call UC Deploy to obtain the information
 */

class EmailTeam {
    /**
     * baseUrl - URL Base for UC Tool
     */
    String baseUrl
    /**
     * userName - User name used to connect to the UC Tool
     */
    String userName
    /**
     * password - Password of the user
     */
    String password
    /**
     * ucClient - HTTP Client object used for REST calls
     */
    HttpClient ucClient
    /**
     * apTool - UC Air tool used for UC Deploy functions
     */
    def apTool

    /**
     * Constructor class for the email team objects accepting the air tool
     * @param airTool - UC Air tool object
     */
    EmailTeam(AirPluginTool airTool) {
        this.apTool = airTool
        this.baseUrl = System.getenv("AH_WEB_URL")
        if(this.baseUrl == "" || this.baseUrl == null) {
            this.baseUrl = System.getenv("WEB_URL")
        }
        this.userName = apTool.getAuthTokenUsername()
        this.password = apTool.getAuthToken()
        this.ucClient = initializeClient()
    }

    /**
     * Constructor class allowing for each parameter to be set.  Useful for test classes
     * @param airTool - UC Air Tool object
     * @param baseUrl - UC Base URL
     * @param userName - UC User Name
     * @param password - UC Password
     */
    EmailTeam(AirPluginTool airTool, String baseUrl, String userName, String password) {
        this.apTool = airTool
        this.baseUrl = baseUrl
        this.userName = userName
        this.password = password
        this.ucClient = initializeClient()
    }

    /**
     * Method to initialize the HTTP Client using class objects
     * @return HTTP Client Object
     */
    HttpClient initializeClient(){
        HttpClientBuilder builder = new HttpClientBuilder()
        builder.setPreemptiveAuthentication(true)
        builder.setUsername(this.userName)
        builder.setPassword(this.password)
        //Accept all certificates
        builder.setTrustAllCerts(true)
        return builder.buildClient()
    }

    /**
     * Method to perform GIT Requests to a specific URL
     * @param client - HTTP Client Object
     * @param requestURL - URL of the Request
     * @return JSON Object containing results of REST request
     */
    Object performGetRequest(HttpClient client, String requestURL) {
        HttpRequest request = new HttpGet(requestURL)
        //Execute the REST GET call
        HttpResponse response = client.execute(request)
        //Check that the call was successful
        int statusCode = response.getStatusLine().getStatusCode()
        if ( statusCode > 299 ) {
            println "ERROR : HttpGet to: "+requestURL+ " returned: " +statusCode
            return null
            //System.exit(1)
        } else {
            //Convert the InputStream returned by response.getEntity().getContent() to a String
            BufferedReader reader=new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"))
            StringBuilder builder=new StringBuilder()
            for(String line=null;(line=reader.readLine())!=null;){
                builder.append(line).append("\n")
            }
            //Parse the returned JSON
            //http://groovy-lang.org/json.html
            JsonSlurper slurper = new JsonSlurper()
            def objects=slurper.parseText(builder.toString())
            //Ensure to release the connection
            request.releaseConnection()
            return objects
        }
    }

    /**
     * Method to obtain list of email addresses for a team
     * @param team - Team name as it appears in UC Deploy
     * @param roles - List of Role names as it appears in UC Deploy (optional)
     * @return List of string objects which are the unique email addresses found
     */
    List<String> getEmailListForTeam(String team, List<String> roles = ["all"]) {
        // Set URL for obtaining team information from UC Deploy
        String addUrl = "/cli/team/info?team=" + java.net.URLEncoder.encode(team,"UTF-8")
        List<String> returnList = []

        // Get the team info from UC Deploy
        def teamInfo = performGetRequest(this.ucClient,this.baseUrl + addUrl)

        // If no information found for the team, return an empty list
        if(teamInfo == null || teamInfo.size() != 0) {
            return returnList
        }

        // Look at each role mapping
        teamInfo.roleMappings.each() { mapping ->
            // Look in the roles specified and if the mapping has it, add the user emails to the list
            // If roles was not specified, add email address for all roles to the list
            if(roles.find {mapping.role.name == it} || roles == ["all"]) {
                // Add individual user email address in the roles to the list
                if(mapping.user != null && mapping.user.email != null && mapping.user.email != "") {
                    returnList.add(mapping.user.email)
                }
                // If using a group, look into the group and add each email address for the group to the list
                if(mapping.group != null && mapping.group != "") {
                    // Get group details for the group found
                    String groupUrl = "/cli/group/exportDetailed?group=" + java.net.URLEncoder.encode(mapping.group.name,"UTF-8")
                    def groupInfo = performGetRequest(this.ucClient,this.baseUrl + groupUrl)
                    groupInfo.each() { group ->
                        if(group.userEmail != "") {
                            returnList.add(group.userEmail)
                        }
                    }
                }
            }
        }

        // Return a unique list of email address
        return returnList.unique()
    }

    /**
     * Method to obtain list of email addresses for a team
     * @param team - Team ID from UC Build
     * @param roles - List of Role names as it appears in UC Build (optional)
     * @return List of string objects which are the unique email addresses found
     */
    List<String> getUCBEmailListForTeam(String team, List<String> roles = ["all"]) {
        // Set URL for obtaining team information from UC Build
        String addUrl = "/rest2/team/" + java.net.URLEncoder.encode(team,"UTF-8")
        List<String> returnList = []

        // Get the team info from UC Build
        def teamInfo = performGetRequest(this.ucClient,this.baseUrl + addUrl)

        // If no information found for the team, return an empty list
        if(teamInfo == null && teamInfo.size() != 0) {
            return returnList
        }

        // Look at each role mapping
        teamInfo.roleMappings.each() { mapping ->
            // Look in the roles specified and if the mapping has it, add the user emails to the list
            // If roles was not specified, add email address for all roles to the list
            if(roles.find {mapping.role.name == it} || roles == ["all"]) {
                // Add individual user email address in the roles to the list
                if(mapping.user != null && mapping.user.email != null && mapping.user.email != "") {
                    returnList.add(mapping.user.email)
                }
                // If using a group, look into the group and add each email address for the group to the list
                if(mapping.group != null && mapping.group != "") {
                    // Get group details for the group found
                    String groupUrl = "/rest2/group/" + java.net.URLEncoder
                        .encode(mapping.group.id,"UTF-8") + "/members"
                    def groupInfo = performGetRequest(this.ucClient,this.baseUrl + groupUrl)
                    groupInfo.each() { group ->
                        if(group.email != "") {
                            returnList.add(group.email)
                        }
                    }
                }
            }
        }

        // Return a unique list of email address
        return returnList.unique()
    }

    /**
     * Method to obtain a list of email addresses by application name
     * @param app - Application Name
     * @param roles - List of Role names as it appears in UC Deploy (optional)
     * @return List of strings which contain email addresses
     */
    List<String> getEmailsByApp(String app, List<String> roles = ["all"]) {
        // Get application information from UC Deploy
        String addUrl = "/cli/application/info?application=" + java.net.URLEncoder.encode(app,"UTF-8")
        def appInfo = performGetRequest(this.ucClient, this.baseUrl + addUrl)

        List<String> teams = []
        List<String> emails = []

        // Get the team information for the application
        appInfo.extendedSecurity.teams.each() { it -> teams.add(it.teamLabel) }
        for(team in teams) {
            // Obtain the list of email address for each team/role
            emails.addAll(getEmailListForTeam(team,roles))
        }

        // Return the list of emails
        return emails
    }

    /**
     * Method to obtain a list of email addresses by component name
     * @param comp - Component name
     * @param roles - List of Role Names as it appears in UC Deploy (optional)
     * @return List of strings which contain email addresses
     */
     List<String> getEmailsByComp(String comp, List<String> roles = ["all"]) {
         // Get component information from UC Deploy
        String addUrl = "/cli/component/info?component=" + java.net.URLEncoder.encode(comp,"UTF-8")
        def compInfo = performGetRequest(this.ucClient, this.baseUrl + addUrl)

        List<String> teams = []
        List<String> emails = []

         // Get the team information for the component
         compInfo.extendedSecurity.teams.each() { it ->
                 teams.add(it.teamLabel)
         }
        for(team in teams) {
            // obtain the list of email address for each team/role
            emails.addAll(getEmailListForTeam(team,roles))
        }

         // Return the list of emails
        return emails
    }

    /**
     * Method to obtain a list of email addresses from UC Build
     * @param projectId - UC Build Project ID
     * @param processId - UC Build Process ID
     * @param roles - List of roles as they appear in UC Build (optional)
     * @return List of strings which contain email addresses
     */
    List<String> getEmailFromUcBuild(String projectId, String processId,
                                     List<String> roles = ["all"]) {
        // Get component information from UC Deploy
        String addUrl = "/rest2/projects/${projectId}/processes/${processId}"
        def compInfo = performGetRequest(this.ucClient, this.baseUrl + addUrl)

        List<String> teams = []
        List<String> emails = []

        // Get the team information for the component
        compInfo.securityResource.teams.each() { it ->
            if (it.name != "System Team") {
                teams.add(it.id)
            }
        }
        for(team in teams) {
            // obtain the list of email address for each team/role
            emails.addAll(getUCBEmailListForTeam(team, roles))
        }

        // Return the list of emails
        return emails
    }

    /**
     * Method to send email using the UC Deploy configuration
     * @param emails - List of email addresses
     * @param subject - Subject to be added to the message
     * @param message - Message body
     * @param attachment - Location of the attachment to add (optional)
     */
    void sendUCDEmail (List<String> emails, String subject, String message, String
        attachment) {

        // create the rest client and get the system configuration
        com.urbancode.air.XTrustProvider.install()

        def client = new SystemClient(new URI(this.baseUrl), this.userName, this.password)
        def values = client.getSystemConfiguration()
        def host
        def port
        boolean secure
        def fromAddress
        def username
        def mailPassword
        ArrayList emailTos = new ArrayList()

        emails.each() { email ->
            emailTos.add(new InternetAddress(email.toString()))
        }

        // find the system configuration properties we are interested in
        values.keys().each() { key ->
            if (key == "deployMailHost") {
                host = values.optString(key)
            }
            if (key == "deployMailPort") {
                port = values.optString(key)
            }
            if (key == "deployMailSecure") {
                secure = values.optString(key)
            }
            if (key == "deployMailSender") {
                fromAddress = "donotreply@" + values.optString(key).split("@")[1]
            }
            if (key == "deployMailUsername") {
                username = values.optString(key)
            }
            if (key == "deployMailPassword") {
                mailPassword = values.optString(key)
            }
        }

        // create a new mail session and message
        Properties mprops = new Properties()
        mprops.put("mail.smtp.host", host)
        mprops.put("mail.smtp.port", port)
        mprops.put("mail.smtp.starttls.enable", secure)
        Session lSession
        if (username && mailPassword) {
            mprops.put("mail.smtp.auth", "true")
            lSession = Session.getInstance(mprops,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, mailPassword)
                        }
                    })
        } else {
            lSession = Session.getDefaultInstance(mprops, null)
        }

        try {
            MimeMessage msg = new MimeMessage(lSession)

            // populate the to, from, subject, and text of the message
            InternetAddress[] to = new InternetAddress[emails.size()]
            to = (InternetAddress[]) emailTos.toArray(to)
            msg.setRecipients(MimeMessage.RecipientType.TO, to)
            msg.setFrom(new InternetAddress(fromAddress))
            msg.setSubject(subject)
            if(attachment != "" && attachment != null) {
                BodyPart messageBodyPart = new MimeBodyPart()
                messageBodyPart.setContent(message,"text/html")
                Multipart multipart = new MimeMultipart()
                multipart.addBodyPart(messageBodyPart)

                messageBodyPart = new MimeBodyPart()
                messageBodyPart.attachFile(attachment)
                multipart.addBodyPart(messageBodyPart)
                // Send the complete message parts
                msg.setContent(multipart)
            } else {
                msg.setText(message)
            }

            // send the message
            Transport.send(msg)

        } catch (MessagingException e) {
            throw new RuntimeException(e)
        }
        println "Email(s) sent!"
    }

    /**
     * Method to send email using the SMTP Server
     * @param emails - List of email addresses
     * @param subject - Subject to be added to the message
     * @param message - Message body
     * @param attachment - Location of the attachment to add (optional)
     * @param host - smtp host name
     * @param port - smpt port
     * @param secure - is the mail server secure
     * @param fromAddress - sending email address
     * @param username - Secure server login name
     * @param mailPassword - Secure server password
     */
    void sendSMTPEmail (List<String> emails, String subject, String message, String attachment,
                        String host, String port, boolean secure, String fromAddress,
                        String username = null, String mailPassword = null) {

        ArrayList emailTos = new ArrayList()

        emails.each() { email ->
            emailTos.add(new InternetAddress(email.toString()))
        }

        // create a new mail session and message
        Properties mprops = new Properties()
        mprops.put("mail.smtp.host", host)
        mprops.put("mail.smtp.port", port)
        mprops.put("mail.smtp.starttls.enable", secure)
        Session lSession
        if (username && mailPassword) {
            mprops.put("mail.smtp.auth", "true")
            lSession = Session.getInstance(mprops,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, mailPassword)
                    }
                })
        } else {
            lSession = Session.getDefaultInstance(mprops, null)
        }

        try {
            MimeMessage msg = new MimeMessage(lSession)

            // populate the to, from, subject, and text of the message
            InternetAddress[] to = new InternetAddress[emails.size()]
            to = (InternetAddress[]) emailTos.toArray(to)
            msg.setRecipients(MimeMessage.RecipientType.TO, to)
            msg.setFrom(new InternetAddress(fromAddress))
            msg.setSubject(subject)
            if(attachment != "" && attachment != null) {
                BodyPart messageBodyPart = new MimeBodyPart()
                messageBodyPart.setContent(message,"text/html")
                Multipart multipart = new MimeMultipart()
                multipart.addBodyPart(messageBodyPart)

                messageBodyPart = new MimeBodyPart()
                messageBodyPart.attachFile(attachment)
                multipart.addBodyPart(messageBodyPart)
                // Send the complete message parts
                msg.setContent(multipart)
            } else {
                msg.setText(message)
            }

            // send the message
            Transport.send(msg)

        } catch (MessagingException e) {
            throw new RuntimeException(e)
        }
        println "Email(s) sent!"
    }
}
