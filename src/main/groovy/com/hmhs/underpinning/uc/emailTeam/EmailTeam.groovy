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

/** TODO: Add Groovy Doc dumbass */

class EmailTeam {
    String baseUrl
    String userName
    String password
    HttpClient ucClient
    def apTool

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

    EmailTeam(AirPluginTool airTool, String baseUrl, String userName, String password) {
        this.apTool = airTool
        this.baseUrl = baseUrl
        this.userName = userName
        this.password = password
        this.ucClient = initializeClient()
    }

    HttpClient initializeClient(){
        HttpClientBuilder builder = new HttpClientBuilder()
        builder.setPreemptiveAuthentication(true)
        builder.setUsername(this.userName)
        builder.setPassword(this.password)
        //Accept all certificates
        builder.setTrustAllCerts(true)
        return builder.buildClient()
    }

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

    List<String> getEmailListForTeam(String team, List<String> roles = ["all"]) {
        String addUrl = "/cli/team/info?team=" + java.net.URLEncoder.encode(team,"UTF-8")
        List<String> returnList = []
        def teamInfo = performGetRequest(this.ucClient,this.baseUrl + addUrl)
        if(teamInfo == null && teamInfo.size() != 0) {
            return returnList
        }
        teamInfo.roleMappings.each() { mapping ->
            if(roles.find {mapping.role.name == it} || roles == ["all"]) {
                if(mapping.user != null && mapping.user.email != null && mapping.user.email != "") {
                    returnList.add(mapping.user.email)
                }
                if(mapping.group != null && mapping.group != "") {
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
        return returnList.unique()
    }

    List<String> getEmailsByApp(String app, List<String> roles) {
        String addUrl = "/cli/application/info?application=" + java.net.URLEncoder.encode(app,"UTF-8")
        def appInfo = performGetRequest(this.ucClient, this.baseUrl + addUrl)
        List<String> teams = []
        List<String> emails = []
        appInfo.extendedSecurity.teams.each() { it -> teams.add(it.teamLabel) }
        for(team in teams) {
            emails.addAll(getEmailListForTeam(team,roles))
        }
        return emails
    }

     List<String> getEmailsByComp(String comp, List<String> roles) {
        String addUrl = "/cli/component/info?component=" + java.net.URLEncoder.encode(comp,"UTF-8")
        def compInfo = performGetRequest(this.ucClient, this.baseUrl + addUrl)
        List<String> teams = []
        List<String> emails = []
        compInfo.extendedSecurity.teams.each() { it -> teams.add(it.teamLabel) }
        for(team in teams) {
            emails.addAll(getEmailListForTeam(team,roles))
        }
        return emails
    }

    void sendUCDEmail (List<String> emails, String subject, String message, String attachment) {
        /**
         * (c) Copyright IBM Corporation 2014, 2017.
         * This is licensed under the following license.
         * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
         * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
         */

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
}