package ca.unb.cs.pcsf.services.crt.test;

import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TestEmail {
  public void SendEmailTest() {
    Properties props = new Properties();
    props.setProperty("mail.smtp.host", "smtp.gmail.com");
    props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.setProperty("mail.smtp.socketFactory.fallback", "false");
    props.setProperty("mail.smtp.port", "465");
    props.put("mail.smtp.auth", "true");
    Session sendMailSession = Session.getInstance(props, null);

    try {
      Transport transport = sendMailSession.getTransport("smtp");
      transport.connect("smtp.gmail.com", "pcsf.notification@gmail.com", "pcsf123456");
      Message newMessage = new MimeMessage(sendMailSession);

      // 设置mail主题
      String mail_subject = "更改邮件发送人测试";
      newMessage.setSubject(mail_subject);

      // 设置发信人地址
      String strFrom = "pcsf.notification@gmail.com";
      newMessage.setFrom(new InternetAddress(strFrom));
      // Address addressFrom[] = { new InternetAddress("goodnight0001@163.com"),new
      // InternetAddress("goodnight0002@163.com") };
      // 改变发件人地址
      // newMessage.addFrom(addressFrom);
      // 设置收件人地址
      Address addressTo[] = { new InternetAddress("dong.dong@me.com") };
      newMessage.setRecipients(Message.RecipientType.TO, addressTo);

      // 设置mail正文
      newMessage.setSentDate(new java.util.Date());
      String mail_text = "更改邮件发件人调试成功！";
      newMessage.setText(mail_text);

      newMessage.saveChanges(); // 保存发送信息
      transport.sendMessage(newMessage, newMessage.getRecipients(Message.RecipientType.TO)); // 发送邮件

      transport.close();
      System.out.println("OK");
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public static void main(String args[]) throws Exception {
    // TestEmail SEmail = new TestEmail();
    // SEmail.SendEmailTest();
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String date = sDateFormat.format(new java.util.Date());
    System.out.println(date);
  }
}
