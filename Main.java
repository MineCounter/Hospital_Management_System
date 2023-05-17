// importing the JFrame class
import javax.swing.*;
import javax.swing.Timer;
// importing the Color class
import java.awt.Color;
import java.awt.Component;
// importing the ActionListener class
import java.awt.event.*;
//import java.sql
import java.sql.*;
//import java.util
import java.util.*;
import java.util.concurrent.CountDownLatch;
public class Main {

    //create a global variable for the current user and the authorization level
    private static String currentUser = "";
    private static int authorizationLevel = -1;

    public static void main(String[] args) {

        splashScreen();
        //print system initialization message
        System.out.println("System Initializing...");

        // hold for 5 seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //call loginScreen method
        if (loginScreen()) {
            JOptionPane.showMessageDialog(null, "Welcome " + getCurrentUser() + "!\nAuthorization Level: " + getAuthorizationLevel());
            //print system initialization message
            System.out.println("System Initialized!");
        }

        

    }

    //getter for currentUser
    public static String getCurrentUser() {
        return currentUser;
    }
    //setter for currentUser
    public static void setCurrentUser(String currentUser) {
        Main.currentUser = currentUser;
    }
    //getter for authorizationLevel
    public static int getAuthorizationLevel() {
        return authorizationLevel;
    }
    //setter for authorizationLevel
    public static void setAuthorizationLevel(int authorizationLevel) {
        Main.authorizationLevel = authorizationLevel;
    }

    public static void splashScreen() {
        // create an instance of JFrame
        JFrame frame = new JFrame("Splash Screen");
        // set the size of the frame
        frame.setSize(600, 300);
        // center the frame on the screen
        frame.setLocationRelativeTo(null);
        //disable top bar
        frame.setUndecorated(true);
        // set frame background color to #61233b
        frame.getContentPane().setBackground(new Color(97, 35, 59));
        //add image logo.png
        ImageIcon image = new ImageIcon("logo.png");
        // create a label to display the image by passing in the image as an argument to the constructor
        JLabel label = new JLabel(image);
        //scale it to be on the left side of the splash screen
        label.setBounds(0, 75, 150, 150);
        //size the image to fit the label
        label.setIcon(new ImageIcon(image.getImage().getScaledInstance(label.getWidth(), label.getHeight(), image.getImage().SCALE_DEFAULT)));
        // add the label to the frame
        frame.add(label);
        //add text "Hospital Management System"
        JLabel text = new JLabel("Hospital Management System    ");
        //set the text to be on the right side of the splash screen
        text.setBounds(300, 0, 300, 300);
        //right align the text
        text.setHorizontalAlignment(JLabel.RIGHT);
        //set the font to be 30pt
        text.setFont(text.getFont().deriveFont(30f));
        //set the text color to white
        text.setForeground(Color.white);
        frame.add(text);

        // set the frame visibility to true
        frame.setVisible(true);

        // set the splash screen close operation after 5 seconds with ActionListener
        new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //close the splash screen
                frame.setVisible(false);
                frame.dispose();
            }
        }).start();




    }

    public static boolean loginScreen(){

        final boolean[] loginStatus = {false};
        // create an instance of JFrame
        JFrame frame = new JFrame("Login Screen");
        // set the size of the frame to fit a login screen
        frame.setSize(300, 150);
        // center the frame on the screen
        frame.setLocationRelativeTo(null);
        //set exit on close
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //create a panel
        JPanel panel = new JPanel();
        //create login label and text field
        JLabel loginLabel = new JLabel("Login: ");
        JTextField loginText = new JTextField(20);
        //create password label and text field
        JLabel passwordLabel = new JLabel("Password: ");
        JPasswordField passwordText = new JPasswordField(20);
        //create login button
        JButton loginButton = new JButton("Login");
        //set the labels and text field to not stretch but be fixed
        loginLabel.setMaximumSize( loginLabel.getPreferredSize() );
        loginText.setMaximumSize( loginText.getPreferredSize() );
        passwordLabel.setMaximumSize( passwordLabel.getPreferredSize() );
        passwordText.setMaximumSize( passwordText.getPreferredSize() );
        //set their alignment to be centered
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginText.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordText.setAlignmentX(Component.CENTER_ALIGNMENT);
        //set the login button to be centered
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //add login label and text field to panel
        panel.add(loginLabel);
        panel.add(loginText);
        //add password label and text field to panel
        panel.add(passwordLabel);
        panel.add(passwordText);
        //add login button to panel
        panel.add(loginButton);
        //set panel components to be vertically stacked
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


        //add panel to frame
        frame.add(panel);
        // set frame visibility to true
        frame.setVisible(true);
        //set focus to the login text field
        loginText.requestFocus();
        // Create a CountDownLatch with a count of 1
        CountDownLatch latch = new CountDownLatch(1);

        //after login button is clicked check if login and password match a record in the database
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get login from text fields
                String login = loginText.getText();
                //set password from text field
                String password = String.valueOf(passwordText.getPassword());

               

                //check if login and password match a record in the database
                if(checkLogin(login, password)){
                    //if login and password match a record in the database set loginStatus to true
                    loginStatus[0] = true;
                    //print current user and authorization level
                    System.out.println("Current User: " + getCurrentUser());
                    System.out.println("Authorization Level: " + getAuthorizationLevel());
                    // Count down the latch to indicate that the login process has completed
                    latch.countDown();

                }
                else{
                    //if login and password do not match a record in the database display error message
                    JOptionPane.showMessageDialog(null, "Invalid Login or Password");
                    // Count down the latch to indicate that the login process has completed
                    latch.countDown();
                }
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Close the login screen
        frame.setVisible(false);

        // Return the login status
        return loginStatus[0];
    }

    public static boolean checkLogin(String login, String password){

        // Specify the path to the database file
        String dbPath = "hospital.accdb";
        String url = "jdbc:ucanaccess://" + dbPath;

        //connect to the database
        try (Connection conn = DriverManager.getConnection(url)) {
            //create a statement
            Statement stmt = conn.createStatement();
            //create a result set
            ResultSet rs = stmt.executeQuery("SELECT * FROM Users WHERE Username = '" + login + "' AND Password = '" + password + "'");
            //check if the result set is empty
            if(rs.next()){
                //set the authorization level to the authorization level of the user
                setAuthorizationLevel(rs.getInt("Authroity_Level"));
                //set the current user to the username
                setCurrentUser(rs.getString("Username"));
                //if the result set is not empty return true
                //close the statement
                stmt.close();
                //close the result set
                rs.close();
                //close the connection
                conn.close();
                return true;
            }
            else{
                //if the result set is empty return false
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //if the result set is empty return false
        return false;
    }
}