package com.readgroup.nreclient;

import org.fusesource.stomp.codec.StompFrame;
import org.fusesource.stomp.jms.message.*;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

public class MessageHandler implements MessageListener {

    private static final Logger LOG = Logger.getLogger(MessageHandler.class.getName());
    private QueueUtils queueUtils;

    public MessageHandler(QueueUtils queueUtils){
        this.queueUtils = queueUtils;
    }

    @Override
    public void onMessage(Message message) {

        StompFrame stompFrame = ((StompJmsMessage)message).getFrame();
       // System.out.println(stompFrame.contentAsString());
        // System.out.println(stompFrame.action().toString());
        String messageXml = convertToXmlString((BytesMessage)message);
        System.out.println(messageXml);
        queueUtils.sendMessage(messageXml);
    }

    private String convertToXmlString(BytesMessage bytesMessage) {
        if (bytesMessage != null) try {
            long length = bytesMessage.getBodyLength();
            byte[] bytesArray = new byte[(int) length];
            bytesMessage.readBytes(bytesArray);
            Reader streamReader = null;
            try {
                streamReader = new InputStreamReader(new ByteArrayInputStream(bytesArray));
                StringBuilder stringBuilder = new StringBuilder();
                char[] charBuffer = new char[1024];
                int size = streamReader.read(charBuffer);
                while (size > -1) {
                    stringBuilder.append(charBuffer, 0, size);
                    size = streamReader.read(charBuffer);
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (streamReader != null) {
                    streamReader.close();
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
             ex.printStackTrace();
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, null, ex);
             ex.printStackTrace();
        }

        return null;
    }
}