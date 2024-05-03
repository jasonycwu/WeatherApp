import javax.swing.*;

/**
 * @Author: Jason Y. Wu
 * @Date:   2024-04-28 04:14:36
 * @Last Modified by:   Jason Y. Wu
 * @Last Modified time: 2024-05-03 00:15:25
 */


public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                // display weather app gui
                new WeatherAppGui().setVisible(true);
            }
        });
    } 
}
