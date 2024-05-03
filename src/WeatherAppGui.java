/**
 * @Author: Jason Y. Wu
 * @Date:   2024-04-28 04:06:06
 * @Last Modified by:   Jason Y. Wu
 * @Last Modified time: 2024-05-03 01:00:13
 */
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.json.simple.JSONObject;

import javax.swing.ImageIcon;

/**
 * @Author: Jason Y. Wu
 * @Date:   2024-04-28 04:06:06
 * @Last Modified by:   Jason Y. Wu
 * @Last Modified time: 2024-04-28 04:23:44
 */


public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui(){
        super("Weather App"); // set up gui and add title
        setDefaultCloseOperation(EXIT_ON_CLOSE); // config gui to end once closed
        setSize( 450, 650); // set gui size
        setLocationRelativeTo(null); // load gui at center of screen
        setLayout(null); // make components within gui
        setResizable(false); // make gui not resizeable
        addGuiComponents();
    }

    private void addGuiComponents(){
        // search field
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45); // set location, size of component
        searchTextField.setFont(new Font("App Font", Font.PLAIN, 24)); // change font style and size
        add(searchTextField);

        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/BEGJaDww.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("App Font", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // weather condition description
        JLabel weatherConditionDesc = new JLabel("Sunny");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("App Font", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // add humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/8t_ZPnx4.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // add humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("App Font", Font.PLAIN, 12));
        add(humidityText);

        // add windspeed image
        JLabel windspeedImage = new JLabel(loadImage("src/assets/_B2oOzDY.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        // add windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km </html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("App Font", Font.PLAIN, 12));
        add(windspeedText);

        // search button; JButton and JLabel allows us to init w an image obj
        JButton searchButton = new JButton(loadImage("src/assets/yOB1lSDc.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));// change cursor to hand cursor when hovering over this button
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();
                // validate input - remove whitespace
                if (userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                // retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                // uodate weather image
                String weatherCondition = (String) weatherData.get("weather_condition");
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/BEGJaDww.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/4-jw4bFE.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/TqGhyRy4.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/lVFAbGEk.png"));
                        break;
                }

                // update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                // update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);
    }

 
    // used to create images in gui components
    private ImageIcon loadImage(String resourcePath){
        try{
            // read image file from path
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
            
        } catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Could not find resource");
        return null;
    }
}
