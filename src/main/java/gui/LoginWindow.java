package gui;

import db.ManagerDB;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTextField;

import javax.swing.*;

public class LoginWindow
{
  private final JXFrame frame;
  private final JXTextField login;
  private final JPasswordField password;

  public LoginWindow()
  {
    ManagerDB db = new ManagerDB();

    frame = new JXFrame("Login");
    frame.setLayout(new MigLayout("", "[][fill, grow][fill, grow]", "[]"));
    frame.add(new JXLabel("Login:"), "align right");
    login = new JXTextField();
    frame.add(login, "span");
    frame.add(new JXLabel("Password:"), "align right");
    password = new JPasswordField();
    password.setEchoChar('*');
    frame.add(password, "span");

    JXButton si = new JXButton("Sign in");
    si.addActionListener(e -> {
      if (db.userIsExist(login.getText(), String.valueOf(password.getPassword())))
      {
        MainGUI g = new MainGUI(login.getText());
        g.show();
        frame.dispose();
      }
      else
      {
        JOptionPane.showMessageDialog(null, "Not correct login or password");
      }
    });
    frame.add(si, "skip");
    JXButton su = new JXButton("Sign up");
    su.addActionListener(e -> {
      JOptionPane.showMessageDialog(null, db.addUser(login.getText(), String.valueOf(password.getPassword()))
                                          ? "Account created successful" : "This user already exist");
    });
    frame.add(su, "span");
  }

  public void show()
  {
    frame.setDefaultCloseOperation(JXFrame.EXIT_ON_CLOSE);
    frame.setStartPosition(JXFrame.StartPosition.CenterInScreen);
    frame.setSize(300, 120);
    frame.setVisible(true);
  }

}
