/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sample_maven_web_app;

import static com.mycompany.sample_maven_web_app.UserService.logger;
import data.ModelMessages;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import objects.Message;
import objects.User;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author wlloyd
 */
@Path("messages")
public class MessageService {
    
    static final Logger logger = Logger.getLogger(MessageService.class.getName());

    public MessageService() 
    {
    }
    
/*    @GET
    @Path("{messageid}")
    @Produces(MediaType.TEXT_HTML)
    public String getMessages(@PathParam("userid") String id) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><style>table, th, td {font-family:Arial,Verdana,sans-serif;font-size:16px;padding: 0px;border-spacing: 0px;}</style><b>MESSAGES LIST:</b><br><br><table cellpadding=10 border=1><tr><td>Message Id</td><td>User Id</td><td>Message</td><td>Date Added</td></tr>");
        try
        {
            ModelMessages db = ModelMessages.singleton();
            Message[] msgs = db.getMessages();
            for (int i=0;i<msgs.length;i++)
                sb.append("<tr><td>" + msgs[i].getMessageId()+ "</td><td>" + msgs[i].getUserId()+ "</td><td>" + msgs[i].getMessage()+ "</td><td>" + msgs[i].getDateadded()+ "</td></tr>");
        }
        catch (Exception e)
        {
            sb.append("</table><br>Error getting messages: " + e.toString() + "<br>");
        }
        sb.append("</table></body></html>");
        return sb.toString();
    }
    */
    
    @GET
    @Path("{messageid}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Message> getMessagesJson(@PathParam("messageid") String id) {
        LinkedList<Message> lmsgs = new LinkedList<Message>();
     
        try
        {
            int msgid = Integer.parseInt(id);
            ModelMessages db = ModelMessages.singleton();
            Message[] msgs = db.getMessages();
            if (msgid ==0)
                for (int i=0;i<msgs.length;i++)
                    lmsgs.add(msgs[i]);
            else
                lmsgs.add(msgs[0]);
            logger.log(Level.INFO, "Received request to fetch user id=" + msgid);
            return lmsgs;
        }
        catch (Exception e)
        {
            JSONObject obj = new JSONObject();
                logger.log(Level.WARNING, "Error getting users:" + e.toString());
                return null;
        }
    }    
    
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateMessage(String jobj) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        Message msg = mapper.readValue(jobj.toString(), Message.class);
        StringBuilder text = new StringBuilder();
        try {
            ModelMessages db = ModelMessages.singleton();
            int messageid = msg.getMessageId();
            db.updateMessage(msg);
            logger.log(Level.INFO, "update msg with messageid=" + messageid);
            text.append("Message id updated with message id=" + messageid + "\n");
        }
        catch (SQLException sqle)
        {
            String errText = "Error updating user after db connection made:\n" + sqle.getMessage() + " --- " + sqle.getSQLState() + "\n";
            logger.log(Level.SEVERE, errText);
            text.append(errText);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error connecting to db.");
            text.append("Error connecting to db.");
        }
        return text.toString();
    }
    
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String deleteMessage(String jobj) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        Message msg = mapper.readValue(jobj.toString(), Message.class);
        StringBuilder text = new StringBuilder();
        try {
            ModelMessages db = ModelMessages.singleton();
            int messageid = msg.getMessageId();
            db.deleteMessage(messageid);
            logger.log(Level.INFO, "message deleted from db=" + messageid);
            text.append("Message id deleted with id=" + messageid);
        }
        catch (SQLException sqle)
        {
            String errText = "Error deleteing msg after db connection made:\n" + sqle.getMessage() + " --- " + sqle.getSQLState() + "\n";
            logger.log(Level.SEVERE, errText);
            text.append(errText);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error connecting to db.");
            text.append("Error connecting to db.");
        }
        return text.toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Message> CreateMessage(String jobj) throws IOException 
    {
        logger.log(Level.INFO, "RECEIVED CREATE REQUEST FOR:\n");
        logger.log(Level.INFO, "OBJECT:" + jobj + "\n");
        
        LinkedList<Message> lmsgs = new LinkedList<Message>();

        ObjectMapper mapper = new ObjectMapper();
        Message msg = mapper.readValue(jobj.toString(), Message.class);
        
       /* StringBuilder text = new StringBuilder();
        text.append("The JSON obj:" + jobj.toString() + "\n");
        text.append("Hello " + msg.getName() + "\n");
        text.append("You're only " + user.getAge() + " years old.\n");
        text.append("Messages:\n");
        if (msg.getMessages() != null)
            for (Object msg : user.getMessages())
                text.append(msg.toString() + "\n");
                */
        
        try {
            ModelMessages db = ModelMessages.singleton();
            Message message = db.newMessage(msg);
           // logger.log(Level.INFO, "user persisted to db as userid=" + usr.getUserid());
            //text.append("User id persisted with id=" + usr.getUserid());
            lmsgs.add(message);
        }
        catch (SQLException sqle)
        {
            String errText = "Error persisting user after db connection made:\n" + sqle.getMessage() + " --- " + sqle.getSQLState() + "\n";
            //logger.log(Level.SEVERE, errText);
            //text.append(errText);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error connecting to db.");
        }
        
        return lmsgs;
    }
    
}
