import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CarbonFootprintCalculator extends JFrame {

    private JComboBox<String> transportCombo, dietCombo;
    private JTextField distanceField, electricityField;
    private JLabel resultLabel, tipsLabel;
    private JPanel chartPanel;

    public CarbonFootprintCalculator() {
        setTitle("Carbon Footprint Calculator");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Daily Usage"));

        inputPanel.add(new JLabel("Transport Mode:"));
        transportCombo = new JComboBox<>(new String[]{"Car", "Bus", "Bicycle", "Walk"});
        inputPanel.add(transportCombo);

        inputPanel.add(new JLabel("Distance Travelled (km/day):"));
        distanceField = new JTextField();
        inputPanel.add(distanceField);

        inputPanel.add(new JLabel("Electricity Usage (kWh/day):"));
        electricityField = new JTextField();
        inputPanel.add(electricityField);

        inputPanel.add(new JLabel("Diet Type:"));
        dietCombo = new JComboBox<>(new String[]{"Vegan", "Vegetarian", "Non-Vegetarian"});
        inputPanel.add(dietCombo);

        JButton calculateBtn = new JButton("Calculate CO₂");
        JButton clearBtn = new JButton("Clear");

        inputPanel.add(calculateBtn);
        inputPanel.add(clearBtn);

        add(inputPanel, BorderLayout.NORTH);

        // Output Panel
        JPanel outputPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        outputPanel.setBorder(BorderFactory.createTitledBorder("Results"));

        resultLabel = new JLabel("Total CO₂ Emission: - kg/day", SwingConstants.CENTER);
        tipsLabel = new JLabel("<html>Tips will appear here.</html>", SwingConstants.CENTER);

        chartPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart(g);
            }
        };
        chartPanel.setPreferredSize(new Dimension(550, 150));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createTitledBorder("CO₂ Contribution Chart"));

        outputPanel.add(resultLabel);
        outputPanel.add(chartPanel);
        outputPanel.add(tipsLabel);

        add(outputPanel, BorderLayout.CENTER);

        // Action Listeners
        calculateBtn.addActionListener(e -> calculateCarbon());
        clearBtn.addActionListener(e -> clearFields());
    }

    private double transportCO2 = 0, electricityCO2 = 0, dietCO2 = 0;

    private void calculateCarbon() {
        try {
            String transport = (String) transportCombo.getSelectedItem();
            double distance = Double.parseDouble(distanceField.getText().trim());
            double electricity = Double.parseDouble(electricityField.getText().trim());
            String diet = (String) dietCombo.getSelectedItem();

            // CO2 emission factors
            transportCO2 = switch (transport) {
                case "Car" -> distance * 0.21;
                case "Bus" -> distance * 0.11;
                case "Bicycle", "Walk" -> 0;
                default -> 0;
            };
            electricityCO2 = electricity * 0.475;
            dietCO2 = switch (diet) {
                case "Vegan" -> 2.0;
                case "Vegetarian" -> 3.5;
                case "Non-Vegetarian" -> 7.0;
                default -> 0;
            };

            double totalCO2 = transportCO2 + electricityCO2 + dietCO2;
            resultLabel.setText(String.format("Total CO₂ Emission: %.2f kg/day", totalCO2));

            // Tips
            String tips = "<html>Tips to reduce CO₂:<br>";
            if (transportCO2 > 5) tips += "- Consider public transport or biking.<br>";
            if (electricityCO2 > 5) tips += "- Use energy-efficient appliances and LED bulbs.<br>";
            if (dietCO2 > 5) tips += "- Reduce meat consumption for lower diet CO₂.<br>";
            if (transportCO2 <= 5 && electricityCO2 <= 5 && dietCO2 <= 5)
                tips += "- Excellent! Your daily CO₂ is low.<br>";
            tips += "</html>";
            tipsLabel.setText(tips);

            chartPanel.repaint();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for distance and electricity.");
        }
    }

    private void clearFields() {
        distanceField.setText("");
        electricityField.setText("");
        transportCombo.setSelectedIndex(0);
        dietCombo.setSelectedIndex(0);
        resultLabel.setText("Total CO₂ Emission: - kg/day");
        tipsLabel.setText("<html>Tips will appear here.</html>");
        transportCO2 = electricityCO2 = dietCO2 = 0;
        chartPanel.repaint();
    }

    private void drawChart(Graphics g) {
        int total = (int)(transportCO2 + electricityCO2 + dietCO2);
        if (total == 0) return;

        int width = 500;
        int startX = 30;
        int y = 50;
        int height = 30;

        int transportWidth = (int)(width * transportCO2 / total);
        int electricityWidth = (int)(width * electricityCO2 / total);
        int dietWidth = (int)(width * dietCO2 / total);

        // Draw transport
        g.setColor(new Color(200, 100, 100));
        g.fillRect(startX, y, transportWidth, height);
        g.setColor(Color.BLACK);
        g.drawRect(startX, y, transportWidth, height);
        g.drawString("Transport: " + String.format("%.2f", transportCO2), startX + 5, y + 20);

        // Draw electricity
        g.setColor(new Color(100, 200, 100));
        g.fillRect(startX + transportWidth, y, electricityWidth, height);
        g.setColor(Color.BLACK);
        g.drawRect(startX + transportWidth, y, electricityWidth, height);
        g.drawString("Electricity: " + String.format("%.2f", electricityCO2), startX + transportWidth + 5, y + 20);

        // Draw diet
        g.setColor(new Color(100, 100, 250));
        g.fillRect(startX + transportWidth + electricityWidth, y, dietWidth, height);
        g.setColor(Color.BLACK);
        g.drawRect(startX + transportWidth + electricityWidth, y, dietWidth, height);
        g.drawString("Diet: " + String.format("%.2f", dietCO2), startX + transportWidth + electricityWidth + 5, y + 20);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CarbonFootprintCalculator().setVisible(true));
    }
}
